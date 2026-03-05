package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import utils.BasicObjectBuilders;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    protected List<Card> cards;

    public Hand() {
        cards = new ArrayList<>(6);
    }

    public List<Card> getCards() { return cards; }
    public void showHand(ActorRef out) {
        for(Card card : cards) {
            BasicCommands.drawCard(out, card, cards.indexOf(card), 0);
        }
    }

    public void addCard(Card card) {
        try {
            cards.add(card);
        } catch(NullPointerException e) {
            // TODO replace with the end game method when it's made
            System.err.println("Deck empty!");
        }
    }


}
