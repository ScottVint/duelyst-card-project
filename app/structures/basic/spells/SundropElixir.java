package structures.basic.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Tile;
import structures.basic.players.Player;
import structures.basic.unittypes.Unit;

import java.util.HashSet;
import java.util.Set;

public class SundropElixir extends Spell {
    @Override
    public Set<Tile> validTargets(Player player, Board board) {
        Set<Tile> targets = new HashSet<Tile>();
        for (Unit ally : player.getUnitList().values()) {
            Tile tile = board.getTile(
                    ally.getPosition().getTilex(),
                    ally.getPosition().getTiley());
            targets.add(tile);
        }
        return targets;
    }

    @Override
    public void cast(ActorRef out, GameState gameState,
                     Player player, Tile clickedTile,
                     Board board, int cardIndex) {
        Unit target = clickedTile.getUnit();
        target.takeDamage(out, -4);
    }

}
