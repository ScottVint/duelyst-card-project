package structures.basic.players;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;

import commands.BasicCommands;
import structures.basic.BetterUnit;
import structures.basic.Card;
import structures.basic.Deck;
import structures.basic.Unit;

/**
 * A basic representation of the Player. A player
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
	private static final int MAX_MANA = 9;
	protected BetterUnit avatar;
	protected Deck deck;
	protected List<Card> hand;

	public Player() {
		super();
		this.health = 20;
		this.mana = 0;
		// Check player class and pass it to deck.
		// Return an error otherwise.
		try {
			this.deck = new Deck(this);
		}  catch (IllegalArgumentException e) {
			System.err.println(e.getMessage() + "Initialising empty deck...");
			this.deck = new Deck();
		}
		this.hand = new ArrayList<>();
		// Note: setAvatar(BetterUnit) must be called externally after construction
	}

	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
		this.deck = new Deck(this);
		this.hand = new ArrayList<>();
	}

	public int getHealth() { return health; }
	public void setHealth(int health) { this.health = health; }

	public void setHealth(ActorRef out, int health) {
		throw new Error("Unknown Player subclass");
	}

	public int getMana() { return mana; }
	public void setMana(ActorRef out, int mana) {
		if (mana > 9) {
			mana = 9;
		} else if (mana < 0) {
			mana = 0;
		}
		this.mana = mana;
	}

	public void setMana(int mana) {
		if (mana > 9) {
			mana = 9;
		} else if (mana < 0) {
			mana = 0;
		}
		this.mana = mana;
	}

	public static int getMaxMana() { return MAX_MANA; }

	public BetterUnit getAvatar() { return avatar; }

	/** Sets the avatar unit for this player (used by GameState during initialisation). */
	public void setAvatar(BetterUnit avatar) {
		this.avatar = avatar;
	}

	/** Returns the player's draw pile. */
	public List<Card> getDeck() { return deck.cards; }

	public List<Card> getHand() { return hand; }

	/** Maximum number of cards a player can hold in hand (Story Card #21). */
	public static final int MAX_HAND_SIZE = 6;

	/**
	 * Draws the top card from the deck (Story Card #21).
	 * Always removes the top card from the deck; adds it to the hand only if
	 * the hand has fewer than MAX_HAND_SIZE cards. Silently discards if full.
	 * Does nothing if the deck is empty.
	 * @author Minghao
	 */
	public void drawCard() {
		if (deck == null || deck.cards.isEmpty()) return;
		Card drawn = deck.cards.remove(0);
		if (hand.size() < MAX_HAND_SIZE) {
			hand.add(drawn);
		}
		// drawn card is silently discarded when hand is full
	}

	/// Displays all cards in hand to the screen.
	/// @author Scott
	public void drawHand(ActorRef out) {
		for (Card card : hand) {
			BasicCommands.drawCard(out, card, hand.indexOf(card) + 1, 0);
		}
	}

	public String toString() {
		return "Unknown Player";
	}
}
