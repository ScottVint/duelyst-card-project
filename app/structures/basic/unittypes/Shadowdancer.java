package structures.basic.unittypes;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.UnitAnimationType;
import structures.basic.players.Player;

public class Shadowdancer extends Unit {

    @Override
    public void deathwatch(ActorRef out, GameState gameState) {
        BasicCommands.playUnitAnimation(out, this, UnitAnimationType.attack);
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
        // 1. 找到双方玩家
        Player myPlayer = this.getOwner();
        Player enemyPlayer = (myPlayer == gameState.getPlayer1()) ? gameState.getPlayer2() : gameState.getPlayer1();

        // 2. 我方主将回血 (上限不超)
        Unit myAvatar = myPlayer.getAvatar();
        if (myAvatar != null && !myAvatar.isDead()) {
            myAvatar.setHealth(out, myPlayer, myAvatar.getHealth() + 1);
        }

        // 3. 敌方主将扣血
        Unit enemyAvatar = enemyPlayer.getAvatar();
        if (enemyAvatar != null && !enemyAvatar.isDead()) {
            // 调用 GameState 的统一伤害处理中心，它会自动扣血并判断主将是否死亡
            gameState.dealDirectDamage(out, enemyAvatar, 1);
        }
    }
}