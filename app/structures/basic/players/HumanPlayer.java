package structures.basic.players;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.unittypes.BetterUnit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

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
        super.setMana(out, mana);
        BasicCommands.setPlayer1Mana(out, this);
    }

    @Override
    public void setAvatar() {
        this.avatar = (BetterUnit) BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, BetterUnit.class); // TODO Replace 0 with dynamic ID function
    }

    @Override
    public boolean enoughMana(ActorRef out, int manaCost) {
        boolean hasEnough = manaCost < mana;
        if (!hasEnough) {
            BasicCommands.addPlayer1Notification(out, "Not enough mana.", 2);
        }
        return hasEnough;
    }
    @Override
    public String toString() {
        return "Player 1";
    }
}
