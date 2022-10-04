package greekfantasy.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import greekfantasy.GFRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;

public class OceanVillageStructure extends Structure {

    public static final Codec<OceanVillageStructure> CODEC = RecordCodecBuilder.<OceanVillageStructure>mapCodec(instance ->
            instance.group(Structure.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 7).fieldOf("size").forGetter(structure -> structure.maxDepth),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    Codec.intRange(0, 128).optionalFieldOf("min_distance_below_sea_level", 16).forGetter(structure -> structure.minDistanceBelowSeaLevel)
            ).apply(instance, OceanVillageStructure::new)).codec();

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int maxDepth;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final int minDistanceBelowSeaLevel;

    public OceanVillageStructure(Structure.StructureSettings settings, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName,
                                 int maxDepth, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter,
                                 int minDistanceBelowSeaLevel) {
        super(settings);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.minDistanceBelowSeaLevel = minDistanceBelowSeaLevel;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(Structure.GenerationContext context) {

        ChunkPos chunkpos = context.chunkPos();
        int i = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), i, chunkpos.getMinBlockZ());

        if (!isBelowSeaLevel(context, blockpos, this.minDistanceBelowSeaLevel)) {
            return Optional.empty();
        }

        return JigsawPlacement.addPieces(context, this.startPool, this.startJigsawName, this.maxDepth, blockpos, false, this.projectStartToHeightmap, this.maxDistanceFromCenter);
    }

    @Override
    public StructureType<?> type() {
        return GFRegistry.StructureReg.OCEAN_VILLAGE.get();
    }

    private static boolean isBelowSeaLevel(Structure.GenerationContext context, final BlockPos origin, final int minBlocksBelowSeaLevel) {
        int startHeight = context.chunkGenerator().getBaseHeight(origin.getX(), origin.getZ(), Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        return context.chunkGenerator().getSeaLevel() - startHeight >= minBlocksBelowSeaLevel;
    }
}
