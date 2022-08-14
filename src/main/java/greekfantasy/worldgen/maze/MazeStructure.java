package greekfantasy.worldgen.maze;

import com.mojang.serialization.Codec;
import greekfantasy.GreekFantasy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.awt.Point;
import java.util.Map;

public class MazeStructure extends StructureFeature<MazeConfiguration> {
    public MazeStructure(Codec<MazeConfiguration> codec) {
        super(codec, PieceGeneratorSupplier.simple(MazeStructure::checkLocation, MazeStructure::generatePieces));
    }

    private static boolean checkLocation(PieceGeneratorSupplier.Context<MazeConfiguration> context) {
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
        double probability = (context.config()).getProbability();
        if (worldgenrandom.nextDouble() >= probability) {
            return false;
        }
        return context.validBiome().test(context.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(context.chunkPos().getMiddleBlockX()), QuartPos.fromBlock(50), QuartPos.fromBlock(context.chunkPos().getMiddleBlockZ())));
    }

    private static void generatePieces(StructurePiecesBuilder builder, PieceGenerator.Context<MazeConfiguration> context) {
        //Map<Point, MazePiece> pieces = MazeGenerator.generateMaze(context.chunkPos().getBlockAt(0, 0, 0), context.config(), context.random());

        //for (MazePiece piece : pieces.values()) {
            //builder.addPiece(piece);
        //}

        BlockPos origin = context.chunkPos().getBlockAt(0, 0, 0);

        // dead end
        builder.addPiece(MazePiece.create(origin, 0, 0).withWalls(false, false, true, false).bake(context.random()));
        builder.addPiece(MazePiece.create(origin, 2, 0).withWalls(true, false, false, false).bake(context.random()));
        builder.addPiece(MazePiece.create(origin, 4, 0).withWalls(false, false, false, true).bake(context.random()));
        builder.addPiece(MazePiece.create(origin, 6, 0).withWalls(false, true, false, false).bake(context.random()));

        // corner
        builder.addPiece(MazePiece.create(origin, 0, 2).withWalls(false, false, true, true).bake(context.random()));
        builder.addPiece(MazePiece.create(origin, 2, 2).withWalls(true, false, false, true).bake(context.random()));
        builder.addPiece(MazePiece.create(origin, 4, 2).withWalls(true, true, false, false).bake(context.random()));
        MazePiece m;
        builder.addPiece(m = MazePiece.create(origin, 6, 2).withWalls(false, true, true, false).bake(context.random())); // BROKEN
        GreekFantasy.LOGGER.debug("m=" + m.getVariant().getSerializedName() + " o=" + m.getOrientation() + " r=" + m.getRotation() + " m=" + m.getMirror() + " p=" + origin);

        // three way
        builder.addPiece(MazePiece.create(origin, 0, 4).withWalls(false, true, true, true).bake(context.random()));
        builder.addPiece(MazePiece.create(origin, 2, 4).withWalls(true, false, true, true).bake(context.random()));
        builder.addPiece(MazePiece.create(origin, 4, 4).withWalls(true, true, false, true).bake(context.random()));
        builder.addPiece(MazePiece.create(origin, 6, 4).withWalls(true, true, true, false).bake(context.random()));

        // two way
        builder.addPiece(MazePiece.create(origin, 0, 6).withWalls(true, false, true, false).bake(context.random()));
        builder.addPiece(MazePiece.create(origin, 2, 6).withWalls(false, true, false, true).bake(context.random()));

        int i = context.chunkGenerator().getSeaLevel();
        builder.moveInsideHeights(context.random(), 140, 160);
        //builder.moveBelowSeaLevel(i, context.chunkGenerator().getMinY(), context.random(), 10);

    }
}
