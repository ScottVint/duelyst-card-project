package structures.basic.spells;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.EffectAnimation;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import structures.basic.players.Player;
import structures.basic.unittypes.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.HashSet;
import java.util.Set;

public class Truestrike extends Spell {
    @Override
    public Set<Tile> validTargets(Player player, Board board) {
        Set<Tile> validTargets = new HashSet<>();
        for (Tile[] row : board.getTiles()) {
            for (Tile tile : row) {
                // null-check required: empty tiles return null from getUnit()
                if (tile.getUnit() != null && tile.getUnit().getOwner() != player) {
                    validTargets.add(tile);
                }
            }
        }
        return validTargets;
    }

    @Override
    public void cast(ActorRef out, GameState gameState, Player player, Tile clickedTile) {
        Unit enemy = clickedTile.getUnit();

        BasicCommands.playUnitAnimation(out, player.getAvatar(), UnitAnimationType.channel);
        EffectAnimation effect = BasicObjectBuilders.loadEffect("conf/gameconfs/effects/f1_projectilesummon.json");
        // Create projectile
        try { Thread.sleep(BasicCommands.playEffectAnimation(out, effect, player.getAvatar().getCurrentTile())); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        // Fire projectile
        EffectAnimation projectile = BasicObjectBuilders.loadEffect("conf/gameconfs/effects/f1_projectiles.json");
        BasicCommands.playProjectileAnimation(out, projectile, 0, player.getAvatar().getCurrentTile(), clickedTile);
        try { Thread.sleep(1000); }
        // Projectile hits
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        try { Thread.sleep(BasicCommands.playEffectAnimation(out, projectile, clickedTile) / 4 * 3); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        // Hurt animation plays
        try { Thread.sleep(BasicCommands.playUnitAnimation(out, enemy, UnitAnimationType.hit)); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        BasicCommands.playUnitAnimation(out, enemy, UnitAnimationType.idle);
        enemy.takeDamage(out, gameState, 2);

        BasicCommands.playUnitAnimation(out, player.getAvatar(), UnitAnimationType.idle);
    }
}
