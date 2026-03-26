package structures.basic.unittypes;

import akka.actor.ActorRef;
import structures.GameState;

public class ShadowWatcher extends Unit {

    @Override
    public void deathwatch(ActorRef out, GameState gameState) {
        // Grants +1 Attack, +1 Max Health, and heals for 1
        this.setAttack(out, this.getAttack() + 1);
        this.setMaxHealth(this.getMaxHealth() + 1);
        this.setHealth(out, this.getHealth() + 1);
    }
}

