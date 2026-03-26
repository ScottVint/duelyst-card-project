package structures.basic.unittypes;

import akka.actor.ActorRef;
import structures.GameState;

public class Shadowdancer extends Unit {

    @Override
    public void deathwatch(ActorRef out, GameState gameState) {
        // Heal the friendly avatar by 1, capped at maxHealth to prevent overhealing
        BetterUnit ownAvatar = this.owner.getAvatar();
        if (ownAvatar != null) {
            int newHealth = Math.min(ownAvatar.getHealth() + 1, ownAvatar.getMaxHealth());
            ownAvatar.setHealth(out, ownAvatar.getOwner(), newHealth);
        }

        // Deal 1 damage to the enemy avatar directly
        BetterUnit enemyAvatar = null;
        if (this.owner == gameState.getPlayer1()) {
            enemyAvatar = gameState.getPlayer2().getAvatar();
        } else {
            enemyAvatar = gameState.getPlayer1().getAvatar();
        }

        if (enemyAvatar != null) {
            enemyAvatar.takeDamage(out, gameState, 1);
        }
    }
}

