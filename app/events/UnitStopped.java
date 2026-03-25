package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.logic.BoardLogic;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import structures.basic.unittypes.Unit;

import structures.logic.CombatLogic;
/**
 * Indicates that a unit instance has stopped moving.
 * The event reports the unique id of the unit.
 */
public class UnitStopped implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
        // Clear all highlights and selection after unit finishes moving
        BoardLogic.clearSelection(out, gameState.board);
        gameState.selectedUnit = null;
		gameState.highlightedTiles.clear();

		int unitid = message.get("id").asInt();

		if (!gameState.unitMoving) return;
		if (gameState.movingUnit == null) return;
		if (gameState.moveTargetTile == null) return;

		Unit movingUnit = gameState.movingUnit;

		if (movingUnit.getId() != unitid) return;

		Tile oldTile = gameState.getBoard().getTile(
				movingUnit.getPosition().getTilex(),
				movingUnit.getPosition().getTiley()
		);

		Tile targetTile = gameState.moveTargetTile;

		if (oldTile != null) {
			oldTile.setUnit(null);
		}

		movingUnit.setPositionByTile(targetTile);
		targetTile.setUnit(movingUnit);

// Movement is now fully complete
		movingUnit.hasMoved = true;
		BasicCommands.playUnitAnimation(out, movingUnit, UnitAnimationType.idle);

// Snapshot pending move-then-attack state before cleanup
		boolean shouldResolvePendingAttack =
				gameState.pendingAttackAfterMove
						&& gameState.pendingAttackAttacker != null
						&& gameState.pendingAttackTargetTile != null
						&& gameState.pendingAttackAttacker.getId() == movingUnit.getId();

		Tile pendingTargetTile = gameState.pendingAttackTargetTile;

// Always clear movement state now that the walk has ended
		gameState.movingUnit = null;
		gameState.moveTargetTile = null;
		gameState.unitMoving = false;

// If this move was part of story #30, try to resolve the queued attack now
		if (shouldResolvePendingAttack) {
			Unit defender = pendingTargetTile.getUnit();

			// Only attack if target still exists, is still an enemy,
			// and is still a legal attack target from the new position
			Tile attackerTile = gameState.pendingAttackAttacker.getCurrentTile();

			boolean validPendingAttack =
					defender.getOwner() != movingUnit.getOwner()
					&& BoardLogic.findValidAttackUnits(attackerTile, movingUnit, gameState.getBoard())
							.contains(pendingTargetTile);

			if (validPendingAttack)
				CombatLogic.resolveCombat(out, gameState, movingUnit, defender);

			gameState.clearPendingAttackAfterMove();
		}
	}
}