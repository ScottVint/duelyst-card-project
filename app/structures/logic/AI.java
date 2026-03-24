package structures.logic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.unittypes.Unit;
import structures.basic.players.Player;

import java.util.Timer;
import java.util.TimerTask;

public class AI {

    private static boolean canSummon = true;
    private static boolean canMove = true;
    private static boolean canAttack = true;
    private static int maxTime = 5; //TODO Change to something more reasonable
    private static int timePassed = 0;

    public static void passTime() {
        timePassed++;
    }
    public static class AILogic {

        private AILogic() {}

        public static void summon() {
            // TODO: implement later
        }

        public void checkSummon() {
            // TODO: implement later
        }

        public static void moveUnit(Unit u) {
            // TODO: implement later
        }

        public void checkMove() {
            // TODO: implement later
        }

        public static void attack() {
            // TODO: implement later
        }

        public void checkAttack() {
            // TODO: implement later
        }

        public static void runAI(ActorRef out, GameState gs, Player p1, Player p2) {
            // AI requires a live WebSocket connection; skip during unit tests
            if (out == null) return;
            BasicCommands.addPlayer1Notification(out, "AI's Turn", 2);
            // 2. 让后端线程暂停 2000 毫秒（2秒），模拟 AI 在思考
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            while ((canSummon || canMove || canAttack) && (timePassed < maxTime)) {
//                // TODO: implement actual AI later
////                System.out.println(timePassed);
//                continue;
//            }
            timePassed = 0;
            gs.endTurn(out, p2, p1);

            BasicCommands.addPlayer1Notification(out, "Your Turn", 2);
        }
    }
}