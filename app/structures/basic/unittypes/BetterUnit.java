package structures.basic.unittypes;

import java.util.HashSet;
import java.util.Set;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.players.Player;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class BetterUnit extends Unit {

	Set<String> keywords;
	int hornCharges = 0;
	public final int MAX_HORN_CHARGES = 3;


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

	/**
	 * Story Card #2: sets unit health, updates the HP badge on the front-end,
	 * and syncs the owning player's health bar.
	 * @author Minghao
	 */
	@Override
	public void setHealth(ActorRef out, Player player, int health) {
		super.setHealth(out, health);
		// setUnitHealth is NOT called in super (Unit.setHealth only updates the field),
		// so we must emit it here to keep the front-end HP badge up-to-date (SC#2).
		BasicCommands.setUnitHealth(out, this, this.health);
		player.setHealth(out, this.health);
	}

	public int getHornCharges() {return hornCharges;}
	public void setHornCharges(int charge) {
		if (charge < 0) {
			this.hornCharges = 0;
		}
		else if (charge > 3) { // must check parameter `charge`, not the field `hornCharges`
			this.hornCharges = 3;
		}
		else {
			this.hornCharges = charge;
		}
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
	@Override
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
