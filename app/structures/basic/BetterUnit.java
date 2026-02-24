package structures.basic;

import java.util.HashSet;
import java.util.Set;

import akka.actor.ActorRef;
import commands.BasicCommands;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class BetterUnit extends Unit {

	Set<String> keywords;

	public BetterUnit() {}
	
	public BetterUnit(Set<String> keywords) {
		super();
		this.keywords = keywords;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	};

	public void setHealth(ActorRef out, Player player, int health) {
		super.setHealth(health);
		player.setHealth(this.health);
		BasicCommands.setPlayer1Health(out, this.health);
	}

	/**
	 * Avatar damage taking method.
	 * Sets the health of the unit and displays it, then sets the corresponding
	 * player's health to the same value. <br />
	 * The display is currently unimplemented.
	 * @param player
	 * @param damage
	 * @author Scott
	 */
	public void takeDamage(Player player, int damage) {
		super.takeDamage(damage);
		player.setHealth(this.health);
	}
	
	
	public static void main(String[] args) {
		
		BetterUnit unit = (BetterUnit)BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, BetterUnit.class);
		Set<String> keywords = new HashSet<String>();
		keywords.add("MyKeyword");
		unit.setKeywords(keywords);
		
		System.err.println(unit.getClass());
		
	}
}
