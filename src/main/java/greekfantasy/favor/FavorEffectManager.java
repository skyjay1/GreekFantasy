package greekfantasy.favor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import greekfantasy.GreekFantasy;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class FavorEffectManager {
  
  private static List<ResourceLocation> functions = new ArrayList<>();
  
  private static void loadFunctions(final FunctionManager manager) {
    manager.reloader.func_240931_a_().keySet().forEach(rl -> {
      if(rl.getNamespace().equals(GreekFantasy.MODID) && rl.getPath().contains("favor_effect")) {
        functions.add(rl);
      }
    });
  }

  public static long onFavorEffect(final World worldIn, final PlayerEntity playerIn, final IDeity deity, final IFavor favor, final FavorLevel info) {
    final MinecraftServer server = worldIn.getServer();
    if(server != null) {
      boolean flag = false;
      final FavorEffect effect = deity.getRandomEffect(playerIn.getRNG(), info.getLevel());
      // attempt to run the function, summon, item, or potion effect (exclusively, in that order)
      if(functionFavorEffect(server, worldIn, playerIn, effect.getFunction())) {
        flag = true;
      } else if(summonFavorEffect(worldIn, playerIn, effect.getSummon())) {
        flag = true;
      } else if(itemFavorEffect(playerIn, effect.getItem())) {
        flag = true;
      } else if(potionFavorEffect(playerIn, effect.getPotion())) {
        flag = true;
      } else GreekFantasy.LOGGER.debug("Failed to run any favor effect :(");
      // if any of the effects ran successfully, send a message to the player, play a sound, and set cooldown
      if(flag) {
        final String message = effect.isPositive() ? "positive" : "negative";
        final TextFormatting color = effect.isPositive() ? TextFormatting.GREEN : TextFormatting.RED;
        final SoundEvent sound = effect.isPositive() ? SoundEvents.ENTITY_PLAYER_LEVELUP : SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER;
        playerIn.sendStatusMessage(new TranslationTextComponent("favor.effect." + message, deity.getText()).mergeStyle(color), false);
        playerIn.playSound(sound, 0.4F, 0.9F + playerIn.getRNG().nextFloat() * 0.2F);
        return effect.getMinCooldown() + playerIn.getRNG().nextInt((int)Math.max(1, effect.getMinCooldown()));
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
      // DEBUG
      GreekFantasy.LOGGER.debug("Potion favor effect is running...");
      return playerIn.addPotionEffect(EffectInstance.read(potionTag.get()));
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
      // DEBUG
      GreekFantasy.LOGGER.debug("Item favor effect is running...");
      ItemEntity item = playerIn.entityDropItem(itemTag.get());
      if(item != null) {
        item.setNoPickupDelay();
      }
    }
    return false;
  }
  
  private static boolean summonFavorEffect(final World worldIn, final PlayerEntity playerIn, final Optional<CompoundNBT> entityTag) {
    if(entityTag.isPresent()) {
      // DEBUG
      GreekFantasy.LOGGER.debug("Summon favor effect is running...");
      Optional<EntityType<?>> entityType = EntityType.readEntityType(entityTag.get());
      if(entityType.isPresent()) {
        // create the entity
        Entity entity = entityType.get().create(worldIn);
        entity.read(entityTag.get());
        // find a place to spawn the entity
        Random rand = playerIn.getRNG();
        BlockPos spawnPos;
        for(int attempts = 30, range = 10; attempts > 0; attempts--) {
          spawnPos = playerIn.getPosition().add(rand.nextInt(range), rand.nextInt(4) - 2, rand.nextInt(range));
          // check if this is a valid position (solid block with 2 air blocks above it)
          if(worldIn.getBlockState(spawnPos.down()).isSolidSide(worldIn, spawnPos, Direction.UP) && worldIn.isAirBlock(spawnPos) && worldIn.isAirBlock(spawnPos.up())) {
            // spawn the entity here and finish
            entity.setPosition(spawnPos.getX() + 0.5D, spawnPos.getY() + 0.01D, spawnPos.getZ());
            return worldIn.addEntity(entity);
          }
        }
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
   * @return true if the function was successfully executed
   */
  private static boolean functionFavorEffect(final MinecraftServer server, final World worldIn, final PlayerEntity playerIn, final Optional<ResourceLocation> function) {
    if(function.isPresent() && server != null) {
      // DEBUG
      GreekFantasy.LOGGER.debug("Function favor effect is running...");
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
      final BlockPos pos = playerIn.getPosition().up();
      final Vector3d vec = Vector3d.copyCenteredWithVerticalOffset(pos, 0.5D);
      if(mcfunction.isPresent()) {
        // make a command source at the player
        final CommandSource commandSource = manager.getCommandSource().withEntity(playerIn).withPos(vec).withPermissionLevel(4).withFeedbackDisabled();
        manager.execute(mcfunction.get(), commandSource);
        return true;
      }
    }
    return false;
  }
}
