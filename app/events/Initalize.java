package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import demo.CommandDemo;
import demo.Loaders_2024_Check;
import structures.GameState;

// Import the necessary structural classes
import structures.basic.Player;
import structures.basic.Unit;
import structures.basic.Tile;
import commands.BasicCommands;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * * {
 * messageType = "initalize"
 * }
 * * @author Dr. Richard McCreadie
 */
public class Initalize implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
        // Mark the game state as initialized
        gameState.gameInitalised = true;

        // Retrieve player objects (Assuming player1 and player2 are already instantiated within gameState)
        Player player1 = gameState.getPlayer1();
        Player player2 = gameState.getPlayer2();

        // Retrieve the Avatar units for both players
        Unit avatar1 = player1.getAvatar();
        Unit avatar2 = player2.getAvatar();

        // ==========================================
        // Story Card #18 Acceptance Test: Both players and their avatars have initial HP set to 20 [cite: 160]
        // ==========================================
        player1.setHealth(20);
        player2.setHealth(20);
        
        avatar1.setHealth(20);
        avatar1.setMaxHealth(20);
        
        avatar2.setHealth(20);
        avatar2.setMaxHealth(20);
        
        // Send commands to the front-end to update the players' health display
        BasicCommands.setPlayer1Health(out, player1);
        BasicCommands.setPlayer2Health(out, player2);

        // ==========================================
        // Story Card #18 Acceptance Test: Set initial avatar positions
        // ==========================================
        
        // Player 1 avatar starts at [2,3] [cite: 161]
        Tile tileP1 = gameState.getBoard().getTile(2, 3);
        tileP1.setUnit(avatar1);
        avatar1.setPositionByTile(tileP1);
        BasicCommands.drawUnit(out, avatar1, tileP1); // Notify front-end to render the unit on the board
        BasicCommands.setUnitHealth(out, avatar1, avatar1.getHealth());
        BasicCommands.setUnitAttack(out, avatar1, avatar1.getAttack());

        // Player 2 avatar starts at [8,3] [cite: 161]
        Tile tileP2 = gameState.getBoard().getTile(8, 3);
        tileP2.setUnit(avatar2);
        avatar2.setPositionByTile(tileP2);
        BasicCommands.drawUnit(out, avatar2, tileP2); // Notify front-end to render the unit on the board
        BasicCommands.setUnitHealth(out, avatar2, avatar2.getHealth());
        BasicCommands.setUnitAttack(out, avatar2, avatar2.getAttack());

        // ==========================================
        // Story Card #18 Acceptance Test: Each player starts with 3 cards drawn from the deck [cite: 162]
        // ==========================================
        for (int i = 0; i < 3; i++) {
            player1.drawCard();
            player2.drawCard();
        }
        
        // Notify front-end to render Player 1's initial hand (Assuming Player 1 is the local human player)
        // BasicCommands.drawHand(out, player1.getHand());

        // Note: As per the template's instructions, comment out the demo execution when implementing your own solution
        // CommandDemo.executeDemo(out); 
    }
}


