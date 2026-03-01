package structures.basic.players;
import akka.actor.ActorRef;

/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Player {

	// For use in subclasses, these are now protected instead of private
	protected int health;
	protected int mana;

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
	public void setHealth(ActorRef out, int health) {
		throw new Error("Unknown Player subclass");
	}
	public int getMana() {
		return mana;
	}
	public void setMana(ActorRef out, int mana) {
		this.mana = mana;
	}
	
	
	
}
