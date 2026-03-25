import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import events.Initalize;
import play.libs.Json;
import structures.GameState;
import structures.basic.Tile;
import utils.BasicObjectBuilders;

/**
 * Initialization tests
 */
public class InitalizationTest {

	@Test
	public void checkInitalized() {
		
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		
		GameState gameState = new GameState();
		Initalize initalizeProcessor = new Initalize();
		
		assertFalse(gameState.gameInitalised);
		
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage);
		
		assertTrue(gameState.gameInitalised);
		
		Tile tile = BasicObjectBuilders.loadTile(3, 2);
		BasicCommands.drawTile(null, tile, 0);
	}

	/**
	 * Verify that exactly one player starts with 2 mana and the other starts with 0.
	 * This test does not assume which player starts first.
	 */
	@Test
	public void checkStartingMana() {
		
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		
		GameState gameState = new GameState();
		Initalize initalizeProcessor = new Initalize();
		
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage);
		
		boolean player1Starts = gameState.getPlayer1().enoughMana(null, 2)
				&& !gameState.getPlayer1().enoughMana(null, 3)
				&& !gameState.getPlayer2().enoughMana(null, 1);

		boolean player2Starts = gameState.getPlayer2().enoughMana(null, 2)
				&& !gameState.getPlayer2().enoughMana(null, 3)
				&& !gameState.getPlayer1().enoughMana(null, 1);

		assertTrue(player1Starts || player2Starts);
	}
}