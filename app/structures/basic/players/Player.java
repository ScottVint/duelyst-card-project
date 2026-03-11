package structures.basic.players;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;

import structures.basic.BetterUnit;
import structures.basic.Card;
import structures.basic.Unit;

/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * <p>
 * Extended to include the player's avatar unit, deck, hand, and card-draw logic.
 *
 * @author Dr. Richard McCreadie
 * @author Minghao
 *
 */
public class Player {

	// For use in subclasses, these are now protected instead of private
	protected int health;
	protected int mana;
	/** The avatar unit representing this player on the board. @author Minghao */
	BetterUnit avatar;
	// TODO: Replace List<Card> deck/hand with a Deck class that handles loading and shuffling
	/** The remaining cards in this player's draw pile. @author Minghao */
	List<Card> deck;
	/** The cards currently held in this player's hand. @author Minghao */
	List<Card> hand;

	public Player() {
		super();
		this.health = 20;
		this.mana = 0;
		this.deck = new ArrayList<>();
		this.hand = new ArrayList<>();
	}
	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
		this.deck = new ArrayList<>();
		this.hand = new ArrayList<>();
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public void setHealth(ActorRef out, int health) {
		throw new Error("Unknown Player subclass");
	}
	public int getMana() {
		return mana;
	}
	public void setMana(int mana) {
		this.mana = mana;
	}
	public void setMana(ActorRef out, int mana) {
		this.mana = mana;
	}
	/**
	 * Returns this player's avatar unit.
	 * @author Minghao
	 */
	public BetterUnit getAvatar() {
		return avatar;
	}
	public void setAvatar(BetterUnit avatar) {
		this.avatar = avatar;
	}
	/**
	 * Returns the player's draw pile.
	 * @author Minghao
	 */
	public List<Card> getDeck() {
		return deck;
	}
	/**
	 * Sets the player's draw pile.
	 * @param deck list of cards forming the deck
	 * @author Minghao
	 */
	public void setDeck(List<Card> deck) {
		this.deck = deck;
	}
	/**
	 * Returns the cards currently in the player's hand.
	 * @author Minghao
	 */
	public List<Card> getHand() {
		return hand;
	}
	/** Maximum number of cards a player can hold in hand (Story Card #21). */
	public static final int MAX_HAND_SIZE = 6;

	/**
	 * Draws the top card from the deck (Story Card #21).
	 * The card is always removed from the deck; it is added to the hand only
	 * if the hand has fewer than MAX_HAND_SIZE cards.  If the hand is already
	 * full the drawn card is silently discarded.
	 * Does nothing if the deck is empty.
	 * @author Minghao
	 */
	public void drawCard() {
		if (deck == null || deck.isEmpty()) return;
		Card drawn = deck.remove(0);
		if (hand.size() < MAX_HAND_SIZE) {
			hand.add(drawn);
		}
		// drawn card is silently discarded when hand is full
	}


}
