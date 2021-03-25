package greekfantasy.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
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
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;

public class NemeanLionEntity extends MonsterEntity {
  
  private static final DataParameter<Byte> STATE = EntityDataManager.createKey(NemeanLionEntity.class, DataSerializers.BYTE);
  private static final String KEY_STATE = "NemeanState";
  //bytes to use in STATE
  private static final byte NONE = (byte)0;
  private static final byte SPAWNING = (byte)1;
  private static final byte ROARING = (byte)2;
  private static final byte ATTACKING = (byte)3;
  private static final byte SITTING = (byte)4;
  
  private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.YELLOW, BossInfo.Overlay.PROGRESS));
  
  public NemeanLionEntity(final EntityType<? extends NemeanLionEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 84.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.28D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.92D)
        .createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.0D)
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
    this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.54F));
    this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.25D, true));
    this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.92D));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 8.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false)); 
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
  }
  
  // Misc //
  
  @Override
  protected void damageEntity(final DamageSource source, final float amountIn) {
    float damageAmount = amountIn;
    if (!source.isDamageAbsolute() && !source.isMagicDamage()) {
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
}
