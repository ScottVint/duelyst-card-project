package structures;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	public boolean gameInitalised = false;
	
	public boolean something = false;

	public boolean player1Turn = true; // Tracks current active turn

	public void advanceTurn() {
		player1Turn = !player1Turn;
	}
	
}
