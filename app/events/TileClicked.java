package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * Indicates that the user has clicked a tile on the game canvas.
 * <p>
 * Handles Story Card #3: when the human player clicks one of their units, the unit is
 * selected and its valid movement range is highlighted in white.
 * <pre>
 * {
 *   messageType = "tileClicked"
 *   tilex = &lt;x index of the tile&gt;
 *   tiley = &lt;y index of the tile&gt;
 * }
 * </pre>
 *
 * @author Dr. Richard McCreadie
 * @author Minghao
 */
public class TileClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        int tilex = message.get("tilex").asInt();
        int tiley = message.get("tiley").asInt();

        Tile clickedTile = gameState.getBoard().getTile(tilex, tiley);

        if (clickedTile.getUnit() != null) {
            Unit clickedUnit = clickedTile.getUnit();

            if (clickedUnit.getOwner() == gameState.getPlayer1()) {
                gameState.getBoard().clearSelection(out);
                gameState.setSelectedUnit(clickedUnit);
                BasicCommands.drawTile(out, clickedTile, 1); // white highlight
                gameState.getBoard().highlightMovement(out, clickedTile);
            }

        } else {
            // TODO: Story Card #4 — move selected unit to this empty tile
        }
    }
}
