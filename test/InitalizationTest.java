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
	 * NEW TEST: verify starting mana behaviour (without accessing protected fields)
	 */
	@Test
	public void checkStartingMana() {
		
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		
		GameState gameState = new GameState();
		Initalize initalizeProcessor = new Initalize();
		
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage);
		
		
		assertTrue(gameState.getPlayer1().enoughMana(null, 2));
		
		assertFalse(gameState.getPlayer1().enoughMana(null, 3));
		
	
		assertFalse(gameState.getPlayer2().enoughMana(null, 1));
	}
}