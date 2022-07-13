package greekfantasy.worldgen;

import com.mojang.serialization.Codec;
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

public class ArachnePitFeature extends StructureFeature<JigsawConfiguration> {

    public ArachnePitFeature(Codec<JigsawConfiguration> codec) {
        super(codec, (context) -> {
            ChunkPos chunkpos = context.chunkPos();
            int x = chunkpos.x >> 4;
            int z = chunkpos.z >> 4;
            WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
            worldgenrandom.setSeed((long)(x ^ z << 4) ^ context.seed());
            worldgenrandom.nextInt();
            int y = worldgenrandom.nextInt(-60, 128);
            BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), y, chunkpos.getMinBlockZ());
            if(!checkLocation(context, blockpos)) {
                return Optional.empty();
            }
            Pools.bootstrap();
            // params: context, pieceGeneratorSupplier, position, buildInChunk?, buildAtSurface
            return JigsawPlacement.addPieces(context, PoolElementStructurePiece::new, blockpos, true, false);
        });
    }

    private static boolean checkLocation(PieceGeneratorSupplier.Context<JigsawConfiguration> context, BlockPos pos) {
        // check the position is below world surface
        int x = pos.getX();
        int z = pos.getZ();
        int y = context.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
        if(pos.getY() - y < 8) {
            return false;
        }
        // random chance
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setSeed((long)(x ^ (long) z << 4) ^ context.seed());
        worldgenrandom.nextInt();
        if(worldgenrandom.nextInt(3) != 0) {
            return false;
        }
        return true;
    }
}
