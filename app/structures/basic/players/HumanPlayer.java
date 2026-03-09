package structures.basic.players;

import akka.actor.ActorRef;
import commands.BasicCommands;

/**
 * This class represents the human player of the game.
 * 2 different classes are required to be able to use the frontend commands.
 */
public class HumanPlayer extends Player {

    public HumanPlayer() {
        super();
    }

    @Override
    public void setHealth(ActorRef out, int health) {
        this.health = health;
        BasicCommands.setPlayer1Health(out, this);
    }

    @Override
    public void setMana(ActorRef out, int mana) {
        this.mana = mana;
        BasicCommands.setPlayer1Mana(out, this);
    }
}