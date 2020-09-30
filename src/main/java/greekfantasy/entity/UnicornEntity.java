package greekfantasy.entity;

import greekfantasy.GFRegistry;
import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;

public class UnicornEntity extends AbstractHorseEntity {
  
  public UnicornEntity(EntityType<? extends UnicornEntity> type, World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return AbstractHorseEntity.func_234237_fg_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 64.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D);
  }
  
  @Override
  public void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, PlayerEntity.class, 16.0F, 1.2D, 1.0D, (entity) -> {
      return !entity.isDiscrete() && EntityPredicates.CAN_AI_TARGET.test(entity) && 
          (!this.isTame() || this.getOwnerUniqueId() == null || !entity.getUniqueID().equals(this.getOwnerUniqueId()));
   }));
  }
  
  // CALLED FROM ON INITIAL SPAWN
  @Override
  protected void func_230273_eI_() {
  }
  
  @Override
  protected void damageEntity(final DamageSource source, final float amountIn) {
    super.damageEntity(source, amountIn * 0.5F);
  }

  @Override
  public boolean isPotionApplicable(EffectInstance potioneffectIn) {
    if (potioneffectIn.getPotion() == Effects.POISON) {
      PotionEvent.PotionApplicableEvent event = new PotionEvent.PotionApplicableEvent(this, potioneffectIn);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getResult() == Event.Result.ALLOW;
    }
    return super.isPotionApplicable(potioneffectIn);
  }
  
  @Override
  protected void playGallopSound(SoundType sound) {
    super.playGallopSound(sound);
    if (this.rand.nextInt(10) == 0) {
       this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, sound.getVolume() * 0.6F, sound.getPitch());
    }

    ItemStack stack = this.horseChest.getStackInSlot(1);
    if (isArmor(stack)) stack.onHorseArmorTick(world, this);
 }
  
  @Override
  protected SoundEvent getAmbientSound() {
    super.getAmbientSound();
    return SoundEvents.ENTITY_HORSE_AMBIENT;
  }

  @Override
  protected SoundEvent getDeathSound() {
    super.getDeathSound();
    return SoundEvents.ENTITY_HORSE_DEATH;
  }

  @Override
  protected SoundEvent func_230274_fe_() {
    return SoundEvents.ENTITY_HORSE_EAT;
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
    super.getHurtSound(damageSourceIn);
    return SoundEvents.ENTITY_HORSE_HURT;
  }

  @Override
  protected SoundEvent getAngrySound() {
    super.getAngrySound();
    return SoundEvents.ENTITY_HORSE_ANGRY;
  }

  @Override
  public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
    ItemStack itemstack = player.getHeldItem(hand);
    if (!this.isChild()) {
      if (this.isTame() && player.isSecondaryUseActive()) {
        this.openGUI(player);
        return ActionResultType.func_233537_a_(this.world.isRemote());
      }

      if (this.isBeingRidden()) {
        return super.func_230254_b_(player, hand);
      }
    }

    if (!itemstack.isEmpty()) {
      if (this.isBreedingItem(itemstack)) {
        return this.func_241395_b_(player, itemstack);
      }

      ActionResultType actionresulttype = itemstack.interactWithEntity(player, this, hand);
      if (actionresulttype.isSuccessOrConsume()) {
        return actionresulttype;
      }

      if (!this.isTame()) {
        this.makeMad();
        return ActionResultType.func_233537_a_(this.world.isRemote());
      }

      boolean flag = !this.isChild() && !this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE;
      if (this.isArmor(itemstack) || flag) {
        this.openGUI(player);
        return ActionResultType.func_233537_a_(this.world.isRemote());
      }
    }

    if (this.isChild()) {
      return super.func_230254_b_(player, hand);
    } else if(this.isTame() || itemstack.getItem() == GFRegistry.GOLDEN_BRIDLE) {
      this.mountTo(player);
    }
    return ActionResultType.func_233537_a_(this.world.isRemote());
  }

//  @Override
//  public boolean isArmor(ItemStack stack) {
//    return stack.getItem() instanceof HorseArmorItem;
//  }

  @Override
  public boolean canMateWith(final AnimalEntity otherAnimal) {
    if (otherAnimal == this) {
      return false;
    } else {
      return otherAnimal instanceof UnicornEntity && this.canMate() && ((UnicornEntity)otherAnimal).canMate();
    }
  }

}
