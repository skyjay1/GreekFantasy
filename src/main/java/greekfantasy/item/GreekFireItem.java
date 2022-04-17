package greekfantasy.item;

import greekfantasy.entity.misc.GreekFireEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import net.minecraft.item.Item.Properties;

public class GreekFireItem extends Item {

  public GreekFireItem(final Properties properties) {
    super(properties);
  }
  
  @Override
  public ActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getItemInHand(hand);
    player.getCooldowns().addCooldown(this, 20);
    // spawn a Greek Fire entity that will cause an explosion
    if(!world.isClientSide()) {
      GreekFireEntity dragonTooth = GreekFireEntity.create(world, player);
      // this unmapped method from ProjectileEntity does some math, then calls #shoot
      dragonTooth.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 1.5F, 1.0F);
      world.addFreshEntity(dragonTooth);
    }
    
    // shrink the item stack
    if(!player.isCreative()) {
      stack.shrink(1);
    }
    
    return ActionResult.sidedSuccess(stack, world.isClientSide());
  }
}
