package structures.basic.unittypes;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.UnitAnimationType;

public class BadOmen extends Unit {

    @Override
    public void deathwatch(ActorRef out, GameState gameState) {
        BasicCommands.playUnitAnimation(out, this, UnitAnimationType.attack);
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
        // 每次死人，攻击力 +1
        this.setAttack(out, this.getAttack() + 1);
    }
}