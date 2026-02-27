package structures;

import structures.basic.Tile;
import utils.BasicObjectBuilders;

/**
 * Represents the 9x5 game board, holding a grid of Tile objects.
 * Each tile is initialised via {@link BasicObjectBuilders#loadTile(int, int)}
 * using grid coordinates (x = column 0-8, y = row 0-4).
 *
 * @author Minghao
 */
public class Board {

	/** Number of columns on the board. @author Minghao */
	public static final int BOARD_WIDTH = 9;
	/** Number of rows on the board. @author Minghao */
	public static final int BOARD_HEIGHT = 5;

	private Tile[][] tiles;

	/**
	 * Constructs the board and initialises every tile using
	 * {@link BasicObjectBuilders#loadTile(int, int)}.
	 *
	 * @author Minghao
	 */
	public Board() {
		tiles = new Tile[BOARD_WIDTH][BOARD_HEIGHT];
		for (int x = 0; x < BOARD_WIDTH; x++) {
			for (int y = 0; y < BOARD_HEIGHT; y++) {
				tiles[x][y] = BasicObjectBuilders.loadTile(x, y);
			}
		}
	}

	/**
	 * Returns the tile at the given grid coordinates.
	 *
	 * @param x column index (0-8)
	 * @param y row index (0-4)
	 * @return the Tile at (x, y)
	 * @author Minghao
	 */
	public Tile getTile(int x, int y) {
		return tiles[x][y];
	}

	/**
	 * Returns the number of columns on the board.
	 * @author Minghao
	 */
	public int getX() {
		return BOARD_WIDTH;
	}

	/**
	 * Returns the number of rows on the board.
	 * @author Minghao
	 */
	public int getY() {
		return BOARD_HEIGHT;
	}

}
