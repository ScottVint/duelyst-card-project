package structures.basic;


import akka.actor.ActorRef;
import com.fasterxml.jackson.annotation.JsonIgnore;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.players.Player;
import structures.basic.spells.*;
import structures.basic.unittypes.Unit;
import structures.logic.BoardLogic;
import utils.BasicObjectBuilders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is the base representation of a Card which is rendered in the player's hand.
 * A card has an id, a name (cardname) and a manacost. A card then has a large and mini
 * version. The mini version is what is rendered at the bottom of the screen. The big
 * version is what is rendered when the player clicks on a card in their hand.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Card {

	int id;

	String cardname;
	int manacost;

	MiniCard miniCard;
	BigCard bigCard;

	boolean isCreature;
	String unitConfig;

	Spell spell = null;

	public Card() {
	}

	;

	public Card(int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard, boolean isCreature, String unitConfig) {
		super();
		this.id = id;
		this.cardname = cardname;
		this.manacost = manacost;
		this.miniCard = miniCard;
		this.bigCard = bigCard;
		this.isCreature = isCreature;
		this.unitConfig = unitConfig;

		Class<? extends Spell> spellClass = findSpell(this.cardname);
		if (spellClass != null) {
			try {
				this.spell = spellClass.newInstance();
			} catch (InstantiationException e) {
				System.err.println("Whoops!");
			} catch (IllegalAccessException e) {
				System.err.println("Whoops!");
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCardname() {
		return cardname;
	}

	public void setCardname(String cardname) {
		this.cardname = cardname;
	}

	public int getManacost() {
		return manacost;
	}

	public void setManacost(int manacost) {
		this.manacost = manacost;
	}

	public MiniCard getMiniCard() {
		return miniCard;
	}

	public void setMiniCard(MiniCard miniCard) {
		this.miniCard = miniCard;
	}

	public BigCard getBigCard() {
		return bigCard;
	}

	public void setBigCard(BigCard bigCard) {
		this.bigCard = bigCard;
	}

	public boolean getIsCreature() {
		return isCreature;
	}

	public void setIsCreature(boolean isCreature) {
		this.isCreature = isCreature;
	}

	public void setCreature(boolean isCreature) {
		this.isCreature = isCreature;
	}

	public boolean isCreature() {
		return isCreature;
	}

	public String getUnitConfig() {
		return unitConfig;
	}

	public void setUnitConfig(String unitConfig) {
		this.unitConfig = unitConfig;
	}

	//TODO Unify to abstract cast() method
	public static void useNonCreatureCardOnEmptyTile(ActorRef out,
													 GameState gameState,
													 Card card,
													 Tile clickedTile,
													 int cardIndex) {

		String cardName = card.getCardname();

		switch (cardName) {
			case "Wraithling Swarm":
				spell.cast(out, gameState, clickedTile, cardIndex);
				break;

			case "Horn of the Forsaken":
				BasicCommands.addPlayer1Notification(out, "Horn must target your avatar", 2);
				break;

			default:
				BasicCommands.addPlayer1Notification(out, "This card must target a unit.", 2);
				break;
		}
	}

	public static void useNonCreatureCardOnUnit(ActorRef out,
												GameState gameState,
												Card card,
												Unit targetUnit,
												int cardIndex) {

		String cardName = card.getCardname();

		switch (cardName) {

			case "Horn of the Forsaken":
				if (targetUnit != gameState.getPlayer1().getAvatar()) {
					BasicCommands.addPlayer1Notification(out, "Horn must target your avatar", 2);
				} else if (gameState.getPlayer1().getMana() < card.getManacost()) {
					BasicCommands.addPlayer1Notification(out, "Not enough mana", 2);
				} else {
					gameState.equipPlayer1Horn();
					gameState.player1.useCard(out, gameState, cardIndex, card.getManacost());
					BasicCommands.addPlayer1Notification(out, "Horn equipped (3)", 2);
				}
				break;

			case "Truestrike":
				if (targetUnit.getOwner() != gameState.getPlayer2()) { //TODO Use subclasses, also delete duplicate code
					BasicCommands.addPlayer1Notification(out, "Truestrike must target an enemy unit", 2);
				} else if (gameState.getPlayer1().getMana() < card.getManacost()) {
					BasicCommands.addPlayer1Notification(out, "Not enough mana", 2);
				} else {
					gameState.dealDirectDamage(out, targetUnit, 2); //TODO Create DamageLogic Class
					gameState.player1.useCard(out, gameState, cardIndex, card.getManacost());
					BasicCommands.addPlayer1Notification(out, "Truestrike cast", 2);
				}
				break;

			case "Sundrop Elixir":
				if (gameState.getPlayer1().getMana() < card.getManacost()) {
					BasicCommands.addPlayer1Notification(out, "Not enough mana", 2);
				} else {
					gameState.healUnit(out, targetUnit, 5);

					gameState.player1.useCard(out, gameState, cardIndex, card.getManacost());
					BasicCommands.addPlayer1Notification(out, "Sundrop Elixir cast", 2);
				}
				break;

			case "Dark Terminus":
				if (targetUnit.getOwner() != gameState.getPlayer2()) {
					BasicCommands.addPlayer1Notification(out, "Dark Terminus must target an enemy unit", 2);
				} else if (targetUnit == gameState.getPlayer2().getAvatar()) {
					BasicCommands.addPlayer1Notification(out, "Dark Terminus cannot target enemy avatar", 2);
				} else if (gameState.getPlayer1().getMana() < card.getManacost()) {
					BasicCommands.addPlayer1Notification(out, "Not enough mana", 2);
				} else {
					Tile deathTile = gameState.getBoard().getTile(
							targetUnit.getPosition().getTilex(),
							targetUnit.getPosition().getTiley()
					);


					gameState.death(out, targetUnit);
					gameState.summonWraithling(out, deathTile, gameState.getPlayer1());

					gameState.player1.useCard(out, gameState, cardIndex, card.getManacost());
					BasicCommands.addPlayer1Notification(out, "Dark Terminus cast", 2);
				}
				break;

			default:
				BasicCommands.addPlayer1Notification(out, "Spell/artifact not implemented yet.", 2);
		}
	}


	public Unit createUnit(ActorRef out, GameState gameState, Player player) {
		int handPosition = gameState.selectedHandPosition;
		int cardIndex = handPosition - 1;
		Card card = player.getHand().get(cardIndex);


		Class<? extends Unit> unitClass = Unit.findUnitClass(unitConfig);
		Unit summonedUnit = BasicObjectBuilders.loadUnit(this.unitConfig, gameState.getNextUnitId(), unitClass);
		summonedUnit = unitClass.cast(summonedUnit);

		summonedUnit.setOwner(player);
		summonedUnit.setAttack(card.getBigCard().getAttack());
		summonedUnit.setMaxHealth(card.getBigCard().getHealth());
		summonedUnit.setHealth(out, card.getBigCard().getHealth());

		System.out.println("Unit created: " + summonedUnit.getClass());

		return summonedUnit;
	}


	public void summon(ActorRef out, GameState gameState, Player player, Tile clickedTile, Board board) {
		int handPosition = gameState.selectedHandPosition;
		int cardIndex = handPosition - 1;
		Card card = player.getHand().get(cardIndex);

		if (player.enoughMana(out, card.getManacost()) && BoardLogic.findValidSummonTiles(player, board).contains(clickedTile)) {

			Unit summonedUnit = createUnit(out, gameState, player);

			summonedUnit.setPositionByTile(clickedTile);
			clickedTile.setUnit(summonedUnit);

			BasicCommands.drawUnit(out, summonedUnit, clickedTile);
			BasicCommands.setUnitAttack(out, summonedUnit, summonedUnit.getAttack());
			BasicCommands.setUnitHealth(out, summonedUnit, summonedUnit.getHealth());

			player.useCard(out, gameState, cardIndex, card.getManacost());
		}
	}

	/// Method to find the relevant spell's class
	public Class<? extends Spell> findSpell(String spellName) {
		Class<? extends Spell> spellClass = null;
		switch (spellName) {
			case "Dark Terminus":
				spellClass = DarkTerminus.class;
				break;

			case "Wraithling Swarm":
				spellClass = WraithlingSwarm.class;
				break;

			case "Horn of the Forsaken":
				spellClass = HornOfTheForsaken.class;
				break;

			case "Sundrop Elixir":
				spellClass = SundropElixir.class;
				break;

			case "True Strike":
				spellClass = Truestrike.class;
				break;

			case "Beam Shock":
				spellClass = Beamshock.class;
				break;
		}
		return spellClass;
	}
}

