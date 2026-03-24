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
        // 1. 找到周围所有的相邻格子
        List<Tile> summonable = new ArrayList<>(BoardLogic.findAdjacentTiles(this.getCurrentTile(), gameState.getBoard()));

        // 2. 过滤掉那些已经有人的格子
        summonable.removeIf(tile -> tile.getUnit() != null);

        // 3. 如果还有空位，随机挑一个召唤小幽灵
        if (!summonable.isEmpty()) {
            int idx = (int) (Math.random() * summonable.size());
            Tile targetTile = summonable.get(idx);

            // 直接复用你在 Unit.java 里写好的神仙方法！
            Unit.summonWraithling(out, targetTile, this.getOwner(), gameState);
        }
    }
}