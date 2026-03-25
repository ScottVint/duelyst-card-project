package structures.basic.unittypes;

import akka.actor.ActorRef;
import structures.GameState;

public class BadOmen extends Unit {

    @Override
    public void deathwatch(ActorRef out, GameState gameState) {
        // 增加 1 点攻击力并同步给前端
        this.setAttack(out, this.getAttack() + 1);
    }
}