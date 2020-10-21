package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.HealingSpellEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class HealingRodItem extends Item {

  public HealingRodItem(final Item.Properties properties) {
    super(properties);
  }
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    // prevent the item from being used up all the way
    if(stack.getMaxDamage() - stack.getDamage() <= 1) {
      return ActionResult.resultFail(stack);
    }
    player.getCooldownTracker().setCooldown(this, GreekFantasy.CONFIG.getHealingRodCooldown());
    // spawn a healing spell entity
    if(!world.isRemote()) {
      HealingSpellEntity healingSpell = HealingSpellEntity.create(world, player);
      world.addEntity(healingSpell);
    }
    
    // damage the item stack
    if(!player.isCreative()) {
      stack.damageItem(1, player, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
    }
    
    return ActionResult.func_233538_a_(stack, world.isRemote());
  }
  
  @Override
  public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    return toRepair.getItem() == this && toRepair.getDamage() < toRepair.getMaxDamage() && isRepairItem(repair);
  }
  
  private boolean isRepairItem(final ItemStack repair) {
    return repair.getItem() == GFRegistry.PURIFIED_SNAKESKIN;
  }
}
