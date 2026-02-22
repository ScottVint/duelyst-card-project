import org.junit.Test;

import structures.basic.BetterUnit;
import structures.basic.players.*;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import static org.junit.Assert.*;

public class BetterUnitTest {
    @Test
    public void testPlayerHPLink() {

        HumanPlayer player1 = new HumanPlayer();
        AIPlayer player2 = new AIPlayer();

        BetterUnit playerAvatar = (BetterUnit) BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, BetterUnit.class);
        BetterUnit aiAvatar = (BetterUnit) BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 0, BetterUnit.class);

        // Test setting health
        playerAvatar.setHealth(player1, 20);
        aiAvatar.setHealth(player2, 20);

        assertEquals(playerAvatar.getHealth(), player1.getHealth());
        assertEquals(aiAvatar.getHealth(), player2.getHealth());

        // Test taking damage
        playerAvatar.takeDamage(10);
        aiAvatar.takeDamage(15);

        assertEquals(player1.getHealth(), playerAvatar.getHealth());
        assertEquals(player2.getHealth(), aiAvatar.getHealth());

        // Test healing
        playerAvatar.takeDamage(-10);
        aiAvatar.takeDamage(-5);

        assertEquals(player1.getHealth(), playerAvatar.getHealth());
        assertEquals(player2.getHealth(), aiAvatar.getHealth());
    }

    @Test
    public void testBottomOut() {
        HumanPlayer player1 = new HumanPlayer();
        AIPlayer player2 = new AIPlayer();
        BetterUnit playerAvatar = (BetterUnit) BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, BetterUnit.class);
        BetterUnit aiAvatar = (BetterUnit) BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 0, BetterUnit.class);

        playerAvatar.setHealth(player1, 20);
        aiAvatar.setHealth(player2, 20);

        playerAvatar.takeDamage(50);
        aiAvatar.takeDamage(5);
        aiAvatar.takeDamage(16);

        assertEquals( 0, playerAvatar.getHealth());
        assertEquals(0, aiAvatar.getHealth());

    }
}
