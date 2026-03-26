package structures.basic.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.EffectAnimation;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import structures.basic.players.Player;
import structures.basic.unittypes.BetterUnit;
import structures.basic.unittypes.Unit;
import structures.basic.unittypes.Wraithling;
import structures.logic.CombatLogic;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

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

  @Override
    public void cast(ActorRef out, GameState gameState,
                     Player player, Tile clickedTile) {
        BasicCommands.playUnitAnimation(out, player.getAvatar(), UnitAnimationType.channel);
        EffectAnimation effect = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_martyrdom);
        try { Thread.sleep(BasicCommands.playEffectAnimation(out, effect, clickedTile)); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        
        Unit enemy = clickedTile.getUnit();
        enemy.die(out, gameState);
        
        BasicCommands.playUnitAnimation(out, player.getAvatar(), UnitAnimationType.idle);
    
        Wraithling summon = Unit.createWraithling(out, player, gameState);
        Unit.summonWraithling(out, clickedTile, player, gameState);
    }
}
