package structures.basic.players;

import akka.actor.ActorRef;
import commands.BasicCommands;

public class AIPlayer extends structures.basic.Player{
    public AIPlayer() {
        super();
    }

    public void setHealth(ActorRef out, int health) {
        this.health = health;
        BasicCommands.setPlayer2Health(out, this);
    }

    public void setMana(ActorRef out, int mana) {
        this.mana = mana;
        BasicCommands.setPlayer2Mana(out, this);
    }
}

