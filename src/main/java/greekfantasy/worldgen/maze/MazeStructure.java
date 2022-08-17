package greekfantasy.worldgen.maze;

import com.mojang.serialization.Codec;
import greekfantasy.GreekFantasy;
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

import javax.annotation.Nullable;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MazeStructure extends StructureFeature<MazeConfiguration> {

    public MazeStructure(Codec<MazeConfiguration> codec) {
        super(codec, PieceGeneratorSupplier.simple(MazeStructure::checkLocation, MazeStructure::generatePieces));
    }

    /**
     * Determines if the structure can generate at the given location.
     * @param context the piece generator supplier context
     * @return true if the structure can generate at the given location
     */
    private static boolean checkLocation(PieceGeneratorSupplier.Context<MazeConfiguration> context) {
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
        worldgenrandom.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
        double probability = (context.config()).getProbability();
        if (worldgenrandom.nextDouble() >= probability) {
            return false;
        }
        return context.validBiome().test(context.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(context.chunkPos().getMiddleBlockX()), QuartPos.fromBlock(50), QuartPos.fromBlock(context.chunkPos().getMiddleBlockZ())));
    }

    /**
     * Generates all of the pieces for the structure and adds them to the builder.
     * @param builder the structure pieces builder
     * @param context the piece generator context
     * @see #generateMaze(BlockPos, int, PieceGenerator.Context)
     */
    private static void generatePieces(StructurePiecesBuilder builder, PieceGenerator.Context<MazeConfiguration> context) {
        int offsetY = context.random().nextIntBetweenInclusive(0, 78) - 16;

        List<MazePiece> pieces = generateMaze(context.chunkPos().getBlockAt(0, 0, 0), offsetY, context);

        for (MazePiece piece : pieces) {
            builder.addPiece(piece);
        }

        builder.offsetPiecesVertically(offsetY);
    }

    /**
     * Creates each MazePiece with the correct orientation and openings.
     * Also creates the stairway and entrance pieces to the surface.
     * @param origin the origin block position
     * @param offsetY the number of blocks to offset vertically, used to create stairways
     * @param context the piece generator context
     * @return the list of configured MazePieces
     * @see #depthFirst(MazeConfiguration, Point, boolean[][], MazePiece[][], RandomSource)
     */
    public static List<MazePiece> generateMaze(BlockPos origin, int offsetY, PieceGenerator.Context<MazeConfiguration> context) {
        //// GATHER VALUES ////

        MazeConfiguration config = context.config();
        RandomSource rand = context.random();
        int countX = config.getPieceCountX();
        int countZ = config.getPieceCountZ();
        float roomChance = (float) config.getRoomChance();

        //// INITIALIZE MAZE ////

        List<MazePiece> mazeRooms = new ArrayList<>();
        MazePiece[][] tiles = new MazePiece[countX][countZ];
        boolean[][] visited = new boolean[countX][countZ];

        // Create each maze piece immediately, with no explicitly defined openings or variant
        for (int i = 0; i < countX; i++) {
            for (int j = 0; j < countZ; j++) {
                tiles[i][j] = MazePiece.create(origin, i, j);
            }
        }

        //// PREPARE FOR MAZE GENERATION ////

        // determine location of boss rooms
        int bX = countX / 2;
        int bY = countZ / 2;
        bX += rand.nextInt(bX / 2) - bX / 4;
        bY += rand.nextInt(bY / 2) - bY / 4;
        // connect the boss room and maze piece to the north
        tiles[bX][bY - 1].withOpenings(false, false, true, false);
        // set boss rooms with variant and orientation
        tiles[bX][bY].withOpenings(false, true, true, false).withVariant(MazePiece.Variant.BOSS_ROOM_ENTRANCE).withDirection(Direction.WEST);
        tiles[bX + 1][bY].withOpenings(false, true, false, true).withVariant(MazePiece.Variant.BOSS_ROOM).withDirection(Direction.NORTH);
        tiles[bX][bY + 1].withOpenings(true, true, false, false).withVariant(MazePiece.Variant.BOSS_ROOM).withDirection(Direction.SOUTH);
        tiles[bX + 1][bY + 1].withOpenings(true, false, false, true).withVariant(MazePiece.Variant.BOSS_ROOM).withDirection(Direction.EAST);
        // mark boss rooms as visited to ensure they are not replaced
        visited[bX][bY] = visited[bX + 1][bY] = visited[bX][bY + 1] = visited[bX + 1][bY + 1] = true;

        // Randomly select rooms to connect to a random neighbor
        // Do not mark these rooms as visited, since their connections are not final
        double connectionRatio = config.getRandomConnectionRatio();
        for(int i = 0, n = (int)Math.floor(countX * countZ * connectionRatio); i < n; i++) {
            Point p1 = new Point(1 + rand.nextInt(countX - 2), 1 + rand.nextInt(countZ - 2));
            Point p2 = unvisited(config, p1, visited, rand);
            // connect the two points
            if(p2 != null) {
                connect(p1, p2, tiles);
            }
        }

        //// GENERATE MAZE ////

        // This method calls itself recursively until every tile has been visited
        depthFirst(config, new Point(bX, bY - 1), visited, tiles, rand);

        //// POST-PROCESS MAZE ////
        // Create entrances to connect to the outside.
        // The uses of rand.nextInt ensure that the entrances are close to the center of their respective side.
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

        //// BAKE MAZE PIECES ////
        // add all tiles to the list
        for (int i = 0; i < countX; i++) {
            for (int j = 0; j < countZ; j++) {
                mazeRooms.add(tiles[i][j].deadEndOrRoom(rand, roomChance).bake(rand));
            }
        }

        return mazeRooms;
    }

    /**
     * Creates stairway and entrance pieces connected to the given piece.
     * @param context the piece generator context
     * @param origin the origin block of the structure
     * @param vertex the indices of the piece that will become an entrance
     * @param offsetY the vertical offset, used to determine the number of stairways
     * @param piece the piece to attach to the stairways
     * @param direction the direction from the maze piece to the entrance stairway (away from the maze)
     * @return a collection of newly created and baked maze pieces (not including the maze piece from method args)
     */
    private static Collection<MazePiece> entrance(final PieceGenerator.Context<MazeConfiguration> context, final BlockPos origin, final Point vertex, int offsetY, final MazePiece piece, final Direction direction) {
        List<MazePiece> pieces = new ArrayList<>();
        // add opening to the existing piece
        piece.withOpenings(piece.getOpenings().a || direction == Direction.NORTH, piece.getOpenings().b || direction == Direction.EAST, piece.getOpenings().c || direction == Direction.SOUTH, piece.getOpenings().d || direction == Direction.WEST);
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

    /**
     * Recursive maze generation algorithm that calls itself until it has visited all the points.
     * The algorithm connects a single point to the last point and chooses the next point to visit.
     * This method is guaranteed to produce a maze with one solution and no loops or isolated segments.
     * @param configuration the maze configuration
     * @param vertex the next point to visit
     * @param visited the array of visited flags
     * @param tiles the array of maze pieces
     * @param rand the random source
     */
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

    /**
     * Selects a random unvisited point adjacent to the given point
     * @param configuration the maze configuration
     * @param vertex the point
     * @param visited the array of visited flags
     * @param rand the random source
     * @return a randomly selected neighbor, or null if there are no adjacent neighbors
     */
    @Nullable
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

    /**
     * Connects two points by adding openings between the two points.
     * Does not check that the points are adjacent or within bounds.
     * @param p1 the first point
     * @param p2 the second point
     * @param tiles the maze piece array
     */
    private static void connect(final Point p1, final Point p2, final MazePiece[][] tiles) {
        // connect the rooms at these points
        MazePiece temp;
        if (p1.x == p2.x && p1.y > p2.y) { // north
            temp = tiles[p1.x][p1.y];
            temp.withOpenings(true, temp.getOpenings().b, temp.getOpenings().c, temp.getOpenings().d);
            temp = tiles[p2.x][p2.y];
            temp.withOpenings(temp.getOpenings().a, temp.getOpenings().b, true, temp.getOpenings().d);
        } else if (p1.x < p2.x && p1.y == p2.y) { // east
            temp = tiles[p1.x][p1.y];
            temp.withOpenings(temp.getOpenings().a, true, temp.getOpenings().c, temp.getOpenings().d);
            temp = tiles[p2.x][p2.y];
            temp.withOpenings(temp.getOpenings().a, temp.getOpenings().b, temp.getOpenings().c, true);
        } else if (p1.x == p2.x && p1.y < p2.y) { // south
            temp = tiles[p1.x][p1.y];
            temp.withOpenings(temp.getOpenings().a, temp.getOpenings().b, true, temp.getOpenings().d);
            temp = tiles[p2.x][p2.y];
            temp.withOpenings(true, temp.getOpenings().b, temp.getOpenings().c, temp.getOpenings().d);
        } else if (p1.x > p2.x && p1.y == p2.y) { // west
            temp = tiles[p1.x][p1.y];
            temp.withOpenings(temp.getOpenings().a, temp.getOpenings().b, temp.getOpenings().c, true);
            temp = tiles[p2.x][p2.y];
            temp.withOpenings(temp.getOpenings().a, true, temp.getOpenings().c, temp.getOpenings().d);
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
