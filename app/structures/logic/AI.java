package structures.logic;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Unit;
import structures.basic.players.AIPlayer;
import structures.basic.players.HumanPlayer;

import java.util.Timer;
import java.util.TimerTask;

public class AI {
    // Logic flags to track what actions AI can perform
    private static boolean canSummon = true;
    private static boolean canMove = true;
    private static boolean canAttack = true;
    private static boolean hasTime = true;



    public static class AILogic {

        private AILogic() {};

        // Timer.
        private static Timer timer;

        public static class CheckTimeTask extends TimerTask {
            public void run() {
                hasTime = false;
                timer.cancel();
            }
        }


        // TODO Write general methods in another class to use here

        // Summons a new unit and reduces mana
        public static void summon() {}

        // Checks if AI can summon more units and flips flag if false
        public void checkSummon() {}

        // Moves unit to a valid location
        public static void moveUnit(Unit u) {}

        // Checks if AI can move any units and flips flag if false
        public void checkMove() {}

        // Makes unit attack an enemy unit
        public static void attack() {}

        // Checks if AI has any more units that can attack and flips flag if false
        public void canAttack() {}

        // Runs a timer that flips the hasTime flag at the end
        public static void time() {
            // TODO Change to more realistic number
            int seconds = 5;
            timer = new Timer();
            timer.schedule(new CheckTimeTask(), seconds*1000);
            // Test code remove later
            int i = 0;
            while(i < 5) {
                System.out.println("Time passed: " + i + " seconds");
                try {Thread.sleep(1000);} catch (InterruptedException e) {throw new RuntimeException(e);}
                i++;
            }
        }

        // Runs all associated methods with AILogic and ends when conditions are met.
        public static void runAI(ActorRef out, GameState gs, HumanPlayer p1, AIPlayer p2) {
            time();
            while((canSummon || canMove || canAttack) && hasTime) {
                // TODO Fill in actual logic
            }
            canSummon = true;
            canMove = true;
            canAttack = true;
            hasTime = true;
            // TODO: trigger end-of-AI-turn via EndTurnClicked event
        }




    }
}
