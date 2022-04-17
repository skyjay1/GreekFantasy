package greekfantasy.entity;

import java.util.EnumSet;

import javax.annotation.Nullable;

import greekfantasy.GreekFantasy;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtByTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpartiEntity extends TameableEntity {
  protected static final DataParameter<Boolean> SPAWNING = EntityDataManager.defineId(SpartiEntity.class, DataSerializers.BOOLEAN);

  protected static final String KEY_SPAWN_TIME = "Spawning";
  protected static final String KEY_LIFE_TICKS = "LifeTicks";
  //bytes to use in World#setEntityState
  private static final byte SPAWN_CLIENT = 11;
    
  /** The max time spent 'spawning' **/
  protected final int maxSpawnTime = 60;
  protected int spawnTime;
  /** The number of ticks until the entity starts taking damage **/
  protected boolean limitedLifespan;
  protected int limitedLifeTicks;
  
  private final EntitySize spawningSize;
  
  public SpartiEntity(final EntityType<? extends SpartiEntity> type, final World worldIn) {
    super(type, worldIn);
    spawningSize = EntitySize.scalable(0.8F, 0.2F);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 54.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.28D)
        .add(Attributes.ATTACK_DAMAGE, 2.0D)
        .add(Attributes.ARMOR, 2.0D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SpartiEntity.SpawningGoal());
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
    this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 16.0F, 5.0F, false));
    this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.78D));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, MobEntity.class, 5, false, false, e -> {
      return e instanceof IMob && !(e instanceof CreeperEntity);
    }));
  }
  
  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(SPAWNING, Boolean.valueOf(false));
  }
  
  @Override
  public void aiStep() {
    super.aiStep();

    // lifespan
    if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
      this.limitedLifeTicks = 20;
      hurt(DamageSource.STARVE, 2.0F);
    }
    
    // update spawn time
    if(isSpawning() && spawnTime > 0) {
      if(--spawnTime <= 0) {
        setSpawning(false);
      }
    }
    
    // particles when spawning
    if(isSpawning() && level.isClientSide()) {
      int i = MathHelper.floor(this.getX());
      int j = MathHelper.floor(this.getY() - (double)0.2F);
      int k = MathHelper.floor(this.getZ());
      BlockPos pos = new BlockPos(i, j, k);
      BlockState blockstate = level.getBlockState(pos);
      if (!level.isEmptyBlock(pos)) {
        for(int count = 0; count < 10; count++) {
         this.level.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(pos), 
             this.getX() + ((double)this.random.nextFloat() - 0.5D) * (double)this.getBbWidth(), 
             this.getY() + 0.1D, 
             this.getZ() + ((double)this.random.nextFloat() - 0.5D) * (double)this.getBbWidth(), 
             4.0D * ((double)this.random.nextFloat() - 0.5D), 0.6D, ((double)this.random.nextFloat() - 0.5D) * 4.0D);
        }
      }
    }
  }
  
  public void setEquipmentOnSpawn() {
    this.setItemInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
    this.setDropChance(EquipmentSlotType.MAINHAND, 0);
  }
  
  @Override
  public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final ILivingEntityData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    setEquipmentOnSpawn();
    setSpawning(true);
    setBaby(false);
    return data;
  }

  // Spawn methods //
  
  public void setSpawning(final boolean spawning) {
    this.spawnTime = spawning ? maxSpawnTime : 0;
    this.getEntityData().set(SPAWNING, spawning);
    this.refreshDimensions();
    if(spawning && !level.isClientSide()) {
      this.level.broadcastEntityEvent(this, SPAWN_CLIENT);
    }
  }
  
  public boolean isSpawning() { return spawnTime > 0 || this.getEntityData().get(SPAWNING); }
  
  public float getSpawnPercent() {
    return spawnTime > 0 ? 1.0F - ((float)spawnTime / (float)maxSpawnTime) : 1.0F;
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleEntityEvent(byte id) {
    switch(id) {
    case SPAWN_CLIENT:
      setSpawning(true);
      break;
    default:
      super.handleEntityEvent(id);
      break;
    }
  }
  
  // Lifespan methods

  public void setLimitedLife(int life) {
    this.limitedLifespan = true;
    this.limitedLifeTicks = life;
  }
  
  // Misc. methods //
  
  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.SKELETON_AMBIENT; }

  @Override
  protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.SKELETON_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.SKELETON_DEATH; }

  @Override
  protected void playStepSound(BlockPos pos, BlockState blockIn) { this.playSound(SoundEvents.SKELETON_STEP, 0.15F, 1.0F); }
  
  @Override
  public EntitySize getDimensions(final Pose poseIn) { return this.isSpawning() ? spawningSize : super.getDimensions(poseIn); }
  
  @Override
  public CreatureAttribute getMobType() { return CreatureAttribute.UNDEAD; }
  
  @Override
  public boolean removeWhenFarAway(double distanceToClosestPlayer) { return !this.isTame(); }
  
  @Override
  protected float getStandingEyeHeight(Pose pose, EntitySize size) { return this.isSpawning() ? 0.05F : 1.74F; }
  
  @Override
  public double getMyRidingOffset() { return -0.6D; }  
  
  @Override
  public boolean canBeLeashed(PlayerEntity player) { return false; }
  
  @Override
  public boolean canMate(AnimalEntity otherAnimal) { return false; }
  
  @Override
  public AgeableEntity getBreedOffspring(ServerWorld world, AgeableEntity mate) { return null; }
  
  @Override
  public void setOrderedToSit(boolean sitting) { }
  
  @Override
  public void die(DamageSource cause) {
    setOwnerUUID(null);
    super.die(cause);
  }
  
  // NBT methods //
  
  @Override
  public void addAdditionalSaveData(CompoundNBT compound) {
    super.addAdditionalSaveData(compound);
    compound.putBoolean(KEY_SPAWN_TIME, isSpawning());
    if (this.limitedLifespan) {
      compound.putInt(KEY_LIFE_TICKS, this.limitedLifeTicks);
    }
  }

  @Override
  public void readAdditionalSaveData(CompoundNBT compound) {
    super.readAdditionalSaveData(compound);
    setSpawning(compound.getBoolean(KEY_SPAWN_TIME));
    if (compound.contains(KEY_LIFE_TICKS)) {
      setLimitedLife(compound.getInt(KEY_LIFE_TICKS));
    }
  }
  
  // Attack predicate methods //
  
  @Override
  public boolean canAttack(LivingEntity entity) {
    if (isOwnedBy(entity)) {
      return false;
    }
    return super.canAttack(entity);
  }

  @Override
  public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
    if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity)) {
      if (target instanceof TameableEntity) {
        TameableEntity tameable = (TameableEntity) target;
        return !tameable.isTame() || tameable.getOwner() != owner;
      } else if (target instanceof PlayerEntity && owner instanceof PlayerEntity
          && !((PlayerEntity) owner).canHarmPlayer((PlayerEntity) target)) {
        return false;
      } else if (target instanceof AbstractHorseEntity && ((AbstractHorseEntity) target).isTamed()) {
        return false;
      } else {
        return !(target instanceof TameableEntity) || !((TameableEntity) target).isTame();
      }
    } else {
      return false;
    }
  }
  
  // Goals //
  
  class SpawningGoal extends Goal {
    
    public SpawningGoal() {
      setFlags(EnumSet.allOf(Goal.Flag.class));
    }

    @Override
    public boolean canUse() {
      return SpartiEntity.this.isSpawning();
    }
    
    @Override
    public void tick() {
      SpartiEntity.this.getNavigation().stop();
    }

  }
}
