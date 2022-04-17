package greekfantasy.item;

import greekfantasy.entity.misc.WebBallEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import net.minecraft.item.Item.Properties;

public class WebBallItem extends Item {

  public WebBallItem(final Properties properties) {
    super(properties);
  }
  
  @Override
  public ActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getItemInHand(hand);
    player.getCooldowns().addCooldown(this, 10);
    // spawn a dragon tooth entity that will then spawn a Sparti
    if(!world.isClientSide()) {
      WebBallEntity webBall = WebBallEntity.create(world, player);
      // this method from ProjectileEntity does some math, then calls #shoot
      webBall.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 1.5F, 1.0F);
      world.addFreshEntity(webBall);
      // set the web type with hardcoded chances for web, spider, and item (config?)
      webBall.setWebType(random.nextFloat() < 0.35F, random.nextFloat() < 0.4F, random.nextFloat() < 0.6F);
    }
    
    // shrink the item stack
    if(!player.isCreative()) {
      stack.shrink(1);
    }
    
    return ActionResult.sidedSuccess(stack, world.isClientSide());
  }
}
