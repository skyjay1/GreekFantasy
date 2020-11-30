package greekfantasy.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;

public class CharybdisEntity extends WaterMobEntity {
  
  private static final DataParameter<Byte> STATE = EntityDataManager.createKey(CharybdisEntity.class, DataSerializers.BYTE);
  private static final String KEY_STATE = "CharybdisState";
  // bytes to use in STATE
  private static final byte NONE = (byte)0;
  private static final byte SPAWNING = (byte)1;
  
  // other constants for attack, spawn, etc.
 
  private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS));
  
  private int spawnTime;
  
  public CharybdisEntity(final EntityType<? extends CharybdisEntity> type, final World worldIn) {
    super(type, worldIn);
    this.stepHeight = 1.0F;
    this.experienceValue = 50;
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 14.0D) // TODO change this before release
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.29D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
        .createMutableAttribute(Attributes.FOLLOW_RANGE, 48.0D);
  }
  
  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(STATE, Byte.valueOf(NONE));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new SwimGoal(this));
//    this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
//    this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
//    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
//    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
//    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
//    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();

    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
   
    // update spawn time
//    if(isSpawning() && --spawnTime <= 0) {
//      // update timer
//      setSpawning(false);
//    }
//    
//    // update smash attack
//    if(this.isSpitAttack()) {
//      spitTime++;
//    } else if(spitTime > 0) {
//      spitTime = 0;
//    }
  }

  // Misc //
  
  @Override
  public boolean isNonBoss() { return false; }
  
  @Override
  public boolean canDespawn(final double disToPlayer) { return false; }
  
  @Override
  protected boolean canBeRidden(Entity entityIn) { return false; }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return source == DamageSource.IN_WALL || source == DamageSource.WITHER || super.isInvulnerableTo(source);
  }
  
  @Override
  protected void updateAir(int air) { }
  
  // Boss //

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

  // Sounds //
  
//  @Override
//  protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_SPIDER_AMBIENT; }
//
//  @Override
//  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_SPIDER_HURT; }
//
//  @Override
//  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_SPIDER_DEATH; }

  @Override
  protected float getSoundVolume() { return 1.0F; }
  
  @Override
  protected float getSoundPitch() { return 0.6F + rand.nextFloat() * 0.2F; }
  
  // NBT //
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
     super.writeAdditional(compound);
     compound.putByte(KEY_STATE, this.getCharybdisState());
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
     super.readAdditional(compound);
     this.setCharybdisState(compound.getByte(KEY_STATE));
  }
  
  // States //
  
  public byte getCharybdisState() { return this.getDataManager().get(STATE).byteValue(); }
  
  public void setCharybdisState(final byte state) { this.getDataManager().set(STATE, Byte.valueOf(state)); }
  
  public boolean isNoneState() { return getCharybdisState() == NONE; }
  
  
  // Attack //
  
//  @Override
//  public boolean attackEntityAsMob(final Entity entity) {
//    if (super.attackEntityAsMob(entity)) {
//      if (entity instanceof LivingEntity) {
//        ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.POISON, 3 * 20, 0));
//      }
//      return true;
//    }
//    return false;
//  }

  
  // Goals //
  
}
