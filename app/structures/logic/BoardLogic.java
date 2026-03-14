package structures.logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Board;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.players.Player;
import utils.BasicObjectBuilders;

import java.util.HashSet;
import java.util.Set;

/**
 * Store logic relating to board pathfinding and highlighting.
 */
public class BoardLogic {

	/** Creates a small pause.
	 * This is necessary for the tiles to load without encountering a BufferOverflow.
	 */
	private static void blink() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Resets every tile to its default (unhighlighted) state.
	 * Call before applying new highlights to avoid stale state.
	 *
	 * @author Scott
	 */
	public static void clearSelection(ActorRef out, Board board) {
		for (Tile[] row : board.getTiles()) {
			for (Tile tile : row) {
				BasicCommands.drawTile(out, tile, 0); // grey
				blink();
			}
		}
	}


	public static Set<Tile> findNeighbours(Tile startingTile, Board board){
		Set<Tile> neighbours = new HashSet<>();
		int[] xoffset = {1, -1 , 0, 0};
		int[] yoffset = {0, 0, 1, -1};
		for (int i = 0; i < xoffset.length; i++) {
			try {
				neighbours.add(board.getTiles()[startingTile.getTilex() + xoffset[i]][startingTile.getTiley() + yoffset[i]]);
			} catch (ArrayIndexOutOfBoundsException e) {
				continue;
			}
		}
		return neighbours;
	}

	public static Set<Tile> findValidMovement(Tile startingTile, int range, Unit unit, Board board) {
		Set<Tile> result = new HashSet<>();
		Player unitOwner = unit.getOwner();
		int startx = startingTile.getTilex();
		int starty = startingTile.getTiley();

		int maxOffset = range - 1;
		for (int x = 0; x < maxOffset; x++) {
			if (board.getTiles()[startx + x][starty].getUnit().getOwner() != unitOwner) {
				break;
			}
			for (int y = 0; y < maxOffset; y++) {
				if (board.getTiles()[startx + x][starty + y].getUnit().getOwner() != unitOwner) {
					break;
				}
				Tile currTile = board.getTiles()[startx + x][starty + y];
				result.addAll(findNeighbours(currTile, board));
			}
		}
		return result;
	}

	public static Set<Tile> findValidMovement(Tile startingTile, Unit unit, Board board) {
		return findValidMovement(startingTile, 2, unit, board);
	}


	/// Searches for all valid tiles, then highlights them.
	/// Enemy units block pathfinding.
	public static void highlightMovement(ActorRef out, Tile startingTile, Unit unit, Board board) {
		int range = 2; //unit.getMovement(); TODO create separate unit movement stats
		Set<Tile> targets = findValidMovement(startingTile, range,  unit, board);
		for (Tile target : targets) {
			BasicCommands.drawTile(out, target, 1);
			blink();
		}
	}

	/**
	 * Highlights all empty tiles adjacent (8-directional) to any friendly unit
	 * owned by player1 in green (mode 2). Used for creature card summon targeting.
	 *
	 * @author Minghao
	 */
	public static void highlightSummonTiles(ActorRef out, Player player1, Board board) {
		for (int x = 0; x < board.getX(); x++) {
			for (int y = 0; y < board.getY(); y++) {
				Unit unit = board.getTiles()[x][y].getUnit();
				if (unit == null || unit.getOwner() != player1) continue;

				for (int dx = -1; dx <= 1; dx++) {
					for (int dy = -1; dy <= 1; dy++) {
						if (dx == 0 && dy == 0) continue;
						int nx = x + dx;
						int ny = y + dy;
						if (nx < 0 || nx >= board.getX() || ny < 0 || ny >= board.getY()) continue;
						if (board.getTiles()[nx][ny].getUnit() == null) {
							BasicCommands.drawTile(out, board.getTiles()[nx][ny], 2);
						}
					}
				}
			}
		}
	}
}

