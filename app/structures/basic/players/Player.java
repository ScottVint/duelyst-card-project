package structures.basic.players;

import structures.basic.Card;
import structures.basic.Deck;
import structures.basic.Hand;

/**
 * A basic representation of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Player {

	// For use in subclasses, these are now protected instead of private
	protected int health;
	protected int mana;
	protected Deck deck = new Deck(this);
	protected Hand hand = new Hand();

	public Player() {
		super();
		this.health = 20;
		this.mana = 0;
	}
	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public int getMana() {
		return mana;
	}
	public void setMana(int mana) {
		this.mana = mana;
	}

	// Shared methods
	public Deck getDeck() { return deck; }
	public Hand getHand() { return hand; }

	public void drawToHand() {
		Card card = this.deck.drawFromDeck();
		System.out.println(card);
		hand.addCard(card);
	}
	
	
	
}
