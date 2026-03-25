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
        // 获取相邻的所有 Tile
        List<Tile> adjacentTiles = new ArrayList<>(BoardLogic.findAdjacentTiles(this.currentTile, gameState.getBoard()));

        // 过滤掉已经有单位的 Tile，只保留空位
        adjacentTiles.removeIf(tile -> tile.getUnit() != null);

        // 如果有空位，随机选择一个并召唤 Wraithling
        if (!adjacentTiles.isEmpty()) {
            int idx = (int) (Math.random() * adjacentTiles.size());
            Tile targetTile = adjacentTiles.get(idx);
            summonWraithling(out, targetTile, this.owner, gameState);
        }
    }
}