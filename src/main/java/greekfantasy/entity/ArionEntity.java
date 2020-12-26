package greekfantasy.entity;

import java.util.UUID;

import greekfantasy.GreekFantasy;
import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class ArionEntity extends AbstractChestedHorseEntity {
  
  protected static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
  protected static final String KEY_ARMOR = "ArmorItem";
  
  protected static final IOptionalNamedTag<Item> FOOD = ItemTags.createOptional(new ResourceLocation(GreekFantasy.MODID, "arion_food"));
  
  public ArionEntity(EntityType<? extends ArionEntity> type, World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return AbstractChestedHorseEntity.func_234234_eJ_();
  }
  
  @Override
  public void registerGoals() {
    super.registerGoals();
  }
  
  // CALLED FROM ON INITIAL SPAWN //
  
  @Override
  protected void func_230273_eI_() {
    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)this.getModifiedMaxHealth());
    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getModifiedMovementSpeed());
    this.getAttribute(Attributes.HORSE_JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
  }
  
  @Override
  protected float getModifiedMaxHealth() {
    return super.getModifiedMaxHealth() + 30.0F;
  }

  @Override
  protected double getModifiedJumpStrength() {
    return super.getModifiedJumpStrength() + 0.25F;
  }

  @Override
  protected double getModifiedMovementSpeed() {
    return super.getModifiedMovementSpeed() + 0.21F;
  }
  
  // INVENTORY //
  
  @Override
  protected void func_230275_fc_() {
    super.func_230275_fc_();
    if (!this.world.isRemote()) {
      super.func_230275_fc_();
      this.initArmor(this.horseChest.getStackInSlot(1));
   }
  }
 
  private void initArmor(ItemStack p_213804_1_) {
    if (!this.world.isRemote()) {
       this.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
       if (this.isArmor(p_213804_1_)) {
          int i = ((HorseArmorItem)p_213804_1_.getItem()).getArmorValue();
          if (i != 0) {
             this.getAttribute(Attributes.ARMOR).applyNonPersistentModifier(new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)i, AttributeModifier.Operation.ADDITION));
          }
       }
    }

 }

  @Override
  public void onInventoryChanged(IInventory invBasic) {
    ItemStack itemstack = this.getChestItem();
    super.onInventoryChanged(invBasic);
    ItemStack itemstack1 = this.getChestItem();
    if (this.ticksExisted > 20 && this.isArmor(itemstack1) && itemstack != itemstack1) {
      this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
    }

  }

  public ItemStack getChestItem() {
    return this.getItemStackFromSlot(EquipmentSlotType.CHEST);
  }
  
  // SOUNDS //
  
  @Override
  protected void playGallopSound(SoundType sound) {
    super.playGallopSound(sound);
    if (this.rand.nextInt(10) == 0) {
      this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, sound.getVolume() * 0.6F, sound.getPitch());
    }

    ItemStack stack = this.horseChest.getStackInSlot(1);
    if (isArmor(stack))
      stack.onHorseArmorTick(world, this);
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
  
  // MISC //
  
  @Override
  public double getMountedYOffset() { return super.getMountedYOffset() - 0.25D; }
  
  @Override
  protected boolean handleEating(final PlayerEntity player, final ItemStack stack) {
    if (stack.getItem().isIn(FOOD)) {
      return super.handleEating(player, stack);
    }
    return false;
  }
  
  @Override
  public boolean isBreedingItem(ItemStack stack) {
    return stack.getItem().isIn(FOOD);
  }

  @Override
  public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
    // Most of this is copied from HorseEntity
    ItemStack itemstack = player.getHeldItem(hand);
    if (!this.isChild()) {
      if (this.isTame() && player.isSecondaryUseActive()) {
        this.openGUI(player);
        return ActionResultType.func_233537_a_(this.world.isRemote);
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
        return ActionResultType.func_233537_a_(this.world.isRemote);
      }

      boolean flag = !this.isChild() && !this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE;
      if (this.isArmor(itemstack) || flag) {
        this.openGUI(player);
        return ActionResultType.func_233537_a_(this.world.isRemote);
      }
    }

    // Only mount if already tame
    if (this.isTame() && !this.isChild()) {
      this.mountTo(player);
      return ActionResultType.func_233537_a_(this.world.isRemote);
    }
    // DO NOT CALL SUPER METHOD
    // return super.func_230254_b_(player, hand);
    return ActionResultType.PASS;
  }

  // NBT //
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    if (!this.horseChest.getStackInSlot(1).isEmpty()) {
      compound.put(KEY_ARMOR, this.horseChest.getStackInSlot(1).write(new CompoundNBT()));
    }
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
     super.readAdditional(compound);
     if (compound.contains(KEY_ARMOR, 10)) {
        ItemStack itemstack = ItemStack.read(compound.getCompound(KEY_ARMOR));
        if (!itemstack.isEmpty() && this.isArmor(itemstack)) {
           this.horseChest.setInventorySlotContents(1, itemstack);
        }
     }
     this.func_230275_fc_();
  }


  @Override
  public boolean isArmor(ItemStack stack) {
    return stack.getItem() instanceof HorseArmorItem;
  }
  
  @Override
  public int getMaxTemper() {
    return 200;
  }

  @Override
  public boolean canMateWith(final AnimalEntity otherAnimal) {
    if (otherAnimal == this) {
      return false;
    } else {
      return otherAnimal instanceof ArionEntity && this.canMate() && ((ArionEntity)otherAnimal).canMate();
    }
  }

}
