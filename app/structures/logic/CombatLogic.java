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

    public static void tryAttackSelectedUnit(ActorRef out, GameState gameState, Unit defender) {
        Unit attacker = gameState.getSelectedUnit();

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

        // Active attack
        BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
        BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.hit);
        defender.takeDamage(out, attacker.getAttack());

        // After an attack, the unit cannot attack again this turn; if it has not moved before, it also loses the right to move.
        attacker.hasAttacked = true;
        attacker.hasMoved = true;

        // Counterattack is only allowed if you haven't counterattacked in this round yet, and is defender isn't dead.
        if (!defender.isDead() && !defender.hasCounterattacked) {
            BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.attack);
            BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.hit);
            attacker.takeDamage(out, gameState, defender.getAttack());

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