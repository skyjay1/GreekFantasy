package greekfantasy.deity.favor_effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.IDeity;
import greekfantasy.deity.favor.FavorLevel;
import greekfantasy.deity.favor.IFavor;
import greekfantasy.event.FavorChangedEvent;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class FavorEffectManager {
  
  private static List<ResourceLocation> functions = new ArrayList<>();
  
  private static void loadFunctions(final FunctionManager manager) {
    manager.reloader.func_240931_a_().keySet().forEach(rl -> {
      if(rl.getNamespace().equals(GreekFantasy.MODID) && rl.getPath().contains("favor_effect")) {
        functions.add(rl);
      }
    });
  }
  
  /**
   * Chooses a triggered favor effect based on the type, deity, and level 
   * @param type the type of triggered favor effect to perform
   * @param data the data to interpret for the given effect type
   * @param worldIn the world
   * @param playerIn the player
   * @param deity the deity associated with this effect
   * @param favor the player's favor
   * @param level the player's favor level with the given deity
   * @return the favor effect cooldown, or -1 if no effect was performed. Always returns -1 on client
   */
  public static long onTriggeredFavorEffect(final FavorEffectTrigger.Type type, final ResourceLocation data, final World worldIn, 
      final PlayerEntity playerIn, final IDeity deity, final IFavor favor, final FavorLevel level) {
    final MinecraftServer server = worldIn.getServer();
    if(server != null) {
      final TriggeredFavorEffect effect = deity.getTriggeredFavorEffect(playerIn.getRNG(), type, data, level.getLevel());
      return effect == TriggeredFavorEffect.EMPTY ? -1 : performFavorEffect(server, worldIn, playerIn, deity, effect.getEffect());
    }
    return -1;
  }

  /**
   * Chooses a favor effect to perform and attempts to perform it
   * @param worldIn the world
   * @param playerIn the player
   * @param deity the deity that will choose an effect
   * @param favor the player's favor
   * @param info the player's favor level with the given deity
   * @return the favor effect cooldown, or -1 if no effect was performed. Always returns -1 on client
   */
  public static long onFavorEffect(final World worldIn, final PlayerEntity playerIn, final IDeity deity, final IFavor favor, final FavorLevel info) {
    final MinecraftServer server = worldIn.getServer();
    if(server != null) {
      final FavorEffect effect = deity.getRandomEffect(playerIn.getRNG(), info.getLevel());
      return performFavorEffect(server, worldIn, playerIn, deity, effect);
    }
    return -1;
  }
  
  /**
   * Sends a status message to the player (in chat) to inform them which deity caused the recent effect
   * @param playerIn the player
   * @param deity the deity
   * @param positive true if the effect was positive
   */
  public static void sendStatusMessage(final PlayerEntity playerIn, final IDeity deity, final boolean positive) {
    if(GreekFantasy.CONFIG.isFavorNotifyEnabled()) {
      final String message = positive ? "positive" : "negative";
      final TextFormatting color = positive ? TextFormatting.GREEN : TextFormatting.RED;
      final SoundEvent sound = positive ? SoundEvents.ENTITY_PLAYER_LEVELUP : SoundEvents.ENTITY_ITEM_BREAK;
      playerIn.sendStatusMessage(new TranslationTextComponent("favor.effect." + message, deity.getText()).mergeStyle(color), !GreekFantasy.CONFIG.isFavorNotifyChat());
      playerIn.getEntityWorld().playSound(playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), sound, SoundCategory.PLAYERS, 0.4F, 0.9F + playerIn.getRNG().nextFloat() * 0.2F, false);
    }
  }
  
  /**
   * Performs the given favor effect at the player's location
   * @param server the minecraft server
   * @param worldIn the current world
   * @param playerIn the player to affect
   * @param deity the deity associated with the effect
   * @param effect the favor effect
   * @return the favor effect cooldown, or -1 if no effect was performed
   */
  private static long performFavorEffect(final MinecraftServer server, final World worldIn, final PlayerEntity playerIn, final IDeity deity, final FavorEffect effect) {
    boolean flag = false;
    if(effect.isInBiome(worldIn, playerIn.getPosition())) {
      // attempt to run the function, summon, item, potion, or add-favor effect (exclusively, in that order)
      if(functionFavorEffect(server, worldIn, playerIn, effect.getFunction())) {
        flag = true;
      } else if(summonFavorEffect(worldIn, playerIn, effect.getSummon())) {
        flag = true;
      } else if(itemFavorEffect(playerIn, effect.getItem())) {
        flag = true;
      } else if(potionFavorEffect(playerIn, effect.getPotion())) {
        flag = true;
      } else if(addFavorEffect(playerIn, deity, effect.getFavor())) {
        flag = true;
      } else GreekFantasy.LOGGER.debug("Failed to run any part of a favor effect for " + deity.getName().toString() + "... " + effect.toString());
      // if any of the effects ran successfully, send a message to the player, play a sound, and return cooldown
      if(flag) {
        if(!effect.getFavor().isPresent()) {
          sendStatusMessage(playerIn, deity, effect.isPositive());
        }
        return Math.abs(effect.getMinCooldown()) + playerIn.getRNG().nextInt((int)Math.max(1, Math.abs(effect.getMinCooldown())));
      }
    }
    return -1;
  }
  
  /**
   * Adds a potion effect to the player
   * @param playerIn the player
   * @param potionTag an optional containing the potion effect's NBT (or empty)
   * @return true if the potion effect was successfully added
   */
  private static boolean potionFavorEffect(final PlayerEntity playerIn, final Optional<CompoundNBT> potionTag) {
    if(potionTag.isPresent()) {
      final CompoundNBT nbt = potionTag.get().copy();
      nbt.putByte("Id", (byte) Effect.getId(ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionTag.get().getString("Potion")))));
      return playerIn.addPotionEffect(EffectInstance.read(nbt));
    }
    return false;
  }
  
  
  /**
   * Gives an ItemStack to the player by spawning an ItemEntity at the player's position
   * @param playerIn the player
   * @param itemTag an optional containing the ItemStack (or empty)
   * @return true if the item was successfully added
   */
  private static boolean itemFavorEffect(final PlayerEntity playerIn, final Optional<ItemStack> itemTag) {
    if(itemTag.isPresent()) {
      ItemEntity item = playerIn.entityDropItem(itemTag.get());
      if(item != null) {
        item.setNoPickupDelay();
      }
      return true;
    }
    return false;
  }
  
  /**
   * Summons an entity on a solid block near the player
   * @param worldIn the world
   * @param playerIn the player
   * @param entityTag an optional containing the NBTTagCompound of an entity (or empty)
   * @return true if the entity was successfully added
   */
  private static boolean summonFavorEffect(final World worldIn, final PlayerEntity playerIn, final Optional<CompoundNBT> entityTag) {
    if(entityTag.isPresent() && worldIn instanceof IServerWorld) {
      final Optional<EntityType<?>> entityType = EntityType.readEntityType(entityTag.get());
      if(entityType.isPresent()) {
        Entity entity = entityType.get().create(worldIn);
        final boolean waterMob = entity instanceof WaterMobEntity || entity instanceof DrownedEntity || entity instanceof GuardianEntity
            || (entity instanceof MobEntity && ((MobEntity)entity).getNavigator() instanceof SwimmerPathNavigator);
        // find a place to spawn the entity
        Random rand = playerIn.getRNG();
        BlockPos spawnPos;        
        for(int attempts = 24, range = 9; attempts > 0; attempts--) {
          spawnPos = playerIn.getPosition().add(rand.nextInt(range) - rand.nextInt(range), rand.nextInt(2) - rand.nextInt(2), rand.nextInt(range) - rand.nextInt(range));
          // check if this is a valid position
          boolean isValidSpawn = EntitySpawnPlacementRegistry.canSpawnEntity(entityType.get(), (IServerWorld)worldIn, SpawnReason.MOB_SUMMONED, spawnPos, rand) 
              || (waterMob && worldIn.getBlockState(spawnPos).matchesBlock(Blocks.WATER))
              || (!waterMob && worldIn.getBlockState(spawnPos.down()).isSolid()
                  && worldIn.getBlockState(spawnPos).getMaterial() == Material.AIR
                  && worldIn.getBlockState(spawnPos.up()).getMaterial() == Material.AIR);
          if(isValidSpawn) {
            // spawn the entity at this position and finish
            entity.read(entityTag.get());
            entity.setPosition(spawnPos.getX() + 0.5D, spawnPos.getY() + 0.01D, spawnPos.getZ() + 0.5D);
            worldIn.addEntity(entity);
            return true;
          }
        }
        entity.remove();
      }
    }
    return false;
  }
  
  /**
   * Executes a favor effect function
   * @param manager the function manager
   * @param worldIn the world
   * @param playerIn the player
   * @param function an optional containing a function to execute (or empty)
   * @param data 
   * @return true if the function was successfully executed
   */
  private static boolean functionFavorEffect(final MinecraftServer server, final World worldIn, final PlayerEntity playerIn, final Optional<ResourceLocation> function) {
    if(function.isPresent() && server != null) {
      // load the functions from the function manager
      final FunctionManager manager = server.getFunctionManager();
      if(functions.isEmpty()) {
        loadFunctions(manager);
        // if it's still empty, something went wrong
        if(functions.isEmpty()) {
          GreekFantasy.LOGGER.error("Tried to load functions for favor_effect but none were found! What went wrong?");
          return false;
        }
      }
      // prepare to execute the function
      final Optional<FunctionObject> mcfunction = manager.get(function.get());
      final Vector3d vec = playerIn.getPositionVec().add(0.0D, 1.0D, 0.0D);
      if(mcfunction.isPresent()) {
        // make a command source at the player
        final CommandSource commandSource = manager.getCommandSource().withEntity(playerIn).withPos(vec).withPermissionLevel(4).withFeedbackDisabled();
        manager.execute(mcfunction.get(), commandSource);
        return true;
      }
    }
    return false;
  }
  
  /**
   * Gives the player the amount of favor specified
   * @param playerIn the player
   * @param deity the deity whose favor to change
   * @param favorAmount the amount of favor
   * @return true if favor was successfully added
   */
  private static boolean addFavorEffect(final PlayerEntity playerIn, final IDeity deity, final Optional<Long> favorAmount) {
    if(favorAmount.isPresent()) {
      playerIn.getCapability(GreekFantasy.FAVOR).ifPresent(favor -> favor.getFavor(deity).addFavor(playerIn, deity, favorAmount.get(), FavorChangedEvent.Source.OTHER));
      return true;
    }
    return false;
  }
}
