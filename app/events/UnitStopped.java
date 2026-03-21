package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * Indicates that a unit instance has stopped moving.
 * The event reports the unique id of the unit.
 */
public class UnitStopped implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int unitid = message.get("id").asInt();

		if (!gameState.isUnitMoving()) return;
		if (gameState.getMovingUnit() == null) return;
		if (gameState.getMoveTargetTile() == null) return;

		Unit movingUnit = gameState.getMovingUnit();

		if (movingUnit.getId() != unitid) return;

		Tile oldTile = gameState.getBoard().getTile(
				movingUnit.getPosition().getTilex(),
				movingUnit.getPosition().getTiley()
		);

		Tile targetTile = gameState.getMoveTargetTile();

		if (oldTile != null) {
			oldTile.setUnit(null);
		}

		movingUnit.setPositionByTile(targetTile);
		targetTile.setUnit(movingUnit);

		gameState.setMovingUnit(null);
		gameState.setMoveTargetTile(null);
		gameState.setUnitMoving(false);
	}
}