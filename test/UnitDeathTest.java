import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.BasicCommands;
import commands.DummyTell;
import events.Initalize;
import play.libs.Json;
import structures.GameState;
import structures.basic.BetterUnit;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * JUnit tests for Story Card #35: Unit Death Logic.
 *
 * Acceptance criteria:
 * 1. System checks health whenever it is altered.
 * 2. Plays death animation and deletes the unit.
 * 3. Units with 0 health cannot counter-attack (isDead() flag).
 *
 * @author Minghao
 */
public class UnitDeathTest {

    // -----------------------------------------------------------------------
    // Recording DummyTell
    // -----------------------------------------------------------------------

    static class RecordingTell implements DummyTell {
        final List<ObjectNode> messages = new ArrayList<>();

        @Override
        public void tell(ObjectNode message) {
            messages.add(message.deepCopy());
        }

        boolean hasMessageType(String type) {
            for (ObjectNode m : messages) {
                if (type.equals(m.get("messagetype").asText())) return true;
            }
            return false;
        }

        /** Returns true if a playUnitAnimation with animation=death was sent for the given unit id. */
        boolean deathAnimationPlayedFor(int unitId) {
            for (ObjectNode m : messages) {
                if (!"playUnitAnimation".equals(m.get("messagetype").asText())) continue;
                if (m.get("unit").get("id").asInt() == unitId
                        && "death".equals(m.get("animation").asText())) return true;
            }
            return false;
        }

        /** Returns true if a deleteUnit message was sent for the given unit id. */
        boolean deleteUnitSentFor(int unitId) {
            for (ObjectNode m : messages) {
                if (!"deleteUnit".equals(m.get("messagetype").asText())) continue;
                if (m.get("unit").get("id").asInt() == unitId) return true;
            }
            return false;
        }
    }

    // -----------------------------------------------------------------------
    // Shared test state
    // -----------------------------------------------------------------------

    private RecordingTell recorder;
    private GameState gameState;

    @Before
    public void setUp() {
        recorder = new RecordingTell();
        BasicCommands.altTell = recorder;
        gameState = new GameState();
        new Initalize().processEvent(null, gameState, Json.newObject());
    }

    // -----------------------------------------------------------------------
    // AC1 — Health check triggers death at 0
    // -----------------------------------------------------------------------

    /**
     * SC#35 - AC1:
     * After taking lethal damage, isDead() must return true.
     */
    @Test
    public void isDead_returnsTrueWhenHealthReachesZero() {
        BetterUnit avatar1 = gameState.getPlayer1().getAvatar();
        avatar1.takeDamage(null, 20); // lethal
        assertTrue("isDead() must be true after lethal damage", avatar1.isDead());
    }

    /**
     * SC#35 - AC1 (alive):
     * A unit that has taken non-lethal damage must NOT be considered dead.
     */
    @Test
    public void isDead_returnsFalseWhenHealthAboveZero() {
        BetterUnit avatar2 = gameState.getPlayer2().getAvatar();
        avatar2.takeDamage(null, 10); // non-lethal
        assertFalse("isDead() must be false after non-lethal damage", avatar2.isDead());
    }

    // -----------------------------------------------------------------------
    // AC2 — Death animation and delete command sent
    // -----------------------------------------------------------------------

    /**
     * SC#35 - AC2 (animation):
     * When a unit's health reaches 0, the death animation must be played.
     */
    @Test
    public void deathAnimationPlayedOnFatalDamage() {
        BetterUnit avatar1 = gameState.getPlayer1().getAvatar();
        recorder.messages.clear();

        avatar1.takeDamage(null, 20); // lethal

        assertTrue("Death animation must be played when health reaches 0",
                recorder.deathAnimationPlayedFor(avatar1.getId()));
    }

    /**
     * SC#35 - AC2 (delete command):
     * When a unit's health reaches 0, a deleteUnit command must be sent.
     */
    @Test
    public void deleteCommandSentOnFatalDamage() {
        BetterUnit avatar1 = gameState.getPlayer1().getAvatar();
        recorder.messages.clear();

        avatar1.takeDamage(null, 20); // lethal

        assertTrue("deleteUnit command must be sent when health reaches 0",
                recorder.deleteUnitSentFor(avatar1.getId()));
    }

    /**
     * SC#35 - AC2 (tile cleared):
     * After fatal damage, the unit's tile must no longer reference the unit.
     */
    @Test
    public void unitRemovedFromTileOnDeath() {
        BetterUnit avatar1 = gameState.getPlayer1().getAvatar();
        Tile tile = gameState.getBoard().getTile(2, 3); // avatar1 starts here

        assertEquals("Tile [2,3] must hold avatar1 before death", avatar1, tile.getUnit());

        avatar1.takeDamage(null, 20); // lethal

        assertNull("Tile [2,3] must be empty after avatar1 dies", tile.getUnit());
    }

    /**
     * SC#35 - no death on non-lethal damage:
     * A unit that survives damage must NOT have its death animation played.
     */
    @Test
    public void noDeathAnimationOnNonLethalDamage() {
        BetterUnit avatar2 = gameState.getPlayer2().getAvatar();
        recorder.messages.clear();

        avatar2.takeDamage(null, 5); // survives

        assertFalse("Death animation must NOT be played on non-lethal damage",
                recorder.deathAnimationPlayedFor(avatar2.getId()));
        assertFalse("deleteUnit must NOT be sent on non-lethal damage",
                recorder.deleteUnitSentFor(avatar2.getId()));
    }

    // -----------------------------------------------------------------------
    // AC3 — isDead() flag can be used to block counter-attack
    // -----------------------------------------------------------------------

    /**
     * SC#35 - AC3:
     * A unit at 0 HP is considered dead; callers (e.g. counter-attack logic)
     * must check isDead() before retaliating.
     */
    @Test
    public void deadUnitIsMarkedAsUnableToAct() {
        // Use a properly loaded unit so animations are available (no NPE in playUnitAnimation)
        BetterUnit unit = (BetterUnit) BasicObjectBuilders.loadUnit(
                StaticConfFiles.humanAvatar, 99, BetterUnit.class);
        unit.setMaxHealth(5);
        unit.setHealth(5);
        assertFalse("Healthy unit must not be considered dead", unit.isDead());

        unit.takeDamage(null, 5);
        assertTrue("Unit at 0 HP must be considered dead (unable to counter-attack)",
                unit.isDead());
    }
}
