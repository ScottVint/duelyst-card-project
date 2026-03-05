package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * <p>
 * Handles Story Card #12 (Cancel Selection): clicking on a non-interactive area
 * cancels any active unit or card selection and clears all board highlights.
 *
 * {
 *   messageType = "otherClicked"
 * }
 *
 * @author Dr. Richard McCreadie
 * @author Minghao
 */
public class OtherClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		// Story Card #12: cancel any active unit or card selection
		gameState.setSelectedUnit(null);
		gameState.setSelectedHandPosition(-1);

		// Clear all board highlights left from the previous selection
		for (int x = 0; x < gameState.getBoard().getX(); x++) {
			for (int y = 0; y < gameState.getBoard().getY(); y++) {
				Tile tile = gameState.getBoard().getTile(x, y);
				BasicCommands.drawTile(out, tile, 0);
			}
		}
	}

}
