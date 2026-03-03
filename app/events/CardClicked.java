package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.Board;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * <p>
 * Handles Story Card #22: when the player selects a creature card, all empty board
 * tiles that are adjacent (8-directional, 1 step) to a friendly unit already on the
 * board are highlighted in green (mode 2).  Tiles occupied by any unit are not
 * highlighted as valid summon destinations.
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

		// Deselect any previously selected unit and clear board highlights
		gameState.setSelectedUnit(null);
		clearAllHighlights(out, gameState);

		// Validate the hand position
		List<Card> hand = gameState.getPlayer1().getHand();
		int index = handPosition - 1; // convert to 0-indexed
		if (index < 0 || index >= hand.size()) return;

		Card card = hand.get(index);

		// Record the selected card so later events (e.g. TileClicked for summoning) can act on it
		gameState.setSelectedHandPosition(handPosition);

		// Story Card #22: creature cards → highlight valid summon tiles in green
		if (card.isCreature()) {
			highlightSummonTiles(out, gameState);
		}
	}

	/**
	 * Resets every tile on the board to its default (unhighlighted) state.
	 *
	 * @param out       the ActorRef used to send front-end commands
	 * @param gameState the current game state
	 * @author Minghao
	 */
	private void clearAllHighlights(ActorRef out, GameState gameState) {
		for (int x = 0; x < gameState.getBoard().getX(); x++) {
			for (int y = 0; y < gameState.getBoard().getY(); y++) {
				BasicCommands.drawTile(out, gameState.getBoard().getTile(x, y), 0);
			}
		}
	}

	/**
	 * Highlights all empty tiles that are adjacent (8-directional, 1 step) to any
	 * friendly (Player 1) unit currently on the board, using mode 2 (green).
	 * Tiles already occupied by a unit are never highlighted.
	 *
	 * @param out       the ActorRef used to send front-end commands
	 * @param gameState the current game state
	 * @author Minghao
	 */
	private void highlightSummonTiles(ActorRef out, GameState gameState) {
		Board board = gameState.getBoard();
		Player player1 = gameState.getPlayer1();

		for (int x = 0; x < board.getX(); x++) {
			for (int y = 0; y < board.getY(); y++) {
				Unit unit = board.getTile(x, y).getUnit();
				if (unit == null || unit.getOwner() != player1) continue;

				// Found a friendly unit — highlight its empty neighbours
				for (int dx = -1; dx <= 1; dx++) {
					for (int dy = -1; dy <= 1; dy++) {
						if (dx == 0 && dy == 0) continue; // skip the unit's own tile
						int nx = x + dx;
						int ny = y + dy;
						if (nx < 0 || nx >= board.getX() || ny < 0 || ny >= board.getY()) continue;
						Tile adjacentTile = board.getTile(nx, ny);
						if (adjacentTile.getUnit() == null) {
							BasicCommands.drawTile(out, adjacentTile, 2); // mode 2 = green
						}
					}
				}
			}
		}
	}

}
