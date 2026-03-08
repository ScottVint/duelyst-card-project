package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * Indicates that the user has clicked the end-turn button.
 */
public class EndTurnClicked implements EventProcessor {

	private static final int MAX_MANA = 9;

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		gameState.clearSelections(out);

		if (gameState.isPlayer1Turn()) {
			gameState.setPlayer1Turn(false);
			int mana = Math.min(gameState.getTurnCount() + 1, MAX_MANA);
			gameState.getPlayer2().setMana(mana);
			BasicCommands.setPlayer2Mana(out, gameState.getPlayer2());
			BasicCommands.addPlayer1Notification(out, "AI Turn", 2);
		} else {
			gameState.setPlayer1Turn(true);
			gameState.incrementTurnCount();
			int mana = Math.min(gameState.getTurnCount() + 1, MAX_MANA);
			gameState.getPlayer1().setMana(mana);
			BasicCommands.setPlayer1Mana(out, gameState.getPlayer1());
			BasicCommands.addPlayer1Notification(out, "Player Turn", 2);
		}
	}
}