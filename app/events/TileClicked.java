package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 0.
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

        // Retrieve the tile that was clicked from the board
        Tile clickedTile = gameState.getBoard().getTile(tilex, tiley);

        if (clickedTile.getUnit() != null) {
            // --- A tile containing a unit was clicked ---
            Unit clickedUnit = clickedTile.getUnit();

            // Only respond to clicks on Player 1 (human) units.
            // avatar ownership is set in Initalize.java; without it getOwner() returns null.
            if (clickedUnit.getOwner() == gameState.getPlayer1()) {

                // Clear any highlights left over from a previous selection before
                // applying new ones, so the board never shows stale highlights.
                clearAllHighlights(out, gameState); // Story Card #3 fix @author Minghao

                // Record the newly selected unit in GameState so subsequent events
                // (e.g. TileClicked for movement, Story Card #4) can act on it.
                gameState.setSelectedUnit(clickedUnit);

                // Highlight the unit's own tile in white to show it is selected
                BasicCommands.drawTile(out, clickedTile, 1); // mode 1 = white highlight

                // Highlight all valid movement tiles in white (Story Card #3)
                highlightMovementRange(out, gameState, clickedTile);
            }

        } else {
            // --- An empty tile was clicked ---
            // Movement logic for Story Card #4 (move selected unit here) will be added here.
        }
    }

    /**
     * Resets every tile on the board to its default (unhighlighted) state.
     * <p>
     * Must be called before applying new highlights so that tiles highlighted
     * by a previous selection are fully cleared.
     *
     * @param out       the ActorRef used to send front-end commands
     * @param gameState the current game state (provides board dimensions and tiles)
     * @author Minghao
     */
    private void clearAllHighlights(ActorRef out, GameState gameState) {
        for (int x = 0; x < gameState.getBoard().getX(); x++) {
            for (int y = 0; y < gameState.getBoard().getY(); y++) {
                Tile tile = gameState.getBoard().getTile(x, y);
                BasicCommands.drawTile(out, tile, 0); // mode 0 = normal (no highlight)
            }
        }
    }

    /**
     * Highlights valid movement tiles for the unit standing on {@code startTile}, in white.
     * <p>
     * Movement rules (Story Card #3 / Game Rules):
     * <ul>
     *   <li><b>Cardinal</b> (up / down / left / right): up to 2 steps.
     *       Path blocking applies — if any tile along the path is occupied the
     *       unit cannot move through it or land behind it.</li>
     *   <li><b>Diagonal</b>: exactly 1 step in any of the four diagonal directions.
     *       No path-blocking check is needed for a single diagonal step.</li>
     *   <li>Only <em>empty</em> tiles are highlighted as valid landing squares.</li>
     * </ul>
     *
     * @param out       the ActorRef used to send front-end commands
     * @param gameState the current game state (provides board and tile data)
     * @param startTile the tile the selected unit currently occupies
     * @author Minghao
     */
    private void highlightMovementRange(ActorRef out, GameState gameState, Tile startTile) {
        int sx     = startTile.getTilex();
        int sy     = startTile.getTiley();
        int boardW = gameState.getBoard().getX();
        int boardH = gameState.getBoard().getY();

        // --- Cardinal directions: up to 2 steps with path blocking ---
        // For each direction we step outward one tile at a time.
        // If a tile is occupied we stop immediately (cannot move through units).
        int[][] cardinalDirs = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
        for (int[] dir : cardinalDirs) {
            for (int step = 1; step <= 2; step++) {
                int nx = sx + dir[0] * step;
                int ny = sy + dir[1] * step;

                // Stop if this step would leave the board
                if (nx < 0 || nx >= boardW || ny < 0 || ny >= boardH) break;

                Tile nextTile = gameState.getBoard().getTile(nx, ny);

                // A unit on this tile blocks the path — cannot pass through or stop here
                if (nextTile.getUnit() != null) break;

                // Empty tile within range: highlight white
                BasicCommands.drawTile(out, nextTile, 1); // mode 1 = white highlight
            }
        }

        // --- Diagonal directions: exactly 1 step (no path blocking needed) ---
        int[][] diagonalDirs = { {1, 1}, {1, -1}, {-1, 1}, {-1, -1} };
        for (int[] dir : diagonalDirs) {
            int nx = sx + dir[0];
            int ny = sy + dir[1];

            // Skip if out of board bounds
            if (nx < 0 || nx >= boardW || ny < 0 || ny >= boardH) continue;

            Tile diagTile = gameState.getBoard().getTile(nx, ny);

            // Only highlight if the destination tile is empty
            if (diagTile.getUnit() == null) {
                BasicCommands.drawTile(out, diagTile, 1); // mode 1 = white highlight
            }
        }
    }
}
