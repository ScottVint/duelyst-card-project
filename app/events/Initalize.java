package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.BetterUnit;
import structures.basic.Tile;
import structures.basic.players.Player;
import structures.logic.BoardLogic;


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
        BoardLogic.clearSelection(out, gameState.getBoard());

        // Retrieve player objects
        Player player1 = gameState.getPlayer1();
        Player player2 = gameState.getPlayer2();

        // Retrieve the Avatar units for both players
        BetterUnit avatar1 = player1.getAvatar();
        BetterUnit avatar2 = player2.getAvatar();

        // Set avatar ownership so that unit-selection logic (Story Card #3) can
        // correctly identify which units belong to the human player.
        // Without this, getOwner() returns null and avatar clicks are ignored.
        // TODO consolidate to summoning method when it is create-----finished zechao

        // ==========================================
        // Story Card #18 Acceptance Test: Both players and their avatars have initial HP set to 20 [cite: 160]
        // ==========================================
        // TODO consolidate to my linked methods -- Scott---finished zechaowu
        setupAvatar(out,avatar1, player1);
        setupAvatar(out,avatar2, player2);

        // Initialise avatar stats --Zechao Wu


        // Send commands to the front-end to update the players' health display

        // Initialise player HUD health--Zechao Wu
        player1.setHealth(out, 20);
        player2.setHealth(out, 20);

        // Player starts their first turn with 2 mana
        player1.setMana(out, 2);

        // ==========================================
        // Story Card #18 Acceptance Test: Set initial avatar positions
        // ==========================================

        // TODO place this in a method   finished -zechao wu


        // Player 1 avatar starts at [2,3]
        Tile tileP1 = gameState.getBoard().getTile(1, 2); // It's 0-indexed
        placeAvatar(out, avatar1, tileP1);

        // Player 2 avatar starts at [8,3]
        Tile tileP2 = gameState.getBoard().getTile(7, 2);
        placeAvatar(out, avatar2, tileP2);


        // Each player starts with 3 cards drawn from the deck

        for (int i = 0; i < 3; i++) {
            player1.drawCardIntoHand();
            player2.drawCardIntoHand();
        }

        player1.drawHand(out);

        // Note: As per the template's instructions, comment out the demo execution when implementing your own solution
        // CommandDemo.executeDemo(out);
    }
    private void placeAvatar(ActorRef out, BetterUnit avatar, Tile tile) {
        tile.setUnit(avatar);
        avatar.setPositionByTile(tile);
        BasicCommands.drawUnit(out, avatar, tile);
        BasicCommands.setUnitHealth(out, avatar, avatar.getHealth());
        BasicCommands.setUnitAttack(out, avatar, avatar.getAttack());
    }
    private void setupAvatar(ActorRef out, BetterUnit avatar, Player owner) {
        avatar.setOwner(owner);
        avatar.setMaxHealth(20);
        avatar.setHealth(out, owner, 20);
        avatar.setAttack(2);
    }


}