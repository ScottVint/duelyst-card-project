package structures.logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Card;
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

	/// Finds all adjacent tiles to the given Tile.
	/// Searches both cardinal and diagonal directions.
	public static Set<Tile> findAdjacentTiles(Tile startingTile, Board board) {
		Set<Tile> result = new HashSet<>();
		int x = board.getX();
		int y = board.getY();

		int[] xoffset = {1, -1, 0,  0, 1,  1, -1, -1};
		int[] yoffset = {0,  0, 1, -1, 1, -1,  1, -1};
		for (int i = 0; i < xoffset.length; i++) {
			try {
				result.add(board.getTiles()[x + xoffset[i]][y + yoffset[i]]);
			} catch (ArrayIndexOutOfBoundsException e) {
				continue;
			}
		}
		return result;
	}

	public static Set<Tile> findValidAttackUnits(Tile startingTile, Unit unit, Board board) {
		Set<Tile> targets = findAdjacentTiles(startingTile, board);
		Set<Tile> validTargets = new HashSet<>();
		for (Tile target : targets) {
			if (target.getUnit().getOwner() != unit.getOwner()) {
				validTargets.add(target);
			}
		}
		return validTargets;
	}

	public static Set<Tile> findValidSummonTiles(Player summoner, Board board) {
		Tile[][] boardTiles = board.getTiles();
		Set<Tile> alliedUnits = new HashSet<>();
		// Find all allied units on the board
		for (Tile[] row : boardTiles) {
			for (Tile tile : row) {
				if (tile.getUnit().getOwner() == summoner) {
				alliedUnits.add(tile);
				}
			}
		}

		Set<Tile> validTargets = new HashSet<>();

		// Find the valid summoning spaces
		for (Tile unitPos : alliedUnits) {
			Set<Tile> targets = findAdjacentTiles(unitPos, board);
			for (Tile target : targets) {
				if (target.getUnit() == null) {
					validTargets.add(target);
				}
			}
		}
		return validTargets;
	}


	public static void highlightAttackTiles(ActorRef out, Tile startingTile, Unit unit, Board board) {
		Set<Tile> targets = findValidAttackUnits(startingTile, unit, board);
		for  (Tile target : targets) {
			BasicCommands.drawTile(out, target, 3);
			blink();
		}
	}

	/**
	 * Highlights all empty tiles adjacent (8-directional) to any friendly unit
	 * owned by player1 in green (mode 2). Used for creature card summon targeting.
	 *
	 * @author Minghao
	 */
	public static void highlightSummonTiles(ActorRef out, Player summoner, Board board) {
		Set<Tile> targets = findValidSummonTiles(summoner, board);
		for  (Tile target : targets) {
			BasicCommands.drawTile(out, target, 2);
			blink();
		}
	}

	public static void showSpellPreview(ActorRef out, GameState gameState, Card card) {
		//TODO implement cards as subclasses
		final String HORN_OF_THE_FORSAKEN = "Horn of the Forsaken";
		final String WRAITHLING_SWARM = "Wraithling Swarm";
		final String TRUESTRIKE = "Truestrike";
		final String SUNDROP_ELIXIR = "Sundrop Elixir";
		final String DARK_TERMINUS = "Dark Terminus";

		String cardName = card.getCardname();
		switch (cardName) {
			case HORN_OF_THE_FORSAKEN:
				BasicCommands.addPlayer1Notification(out, "Click your avatar to equip Horn", 2);

				int ax = gameState.getPlayer1().getAvatar().getPosition().getTilex();
				int ay = gameState.getPlayer1().getAvatar().getPosition().getTiley();
				Tile avatarTile = gameState.getBoard().getTile(ax, ay);
				BasicCommands.drawTile(out, avatarTile, 1);
				break;

			case WRAITHLING_SWARM:
				BasicCommands.addPlayer1Notification(out, "Click an empty tile to cast Wraithling Swarm", 2);
				break;

			case TRUESTRIKE:
				BasicCommands.addPlayer1Notification(out, "Click an enemy unit for Truestrike", 2);
				break;

			case SUNDROP_ELIXIR:
				BasicCommands.addPlayer1Notification(out, "Click a unit to heal 5", 2);
				break;

			case DARK_TERMINUS:
				BasicCommands.addPlayer1Notification(out, "Click an enemy non-avatar unit", 2);
				break;

			default:
				BasicCommands.addPlayer1Notification(out, "Spell/artifact not implemented yet.", 2);
		}
	}

// TODO: add spell target highlighting
}

