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
 * JUnit tests for all units with Deathwatch abilities.
 * Verifies that Bad Omen, Shadowdancer, Bloodmoon Priestess, and Shadow Watcher
 * correctly trigger their effects when any unit on the board dies.
 */
public class DeathwatchTest implements DummyTell {

    @Override
    public void tell(ObjectNode message) {
        // No-op: Discard front-end UI commands during backend testing
    }

    @Before
    public void setUp() {
        // Use dummy interceptor to avoid NullPointerException from BasicCommands
        BasicCommands.altTell = this;
    }

    // -------------------------------------------------------------------------
    // AC1 - Bad Omen: Increases attack by 1 on death
    // -------------------------------------------------------------------------
    @Test
    public void testBadOmenDeathwatch() {
        GameState gameState = new GameState();
        new Initalize().processEvent(null, gameState, Json.newObject());

        BadOmen badOmen = (BadOmen) BasicObjectBuilders.loadUnit("conf/gameconfs/units/bad_omen.json", 100, BadOmen.class);
        badOmen.setAttack(null, 2);
        badOmen.setMaxHealth(5);
        badOmen.setHealth(null, 5);
        badOmen.setOwner(gameState.getPlayer1());

        Tile tile1 = gameState.getBoard().getTile(1, 1);
        tile1.setUnit(badOmen);
        badOmen.setPositionByTile(tile1);
        gameState.getPlayer1().getUnitList().put(badOmen.getId(), badOmen);

        int initialAttack = badOmen.getAttack();

        Unit sacrifice = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 101, Unit.class);
        sacrifice.setMaxHealth(1);
        sacrifice.setHealth(null, 1);
        sacrifice.setOwner(gameState.getPlayer2());
        Tile tile2 = gameState.getBoard().getTile(2, 2);
        tile2.setUnit(sacrifice);
        sacrifice.setPositionByTile(tile2);
        gameState.getPlayer2().getUnitList().put(sacrifice.getId(), sacrifice);

        // Trigger combat kill
        sacrifice.takeDamage(null, gameState, 10);

        assertEquals("Bad Omen attack should increase by 1", initialAttack + 1, badOmen.getAttack());
    }

    // -------------------------------------------------------------------------
    // AC2 - Shadowdancer: Deals 1 damage to enemy avatar, heals friendly by 1
    // -------------------------------------------------------------------------
    @Test
    public void testShadowdancerDeathwatch() {
        GameState gameState = new GameState();
        new Initalize().processEvent(null, gameState, Json.newObject());

        BetterUnit p1Avatar = gameState.getPlayer1().getAvatar();
        BetterUnit p2Avatar = gameState.getPlayer2().getAvatar();
        p1Avatar.setHealth(null, gameState.getPlayer1(), 15);
        p2Avatar.setHealth(null, gameState.getPlayer2(), 20);

        Shadowdancer dancer = (Shadowdancer) BasicObjectBuilders.loadUnit("conf/gameconfs/units/shadowdancer.json", 102, Shadowdancer.class);
        dancer.setMaxHealth(5);
        dancer.setHealth(null, 5);
        dancer.setOwner(gameState.getPlayer1());

        Tile tile1 = gameState.getBoard().getTile(1, 1);
        tile1.setUnit(dancer);
        dancer.setPositionByTile(tile1);
        gameState.getPlayer1().getUnitList().put(dancer.getId(), dancer);

        Unit sacrifice = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 103, Unit.class);
        sacrifice.setMaxHealth(1);
        sacrifice.setHealth(null, 1);
        sacrifice.setOwner(gameState.getPlayer2());
        Tile tile2 = gameState.getBoard().getTile(2, 2);
        tile2.setUnit(sacrifice);
        sacrifice.setPositionByTile(tile2);
        gameState.getPlayer2().getUnitList().put(sacrifice.getId(), sacrifice);

        // Trigger arbitrary death
        sacrifice.die(null, gameState);

        assertEquals("Friendly avatar should be healed by 1", 16, p1Avatar.getHealth());
        assertEquals("Enemy avatar should take 1 damage", 19, p2Avatar.getHealth());
    }

    // -------------------------------------------------------------------------
    // AC3 - Bloodmoon Priestess: Summons a Wraithling on adjacent tile
    // -------------------------------------------------------------------------
    @Test
    public void testBloodmoonPriestessDeathwatch() {
        GameState gameState = new GameState();
        new Initalize().processEvent(null, gameState, Json.newObject());

        BloodmoonPriestess priestess = (BloodmoonPriestess) BasicObjectBuilders.loadUnit("conf/gameconfs/units/bloodmoon_priestess.json", 104, BloodmoonPriestess.class);
        priestess.setMaxHealth(5);
        priestess.setHealth(null, 5);
        priestess.setOwner(gameState.getPlayer1());

        Tile priestessTile = gameState.getBoard().getTile(4, 2); // Center of board
        priestessTile.setUnit(priestess);
        priestess.setPositionByTile(priestessTile);
        gameState.getPlayer1().getUnitList().put(priestess.getId(), priestess);

        int unitsBeforeDeath = gameState.getPlayer1().getUnitList().size();

        Unit sacrifice = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 105, Unit.class);
        sacrifice.setMaxHealth(1);
        sacrifice.setHealth(null, 1);
        sacrifice.setOwner(gameState.getPlayer2());
        gameState.getBoard().getTile(0, 0).setUnit(sacrifice);
        gameState.getPlayer2().getUnitList().put(sacrifice.getId(), sacrifice);

        sacrifice.die(null, gameState);

        int unitsAfterDeath = gameState.getPlayer1().getUnitList().size();
        assertEquals("Player 1 should have one more unit (a summoned Wraithling)", unitsBeforeDeath + 1, unitsAfterDeath);
    }

    // -------------------------------------------------------------------------
    // AC4 - Shadow Watcher: Gains +1 attack and +1 health/max health
    // -------------------------------------------------------------------------
    @Test
    public void testShadowWatcherDeathwatch() {
        GameState gameState = new GameState();
        new Initalize().processEvent(null, gameState, Json.newObject());

        ShadowWatcher watcher = (ShadowWatcher) BasicObjectBuilders.loadUnit("conf/gameconfs/units/shadow_watcher.json", 106, ShadowWatcher.class);
        watcher.setAttack(null, 2);
        watcher.setMaxHealth(2);
        watcher.setHealth(null, 2);
        watcher.setOwner(gameState.getPlayer1());

        Tile tile1 = gameState.getBoard().getTile(1, 1);
        tile1.setUnit(watcher);
        watcher.setPositionByTile(tile1);
        gameState.getPlayer1().getUnitList().put(watcher.getId(), watcher);

        Unit sacrifice = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, 107, Unit.class);
        sacrifice.setMaxHealth(1);
        sacrifice.setHealth(null, 1);
        sacrifice.setOwner(gameState.getPlayer2());
        gameState.getBoard().getTile(2, 2).setUnit(sacrifice);
        gameState.getPlayer2().getUnitList().put(sacrifice.getId(), sacrifice);

        sacrifice.die(null, gameState);

        assertEquals("Shadow Watcher attack should increase to 3", 3, watcher.getAttack());
        assertEquals("Shadow Watcher max health should increase to 3", 3, watcher.getMaxHealth());
        assertEquals("Shadow Watcher health should increase to 3", 3, watcher.getHealth());
    }
}