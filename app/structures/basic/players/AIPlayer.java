package structures.basic.players;

import akka.actor.ActorRef;
import commands.BasicCommands;

// Called AIPlayer for our purposes, but more realistically
// can be used as a player 2.
public class AIPlayer extends Player {

    public AIPlayer() {
        super();
    }

    @Override
    public void setHealth(ActorRef out, int health) {
        this.health = health;
        BasicCommands.setPlayer2Health(out, this);
    }

    @Override
    public void setMana(ActorRef out, int mana) {
        super.setMana(out, mana);
        BasicCommands.setPlayer2Mana(out, this);
    }


}

