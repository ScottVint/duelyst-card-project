package structures.basic.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Tile;
import structures.basic.players.Player;
import structures.basic.unittypes.BetterUnit;
import structures.basic.unittypes.Unit;
import structures.basic.unittypes.Wraithling;
import structures.logic.CombatLogic;

import java.util.HashSet;
import java.util.Set;

public class DarkTerminus extends Spell {
    @Override
    public Set<Tile> validTargets(Player player, Board board) {
        Set<Tile> targets = new HashSet<Tile>();
        for (Tile[] row : board.getTiles()) {
            for (Tile tile : row) {
                if (tile.getUnit() != null &&
                        tile.getUnit().getOwner() != player && !(tile.getUnit() instanceof BetterUnit)) {
                    targets.add(tile);
                }
            }
        }
        return targets;
    }
    public void cast(ActorRef out, GameState gameState,
                     Player player, Tile clickedTile,
                     Board board, int cardIndex) {
        Unit enemy = clickedTile.getUnit();
        CombatLogic.death(out, gameState, enemy);
        Wraithling summon = Unit.createWraithling(out, player, gameState);
        Unit.summonWraithling(out, clickedTile, player, gameState);
    }
}
