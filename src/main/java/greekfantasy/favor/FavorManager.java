package greekfantasy.favor;

import java.util.Map;
import java.util.Map.Entry;

import greekfantasy.tileentity.AltarTileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FavorManager {
  
  public static final IFavorEffect NONE = (p, f) -> 1000;
  
  public static boolean onPlayerTick(final PlayerEntity player, final IFavor favor) {
    final long time = player.getServer().getServerTime();
    for(final Entry<IDeity, FavorInfo> entry : favor.getAllFavor().entrySet()) {
      final FavorInfo info = entry.getValue();
      if(info.canExecute(time)) {
        final long cooldown = entry.getKey().getRandomEffect(info.getFavor() > 0, player.getRNG()).performEffect(player, info);
        info.setEffectTimestamp(time);
        info.setEffectCooldown(cooldown);
      }
    }
    
    return false;
  }
  
  /**
   * Called when a player attacks an entity
   * @param entity the entity that was attacked
   * @param playerIn the player
   * @param favor the player's favor (from capability)
   * @return true if the attack affected the player's favor
   */
  public static boolean onAttackEntity(final LivingEntity entity, final PlayerEntity playerIn, final IFavor favor) {
    boolean flag = false;
    for(final IDeity deity : DeityManager.getDeityCollection()) {
      final Map<ResourceLocation, Integer> modifiers = deity.getKillFavorModifiers();
      final ResourceLocation id = entity.getType().getRegistryName();
      if(modifiers.containsKey(id)) {
        favor.getOrCreateFavor(deity).addFavor(modifiers.get(id).intValue() / 10);
        flag = true;
      }
    }
    return flag;
  }
  
  /**
   * Called when a player kills an entity
   * @param entity the entity that was killed
   * @param playerIn the player
   * @param favor the player's favor (from capability)
   * @return true if the kill affected the player's favor
   */
  public static boolean onKillEntity(final LivingEntity entity, final PlayerEntity playerIn, final IFavor favor) {
    boolean flag = false;
    for(final IDeity deity : DeityManager.getDeityCollection()) {
      final Map<ResourceLocation, Integer> modifiers = deity.getKillFavorModifiers();
      final ResourceLocation id = entity.getType().getRegistryName();
      if(modifiers.containsKey(id)) {
        favor.getOrCreateFavor(deity).addFavor(modifiers.get(id));
        flag = true;
      }
    }
    return flag;
  }

  /**
   * Called when an item is used on an altar
   * @param altar the altar
   * @param deity the deity of the altar
   * @param playerIn the player
   * @param favor the favor info (from capability)
   * @param item the item that was used
   * @return true if the item affected the player's favor
   */
  public static boolean onGiveItem(final AltarTileEntity altar, final IDeity deity,
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
