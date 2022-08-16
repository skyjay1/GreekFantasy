package greekfantasy.worldgen.maze;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        int offsetY = context.random().nextIntBetweenInclusive(0, 78) - 16;

        List<MazePiece> pieces = generateMaze(context.chunkPos().getBlockAt(0, 0, 0), offsetY, context);

        for (MazePiece piece : pieces) {
            builder.addPiece(piece);
        }

        builder.offsetPiecesVertically(offsetY + 100);
    }

    public static List<MazePiece> generateMaze(BlockPos origin, int offsetY, PieceGenerator.Context<MazeConfiguration> context) {
        List<MazePiece> mazeRooms = new ArrayList<>();

        MazeConfiguration config = context.config();
        RandomSource rand = context.random();
        int countX = config.getPieceCountX();
        int countZ = config.getPieceCountZ();
        float roomChance = (float) config.getRoomChance();

        MazePiece[][] tiles = new MazePiece[countX][countZ];
        boolean[][] visited = new boolean[countX][countZ];

        for (int i = 0; i < countX; i++) {
            for (int j = 0; j < countZ; j++) {
                tiles[i][j] = MazePiece.create(origin, i, j);
            }
        }
        // place boss room
        int bX = countX / 2;
        int bY = countZ / 2;
        bX += rand.nextInt(bX / 2) - bX / 4;
        bY += rand.nextInt(bY / 2) - bY / 4;
        tiles[bX][bY - 1].withWalls(true, false, true, false);
        tiles[bX][bY].withWalls(false, true, true, false).withVariant(MazePiece.Variant.BOSS_ROOM_ENTRANCE).withDirection(Direction.WEST);
        tiles[bX + 1][bY].withWalls(false, true, false, true).withVariant(MazePiece.Variant.BOSS_ROOM).withDirection(Direction.NORTH);
        tiles[bX][bY + 1].withWalls(true, true, false, false).withVariant(MazePiece.Variant.BOSS_ROOM).withDirection(Direction.SOUTH);
        tiles[bX + 1][bY + 1].withWalls(true, false, false, true).withVariant(MazePiece.Variant.BOSS_ROOM).withDirection(Direction.EAST);
        visited[bX][bY] = visited[bX + 1][bY] = visited[bX][bY + 1] = visited[bX + 1][bY + 1] = true;
        // depth-first maze generation
        depthFirst(config, new Point(bX, bY - 1), visited, tiles, rand);
        // Create entrances to connect to the outside.
        // The uses of rand.nextInt ensure that the entrances are closer to the center of their respective side.
        // West entrance
        final Point startW = new Point(0, countZ / 2 + rand.nextInt(countZ / 2) - countZ / 4);
        mazeRooms.addAll(entrance(context, origin, startW, offsetY, tiles[startW.x][startW.y], Direction.WEST));
        // North entrance
        final Point startN = new Point(countX / 2 + rand.nextInt(countX / 2) - countX / 4, 0);
        mazeRooms.addAll(entrance(context, origin, startN, offsetY, tiles[startN.x][startN.y], Direction.NORTH));
        // East entrance
        final Point startE = new Point(countX - 1, countZ / 2 + rand.nextInt(countZ / 2) - countZ / 4);
        mazeRooms.addAll(entrance(context, origin, startE, offsetY, tiles[startE.x][startE.y], Direction.EAST));
        // South entrance
        final Point startS = new Point(countX / 2 + rand.nextInt(countX / 2) - countX / 4, countZ - 1);
        mazeRooms.addAll(entrance(context, origin, startS, offsetY, tiles[startS.x][startS.y], Direction.SOUTH));
        // add all tiles to the display
        for (int i = 0; i < countX; i++) {
            for (int j = 0; j < countZ; j++) {
                mazeRooms.add(tiles[i][j].deadEndOrRoom(rand, roomChance).bake(rand));
            }
        }

        return mazeRooms;
    }

    private static Collection<MazePiece> entrance(final PieceGenerator.Context context, final BlockPos origin, final Point vertex, int offsetY, final MazePiece piece, final Direction direction) {
        List<MazePiece> pieces = new ArrayList<>();
        // add opening to the existing piece
        piece.withWalls(piece.getOpenings().a || direction == Direction.NORTH, piece.getOpenings().b || direction == Direction.EAST, piece.getOpenings().c || direction == Direction.SOUTH, piece.getOpenings().d || direction == Direction.WEST);
        // use reversed direction for pieces
        Direction dOpposite = direction.getOpposite();
        // determine column point
        Point point = new Point(vertex.x + direction.getStepX(), vertex.y + direction.getStepZ());
        // add lower entrance
        MazePiece lowerEntrance = MazePiece.create(origin, point.x, point.y)
                .withVariant(MazePiece.Variant.LOWER_ENTRANCE)
                .withDirection(dOpposite)
                .bake(context.random());
        pieces.add(lowerEntrance);
        // determine stairways
        BlockPos pos = lowerEntrance.getBoundingBox().getCenter();
        int height = (context.chunkGenerator().getBaseHeight(pos.getX(), pos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor()));
        int count = (height - offsetY) / MazePiece.HEIGHT;
        // add each stairway
        for (int i = 1; i < count; i++) {
            MazePiece stairway = MazePiece.create(origin, point.x, i, point.y)
                    .withVariant(MazePiece.Variant.STAIRWAY)
                    .withDirection(dOpposite)
                    .bake(context.random());
            pieces.add(stairway);
        }
        // add upper entrance
        MazePiece upperEntrance = MazePiece.create(origin, point.x, count, point.y)
                .withVariant(MazePiece.Variant.UPPER_ENTRANCE)
                .withDirection(dOpposite)
                .bake(context.random());
        pieces.add(upperEntrance);

        return pieces;
    }

    private static void depthFirst(final MazeConfiguration configuration, final Point vertex, final boolean[][] visited, final MazePiece[][] tiles, final RandomSource rand) {
        // mark visited
        visited[vertex.x][vertex.y] = true;
        // choose an unvisited neighbor to start recursive generation
        Point next = unvisited(configuration, vertex, visited, rand);
        while (next != null) {
            connect(vertex, next, tiles);
            depthFirst(configuration, next, visited, tiles, rand);
            next = unvisited(configuration, vertex, visited, rand);
        }
    }

    private static Point unvisited(final MazeConfiguration configuration, final Point vertex, final boolean[][] visited, final RandomSource rand) {
        List<Point> neighborList = new ArrayList<>();
        if (vertex.x < configuration.getPieceCountX() - 1) neighborList.add(new Point(vertex.x + 1, vertex.y));
        if (vertex.y < configuration.getPieceCountZ() - 1) neighborList.add(new Point(vertex.x, vertex.y + 1));
        if (vertex.x > 0) neighborList.add(new Point(vertex.x - 1, vertex.y));
        if (vertex.y > 0) neighborList.add(new Point(vertex.x, vertex.y - 1));
        // return the first unvisited member of the list
        if (!neighborList.isEmpty()) {
            shuffle(neighborList, rand);
            for (final Point p : neighborList) {
                if (!visited[p.x][p.y]) return p;
            }
        }
        return null;
    }

    private static void connect(final Point p1, final Point p2, final MazePiece[][] tiles) {
        // connect the rooms at these points
        MazePiece temp;
        if (p1.x == p2.x && p1.y > p2.y) { // north
            temp = tiles[p1.x][p1.y];
            temp.withWalls(true, temp.getOpenings().b, temp.getOpenings().c, temp.getOpenings().d);
            temp = tiles[p2.x][p2.y];
            temp.withWalls(temp.getOpenings().a, temp.getOpenings().b, true, temp.getOpenings().d);
        } else if (p1.x < p2.x && p1.y == p2.y) { // east
            temp = tiles[p1.x][p1.y];
            temp.withWalls(temp.getOpenings().a, true, temp.getOpenings().c, temp.getOpenings().d);
            temp = tiles[p2.x][p2.y];
            temp.withWalls(temp.getOpenings().a, temp.getOpenings().b, temp.getOpenings().c, true);
        } else if (p1.x == p2.x && p1.y < p2.y) { // south
            temp = tiles[p1.x][p1.y];
            temp.withWalls(temp.getOpenings().a, temp.getOpenings().b, true, temp.getOpenings().d);
            temp = tiles[p2.x][p2.y];
            temp.withWalls(true, temp.getOpenings().b, temp.getOpenings().c, temp.getOpenings().d);
        } else if (p1.x > p2.x && p1.y == p2.y) { // west
            temp = tiles[p1.x][p1.y];
            temp.withWalls(temp.getOpenings().a, temp.getOpenings().b, temp.getOpenings().c, true);
            temp = tiles[p2.x][p2.y];
            temp.withWalls(temp.getOpenings().a, true, temp.getOpenings().c, temp.getOpenings().d);
        }
    }

    // variation of Collections#shuffle that uses RandomSource instead of Random
    private static void shuffle(List<?> list, RandomSource rnd) {
        int size = list.size();
        for (int i = size; i > 1; i--) {
            Collections.swap(list, i - 1, rnd.nextInt(i));
        }
    }
}
