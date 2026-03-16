package structures.basic.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Tile;
import structures.basic.players.Player;
import structures.basic.unittypes.BetterUnit;
import structures.basic.unittypes.Unit;

import java.util.HashSet;
import java.util.Set;

public class Truestrike extends Spell {
    public Set<Tile> validTargets(Player player, Board board) {
        Set<Tile> validTargets = new HashSet<>();
                if (tile.getUnit() \!= null && tile.getUnit().getOwner() \!= player) {
            for (Tile tile : row) {
                if (tile.getUnit().getOwner() != player) {
                    validTargets.add(tile);
                }
            }
        }
        return validTargets;
    }

    public void cast(ActorRef out, GameState gameState, Player player, Tile clickedTile, Board board, int cardIndex) {
        Unit enemy = clickedTile.getUnit();
        enemy.takeDamage(out, 2);
        ;
    }
}
