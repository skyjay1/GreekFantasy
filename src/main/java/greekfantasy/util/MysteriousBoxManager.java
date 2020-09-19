package greekfantasy.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import greekfantasy.GreekFantasy;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class MysteriousBoxManager {
  
  private static List<ResourceLocation> functions = new ArrayList<>();
  
  private static void loadFunctions(final FunctionManager manager) {
    functions.add(new ResourceLocation(GreekFantasy.MODID, "mysterious_box/summon_shade"));
  }
  
  private static Optional<FunctionObject> getRandomFunction(final FunctionManager manager, final Random rand) {
    if(functions.isEmpty()) {
      loadFunctions(manager);
    }
    final int index = rand.nextInt(functions.size());
    final ResourceLocation rl = functions.get(index);
    return manager.get(rl);
  }
  
  public static boolean onBoxOpened(final World worldIn, final PlayerEntity playerIn, final BlockState state, final BlockPos pos) {
    final MinecraftServer server = worldIn.getServer();
    if(server != null) {
      final FunctionManager manager = server.getFunctionManager();
      final Optional<FunctionObject> function = getRandomFunction(manager, worldIn.getRandom());
      final Vector3d vec = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.85D, pos.getZ() + 0.5D);
      if(function.isPresent()) {
        final CommandSource commandSource = manager.getCommandSource().withEntity(playerIn).withPos(vec).withPermissionLevel(4).withFeedbackDisabled();
        manager.execute(function.get(), commandSource);
        return true;
      }
    }
    return false;
  }
}
