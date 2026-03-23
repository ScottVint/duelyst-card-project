package events;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Card;
import structures.logic.BoardLogic;

/**
 * Indicates that the user has clicked a card in their hand.
 *
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

/**
 * Indicates that the user has clicked a card in their hand.
 */
public class CardClicked implements EventProcessor {


	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		// Reset card drawn
		gameState.player1.drawHand(out);

		if (gameState.gameOver) {
			BasicCommands.addPlayer1Notification(out, "The game is over.", 2);
			return;
		}

		if (!gameState.player1Turn) {
			BasicCommands.addPlayer1Notification(out, "It is not your turn.", 2);
			return;
		}

		int handPosition = message.get("position").asInt(); // 1-indexed

		// Re-click same card -> deselect and stop
		if (Integer.valueOf(handPosition).equals(gameState.selectedHandPosition)) {
			gameState.selectedHandPosition = null;
			gameState.selectedUnit = null;
			BoardLogic.clearSelection(out, gameState.board);
			return;
		}

		// Clear previous selections
		gameState.selectedUnit = null;
		BoardLogic.clearSelection(out, gameState.board);

		List<Card> hand = gameState.getPlayer1().getHand();
		int index = handPosition - 1;

		Card card = hand.get(index);
		BasicCommands.drawCard(out, card, handPosition, 1);

		// Preview/highlight should still happen on card-click.
		gameState.selectedHandPosition = handPosition;

		if (!gameState.player1.enoughMana(out, card.getManacost())) {
			BasicCommands.addPlayer1Notification(out, "Not enough mana to play this card.", 2);
		}

		gameState.highlightedTiles = card.getTargets(gameState.player1, gameState.board);
		card.highlightTargets(out, gameState.player1, gameState.board);

		if (gameState.highlightedTiles == null || gameState.highlightedTiles.isEmpty()) {
			BasicCommands.addPlayer1Notification(out, "This card has no valid targets.", 2);
		}
	}
}