package greekfantasy.favor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import greekfantasy.GreekFantasy;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
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
      final FunctionManager manager = server.getFunctionManager();
      if(functions.isEmpty()) {
        loadFunctions(manager);
        // if it's still empty, something went wrong
        if(functions.isEmpty()) {
          GreekFantasy.LOGGER.error("Tried to load functions for favor_effect but none were found! What went wrong?");
          return -1;
        }
      }
      final FavorEffect effect = deity.getRandomEffect(playerIn.getRNG(), info.getLevel());
      final Optional<FunctionObject> function = manager.get(effect.getFunction());
      final BlockPos pos = playerIn.getPosition().up();
      final Vector3d vec = Vector3d.copyCenteredWithVerticalOffset(pos, 0.5D);
      if(function.isPresent()) {
        final CommandSource commandSource = manager.getCommandSource().withEntity(playerIn).withPos(vec).withPermissionLevel(4).withFeedbackDisabled();
        manager.execute(function.get(), commandSource);
        final String message = effect.isPositive() ? "positive" : "negative";
        final TextFormatting color = effect.isPositive() ? TextFormatting.GREEN : TextFormatting.RED;
        playerIn.sendStatusMessage(new TranslationTextComponent("favor.effect." + message, deity.getText()).mergeStyle(color), false);
        return effect.getMinCooldown() + playerIn.getRNG().nextInt((int)Math.max(1, effect.getMinCooldown()));
      }
    }
    return -1;
  }
}
