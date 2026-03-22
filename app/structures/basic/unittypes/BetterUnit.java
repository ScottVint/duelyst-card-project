package structures.basic.unittypes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.players.Player;
import structures.logic.BoardLogic;
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

	@Override
	public void setHealth(ActorRef out, Player player, int health) {
		super.setHealth(out, health);
		player.setHealth(out, this.health);
	}

	public int getHornCharges() {return hornCharges;}
	public void setHornCharges(int charge) {
		if (charge < 0)
			this.hornCharges = 0;
		else if (charge > 3)
			this.hornCharges = 3;
		else
			this.hornCharges = charge;
	}

	/**
	 * Avatar damage taking method.
	 * Sets the health of the unit and displays it, then sets the corresponding
	 * player's health to the same value. <br />
	 * The display is currently unimplemented.
	 * @param damage
	 * @author Scott
	 */
	@Override
	public void takeDamage(ActorRef out, GameState gameState, int damage) {
		// Summon a wraithling in a random adjacent tile if unit has horn charges
		if (hornCharges > 0 ) {
			hornCharges--;
			List<Tile> summonable = new ArrayList<>(BoardLogic.findAdjacentTiles(this.tileOccupied, gameState.getBoard()));
			// Remove occupied tiles
            summonable.removeIf(tile -> tile.getUnit() != null);
			if (!summonable.isEmpty()) {
				int idx = (int) (Math.random() * summonable.size());
				Tile tile = summonable.get(idx);
				summonWraithling(out, tile, this.owner, gameState);
			}
		}
		super.takeDamage(out, damage);
		setHealth(out, this.health);
	}
	
	
	public static void main(String[] args) {
		
		BetterUnit unit = (BetterUnit)BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, BetterUnit.class);
		Set<String> keywords = new HashSet<String>();
		keywords.add("MyKeyword");
		unit.setKeywords(keywords);
		
		System.err.println(unit.getClass());
		
	}
}
