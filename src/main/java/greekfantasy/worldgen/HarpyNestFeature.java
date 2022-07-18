package greekfantasy.worldgen;

import com.mojang.serialization.Codec;
import greekfantasy.GFRegistry;
import greekfantasy.entity.monster.Harpy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Material;

import java.util.Random;

public class HarpyNestFeature extends Feature<TreeConfiguration> {


    public HarpyNestFeature(final Codec<TreeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<TreeConfiguration> context) {
        // choose random variant (or empty)
        final Rotation rotation = Rotation.getRandom(context.random());
        switch (context.random().nextInt(2)) {
            case 0:
                return buildTallVariant(context, rotation);
            case 1:
                return buildShortVariant(context, rotation);
            default:
                return false;
        }
    }

    public boolean buildTallVariant(final FeaturePlaceContext<TreeConfiguration> context, final Rotation rotation) {
        // unwrap config
        final ServerLevelAccessor level = context.level();
        final TreeConfiguration config = context.config();
        final Random rand = context.random();
        // determine start position and height
        BlockPos.MutableBlockPos pos = context.origin().mutable();
        int dHeight = context.random().nextInt(3) - 1;
        // build tree
        for (int i = 0, n = 14 + dHeight; i < n; i++) {
            level.setBlock(pos, config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
            pos.move(Direction.UP, 1);
        }
        // prepare to place branch
        pos.setWithOffset(context.origin(), 0, dHeight, 0);
        // place branch
        level.setBlock(rot(pos.move(Direction.UP, 4), 0, 1, rotation), config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
        level.setBlock(rot(pos.move(Direction.UP, 1), 0, 1, rotation), config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
        level.setBlock(rot(pos, 0, 1, rotation), config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
        // prepare to place canopy
        pos.setWithOffset(context.origin(), 0, 3 + dHeight, 0);
        // lower canopy
        generateLeavesAround(level, rand, pos.move(Direction.UP), config.foliageProvider, 2);
        generateLeavesAround(level, rand, pos.move(Direction.UP), config.foliageProvider, 2);
        generateLeavesAround(level, rand, pos.move(Direction.UP), config.foliageProvider, 1);
        generateLeavesAround(level, rand, rot(pos.move(Direction.DOWN), 0, 2, rotation), config.foliageProvider, 1);
        generateLeavesAround(level, rand, rot(pos, 0, 1, rotation), config.foliageProvider, 1);
        // upper canopy
        pos.setWithOffset(context.origin(), 0, 8 + dHeight, 0);
        generateLeavesAround(level, rand, pos.move(Direction.UP, 2), config.foliageProvider, 1);
        generateLeavesAround(level, rand, pos.move(Direction.UP), config.foliageProvider, 2);
        generateLeavesAround(level, rand, pos.move(Direction.UP), config.foliageProvider, 2);
        generateLeavesAround(level, rand, pos.move(Direction.UP), config.foliageProvider, 2);
        generateLeavesAround(level, rand, pos.move(Direction.UP), config.foliageProvider, 1);
        if(rand.nextBoolean()) {
            level.setBlock(pos.move(Direction.UP), config.foliageProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
        }
        // nest
        rot(pos.setWithOffset(context.origin(), 0, 6 + dHeight, 0), 0, 2, rotation);
        generateNestAround(level, pos);
        addHarpy(level, rand, pos.move(Direction.UP));
        if (rand.nextBoolean()) {
            addHarpy(level, rand, pos);
        }

        return true;
    }

    public boolean buildShortVariant(final FeaturePlaceContext<TreeConfiguration> context, final Rotation rotation) {
        // unwrap config
        final ServerLevelAccessor level = context.level();
        final TreeConfiguration config = context.config();
        final Random rand = context.random();
        // determine start position and height
        BlockPos.MutableBlockPos pos = context.origin().mutable();
        int dHeight = context.random().nextInt(3) - 1;
        // build tree
        for (int i = 0, n = 6 + dHeight; i < n; i++) {
            level.setBlock(pos, config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
            pos.move(Direction.UP, 1);
        }
        // -z branch
        pos.setWithOffset(context.origin(), 0, 4 + dHeight, 0);
        level.setBlock(rot(pos, 0, -1, rotation), config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
        level.setBlock(rot(pos.move(Direction.UP), 0, -1, rotation), config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
        // +z branch
        pos.setWithOffset(context.origin(), 0, 4 + dHeight, 0);
        level.setBlock(rot(pos, 0, 1, rotation), config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
        level.setBlock(rot(pos.move(Direction.UP), 0, 1, rotation), config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
        // -x branch
        pos.setWithOffset(context.origin(), 0, 4 + dHeight, 0);
        level.setBlock(rot(pos, -1, 0, rotation), config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
        level.setBlock(rot(pos.move(Direction.UP), -1, 0, rotation), config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
        level.setBlock(rot(pos, 0, -1, rotation), config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
        level.setBlock(rot(pos.move(Direction.UP), -1, 1, rotation), config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
        // +x branch
        pos.setWithOffset(context.origin(), 0, 5 + dHeight, 0);
        level.setBlock(rot(pos, 1, 0, rotation), config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
        level.setBlock(rot(pos, 1, 0, rotation), config.trunkProvider.getState(rand, pos), Block.UPDATE_CLIENTS);
        // prepare to place leaves
        pos.setWithOffset(context.origin(), 0, 3 + dHeight, 0);
        // central canopy
        generateLeavesAround(level, rand, pos.move(Direction.UP), config.foliageProvider, 2);
        generateLeavesAround(level, rand, pos.move(Direction.UP), config.foliageProvider, 3);
        // -x side
        pos.setWithOffset(context.origin(), 0, 5 + dHeight, 0);
        generateLeavesAround(level, rand, rot(pos, -2, 0, rotation), config.foliageProvider, 1);
        generateLeavesAround(level, rand, rot(pos.move(Direction.UP), -1, 0, rotation), config.foliageProvider, 1);
        generateLeavesAround(level, rand, rot(pos, 1, 1, rotation), config.foliageProvider, 1);
        generateLeavesAround(level, rand, pos.move(Direction.UP), config.foliageProvider, 1);
        generateLeavesAround(level, rand, rot(pos, 0, -1, rotation), config.foliageProvider, 1);
        generateLeavesAround(level, rand, rot(pos.move(Direction.DOWN), 0, -1, rotation), config.foliageProvider, 1);
        // +x side
        rot(pos.setWithOffset(context.origin(), 0, 5 + dHeight, 0), 2, 0, rotation);
        generateLeavesAround(level, rand, pos, config.foliageProvider, 1);
        generateLeavesAround(level, rand, pos.move(Direction.UP), config.foliageProvider, 1);
        // -z side
        rot(pos.setWithOffset(context.origin(), 0, 6 + dHeight, 0), 0, -2, rotation);
        generateLeavesAround(level, rand, pos, config.foliageProvider, 1);
        generateLeavesAround(level, rand, pos.move(Direction.UP), config.foliageProvider, 1);
        // +z side
        rot(pos.setWithOffset(context.origin(), 0, 6 + dHeight, 0), 0, 2, rotation);
        generateLeavesAround(level, rand, pos, config.foliageProvider, 1);
        generateLeavesAround(level, rand, pos.move(Direction.UP), config.foliageProvider, 1);
        // nest
        pos.setWithOffset(context.origin(), 0, 6 + dHeight, 0);
        generateNestAround(level, pos);
        pos.move(Direction.UP);
        addHarpy(level, rand, pos);
        if (rand.nextBoolean()) {
            addHarpy(level, rand, pos);
        }
        // add a few more leaves for an imperfect nest
        rot(pos.move(Direction.DOWN, 2), -1, 1, rotation);
        for (int i = 0; i < 3; i++) {
            level.setBlock(pos.move(Direction.UP), config.foliageProvider.getState(rand, pos), Block.UPDATE_ALL);
        }
        return true;
    }

    // HELPER METHODS //

    /**
     * Offsets the mutable block pos based on the rotation and offset amounts
     *
     * @param origin the mutable block pos
     * @param x      the x offset
     * @param z      the z offset
     * @param rot    the structure rotation
     * @return the mutable block pos
     */
    protected static BlockPos rot(final BlockPos.MutableBlockPos origin, final int x, final int z, final Rotation rot) {
        return origin.move(new BlockPos(x, 0, z).rotate(rot));
    }

    protected static boolean isReplaceableAt(LevelAccessor level, BlockPos pos) {
        return level.isStateAtPosition(pos, state -> (state.isAir()
                || state.getMaterial().isReplaceable() || state.getMaterial() == Material.LEAVES));
    }

    protected static void generateLeavesAround(LevelAccessor level, Random rand, BlockPos pos, BlockStateProvider leaf, int radius) {
        BlockPos.MutableBlockPos p = pos.mutable();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                p.setWithOffset(pos, x, 0, z);
                if (shouldGenerateLeaf(x, z, radius, rand) && isReplaceableAt(level, p)) {
                    level.setBlock(p, leaf.getState(rand, p), Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    protected static void generateNestAround(LevelAccessor level, BlockPos pos) {
        BlockPos.MutableBlockPos p = pos.mutable();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                p.setWithOffset(pos, x, 0, z);
                level.setBlock(p, GFRegistry.BlockReg.NEST.get().defaultBlockState(), Block.UPDATE_ALL);
                level.setBlock(p.move(Direction.UP), Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                level.setBlock(p.move(Direction.UP), Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    protected static void addHarpy(final ServerLevelAccessor level, final Random rand, final BlockPos pos) {
        // spawn a harpy
        final Harpy entity = GFRegistry.EntityReg.HARPY.get().create(level.getLevel());
        entity.moveTo(pos.getX() + rand.nextDouble(), pos.getY() + 0.5D, pos.getZ() + rand.nextDouble(), 0, 0);
        entity.setPersistenceRequired();
        level.addFreshEntity(entity);
    }

    protected static boolean shouldGenerateLeaf(final int x, final int z, final int radius, final Random rand) {
        return !(Math.abs(x) == radius && Math.abs(z) == radius && rand.nextInt(2) == 0);
    }
}
