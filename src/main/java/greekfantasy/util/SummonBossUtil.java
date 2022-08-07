package greekfantasy.util;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.Automaton;
import greekfantasy.entity.boss.BronzeBull;
import greekfantasy.entity.boss.Cerberus;
import greekfantasy.entity.boss.Talos;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.UUID;

public final class SummonBossUtil {

    public static final TagKey<Block> BRONZE_BLOCK = ForgeRegistries.BLOCKS.tags().createTagKey(new ResourceLocation("forge", "storage_blocks/bronze"));
    public static final TagKey<Block> COPPER_BLOCK = ForgeRegistries.BLOCKS.tags().createTagKey(new ResourceLocation("forge", "storage_blocks/any_copper"));
    public static final TagKey<Block> CERBERUS_FRAME = ForgeRegistries.BLOCKS.tags().createTagKey(new ResourceLocation(GreekFantasy.MODID, "cerberus_frame"));

    /**
     * BlockPattern for Talos boss
     **/
    private static final BlockPattern talosPattern = BlockPatternBuilder.start()
            .aisle("~^~", "###", "###")
            .where('^', BlockInWorld.hasState(state -> state.is(BRONZE_BLOCK)))
            .where('#', BlockInWorld.hasState(state -> state.is(COPPER_BLOCK)))
            .where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();

    /**
     * BlockPattern for Bronze Bull boss
     **/
    private static final BlockPattern bronzeBullPattern = BlockPatternBuilder.start()
            .aisle("^##^", "~##~")
            .aisle("~##~", "~##~")
            .where('^', BlockInWorld.hasState(state -> state.is(BRONZE_BLOCK)))
            .where('#', BlockInWorld.hasState(state -> state.is(COPPER_BLOCK)))
            .where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();

    /**
     * BlockPattern for Cerberus boss
     **/
    private static final BlockPattern cerberusPattern = BlockPatternBuilder.start()
            .aisle("~##~", "~~~~", "~~~~")
            .aisle("#^^#", "~OO~", "~OO~")
            .aisle("#^^#", "~OO~", "~OO~")
            .aisle("~##~", "~~~~", "~~~~")
            .where('#', BlockInWorld.hasState(state -> state.is(CERBERUS_FRAME)))
            .where('^', BlockInWorld.hasState(state -> state.is(Blocks.LAVA)))
            .where('O', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR)))
            .where('~', BlockInWorld.hasState(state -> true)).build();

    /**
     * BlockPattern for Automaton
     **/
    private static final BlockPattern automatonPattern = BlockPatternBuilder.start()
            .aisle("^", "#")
            .where('^', BlockInWorld.hasState(state -> state.is(BRONZE_BLOCK)))
            .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(GFRegistry.BlockReg.ICHOR_INFUSED_GEARBOX.get())))
            .build();


    /**
     * Called when a block in the {@link #BRONZE_BLOCK} tag is placed. Checks if a boss stucture was formed,
     * and if so, removes the blocks and summons the boss.
     *
     * @param level  the level
     * @param pos    the position of the block
     * @param state  the block state
     * @param placer the entity who placed the block, if any
     * @return true if the boss was summoned and the structure was replaced
     */
    public static boolean onPlaceBronzeBlock(Level level, BlockPos pos, BlockState state, @Nullable Entity placer) {
        if(!state.is(BRONZE_BLOCK)) {
            return false;
        }
        // check if an automaton was built
        BlockPattern pattern = automatonPattern;
        BlockPattern.BlockPatternMatch helper = pattern.find(level, pos);
        if (helper != null) {
            // remove the blocks that were used
            for (int i = 0; i < pattern.getWidth(); ++i) {
                for (int j = 0; j < pattern.getHeight(); ++j) {
                    for (int k = 0; k < pattern.getDepth(); ++k) {
                        BlockInWorld cachedblockinfo1 = helper.getBlock(i, j, k);
                        level.destroyBlock(cachedblockinfo1.getPos(), false);
                    }
                }
            }
            // spawn the automaton
            Automaton.spawnAutomaton(level, helper.getBlock(0, 1, 0).getPos(), 0);
            return true;
        }
        // check if a talos was built
        pattern = talosPattern;
        helper = pattern.find(level, pos);
        if (helper != null) {
            // remove the blocks that were used
            for (int i = 0; i < pattern.getWidth(); ++i) {
                for (int j = 0; j < pattern.getHeight(); ++j) {
                    for (int k = 0; k < pattern.getDepth(); ++k) {
                        BlockInWorld cachedblockinfo1 = helper.getBlock(i, j, k);
                        level.destroyBlock(cachedblockinfo1.getPos(), false);
                    }
                }
            }
            // spawn the talos
            Talos.spawnTalos(level, helper.getBlock(1, 2, 0).getPos(), 0);
            return true;
        }
        // check if a bronze bull was built
        pattern = bronzeBullPattern;
        helper = pattern.find(level, pos);
        if (helper != null) {
            // remove the blocks that were used
            for (int i = 0; i < pattern.getWidth(); ++i) {
                for (int j = 0; j < pattern.getHeight(); ++j) {
                    for (int k = 0; k < pattern.getDepth(); ++k) {
                        BlockInWorld cachedblockinfo1 = helper.getBlock(i, j, k);
                        level.destroyBlock(cachedblockinfo1.getPos(), false);
                    }
                }
            }
            // spawn the bronze bull
            BronzeBull.spawnBronzeBull(level, helper.getBlock(1, 1, 0).getPos(), 0);
            return true;
        }
        return false;
    }


    /**
     * Called when an Orthus Head item entity is removed while on fire. Checks if a boss structure was formed,
     * and if so, removes the blocks and summons the boss.
     * @param level the level
     * @param pos the block position
     * @param thrower the entity that burned the orthus head, may be null
     * @return true if the boss was summoned and the structure was replaced
     */
    public static boolean onOrthusHeadBurned(Level level, BlockPos pos, @Nullable UUID thrower) {
        // check if a cerberus frame was built
        BlockPattern pattern = cerberusPattern;
        BlockPattern.BlockPatternMatch helper = pattern.find(level, pos);
        if (helper != null) {
            // replace the lava blocks that were used
            for (int i = 1; i < pattern.getWidth() - 1; ++i) {
                for (int k = 1; k < pattern.getDepth() - 1; ++k) {
                    BlockInWorld cachedblockinfo1 = helper.getBlock(i, 0, k);
                    level.setBlock(cachedblockinfo1.getPos(), Blocks.MAGMA_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
            // spawn the cerberus
            Cerberus.spawnCerberus(level, Vec3.atBottomCenterOf(pos.above()));
            return true;
        }
        return false;
    }
}