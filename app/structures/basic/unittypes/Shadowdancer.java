package structures.basic.unittypes;

import akka.actor.ActorRef;
import structures.GameState;

public class Shadowdancer extends Unit {

    @Override
    public void deathwatch(ActorRef out, GameState gameState) {
        // 1. 给己方主将恢复 1 点生命值
        BetterUnit ownAvatar = this.owner.getAvatar();
        if (ownAvatar != null) {
            // setHealth 内部已做了不可超过 MaxHealth 的限制
            ownAvatar.setHealth(out, ownAvatar.getOwner(), ownAvatar.getHealth() + 1);
        }

        // 2. 对敌方主将造成 1 点伤害
        // 遍历棋盘寻找敌方主将（BetterUnit 且所有者不是自己）
        for (int x = 0; x < gameState.getBoard().getTiles().length; x++) {
            for (int y = 0; y < gameState.getBoard().getTiles()[x].length; y++) {
                Unit unit = gameState.getBoard().getTile(x, y).getUnit();
                // 检查是否是敌方主将
                if (unit != null && unit instanceof BetterUnit && unit.getOwner() != this.owner) {
                    unit.takeDamage(out, gameState, 1);
                    return; // 找到了敌方主将并造成伤害后即可结束循环
                }
            }
        }
    }
}