package greekfantasy.favor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.common.collect.Lists;

import greekfantasy.GreekFantasy;
import greekfantasy.events.FavorChangedEvent.Source;
import greekfantasy.tileentity.StatueTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FavorManager {
  
  /**
   * Called when a living entity kills the player
   * @param player the player
   * @param source the true entity that killed the player
   * @param favor the player's favor capability
   */
  public static void onPlayerKilled(final PlayerEntity player, final Entity source, final IFavor favor) {
    // attempt to trigger ENTITY_KILLED_PLAYER favor effect
    triggerFavorEffect(FavorEffectTrigger.Type.ENTITY_KILLED_PLAYER, source.getType().getRegistryName(), player, favor);
  }
  
  /**
   * Called when a living entity hurts the player
   * @param player the player
   * @param source the immediate entity that hurt the player
   * @param favor the player's favor capability
   */
  public static void onPlayerHurt(final PlayerEntity player, final Entity source, final IFavor favor) {
    // attempt to trigger ENTITY_HURT_PLAYER favor effect
    triggerFavorEffect(FavorEffectTrigger.Type.ENTITY_HURT_PLAYER, source.getType().getRegistryName(), player, favor);
  }
  
  /**
   * Called when a player breaks a block
   * @param player the player
   * @param block the block that was broken
   * @param favor the player's favor capability
   */
  public static void onBreakBlock(final PlayerEntity player, final Block block, final IFavor favor) {
    // attempt to trigger PLAYER_BREAK_BLOCK favor effect
    triggerFavorEffect(FavorEffectTrigger.Type.PLAYER_BREAK_BLOCK, block.getRegistryName(), player, favor);
  }
  
  /**
   * Called every server-side player tick
   * @param player the player
   * @param favor the player's favor capability
   */
  public static void onPlayerTick(final PlayerEntity player, final IFavor favor) {
    final long time = player.getEntityWorld().getGameTime() + player.getEntityId() * 3;
    // decrease all favor 
    if(player.ticksExisted > 10 && GreekFantasy.CONFIG.doesFavorDecrease() && time % GreekFantasy.CONFIG.getFavorDecreaseInterval() == 0) {
      favor.forEach((d, f) -> f.depleteFavor(player, d, 1, Source.PASSIVE));
    }
    // attempt to perform a favor effect
    if(!player.isCreative() && !player.isSpectator() && player.ticksExisted > 10 && time % 100 == 0) {
      Optional<Deity> deity;
      FavorLevel info;
      ArrayList<Entry<ResourceLocation, FavorLevel>> entryList = Lists.newArrayList(favor.getAllFavor().entrySet());
      // order by which deity has the most favor
      Collections.sort(entryList, (e1, e2) -> e1.getValue().compareToAbs(e2.getValue()));
      // loop through each deity until one of them can perform an effect
      for(final Entry<ResourceLocation, FavorLevel> entry : entryList) {
        deity = GreekFantasy.PROXY.DEITY.get(entry.getKey());
        info = entry.getValue();
        if(deity.isPresent() && favor.canUseEffect(info, time, player.getRNG())) {
          // perform an effect, set the timestamp and cooldown, and exit the loop
          long cooldown = FavorEffectManager.onFavorEffect(player.getEntityWorld(), player, deity.get(), favor, info);
          if(cooldown <= 0) {
            cooldown = 200;
          }
          favor.setEffectTimestamp(time);
          favor.setEffectCooldown(cooldown);
          return;
        }
      }
    }
    return;
  }
  
  /**
   * Called when a player attacks an entity
   * @param entity the entity that was attacked
   * @param playerIn the player
   * @param favor the player's favor (from capability)
   */
  public static void onAttackEntity(final LivingEntity entity, final PlayerEntity playerIn, final IFavor favor) {
    IDeity deity;
    final List<Optional<Deity>> deityList = Lists.newArrayList(GreekFantasy.PROXY.DEITY.getValues());
    // order by which deity has the most favor
    deityList.sort((o1, o2) -> favor.getFavor(o1.orElse(Deity.EMPTY)).compareToAbs(favor.getFavor(o2.orElse(Deity.EMPTY))));
    // change favor amounts for each deity
    for(final Optional<Deity> oDeity : deityList) {
      if(oDeity.isPresent()) {
        deity = oDeity.get();
        // attempt to modify the player's favor with this deity
        final long favorModifier = deity.getKillFavorModifier(entity.getType()) / 8;
        if(favorModifier != 0) {
          favor.getFavor(deity).addFavor(playerIn, deity, favorModifier, Source.ATTACK_ENTITY);
        }
      }
    }
    // attempt to trigger PLAYER_HURT_ENTITY favor effect
    triggerFavorEffect(FavorEffectTrigger.Type.PLAYER_HURT_ENTITY, entity.getType().getRegistryName(), playerIn, favor);
  }
  
  /**
   * Called when a player kills an entity
   * @param entity the entity that was killed
   * @param playerIn the player
   * @param favor the player's favor (from capability)
   */
  public static void onKillEntity(final LivingEntity entity, final PlayerEntity playerIn, final IFavor favor) {
    IDeity deity;
    for(final Optional<Deity> oDeity : GreekFantasy.PROXY.DEITY.getValues()) {
      if(oDeity.isPresent()) {
        deity = oDeity.get();
        final long favorModifier = deity.getKillFavorModifier(entity.getType());
        if(favorModifier != 0) {
          favor.getFavor(deity).addFavor(playerIn, deity, favorModifier, Source.KILL_ENTITY);
        }
      }
    }
    // attempt to trigger PLAYER_KILLED_ENTITY favor effect
    triggerFavorEffect(FavorEffectTrigger.Type.PLAYER_KILLED_ENTITY, entity.getType().getRegistryName(), playerIn, favor);
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
  public static boolean onGiveItem(final StatueTileEntity altar, final IDeity deity,
      final PlayerEntity playerIn, final FavorLevel info, final ItemStack item) {
    final long favorModifier = deity.getItemFavorModifier(item.getItem());
    if(favorModifier != 0 && (info.getFavor() + favorModifier) <= FavorLevel.MAX_FAVOR) {
      info.addFavor(playerIn, deity, favorModifier, Source.GIVE_ITEM);
      if(!playerIn.isCreative()) {
        item.shrink(1);
      }
      return true;
    }
    return false;
  }
  
  public static boolean triggerFavorEffect(final FavorEffectTrigger.Type type, final ResourceLocation data, 
      final PlayerEntity playerIn, final IFavor favor) {
    final long time = playerIn.getEntityWorld().getGameTime() + playerIn.getEntityId() * 3;
    final List<Optional<Deity>> deityList = Lists.newArrayList(GreekFantasy.PROXY.DEITY.getValues());
    // order by which deity has the most favor
    deityList.sort((o1, o2) -> favor.getFavor(o1.orElse(Deity.EMPTY)).compareToAbs(favor.getFavor(o2.orElse(Deity.EMPTY))));
    IDeity deity;
    FavorLevel level;
    // loop through each deity until one of them can perform an effect
    for(final Optional<Deity> oDeity : deityList) {
      if(oDeity.isPresent()) {
        deity = oDeity.get();
        level = favor.getFavor(deity);
        if(favor.canUseTriggeredEffect(level, time)) {
          // perform an effect, set the timestamp and cooldown, and exit the loop
          long cooldown = FavorEffectManager.onTriggeredFavorEffect(type, data, playerIn.getEntityWorld(), playerIn, deity, favor, level);
          if(cooldown <= 0) {
            cooldown = 200;
          }
          favor.setTriggeredTimestamp(time);
          favor.setTriggeredCooldown(cooldown);
          return true;
        }
      }
    }
    return false;
  }
}
