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

public abstract class EnchantedBowItem extends BowItem {

  public EnchantedBowItem(Properties builder) {
    super(builder);
  }


  /**
   * Called when the player stops using an Item (stops holding the right mouse button).
   */
  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    // Copied directly from BowItem with a few changes
    if (entityLiving instanceof PlayerEntity) {
      PlayerEntity playerentity = (PlayerEntity) entityLiving;
      boolean flag = playerentity.abilities.isCreativeMode
          || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
      ItemStack itemstack = playerentity.findAmmo(stack);

      int i = this.getUseDuration(stack) - timeLeft;
      i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, playerentity, i, !itemstack.isEmpty() || flag);
      if (i < 0)
        return;

      if (!itemstack.isEmpty() || flag) {
        if (itemstack.isEmpty()) {
          itemstack = new ItemStack(Items.ARROW);
        }

        float f = getArrowVelocity(i) * getArrowVelocityMultiplier();
        if (!((double) f < 0.1D)) {
          boolean flag1 = playerentity.abilities.isCreativeMode || (itemstack.getItem() instanceof ArrowItem
              && ((ArrowItem) itemstack.getItem()).isInfinite(itemstack, stack, playerentity));
          if (!worldIn.isRemote()) {
            // support multi-shot
            for(int arrows = 0, maxArrows = getArrowCount(stack); arrows < maxArrows; arrows++) {
              ArrowItem arrowitem = (ArrowItem) (itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
              AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(worldIn, itemstack, playerentity);
              abstractarrowentity = customArrow(abstractarrowentity);
              // params: thrower, rotationPitch, rotationYaw, ???, speed, inaccuracy
              abstractarrowentity.func_234612_a_(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0F, f * 3.0F,
                  1.0F + 10.0F * arrows);
              if (f == 1.0F) {
                abstractarrowentity.setIsCritical(true);
              }

              int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
              if (j > 0) {
                abstractarrowentity.setDamage(abstractarrowentity.getDamage() + (double) j * 0.5D + 0.5D);
              }

              int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
              if (k > 0) {
                abstractarrowentity.setKnockbackStrength(k);
              }

              if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                abstractarrowentity.setFire(100);
              }
              // set pickup status
              if (arrows > 0 || flag1 || playerentity.abilities.isCreativeMode
                  && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
                abstractarrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
              }
              // actually add the arrow entity
              worldIn.addEntity(abstractarrowentity);
            }
            // attempt to damage item
            stack.damageItem(1, playerentity, (e) -> {
              e.sendBreakAnimation(playerentity.getActiveHand());
            });
            
          }

          worldIn.playSound((PlayerEntity) null, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(),
              SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
          if (!flag1 && !playerentity.abilities.isCreativeMode) {
            itemstack.shrink(1);
            if (itemstack.isEmpty()) {
              playerentity.inventory.deleteStack(itemstack);
            }
          }

          playerentity.addStat(Stats.ITEM_USED.get(this));
        }
      }
    }
  }
  
  protected abstract void addBaseEnchantments(final ItemStack stack);
  
  protected abstract int getNumBaseEnchantments();
  
  protected abstract float getArrowVelocityMultiplier();
  
  protected int getArrowCount(final ItemStack stack) { return 1; }
  
  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    // add the item to the group with enchantment already applied
    if (this.isInGroup(group)) {
      ItemStack stack = new ItemStack(this);
      addBaseEnchantments(stack);
      items.add(stack);
    }
  }
  
  @Override
  public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
    addBaseEnchantments(stack);
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return stack.getEnchantmentTagList().size() > getNumBaseEnchantments();
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
      if(EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) < 1) {
        stack.addEnchantment(Enchantments.FLAME, 1);
      }
    }
    
    @Override
    protected int getNumBaseEnchantments() { return 1; }

    @Override
    protected float getArrowVelocityMultiplier() { return 1.0F; }
  }
  
  public static class ArtemisBowItem extends EnchantedBowItem {
    public ArtemisBowItem(Properties builder) {
      super(builder);
    }
    
    @Override
    protected void addBaseEnchantments(final ItemStack stack) {
      if(EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack) < 5) {
        stack.addEnchantment(Enchantments.POWER, 5);
      }
    }
    
    @Override
    protected int getNumBaseEnchantments() { return 1; }

    @Override
    protected float getArrowVelocityMultiplier() { return 1.5F; }
    
    @Override
    protected int getArrowCount(final ItemStack stack) { return 2; }
  }
  
  public static class ApolloBowItem extends EnchantedBowItem {
    public ApolloBowItem(Properties builder) {
      super(builder);
    }
    
    @Override
    protected void addBaseEnchantments(final ItemStack stack) {
      if(EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) < 1) {
        stack.addEnchantment(Enchantments.FLAME, 1);
      }
    }

    @Override
    protected int getNumBaseEnchantments() { return 1; }

    @Override
    protected float getArrowVelocityMultiplier() { return 1.5F; }
    
    @Override
    protected int getArrowCount(final ItemStack stack) { return 2; }
  }
}
