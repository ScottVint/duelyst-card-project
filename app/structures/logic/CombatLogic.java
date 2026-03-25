package structures.logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Tile;
import structures.basic.players.Player;
import structures.basic.unittypes.Unit;
import structures.basic.UnitAnimationType;

import java.util.Set;

public class CombatLogic {
    public static void tryAttackSelectedUnit(ActorRef out, GameState gameState, Tile target) {
        Unit attacker = gameState.getSelectedUnit();
        int[] attackerPosition = {attacker.getPosition().getTilex(), attacker.getPosition().getTiley()};
        Tile origin = gameState.getBoard().getTile(attackerPosition[0], attackerPosition[1]);

        if (BoardLogic.findValidAttackUnits(origin, attacker, gameState.getBoard()).contains(target)) {

            Unit defender = target.getUnit();
            BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
            BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.hit);
            gameState.dealDamage(out, attacker, defender);

            BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.idle);
            if (defender.getHealth() > 0) {
                BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.idle);
            }

            BoardLogic.clearSelection(out, gameState.getBoard());
            gameState.selectedUnit = null;
            gameState.selectedHandPosition = null;
        }
    }

    public static Set<Tile> findUnitsWithProvokeAdjacent(Board board, Unit startingUnit) {
        Set<Tile> targets = BoardLogic.findAdjacentTiles(startingUnit.getCurrentTile(), board);
        Player unitOwner = startingUnit.getOwner();
        targets.removeIf(tile -> tile.getUnit() == null || !tile.getUnit().hasProvoke() || tile.getUnit().getOwner().equals(unitOwner));

        return targets;
    }


}
