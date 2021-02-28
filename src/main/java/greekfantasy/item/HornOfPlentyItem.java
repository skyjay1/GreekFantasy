package greekfantasy.item;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class HornOfPlentyItem extends Item {
  
  protected static final IOptionalNamedTag<Item> FOOD = ItemTags.createOptional(new ResourceLocation(GreekFantasy.MODID, "horn_of_plenty"));

  public HornOfPlentyItem(Properties properties) {
    super(properties);
  }
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    // spawn a food item at the player's position
    ItemStack food = new ItemStack(FOOD.getRandomElement(player.getRNG()), 2 + player.getRNG().nextInt(4));
    ItemEntity item = player.entityDropItem(food);
    if(item != null) {
        item.setNoPickupDelay();
    }
    // damage the item stack
    if(!player.isCreative()) {
      stack.damageItem(1, player, (entity) -> entity.sendBreakAnimation(hand));
    }
    // set cooldown
    player.getCooldownTracker().setCooldown(this, 8);
    return ActionResult.func_233538_a_(stack, world.isRemote());
  }
  
  @Override
  public int getItemEnchantability() { return 10; }

}
