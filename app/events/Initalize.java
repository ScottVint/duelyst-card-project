package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.players.Player;
import structures.basic.unittypes.BetterUnit;
import structures.logic.BoardLogic;

/**
 * Indicates that both the core game loop in the browser is starting.
 */
public class Initalize implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        // Mark the game state as initialized
        gameState.gameInitalised = true;


        BoardLogic.clearSelection(out, gameState.board);

        // Retrieve players
        Player player1 = gameState.getPlayer1();
        Player player2 = gameState.getPlayer2();

        // Fixed starting player for stable game setup/tests
        boolean humanStarts = true;
        gameState.player1Turn = true;

        System.out.println("Players: " + player1.getClass() + "," + player2.getClass());

        // Create avatars
        player1.setAvatar(out, gameState);
        player2.setAvatar(out, gameState);

        BetterUnit humanAvatar = player1.getAvatar();
        BetterUnit aiAvatar = player2.getAvatar();

        // Starting player begins with 2 mana, the other with 0
        if (humanStarts) {
            player1.setMana(out, 2);
            player2.setMana(out, 0);
            BasicCommands.addPlayer1Notification(out, "You go first!", 2);
        } else {
            player1.setMana(out, 0);
            player2.setMana(out, 2);
            BasicCommands.addPlayer1Notification(out, "AI goes first!", 2);
        }

        // Place avatars
        gameState.placeAvatar(out, humanAvatar, 1, 2);
        gameState.placeAvatar(out, aiAvatar, 7, 2);

        // Reset action flags
        humanAvatar.hasAttacked = false;
        humanAvatar.hasMoved = false;
        aiAvatar.hasAttacked = false;
        aiAvatar.hasMoved = false;

        // Draw starting cards
        for (int i = 0; i < 2; i++) {
            player1.drawCardIntoHand();
            player2.drawCardIntoHand();
        }

        gameState.player1.drawHand(out);
    }
}