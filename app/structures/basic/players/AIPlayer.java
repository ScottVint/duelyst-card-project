package structures.basic.players;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.BetterUnit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

// Called AIPlayer for our purposes, but more realistically
// can be used as a player 2.
//                                      --Scott
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
        this.mana = mana;
        BasicCommands.setPlayer2Mana(out, this);
    }

    @Override
    public void setAvatar() {
        this.avatar = (BetterUnit) BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, useUnitId(), BetterUnit.class);
    }
}

