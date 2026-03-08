package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;

/**
 * Indicates that the user has clicked a card in their hand.
 */
public class CardClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		if (!gameState.isPlayer1Turn()) {
			BasicCommands.addPlayer1Notification(out, "It is not your turn.", 2);
			return;
		}

		int handPosition = message.get("position").asInt(); // 1-indexed

		// Re-click same card -> deselect
		if (Integer.valueOf(handPosition).equals(gameState.getSelectedHandPosition())) {
			gameState.setSelectedHandPosition(null);
			gameState.getBoard().clearSelection(out);
			return;
		}

		// Clear previous selections
		gameState.setSelectedUnit(null);
		gameState.getBoard().clearSelection(out);

		List<Card> hand = gameState.getPlayer1().getHand();
		int index = handPosition - 1;
		if (index < 0 || index >= hand.size()) {
			return;
		}

		Card card = hand.get(index);

		if (gameState.getPlayer1().getMana() < card.getManacost()) {
			BasicCommands.addPlayer1Notification(out, "Not enough mana.", 2);
			return;
		}

		gameState.setSelectedHandPosition(handPosition);

		if (card.isCreature()) {
			BasicCommands.addPlayer1Notification(out, "Creature selected", 2);
			gameState.getBoard().highlightSummonTiles(out, gameState.getPlayer1());
		} else if ("Horn of the Forsaken".equals(card.getCardname())) {
			BasicCommands.addPlayer1Notification(out, "Click your avatar to equip Horn", 2);

			int ax = gameState.getPlayer1().getAvatar().getPosition().getTilex();
			int ay = gameState.getPlayer1().getAvatar().getPosition().getTiley();
			BasicCommands.drawTile(out, gameState.getBoard().getTile(ax, ay), 1);
		} else {
			BasicCommands.addPlayer1Notification(out, "Spell/artifact not implemented yet.", 2);
		}
	}
}