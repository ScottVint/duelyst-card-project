package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

// Import the necessary structural classes
import structures.basic.Tile;
import structures.basic.Unit;
import commands.BasicCommands;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 0.
 * * {
 * messageType = "tileClicked"
 * tilex = <x index of the tile>
 * tiley = <y index of the tile>
 * }
 * * @author Dr. Richard McCreadie
 */
public class TileClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        int tilex = message.get("tilex").asInt();
        int tiley = message.get("tiley").asInt();

        // 1. Get the tile that was clicked from the board
        Tile clickedTile = gameState.getBoard().getTile(tilex, tiley);

        // 2. Check if the tile contains a unit
        if (clickedTile.getUnit() != null) {
            Unit clickedUnit = clickedTile.getUnit();

            // 3. Check if the unit belongs to the current player (Player 1)
            // Assuming your Unit class has a way to check ownership (e.g., getting the player object or an ID)
            if (clickedUnit.getOwner() == gameState.getPlayer1()) {
                
                // Save the selected unit to the GameState so we know what is selected for future actions
                gameState.setSelectedUnit(clickedUnit);

                // 4. Highlight the clicked tile (the unit itself)
                // Mode 1 is typically the standard White highlight in the BasicCommands utility
                BasicCommands.drawTile(out, clickedTile, 1); 

                // 5. Calculate and show the valid movement range in White 
                highlightMovementRange(out, gameState, clickedTile);
            }
        } else {
            // Logic for when an empty tile is clicked (e.g., Story Card #4 Unit Movement) will go here
        }
    }

    /**
     * Helper method to calculate and highlight the valid movement range for a unit.
     */
    private void highlightMovementRange(ActorRef out, GameState gameState, Tile startTile) {
        int startX = startTile.getTilex();
        int startY = startTile.getTiley();
        
        // Standard movement range is usually 2 tiles in any direction. 
        // If units have variable movement, retrieve this from the Unit object (e.g., clickedUnit.getMoveRange()).
        int moveRange = 2; 

        // Iterate through all tiles within a 2-tile radius
        for (int x = startX - moveRange; x <= startX + moveRange; x++) {
            for (int y = startY - moveRange; y <= startY + moveRange; y++) {
                
                // Verify the tile is within the board boundaries
                if (x >= 0 && x < gameState.getBoard().getX() && y >= 0 && y < gameState.getBoard().getY()) {
                    
                    // Calculate distance. For standard grid movement allowing diagonals, 
                    // distance is usually max(deltaX, deltaY).
                    int distance = Math.max(Math.abs(x - startX), Math.abs(y - startY));

                    // If the tile is within range and is not the starting tile
                    if (distance > 0 && distance <= moveRange) {
                        Tile targetTile = gameState.getBoard().getTile(x, y);

                        // The movement range must be highlighted in White. [cite: 20]
                        // We only highlight empty tiles for valid movement.
                        if (targetTile.getUnit() == null) {
                            BasicCommands.drawTile(out, targetTile, 1); // 1 = White highlight
                        }
                    }
                }
            }
        }
    }
}
