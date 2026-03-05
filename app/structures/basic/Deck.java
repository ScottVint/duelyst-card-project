package structures.basic;

import structures.basic.players.*;
import utils.OrderedCardLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;

    /// Draws all cards into deck, then shuffles.
    public Deck(Class<? extends Player> playerClass) {
        if(playerClass == HumanPlayer.class) {
            cards = OrderedCardLoader.getPlayer1Cards(20);
        } else if(playerClass == AIPlayer.class) {
            cards = OrderedCardLoader.getPlayer2Cards(20);
        }
        shuffle();
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
