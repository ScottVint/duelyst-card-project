package structures.basic.unittypes;

import akka.actor.ActorRef;
import structures.GameState;

public class ShadowWatcher extends Unit {

    @Override
    public void deathwatch(ActorRef out, GameState gameState) {

        this.setAttack(out, this.getAttack() + 1);  // add attack point
        this.setMaxHealth(this.getMaxHealth() + 1);
        this.setHealth(out, this.getHealth() + 1);
    }
}