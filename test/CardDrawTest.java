import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.BasicCommands;
import commands.DummyTell;
import events.EndTurnClicked;
import events.Initalize;
import play.libs.Json;
import structures.GameState;
import structures.basic.Card;
import structures.basic.players.Player;

/**
 * JUnit tests for Story Card #21: Card Draw & Hand Limit.
 *
 * Acceptance criteria:
 * 1. Hand limit is capped at 6 cards.
 * 2. If the hand is full and a card is drawn, the top card from the deck is
 *    deleted/destroyed (removed from deck, not added to hand).
 * 3. One extra card is drawn from the deck at the start of each Player 1 turn.
 *
 * @author Minghao
 */
public class CardDrawTest {

    static class RecordingTell implements DummyTell {
        final List<ObjectNode> messages = new ArrayList<>();
        @Override
        public void tell(ObjectNode message) { messages.add(message.deepCopy()); }

        long countMessageType(String type) {
            return messages.stream()
                    .filter(m -> type.equals(m.get("messagetype").asText()))
                    .count();
        }
    }

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
    // AC1 — Hand limit capped at 6
    // -----------------------------------------------------------------------

    /**
     * SC#21 - AC1:
     * drawCard() must not add a card when the hand already has 6 cards.
     */
    @Test
    public void handSizeDoesNotExceedSixAfterDraw() {
        Player p1 = gameState.getPlayer1();
        // Fill the hand to exactly 6 (it already has 3 from Initalize; draw 3 more)
        p1.drawCard();
        p1.drawCard();
        p1.drawCard();
        assertEquals("Hand should be exactly 6 after filling", 6, p1.getHand().size());

        // Drawing again must not exceed 6
        p1.drawCard();
        assertEquals("Hand must not exceed 6 cards", 6, p1.getHand().size());
    }

    // -----------------------------------------------------------------------
    // AC2 — Full hand: card removed from deck but not added to hand
    // -----------------------------------------------------------------------

    /**
     * SC#21 - AC2 (deck shrinks):
     * When the hand is full, drawing still removes the top card from the deck.
     */
    @Test
    public void deckShrinksEvenWhenHandFull() {
        Player p1 = gameState.getPlayer1();
        // Fill hand to 6
        p1.drawCard();
        p1.drawCard();
        p1.drawCard();

        int deckSizeBefore = p1.getDeck().size();
        p1.drawCard(); // hand full → card discarded
        assertEquals("Deck must shrink by 1 even when hand is full",
                deckSizeBefore - 1, p1.getDeck().size());
    }

    /**
     * SC#21 - AC2 (hand unchanged):
     * When the hand is full, the hand size stays at 6 after a draw attempt.
     */
    @Test
    public void handRemainsFullAfterDiscardDraw() {
        Player p1 = gameState.getPlayer1();
        p1.drawCard(); p1.drawCard(); p1.drawCard(); // fill to 6
        p1.drawCard(); // discard
        assertEquals("Hand must stay at 6 when card is discarded", 6, p1.getHand().size());
    }

    // -----------------------------------------------------------------------
    // AC2 edge case — Empty deck
    // -----------------------------------------------------------------------

    /**
     * SC#21 - AC2 edge case:
     * drawCard() with an empty deck must not throw and must leave hand unchanged.
     */
    @Test
    public void drawFromEmptyDeckDoesNothing() {
        Player p1 = gameState.getPlayer1();
        p1.getDeck().clear();
        int handSizeBefore = p1.getHand().size();
        p1.drawCard(); // no-op
        assertEquals("Hand must be unchanged when deck is empty",
                handSizeBefore, p1.getHand().size());
    }

    // -----------------------------------------------------------------------
    // AC3 — One card drawn at the start of Player 1's turn
    // -----------------------------------------------------------------------

    /**
     * SC#21 - AC3:
     * Ending Player 2's turn must cause Player 1 to draw exactly one card.
     */
    @Test
    public void endTurnDrawsOneCardForPlayer1() {
        Player p1 = gameState.getPlayer1();
        // Simulate P1 ends turn → P2's turn starts
        new EndTurnClicked().processEvent(null, gameState, Json.newObject());
        int handSizeAfterP1Turn = p1.getHand().size();

        // Simulate P2 ends turn → P1's turn starts → card should be drawn
        int deckSizeBefore = p1.getDeck().size();
        new EndTurnClicked().processEvent(null, gameState, Json.newObject());

        assertEquals("Player 1 should draw exactly one card at turn start",
                handSizeAfterP1Turn + 1, p1.getHand().size());
        assertEquals("Deck should shrink by one",
                deckSizeBefore - 1, p1.getDeck().size());
    }

    /**
     * SC#21 - AC3 (full hand at turn start):
     * If Player 1's hand is full at turn start, the drawn card is discarded.
     */
    @Test
    public void endTurnDiscardsCardWhenHandFull() {
        Player p1 = gameState.getPlayer1();
        // Fill hand to 6 before the turn ends
        p1.drawCard(); p1.drawCard(); p1.drawCard(); // now 6

        // P1 ends turn
        new EndTurnClicked().processEvent(null, gameState, Json.newObject());
        int deckSizeBefore = p1.getDeck().size();

        // P2 ends turn → P1 turn starts, hand is full → card discarded
        new EndTurnClicked().processEvent(null, gameState, Json.newObject());

        assertEquals("Hand must stay at 6 when card discarded at turn start",
                6, p1.getHand().size());
        assertEquals("Deck must still shrink by 1",
                deckSizeBefore - 1, p1.getDeck().size());
    }
}
