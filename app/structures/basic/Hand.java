package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import utils.BasicObjectBuilders;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    List<Card> cards;

    public Hand() {
        List<Card> cards = new ArrayList<Card>();
    }

    public void showHand(ActorRef out) {
        for(Card card : cards) {
            BasicCommands.drawCard(out, card, cards.indexOf(card), 0);
        }
    }
}
