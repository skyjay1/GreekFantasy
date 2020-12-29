package greekfantasy.favor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import greekfantasy.GreekFantasy;
import greekfantasy.tileentity.AltarTileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FavorManager {
  
  public static boolean onPlayerTick(final PlayerEntity player, final IFavor favor) {
    if(player.ticksExisted > 100 && player.ticksExisted % 50 == 0) {
      final long time = player.getEntityWorld().getGameTime();
      FavorLevel info;
      ArrayList<Entry<IDeity, FavorLevel>> entryList = Lists.newArrayList(favor.getAllFavor().entrySet());
      // order by which deity has the most favor
      Collections.sort(entryList, (e1, e2) -> e1.getValue().compareToAbs(e2.getValue()));
      for(final Entry<IDeity, FavorLevel> entry : entryList) {
        info = entry.getValue();
        if(favor.canUseEffect(info, time, player.getRNG())) {
          long cooldown = FavorEffectManager.onFavorEffect(player.getEntityWorld(), player, entry.getKey(), favor, info);
          if(cooldown <= 0) {
            cooldown = 1000;
          }
          favor.setEffectTimestamp(time);
          favor.setEffectCooldown(cooldown);
          return true;
        }
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
        favor.getFavor(deity).addFavor(modifiers.get(id).intValue() / 10);
        flag = true;
      }
    }
    GreekFantasy.LOGGER.debug("onAttackEntity: " + flag);
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
      if(deity.hasKillFavorModifier(entity.getType())) {
        favor.getFavor(deity).addFavor(deity.getKillFavorModifier(entity.getType()));
        flag = true;
      }
    }
    GreekFantasy.LOGGER.debug("onKillEntity: " + flag);
    return flag;
  }

  /**
   * Called when an item is used on an altar
   * @param altar the altar
   * @param deity the deity of the altar
   * @param playerIn the player
   * @param info the favor info (from capability)
   * @param item the item that was used
   * @return true if the item affected the player's favor
   */
  public static boolean onGiveItem(final AltarTileEntity altar, final IDeity deity,
      final PlayerEntity playerIn, final FavorLevel info, final ItemStack item) {
    if(deity.hasItemFavorModifier(item.getItem())) {
      info.addFavor(deity.getItemFavorModifier(item.getItem()));
      if(!playerIn.isCreative()) {
        item.shrink(1);
      }
      GreekFantasy.LOGGER.debug("onGiveItem: true");
      return true;
    }
    GreekFantasy.LOGGER.debug("onGiveItem: false");
    return false;
  }
  
}
