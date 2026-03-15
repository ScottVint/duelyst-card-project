package structures.logic;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.unittypes.Unit;
import structures.basic.players.Player;

import java.util.Timer;
import java.util.TimerTask;

public class AI {

    private static boolean canSummon = true;
    private static boolean canMove = true;
    private static boolean canAttack = true;
    private static boolean hasTime = true;

    public static class AILogic {

        private AILogic() {}

        private static Timer timer;

        public static class CheckTimeTask extends TimerTask {
            @Override
            public void run() {
                hasTime = false;
                if (timer != null) {
                    timer.cancel();
                }
            }
        }

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

        public static void time() {
            int seconds = 5;
            timer = new Timer();
            timer.schedule(new CheckTimeTask(), seconds * 1000);
        }

        public static void runAI(ActorRef out, GameState gs, Player p1, Player p2) {
            time();

            while ((canSummon || canMove || canAttack) && hasTime) {
                // TODO: implement actual AI later
                break;
            }

            canSummon = true;
            canMove = true;
            canAttack = true;
            hasTime = true;
        }
    }
}