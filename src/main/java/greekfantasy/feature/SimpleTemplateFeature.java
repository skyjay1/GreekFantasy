package greekfantasy.feature;

import com.mojang.serialization.Codec;
import greekfantasy.GreekFantasy;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public abstract class SimpleTemplateFeature extends Feature<NoFeatureConfig> {

    public SimpleTemplateFeature(final Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    protected abstract ResourceLocation getStructure(final Random rand);

    protected boolean isValidPosition(final ISeedReader reader, final BlockPos pos, final BlockPos size, final Rotation r) {
        final BlockPos c1 = pos;
//    final BlockPos c2 = pos.add(new BlockPos(-size.getX(), 0, 0).rotate(r));
//    final BlockPos c3 = pos.add(new BlockPos(0, 0, -size.getZ()).rotate(r));
        final BlockPos c4 = pos.offset(new BlockPos(-size.getX(), 0, -size.getZ()).rotate(r));
        return isValidPosition(reader, c1) && isValidPosition(reader, c4);
    }

    protected boolean isValidPosition(final ISeedReader reader, final BlockPos pos) {
        return pos.getY() > 3 && reader.getBlockState(pos).canOcclude() && isReplaceableAt(reader, pos.above(1));
    }

    public static boolean isPlantAt(IWorldGenerationReader reader, BlockPos pos) {
        return reader.isStateAtPosition(pos, state -> {
            Material m = state.getMaterial();
            return (m == Material.REPLACEABLE_PLANT || m == Material.PLANT);
        });
    }

    protected static boolean isReplaceableAt(IWorldGenerationReader reader, BlockPos pos) {
        return (isAir(reader, pos) || isPlantAt(reader, pos));
    }

    public static BlockPos getRandomPosition(final ISeedReader reader, BlockPos origin, final Random rand, int down) {
        final BlockPos blockPos = origin.offset(4 + rand.nextInt(8), 0, 4 + rand.nextInt(8));
        return getHeightPos(reader, blockPos);
    }

    public static BlockPos getHeightPos(final ISeedReader world, final BlockPos original) {
        int y = world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, original).getY();
        final BlockPos pos = new BlockPos(original.getX(), y, original.getZ());
        return world.getBlockState(pos).is(Blocks.SNOW) ? pos.below(2) : pos.below();
    }

    public static void fillBelow(final ISeedReader world, final BlockPos origin, final BlockPos size, final Rotation rotation, final Block[] blocks) {
        BlockPos tmp;
        BlockPos pos;
        Block block;
        for (int dx = 0, lx = size.getX(); dx < lx; dx++) {
            for (int dz = 0, lz = size.getZ(); dz < lz; dz++) {
                tmp = new BlockPos(dx, 0, dz).rotate(rotation);
                pos = origin.offset(tmp);
                // set blocks below this one to the provided block(s)
                if (world.hasChunk(pos.getX() >> 4, pos.getZ() >> 4) && world.getBlockState(pos.above()).canOcclude()) {
                    while (pos.getY() > 0 && isReplaceableAt(world, pos)) {
                        block = blocks[world.getRandom().nextInt(blocks.length)];
                        world.setBlock(pos, block.defaultBlockState(), 2);
                        pos = pos.below();
                    }
                }
            }
        }
    }

    public static boolean isValidDimension(final ISeedReader world) {
        String dimName = world.getLevel().dimension().location().toString();
        return GreekFantasy.CONFIG.IS_FEATURES_WHITELIST.get() == GreekFantasy.CONFIG.FEATURES_DIMENSION_WHITELIST.get().contains(dimName);
    }
}