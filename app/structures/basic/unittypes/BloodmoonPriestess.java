package structures.basic.unittypes;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Tile;
import structures.logic.BoardLogic;
import java.util.ArrayList;
import java.util.List;

public class BloodmoonPriestess extends Unit {

    @Override
    public void deathwatch(ActorRef out, GameState gameState) {
        List<Tile> adjacentTiles = new ArrayList<>(BoardLogic.findAdjacentTiles(this.currentTile, gameState.getBoard()));
        adjacentTiles.removeIf(tile -> tile.getUnit() != null);
        if (!adjacentTiles.isEmpty()) {
            int idx = (int) (Math.random() * adjacentTiles.size());
            Tile targetTile = adjacentTiles.get(idx);
            summonWraithling(out, targetTile, this.owner, gameState);
        }
    }
}