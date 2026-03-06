package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.players.Player;
import utils.BasicObjectBuilders;

/**
 * Represents the 9x5 game board, holding a grid of Tile objects.
 * Each tile is initialised via {@link BasicObjectBuilders#loadTile(int, int)}
 * using grid coordinates (x = column 0-8, y = row 0-4).
 *
 * @author Minghao
 */
public class Board {

	/** Number of columns on the board. */
	public static final int BOARD_WIDTH = 9;
	/** Number of rows on the board. */
	public static final int BOARD_HEIGHT = 5;

	private Tile[][] tiles;

	public Board() {
		tiles = new Tile[BOARD_WIDTH][BOARD_HEIGHT];
		for (int x = 0; x < BOARD_WIDTH; x++) {
			for (int y = 0; y < BOARD_HEIGHT; y++) {
				tiles[x][y] = BasicObjectBuilders.loadTile(x, y);
			}
		}
	}

	public Tile getTile(int x, int y) {
		return tiles[x][y];
	}

	public Tile[][] getTiles() {
		return tiles;
	}

	public int getX() {
		return BOARD_WIDTH;
	}

	public int getY() {
		return BOARD_HEIGHT;
	}

	/**
	 * Resets every tile to its default (unhighlighted) state.
	 * Call before applying new highlights to avoid stale state.
	 * @author Scott
	 */
	public void clearSelection(ActorRef out) {
		for (Tile[] row : tiles) {
			for (Tile tile : row) {
				BasicCommands.drawTile(out, tile, 0); // grey
			}
		}
	}

	/**
	 * Highlights valid movement tiles (white, mode 1) for the unit on startTile.
	 * Cardinal directions: up to 2 steps with path blocking.
	 * Diagonal directions: exactly 1 step, no path blocking.
	 * Only empty tiles are highlighted.
	 * @author Minghao
	 */
	public void highlightMovement(ActorRef out, Tile startTile) {
		int sx = startTile.getTilex();
		int sy = startTile.getTiley();

		// Cardinal directions: up to 2 steps with path blocking
		int[][] cardinalDirs = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
		for (int[] dir : cardinalDirs) {
			for (int step = 1; step <= 2; step++) {
				int nx = sx + dir[0] * step;
				int ny = sy + dir[1] * step;
				if (nx < 0 || nx >= BOARD_WIDTH || ny < 0 || ny >= BOARD_HEIGHT) break;
				Tile nextTile = tiles[nx][ny];
				if (nextTile.getUnit() != null) break; // path blocked
				BasicCommands.drawTile(out, nextTile, 1);
			}
		}

		// Diagonal directions: exactly 1 step
		int[][] diagonalDirs = { {1, 1}, {1, -1}, {-1, 1}, {-1, -1} };
		for (int[] dir : diagonalDirs) {
			int nx = sx + dir[0];
			int ny = sy + dir[1];
			if (nx < 0 || nx >= BOARD_WIDTH || ny < 0 || ny >= BOARD_HEIGHT) continue;
			Tile diagTile = tiles[nx][ny];
			if (diagTile.getUnit() == null) {
				BasicCommands.drawTile(out, diagTile, 1);
			}
		}
	}

	/**
	 * Highlights all empty tiles adjacent (8-directional) to any friendly unit
	 * owned by player1 in green (mode 2). Used for creature card summon targeting.
	 * @author Minghao
	 */
	public void highlightSummonTiles(ActorRef out, Player player1) {
		for (int x = 0; x < BOARD_WIDTH; x++) {
			for (int y = 0; y < BOARD_HEIGHT; y++) {
				Unit unit = tiles[x][y].getUnit();
				if (unit == null || unit.getOwner() != player1) continue;

				for (int dx = -1; dx <= 1; dx++) {
					for (int dy = -1; dy <= 1; dy++) {
						if (dx == 0 && dy == 0) continue;
						int nx = x + dx;
						int ny = y + dy;
						if (nx < 0 || nx >= BOARD_WIDTH || ny < 0 || ny >= BOARD_HEIGHT) continue;
						if (tiles[nx][ny].getUnit() == null) {
							BasicCommands.drawTile(out, tiles[nx][ny], 2);
						}
					}
				}
			}
		}
	}
}
