package greekfantasy.worldgen.maze;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.RandomSource;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MazeGenerator {
	
	public static Map<Point, MazePiece> generateMaze(BlockPos origin, MazeConfiguration config, final RandomSource rand) {
		Map<Point, MazePiece> mazeRooms = new HashMap<>();

		int countX = config.getPieceCountX();
		int countZ = config.getPieceCountZ();

		MazePiece[][] tiles = new MazePiece[countX][countZ];
		boolean[][] visited = new boolean[countX][countZ];

		for(int i = 0; i < countX; i++) {
			for(int j = 0; j < countZ; j++) {
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
		tiles[bX][bY + 1].withWalls(true, true, false, false).withVariant(MazePiece.Variant.BOSS_ROOM).withDirection(Direction.WEST);
		tiles[bX + 1][bY + 1].withWalls(true, false, false, true).withVariant(MazePiece.Variant.BOSS_ROOM).withDirection(Direction.SOUTH);
		visited[bX][bY] = visited[bX + 1][bY] = visited[bX][bY + 1] = visited[bX + 1][bY + 1] = true;
		// depth-first maze generation
		depthFirst(config, new Point(bX, bY - 1), visited, tiles, rand);
		// Create entrances to connect to the outside.
		// The uses of rand.nextInt ensure that the entrances are closer to the center of their respective side.
		// West entrance
		final Point startW = new Point(0, countZ / 2 + rand.nextInt(countZ / 2) - countZ / 4);
		tiles[startW.x][startW.y].withWalls(tiles[startW.x][startW.y].getWalls().a, tiles[startW.x][startW.y].getWalls().b, tiles[startW.x][startW.y].getWalls().c, true);
		// North entrance
		final Point startN = new Point(countX / 2 + rand.nextInt(countX / 2) - countX / 4, 0);
		tiles[startN.x][startN.y].withWalls(tiles[startN.x][startN.y].getWalls().a, tiles[startN.x][startN.y].getWalls().b, true, tiles[startN.x][startN.y].getWalls().d);
		// East entrance
		final Point startE = new Point(countX - 1, countZ / 2 + rand.nextInt(countZ / 2) - countZ / 4);
		tiles[startE.x][startE.y].withWalls(tiles[startE.x][startE.y].getWalls().a, true, tiles[startE.x][startE.y].getWalls().c, tiles[startE.x][startE.y].getWalls().d);
		// South entrance
		final Point startS = new Point(countX / 2 + rand.nextInt(countX / 2) - countX / 4, countZ - 1);
		tiles[startS.x][startS.y].withWalls(true, tiles[startS.x][startS.y].getWalls().b, tiles[startS.x][startS.y].getWalls().c, tiles[startS.x][startS.y].getWalls().d);
		// add all tiles to the display
		for(int i = 0; i < countX; i++) {
			for(int j = 0; j < countZ; j++) {
				mazeRooms.put(new Point(i, j), tiles[i][j].deadEndOrRoom(rand, 0.35F).bake(rand));
			}
		}

		return mazeRooms;
	}
	
	private static void depthFirst(final MazeConfiguration configuration, final Point vertex, final boolean[][] visited, final MazePiece[][] tiles, final RandomSource rand) {
		// mark visited
		visited[vertex.x][vertex.y] = true;
		// choose an unvisited neighbor to start recursive generation
		Point next = unvisited(configuration, vertex, visited, rand);
		while(next != null) {
			connect(vertex, next, tiles);
			depthFirst(configuration, next, visited, tiles, rand);
			next = unvisited(configuration, vertex, visited, rand);
		}
	}
	
	private static Point unvisited(final MazeConfiguration configuration, final Point vertex, final boolean[][] visited, final RandomSource rand) {
		List<Point> neighborList = new ArrayList<>();
		if(vertex.x < configuration.getPieceCountX() - 1) neighborList.add(new Point(vertex.x + 1, vertex.y));
		if(vertex.y < configuration.getPieceCountZ() - 1) neighborList.add(new Point(vertex.x, vertex.y + 1));
		if(vertex.x > 0) neighborList.add(new Point(vertex.x - 1, vertex.y));
		if(vertex.y > 0) neighborList.add(new Point(vertex.x, vertex.y - 1));
		// return the first unvisited member of the list
		if(!neighborList.isEmpty()) {
			shuffle(neighborList, rand);
			for(final Point p : neighborList) {
				if(!visited[p.x][p.y]) return p;
			}
		}
		return null;
	}
	
	private static void connect(final Point p1, final Point p2, final MazePiece[][] tiles) {
		// connect the rooms at these points
		MazePiece temp;
		if(p1.x == p2.x && p1.y < p2.y) { // north
			temp = tiles[p1.x][p1.y];
			temp.withWalls(true, temp.getWalls().b, temp.getWalls().c, temp.getWalls().d);
			temp = tiles[p2.x][p2.y];
			temp.withWalls(temp.getWalls().a, temp.getWalls().b, true, temp.getWalls().d);
		} else if (p1.x < p2.x && p1.y == p2.y) { // east
			temp = tiles[p1.x][p1.y];
			temp.withWalls(temp.getWalls().a, true, temp.getWalls().c, temp.getWalls().d);
			temp = tiles[p2.x][p2.y];
			temp.withWalls(temp.getWalls().a, temp.getWalls().b, temp.getWalls().c, true);
		} else if (p1.x == p2.x && p1.y > p2.y) { // south
			temp = tiles[p1.x][p1.y];
			temp.withWalls(temp.getWalls().a, temp.getWalls().b, true, temp.getWalls().d);
			temp = tiles[p2.x][p2.y];
			temp.withWalls(true, temp.getWalls().b, temp.getWalls().c, temp.getWalls().d);
		} else if (p1.x > p2.x && p1.y == p2.y) { // west
			temp = tiles[p1.x][p1.y];
			temp.withWalls(temp.getWalls().a, temp.getWalls().b, temp.getWalls().c, true);
			temp = tiles[p2.x][p2.y];
			temp.withWalls(temp.getWalls().a, true, temp.getWalls().c, temp.getWalls().d);
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
