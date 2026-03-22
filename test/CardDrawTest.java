import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import commands.BasicCommands;
import commands.DummyTell;
import events.EndTurnClicked;
import events.Initalize;
import play.libs.Json;
import structures.GameState;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit tests for Story Card #21: Card Draw & Hand Limit.
 * <p>
 * Player 1 draws one card at the start of their turn (when Player 2 ends their
 * turn). A full round requires two processEvent calls: P1 ends their turn,
 * then P2 ends their turn (at which point P1 draws one card).
 * <p>
 * The hand is capped at 6 cards; excess draws are silently discarded.
 *
 * @author Minghao
 */
public class CardDrawTest {

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
    }

    private RecordingTell recorder;
    private GameState gameState;
    private EndTurnClicked processor;

    @Before
    public void setUp() {
        recorder = new RecordingTell();
        BasicCommands.altTell = recorder;
        gameState = new GameState();
        new Initalize().processEvent(null, gameState, Json.newObject());
        processor = new EndTurnClicked();
    }

    /** Simulates a full round: P1 ends their turn, then P2 ends their turn. */
    private void doFullRound() {
        processor.processEvent(null, gameState, Json.newObject()); // P1 ends
        processor.processEvent(null, gameState, Json.newObject()); // P2 ends → P1 draws
    }

    // -----------------------------------------------------------------------
    // AC1 — Player 1 draws a card when Player 2 ends their turn
    // -----------------------------------------------------------------------

    @Test
    public void player1DrawsCardWhenPlayer2EndsTurn() {
        int handSizeBefore = gameState.getPlayer1().getHand().size(); // 3 cards from init

        doFullRound();

        int handSizeAfter = gameState.getPlayer1().getHand().size();
        assertEquals("Player 1 must draw exactly 1 card when P2 ends their turn",
                handSizeBefore + 1, handSizeAfter);
    }

    // -----------------------------------------------------------------------
    // AC2 — Player 1 does NOT draw when Player 1 ends their own turn
    // -----------------------------------------------------------------------

    @Test
    public void player1DoesNotDrawWhenPlayer1EndsTurn() {
        int handSizeBefore = gameState.getPlayer1().getHand().size();

        // Only P1 ends; P2 does NOT end, so P1 should not draw
        processor.processEvent(null, gameState, Json.newObject()); // P1 ends only

        int handSizeAfter = gameState.getPlayer1().getHand().size();
        assertEquals("Player 1 must not draw when ending their own turn",
                handSizeBefore, handSizeAfter);
    }

    // -----------------------------------------------------------------------
    // AC3 — Hand is capped at 6 cards
    // -----------------------------------------------------------------------

    @Test
    public void handIsNeverMoreThanSix() {
        while (gameState.getPlayer1().getHand().size() < 6) {
            gameState.getPlayer1().drawCardIntoHand();
        }
        assertEquals("Pre-condition: hand should be full at 6", 6,
                gameState.getPlayer1().getHand().size());

        doFullRound();

        assertTrue("Hand must never exceed 6 cards",
                gameState.getPlayer1().getHand().size() <= 6);
    }

    // -----------------------------------------------------------------------
    // AC4 — Card drawn from deck (deck shrinks by 1)
    // -----------------------------------------------------------------------

    @Test
    public void deckShrinksWhenPlayer1DrawsCard() {
        int deckSizeBefore = gameState.getPlayer1().getDeck().size();

        doFullRound();

        int deckSizeAfter = gameState.getPlayer1().getDeck().size();
        assertEquals("P1 deck must shrink by 1 per round draw",
                deckSizeBefore - 1, deckSizeAfter);
    }

    // -----------------------------------------------------------------------
    // AC5 — Card still removed from deck even when hand is full
    // -----------------------------------------------------------------------

    @Test
    public void deckShrinksEvenWhenHandIsFull() {
        while (gameState.getPlayer1().getHand().size() < 6) {
            gameState.getPlayer1().drawCardIntoHand();
        }
        int deckSizeBefore = gameState.getPlayer1().getDeck().size();

        doFullRound();

        int deckSizeAfter = gameState.getPlayer1().getDeck().size();
        assertEquals("Card must still be removed from deck even if hand is full",
                deckSizeBefore - 1, deckSizeAfter);
    }
}
