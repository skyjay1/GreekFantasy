package greekfantasy.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HydraHeadEntity extends MonsterEntity {
  
  private static final DataParameter<Byte> PART_ID = EntityDataManager.createKey(HydraHeadEntity.class, DataSerializers.BYTE);
  private static final DataParameter<Byte> STATE = EntityDataManager.createKey(HydraHeadEntity.class, DataSerializers.BYTE);
  private static final String KEY_ID = "HydraHeadId";
  private static final String KEY_STATE = "HydraHeadState";
  // bytes to use in STATE
  private static final byte NORMAL = (byte)0;
  private static final byte SEVERED = (byte)1;
  private static final byte GROWING = (byte)2;
  private static final byte CHARRED = (byte)3;
  // bytes to use in World#setEntityState
  private static final byte GROWING_CLIENT = (byte)8;
  private static final byte CHANGE_SIZE_CLIENT = (byte)9;

  /** The amount of time to spend "severed" before growing **/
  private final int maxSeveredTime = 100;
  private int severedTime;
  
  /** The amount of time to spend "growing" before normal **/
  private final int maxGrowTime = 60;
  private int growTime;
  
  private final EntitySize severedSize;
      
  public HydraHeadEntity(final EntityType<? extends HydraHeadEntity> type, final World world) {
    super(type, world);
    severedSize = EntitySize.flexible(type.getWidth() * 0.75F, type.getHeight() * 0.25F);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 16.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.24D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 6.0D);
  }
  
  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(PART_ID, Byte.valueOf((byte)0));
    this.getDataManager().register(STATE, Byte.valueOf(NORMAL));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new SwimGoal(this));
//    this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
//    this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
//    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
//    this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setCallsForHelp());
//    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
  }
  
  @Override
  public void tick() {
    // remove when not linked to a hydra
    if(!hasHydra() && !world.isRemote()) {
      remove();
      return;
    }
    
    // update severed timer
    if(!isCharred() && isSevered() && severedTime > 0 && ++severedTime > maxSeveredTime) {
      severedTime = 0;
      setGrowing();
    }
    
    // update growing timer
    if(!isCharred() && isGrowing() && growTime > 0 && ++growTime > maxGrowTime) {
      growTime = 0;
      setHealth(getMaxHealth());
      setNormal();
    }
    
    // recalculate size
    if(isCharred() || isSevered()) {
      recalculateSize();
    }
    
    if(isCharred() && world.isRemote() && rand.nextInt(5) == 0) {
      world.addParticle(ParticleTypes.SMOKE, 
          getPosX() + (rand.nextDouble() - 0.5D) * getWidth(), 
          getPosY() + getHeight(), 
          getPosZ() + (rand.nextDouble() - 0.5D) * getWidth(), 0.0D, 0.0D, 0.0D);
    }
    
    super.tick();
  }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return ((isSevered() || isGrowing()) && !source.isFireDamage()) 
        || isCharred() || source == DamageSource.IN_WALL || source == DamageSource.WITHER 
        || super.isInvulnerableTo(source);
  }
  
  @Override
  public void onDeath(DamageSource cause) {
    // set this head to "severed"
    setSevered();
    // either set this head to "charred" or add another head that is currently "severed"
    if(this.getFireTimer() > 0) {
      setNoAI(true);
      setCharred();
    } else {
      HydraHeadEntity head = getHydra().addHead(getHydra().getHeads());
      head.setSevered();
    }
    // reset health to prevent removal
    this.setHealth(1.0F);
    this.recalculateSize();
    world.setEntityState(this, CHANGE_SIZE_CLIENT);
  }
  
  @Override
  public boolean canDespawn(double distanceToClosestPlayer) { return false; }

  @Override
  public boolean canBeCollidedWith() { return isNormal(); }
  
  @Override
  protected boolean canBeRidden(Entity entityIn) { return entityIn instanceof HydraEntity; }

  @Override
  public boolean attackEntityFrom(DamageSource source, float amount) {
    if(super.attackEntityFrom(source, amount) && hasHydra()) {
      getHydra().attackEntityFrom(source, amount * 0.1F);
    }
    return false;
  }

  // Hydra body methods //
  
  public void setPartId(final int id) { getDataManager().set(PART_ID, Byte.valueOf((byte)id)); }
  
  public int getPartId() { return getDataManager().get(PART_ID).intValue(); }
  
  public boolean hasHydra() { return getRidingEntity() instanceof HydraEntity; }

  /**
   * @return the Hydra this head belongs to, or null if it is not found
   */
  @Nullable
  public HydraEntity getHydra() {
    if(getRidingEntity() instanceof HydraEntity) {
      return (HydraEntity)getRidingEntity();
    }    
    return null;
  }
  
  @Override
  public void updateRidden() {
    this.setMotion(Vector3d.ZERO);
    if (canUpdate())
      this.tick();
    if (this.isPassenger() && hasHydra()) {
       getHydra().updatePassenger(this, getPartId(), Entity::setPosition);
    }
  }

  //States //

  public byte getHeadState() { return this.getDataManager().get(STATE).byteValue(); }
  public void setHeadState(final byte state) { 
    this.getDataManager().set(STATE, Byte.valueOf(state));
    this.recalculateSize();
    if(!world.isRemote()) {
      world.setEntityState(this, CHANGE_SIZE_CLIENT);
    }
  }

  public boolean isNormal() { return getHeadState() == NORMAL; }
  public boolean isSevered() { return severedTime > 0 || getHeadState() == SEVERED; }
  public boolean isGrowing() { return growTime > 0 || getHeadState() == GROWING; }
  public boolean isCharred() { return getHeadState() == CHARRED; }

  public void setNormal() { setHeadState(NORMAL); }
  public void setCharred() { setHeadState(CHARRED); } 

  public void setSevered() { 
    setHeadState(SEVERED);
    severedTime = 1;
  }
  
  public void setGrowing() {
    setHeadState(GROWING);
    growTime = 1;
    if (!this.world.isRemote()) {
      this.world.setEntityState(this, GROWING_CLIENT);
    }
  }


  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    switch (id) {
    case GROWING_CLIENT:
      setGrowing();
      recalculateSize();
      break;
    case CHANGE_SIZE_CLIENT:
      recalculateSize();
      break;
    default:
      super.handleStatusUpdate(id);
      break;
    }
  }
  
  // Size //
  
  @Override
  public EntitySize getSize(Pose poseIn) {
    return this.isNormal() ? super.getSize(poseIn) : severedSize;
  }

  @Override
  protected float getStandingEyeHeight(Pose pose, EntitySize size) {
    return this.isNormal() ? super.getStandingEyeHeight(pose, size) : severedSize.height * 0.85F;
  }
  
  // NBT Methods //

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putByte(KEY_ID, (byte)getPartId());
    compound.putByte(KEY_STATE, this.getHeadState());
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    setPartId((int)compound.getByte(KEY_ID));
    setHeadState(compound.getByte(KEY_STATE));
  }
  
  // render percent helpers
  
  @OnlyIn(Dist.CLIENT)
  public float getSpawnPercent() {
    return growTime > 0 ? (float)growTime / (float)maxGrowTime : 1.0F;
  }
}
