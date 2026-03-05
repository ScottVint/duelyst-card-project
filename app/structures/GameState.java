package structures;

import structures.basic.players.Player;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * <p>
 * Extended to initialise the board, both players, their avatars and decks
 * at the start of a game session.
 *
 * @author Dr. Richard McCreadie
 * @author Minghao
 *
 */

public class GameState {

	public boolean gameInitalised = false;
	public boolean something = false;

	/** Human player (Player 1). @author Minghao */
	private Player player1;
	/** AI player (Player 2). @author Minghao */
	private Player player2;
	/** The 9x5 game board. @author Minghao */
	private Board board;
	/** The unit currently selected by the player, or null if none. @author Minghao */
	private Unit selectedUnit;
	/** True when it is Player 1's turn to act. @author Minghao */
	private boolean player1Turn = true;
	/** How many turns Player 1 has had (starts at 1 since the game opens on P1's first turn). @author Minghao */
	private int player1TurnCount = 1;
	/** How many turns Player 2 has had (0 until P1 first ends their turn). @author Minghao */
	private int player2TurnCount = 0;
	/** Hand position (1-indexed) of the card selected by the player, or -1 if none. @author Minghao */
	private int selectedHandPosition = -1;

	/**
	 * Initialises the board, loads both avatars from config, and prepares
	 * each player with their starting deck (2 copies).
	 *
	 * @author Minghao
	 */
	public GameState() {
		board = new Board();

		Unit avatar1 = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 1, Unit.class);
		Unit avatar2 = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 2, Unit.class);

		player1 = new Player();
		player1.setAvatar(avatar1);
		player1.setDeck(OrderedCardLoader.getPlayer1Cards(2));

		player2 = new Player();
		player2.setAvatar(avatar2);
		player2.setDeck(OrderedCardLoader.getPlayer2Cards(2));
	}

	/**
	 * Returns the human player (Player 1).
	 * @author Minghao
	 */
	public Player getPlayer1() {
		return player1;
	}

	/**
	 * Returns the AI player (Player 2).
	 * @author Minghao
	 */
	public Player getPlayer2() {
		return player2;
	}

	/**
	 * Returns the game board.
	 * @author Minghao
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Returns the currently selected unit, or null if none is selected.
	 * @author Minghao
	 */
	public Unit getSelectedUnit() {
		return selectedUnit;
	}

	/**
	 * Sets the currently selected unit. Pass null to deselect.
	 * @param unit the unit to select, or null
	 * @author Minghao
	 */
	public void setSelectedUnit(Unit unit) {
		this.selectedUnit = unit;
	}

	/** Returns true when it is Player 1's turn. @author Minghao */
	public boolean isPlayer1Turn() { return player1Turn; }

	/** Sets whose turn it is. @author Minghao */
	public void setPlayer1Turn(boolean player1Turn) { this.player1Turn = player1Turn; }

	/** Returns how many turns Player 1 has had so far. @author Minghao */
	public int getPlayer1TurnCount() { return player1TurnCount; }

	/** Returns how many turns Player 2 has had so far. @author Minghao */
	public int getPlayer2TurnCount() { return player2TurnCount; }

	/** Increments Player 1's turn counter (called when P1's new turn begins). @author Minghao */
	public void incrementPlayer1TurnCount() { player1TurnCount++; }

	/** Increments Player 2's turn counter (called when P2's new turn begins). @author Minghao */
	public void incrementPlayer2TurnCount() { player2TurnCount++; }

	/** Returns the 1-indexed hand position of the selected card, or -1 if none. @author Minghao */
	public int getSelectedHandPosition() { return selectedHandPosition; }

	/** Sets the selected hand position. Pass -1 to deselect. @author Minghao */
	public void setSelectedHandPosition(int pos) { this.selectedHandPosition = pos; }

}
