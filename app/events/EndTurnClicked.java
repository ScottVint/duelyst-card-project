package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
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

	// TODO: Move MAX_MANA constant into the Player class
	private static final int MAX_MANA = 9;

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		// Clear any active unit/card selection
		gameState.setSelectedUnit(null);
		gameState.setSelectedHandPosition(null);

		// TODO: Board.clearSelection() to be called here once consolidated — Scott
		gameState.getBoard().clearSelection(out);

		if (gameState.isPlayer1Turn()) {
			// Player 1 ends their turn → Player 2's turn begins (same round)
			gameState.setPlayer1Turn(false);
			int mana = Math.min(gameState.getTurnCount() + 1, MAX_MANA);
			gameState.getPlayer2().setMana(mana);
			BasicCommands.setPlayer2Mana(out, gameState.getPlayer2());
		} else {
			// Player 2 ends their turn → Player 1's turn begins (new round)
			gameState.setPlayer1Turn(true);
			gameState.incrementTurnCount();
			int mana = Math.min(gameState.getTurnCount() + 1, MAX_MANA);
			gameState.getPlayer1().setMana(mana);
			BasicCommands.setPlayer1Mana(out, gameState.getPlayer1());

			// Story Card #21: draw one card for player 1 at the start of their turn
			gameState.getPlayer1().drawCard();
			redrawHand(out, gameState.getPlayer1());
		}
	}

	/**
	 * Clears all six hand slots on the front-end, then redraws the current hand.
	 * @author Minghao
	 */
	private void redrawHand(ActorRef out, Player player) {
		for (int i = 1; i <= Player.MAX_HAND_SIZE; i++) {
			BasicCommands.deleteCard(out, i);
		}
		List<Card> hand = player.getHand();
		for (int i = 0; i < hand.size(); i++) {
			BasicCommands.drawCard(out, hand.get(i), i + 1, 0);
		}
	}

}
