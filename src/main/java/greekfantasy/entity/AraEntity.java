package greekfantasy.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.entity.ai.FollowGoal;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.MobEntity;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
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
  
  private static final RangedInteger ANGER_RANGE = TickRangeConverter.convertRange(20, 39);
  private int angerTime;
  private UUID angerTarget;
  
  private Item weapon;
  
  public AraEntity(final EntityType<? extends AraEntity> type, final World worldIn) {
    super(type, worldIn);
    weapon = GFRegistry.FLINT_KNIFE;
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(3, new FollowGoal(this, 1.0D, 6.0F, 12.0F) {
      @Override
      public boolean shouldExecute() { return entity.getRNG().nextInt(80) == 0 && super.shouldExecute(); }
    });
    this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setCallsForHelp());
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
    this.targetSelector.addGoal(4, new ResetAngerGoal<>(this, true));  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    // anger timer
    if (!this.world.isRemote()) {
      this.func_241359_a_((ServerWorld) this.world, true);
    }
    // when aggressive, equip a weapon
    if(this.isAggressive()) {
      if(this.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
        this.setHeldItem(Hand.MAIN_HAND, new ItemStack(weapon));
        this.getEntityWorld().playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
      }
    } else if(this.getAttackTarget() == null && this.getHeldItem(Hand.MAIN_HAND).getItem() == weapon) {
      this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
    }
  }
  
  // IAngerable methods
  
  @Override
  public void func_230258_H__() { this.setAngerTime(ANGER_RANGE.getRandomWithinRange(this.rand)); }
  @Override
  public void setAngerTime(int time) { this.angerTime = time; }
  @Override
  public int getAngerTime() { return this.angerTime; }
  @Override
  public void setAngerTarget(@Nullable UUID target) { this.angerTarget = target; }
  @Override
  public UUID getAngerTarget() { return this.angerTarget; }
 
  // End IAngerable methods

  @Override
  public boolean canDespawn(double distanceToClosestPlayer) {
    return this.ticksExisted > 2400;
  }
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    this.writeAngerNBT(compound);
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.readAngerNBT((ServerWorld)this.world, compound);
  }
  
  public boolean isHoldingWeapon() {
    return this.getHeldItem(Hand.MAIN_HAND).getItem() == weapon;
  }

}
