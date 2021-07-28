package greekfantasy.entity;

import java.util.EnumSet;

import javax.annotation.Nullable;

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
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MakhaiEntity extends TameableEntity {
  protected static final DataParameter<Byte> STATE = EntityDataManager.createKey(MakhaiEntity.class, DataSerializers.BYTE);
  protected static final String KEY_STATE = "MakhaiState";
  // bytes to use in STATE
  private static final byte NONE = (byte)0;
  private static final byte SPAWNING = (byte)1;
  private static final byte DESPAWNING = (byte)2;
  // bytes to use in World#setEntityState
  private static final byte SPAWN_CLIENT = 9;
  private static final byte DESPAWN_CLIENT = 10;
    
  /** The max time spent spawning or despawning **/
  protected final int maxSpawnTime = 25;
  protected int spawnTime;
  protected int despawnTime;
    
  public MakhaiEntity(final EntityType<? extends MakhaiEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 12.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.29D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 5.0D)
        .createMutableAttribute(Attributes.ARMOR, 3.0D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new MakhaiEntity.SpawningGoal());
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
    this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
    this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.78D));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 5, false, false, e -> !MakhaiEntity.this.isOnSameTeam(e)));
    this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, MobEntity.class, 5, false, false, e -> e instanceof IMob && EntityPredicates.CAN_AI_TARGET.test(e) && !MakhaiEntity.this.isOnSameTeam(e)));
  }
  
  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(STATE, Byte.valueOf(NONE));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // update spawn time
    if(isSpawning()) {
      if(--spawnTime <= 0) {
        setSpawning(false);
      }
    }
    
    // update despawn time
    if(isDespawning()) {
      if(--despawnTime <= 0) {
        setDespawning(false);
        remove();
        return;
      }
    }
    
    // determine when to despawn
    if(!world.isRemote() && !this.isAIDisabled() && (getAttackTarget() == null || getNavigator().noPath()) && !isDespawning() && rand.nextInt(280) == 0) {
      setDespawning(true);
    }
    
    // spawn particles
    if(world.isRemote() && (isSpawning() || isDespawning())) {
      final double x = this.getPosX();
      final double y = this.getPosY() + 0.5D;
      final double z = this.getPosZ();
      final double motion = 0.06D;
      final double radius = this.getWidth() * 1.15D;
      for(int i = 0; i < 4; i++) {
        world.addParticle(ParticleTypes.LARGE_SMOKE, 
          x + (world.rand.nextDouble() - 0.5D) * radius, 
          y + (world.rand.nextDouble() - 0.5D) * radius, 
          z + (world.rand.nextDouble() - 0.5D) * radius,
          (world.rand.nextDouble() - 0.5D) * motion, 
          (world.rand.nextDouble() - 0.5D) * 0.07D,
          (world.rand.nextDouble() - 0.5D) * motion);
      }
    }
  }
  
  public void setEquipmentOnSpawn() {
    setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
    setHeldItem(Hand.OFF_HAND, new ItemStack(Items.GOLDEN_SWORD));
    setDropChance(EquipmentSlotType.MAINHAND, 0);
    setDropChance(EquipmentSlotType.OFFHAND, 0);
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    setEquipmentOnSpawn();
    setSpawning(true);
    setChild(false);
    return data;
  }
  
  @Override
  public boolean isBreedingItem(final ItemStack item) { return false; }
  
  // State methods //
  
  public byte getState() { return this.getDataManager().get(STATE).byteValue(); }
  
  public void setState(final byte state) { this.getDataManager().set(STATE, Byte.valueOf(state)); }
  
  public boolean isNoneState() { return getState() == NONE; }
  
  public boolean isSpawning() { return spawnTime > 0 || getState() == SPAWNING; }
  
  public boolean isDespawning() { return despawnTime > 0 || getState() == DESPAWNING; }
  
  public void setSpawning(final boolean spawning) {
    spawnTime = spawning ? maxSpawnTime : 0;
    setState(spawning ? SPAWNING : NONE);
    if(spawning && !this.world.isRemote()) {
      this.world.setEntityState(this, SPAWN_CLIENT);
    }
  }
  
  public void setDespawning(final boolean despawning) {
    despawnTime = despawning ? maxSpawnTime : 0;
    setState(despawning ? DESPAWNING : NONE);
    if(despawning && !this.world.isRemote()) {
      this.world.setEntityState(this, DESPAWN_CLIENT);
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    switch(id) {
    case SPAWN_CLIENT:
      setSpawning(true);
      spawnTime = maxSpawnTime;
      break;
    case DESPAWN_CLIENT:
      setDespawning(true);
      despawnTime = maxSpawnTime;
      break;
    default:
      super.handleStatusUpdate(id);
      break;
    }
  }
  
  // Misc. methods //
  
  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_DROWNED_AMBIENT; }

  @Override
  protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ENTITY_DROWNED_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_PHANTOM_DEATH; }

  @Override
  protected void playStepSound(BlockPos pos, BlockState blockIn) { this.playSound(SoundEvents.ENTITY_ZOMBIE_STEP, 0.15F, 1.0F); }
    
  @Override
  public CreatureAttribute getCreatureAttribute() { return CreatureAttribute.UNDEAD; }
  
  @Override
  public boolean canDespawn(double distanceToClosestPlayer) { return !this.isTamed(); }
  
  @Override
  protected float getStandingEyeHeight(Pose pose, EntitySize size) { return this.isSpawning() ? 0.05F : 1.74F; }
  
  @Override
  public double getYOffset() { return -0.6D; }
  
  @Override
  public boolean canBeLeashedTo(PlayerEntity player) { return false; }
  
  @Override
  public boolean canMateWith(AnimalEntity otherAnimal) { return false; }
  
  @Override
  public AgeableEntity createChild(ServerWorld world, AgeableEntity mate) { return null; }
  
  @Override
  public void setSitting(boolean sitting) { }
  
  @Override
  public void onDeath(DamageSource cause) {
    setOwnerId(null);
    super.onDeath(cause);
  }
  
  // NBT methods //
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putByte(KEY_STATE, getState());
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    setState(compound.getByte(KEY_STATE));
  }
  
  // Attack predicate methods //

  @Override
  public boolean canAttack(LivingEntity entity) {
    if (isOwner(entity)) {
      return false;
    }
    return super.canAttack(entity);
  }
  
  @Override
  public boolean shouldAttackEntity(LivingEntity target, LivingEntity owner) {
    if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity)) {
      if (target instanceof TameableEntity) {
        TameableEntity tameable = (TameableEntity) target;
        return !tameable.isTamed() || tameable.getOwner() != owner;
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
  
  // Goals //
  
  class SpawningGoal extends Goal {
    
    public SpawningGoal() {
      setMutexFlags(EnumSet.allOf(Goal.Flag.class));
    }

    @Override
    public boolean shouldExecute() {
      return MakhaiEntity.this.isSpawning() || MakhaiEntity.this.isDespawning();
    }
    
    @Override
    public void tick() {
      MakhaiEntity.this.getNavigator().clearPath();
    }

  }
}
