package structures.basic.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Tile;
import structures.basic.players.Player;
import structures.basic.unittypes.BetterUnit;

import java.util.HashSet;
import java.util.Set;

public class HornOfTheForsaken extends Spell {

    // Returns only the related avatar unit.
    @Override
    public Set<Tile> validTargets(Player player, Board board) {
        Set<Tile> targets =  new HashSet<Tile>();
        int x = player.getAvatar().getPosition().getTilex();
        int y = player.getAvatar().getPosition().getTiley();
        Tile target = board.getTile(x, y);
        targets.add(target);
        return targets;
    }

    @Override
    public void cast(ActorRef out, GameState gameState,
                     Player player, Tile clickedTile,
                     Board board, int cardIndex) {
        player.getAvatar().setHornCharges(player.getAvatar().MAX_HORN_CHARGES);
        BasicCommands.addPlayer1Notification(out, "Horn equipped", 1);
    }
}

