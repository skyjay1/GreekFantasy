package greekfantasy.entity;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import greekfantasy.entity.ai.HasOwnerFollowGoal;
import greekfantasy.entity.ai.HasOwnerHurtByTargetGoal;
import greekfantasy.entity.ai.HasOwnerHurtTargetGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpartiEntity extends CreatureEntity implements IHasOwner {
  protected static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.createKey(SpartiEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
  protected static final DataParameter<Boolean> SPAWNING = EntityDataManager.createKey(SpartiEntity.class, DataSerializers.BOOLEAN);

  protected static final String KEY_SPAWN_TIME = "Spawning";
  protected static final String KEY_LIFE_TICKS = "LifeTicks";
    
  /** The max time spent 'spawning' **/
  protected final int maxSpawnTime = 60;
  protected int spawnTime;
  /** The number of ticks until the entity starts taking damage **/
  protected boolean limitedLifespan;
  protected int limitedLifeTicks;
  
  private final EntitySize spawningSize;
  
  public SpartiEntity(final EntityType<? extends SpartiEntity> type, final World worldIn) {
    super(type, worldIn);
    spawningSize = EntitySize.flexible(0.8F, 0.2F);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 54.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.28D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SpartiEntity.SpawningGoal());
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
    this.goalSelector.addGoal(3, new HasOwnerFollowGoal<>(this, 1.0D, 24.0F, 5.0F, false));
    this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.78D));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HasOwnerHurtByTargetGoal<>(this));
    this.targetSelector.addGoal(2, new HasOwnerHurtTargetGoal<>(this));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, MobEntity.class, 5, false, false, e -> {
      return e instanceof IMob && !(e instanceof CreeperEntity);
    }));
  }
  
  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(OWNER, Optional.empty());
    this.getDataManager().register(SPAWNING, Boolean.valueOf(false));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // spawn time
    if(isSpawning()) {
      // update timer
      if(--spawnTime <= 0) {
        setSpawning(false);
      }
      // spawn particles
      int i = MathHelper.floor(this.getPosX());
      int j = MathHelper.floor(this.getPosY() - (double)0.2F);
      int k = MathHelper.floor(this.getPosZ());
      BlockPos pos = new BlockPos(i, j, k);
      BlockState blockstate = this.world.getBlockState(pos);
      if (!blockstate.isAir(this.world, pos)) {
        for(int count = 0; count < 10; count++) {
         this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(pos), 
             this.getPosX() + ((double)this.rand.nextFloat() - 0.5D) * (double)this.getWidth(), 
             this.getPosY() + 0.1D, 
             this.getPosZ() + ((double)this.rand.nextFloat() - 0.5D) * (double)this.getWidth(), 
             4.0D * ((double)this.rand.nextFloat() - 0.5D), 0.6D, ((double)this.rand.nextFloat() - 0.5D) * 4.0D);
        }
      }
    }

    // lifespan
    if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
      this.limitedLifeTicks = 20;
      attackEntityFrom(DamageSource.STARVE, 2.0F);
    }
  }
  
  public void setEquipmentOnSpawn() {
    // TODO what equipment should sparti have?
    this.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    setEquipmentOnSpawn();
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }

  // Owner methods //
    
  @Override
  public Optional<UUID> getOwnerID() { return this.getDataManager().get(OWNER); }
  
  @Override
  public void setOwner(@Nullable final UUID uuid) { this.getDataManager().set(OWNER, Optional.ofNullable(uuid)); }
    
  @Override
  public LivingEntity getOwner() {
    if(hasOwner()) {
      return this.getEntityWorld().getPlayerByUuid(getOwnerID().get());
    }
    return null;
  }
  
  // Spawn methods //
  
  public void setSpawning(final boolean spawning) {
    this.spawnTime = spawning ? maxSpawnTime : 0;
    this.getDataManager().set(SPAWNING, spawning);
    this.recalculateSize();
  }

  public boolean isSpawningTime() { return spawnTime > 0; }
  
  public boolean isSpawning() { return this.getDataManager().get(SPAWNING); }
  
  public float getSpawnTime(final float ageInTicks) {
    return (float) (spawnTime + (ageInTicks < 1.0F ? ageInTicks : 0));
  }
  
  public float getSpawnPercent(final float ageInTicks) {
    return 1.0F - (getSpawnTime(ageInTicks) / (float)maxSpawnTime);
  }
  
  // Lifespan methods

  public void setLimitedLife(int life) {
    this.limitedLifespan = true;
    this.limitedLifeTicks = life;
  }
  
  // Misc. methods //
  
  @Override
  public void notifyDataManagerChange(final DataParameter<?> key) {
    super.notifyDataManagerChange(key);
    if(key == SPAWNING) {
      this.spawnTime = isSpawning() ? maxSpawnTime : 0;
      recalculateSize();
    }
  }
  
  @Override
  public EntitySize getSize(final Pose poseIn) { return this.isSpawningTime() ? spawningSize : super.getSize(poseIn); }
  
  @Override
  public CreatureAttribute getCreatureAttribute() { return CreatureAttribute.UNDEAD; }
  
  @Override
  public boolean canDespawn(double distanceToClosestPlayer) { return !this.hasOwner(); }
  
  @Override
  protected float getStandingEyeHeight(Pose pose, EntitySize size) { return this.isSpawningTime() ? 0.05F : 1.74F; }
  
  @Override
  public double getYOffset() { return -0.6D; }
  
  // Team methods //

  @Override
  public Team getTeam() {
    return getOwnerTeam(super.getTeam());
  }

  @Override
  public boolean isOnSameTeam(final Entity entity) {
    return isOnSameTeamAs(entity) || super.isOnSameTeam(entity);
  }
  
  // NBT methods //
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    writeOwner(compound);
    if (this.limitedLifespan) {
      compound.putInt(KEY_LIFE_TICKS, this.limitedLifeTicks);
    }
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    readOwner(compound);
    if (compound.contains(KEY_LIFE_TICKS)) {
      setLimitedLife(compound.getInt(KEY_LIFE_TICKS));
    }
  }
  
  // Attack predicate methods //
  
  @Override
  public boolean shouldAttackEntity(LivingEntity target, LivingEntity owner) {
    if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity)) {
      if (target instanceof SpartiEntity) {
        SpartiEntity spartiEntity = (SpartiEntity) target;
        return !spartiEntity.hasOwner() || spartiEntity.getOwner() != owner;
      } else if (target instanceof PlayerEntity && owner instanceof PlayerEntity
          && !((PlayerEntity) owner).canAttackPlayer((PlayerEntity) target)) {
        return false;
      } else if (target instanceof AbstractHorseEntity && ((AbstractHorseEntity) target).isTame()) {
        return false;
      } else {
        return !(target instanceof TameableEntity) || !((TameableEntity) target).isTamed();
      }
    } else {
      return false;
    }
  }

  @Override
  public boolean canAttack(LivingEntity entity) {
    if (isOwner(entity)) {
      return false;
    }
    return super.canAttack(entity);
  }
  
  
  class SpawningGoal extends Goal {
    
    public SpawningGoal() {
      setMutexFlags(EnumSet.allOf(Goal.Flag.class));
    }

    @Override
    public boolean shouldExecute() {
      return SpartiEntity.this.isSpawningTime();
    }
    
    @Override
    public void tick() {
      SpartiEntity.this.getNavigator().clearPath();
    }

  }
}
