package structures.basic.unittypes;

import akka.actor.ActorRef;
import structures.GameState;

public class ShadowWatcher extends Unit {

    @Override
    public void deathwatch(ActorRef out, GameState gameState) {
        // 增加攻击力
        this.setAttack(out, this.getAttack() + 1);

        // 增加生命值（注意：必须先提升最大生命值上限，否则加血会无效）
        this.setMaxHealth(this.getMaxHealth() + 1);
        this.setHealth(out, this.getHealth() + 1);
    }
}