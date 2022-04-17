package greekfantasy.feature;

import com.mojang.serialization.Codec;
import greekfantasy.GreekFantasy;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;

public class SmallShrineFeature extends SimpleTemplateFeature {

    private static final ResourceLocation STRUCTURE_LIMESTONE = new ResourceLocation(GreekFantasy.MODID, "small_limestone_shrine");
    private static final ResourceLocation STRUCTURE_MARBLE = new ResourceLocation(GreekFantasy.MODID, "small_marble_shrine");

    public SmallShrineFeature(final Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(final ISeedReader reader, final ChunkGenerator chunkGenerator, final Random rand,
                         final BlockPos blockPosIn, final NoFeatureConfig config) {
        // check dimension from config
        if (!SimpleTemplateFeature.isValidDimension(reader)) {
            return false;
        }
        // template loading
        final TemplateManager manager = reader.getLevel().getStructureManager();
        final Template template = manager.getOrCreate(getStructure(rand));

        // position for generation
        BlockPos pos = getRandomPosition(reader, blockPosIn, rand, 1);

        // rotation / mirror
        Mirror mirror = Mirror.NONE;
        Rotation rotation = Rotation.getRandom(rand);

        // check for valid position
        final BlockPos size = template.getSize();
        if (!isValidPosition(reader, pos, size, rotation)) {
            return false;
        }

        // placement settings
        MutableBoundingBox mbb = new MutableBoundingBox(pos.getX() - 8, pos.getY() - 16, pos.getZ() - 8, pos.getX() + 8, pos.getY() + 16, pos.getZ() + 8);
        PlacementSettings placement = new PlacementSettings()
                .setRotation(rotation).setMirror(mirror).setRandom(rand).setBoundingBox(mbb)
                .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);

        // actually generate the structure
        if (template.placeInWorld(reader, pos, pos, placement, rand, 2)) {
            fillBelow(reader, pos.above(1), size, rotation, new Block[]{Blocks.DIRT});
            return true;
        }
        return false;
    }

    @Override
    protected ResourceLocation getStructure(final Random rand) {
        return rand.nextBoolean() ? STRUCTURE_LIMESTONE : STRUCTURE_MARBLE;
    }
}
