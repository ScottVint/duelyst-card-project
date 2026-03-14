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
	//TODO implement cards as subclasses
	private static final String HORN_OF_THE_FORSAKEN = "Horn of the Forsaken";
	private static final String WRAITHLING_SWARM = "Wraithling Swarm";
	private static final String TRUESTRIKE = "Truestrike";
	private static final String SUNDROP_ELIXIR = "Sundrop Elixir";
	private static final String DARK_TERMINUS = "Dark Terminus";

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
			BoardLogic.clearSelection(out, gameState.board);
			return;
		}

		// Clear previous selections
		gameState.setSelectedUnit(null);
		BoardLogic.clearSelection(out, gameState.board);

		List<Card> hand = gameState.getPlayer1().getHand();
		int index = handPosition - 1;

		Card card = hand.get(index);

		// Important:
		// Do NOT block card selection here based on mana.
		// Preview/highlight should still happen on card-click.
		// Mana is checked later in TileClicked when the player actually casts/places the card.
		gameState.setSelectedHandPosition(handPosition);

		if (card.isCreature()) {
			BoardLogic.highlightSummonTiles(out, gameState.getPlayer1(), gameState.board);
		}

		showSpellPreview(out, gameState, card);
	}

	//TODO move methods elsewhere
	private void showSpellPreview(ActorRef out, GameState gameState, Card card) {
		String cardName = card.getCardname();

		//TODO create subclasses for these
		if (HORN_OF_THE_FORSAKEN.equals(cardName)) {
			BasicCommands.addPlayer1Notification(out, "Click your avatar to equip Horn", 2);

			int ax = gameState.getPlayer1().getAvatar().getPosition().getTilex();
			int ay = gameState.getPlayer1().getAvatar().getPosition().getTiley();
			Tile avatarTile = gameState.getBoard().getTile(ax, ay);
			BasicCommands.drawTile(out, avatarTile, 1);
			return;
		}

		if (WRAITHLING_SWARM.equals(cardName)) {
			BasicCommands.addPlayer1Notification(out, "Click an empty tile to cast Wraithling Swarm", 2);
			return;
		}

		if (TRUESTRIKE.equals(cardName)) {
			BasicCommands.addPlayer1Notification(out, "Click an enemy unit for Truestrike", 2);
			return;
		}

		if (SUNDROP_ELIXIR.equals(cardName)) {
			BasicCommands.addPlayer1Notification(out, "Click a unit to heal 5", 2);
			return;
		}

		if (DARK_TERMINUS.equals(cardName)) {
			BasicCommands.addPlayer1Notification(out, "Click an enemy non-avatar unit", 2);
			return;
		}

		BasicCommands.addPlayer1Notification(out, "Spell/artifact not implemented yet.", 2);
	}
}
// TODO: add spell target highlighting