package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.BetterUnit;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.players.Player;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.List;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to receive commands from the back-end.
 */
public class Initalize implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
        // Mark the game state as initialized
        gameState.gameInitalised = true;

        gameState.getBoard().clearSelection(out);

        Player player1 = gameState.getPlayer1();
        Player player2 = gameState.getPlayer2();

        BetterUnit avatar1 = player1.getAvatar();
        BetterUnit avatar2 = player2.getAvatar();

        avatar1.setOwner(player1);
        avatar2.setOwner(player2);

        // Display tiles on the board
        player1.setHealth(20);
        player2.setHealth(20);

        // Retrieve player objects (Assuming player1 and player2 are already instantiated within gameState)
        avatar1.setAttack(2);
        avatar1.setHealth(20);
        avatar1.setMaxHealth(20);

        // Retrieve the Avatar units for both players
        avatar2.setAttack(2);
        avatar2.setHealth(20);
        avatar2.setMaxHealth(20);

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
        BasicCommands.setPlayer1Health(out, player1);
        BasicCommands.setPlayer2Health(out, player2);

        player1.setMana(2);
        BasicCommands.setPlayer1Mana(out, player1);

        // Send commands to the front-end to update the players' health display
        Tile tileP1 = gameState.getBoard().getTile(2, 3);
        tileP1.setUnit(avatar1);
        avatar1.setPositionByTile(tileP1);
        BasicCommands.drawUnit(out, avatar1, tileP1);
        BasicCommands.setUnitAttack(out, avatar1, avatar1.getAttack());
        BasicCommands.setUnitHealth(out, avatar1, avatar1.getHealth());

        Tile tileP2 = gameState.getBoard().getTile(8, 3);
        tileP2.setUnit(avatar2);
        avatar2.setPositionByTile(tileP2);
        BasicCommands.drawUnit(out, avatar2, tileP2);
        BasicCommands.setUnitAttack(out, avatar2, avatar2.getAttack());
        BasicCommands.setUnitHealth(out, avatar2, avatar2.getHealth());

        // Temporary enemy non-avatar unit for testing Dark Terminus
        Unit testEnemy = BasicObjectBuilders.loadUnit(
                "conf/gameconfs/units/gloom_chaser.json",
                gameState.getNextUnitId(),
                Unit.class
        );
        testEnemy.setOwner(player2);
        testEnemy.setAttack(3);
        testEnemy.setMaxHealth(1);
        testEnemy.setHealth(1);

        Tile enemyTestTile = gameState.getBoard().getTile(7, 2);
        enemyTestTile.setUnit(testEnemy);
        testEnemy.setPositionByTile(enemyTestTile);

        BasicCommands.drawUnit(out, testEnemy, enemyTestTile);
        BasicCommands.setUnitAttack(out, testEnemy, testEnemy.getAttack());
        BasicCommands.setUnitHealth(out, testEnemy, testEnemy.getHealth());

        for (int i = 0; i < 3; i++) {
            player1.drawCard();
            player2.drawCard();
        }

        List<Card> p1Hand = player1.getHand();
        for (int i = 0; i < p1Hand.size() && i < 6; i++) {
            BasicCommands.drawCard(out, p1Hand.get(i), i + 1, 0);
        }
    }
}