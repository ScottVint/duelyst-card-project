package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.players.Player;
import utils.BasicObjectBuilders;

/**
 * Represents the 9x5 game board.
 */
public class Board {

	public static final int BOARD_WIDTH = 9;
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

	//TODO move into a more general class

	/** Creates a small pause.
	 * This is necessary for the tiles to load without encountering a BufferOverflow.
	 */
	private void blink() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Reset every tile to default.
	 */
	public void clearSelection(ActorRef out) {
		for (int x = 0; x < BOARD_WIDTH; x++) {
			for (int y = 0; y < BOARD_HEIGHT; y++) {
				BasicCommands.drawTile(out, tiles[x][y], 0);
				blink();
			}
		}
	}

	/**
	 * Highlight valid movement tiles for a selected unit.
	 */
	public void highlightMovement(ActorRef out, Tile startTile) {
		int sx = startTile.getTilex();
		int sy = startTile.getTiley();

		// Cardinal directions: up to 2 steps
		int[][] cardinalDirs = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
		for (int[] dir : cardinalDirs) {
			for (int step = 1; step <= 2; step++) {
				int nx = sx + dir[0] * step;
				int ny = sy + dir[1] * step;

				if (nx < 0 || nx >= BOARD_WIDTH || ny < 0 || ny >= BOARD_HEIGHT) break;

				Tile nextTile = tiles[nx][ny];
				if (nextTile.getUnit() != null) break;

				BasicCommands.drawTile(out, nextTile, 1);
				blink();
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
				blink();
			}
		}
	}

	/**
	 * Highlight all valid summon tiles adjacent to any friendly unit.
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
							blink();
						}
					}
				}
			} // OH MY GOD WHAT
		}
	}
}