package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.BetterUnit;
import structures.basic.Board;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.players.Player;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 */
public class GameState {

	public boolean gameInitalised = false;
	public boolean something = false;

	public Player player1;
	public Player player2;
	public Board board;
	public Unit selectedUnit;
	public boolean player1Turn = true;

	/** Current round number */
	public int turnCount = 1;

	/** 1-indexed hand position of the selected card, or null if none selected */
	public Integer selectedHandPosition = null;

	/** Next unique unit id for summoned units. Avatars already use 1 and 2 */
	private int nextUnitId = 3;

	/** Horn of the Forsaken charges for player 1 */
	private int player1HornCharges = 0;

	public GameState() {
		board = new Board();

		BetterUnit avatar1 = (BetterUnit) BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 1, BetterUnit.class);
		BetterUnit avatar2 = (BetterUnit) BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 2, BetterUnit.class);

		player1 = new Player();
		player1.setAvatar(avatar1);
		player1.setDeck(OrderedCardLoader.getPlayer1Cards(2));
		avatar1.setOwner(player1);

		player2 = new Player();
		player2.setAvatar(avatar2);
		player2.setDeck(OrderedCardLoader.getPlayer2Cards(2));
		avatar2.setOwner(player2);
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

	public int getNextUnitId() {
		return nextUnitId++;
	}

	public void clearSelections(ActorRef out) {
		selectedUnit = null;
		selectedHandPosition = null;
		board.clearSelection(out);
	}

	public int getPlayer1HornCharges() {
		return player1HornCharges;
	}

	public void equipPlayer1Horn() {
		player1HornCharges = 3;
	}

	public boolean player1HasHorn() {
		return player1HornCharges > 0;
	}

	public void usePlayer1HornCharge() {
		if (player1HornCharges > 0) {
			player1HornCharges--;
		}
	}

	/**
	 * Unified damage helper for combat / spell damage.
	 */
	public void dealDamage(ActorRef out, Unit attacker, Unit target) {
		if (attacker == null || target == null) return;

		target.takeDamage(out, attacker.getAttack());

		// Avatar HP must also update the player HUD
		if (target == player1.getAvatar()) {
			player1.setHealth(target.getHealth());
			BasicCommands.setPlayer1Health(out, player1);
		} else if (target == player2.getAvatar()) {
			player2.setHealth(target.getHealth());
			BasicCommands.setPlayer2Health(out, player2);
		}

		// Remove dead units from board + UI
		if (target.getHealth() <= 0 && target.getPosition() != null) {
			Tile tile = board.getTile(target.getPosition().getTilex(), target.getPosition().getTiley());
			if (tile != null) {
				tile.setUnit(null);
			}
			BasicCommands.deleteUnit(out, target);
		}
	}
}