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

import net.minecraft.item.Item.Properties;

public class HornOfPlentyItem extends Item {
  
  protected static final IOptionalNamedTag<Item> FOOD = ItemTags.createOptional(new ResourceLocation(GreekFantasy.MODID, "horn_of_plenty"));

  public HornOfPlentyItem(Properties properties) {
    super(properties);
  }
  
  @Override
  public ActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getItemInHand(hand);
    // spawn a food item at the player's position
    ItemStack food = new ItemStack(FOOD.getRandomElement(player.getRandom()), 2 + player.getRandom().nextInt(4));
    ItemEntity item = player.spawnAtLocation(food);
    if(item != null) {
        item.setNoPickUpDelay();
    }
    // damage the item stack
    if(!player.isCreative()) {
      stack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(hand));
    }
    // set cooldown
    player.getCooldowns().addCooldown(this, 8);
    return ActionResult.sidedSuccess(stack, world.isClientSide());
  }
  
  @Override
  public int getEnchantmentValue() { return 10; }

}
