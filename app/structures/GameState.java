package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.BetterUnit;
import structures.basic.Board;
import structures.basic.players.AIPlayer;
import structures.basic.players.HumanPlayer;
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

	public HumanPlayer player1;
	public AIPlayer player2;
	public Board board;
	public Unit selectedUnit;
	public boolean player1Turn = true;

	/** Current round number (shared by both players). Increments when P2 ends their turn. */
	public int turnCount = 1;

	/** 1-indexed hand position of the selected card, or null if none selected. */
	public Integer selectedHandPosition = null;

	// TODO: Move player/avatar/deck initialisation into the Player class constructor

	public GameState() {
		board = new Board();

		BetterUnit avatar1 = (BetterUnit) BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 1, BetterUnit.class);
		BetterUnit avatar2 = (BetterUnit) BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 2, BetterUnit.class);

		player1 = new HumanPlayer();

		player2 = new AIPlayer();
	}

	public Player getPlayer1() { return player1; }
	public Player getPlayer2() { return player2; }
	public Board getBoard() { return board; }
	public Unit getSelectedUnit() { return selectedUnit; }
	public void setSelectedUnit(Unit unit) { this.selectedUnit = unit; }
	public boolean isPlayer1Turn() { return player1Turn; }
	public void setPlayer1Turn(boolean player1Turn) { this.player1Turn = player1Turn; }
	public int getTurnCount() { return turnCount; }
	public void incrementTurnCount() { turnCount++; }
	public Integer getSelectedHandPosition() { return selectedHandPosition; }
	public void setSelectedHandPosition(Integer pos) { this.selectedHandPosition = pos; }

	public void endTurn(ActorRef out, Player playerEndingTurn, Player playerStartingTurn) {
		player1Turn = !player1Turn;
		int startingMana = Math.min(turnCount + 1, Player.getMaxMana());
		playerStartingTurn.setMana(out, startingMana);
		playerEndingTurn.setMana(out, 0);
		// TODO consolidate into setMana function
		BasicCommands.setPlayer1Mana(out, player1);
		BasicCommands.setPlayer2Mana(out, player2);
	}
}
