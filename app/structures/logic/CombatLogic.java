package structures.logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Tile;
import structures.basic.unittypes.Unit;
import structures.basic.UnitAnimationType;

public class CombatLogic {
    public static void tryAttackSelectedUnit(ActorRef out, GameState gameState, Tile target, Board board) {
        Unit attacker = gameState.getSelectedUnit();
        int[] attackerPosition = {attacker.getPosition().getTilex(), attacker.getPosition().getTiley()};
        Tile origin = board.getTile(attackerPosition[0], attackerPosition[1]);

        if (BoardLogic.findValidAttackUnits(origin, attacker, board).contains(target)) {

            BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
            BasicCommands.playUnitAnimation(out, target.getUnit(), UnitAnimationType.hit);

            gameState.dealDamage(out, attacker, target.getUnit());

            BoardLogic.clearSelection(out, board);
            gameState.selectedUnit = null;
            gameState.selectedHandPosition = null;
        }
    }

    /**
     * Remove a dead unit from the board and UI.
     */
    public static void death(ActorRef out, GameState gameState, Unit target) {
        Tile tile = gameState.board.getTile(target.getPosition().getTilex(), target.getPosition().getTiley());

        tile.setUnit(null);
        BasicCommands.deleteUnit(out, target);
    }
}
