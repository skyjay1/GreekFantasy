package greekfantasy.item;

import greekfantasy.entity.misc.WebBallEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WebBallItem extends Item {

  public WebBallItem(final Properties properties) {
    super(properties);
  }
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    player.getCooldownTracker().setCooldown(this, 10);
    // spawn a dragon tooth entity that will then spawn a Sparti
    if(!world.isRemote()) {
      WebBallEntity webBall = WebBallEntity.create(world, player);
      // this method from ProjectileEntity does some math, then calls #shoot
      webBall.setDirectionAndMovement(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
      world.addEntity(webBall);
      // set the web type with hardcoded chances for web, spider, and item (config?)
      webBall.setWebType(random.nextFloat() < 0.35F, random.nextFloat() < 0.4F, random.nextFloat() < 0.6F);
    }
    
    // shrink the item stack
    if(!player.isCreative()) {
      stack.shrink(1);
    }
    
    return ActionResult.func_233538_a_(stack, world.isRemote());
  }
}
