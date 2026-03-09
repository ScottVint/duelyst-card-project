package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
//* <p>
//* Handles Story Card #6: mana replenishes to the correct value at the start of
// * each new turn, and the front-end display is updated immediately.
// * Mana for turn N = min(N + 1, 9) (turn 1 → 2 mana, turn 2 → 3 mana … capped at 9).
//		*
//		* {
//		*   messageType = "endTurnClicked"
//		* }
//		*
//		* @author Dr. Richard McCreadie
// * @author Minghao
// *
/**
 * Indicates that the user has clicked the end-turn button.
 */
public class EndTurnClicked implements EventProcessor {

	private static final int MAX_MANA = 9;

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		gameState.clearSelections(out);

		if (gameState.isPlayer1Turn()) {
			// Player ends -> AI turn
			gameState.setPlayer1Turn(false);
			int mana = Math.min(gameState.getTurnCount() + 1, MAX_MANA);
			gameState.getPlayer2().setMana(mana);
			BasicCommands.setPlayer2Mana(out, gameState.getPlayer2());
			BasicCommands.addPlayer1Notification(out, "AI Turn", 2);
		} else {
			// AI ends -> Player turn
			gameState.setPlayer1Turn(true);
			gameState.incrementTurnCount();

			int mana = Math.min(gameState.getTurnCount() + 1, MAX_MANA);
			gameState.getPlayer1().setMana(mana);
			BasicCommands.setPlayer1Mana(out, gameState.getPlayer1());

			drawOneCardForPlayer1(out, gameState);

			BasicCommands.addPlayer1Notification(out, "Player Turn", 2);
		}
	}

	private void drawOneCardForPlayer1(ActorRef out, GameState gameState) {
		if (gameState.getPlayer1().getHand().size() < 6) {
			gameState.getPlayer1().drawCard();
		}
		redrawPlayerHand(out, gameState);
	}

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