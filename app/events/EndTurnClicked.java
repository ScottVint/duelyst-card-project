package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
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

	// TODO: Move MAX_MANA constant into the Player class
	private static final int MAX_MANA = 9;

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		// Clear any active unit/card selection
		gameState.setSelectedUnit(null);
		gameState.setSelectedHandPosition(null);
		gameState.getBoard().clearSelection(out);

		if (gameState.isPlayer1Turn()) {
			// Player ends -> AI turn

			BasicCommands.addPlayer1Notification(out, "AI Turn", 2);
		} else {
			// AI ends -> Player turn

			BasicCommands.addPlayer1Notification(out, "Player Turn", 2);
		}
	}

	//TODO Merge with proper method
	private void drawOneCardForPlayer1(ActorRef out, GameState gameState) {
		if (gameState.getPlayer1().getHand().size() < 6) {
			gameState.getPlayer1().drawCard();
		}
		redrawPlayerHand(out, gameState);
	}

	//TODO Merge with proper method
	private void redrawPlayerHand(ActorRef out, GameState gameState) {
		for (int i = 1; i <= 6; i++) {
			BasicCommands.deleteCard(out, i);
		}

		List<Card> hand = gameState.getPlayer1().getHand();
		for (int i = 0; i < hand.size() && i < 6; i++) {
			BasicCommands.drawCard(out, hand.get(i), i + 1, 0);
		}
	}
}