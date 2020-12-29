package greekfantasy.favor;

import java.util.ArrayList;
import java.util.Collections;
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
      IDeity deity;
      FavorLevel info;
      ArrayList<Entry<ResourceLocation, FavorLevel>> entryList = Lists.newArrayList(favor.getAllFavor().entrySet());
      // order by which deity has the most favor
      Collections.sort(entryList, (e1, e2) -> e1.getValue().compareToAbs(e2.getValue()));
      for(final Entry<ResourceLocation, FavorLevel> entry : entryList) {
        deity = DeityManager.getDeity(entry.getKey());
        info = entry.getValue();
        if(favor.canUseEffect(info, time, player.getRNG())) {
          long cooldown = FavorEffectManager.onFavorEffect(player.getEntityWorld(), player, deity, favor, info);
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
      final long favorModifier = deity.getKillFavorModifier(entity.getType()) / 8;
      if(favorModifier != 0) {
        favor.getFavor(deity).addFavor(playerIn, deity, favorModifier);
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
      final long favorModifier = deity.getKillFavorModifier(entity.getType());
      if(favorModifier != 0) {
        favor.getFavor(deity).addFavor(playerIn, deity, favorModifier);
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
    final long favorModifier = deity.getItemFavorModifier(item.getItem());
    if(favorModifier != 0) {
      info.addFavor(playerIn, deity, favorModifier);
      if(!playerIn.isCreative()) {
        item.shrink(1);
      }
      return true;
    }
    return false;
  }
  
}
