package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.players.Player;
import structures.logic.BoardLogic;

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
		gameState.selectedUnit = null;
		gameState.selectedHandPosition = null;

		BoardLogic.clearSelection(out, gameState.board);

		if (gameState.player1Turn) {
			// Player 1 ends their turn → Player 2's turn begins (same round)
			gameState.endTurn(out, gameState.getPlayer1(), gameState.getPlayer2());
		} else {
			// Player 2 ends their turn → Player 1's turn begins (new round)
			gameState.endTurn(out, gameState.getPlayer2(), gameState.getPlayer1());
		}

		// Story Card #21: redraw player 1's hand after all turns resolve
		// (covers the case where the AI auto-ends player 2's turn)
		redrawHand(out, gameState.getPlayer1());
	}

	/**
	 * Clears all six hand slots on the front-end, then redraws the current hand.
	 * @author Minghao
	 */
	private void redrawHand(ActorRef out, Player player) {
		List<Card> hand = player.getHand();
		for (int i = 1; i <= 6; i++) {
			BasicCommands.deleteCard(out, i);
		}
		for (int i = 0; i < hand.size(); i++) {
			BasicCommands.drawCard(out, hand.get(i), i + 1, 0);
		}
	}
}