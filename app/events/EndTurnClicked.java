package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * <p>
 * Handles Story Card #6: mana replenishes to the correct value at the start of
 * each new turn, and the front-end display is updated immediately.
 * Mana for a player's Nth turn = min(N + 1, 9), matching standard Duelyst rules
 * (turn 1 → 2 mana, turn 2 → 3 mana … capped at 9).
 *
 * {
 *   messageType = "endTurnClicked"
 * }
 *
 * @author Dr. Richard McCreadie
 * @author Minghao
 *
 */
public class EndTurnClicked implements EventProcessor {

	/** Maximum mana a player can have in any single turn. */
	private static final int MAX_MANA = 9;

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		// Clear any active unit/card selection so the new active player starts fresh
		gameState.setSelectedUnit(null);
		gameState.setSelectedHandPosition(-1);

		// Clear all board highlights left from the previous turn
		for (int x = 0; x < gameState.getBoard().getX(); x++) {
			for (int y = 0; y < gameState.getBoard().getY(); y++) {
				Tile tile = gameState.getBoard().getTile(x, y);
				BasicCommands.drawTile(out, tile, 0);
			}
		}

		if (gameState.isPlayer1Turn()) {
			// Player 1 ends their turn → Player 2's turn begins
			gameState.setPlayer1Turn(false);
			gameState.incrementPlayer2TurnCount();
			int mana = Math.min(gameState.getPlayer2TurnCount() + 1, MAX_MANA);
			gameState.getPlayer2().setMana(mana);
			BasicCommands.setPlayer2Mana(out, gameState.getPlayer2());
		} else {
			// Player 2 ends their turn → Player 1's turn begins
			gameState.setPlayer1Turn(true);
			gameState.incrementPlayer1TurnCount();
			int mana = Math.min(gameState.getPlayer1TurnCount() + 1, MAX_MANA);
			gameState.getPlayer1().setMana(mana);
			BasicCommands.setPlayer1Mana(out, gameState.getPlayer1());
		}
	}

}
