package structures.basic.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.players.Player;
import structures.basic.unittypes.Unit;
import structures.basic.unittypes.Wraithling;
import structures.logic.BoardLogic;
import utils.BasicObjectBuilders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WraithlingSwarm extends Spell {
    @Override
    public Set<Tile> validTargets(Player player, Board board) {
        Set<Tile> targets = BoardLogic.findValidSummonTiles(player, board);
        return targets;
    }

    @Override
    public void highlightTargets(ActorRef out, Player player, Board board) {
        Set<Tile> validTargets = validTargets(player, board);
        if (validTargets.isEmpty()) {
            BasicCommands.addPlayer1Notification(out, "No valid tiles!", 2);
        }
        ;
        for (Tile tile : validTargets) {
            BasicCommands.drawTile(out, tile, 1);
            BoardLogic.blink();
        }
    }

    @Override
    public void cast(ActorRef out,
                     GameState gameState,
                     Player player,
                     Tile clickedTile,
                     Board board,
                     int cardIndex) {

        Card card = player.getHand().get(cardIndex);

        // Searches for valid tiles, then randomly summons them in those slots
        List<Tile> validTargets = new ArrayList<>(validTargets(player, board));
        for (int i = 0; i < 3; i++) {
            // Create a new wraithling unit per loop
            int targetIndex = (int) (Math.random() * validTargets.size());
            Tile target = validTargets.get(targetIndex);

            Unit.summonWraithling(out, target, player, gameState);
            validTargets.remove(target);
        }

        System.out.println("Wraithling Swarm cast");
    }

}
