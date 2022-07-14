package greekfantasy.util;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class MysteriousBoxManager {

    private static final List<ResourceLocation> functions = new ArrayList<>();

    private static void loadFunctions(final ServerFunctionManager manager) {
        manager.library.getFunctions().keySet().forEach(rl -> {
            if (rl.getNamespace().equals(GreekFantasy.MODID) && rl.getPath().contains("mysterious_box")) {
                functions.add(rl);
            }
        });
    }

    private static Optional<CommandFunction> getRandomFunction(final ServerFunctionManager manager, final Random rand) {
        // load functions the first time this is called
        if (functions.isEmpty()) {
            loadFunctions(manager);
            // if it's still empty, something went wrong
            if (functions.isEmpty()) {
                GreekFantasy.LOGGER.error("Tried to load functions for mysterious_box but none were found! What went wrong?");
                return Optional.empty();
            }
        }
        // choose a random function to execute
        final int index = rand.nextInt(functions.size());
        final ResourceLocation rl = functions.get(index);
        return manager.get(rl);
    }

    public static boolean onBoxOpened(final Level level, final Player playerIn, final BlockState state, final BlockPos pos) {
        final MinecraftServer server = level.getServer();
        if (server != null) {
            final ServerFunctionManager manager = server.getFunctions();
            final Optional<CommandFunction> function = getRandomFunction(manager, level.getRandom());
            final Vec3 vec = Vec3.atCenterOf(pos).add(0, 0.35D, 0);
            if (function.isPresent()) {
                final CommandSourceStack commandSource = manager.getGameLoopSender().withEntity(playerIn).withPosition(vec).withPermission(4).withSuppressedOutput();
                manager.execute(function.get(), commandSource);
                // percent chance to spawn Elpis as well
                if (level.getRandom().nextFloat() * 100.0F < GreekFantasy.CONFIG.ELPIS_SPAWN_CHANCE.get()) {
                    addElpis(level, pos);
                }
                return true;
            }
        }
        return false;
    }

    public static void addElpis(final Level level, final BlockPos pos) {
        /*final ElpisEntity entity = GFRegistry.EntityReg.ELPIS_ENTITY.create(worldIn);
        entity.moveTo(pos.getX() + 0.5D, pos.getY() + 0.85D, pos.getZ() + 0.5D, 0, 0);
        entity.restrictTo(pos.above(), ElpisEntity.wanderDistance);
        worldIn.addFreshEntity(entity);*/
    }
}
