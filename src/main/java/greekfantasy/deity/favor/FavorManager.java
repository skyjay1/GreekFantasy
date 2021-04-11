package greekfantasy.deity.favor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;

import com.google.common.collect.Lists;

import greekfantasy.GFRegistry;
import greekfantasy.GFWorldSavedData;
import greekfantasy.GreekFantasy;
import greekfantasy.deity.Deity;
import greekfantasy.deity.IDeity;
import greekfantasy.deity.favor_effect.ConfiguredFavorRange;
import greekfantasy.deity.favor_effect.ConfiguredSpecialFavorEffect;
import greekfantasy.deity.favor_effect.FavorEffectManager;
import greekfantasy.deity.favor_effect.FavorEffectTrigger;
import greekfantasy.deity.favor_effect.SpecialFavorEffect;
import greekfantasy.entity.MakhaiEntity;
import greekfantasy.event.FavorChangedEvent.Source;
import greekfantasy.network.SSimpleParticlesPacket;
import greekfantasy.tileentity.StatueTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

public class FavorManager {
  
  /**
   * Called when a living entity kills the player
   * @param player the player
   * @param source the true entity that killed the player
   * @param favor the player's favor capability
   */
  public static void onPlayerKilled(final PlayerEntity player, final Entity source, final IFavor favor) {
    if(!favor.isEnabled()) return;
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
    if(!favor.isEnabled()) return;
    // attempt to trigger ENTITY_HURT_PLAYER favor effect
    triggerFavorEffect(FavorEffectTrigger.Type.ENTITY_HURT_PLAYER, source.getType().getRegistryName(), player, favor);
    // attempt to trigger effects on combat start
    if(player.getCombatTracker().getCombatDuration() < 40) {
      onCombatStart(player, source, favor);
    }
  }
  
  /**
   * Called when a player breaks a block
   * @param player the player
   * @param block the block that was broken
   * @param favor the player's favor capability
   */
  public static void onBreakBlock(final PlayerEntity player, final Block block, final IFavor favor) {
    if(!favor.isEnabled()) return;
    // attempt to trigger PLAYER_BREAK_BLOCK favor effect
    triggerFavorEffect(FavorEffectTrigger.Type.PLAYER_BREAK_BLOCK, block.getRegistryName(), player, favor);
  }
  
  /**
   * Called when a potion effect is added to a player
   * @param player the player
   * @param effect the potion effect that was added
   * @param favor the player's favor capability
   */
  public static void onAddPotion(final PlayerEntity player, final EffectInstance effect, final IFavor favor) {
    if(!favor.isEnabled()) return;
    // attempt to trigger EFFECTS_CHANGED favor effect
    triggerFavorEffect(FavorEffectTrigger.Type.EFFECTS_CHANGED, effect.getPotion().getRegistryName(), player, favor);
    // attempt to increase the duration
    final long time = IFavor.calculateTime(player);
    final FavorConfiguration favorConfig = GreekFantasy.PROXY.getFavorConfiguration();
    if(player.ticksExisted > 10 && favor.hasNoTriggeredCooldown(time)) {
      float lengthMultiplier = 0.0F;
      long cooldown = -1;
      // determine the length multiplier and max cooldown
      for(final ConfiguredSpecialFavorEffect favorEffect : favorConfig.getSpecials(SpecialFavorEffect.Type.POTION_BONUS_LENGTH)) {
        if(favorEffect.canApply(player, favor)) {
          lengthMultiplier += favorEffect.getEffect().getMultiplier().get();
          cooldown = Math.max(cooldown, favorEffect.getEffect().getRandomCooldown(player.getRNG()));
        }
      }
      if(cooldown > 0) {
        // use the length multiplier amount to change the effect to one with a different duration
        // note: this does not work for negative modifiers
        final int length = effect.getDuration() + (int)Math.round(effect.getDuration() * lengthMultiplier);
        effect.combine(new EffectInstance(effect.getPotion(), length, effect.getAmplifier()));
        // set effect time and cooldown
        favor.setTriggeredTime(time, cooldown);
      }
    }
    // attempt to add a bonus potion effect
    if(player.ticksExisted > 10 && favor.hasNoTriggeredCooldown(time)) {
      long cooldown = -1;
      // determine the length multiplier and max cooldown
      for(final ConfiguredSpecialFavorEffect favorEffect : favorConfig.getSpecials(SpecialFavorEffect.Type.POTION_BONUS_EFFECT)) {
        if(favorEffect.canApply(player, favor)) {
          cooldown = Math.max(cooldown, favorEffect.getEffect().getRandomCooldown(player.getRNG()));
          favorEffect.getEffect().getPotionEffect().ifPresent(e -> player.addPotionEffect(e));
        }
      }
      if(cooldown > 0) {
        // set effect time and cooldown
        favor.setTriggeredTime(time, cooldown);
      }
    }
  }
  
