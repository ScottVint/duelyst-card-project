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

/**
 * JUnit tests for Story Card #2: HP Display.
 *
 * Acceptance criteria:
 * 1. When a unit spawns/is summoned, the UI displays the Max HP value.
 * 2. When a unit is attacked and the animation resolves, the UI updates to the new HP.
 *
 * @author Minghao
 */
public class HPDisplayTest {

    // -----------------------------------------------------------------------
    // Recording DummyTell — captures setUnitHealth messages
    // -----------------------------------------------------------------------

    static class RecordingTell implements DummyTell {
        final List<ObjectNode> messages = new ArrayList<>();

        @Override
        public void tell(ObjectNode message) {
            messages.add(message.deepCopy());
        }

        /** Returns true if setUnitHealth was sent for the given unit id and health value. */
        boolean wasHealthDisplayed(int unitId, int health) {
            for (ObjectNode m : messages) {
                if (!"setUnitHealth".equals(m.get("messagetype").asText())) continue;
                if (m.get("unit").get("id").asInt() == unitId && m.get("health").asInt() == health) return true;
            }
            return false;
        }

        /** Returns the last health value displayed for the given unit id, or -1 if never set. */
        int lastHealthDisplayed(int unitId) {
            int last = -1;
            for (ObjectNode m : messages) {
                if (!"setUnitHealth".equals(m.get("messagetype").asText())) continue;
                if (m.get("unit").get("id").asInt() == unitId) {
                    last = m.get("health").asInt();
                }
            }
            return last;
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
    // AC1 — Max HP shown when unit spawns
    // -----------------------------------------------------------------------

    /**
     * SC#2 - AC1:
     * After game initialisation, both avatars must have their max HP (20)
     * shown in the UI via a setUnitHealth message.
     */
    @Test
    public void avatarHealthDisplayedAtMaxOnSpawn() {
        BetterUnit avatar1 = gameState.getPlayer1().getAvatar();
        BetterUnit avatar2 = gameState.getPlayer2().getAvatar();

        assertTrue("Player 1 avatar must display HP=20 on spawn",
                recorder.wasHealthDisplayed(avatar1.getId(), 20));
        assertTrue("Player 2 avatar must display HP=20 on spawn",
                recorder.wasHealthDisplayed(avatar2.getId(), 20));
    }

    // -----------------------------------------------------------------------
    // AC2 — HP display updates after taking damage
    // -----------------------------------------------------------------------

    /**
     * SC#2 - AC2 (partial damage):
     * After a unit takes damage, the UI must reflect the new reduced HP.
     */
    @Test
    public void healthDisplayUpdatesAfterDamage() {
        BetterUnit avatar1 = gameState.getPlayer1().getAvatar();
        recorder.messages.clear();

        avatar1.takeDamage(null, 5); // 20 → 15

        assertEquals("Unit HP display must update to 15 after taking 5 damage",
                15, recorder.lastHealthDisplayed(avatar1.getId()));
    }

    /**
     * SC#2 - AC2 (overkill):
     * HP display must bottom out at 0 even when damage exceeds remaining health.
     */
    @Test
    public void healthDisplayDoesNotGoBelowZero() {
        BetterUnit avatar2 = gameState.getPlayer2().getAvatar();
        recorder.messages.clear();

        avatar2.takeDamage(null, 999); // overkill

        assertEquals("Unit HP display must bottom out at 0, not go negative",
                0, recorder.lastHealthDisplayed(avatar2.getId()));
    }

    /**
     * SC#2 - AC2 (sequential damage):
     * Multiple hits must each update the display to the latest HP.
     */
    @Test
    public void healthDisplayUpdatesOnEachHit() {
        BetterUnit avatar1 = gameState.getPlayer1().getAvatar();
        recorder.messages.clear();

        avatar1.takeDamage(null, 3); // 20 → 17
        assertEquals(17, recorder.lastHealthDisplayed(avatar1.getId()));

        avatar1.takeDamage(null, 7); // 17 → 10
        assertEquals(10, recorder.lastHealthDisplayed(avatar1.getId()));
    }
}
