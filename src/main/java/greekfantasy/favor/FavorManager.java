package greekfantasy.favor;

import java.util.Map;

import greekfantasy.tileentity.AltarTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FavorManager {

  
  /**
   * Called when an item is used on an altar
   * @param altar the altar
   * @param deity the deity of the altar
   * @param playerIn the player
   * @param favor the favor info (from capability)
   * @param item the item that was used
   * @return true if the item affected the player's favor
   */
  public static boolean onUseItem(final AltarTileEntity altar, final IDeity deity,
      final PlayerEntity playerIn, final FavorInfo favor, final ItemStack item) {
    final Map<Item, Integer> modifiers = deity.getItemFavorModifiers();
    if(modifiers.containsKey(item.getItem())) {
      favor.addFavor(modifiers.get(item.getItem()));
      if(!playerIn.isCreative()) {
        item.shrink(1);
      }
      return true;
    }
    return false;
  }
  
}
