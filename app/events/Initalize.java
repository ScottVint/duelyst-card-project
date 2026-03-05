package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CommandDemo;
import demo.Loaders_2024_Check;
import structures.GameState;
import structures.basic.BetterUnit;
import structures.basic.Board;
import structures.basic.players.*;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gs, JsonNode message) {

		gs.gameInitalised = true;
		
		gs.something = true;
		// Create board and set tiles to white
		gs.board = new Board();
		gs.board.clearSelection(out);

		// Draw cards
		//TODO Change for proper draw at start of turn
		for(int i = 0; i < 3; i++) {
			gs.player.drawToHand();
			gs.ai.drawToHand();
		}

		// Draw cards to screen
		gs.player.getHand().showHand(out);

		//Temporary init usage
		//TODO replace this with proper summoning methods
		BetterUnit playerAvatar = (BetterUnit) BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, BetterUnit.class);
		BetterUnit aiAvatar = (BetterUnit) BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 0, BetterUnit.class);

		//TODO combine these
		gs.player.addUnit(0, playerAvatar);
		gs.ai.addUnit(0, aiAvatar);
		playerAvatar.setPositionByTile(gs.board.getTiles()[2][3]);
		playerAvatar.setPositionByTile(gs.board.getTiles()[2][3]);
		gs.board.getTiles()[2][3].setUnitOnTile(playerAvatar);
		gs.board.getTiles()[8][3].setUnitOnTile(aiAvatar);
		BasicCommands.drawUnit(out, playerAvatar, gs.board.getTiles()[2][3]);
		BasicCommands.drawUnit(out, aiAvatar, gs.board.getTiles()[8][3]);



		
		// User 1 makes a change
//		CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//Loaders_2024_Check.test(out);
	}

}


