package structures.basic;

import play.utils.Reflect;
import structures.basic.players.*;
import utils.OrderedCardLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;

    /// Draws all cards into deck, then shuffles.
    public Deck(Player player) {
        if(player instanceof HumanPlayer) {
            cards = OrderedCardLoader.getPlayer1Cards(2);
        } else if(player instanceof AIPlayer) {
            cards = OrderedCardLoader.getPlayer2Cards(2);
        } else {
            System.err.println("Invalid player type");
        }
        shuffle();
        System.out.println(cards.size());
    }

    private void shuffle() {
        Collections.shuffle(cards);
    }

    /// Returns a card from the top of the deck,
    /// then removes that card from the deck.
    public Card drawFromDeck() {
        Card card = cards.get(0);
        cards.remove(0);
        return card;
    }
}
