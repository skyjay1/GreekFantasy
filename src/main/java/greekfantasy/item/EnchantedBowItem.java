package greekfantasy.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import net.minecraft.item.Item.Properties;

public abstract class EnchantedBowItem extends BowItem {

  public EnchantedBowItem(Properties builder) {
    super(builder);
  }

  /**
   * Called when the player stops using an Item (stops holding the right mouse button).
   */
  @Override
  public void releaseUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    // Copied directly from BowItem with a few changes
    if (entityLiving instanceof PlayerEntity) {
      PlayerEntity playerentity = (PlayerEntity) entityLiving;
      boolean flag = playerentity.abilities.instabuild
          || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
      ItemStack itemstack = playerentity.getProjectile(stack);

      int i = this.getUseDuration(stack) - timeLeft;
      i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, playerentity, i, !itemstack.isEmpty() || flag);
      if (i < 0)
        return;

      if (!itemstack.isEmpty() || flag) {
        if (itemstack.isEmpty()) {
          itemstack = new ItemStack(Items.ARROW);
        }

        float f = getPowerForTime(i) * getArrowVelocityMultiplier();
        if (!((double) f < 0.1D)) {
          boolean flag1 = playerentity.abilities.instabuild || (itemstack.getItem() instanceof ArrowItem
              && ((ArrowItem) itemstack.getItem()).isInfinite(itemstack, stack, playerentity));
          if (!worldIn.isClientSide()) {
            // support multi-shot
            for(int arrows = 0, maxArrows = getArrowCount(stack); arrows < maxArrows; arrows++) {
              ArrowItem arrowitem = (ArrowItem) (itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
              AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(worldIn, itemstack, playerentity);
              abstractarrowentity = customArrow(abstractarrowentity);
              // params: thrower, rotationPitch, rotationYaw, ???, speed, inaccuracy
              abstractarrowentity.shootFromRotation(playerentity, playerentity.xRot, playerentity.yRot, 0.0F, f * 3.0F,
                  (arrows > 0 ? 8.0F : 1.0F));
              if (f == 1.0F) {
                abstractarrowentity.setCritArrow(true);
              }

              int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
              if (j > 0) {
                abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() + (double) j * 0.5D + 0.5D);
              }

              int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
              if (k > 0) {
                abstractarrowentity.setKnockback(k);
              }

              if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                abstractarrowentity.setSecondsOnFire(100);
              }
              // set pickup status
              if (arrows > 0 || flag1 || playerentity.abilities.instabuild
                  && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
                abstractarrowentity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
              }
              // actually add the arrow entity
              worldIn.addFreshEntity(abstractarrowentity);
            }
            // attempt to damage item
            stack.hurtAndBreak(1, playerentity, (e) -> {
              e.broadcastBreakEvent(playerentity.getUsedItemHand());
            });
            
          }

          worldIn.playSound((PlayerEntity) null, playerentity.getX(), playerentity.getY(), playerentity.getZ(),
              SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
          if (!flag1 && !playerentity.abilities.instabuild) {
            itemstack.shrink(1);
            if (itemstack.isEmpty()) {
              playerentity.inventory.removeItem(itemstack);
            }
          }

          playerentity.awardStat(Stats.ITEM_USED.get(this));
        }
      }
    }
  }
  
  protected abstract void addBaseEnchantments(final ItemStack stack);
  
  protected abstract int getNumBaseEnchantments();
  
  protected abstract float getArrowVelocityMultiplier();
  
  protected int getArrowCount(final ItemStack stack) { return 1; }
  
  @Override
  public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
    // add the item to the group with enchantment already applied
    if (this.allowdedIn(group)) {
      ItemStack stack = new ItemStack(this);
      addBaseEnchantments(stack);
      items.add(stack);
    }
  }
  
  @Override
  public void onCraftedBy(ItemStack stack, World worldIn, PlayerEntity playerIn) {
    addBaseEnchantments(stack);
  }

  @Override
  public boolean isFoil(ItemStack stack) {
    return stack.getEnchantmentTags().size() > getNumBaseEnchantments();
  }

  @Override
  public void inventoryTick(final ItemStack stack, final World worldIn, final Entity entityIn, 
      final int itemSlot, final boolean isSelected) {
    addBaseEnchantments(stack);
  }
  
  public static class CursedBowItem extends EnchantedBowItem {
    public CursedBowItem(Properties builder) {
      super(builder);
    }
    
    @Override
    protected void addBaseEnchantments(final ItemStack stack) {
      if(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) < 1) {
        stack.enchant(Enchantments.FLAMING_ARROWS, 1);
      }
      if(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, stack) < 1) {
        stack.enchant(Enchantments.VANISHING_CURSE, 1);
      }
    }
    
    @Override
    protected int getNumBaseEnchantments() { return 2; }

    @Override
    protected float getArrowVelocityMultiplier() { return 1.0F; }
  }
  
  public static class ArtemisBowItem extends EnchantedBowItem {
    public ArtemisBowItem(Properties builder) {
      super(builder);
    }
    
    @Override
    protected void addBaseEnchantments(final ItemStack stack) {
      if(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack) < 5) {
        stack.enchant(Enchantments.POWER_ARROWS, 5);
      }
    }
    
    @Override
    protected int getNumBaseEnchantments() { return 1; }

    @Override
    protected float getArrowVelocityMultiplier() { return 1.25F; }
    
    @Override
    protected int getArrowCount(final ItemStack stack) { return 2; }
    
    @Override
    public AbstractArrowEntity customArrow(AbstractArrowEntity arrow) {
      arrow.setBaseDamage(arrow.getBaseDamage() * 1.25D);
      return arrow;
    }
  }
  
  public static class ApolloBowItem extends EnchantedBowItem {
    public ApolloBowItem(Properties builder) {
      super(builder);
    }
    
    @Override
    protected void addBaseEnchantments(final ItemStack stack) {
      if(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) < 1) {
        stack.enchant(Enchantments.FLAMING_ARROWS, 1);
      }
    }

    @Override
    protected int getNumBaseEnchantments() { return 1; }

    @Override
    protected float getArrowVelocityMultiplier() { return 1.5F; }
    
    @Override
    protected int getArrowCount(final ItemStack stack) { return 2; }
    
    @Override
    public AbstractArrowEntity customArrow(AbstractArrowEntity arrow) {
      arrow.setBaseDamage(arrow.getBaseDamage() * 1.75D);
      return arrow;
    }
  }
}
