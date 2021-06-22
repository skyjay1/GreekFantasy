package greekfantasy.entity;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class HydraEntity extends MonsterEntity {
  
  private static final DataParameter<Byte> HEADS = EntityDataManager.createKey(HydraEntity.class, DataSerializers.BYTE);
  private static final String KEY_HEADS = "Heads";
  
  public static final int MAX_HEADS = 10;
    
  public HydraEntity(final EntityType<? extends HydraEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 200.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.24D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 0.0D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.66D)
        .createMutableAttribute(Attributes.ARMOR, 5.0D);
  }
  
  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(HEADS, Byte.valueOf((byte)0));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(4, new MoveTowardsTargetGoal(this, 1.0D, (float)getAttribute(Attributes.FOLLOW_RANGE).getBaseValue()));
//    this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
//    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    if(!getPassengers().isEmpty() && !world.isRemote()) {
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
        attackEntityFrom(source != null ? source : DamageSource.STARVE, getMaxHealth() * 2.0F);
      } else if(getHealth() < getMaxHealth() && rand.nextFloat() < 0.125F){
        heal(1.25F * (getHeads() - charred));
      }
    }
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    addHead(0);
    addHead(1);
    addHead(2);
    setChild(false);
    return data;
  }
  
  // Heads //
  
  public int getHeads() { return getDataManager().get(HEADS).intValue(); }
  
  public void setHeads(final int heads) { getDataManager().set(HEADS, Byte.valueOf((byte)heads)); }
  
  /**
   * Adds a head to this hydra
   * @param id a unique id of the head
   * @return the hydra head entity
   */
  public HydraHeadEntity addHead(final int id) {
//    GreekFantasy.LOGGER.debug("Adding head with id " + id);
    if(!world.isRemote()) {
      HydraHeadEntity head =  GFRegistry.HYDRA_HEAD_ENTITY.create(getEntityWorld());
      head.setLocationAndAngles(getPosX(), getPosY(), getPosZ(), 0, 0);
      // add the entity to the world
      world.addEntity(head);
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
  public void removePassengers() {
    super.removePassengers();
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
  protected boolean canBeRidden(Entity entityIn) { return false; }
  
  @Override
  protected boolean canFitPassenger(Entity passenger) { return this.getPassengers().size() < MAX_HEADS; }

  public void updatePassenger(Entity passenger, int id, Entity.IMoveCallback callback) {
    if (this.isPassenger(passenger)) {
      double radius = getWidth() * 0.5D;
      double heads = (double)getHeads();
      // the distance between heads
      double wid = ((double)id - heads / 2.0D) * 0.85D;
      // the angle to add based on hydra rotation yaw
      double angleOff = Math.toRadians(this.rotationYaw) + Math.PI / 2.0D;
      // determine x,y,z position for the head
      double dx = this.getPosX() + radius * Math.cos(wid / Math.PI + angleOff);
      double dy = this.getPosY() + this.getMountedYOffset() + passenger.getYOffset();
      double dz = this.getPosZ() + radius * Math.sin(wid / Math.PI + angleOff);
      callback.accept(passenger, dx, dy, dz);
    }
  }
  
  @Override
  public double getMountedYOffset() { return super.getMountedYOffset() + 0.32D; }
  
  // Sounds //
  
  @Override
  protected SoundEvent getAmbientSound() {
    return SoundEvents.ENTITY_BLAZE_AMBIENT;
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_GENERIC_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_GENERIC_DEATH; }

  @Override
  protected float getSoundVolume() { return 1.2F; }
  
  // NBT methods //
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putByte(KEY_HEADS, (byte)getHeads());
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    setHeads((int)compound.getByte(KEY_HEADS));    
  }
}
