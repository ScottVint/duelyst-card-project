package structures;

import akka.actor.ActorRef;
import structures.basic.players.*;
import structures.logic.AI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import structures.basic.Tile;
import structures.basic.Unit;


/**
 * This class can be used to hold information about the on-going game.
 * It's created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */

public class GameState {

	
	public boolean gameInitalised = false;
	
	public boolean something = false;

	public boolean player1Turn = true; // Tracks current active turn


	// Player classes
	public HumanPlayer player = new HumanPlayer();
	public AIPlayer ai = new AIPlayer();

    // Selected unit
    public Unit selectedUnit = null;

    // Tiles and units captured from CommandDemo
    public Tile[][] board = new Tile[10][6]; // use indices 1..9 and 1..5
    public Map<Integer, Unit> unitsById = new HashMap<>();
    public Map<String, Integer> occupiedByUnitId = new HashMap<>(); // "x,y" -> unitId

    // For demo: identify player avatars by spawn tiles
    public Integer p1AvatarId = null; // unit on (2,3)
    public Integer p2AvatarId = null; // unit on (8,3)

    // Highlight caches
    public Set<String> validMoveTiles = new HashSet<>();
    public Set<String> validAttackTiles = new HashSet<>();

    // Animation lock
    public boolean isAnimating = false;

    public void advanceTurn(ActorRef out, HumanPlayer player1, AIPlayer player2) {
		player1Turn = !player1Turn;
		player1.setMana(out,0);
		player2.setMana(out,0);
		if(player1Turn) {
			System.out.println("Player 1 Turn");
		} else {
			System.out.println("Player 2 turn");
			AI.AILogic.runAI(out, this, player1, player2);
		}
	}

}

    public static String key(int x, int y) { return x + "," + y; }

    public boolean inBounds(int x, int y) {
        return x >= 1 && x <= 9 && y >= 1 && y <= 5;
    }

    public Integer unitIdAt(int x, int y) {
        return occupiedByUnitId.get(key(x, y));
    }

    public boolean isEmpty(int x, int y) {
        return unitIdAt(x, y) == null;
    }

    public void clearHighlights() {
        validMoveTiles.clear();
        validAttackTiles.clear();
    }

    public void clearSelection() {
        selectedUnit = null;
    }
}