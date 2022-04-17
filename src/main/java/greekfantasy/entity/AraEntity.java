package greekfantasy.entity;

import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.entity.ai.FollowGoal;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
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
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class AraEntity extends CreatureEntity implements IAngerable {
  
  // possible names:
  // Propoetide (original name)
  // Penthus (mourning)
  // Pothus (longing)
  // Thrasus (rashness)
  // Ara (curse)
  // Dysnomia (lawlessness)
  // Coalemus (stupidity)
  
  private static final RangedInteger ANGER_RANGE = TickRangeConverter.rangeOfSeconds(20, 39);
  private int angerTime;
  private UUID angerTarget;
  
  private Item weapon;
  
  public AraEntity(final EntityType<? extends AraEntity> type, final World worldIn) {
    super(type, worldIn);
    weapon = GFRegistry.FLINT_KNIFE;
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 24.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.25D)
        .add(Attributes.ATTACK_DAMAGE, 2.5D);
  }
  
  public static boolean canAraSpawnOn(final EntityType<? extends MobEntity> entity, final IServerWorld world, final SpawnReason reason, 
      final BlockPos pos, final Random rand) {
    return reason == SpawnReason.SPAWNER || (world.getDifficulty() != Difficulty.PEACEFUL && 
        MonsterEntity.isDarkEnoughToSpawn(world, pos, rand) && checkMobSpawnRules(entity, world, reason, pos, rand));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(3, new FollowGoal(this, 1.0D, 6.0F, 12.0F) {
      @Override
      public boolean canUse() { return entity.getRandom().nextInt(80) == 0 && super.canUse(); }
    });
    this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isAngryAt));
    this.targetSelector.addGoal(4, new ResetAngerGoal<>(this, true));  
  }
  
  @Override
  public void aiStep() {
    super.aiStep();
    // anger timer
    if (!this.level.isClientSide()) {
      this.updatePersistentAnger((ServerWorld) this.level, true);
    }
    // when aggressive, equip a weapon
    if(this.isAggressive()) {
      if(this.getItemInHand(Hand.MAIN_HAND).isEmpty()) {
        this.setItemInHand(Hand.MAIN_HAND, new ItemStack(weapon));
        this.getCommandSenderWorld().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_EQUIP_IRON, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
      }
    } else if(this.getTarget() == null && this.getItemInHand(Hand.MAIN_HAND).getItem() == weapon) {
      this.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
    }
  }
  
  // IAngerable methods
  
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

  @Override
  public boolean removeWhenFarAway(double distanceToClosestPlayer) {
    return this.tickCount > 2400;
  }
  
  @Override
  public void addAdditionalSaveData(CompoundNBT compound) {
    super.addAdditionalSaveData(compound);
    this.addPersistentAngerSaveData(compound);
  }

  @Override
  public void readAdditionalSaveData(CompoundNBT compound) {
    super.readAdditionalSaveData(compound);
    this.readPersistentAngerSaveData((ServerWorld)this.level, compound);
  }
  
  public boolean isHoldingWeapon() {
    return this.getItemInHand(Hand.MAIN_HAND).getItem() == weapon;
  }

}
