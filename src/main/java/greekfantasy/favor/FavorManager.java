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
import net.minecraft.potion.Effect;
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
   * Called when a potion effect is added to a player
   * @param player the player
   * @param effect the potion effect that was added
   * @param favor the player's favor capability
   */
  public static void onAddPotion(final PlayerEntity player, final Effect effect, final IFavor favor) {
    // attempt to trigger EFFECTS_CHANGED favor effect
    triggerFavorEffect(FavorEffectTrigger.Type.EFFECTS_CHANGED, effect.getRegistryName(), player, favor);
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
      favor.forEach((d, f) -> f.depleteFavor(player, d, 1, Source.PASSIVE), true);
    }
    // every few seconds, attempt to perform a favor effect
    if(!player.isCreative() && !player.isSpectator() && player.ticksExisted > 10 && time % 50 == 0 && favor.hasNoEffectCooldown(time)) {
      Optional<Deity> deity;
      FavorLevel info;
      ArrayList<Entry<ResourceLocation, FavorLevel>> entryList = Lists.newArrayList(favor.getAllFavor().entrySet());
      // order by which deity has the most favor
      //// Collections.sort(entryList, (e1, e2) -> e1.getValue().compareToAbs(e2.getValue()));
      //  order randomly
      Collections.shuffle(entryList, player.getRNG());
      // loop through each deity until one of them can perform an effect
      for(final Entry<ResourceLocation, FavorLevel> entry : entryList) {
        deity = GreekFantasy.PROXY.DEITY.get(entry.getKey());
        info = entry.getValue();
        if(deity.isPresent() && deity.get().isEnabled() && player.getRNG().nextDouble() * 0.7D < info.getPercentFavor()) {
          // perform an effect, set the timestamp and cooldown, and exit the loop
          long cooldown = FavorEffectManager.onFavorEffect(player.getEntityWorld(), player, deity.get(), favor, info);
          if(cooldown > 0) {
            favor.setEffectTime(time, cooldown);
            return;
          }
        }
      }
      // if no effect was performed, set a cooldown
      favor.setEffectTime(time, 200);
    }
    return;
  }
  
  /**
   * Triggers a favor effect and sets the triggered timestamp and cooldown
   * @param type the favor effect trigger type
   * @param data the data to interpret based on the type
   * @param playerIn the player
   * @param favor the player's favor data
   * @return true if a favor effect was triggered and ran successfully
   */
  public static boolean triggerFavorEffect(final FavorEffectTrigger.Type type, final ResourceLocation data, 
      final PlayerEntity playerIn, final IFavor favor) {
    final long time = playerIn.getEntityWorld().getGameTime() + playerIn.getEntityId() * 3;
    if(favor.hasNoTriggeredCooldown(time)) {
      final List<IDeity> deityList = Lists.newArrayList(GreekFantasy.PROXY.getDeityCollection(true));
      // order by which deity has the most favor
      //// deityList.sort((o1, o2) -> favor.getFavor(o1.orElse(Deity.EMPTY)).compareToAbs(favor.getFavor(o2.orElse(Deity.EMPTY))));
      // order randomly
      Collections.shuffle(deityList, playerIn.getRNG());
      FavorLevel level;
      // loop through each deity until one of them can perform an effect
      for(final IDeity deity : deityList) {
        level = favor.getFavor(deity);
        // perform an effect, set the timestamp and cooldown, and exit the loop
        long cooldown = FavorEffectManager.onTriggeredFavorEffect(type, data, playerIn.getEntityWorld(), playerIn, deity, favor, level);
        if(cooldown > 0) {
          favor.setTriggeredTime(time, cooldown);
          return true;
        }
      }
      // if no effect was performed, set a cooldown
      favor.setTriggeredTime(time, 100);
    }
    return false;
  }
  
  /**
   * Called when a player attacks an entity
   * @param entity the entity that was attacked
   * @param playerIn the player
   * @param favor the player's favor (from capability)
   */
  public static void onAttackEntity(final LivingEntity entity, final PlayerEntity playerIn, final IFavor favor) {
    final List<IDeity> deityList = Lists.newArrayList(GreekFantasy.PROXY.getDeityCollection(true));
    // change favor amounts for each deity
    for(final IDeity deity : deityList) {
      // attempt to modify the player's favor with this deity
      final long favorModifier = deity.getKillFavorModifier(entity.getType()) / 8;
      if(favorModifier != 0) {
        favor.getFavor(deity).addFavor(playerIn, deity, favorModifier, Source.ATTACK_ENTITY);
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
    for(final IDeity deity : GreekFantasy.PROXY.getDeityCollection(true)) {
      final long favorModifier = deity.getKillFavorModifier(entity.getType());
      if(favorModifier != 0) {
        favor.getFavor(deity).addFavor(playerIn, deity, favorModifier, Source.KILL_ENTITY);
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
  
  
}
