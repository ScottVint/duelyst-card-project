package structures.basic.players;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;

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
	Unit avatar;
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
	public Unit getAvatar() {
		return avatar;
	}
	/**
	 * Sets this player's avatar unit.
	 * @param avatar the avatar unit to assign
	 * @author Minghao
	 */
	public void setAvatar(Unit avatar) {
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
	/**
	 * Draws the top card from the deck into the hand.
	 * Does nothing if the deck is empty.
	 * @author Minghao
	 */
	public void drawCard() {
		if (deck != null && !deck.isEmpty()) {
			hand.add(deck.remove(0));
		}
	}


}
