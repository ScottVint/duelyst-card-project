package structures.basic.unittypes;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Tile;
import structures.logic.BoardLogic;
import structures.logic.CombatLogic;

import java.util.ArrayList;
import java.util.List;

public class NightsorrowAssassin extends Unit {
    @Override
    public void openingGambit(ActorRef out, GameState gameState) {
        List<Tile> enemies = new ArrayList<>(BoardLogic.findAdjacentTiles(this.tileOccupied, gameState.getBoard()));
        // Remove if: No unit on tile, unit is avatar, unit is at max HP
        enemies.removeIf(tile -> tile.getUnit() == null || tile.getUnit().getClass() == BetterUnit.class);
        enemies.removeIf(tile -> tile.getUnit().getHealth() == tile.getUnit().getMaxHealth() || tile.getUnit().getOwner() == this.owner);
        int idx = (int) (Math.random() * enemies.size());

        if (!enemies.isEmpty()) {
            Unit enemy = enemies.get(idx).getUnit();
            CombatLogic.death(out, gameState, enemy);
        }
    }
}
