import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.BasicCommands;
import commands.DummyTell;
import events.Initalize;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.unittypes.*;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import static org.junit.Assert.*;

/**
 * JUnit tests for Story Card - Deathwatch Abilities.
 * This class verifies that units with Deathwatch correctly trigger their
 * passive effects when any unit on the board dies.
 */
public class DeathwatchTest implements DummyTell {

    @Override
    public void tell(ObjectNode message) {
        // No-op: Discard front-end UI messages during backend testing
    }

    @Before
    public void setUp() {
        // Instruction to use the dummy interceptor to avoid NullPointerException
        BasicCommands.altTell = this;
    }

    // -------------------------------------------------------------------------
    // AC1 - Bad Omen increases attack when a unit dies
    // -------------------------------------------------------------------------

    /**
     * Story Card #22 - AC1:
     * When any unit on the board dies, a Bad Omen unit owned by a player
     * must have its attack value increased by 1.
     */
    @Test
    public void testBadOmenDeathwatch() {
        GameState gameState = new GameState();
        // Initialize the game board and avatars
        new Initalize().processEvent(null, gameState, Json.newObject());

        // Arrange: Set up a Bad Omen for Player 1 at (1,1)
        BadOmen badOmen = (BadOmen) BasicObjectBuilders.loadUnit("conf/gameconfs/units/bad_omen.json", 100, BadOmen.class);
        badOmen.setAttack(null, 2);
        badOmen.setMaxHealth(5); // Ensure maxHealth > 0 so setHealth works correctly
        badOmen.setHealth(null, 5);
        badOmen.setOwner(gameState.getPlayer1());

        Tile tile1 = gameState.getBoard().getTile(1, 1);
        tile1.setUnit(badOmen);
        badOmen.setPositionByTile(tile1);
        gameState.getPlayer1().getUnitList().put(badOmen.getId(), badOmen);

        int initialAttack = badOmen.getAttack();

        // Arrange: Place a sacrifice unit for Player 2 at (2,2)
        Unit sacrifice = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 101, Unit.class);
        sacrifice.setMaxHealth(1);
        sacrifice.setHealth(null, 1);
        sacrifice.setOwner(gameState.getPlayer2());

        Tile tile2 = gameState.getBoard().getTile(2, 2);
        tile2.setUnit(sacrifice);
        sacrifice.setPositionByTile(tile2);
        gameState.getPlayer2().getUnitList().put(sacrifice.getId(), sacrifice);

        // Act: Kill the sacrifice unit to trigger global Deathwatch logic
        sacrifice.takeDamage(null, gameState, 10);

        // Assert: Verify the sacrifice is dead and Bad Omen attack increased
        assertTrue("Sacrifice unit should be dead", sacrifice.isDead());
        assertEquals("Bad Omen attack should increase by 1 after unit death",
                initialAttack + 1,
                badOmen.getAttack());
    }

    // -------------------------------------------------------------------------
    // AC2 - Shadowdancer manipulates Avatar health on unit death
    // -------------------------------------------------------------------------

    /**
     * Story Card #25 - AC2:
     * When any unit on the board dies, a Shadowdancer must deal 1 damage
     * to the enemy avatar and restore 1 health to the friendly avatar.
     */
    @Test
    public void testShadowdancerDeathwatch() {
        GameState gameState = new GameState();
        // Initialize board and avatars
        new Initalize().processEvent(null, gameState, Json.newObject());

        BetterUnit p1Avatar = gameState.getPlayer1().getAvatar();
        BetterUnit p2Avatar = gameState.getPlayer2().getAvatar();

        // Prepare Avatar HP for testing
        p1Avatar.setMaxHealth(20);
        p1Avatar.setHealth(null, gameState.getPlayer1(), 15); // Start at 15 to test healing
        p2Avatar.setHealth(null, gameState.getPlayer2(), 20);

        // Arrange: Set up the Shadowdancer for Player 1 at (1,1)
        Shadowdancer dancer = (Shadowdancer) BasicObjectBuilders.loadUnit("conf/gameconfs/units/shadowdancer.json", 102, Shadowdancer.class);
        dancer.setMaxHealth(5);
        dancer.setHealth(null, 5);
        dancer.setOwner(gameState.getPlayer1());

        Tile tile1 = gameState.getBoard().getTile(1, 1);
        tile1.setUnit(dancer);
        dancer.setPositionByTile(tile1);
        gameState.getPlayer1().getUnitList().put(dancer.getId(), dancer);

        // Arrange: Create a sacrifice unit at (2,2)
        Unit sacrifice = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 103, Unit.class);
        sacrifice.setMaxHealth(1);
        sacrifice.setHealth(null, 1);
        sacrifice.setOwner(gameState.getPlayer2());

        Tile tile2 = gameState.getBoard().getTile(2, 2);
        tile2.setUnit(sacrifice);
        sacrifice.setPositionByTile(tile2);
        gameState.getPlayer2().getUnitList().put(sacrifice.getId(), sacrifice);

        // Act: Trigger the death of the unit
        sacrifice.die(null, gameState);

        // Assert: Verify Avatar health changes according to Shadowdancer's ability
        assertEquals("Friendly avatar should be healed by 1", 16, p1Avatar.getHealth());
        assertEquals("Enemy avatar should take 1 damage", 19, p2Avatar.getHealth());
    }
}