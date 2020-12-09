package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BagOfWindItem extends Item {

  public BagOfWindItem(final Item.Properties properties) {
    super(properties);
  }
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    // prevent the item from being used up all the way
    if(stack.getMaxDamage() - stack.getDamage() <= 1) {
      return ActionResult.resultFail(stack);
    }
    // give player potion effect
    player.addPotionEffect(new EffectInstance(Effects.SPEED, GreekFantasy.CONFIG.getBagOfWindDuration(), 1));
    player.addPotionEffect(new EffectInstance(Effects.DOLPHINS_GRACE, GreekFantasy.CONFIG.getBagOfWindDuration(), 0));
    player.getCooldownTracker().setCooldown(this, GreekFantasy.CONFIG.getBagOfWindCooldown());
    if(!player.isCreative()) {
      stack.damageItem(1, player, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
    }
    return ActionResult.func_233538_a_(stack, world.isRemote());
  }
  
  @Override
  public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    return toRepair.getItem() == this && toRepair.getDamage() < toRepair.getMaxDamage() && repair.getItem() == GFRegistry.MAGIC_FEATHER;
  }
}
