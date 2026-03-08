package structures.basic;

import structures.basic.players.*;
import utils.OrderedCardLoader;

import java.util.Collections;
import java.util.List;

public class Deck {
    public List<Card> cards; // Making this public, because I don't think any of this
                             // is accessible outside of the player's deck attribute?

    public Deck(Player player) {
        try {
            if (player instanceof HumanPlayer) {
                cards = OrderedCardLoader.getPlayer1Cards(2);
            } else if (player instanceof AIPlayer) {
                cards = OrderedCardLoader.getPlayer2Cards(2);
            } else {
                throw new ClassNotFoundException("Invalid player type");
            }
            Collections.shuffle(cards);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}
