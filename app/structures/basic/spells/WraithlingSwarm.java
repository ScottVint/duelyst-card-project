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
    public static Set<Tile> validTargets(Player player, Board board) {
        return BoardLogic.findValidSummonTiles(player, board);
    }

    public static void highlightTargets(ActorRef out, Player player, Board board) {
        Set<Tile> validTargets = validTargets(player, board);
        if  (validTargets.isEmpty()) {
            BasicCommands.addPlayer1Notification(out, "Not enough valid tiles!", 2);
        };
        for (Tile tile : validTargets) {
            BasicCommands.drawTile(out, tile, 1);
        }
    }

    public static void cast(ActorRef out, GameState gameState, Player player, Tile clickedTile, Board board, int cardIndex) {

        Card card = player.getHand().get(cardIndex);

        if (!player.enoughMana(out, card.getManacost())) {
            return;
        }

        // Searches for valid tiles, then randomly summons them in those slots
        List<Tile> validTargets = new ArrayList<>(validTargets(player, board));
        for (int i = 0; i < 3; i++) {
            // Create a new wraithling unit per loop
            Wraithling summoned = createWraithling(out, player, gameState);
            int targetIndex = (int) (Math.random() * validTargets.size());
            Tile target = validTargets.get(targetIndex);

            card.summon(out, gameState, player, target, board);
            validTargets.remove(target);
        }

        player.useCard(out, gameState, cardIndex, card.getManacost());
        System.out.println( "Wraithling Swarm cast");
    }

    private static Wraithling createWraithling(ActorRef out, Player player, GameState gameState) {
        Wraithling wraithling = (Wraithling) BasicObjectBuilders.loadUnit("conf/gameconfs/units/wraithling.json", gameState.getNextUnitId(), Wraithling.class);

        wraithling.setOwner(player);
        wraithling.setHealth(out, 1);
        wraithling.setAttack(1);
        wraithling.setMaxHealth(wraithling.getHealth());
        player.getUnitList().put(wraithling.getId(), wraithling);

        System.out.println("Unit created: " + wraithling.getClass());

        return wraithling;
    }
}
