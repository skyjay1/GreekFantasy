package greekfantasy.worldgen;

import com.mojang.serialization.Codec;
import greekfantasy.GreekFantasy;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;

import java.util.Optional;

public class OceanVillageFeature extends StructureFeature<JigsawConfiguration> {

    public OceanVillageFeature(Codec<JigsawConfiguration> codec) {
        super(codec, (context) -> {
            ChunkPos chunkpos = context.chunkPos();
            int x = chunkpos.getMinBlockX();
            int z = chunkpos.getMinBlockZ();
            int y = context.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor());
            BlockPos blockpos = new BlockPos(x, y, z);
            if(!checkLocation(context, blockpos)) {
                return Optional.empty();
            }
            Pools.bootstrap();
            // params: context, pieceGeneratorSupplier, position, buildInChunk?, buildAtSurfaceHeight
            return JigsawPlacement.addPieces(context, PoolElementStructurePiece::new, blockpos, true, false);
        });
    }

    private static boolean checkLocation(PieceGeneratorSupplier.Context<JigsawConfiguration> context, BlockPos pos) {
        // check the position is below water surface
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if(context.chunkGenerator().getSeaLevel() - y < 12) {
            return false;
        }
        // random chance
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setSeed((long)(x ^ (long) z << 4) ^ context.seed());
        worldgenrandom.nextInt();
        if(worldgenrandom.nextInt(100) < 55) {
            return false;
        }
        //GreekFantasy.LOGGER.debug("Ocean Village at " + pos.toShortString() + "; sea level=" + context.chunkGenerator().getSeaLevel());
        return true;
    }
}
