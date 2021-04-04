package greekfantasy.entity;

import java.util.EnumSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
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
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 84.0D)
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
    this.goalSelector.addGoal(1, new NemeanLionEntity.SitGoal());
    this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.54F));
    this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.15D, true));
    this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.86D){
      @Override
      public boolean shouldExecute() {
        return !NemeanLionEntity.this.isSitting() && NemeanLionEntity.this.rand.nextInt(400) == 0 && super.shouldExecute();
      }
    });
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 8.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false, e -> EntityPredicates.CAN_HOSTILE_AI_TARGET.test(e) && e.isNonBoss() && !e.isInWater()));
    
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    
    // randomly sit, or unsit if attacking
    if(!this.world.isRemote()) {
      if(this.getAttackTarget() == null) {
        if(rand.nextFloat() < 0.015F) {
          setSitting(true);
        }
      } else if(isSitting()) {
        setSitting(false);
      }
    }
  }
  
  // Misc //
  
  @Override
  protected void damageEntity(final DamageSource source, final float amountIn) {
    float damageAmount = amountIn;
    if (!source.isDamageAbsolute() && !source.isMagicDamage() && !source.isUnblockable()) {
      damageAmount = Math.min(2.0F, amountIn);
    }
    super.damageEntity(source, damageAmount);
  }

  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return source == DamageSource.IN_WALL || source == DamageSource.WITHER 
        || source.isProjectile() || super.isInvulnerableTo(source);
  }

  @Override
  public boolean isOnLadder() { return false; }

  // Boss //

  @Override
  public boolean isNonBoss() { return false; }

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
}
