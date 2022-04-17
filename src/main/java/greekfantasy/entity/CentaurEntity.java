package greekfantasy.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CentaurEntity extends CreatureEntity implements IAngerable, IRangedAttackMob {
  
  private static final DataParameter<Byte> DATA_COLOR = EntityDataManager.defineId(CentaurEntity.class, DataSerializers.BYTE);
  private static final String TAG_COLOR = "Color";
  
  private static final byte REARING_START = 6;
  private static final byte REARING_END = 7;
  
  private static final RangedInteger ANGER_RANGE = TickRangeConverter.rangeOfSeconds(20, 39);
  private int angerTime;
  private UUID angerTarget;
  
  private boolean isRearing;
  
  public int tailCounter;
  private float rearingAmount;
  private float prevRearingAmount;
  private int rearingCounter;

  public CentaurEntity(final EntityType<? extends CentaurEntity> type, final World worldIn) {
    super(type, worldIn);
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 34.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.25D)
        .add(Attributes.ATTACK_DAMAGE, 4.0D)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(2, new CentaurEntity.RangedAttackGoal(this, 1.0D, this.hasBullHead() ? 50 : 35, 15.0F));
    this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false));
    this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isAngryAt));
    this.targetSelector.addGoal(5, new ResetAngerGoal<>(this, true));
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(DATA_COLOR, Byte.valueOf((byte) 0));
  }

  @Override
  public void aiStep() {
    super.aiStep();
    // tail motion timer
    if (this.random.nextInt(200) == 0) {
      this.moveTail();
    }
    // anger timer
    if (!this.level.isClientSide()) {
      this.updatePersistentAnger((ServerWorld) this.level, true);
    }
  }

  @Override
  public void tick() {
    super.tick();
    // tail movement logic
    if (this.tailCounter > 0 && ++this.tailCounter > 8) {
      this.tailCounter = 0;
    }
    // rearing logic
    this.prevRearingAmount = this.rearingAmount;
    if (this.isRearing()) {
      this.rearingAmount += (1.0F - this.rearingAmount) * 0.4F + 0.05F;
      if (this.rearingAmount > 1.0F) {
        this.rearingAmount = 1.0F;
      }
    } else {
      this.rearingAmount += (0.8F * this.rearingAmount * this.rearingAmount * this.rearingAmount - this.rearingAmount) * 0.6F
          - 0.05F;
      if (this.rearingAmount < 0.0F) {
        this.rearingAmount = 0.0F;
      }
    }
    if (this.isEffectiveAi() && this.rearingCounter > 0 && ++this.rearingCounter > 20) {
      this.rearingCounter = 0;
      this.setRearing(false);
    }
  }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    // immune to damage from other centaurs
    if(source.getEntity() != null && source.getEntity().getType() == GFRegistry.CENTAUR_ENTITY) {
      return true;
    }
    return super.isInvulnerableTo(source);
  }
  
  // Ranged Attack methods
 
  @Override
  public void performRangedAttack(LivingEntity target, float distanceFactor) {
     ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this, Items.BOW)));
     AbstractArrowEntity arrow = ProjectileHelper.getMobArrow(this, itemstack, distanceFactor);
     if (this.getMainHandItem().getItem() instanceof net.minecraft.item.BowItem)
        arrow = ((net.minecraft.item.BowItem)this.getMainHandItem().getItem()).customArrow(arrow);
     // this is copied from LlamaSpit code, it moves the arrow nearer to the centaur's human-body
     arrow.setPos(this.getX() - (this.getBbWidth() + 1.0F) * 0.5D * MathHelper.sin(this.yBodyRot * 0.017453292F), this.getEyeY() - 0.10000000149011612D, this.getZ() + (this.getBbWidth() + 1.0F) * 0.5D * MathHelper.cos(this.yBodyRot * 0.017453292F));
     double dx = target.getX() - arrow.getX();
     double dy = target.getY(0.67D) - arrow.getY();
     double dz = target.getZ() - arrow.getZ();
     double dis = (double)MathHelper.sqrt(dx * dx + dz * dz);
     arrow.shoot(dx, dy + dis * (double)0.2F, dz, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
     this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
     this.level.addFreshEntity(arrow);
  }
  
  // NBT Methods

  @Override
  public void addAdditionalSaveData(CompoundNBT compound) {
    super.addAdditionalSaveData(compound);
    compound.putByte(TAG_COLOR, (byte) this.getCoatColor().getId());
    this.addPersistentAngerSaveData(compound);
  }

  @Override
  public void readAdditionalSaveData(CompoundNBT compound) {
    super.readAdditionalSaveData(compound);
    this.setCoatColor(CoatColors.byId(compound.getByte(TAG_COLOR)));
    this.readPersistentAngerSaveData((ServerWorld)this.level, compound);
  }
  
  //IAngerable methods
  
  @Override
  public void startPersistentAngerTimer() { this.setRemainingPersistentAngerTime(ANGER_RANGE.randomValue(this.random)); }
  @Override
  public void setRemainingPersistentAngerTime(int time) { this.angerTime = time; }
  @Override
  public int getRemainingPersistentAngerTime() { return this.angerTime; }
  @Override
  public void setPersistentAngerTarget(@Nullable UUID target) { this.angerTarget = target; }
  @Override
  public UUID getPersistentAngerTarget() { return this.angerTarget; }
 
  // End IAngerable methods

  @Nullable
  public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final CoatColors color = Util.getRandom(CoatColors.values(), this.random);
    this.setCoatColor(color);
    if(this.random.nextInt(3) > 0) {
      this.setItemInHand(Hand.MAIN_HAND, new ItemStack(Items.BOW));
    }
    return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }

  @Override
  protected int calculateFallDamage(float distance, float damageMultiplier) {
    return MathHelper.ceil((distance * 0.5F - 3.0F) * damageMultiplier);
  }

  @Override
  public boolean onClimbable() {
    return false;
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
     if (this.random.nextInt(3) == 0) {
        this.makeRear();
     }

     return null;
  }

  @Override
  protected SoundEvent getAmbientSound() {
    if (this.random.nextInt(10) == 0 && !this.isImmobile()) {
      this.makeRear();
    }
    return null;
  }

  @Override
  protected float getSoundVolume() {
    return 0.8F;
  }

  @Override
  public int getAmbientSoundInterval() {
    return 400;
  }

  @OnlyIn(Dist.CLIENT)
  public void handleEntityEvent(byte id) {
    switch (id) {
    case REARING_START:
      this.isRearing = true;
      break;
    case REARING_END:
      this.isRearing = false;
      break;
    default:
      super.handleEntityEvent(id);
      break;
    }
  }

  // Rearing and tail-movement methods

  public void makeRear() {
    if (this.isEffectiveAi()) {
      this.rearingCounter = 1;
      this.setRearing(true);
    }
  }

  public void setRearing(final boolean rearing) {
    this.isRearing = rearing;
    this.level.broadcastEntityEvent(this, rearing ? REARING_START : REARING_END);
  }

  public boolean isRearing() {  return this.isRearing; }
  public void moveTail() { this.tailCounter = 1; }
  public float getRearingAmount(float partialTick) { return partialTick > 0.99F ? rearingAmount : MathHelper.lerp(partialTick, prevRearingAmount, rearingAmount); }
  
  // Coat colors
  public void setCoatColor(final CoatColors color) { this.getEntityData().set(DATA_COLOR, (byte) color.getId()); }
  public CoatColors getCoatColor() { return CoatColors.byId(this.getEntityData().get(DATA_COLOR).intValue()); }

  /** @return whether to render using a bull-headed texture **/
  public boolean hasBullHead() { return false; }
  
  class RangedAttackGoal extends net.minecraft.entity.ai.goal.RangedAttackGoal {
    public RangedAttackGoal(IRangedAttackMob entity, double moveSpeed, int attackInterval, float attackDistance) {
      super(entity, moveSpeed, attackInterval, attackDistance);
    }
    
    @Override
    public boolean canUse() {
      return (super.canUse() && CentaurEntity.this.getMainHandItem().getItem() instanceof BowItem);
    }
    
    @Override
    public void start() {
      super.start();
      CentaurEntity.this.setAggressive(true);
    }
    
    @Override
    public void stop() {
      super.stop();
      CentaurEntity.this.setAggressive(false);
    }
  }
}
