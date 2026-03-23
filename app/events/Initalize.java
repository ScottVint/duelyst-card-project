package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.unittypes.BetterUnit;
import structures.basic.players.Player;
import structures.basic.unittypes.Unit;
import structures.basic.Tile;
import structures.logic.AI;
import structures.logic.BoardLogic;

import utils.BasicObjectBuilders;

import java.util.concurrent.ThreadLocalRandom;

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
        BoardLogic.clearSelection(out, gameState.board);

        // Retrieve player objects
        Player player1 = gameState.getPlayer1();
        Player player2 = gameState.getPlayer2();

        // Player 1 starts first
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

        // Player 1 avatar starts at [1,2] (0-indexed)
        gameState.placeAvatar(out, humanAvatar, 1, 2);

        // Player 2 avatar starts at [7,2] (0-indexed)
        gameState.placeAvatar(out, aiAvatar, 7, 2);

        // Only the starting player's avatar may act on turn 1
        if (humanStarts) {
            humanAvatar.hasAttacked = false;
            humanAvatar.hasMoved = false;

            aiAvatar.hasAttacked = true;
            aiAvatar.hasMoved = true;
        } else {
            humanAvatar.hasAttacked = true;
            humanAvatar.hasMoved = true;

            aiAvatar.hasAttacked = false;
            aiAvatar.hasMoved = false;
        }

//        // Temporary enemy non-avatar unit for testing Dark Terminus
//        Unit testEnemy = BasicObjectBuilders.loadUnit(
//                "conf/gameconfs/units/gloom_chaser.json",
//                gameState.getNextUnitId(),
//                Unit.class
//        );
//        testEnemy.setOwner(player2);
//        testEnemy.setAttack(3);
//        testEnemy.setMaxHealth(1);
//        testEnemy.setHealth(out, 1);
//
//        Tile enemyTestTile = gameState.getBoard().getTile(7, 2);
//        enemyTestTile.setUnit(testEnemy);
//        testEnemy.setPositionByTile(enemyTestTile);
//
//        BasicCommands.drawUnit(out, testEnemy, enemyTestTile);
//        BasicCommands.setUnitAttack(out, testEnemy, testEnemy.getAttack());
//        BasicCommands.setUnitHealth(out, testEnemy, testEnemy.getHealth());

        // Each player starts with 3 cards drawn from the deck
        for (int i = 0; i < 3; i++) {
            player1.drawCardIntoHand();
            player2.drawCardIntoHand();
        }

        gameState.player1.drawHand(out);

        

        // Note: As per the template's instructions, comment out the demo execution when implementing your own solution
        // CommandDemo.executeDemo(out);
    }
}