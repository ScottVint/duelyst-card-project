import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.players.AIPlayer;
import structures.basic.players.HumanPlayer;
import structures.basic.unittypes.Unit;
import structures.logic.CombatLogic;
import utils.BasicObjectBuilders;

import static org.junit.Assert.*;

public class CombatLogicTest {

    GameState gs = new GameState();
    HumanPlayer player = gs.player1;
    AIPlayer opponent = gs.player2;
    CheckMessageIsNotNullOnTell altTell;

    @Before
    public void setUp() {
        altTell = new CheckMessageIsNotNullOnTell();
        BasicCommands.altTell = altTell;

        player.setAvatar(null, gs);
        gs.placeAvatar(null, player.getAvatar(), 3, 2);

        opponent.setAvatar(null, gs);
        gs.placeAvatar(null, opponent.getAvatar(), 7, 2);

        player.getAvatar().hasMoved = false;
        opponent.getAvatar().hasMoved = false;

        player.getHand().clear();
        opponent.getHand().clear();
    }

    @After
    public void tearDown() {
        BasicCommands.altTell = null;
        for (Tile[] row : gs.getBoard().getTiles()) {
            for (Tile tile : row) {
                tile.setUnit(null);
            }
        }
    }

    private Unit summonHumanUnit(String cardPath, int x, int y) {
        Card card = BasicObjectBuilders.loadCard(cardPath, 1, Card.class);
        player.getHand().add(card);
        Tile summonTile = gs.getBoard().getTile(x, y);
        player.useCard(null, gs, player.getHand().size() - 1, summonTile, 0);
        return summonTile.getUnit();
    }

    private Unit summonAIUnit(String cardPath, int x, int y) {
        Card card = BasicObjectBuilders.loadCard(cardPath, 1, Card.class);
        opponent.getHand().add(card);
        Tile summonTile = gs.getBoard().getTile(x, y);
        opponent.useCard(null, gs, opponent.getHand().size() - 1, summonTile, 0);
        return summonTile.getUnit();
    }

    @Test
    public void defenderCounterattacksIfStillAlive() {
        Unit attacker = summonHumanUnit("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json", 4, 2);
        Unit defender = summonAIUnit("conf/gameconfs/cards/2_6_c_u_young_flamewing.json", 5, 2);

        int attackerHpBefore = attacker.getHealth();
        int defenderHpBefore = defender.getHealth();

        CombatLogic.resolveCombat(null, gs, attacker, defender);

        assertEquals("Defender should take attacker damage.", defenderHpBefore - attacker.getAttack(), defender.getHealth());
        assertTrue("Attacker should take counterattack damage if defender survives.", attacker.getHealth() < attackerHpBefore);
        assertTrue("Defender should mark that it has counterattacked.", defender.hasCounterattacked);
    }

    @Test
    public void defenderDoesNotCounterattackIfKilled() {
        Unit attacker = summonHumanUnit("conf/gameconfs/cards/2_6_c_u_young_flamewing.json", 4, 2);
        Unit defender = summonAIUnit("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json", 5, 2);

        int attackerHpBefore = attacker.getHealth();

        CombatLogic.resolveCombat(null, gs, attacker, defender);

        assertTrue("Defender should die from the initial hit.", defender.isDead());
        assertEquals("Attacker should not take counterattack damage if defender dies first.", attackerHpBefore, attacker.getHealth());
        assertFalse("Dead defender should not mark counterattack usage.", defender.hasCounterattacked);
    }

    @Test
    public void defenderOnlyCounterattacksOncePerTurn() {
        Unit attacker1 = summonHumanUnit("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json", 4, 2);
        Unit attacker2 = summonHumanUnit("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json", 5, 3);
        Unit defender = summonAIUnit("conf/gameconfs/cards/2_6_c_u_young_flamewing.json", 5, 2);

        int attacker1HpBefore = attacker1.getHealth();
        int attacker2HpBefore = attacker2.getHealth();

        CombatLogic.resolveCombat(null, gs, attacker1, defender);
        CombatLogic.resolveCombat(null, gs, attacker2, defender);

        assertTrue("First attacker should take the one allowed counterattack.", attacker1.getHealth() < attacker1HpBefore);
        assertEquals("Second attacker should not take a second counterattack in the same turn.", attacker2HpBefore, attacker2.getHealth());
    }

    @Test
    public void attackingConsumesAttackAndMovement() {
        Unit attacker = summonHumanUnit("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json", 4, 2);
        Unit defender = summonAIUnit("conf/gameconfs/cards/2_6_c_u_young_flamewing.json", 5, 2);

        CombatLogic.resolveCombat(null, gs, attacker, defender);

        assertTrue("Attacking should consume the attack action.", attacker.hasAttacked);
        assertTrue("Attacking should also consume movement for the turn.", attacker.hasMoved);
    }

    @Test
    public void endTurnResetsActionFlagsForStartingPlayer() {
        Unit unit = summonHumanUnit("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json", 4, 2);

        player.getAvatar().hasMoved = true;
        player.getAvatar().hasAttacked = true;
        player.getAvatar().hasCounterattacked = true;

        unit.hasMoved = true;
        unit.hasAttacked = true;
        unit.hasCounterattacked = true;

        // Simulate AI turn ending so player turn is starting
        gs.player1Turn = false;
        gs.endTurn(null, opponent, player);

        assertFalse("Player avatar movement flag should refresh on new turn.", player.getAvatar().hasMoved);
        assertFalse("Player avatar attack flag should refresh on new turn.", player.getAvatar().hasAttacked);
        assertFalse("Player avatar counterattack flag should refresh on new turn.", player.getAvatar().hasCounterattacked);

        assertFalse("Unit movement flag should refresh on new turn.", unit.hasMoved);
        assertFalse("Unit attack flag should refresh on new turn.", unit.hasAttacked);
        assertFalse("Unit counterattack flag should refresh on new turn.", unit.hasCounterattacked);
    }
}