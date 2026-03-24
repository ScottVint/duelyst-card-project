package structures.logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import structures.basic.unittypes.Unit;
import structures.basic.Board;
import java.util.Set;

public class CombatLogic {

    public static void tryAttackSelectedUnit(ActorRef out, GameState gameState, Tile target) {
        Unit attacker = gameState.getSelectedUnit();

        if (attacker == null || target == null || target.getUnit() == null) {
            return;
        }

        int tileX = attacker.getPosition().getTilex();
        int tileY = attacker.getPosition().getTiley();
        Tile origin = gameState.getBoard().getTile(tileX, tileY);

        if (origin == null) {
            return;
        }

        // Only attack targets within the current legal range of attack.
        if (!BoardLogic.findValidAttackUnits(origin, attacker, gameState.getBoard()).contains(target)) {
            return;
        }

        Unit defender = target.getUnit();
        resolveCombat(out, gameState, attacker, defender);

        BoardLogic.clearSelection(out, gameState.getBoard());
        gameState.selectedUnit = null;
        gameState.selectedHandPosition = null;
        gameState.highlightedTiles.clear();
    }

    /**
     * Resolves the full combat sequence:
     * 1) attacker hits defender
     * 2) defender dies -> stop, no counterattack
     * 3) otherwise defender may counterattack once per turn
     */
    public static void resolveCombat(ActorRef out, GameState gameState, Unit attacker, Unit defender) {
        if (attacker == null || defender == null) {
            return;
        }

        if (attacker.isDead() || defender.isDead()) {
            return;
        }

        // Active attack
        BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
        BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.hit);
        gameState.dealDamage(out, attacker, defender);

        // After an attack, the unit cannot attack again this turn; if it has not moved before, it also loses the right to move.
        attacker.hasAttacked = true;
        attacker.hasMoved = true;

        // The Defender is dead and can't fight back.
        if (defender.isDead()) {
            return;
        }

        // Counterattack is only allowed if you haven't counterattacked in this round yet.
        if (!defender.hasCounterattacked) {
            BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.attack);
            BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.hit);
            gameState.dealDamage(out, defender, attacker);

            defender.hasCounterattacked = true;
        }
    }
    public static Tile findAutoAttackDestination(Unit attacker, Tile enemyTile, Board board) {
        if (attacker == null || enemyTile == null || enemyTile.getUnit() == null || board == null) {
            return null;
        }

        Tile origin = board.getTile(
                attacker.getPosition().getTilex(),
                attacker.getPosition().getTiley()
        );

        if (origin == null) {
            return null;
        }

        Set<Tile> moveTiles = BoardLogic.findValidMovement(origin, attacker, board);

        Tile bestTile = null;
        int bestDistance = Integer.MAX_VALUE;

        int enemyX = enemyTile.getTilex();
        int enemyY = enemyTile.getTiley();

        for (Tile moveTile : moveTiles) {
            Set<Tile> attackTilesFromMoveTile = BoardLogic.findValidAttackUnits(moveTile, attacker, board);

            if (attackTilesFromMoveTile.contains(enemyTile)) {
                int distance = Math.abs(moveTile.getTilex() - enemyX)
                        + Math.abs(moveTile.getTiley() - enemyY);

                if (bestTile == null
                        || distance < bestDistance
                        || (distance == bestDistance && moveTile.getTilex() < bestTile.getTilex())
                        || (distance == bestDistance
                        && moveTile.getTilex() == bestTile.getTilex()
                        && moveTile.getTiley() < bestTile.getTiley())) {
                    bestTile = moveTile;
                    bestDistance = distance;
                }
            }
        }

        return bestTile;
    }
}