package utils;

import java.util.ArrayList;
import java.util.List;

import structures.basic.Card;

/**
 * Temporary fixed deck loader for testing.
 */
public class OrderedCardLoader {

	public static String cardsDIR = "conf/gameconfs/cards/";

	/**
	 * Fixed test deck for player 1.
	 * Opening hand will be the first 3 cards in this list.
	 */
	public static List<Card> getPlayer1Cards(int copies) {

		List<Card> cardsInDeck = new ArrayList<Card>();
		int cardID = 1;

		String[] testDeck = {
				"1_2_c_s_hornoftheforsaken.json",   // Horn of the Forsaken
				"1_5_c_s_wraithling_swarm.json",   // Wraithling Swarm
				"1_8_c_s_dark_terminus.json",      // Dark Terminus
				"2_9_c_s_sundrop_elixir.json",     // Sundrop Elixir
				"2_a1_c_s_truestrike.json",        // Truestrike
				"1_3_c_u_gloom_chaser.json",       // Gloom Chaser
				"1_1_c_u_bad_omen.json"            // Bad Omen
		};

		for (String filename : testDeck) {
			cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR + filename, cardID++, Card.class));
		}

		return cardsInDeck;
	}

	/**
	 * Keep player 2 simple for now.
	 */
	public static List<Card> getPlayer2Cards(int copies) {

		List<Card> cardsInDeck = new ArrayList<Card>();
		int cardID = 1;

		String[] aiDeck = {
				"2_1_c_u_skyrock_golem.json",
				"2_2_c_u_swamp_entangler.json",
				"2_3_c_u_silverguard_knight.json",
				"2_4_c_u_saberspine_tiger.json",
				"2_5_c_s_beamshock.json",
				"2_6_c_u_young_flamewing.json",
				"2_7_c_u_silverguard_squire.json",
				"2_8_c_u_ironcliff_guardian.json"
		};

		for (String filename : aiDeck) {
			cardsInDeck.add(BasicObjectBuilders.loadCard(cardsDIR + filename, cardID++, Card.class));
		}

		return cardsInDeck;
	}
}