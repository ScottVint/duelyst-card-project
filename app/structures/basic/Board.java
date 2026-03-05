package structures.basic;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import utils.BasicObjectBuilders;

public class Board {
    private Tile[][] tiles;

    public Board() {
        // initialise 9 x 5 grid
        tiles = new Tile[9][5];
        for(int y = 0; y < 5; y++) {
            for(int x = 0; x < 9; x++) {
                tiles[x][y] = BasicObjectBuilders.loadTile(x, y);
            }
        }
    }

    public Tile[][] getTiles() { return tiles; }

    /// Resets tile state to 0 (white).
    /// @param out
    ///
    /// @author Scott
    public void clearSelection(ActorRef out) {
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                BasicCommands.drawTile(out, tile, 0); // white
            }
        }
    }
}


