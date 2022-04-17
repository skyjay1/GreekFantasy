package greekfantasy.entity;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class HydraEntity extends MonsterEntity {
  
  private static final DataParameter<Byte> HEADS = EntityDataManager.defineId(HydraEntity.class, DataSerializers.BYTE);
  private static final String KEY_HEADS = "Heads";
  
  public static final int MAX_HEADS = 11;
    
  public HydraEntity(final EntityType<? extends HydraEntity> type, final World worldIn) {
    super(type, worldIn);
    this.maxUpStep = 1.0F;
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 200.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.24D)
        .add(Attributes.ATTACK_DAMAGE, 0.0D)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.66D)
        .add(Attributes.ARMOR, 5.0D)
        .add(Attributes.FOLLOW_RANGE, 16.0D);
  }
  
  @Override
  public void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(HEADS, Byte.valueOf((byte)0));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(2, new HydraEntity.MoveToTargetGoal(this, 1.0D, false));
    this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D) {
      @Override
      public boolean canUse() {
        return HydraEntity.this.random.nextInt(400) == 0 && super.canUse();
      }
    });
    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
    this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, false, false));
  }
  
  @Override
  public void aiStep() {
    super.aiStep();
    
    if(!getPassengers().isEmpty() && !level.isClientSide()) {
      // determine if all heads are charred
      int charred = 0;
      HydraHeadEntity head;
      for(final Entity entity : getPassengers()) {
        head = (HydraHeadEntity)entity;
        if(head.isCharred()) {
          charred++;
        }
      }
      // if all heads are charred, kill the hydra; otherwise, heal the hydra
      if(charred == getHeads()) {
        DamageSource source = this.getLastDamageSource();
        hurt(source != null ? source : DamageSource.STARVE, getMaxHealth() * 2.0F);
        getPassengers().forEach(e -> e.remove());
      } else if(getHealth() < getMaxHealth() && random.nextFloat() < 0.125F){
        heal(1.25F * (getHeads() - charred));
      }
    }
  }
  
  @Override
  public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final ILivingEntityData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    addHead(0);
    addHead(1);
    addHead(2);
    setBaby(false);
    return data;
  }
  
  @Override
  public boolean canBeAffected(EffectInstance potioneffectIn) {
    if (potioneffectIn.getEffect() == Effects.POISON) {
      net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent event = new net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent(
          this, potioneffectIn);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
      return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;
    }
    return super.canBeAffected(potioneffectIn);
  }
  
  // Heads //
  
  public int getHeads() { return getEntityData().get(HEADS).intValue(); }
  
  public void setHeads(final int heads) { getEntityData().set(HEADS, Byte.valueOf((byte)heads)); }
  
  /**
   * Adds a head to this hydra
   * @param id a unique id of the head
   * @return the hydra head entity
   */
  public HydraHeadEntity addHead(final int id) {
    // GreekFantasy.LOGGER.debug("Adding head with id " + id);
    if(!level.isClientSide()) {
      HydraHeadEntity head =  GFRegistry.HYDRA_HEAD_ENTITY.create(level);
      head.moveTo(getX(), getY(), getZ(), 0, 0);
      // add the entity to the world (commented out bc of errors: "trying to add entity with duplicated UUID ...")
      // world.addEntity(head);
      // update the entity data
      head.setPartId(id);
      if(head.startRiding(this)) {
        // increase the number of heads
        setHeads(getHeads() + 1);
      } else {
        head.remove();
      }
      return head;
    }
    return null;
  }
  
  @Override
  protected void removePassenger(Entity passenger) {
    super.removePassenger(passenger);
    setHeads(Math.max(0, getHeads() - 1));
  }
  
  @Override
  public void ejectPassengers() {
    super.ejectPassengers();
    setHeads(0);
  }
  
  @Override
  public void remove() {
    super.remove();
    for(final Entity e : getPassengers()) {
      if(e.getType() == GFRegistry.HYDRA_HEAD_ENTITY) {
        e.remove();
      }
    }
  }
  
  @Override
  protected boolean canRide(Entity entityIn) { return false; }
  
  @Override
  protected boolean canAddPassenger(Entity passenger) { return this.getPassengers().size() < MAX_HEADS; }

  public void updatePassenger(Entity passenger, int id, Entity.IMoveCallback callback) {
    if (this.hasPassenger(passenger)) {
      int headsPerRow = MAX_HEADS / 2;
      int row = id / headsPerRow;
      double heads = (double)getHeads();
      double radius = 0.08D + 0.4D * getBbWidth() + 0.35D * row;
      // the index location of the head, based on id and row, and centered based on total heads
      double index = ((double) (id % headsPerRow)) - 0.92D * getBbWidth();
      // the angle to add based on hydra rotation yaw
      double angleOff = Math.toRadians(this.yRot + (heads / headsPerRow) * 6.0F) + Math.PI / 2.0D;
      // determine x,y,z position for the head
      double dx = this.getX() + radius * Math.cos(index / Math.PI + angleOff);
      double dy = this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset();
      double dz = this.getZ() + radius * Math.sin(index / Math.PI + angleOff);
      callback.accept(passenger, dx, dy, dz);
    }
  }
  
  @Override
  public double getPassengersRidingOffset() { return super.getPassengersRidingOffset() + 0.32D; }
  
  // Sounds //
  
  @Override
  protected SoundEvent getAmbientSound() {
    return SoundEvents.BLAZE_AMBIENT;
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.GENERIC_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.GENERIC_DEATH; }

  @Override
  protected float getSoundVolume() { return 1.2F; }
  
  // NBT methods //
  
  @Override
  public void addAdditionalSaveData(CompoundNBT compound) {
    super.addAdditionalSaveData(compound);
    compound.putByte(KEY_HEADS, (byte)getHeads());
  }

  @Override
  public void readAdditionalSaveData(CompoundNBT compound) {
    super.readAdditionalSaveData(compound);
    setHeads((int)compound.getByte(KEY_HEADS));    
  }
  
  class MoveToTargetGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {

    public MoveToTargetGoal(CreatureEntity creature, double speedIn, boolean useLongMemoryIn) {
      super(creature, speedIn, useLongMemoryIn);
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
      double d0 = this.getAttackReachSqr(enemy);
      if (distToEnemySqr <= d0 && this.getTicksUntilNextAttack() <= 0) {
        this.resetAttackCooldown();
        HydraEntity.this.setLastHurtMob(enemy);
        // this version of the goal intentionally does *not* attack the target, that will be done by the heads
      }
    }
  }
}
