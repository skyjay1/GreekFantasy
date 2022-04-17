package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.misc.HealingSpellEntity;
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
  public ActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getItemInHand(hand);
    // prevent the item from being used up all the way
    if(stack.getMaxDamage() - stack.getDamageValue() <= 1) {
      return ActionResult.fail(stack);
    }
    player.getCooldowns().addCooldown(this, GreekFantasy.CONFIG.getHealingRodCooldown());
    // spawn a healing spell entity
    if(!world.isClientSide()) {
      HealingSpellEntity healingSpell = HealingSpellEntity.create(world, player);
      world.addFreshEntity(healingSpell);
    }
    
    // damage the item stack
    if(!player.isCreative()) {
      stack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
    }
    
    return ActionResult.sidedSuccess(stack, world.isClientSide());
  }
  
  @Override
  public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
    return toRepair.getItem() == this && toRepair.getDamageValue() < toRepair.getMaxDamage() && isRepairItem(repair);
  }
  
  private boolean isRepairItem(final ItemStack repair) {
    return repair.getItem() == GFRegistry.PURIFIED_SNAKESKIN;
  }
}
