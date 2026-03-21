import org.junit.Test;
import structures.basic.BetterUnit;
import structures.basic.players.AIPlayer;
import structures.basic.players.HumanPlayer;

import static org.junit.Assert.*;

public class PlayerTest {

    @Test
    public void testDeckSize() {
        HumanPlayer player1 = new HumanPlayer();
        AIPlayer player2 = new AIPlayer();
        assert player1.getDeck().size() == 20;
        assert player2.getDeck().size() == 20;
    }

    @Test
    public void testAvatars() {
        HumanPlayer player1 = new HumanPlayer();
        AIPlayer player2 = new AIPlayer();

        // If someone knows how to check for
        // specific images loaded, that'd be great
        assert player1.getAvatar().getClass() == BetterUnit.class;
        assert player2.getAvatar().getClass() == BetterUnit.class;
    }

    @Test
    public void testHand() {
        HumanPlayer player1 = new HumanPlayer();
        AIPlayer player2 = new AIPlayer();

        for (int i = 0; i < 10; i++) { player2.drawCardIntoHand(); }

        assert player1.getHand().isEmpty();
        assert player2.getHand().size() == 6;
    }

    @Test
    public void testUnitID() {
        HumanPlayer player1 = new HumanPlayer();
        assert player1.getAvatar().getId() == 0;
    }
}
