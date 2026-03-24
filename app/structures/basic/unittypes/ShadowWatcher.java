package structures.basic.unittypes;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.UnitAnimationType;

public class ShadowWatcher extends Unit {

    @Override
    public void deathwatch(ActorRef out, GameState gameState) {
        // 1. 播放增益动画，让你清楚地看到它触发了！
        BasicCommands.playUnitAnimation(out, this, UnitAnimationType.attack);

        try {
            Thread.sleep(500); // 稍微等半秒，让动画播出来
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 2. 增加最大生命并回血
        this.setMaxHealth(this.getMaxHealth() + 1);
        this.setHealth(out, this.getHealth() + 1);

        // 3. 增加攻击力 (调用 setAttack 会自动发送指令给前端更新面板)
        this.setAttack(out, this.getAttack() + 1);
    }
}