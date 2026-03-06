package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;

/**
 * Indicates that the user has clicked a card in their hand.
 * <p>
 * Handles Story Card #22: selecting a creature card highlights all empty board tiles
 * adjacent (8-directional) to a friendly unit in green (mode 2).
 * Handles Story Card #12: re-clicking the same card deselects it.
 *
 * {
 *   messageType = "cardClicked"
 *   position = &lt;hand index position [1-6]&gt;
 * }
 *
 * @author Dr. Richard McCreadie
 * @author Minghao
 *
 */
public class CardClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int handPosition = message.get("position").asInt(); // 1-indexed

		// Story Card #12: re-clicking the same card deselects it
		if (Integer.valueOf(handPosition).equals(gameState.getSelectedHandPosition())) {
			gameState.setSelectedHandPosition(null);
			gameState.getBoard().clearSelection(out);
			return;
		}

		// Deselect any previously selected unit and clear highlights
		gameState.setSelectedUnit(null);
		gameState.getBoard().clearSelection(out);

		List<Card> hand = gameState.getPlayer1().getHand();
		int index = handPosition - 1;
		if (index < 0 || index >= hand.size()) return;

		Card card = hand.get(index);
		gameState.setSelectedHandPosition(handPosition);

		// Story Card #22: creature cards → highlight valid summon tiles in green
		if (card.isCreature()) {
			gameState.getBoard().highlightSummonTiles(out, gameState.getPlayer1());
		}
	}

}