  /**
   * Called every server-side player tick
   * @param player the player
   * @param favor the player's favor capability
   */
  public static void onPlayerTick(final PlayerEntity player, final IFavor favor) {
    if(!favor.isEnabled()) return;
    final long time = IFavor.calculateTime(player);
    // decrease all favor 
    if(player.ticksExisted > 10 && GreekFantasy.CONFIG.doesFavorDecrease() && time % GreekFantasy.CONFIG.getFavorDecreaseInterval() == 0) {
      favor.forEach((d, f) -> f.depleteFavor(player, d, 1 + Math.abs(f.getLevel()), Source.PASSIVE), true);
    }
    // every few seconds, attempt to perform a favor effect
    if(!player.isCreative() && !player.isSpectator() && player.ticksExisted > 10 && time % 45 == 0 && favor.hasNoEffectCooldown(time)) {
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
            break;
          }
        }
      }
    }
    // every few seconds, attempt to perform passive special effects
    if(!player.isCreative() && !player.isSpectator() && player.ticksExisted > 10 && time % 50 == 0 && favor.hasNoTriggeredCooldown(time)) {
      long cooldown = onNearCrops(player, favor);
      if(cooldown > 0) {
        favor.setTriggeredTime(time, cooldown);
      }
    }
    // every few ticks, ensure that flying players can still fly
    if(GreekFantasy.CONFIG.isFlyingEnabled() && player.getEntityWorld() instanceof ServerWorld && 
        !player.isCreative() && !player.isSpectator()  && player.ticksExisted > 10 && time % 11 == 0) {
      final GFWorldSavedData data = GFWorldSavedData.getOrCreate((ServerWorld)player.getEntityWorld());
      if(data.hasFlyingPlayer(player) && !GFWorldSavedData.validatePlayer(player, favor)) {
        // remove the player as a flying player
        data.removeFlyingPlayer(player);
      }
    }
  }
  
  /**
   * Triggers a favor effect and sets the triggered timestamp and cooldown
   * @param type the favor effect trigger type
   * @param data the data to interpret based on the type
   * @param playerIn the player
   * @param favor the player's favor data
   * @return true if a favor effect was triggered and ran successfully
   */
  private static boolean triggerFavorEffect(final FavorEffectTrigger.Type type, final ResourceLocation data, 
      final PlayerEntity playerIn, final IFavor favor) {
    final long time = IFavor.calculateTime(playerIn);
    if(favor.hasNoTriggeredCooldown(time)) {
      final List<IDeity> deityList = Lists.newArrayList(GreekFantasy.PROXY.getDeityCollection(true));
      // order by which deity has the most favor
      deityList.sort((o1, o2) -> favor.getFavor(o1).compareToAbs(favor.getFavor(o2)));
      FavorLevel level;
      // loop through each deity so each one can perform an effect
      // this is different from regular effects, where only one effect is performed
      long cooldown = -1;
      for(final IDeity deity : deityList) {
        level = favor.getFavor(deity);
        // perform an effect, set the timestamp and cooldown, and exit the loop
        long lCooldown = FavorEffectManager.onTriggeredFavorEffect(type, data, playerIn.getEntityWorld(), playerIn, deity, favor, level);
        cooldown = Math.max(cooldown, lCooldown);
      }
      // set timestamp and cooldown if any of the triggers succeeded
      if(cooldown > 0) {
        favor.setTriggeredTime(time, cooldown);
      }
    }
    return false;
  }
  
  /**
   * Called when the player is attacked (or attacks) and their combat tracker
   * shows a duration of less than 2
   * @param player the player who is in combat
   * @param other the other entity that is involved in the combat
   * @param favor the player's favor
   */
  private static void onCombatStart(final PlayerEntity player, final Entity other, final IFavor favor) {
    final long time = IFavor.calculateTime(player);
    FavorConfiguration favorConfig = GreekFantasy.PROXY.getFavorConfiguration();
    // combat start effect
    if(favor.hasNoTriggeredCooldown(time)) {
      long cooldown = -1;
      for(final ConfiguredSpecialFavorEffect effect : favorConfig.getSpecials(SpecialFavorEffect.Type.COMBAT_START_EFFECT)) {
        if(effect.canApply(player, favor)) {
          effect.getEffect().getPotionEffect().ifPresent(e -> player.addPotionEffect(e));
          cooldown = Math.max(cooldown, effect.getEffect().getRandomCooldown(player.getRNG()));
        }
      }
      // set effect cooldown
      if(cooldown > 0) {
        favor.setTriggeredTime(time, cooldown);
      }
    }
    // combat start summon mahkai (does not trigger when favor level is 0 or combat_start_effect was triggered)
    if(favor.hasNoTriggeredCooldown(time)) {
      long cooldown = -1;
      int level = 0;
      boolean isTame = false;
      ConfiguredFavorRange mahkaiRange = favorConfig.getEntity(GFRegistry.MAKHAI_ENTITY);
      for(final ConfiguredSpecialFavorEffect effect : favorConfig.getSpecials(SpecialFavorEffect.Type.COMBAT_SUMMON_MAKHAI)) {
        level = favor.getFavor(effect.getDeity()).getLevel();
        if(effect.canApply(player, favor) && level != 0) {
          // summon a mahkai
          isTame = (mahkaiRange == ConfiguredFavorRange.EMPTY) ? level > 0 : !mahkaiRange.getHostileRange().isInFavorRange(player, favor);
          final MakhaiEntity entity = summonMahkai(player, isTame);
          // set cooldown
          if(entity != null) {
            cooldown = Math.max(cooldown, effect.getEffect().getRandomCooldown(player.getRNG()));
            break;
          }
        }
      }
      // set effect cooldown
      if(cooldown > 0) {
        favor.setTriggeredTime(time, cooldown);
      }
    }
  }
  
  /**
   * Called when a player attacks an entity
   * @param entity the entity that was attacked
   * @param playerIn the player
   * @param favor the player's favor (from capability)
   */
  public static void onAttackEntity(final LivingEntity entity, final PlayerEntity playerIn, final IFavor favor) {
    if(!favor.isEnabled()) return;
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
    // attempt to trigger effects on combat start
    System.out.println("duration=" + playerIn.getCombatTracker().getCombatDuration());
    if(playerIn.getCombatTracker().getCombatDuration() < 40) {
      onCombatStart(playerIn, entity, favor);
    }
  }
  
  /**
   * Called when a player kills an entity
   * @param entity the entity that was killed
   * @param playerIn the player
   * @param favor the player's favor (from capability)
   */
  public static void onKillEntity(final LivingEntity entity, final PlayerEntity playerIn, final IFavor favor) {
    if(!favor.isEnabled()) return;
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
    if(favorModifier != 0 && (info.getFavor() + favorModifier) <= FavorLevel.MAX_FAVOR_POINTS) {
      info.addFavor(playerIn, deity, favorModifier, Source.GIVE_ITEM);
      if(!playerIn.isCreative()) {
        item.shrink(1);
      }
      return true;
    }
    return false;
  }
  
  /**
   * Called when the player fires an arrow and there are favor effects that
   * should change its attack damage
   * @param player the player
   * @param favor the player's favor
   * @param arrow the arrow
   */
  public static void onShootArrow(final PlayerEntity player, final IFavor favor, final AbstractArrowEntity arrow) {
    if(!favor.isEnabled()) return;
    // attempt to change the damage amount of the arrow
    final long time = IFavor.calculateTime(player);
    if(favor.hasNoTriggeredCooldown(time)) {
      double damage = arrow.getDamage();
      float multiplier = 0.0F;
      long cooldown = -1;
      for(final ConfiguredSpecialFavorEffect effect : GreekFantasy.PROXY.getFavorConfiguration().getSpecials(SpecialFavorEffect.Type.ARROW_DAMAGE_MULTIPLIER)) {
        if(effect.canApply(player, favor)) {
          multiplier += effect.getEffect().getMultiplier().orElse(0.0F);
          cooldown = Math.max(cooldown, effect.getEffect().getRandomCooldown(player.getRNG()));
        }
      }
      if(cooldown > 0 && multiplier != 0.0F) {
        favor.setTriggeredTime(time, cooldown);
        GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SSimpleParticlesPacket(true, arrow.getPosition(), 8));
        arrow.setDamage(damage + (damage * multiplier));
      }
    }
  }

  /**
   * Called when the player picks up an XP orb and there are favor effects that change xp
   * @param player the player
   * @param favor the player's favor
   * @param xpValue the initial xp value
   * @return the new xp value
   */
  public static int onPlayerXP(final PlayerEntity player, final IFavor favor, final int xpValue) {
    final long time = IFavor.calculateTime(player);
    if(favor.isEnabled() && favor.hasNoTriggeredCooldown(time)) {
      final FavorConfiguration favorConfig = GreekFantasy.PROXY.getFavorConfiguration();
      float xpMultiplier = 0.0F;
      long cooldown = -1;
      // determine the xp multiplier and max cooldown
      for(final ConfiguredSpecialFavorEffect effect : favorConfig.getSpecials(SpecialFavorEffect.Type.XP_MULTIPLIER)) {
        if(effect.canApply(player, favor)) {
          xpMultiplier += effect.getEffect().getMultiplier().orElse(0.0F);
          cooldown = Math.max(cooldown, effect.getEffect().getRandomCooldown(player.getRNG()));
        }
      }
      if(cooldown > 0 && xpMultiplier != 0.0F) {
        favor.setTriggeredTime(time, cooldown);
        // use the xp multiplier amount to determine new xp
        return Math.round(xpValue + xpValue * xpMultiplier);
      }
    }
    return xpValue;
  }
  
  /**
   * Called when the player breeds animals
   * @param player the player
   * @param favor the player's favor
   * @return the number of baby animals to spawn
   */
  public static int onBabySpawn(final PlayerEntity player, final IFavor favor) {
    if(!favor.isEnabled()) return 1;
    final long time = IFavor.calculateTime(player);
    final FavorConfiguration favorConfig = GreekFantasy.PROXY.getFavorConfiguration();
    float breedingMultiplier = 0.0F;
    long cooldown = -1;
    // determine the breeding multiplier and max cooldown
    for(final ConfiguredSpecialFavorEffect effect : favorConfig.getSpecials(SpecialFavorEffect.Type.BREEDING_OFFSPRING_MULTIPLIER)) {
      if(effect.canApply(player, favor)) {
        breedingMultiplier += effect.getEffect().getMultiplier().get();
        cooldown = Math.max(cooldown, effect.getEffect().getRandomCooldown(player.getRNG()));
      }
    }
    if(cooldown > 0 && breedingMultiplier != 0.0F) {
      favor.setTriggeredTime(time, cooldown);
      // use the breeding multiplier amount to determine new breeding amount
      return Math.round(1 + breedingMultiplier);
    }
    return 1;
  }
  
  /**
   * Checks random blocks in a radius until either a growable crop has been found
   * and changed, or no crops were found in a limited number of attempts.
   * @param player the player
   * @param favor the player's favor
   * @return whether a crop was found and its age was changed
   **/
  private static long onNearCrops(final PlayerEntity player, final IFavor favor) {
    final IntegerProperty[] AGES = new IntegerProperty[] {
        BlockStateProperties.AGE_0_1, BlockStateProperties.AGE_0_15, BlockStateProperties.AGE_0_2,
        BlockStateProperties.AGE_0_3, BlockStateProperties.AGE_0_5, BlockStateProperties.AGE_0_7
    };
    final FavorConfiguration favorConfig = GreekFantasy.PROXY.getFavorConfiguration();
    final Random rand = player.getEntityWorld().getRandom();
    final int maxAttempts = 10;
    final int variationY = 2;
    int radius = 6;
    int attempts = 0;
    int growthToAdd = 0;
    long cooldown = -1;
    // determine the growth amount to add and max cooldown
    for(final ConfiguredSpecialFavorEffect effect : favorConfig.getSpecials(SpecialFavorEffect.Type.CROP_GROWTH_MULTIPLIER)) {
      if(effect.canApply(player, favor)) {
        growthToAdd += Math.round(effect.getEffect().getMultiplier().get());
        cooldown = Math.max(cooldown, effect.getEffect().getRandomCooldown(rand));
      }
    }
    // if no changes should be made, exit immediately
    if(growthToAdd == 0 || cooldown < 0) {
      return -1;
    }
    // if there are effects that should change growth states, find a crop to affect
    while (attempts++ <= maxAttempts) {
      // get random block in radius
      final int x1 = rand.nextInt(radius * 2) - radius;
      final int y1 = rand.nextInt(variationY * 2) - variationY + 1;
      final int z1 = rand.nextInt(radius * 2) - radius;
      final BlockPos blockpos = player.getPosition().add(x1, y1, z1);
      final BlockState state = player.getEntityWorld().getBlockState(blockpos);
      // if the block can be grown, grow it and return
      if (state.getBlock() instanceof IGrowable) {
        // determine which age property applies to this state
        for(final IntegerProperty AGE : AGES) {
          if(state.hasProperty(AGE)) {
            // attempt to update the age (add or subtract)
            int oldAge = state.get(AGE);
            int newAge = Math.max(0, oldAge + growthToAdd);
            if(AGE.getAllowedValues().contains(Integer.valueOf(newAge))) {
              // update the blockstate's age
              player.getEntityWorld().setBlockState(blockpos, state.with(AGE, newAge));
              // spawn particles
              GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SSimpleParticlesPacket(growthToAdd > 0, blockpos, 10));
              // return cooldown value
              return cooldown;
            }
          }
        }
      }
    }
    return -1;
  }
  
  /**
   * Spawns a Mahkai near the player when they enter combat
   * @param playerIn the player who entered combat
   * @param tame true if the mahkai should support the player
   * @return the MakhaiEntity if it was spawned, otherwise null
   */
  private static MakhaiEntity summonMahkai(final PlayerEntity playerIn, final boolean tame) {
    // create a mahkai
    final MakhaiEntity entity = GFRegistry.MAKHAI_ENTITY.create(playerIn.getEntityWorld());
    final Random rand = playerIn.getRNG();
    // attempt to spawn the mahkai nearby
    BlockPos spawnPos;
    for(int attempts = 24, range = 6; attempts > 0; attempts--) {
      spawnPos = playerIn.getPosition().add(rand.nextInt(range) - rand.nextInt(range), rand.nextInt(2) - rand.nextInt(2), rand.nextInt(range) - rand.nextInt(range));
      // check if this is a valid position
      boolean isValidSpawn = playerIn.world.getBlockState(spawnPos.down()).isSolid()
              && playerIn.world.getBlockState(spawnPos).getMaterial() == Material.AIR
              && playerIn.world.getBlockState(spawnPos.up()).getMaterial() == Material.AIR;
      if(isValidSpawn) {
        // set entity position and data
        entity.setPosition(spawnPos.getX() + 0.5D, spawnPos.getY() + 0.01D, spawnPos.getZ() + 0.5D);
        if(tame) {
          entity.setOwner(playerIn);
        } else {
          entity.setRevengeTarget(playerIn);
        }
        // actually add the entity to the world
        playerIn.world.addEntity(entity);
        // set spawning
        entity.onInitialSpawn((IServerWorld) playerIn.world, playerIn.world.getDifficultyForLocation(spawnPos), SpawnReason.MOB_SUMMONED, null, null);
        entity.setSpawning(true);
        return entity;
      }
    }
    // no spawn location was found, remove the entity
    entity.remove();
    return null;
  }
}
