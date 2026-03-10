package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

// Import the necessary structural classes
import structures.basic.BetterUnit;
import structures.basic.Card;
import structures.basic.players.Player;
import structures.basic.Unit;
import structures.basic.Tile;
import java.util.List;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * <p>
 * Handles Story Card #18: initialises avatars (HP, position, ownership) and
 * deals 3 starting cards to each player.
 * <pre>
 * {
 *   messageType = "initalize"
 * }
 * </pre>
 *
 * @author Dr. Richard McCreadie
 * @author Minghao
 */
public class Initalize implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
        // Mark the game state as initialized
        gameState.gameInitalised = true;

        // Display tiles on the board
        gameState.getBoard().clearSelection(out);

        // Retrieve player objects (Assuming player1 and player2 are already instantiated within gameState)
        Player player1 = gameState.getPlayer1();
        Player player2 = gameState.getPlayer2();

        // Retrieve the Avatar units for both players
        BetterUnit avatar1 = player1.getAvatar();
        BetterUnit avatar2 = player2.getAvatar();

        // Set avatar ownership so that unit-selection logic (Story Card #3) can
        // correctly identify which units belong to the human player.
        // Without this, getOwner() returns null and avatar clicks are ignored.
        // TODO consolidate to summoning method when it is created
        avatar1.setOwner(player1); // @author Minghao
        avatar2.setOwner(player2); // @author Minghao

        // ==========================================
        // Story Card #18 Acceptance Test: Both players and their avatars have initial HP set to 20 [cite: 160]
        // ==========================================
        // TODO consolidate to my linked methods -- Scott
        player1.setHealth(20);
        player2.setHealth(20);

        avatar1.setHealth(20);
        avatar1.setMaxHealth(20);

        avatar2.setHealth(20);
        avatar2.setMaxHealth(20);

        // Send commands to the front-end to update the players' health display
        BasicCommands.setPlayer1Health(out, player1);
        BasicCommands.setPlayer2Health(out, player2);

        // Player starts their first turn with 2 mana [cite: SC6]

        player1.setMana(out, 2);
//        BasicCommands.setPlayer1Mana(out, player1);

        // ==========================================
        // Story Card #18 Acceptance Test: Set initial avatar positions
        // ==========================================

        // TODO place this in a method
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
            player1.drawCardIntoHand();
            player2.drawCardIntoHand();
        }

        gameState.player1.drawHand(out);

        // Note: As per the template's instructions, comment out the demo execution when implementing your own solution
        // CommandDemo.executeDemo(out);
    }
}
