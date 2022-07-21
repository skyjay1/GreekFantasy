package greekfantasy.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public final class SummonBossUtil {

    public static final TagKey<Block> BRONZE_BLOCK = ForgeRegistries.BLOCKS.tags().createTagKey(new ResourceLocation("forge", "storage_blocks/bronze"));

    public static final TagKey<Block> COPPER_BLOCK = ForgeRegistries.BLOCKS.tags().createTagKey(new ResourceLocation("forge", "storage_blocks/copper"));

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
     * Called when a block in the {@link #BRONZE_BLOCK} tag is placed. Checks if a boss stucture was formed,
     * and if so, removes the blocks and summons the boss.
     *
     * @param level  the level
     * @param pos    the position of the block
     * @param state  the block state
     * @param placer the entity who placed the block, if any
     */
    public static void onPlaceBlock(Level level, BlockPos pos, BlockState state, @Nullable Entity placer) {
        // check if a talos was built
        BlockPattern pattern = talosPattern;
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
            // spawn the talos
            // TODO TalosEntity.spawnTalos(worldIn, helper.getBlock(1, 2, 0).getPos(), 0);
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
            // TODO BronzeBullEntity.spawnBronzeBull(worldIn, helper.getBlock(1, 1, 0).getPos(), 0);
        }
    }
}
