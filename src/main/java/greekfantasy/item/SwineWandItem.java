package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.misc.SwineSpellEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SwineWandItem extends Item {

  public SwineWandItem(final Item.Properties properties) {
    super(properties);
  }
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    player.getCooldownTracker().setCooldown(this, GreekFantasy.CONFIG.getSwineWandCooldown());
    // spawn a healing spell entity
    if(!world.isRemote()) {
      SwineSpellEntity healingSpell = SwineSpellEntity.create(world, player);
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
    return repair.getItem() == GFRegistry.BOAR_EAR;
  }
}
