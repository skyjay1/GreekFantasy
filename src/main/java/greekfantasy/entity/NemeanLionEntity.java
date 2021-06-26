package greekfantasy.entity;

import java.util.EnumSet;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NemeanLionEntity extends MonsterEntity {
  
  private static final DataParameter<Byte> STATE = EntityDataManager.createKey(NemeanLionEntity.class, DataSerializers.BYTE);
  private static final String KEY_STATE = "NemeanState";
  //bytes to use in STATE
  private static final byte NONE = (byte)0;
  private static final byte SPAWNING = (byte)1; // unused
  private static final byte ROARING = (byte)2; // unused
  private static final byte ATTACKING = (byte)3; // unused
  private static final byte SITTING = (byte)4;
  // bytes to use in World#setEntityState
  private static final byte SITTING_START_CLIENT = 8;
  private static final byte SITTING_END_CLIENT = 9;
  
  private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.YELLOW, BossInfo.Overlay.PROGRESS));
  
  public NemeanLionEntity(final EntityType<? extends NemeanLionEntity> type, final World worldIn) {
    super(type, worldIn);
    this.enablePersistence();
    this.stepHeight = 1.0F;
    this.experienceValue = 50;
    bossInfo.setVisible(GreekFantasy.CONFIG.showNemeanLionBossBar());
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 100.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.28D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.92D)
        .createMutableAttribute(Attributes.FOLLOW_RANGE, 24.0D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 9.0D)
        .createMutableAttribute(Attributes.ARMOR, 5.0D)
        .createMutableAttribute(Attributes.ARMOR_TOUGHNESS, 2.0D);
  }
  
  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(STATE, Byte.valueOf(NONE));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(1, new NemeanLionEntity.RunAroundLikeCrazyGoal(1.0D));
    this.goalSelector.addGoal(2, new NemeanLionEntity.SitGoal());
    this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.54F));
    this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.15D, true));
    this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.86D){
      @Override
      public boolean shouldExecute() {
        return !NemeanLionEntity.this.isSitting() && NemeanLionEntity.this.rand.nextInt(400) == 0 && super.shouldExecute();
      }
    });
    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, false, false, e -> EntityPredicates.CAN_HOSTILE_AI_TARGET.test(e) && !NemeanLionEntity.this.isBeingRidden()));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, 10, false, false, e -> EntityPredicates.CAN_HOSTILE_AI_TARGET.test(e) && e.canChangeDimension() && !e.isInWater() && !NemeanLionEntity.this.isBeingRidden()));
    
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    
    // randomly sit, or unsit if attacking
    if(!this.world.isRemote()) {
      if(this.getAttackTarget() == null && getPassengers().isEmpty()) {
        if(rand.nextFloat() < 0.0022F) {
          setSitting(!isSitting());
        }
      } else if(isSitting()) {
        setSitting(false);
      }
    }
    
    // update rotation and attack damage while being ridden
    if(isBeingRidden() && getPassengers().get(0) instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity)getPassengers().get(0);
      this.rotationYaw = player.rotationYaw;
      this.prevRotationYaw = this.rotationYaw;
      this.rotationPitch = player.rotationPitch * 0.5F;
      this.setRotation(this.rotationYaw, this.rotationPitch);
      this.renderYawOffset = this.rotationYaw;
      this.rotationYawHead = this.renderYawOffset;
      // strangling damage
      if(this.hurtTime == 0 && !world.isRemote()) {
        this.attackEntityFrom(DamageSource.causePlayerDamage(player), 1.0F + rand.nextFloat());
        // remove regen
        if(this.getActivePotionEffect(Effects.REGENERATION) != null) {
          this.removePotionEffect(Effects.REGENERATION);
        }
      }
    }
  }
  
  // Misc //
  
  @Override
  protected void damageEntity(final DamageSource source, final float amountIn) {
    float damageAmount = amountIn;
    // cap damage at 2.0 (1 heart)
    if (!source.isDamageAbsolute() && !source.isMagicDamage() && !source.isUnblockable()) {
      damageAmount = Math.min(2.0F, amountIn);
    }
    // stop sitting when hurt
    if (!this.world.isRemote() && this.isSitting()) {
      this.setSitting(false);
    }
    super.damageEntity(source, damageAmount);
  }

  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return source == DamageSource.IN_WALL || source == DamageSource.WITHER 
        || source == DamageSource.CACTUS || source == DamageSource.SWEET_BERRY_BUSH
        || source.isProjectile() || super.isInvulnerableTo(source);
  }
  
  @Override
  protected void collideWithEntity(final Entity entityIn) {
    // stop sitting when collided with entity
    if (entityIn instanceof LivingEntity && !this.world.isRemote() && this.isSitting()) {
      this.setSitting(false);
    }
    super.collideWithEntity(entityIn);
  }

  @Override
  public boolean isOnLadder() { return false; }
  
  // Riding //
  
  @Override
  public double getMountedYOffset() { return super.getMountedYOffset() + 0.805D; }
  
  @Override
  public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
    if (!this.isBeingRidden() && !player.isSecondaryUseActive()) {
      if (!this.world.isRemote() && this.canFitPassenger(player)) {
        // mount the player to the entity
        player.startRiding(this);
        // reset sitting
        if(isSitting()) {
          setSitting(false);
        }
      }
      return ActionResultType.func_233537_a_(this.world.isRemote);
    }
    
    return ActionResultType.FAIL;
  }
  
  @Override
  public void removePassengers() {
    if(this.getPassengers().size() > 0) {
      // give lion regen effect when player stops strangling
      addPotionEffect(new EffectInstance(Effects.REGENERATION, 100, 0));
    }
    super.removePassengers();
 }

  // Boss //

  @Override
  public boolean canChangeDimension() { return false; }

  @Override
  protected boolean canBeRidden(Entity entityIn) { return false; }

  @Override
  public boolean canDespawn(double distanceToClosestPlayer) { return false; }
  
  @Override
  public void addTrackingPlayer(ServerPlayerEntity player) {
    super.addTrackingPlayer(player);
    this.bossInfo.addPlayer(player);
  }

  @Override
  public void removeTrackingPlayer(ServerPlayerEntity player) {
    super.removeTrackingPlayer(player);
    this.bossInfo.removePlayer(player);
  }

  // States //
  
  public byte getNemeanState() { return this.getDataManager().get(STATE).byteValue(); }
  
  public void setNemeanState(final byte state) { this.getDataManager().set(STATE, Byte.valueOf(state)); }
  
  public boolean isNoneState() { return getNemeanState() == NONE; }
  
  public boolean isSitting() { return getNemeanState() == SITTING; }
  
  public void setSitting(final boolean sitting) {
    setNemeanState(sitting ? SITTING : NONE);
    if(!this.world.isRemote()) {
      this.world.setEntityState(this, sitting ? SITTING_START_CLIENT : SITTING_END_CLIENT);
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    switch(id) {
    case SITTING_START_CLIENT:
      setSitting(true);
      break;
    case SITTING_END_CLIENT:
      setSitting(false);
      break;
    default:
      super.handleStatusUpdate(id);
      break;
    }
  }

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putByte(KEY_STATE, this.getNemeanState());
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.setNemeanState(compound.getByte(KEY_STATE));
  }
  
  private class SitGoal extends Goal {
    
    public SitGoal() {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.TARGET));
    }

    @Override
    public boolean shouldExecute() { return NemeanLionEntity.this.isSitting(); }
    
    @Override
    public void tick() {
      NemeanLionEntity.this.getNavigator().clearPath();
    }
    
  }
  
  private class RunAroundLikeCrazyGoal extends Goal {
    
    private final NemeanLionEntity lion;
    private final double speed;
    private double targetX;
    private double targetY;
    private double targetZ;

    public RunAroundLikeCrazyGoal(double speedIn) {
      this.lion = NemeanLionEntity.this;
      this.speed = speedIn;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
      if (this.lion.isBeingRidden()) {
        Vector3d vector3d = RandomPositionGenerator.findRandomTarget(this.lion, 5, 4);
        if (vector3d == null) {
          return false;
        } else {
          this.targetX = vector3d.x;
          this.targetY = vector3d.y;
          this.targetZ = vector3d.z;
          return true;
        }
      } else {
        return false;
      }
    }

    @Override
    public void startExecuting() {
      this.lion.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    @Override
    public boolean shouldContinueExecuting() {
      return !this.lion.getNavigator().noPath() && this.lion.isBeingRidden();
    }

    @Override
    public void tick() {
      // randomly remove the passenger
      if (this.lion.getRNG().nextInt(42) == 0) {
        // throw the passenger and apply attack damage
        Entity e = this.lion.getPassengers().get(0);
        this.lion.removePassengers();
        if(e instanceof LivingEntity) {
          LivingEntity passenger = (LivingEntity)e;
          passenger.applyKnockback(2.5F + rand.nextFloat() * 2.0F, rand.nextDouble() * 2.0D - 1.0D, rand.nextDouble() * 2.0D - 1.0D);
          passenger.velocityChanged = true;
          lion.attackEntityAsMob(passenger);
        }
      }

    }

  }
}
