package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.players.Player;

/**
 * Indicates that the user has clicked the end-turn button.
 * <p>
 * Handles Story Card #6: mana replenishes to the correct value at the start of
 * each new turn, and the front-end display is updated immediately.
 * Mana for turn N = min(N + 1, 9) (turn 1 → 2 mana, turn 2 → 3 mana … capped at 9).
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

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		// Clear any active unit/card selection
		gameState.setSelectedUnit(null);
		gameState.setSelectedHandPosition(null);

		gameState.getBoard().clearSelection(out);

		if (gameState.isPlayer1Turn()) {
			// Player 1 ends their turn → Player 2's turn begins (same round)
			gameState.endTurn(out, gameState.getPlayer1(), gameState.getPlayer2());
		} else {
			// Player 2 ends their turn → Player 1's turn begins (new round)
			gameState.incrementTurnCount();
			gameState.endTurn(out, gameState.getPlayer2(), gameState.getPlayer1());

		}
	}

}
