package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.entity.passive.horse.CoatTypes;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class ArionEntity extends HorseEntity {
    
  protected static final IOptionalNamedTag<Item> FOOD = ItemTags.createOptional(new ResourceLocation(GreekFantasy.MODID, "arion_food"));

  public ArionEntity(EntityType<? extends ArionEntity> type, World worldIn) {
     super(type, worldIn);
     this.stepHeight = 1.5F;
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return AbstractHorseEntity.func_234237_fg_().createMutableAttribute(Attributes.ARMOR, 2);
  }
  
  @Override
  public void registerGoals() {
    super.registerGoals();
  }
  
  public static ArionEntity spawnArion(final ServerWorld world, final PlayerEntity player, final HorseEntity horse) {
    ArionEntity entity = GFRegistry.ARION_ENTITY.create(world);
    entity.copyLocationAndAnglesFrom(horse);
    entity.onInitialSpawn(world, world.getDifficultyForLocation(horse.getPosition()), SpawnReason.CONVERSION, (ILivingEntityData)null, (CompoundNBT)null);
    if(horse.hasCustomName()) {
      entity.setCustomName(horse.getCustomName());
      entity.setCustomNameVisible(horse.isCustomNameVisible());
    }
    entity.setTamedBy(player);
    entity.enablePersistence();
    entity.renderYawOffset = horse.renderYawOffset;
    entity.func_242279_ag(); // setPortalCooldown
    entity.setGrowingAge(horse.getGrowingAge());
    world.addEntity(entity);
    // drop the old horse items
    if (horse.horseChest != null) {
      for (int i = 0; i < horse.horseChest.getSizeInventory(); ++i) {
        ItemStack itemstack = horse.horseChest.getStackInSlot(i);
        if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
          horse.entityDropItem(itemstack);
        }
      }
    }
    // remove the old horse
    horse.remove();
    // play sound
    world.playSound(entity.getPosX(), entity.getPosY(), entity.getPosZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, entity.getSoundCategory(), 1.0F, 1.0F, false);
    return entity;
  }

  @Override
  public boolean canMateWith(AnimalEntity otherAnimal) {
     return false;
  }

  @Override
  public boolean func_230276_fq_() {
     return true;
  }

  @Override
  public CoatColors func_234239_eK_() {
    return CoatColors.BLACK;
  }

  @Override
  public CoatTypes func_234240_eM_() {
    return CoatTypes.NONE;
  }

  @Override
  public int getMaxTemper() {
    return 200;
  }
  
  // CALLED FROM ON INITIAL SPAWN //
  
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
  
  // MISC //
  
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
}
