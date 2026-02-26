package structures;

import structures.basic.players.*;

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

	public void advanceTurn(HumanPlayer player1, AIPlayer player2) {
		player1Turn = !player1Turn;
		player1.setMana(0);
		player2.setMana(0);
		if(player1Turn) {
			System.out.println("Player 1 Turn");
		} else {
			System.out.println("Player 2 turn");
		}
	}
	
}
