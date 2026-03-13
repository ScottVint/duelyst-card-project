package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.BetterUnit;
import structures.basic.Board;
import structures.basic.Tile;
import structures.basic.players.AIPlayer;
import structures.basic.players.HumanPlayer;
import structures.basic.players.Player;
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

	public HumanPlayer player1;
	public AIPlayer player2;
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

	/** Movement state */
	public Unit movingUnit = null;
	public Tile moveTargetTile = null;
	public boolean unitMoving = false;

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

	public int getNextUnitId() {
		return nextUnitId++;
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

	public Unit getMovingUnit() { return movingUnit; }
	public void setMovingUnit(Unit movingUnit) { this.movingUnit = movingUnit; }

	public Tile getMoveTargetTile() { return moveTargetTile; }
	public void setMoveTargetTile(Tile moveTargetTile) { this.moveTargetTile = moveTargetTile; }

	public boolean isUnitMoving() { return unitMoving; }
	public void setUnitMoving(boolean unitMoving) { this.unitMoving = unitMoving; }

	public void clearSelections(ActorRef out) {
		selectedUnit = null;
		selectedHandPosition = null;
		board.clearSelection(out);
	}

	/**
	 * Combat damage based on attacker attack stat.
	 */
	public void dealDamage(ActorRef out, Unit attacker, Unit target) {
		if (attacker == null || target == null) return;

		int damage = attacker.getAttack();

		if (damage <= 0) {
			BasicCommands.addPlayer1Notification(out, "Attacker has 0 attack.", 2);
			return;
		}

		dealDirectDamage(out, target, damage);
	}

	/**
	 * Direct spell / combat damage.
	 */
	public void dealDirectDamage(ActorRef out, Unit target, int damage) {
		if (target == null || damage <= 0) return;

		int newHealth = target.getHealth() - damage;
		target.setHealth(newHealth);

		BasicCommands.setUnitHealth(out, target, target.getHealth());

		if (target == player1.getAvatar()) {
			player1.setHealth(target.getHealth());
			BasicCommands.setPlayer1Health(out, player1);
		} else if (target == player2.getAvatar()) {
			player2.setHealth(target.getHealth());
			BasicCommands.setPlayer2Health(out, player2);
		}

		if (target.getHealth() <= 0) {
			removeUnit(out, target);
		}
	}
	/**
	 * Heal a unit, capped by maxHealth.
	 */
	public void healUnit(ActorRef out, Unit target, int amount) {
		if (target == null || amount <= 0) return;

		int healed = Math.min(target.getMaxHealth(), target.getHealth() + amount);
		target.setHealth(healed);
		BasicCommands.setUnitHealth(out, target, target.getHealth());

		if (target == player1.getAvatar()) {
			player1.setHealth(target.getHealth());
			BasicCommands.setPlayer1Health(out, player1);
		} else if (target == player2.getAvatar()) {
			player2.setHealth(target.getHealth());
			BasicCommands.setPlayer2Health(out, player2);
		}
	}

	/**
	 * Remove a dead unit from the board and UI.
	 */
	public void removeUnit(ActorRef out, Unit target) {
		if (target == null) return;

		if (target.getPosition() != null) {
			Tile tile = board.getTile(target.getPosition().getTilex(), target.getPosition().getTiley());
			if (tile != null) {
				tile.setUnit(null);
			}
		}
		BasicCommands.deleteUnit(out, target);
	}

	/**
	 * Summon a 1/1 Wraithling token to a tile.
	 */
	public Unit summonWraithling(ActorRef out, Tile tile, Player owner) {
		if (tile == null || owner == null) return null;
		if (tile.getUnit() != null) return null;

		Unit wraithling = BasicObjectBuilders.loadUnit(
				StaticConfFiles.wraithling,
				getNextUnitId(),
				Unit.class
		);

		wraithling.setOwner(owner);
		wraithling.setAttack(1);
		wraithling.setMaxHealth(1);
		wraithling.setHealth(1);
		wraithling.setPositionByTile(tile);
		tile.setUnit(wraithling);

		BasicCommands.drawUnit(out, wraithling, tile);
		BasicCommands.setUnitAttack(out, wraithling, wraithling.getAttack());
		BasicCommands.setUnitHealth(out, wraithling, wraithling.getHealth());

		return wraithling;
	}
}
	public void endTurn(ActorRef out, Player playerEndingTurn, Player playerStartingTurn) {
		player1Turn = !player1Turn;
		int startingMana = Math.min(turnCount + 1, Player.getMaxMana());
		playerStartingTurn.setMana(out, startingMana);
		playerEndingTurn.setMana(out, 0);
	}
}
