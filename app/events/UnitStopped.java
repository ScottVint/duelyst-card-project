package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;

public class UnitStopped implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        // unlock
        gameState.isAnimating = false;

//        // clear move highlights
//        for (String key : gameState.validMoveTiles) {
//            Tile t = tileFromKey(gameState, key);
//            if (t != null) BasicCommands.drawTile(out, t, 0);
//        }
//
//        // clear attack highlights
//        for (String key : gameState.validAttackTiles) {
//            Tile t = tileFromKey(gameState, key);
//            if (t != null) BasicCommands.drawTile(out, t, 0);
//        }
//
//        gameState.clearHighlights();
//        gameState.clearSelection();
//    }
//
//    private Tile tileFromKey(GameState gs, String key) {
//        int comma = key.indexOf(",");
//        int x = Integer.parseInt(key.substring(0, comma));
//        int y = Integer.parseInt(key.substring(comma + 1));
//        if (!gs.inBounds(x, y)) return null;
//        return gs.board[x][y];
    }
}