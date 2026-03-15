package structures.basic.spells;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.players.*;

import java.util.Set;

public abstract class Spell {
    public Set<Tile> validTargets(Player player, Board board) {return null;}

    public void highlightTargets(ActorRef out, Player player, Board board) {
        Set<Tile> validTargets = validTargets(player, board);
        if  (validTargets.isEmpty()) {
            BasicCommands.addPlayer1Notification(out, "No valid tiles!", 2);
        };
        for (Tile tile : validTargets) {
            BasicCommands.drawTile(out, tile, 1);
        }
    }

    public void cast(ActorRef out, GameState gameState,
                     Player player, Tile clickedTile,
                     Board board, int cardIndex) {}
}
