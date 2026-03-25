import org.junit.Before;
import org.junit.Test;
import structures.GameState;

import static org.junit.Assert.*;

/**
 * Tests for SC#34 — Optional Turn Time Limit.
 * Covers the GameState timer state machine: start, stop, expiry detection,
 * and the guard flag that prevents the Heartbeat from firing end-turn twice.
 */
public class TurnTimerTest {

    GameState gs;

    @Before
    public void setUp() {
        gs = new GameState();
        gs.turnTimerEnabled = true;
        gs.turnTimeLimitSeconds = 60;
    }

    // ── startTurnTimer ────────────────────────────────────────────────────────

    @Test
    public void startTurnTimerSetsDeadlineInFuture() {
        long before = System.currentTimeMillis();
        gs.startTurnTimer();

        assertTrue("Deadline should be in the future after startTurnTimer.",
                gs.currentTurnDeadlineMillis > before);
    }

    @Test
    public void startTurnTimerSetsDeadlineByConfiguredLimit() {
        gs.turnTimeLimitSeconds = 30;
        long before = System.currentTimeMillis();
        gs.startTurnTimer();

        long expectedDeadline = before + 30_000L;
        // Allow 100ms tolerance for test execution time
        assertTrue("Deadline should be ~30 s from now.",
                gs.currentTurnDeadlineMillis >= expectedDeadline - 100
                        && gs.currentTurnDeadlineMillis <= expectedDeadline + 100);
    }

    @Test
    public void startTurnTimerResetsExpiredFlag() {
        gs.timerExpiredThisTurn = true;
        gs.startTurnTimer();

        assertFalse("startTurnTimer should reset the timerExpiredThisTurn flag.",
                gs.timerExpiredThisTurn);
    }

    @Test
    public void startTurnTimerDoesNothingWhenDisabled() {
        gs.turnTimerEnabled = false;
        gs.startTurnTimer();

        assertEquals("Deadline should stay -1 when timer is disabled.",
                -1L, gs.currentTurnDeadlineMillis);
        assertFalse("timerExpiredThisTurn should stay false when disabled.",
                gs.timerExpiredThisTurn);
    }

    // ── stopTurnTimer ─────────────────────────────────────────────────────────

    @Test
    public void stopTurnTimerClearsDeadline() {
        gs.startTurnTimer();
        gs.stopTurnTimer();

        assertEquals("stopTurnTimer should clear deadline to -1.",
                -1L, gs.currentTurnDeadlineMillis);
    }

    @Test
    public void stopTurnTimerResetsExpiredFlag() {
        gs.startTurnTimer();
        gs.timerExpiredThisTurn = true;
        gs.stopTurnTimer();

        assertFalse("stopTurnTimer should reset the timerExpiredThisTurn flag.",
                gs.timerExpiredThisTurn);
    }

    // ── isTurnTimerRunning ────────────────────────────────────────────────────

    @Test
    public void isTurnTimerRunningReturnsTrueAfterStart() {
        gs.startTurnTimer();

        assertTrue("Timer should be running after startTurnTimer.", gs.isTurnTimerRunning());
    }

    @Test
    public void isTurnTimerRunningReturnsFalseAfterStop() {
        gs.startTurnTimer();
        gs.stopTurnTimer();

        assertFalse("Timer should not be running after stopTurnTimer.", gs.isTurnTimerRunning());
    }

    @Test
    public void isTurnTimerRunningReturnsFalseWhenDisabled() {
        gs.turnTimerEnabled = false;
        gs.currentTurnDeadlineMillis = System.currentTimeMillis() + 60_000L; // manually set
        assertFalse("Timer should not be running when turnTimerEnabled is false.",
                gs.isTurnTimerRunning());
    }

    // ── hasTurnTimerExpired ───────────────────────────────────────────────────

    @Test
    public void hasTurnTimerExpiredReturnsFalseBeforeDeadline() {
        gs.startTurnTimer(); // deadline is 60 s away

        assertFalse("Timer should not have expired immediately after start.",
                gs.hasTurnTimerExpired());
    }

    @Test
    public void hasTurnTimerExpiredReturnsTrueWhenDeadlinePassed() {
        // Manually set deadline 1 second in the past
        gs.currentTurnDeadlineMillis = System.currentTimeMillis() - 1_000L;

        assertTrue("Timer should be expired when deadline is in the past.",
                gs.hasTurnTimerExpired());
    }

    @Test
    public void hasTurnTimerExpiredReturnsFalseWhenDisabled() {
        gs.turnTimerEnabled = false;
        gs.currentTurnDeadlineMillis = System.currentTimeMillis() - 1_000L;

        assertFalse("Timer should never expire when turnTimerEnabled is false.",
                gs.hasTurnTimerExpired());
    }

    @Test
    public void hasTurnTimerExpiredReturnsFalseWhenNotStarted() {
        // deadline = -1 by default
        assertFalse("Timer should not be expired if it was never started.",
                gs.hasTurnTimerExpired());
    }

    // ── timerExpiredThisTurn guard ────────────────────────────────────────────

    @Test
    public void timerExpiredFlagPreventsDoubleExpiry() {
        // Simulate: timer expired once, Heartbeat set the flag
        gs.currentTurnDeadlineMillis = System.currentTimeMillis() - 1_000L;
        gs.timerExpiredThisTurn = true;

        // Heartbeat checks: hasTurnTimerExpired() && !timerExpiredThisTurn
        boolean wouldTriggerAgain = gs.hasTurnTimerExpired() && !gs.timerExpiredThisTurn;

        assertFalse("Second Heartbeat tick should not trigger end-turn again.", wouldTriggerAgain);
    }
}
