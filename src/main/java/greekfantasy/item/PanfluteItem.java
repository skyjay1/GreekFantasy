package greekfantasy.item;

import greekfantasy.GreekFantasy;
import greekfantasy.client.gui.GuiLoader;
import greekfantasy.util.PanfluteMusicManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PanfluteItem extends Item {
  
  public static final ResourceLocation DEFAULT_SONG = new ResourceLocation(GreekFantasy.MODID, "greensleeves");
  public static final String KEY_SONG = "Song";


  public PanfluteItem(final Properties properties) {
    super(properties);
  }
  
  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    // add the item to the group with enchantment already applied
    if (this.isInGroup(group)) {
      final ItemStack stack = new ItemStack(this);
      stack.getOrCreateTag().putString(KEY_SONG, DEFAULT_SONG.toString());
      items.add(stack);
    }
  }

  @Override
  public int getUseDuration(final ItemStack stack) {
    return 72000;
  }
  
  @Override
  public UseAction getUseAction(final ItemStack stack) {
    return UseAction.CROSSBOW;
  }

  @Override
  public void onUsingTick(final ItemStack stack, final LivingEntity player, final int count) {
    // note: count starts at #getUseDuration and decreases to zero
    final String songKey = stack.getOrCreateTag().getString(KEY_SONG);
    final ResourceLocation songID = songKey.isEmpty() ? DEFAULT_SONG : new ResourceLocation(songKey);
    PanfluteMusicManager.playMusic(player, songID, getUseDuration(stack) - count, 1.0F, 0.5F);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemstack = playerIn.getHeldItem(Hand.MAIN_HAND);
    // Check if player is sneaking - if so, open song selection GUI instead
    if(itemstack.getItem() == this && playerIn.isSneaking() && worldIn.isRemote()) {
      GuiLoader.openPanfluteGui(playerIn, playerIn.inventory.currentItem, itemstack);
      return ActionResult.resultPass(itemstack);
    }
    playerIn.setActiveHand(handIn);
    return ActionResult.resultConsume(itemstack);
  }
}
