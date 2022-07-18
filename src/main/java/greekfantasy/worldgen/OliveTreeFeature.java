package greekfantasy.worldgen;


import com.mojang.serialization.Codec;
import greekfantasy.GreekFantasy;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class OliveTreeFeature extends Feature<TreeConfiguration> {

    private static final ResourceLocation[] OLIVE_TREES = {
            new ResourceLocation(GreekFantasy.MODID, "olive_tree/olive_tree_0"),
            new ResourceLocation(GreekFantasy.MODID, "olive_tree/olive_tree_1"),
            new ResourceLocation(GreekFantasy.MODID, "olive_tree/olive_tree_2")
    };

    public OliveTreeFeature(final Codec<TreeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<TreeConfiguration> context) {
        // rotation / mirror
        Mirror mirror = Mirror.NONE;
        Rotation rotation = Rotation.getRandom(context.random());

        // template for tree
        final StructureManager manager = context.level().getLevel().getStructureManager();
        final StructureTemplate template = manager.getOrCreate(Util.getRandom(OLIVE_TREES, context.random()));

        // position for tree
        final BlockPos offset = new BlockPos(-3, 0, -3);
        BlockPos pos = context.origin().offset(offset.rotate(rotation));

        // placement settings
        BoundingBox mbb = new BoundingBox(pos.getX() - 8, pos.getY() - 16, pos.getZ() - 8, pos.getX() + 8, pos.getY() + 16, pos.getZ() + 8);
        StructurePlaceSettings placement = new StructurePlaceSettings()
                .setRotation(rotation).setMirror(mirror).setRandom(context.random()).setBoundingBox(mbb)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR)
                .addProcessor(LocStructureProcessor.REQUIRE_AIR);
        // actually build using the template
        template.placeInWorld(context.level(), pos, pos, placement, context.random(), 2);
        return true;
    }
}
