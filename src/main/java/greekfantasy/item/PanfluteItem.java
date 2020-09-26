package greekfantasy.item;

import greekfantasy.GreekFantasy;
import greekfantasy.util.PanfluteMusicManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PanfluteItem extends Item {
  
  // TODO: GUI to allow player to play different songs
  private static final ResourceLocation SONG = new ResourceLocation(GreekFantasy.MODID, "greensleeves");


  public PanfluteItem(final Properties properties) {
    super(properties);
  }

  @Override
  public int getUseDuration(final ItemStack stack) {
    return 72000;
  }
  
  @Override
  public UseAction getUseAction(final ItemStack stack) {
    return UseAction.BOW;
  }

  @Override
  public void onUsingTick(final ItemStack stack, final LivingEntity player, final int count) {
    // note: count starts at #getUseDuration and decreases to zero
    PanfluteMusicManager.playMusic(player, SONG, getUseDuration(stack) - count, 1.0F, 0.5F);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemstack = playerIn.getHeldItem(handIn);
    // TODO check if player is sneaking - if so, open song selection GUI instead
    playerIn.setActiveHand(handIn);
    return ActionResult.resultConsume(itemstack);
  }
}
