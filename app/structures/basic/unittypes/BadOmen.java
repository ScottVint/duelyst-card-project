package structures.basic.unittypes;

import akka.actor.ActorRef;
import structures.GameState;

public class BadOmen extends Unit {

    @Override
    public void deathwatch(ActorRef out, GameState gameState) {
        this.setAttack(out, this.getAttack() + 1); // Increase attack power by 1 point
    }
}

