package structures.basic.spells;
import akka.actor.ActorRef;
import com.fasterxml.jackson.annotation.JsonIgnore;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.players.*;
import structures.logic.BoardLogic;

import java.util.Set;

public abstract class Spell {
    @JsonIgnore
    public Spell() {}

    public Set<Tile> validTargets(Player player, Board board) {return null;}

        if (validTargets == null || validTargets.isEmpty()) {
        Set<Tile> validTargets = validTargets(player, board);
        if  (validTargets.isEmpty()) {
            BasicCommands.addPlayer1Notification(out, "No valid tiles!", 2);
        };
        for (Tile tile : validTargets) {
            BasicCommands.drawTile(out, tile, 2);

                BoardLogic.blink();
            }
        }


    public void cast(ActorRef out, GameState gameState,
                     Player player, Tile clickedTile,
                     Board board, int cardIndex) {}
}

