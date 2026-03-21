package structures.basic;

import java.util.HashSet;
import java.util.Set;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.players.Player;
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
		BasicCommands.setUnitHealth(out, this, this.health);
		player.setHealth(out, this.health);
	}

	/**
	 * Reduces health, updates the HP badge, triggers death (SC#35), and—if this
	 * unit is its owner's avatar—syncs the player health bar (SC#2).
	 * @author Scott
	 * @author Minghao
	 */
	@Override
	public void takeDamage(ActorRef out, int damage) {
		super.takeDamage(out, damage);
		if (owner != null && owner.getAvatar() == this) {
			owner.setHealth(out, this.health);
		}
	}

	/**
	 * Convenience overload that explicitly names the owning player.
	 * Delegates to the single-player-aware override above.
	 * @author Scott
	 */
	public void takeDamage(ActorRef out, Player player, int damage) {
		super.takeDamage(out, damage);
		player.setHealth(out, this.health);
	}
	
	
	public static void main(String[] args) {
		
		BetterUnit unit = (BetterUnit)BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, BetterUnit.class);
		Set<String> keywords = new HashSet<String>();
		keywords.add("MyKeyword");
		unit.setKeywords(keywords);
		
		System.err.println(unit.getClass());
		
	}
}
