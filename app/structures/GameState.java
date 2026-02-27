package structures;

import structures.basic.Player;
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

}
