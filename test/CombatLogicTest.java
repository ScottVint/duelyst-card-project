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

    // ── SP30: Move-then-Attack ────────────────────────────────────────────────

    @Test
    public void findAutoAttackDestinationReturnsNearestTileForReachableEnemy() {
        // Human unit at (4,2), AI avatar is at (7,2); target = (7,2).
        // The attacker can reach (6,2) in one turn (2 cardinal steps right) and
        // (6,2) is adjacent to (7,2) -> valid destination exists.
        Unit attacker = summonHumanUnit("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json", 4, 2);
        Tile enemyTile = gs.getBoard().getTile(7, 2); // AI avatar tile

        Tile result = CombatLogic.findAutoAttackDestination(attacker, enemyTile, gs.getBoard());

        assertNotNull("Should find a move tile from which the enemy is attackable.", result);
        // (6,2) is the closest reachable tile adjacent to (7,2)
        assertEquals(6, result.getTilex());
        assertEquals(2, result.getTiley());
    }

    @Test
    public void findAutoAttackDestinationReturnsNullWhenEnemyOutOfRange() {
        // Human unit at (4,2); place AI unit at (8,4) (far corner).
        // Max movement reaches x=6, but no tile within reach is adjacent to (8,4).
        Unit attacker = summonHumanUnit("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json", 4, 2);
        Unit farEnemy = summonAIUnit("conf/gameconfs/cards/1_1_c_u_bad_omen.json", 8, 4);
        Tile enemyTile = gs.getBoard().getTile(8, 4);

        Tile result = CombatLogic.findAutoAttackDestination(attacker, enemyTile, gs.getBoard());

        assertNull("Should return null when enemy cannot be reached even after moving.", result);
    }

    @Test
    public void findAutoAttackDestinationReturnsNullForEmptyTile() {
        Unit attacker = summonHumanUnit("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json", 4, 2);
        Tile emptyTile = gs.getBoard().getTile(6, 2); // no unit here

        Tile result = CombatLogic.findAutoAttackDestination(attacker, emptyTile, gs.getBoard());

        assertNull("Should return null when the target tile has no unit.", result);
    }

    @Test
    public void startPendingAttackAfterMoveSetsState() {
        Unit attacker = summonHumanUnit("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json", 4, 2);
        Tile targetTile = gs.getBoard().getTile(7, 2);

        gs.startPendingAttackAfterMove(attacker, targetTile);

        assertTrue("pendingAttackAfterMove should be true.", gs.pendingAttackAfterMove);
        assertEquals("pendingAttackAttacker should reference the attacker.", attacker, gs.pendingAttackAttacker);
        assertEquals("pendingAttackTargetTile should reference the target.", targetTile, gs.pendingAttackTargetTile);
    }

    @Test
    public void clearPendingAttackAfterMoveClearsState() {
        Unit attacker = summonHumanUnit("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json", 4, 2);
        Tile targetTile = gs.getBoard().getTile(7, 2);

        gs.startPendingAttackAfterMove(attacker, targetTile);
        gs.clearPendingAttackAfterMove();

        assertFalse("pendingAttackAfterMove should be cleared.", gs.pendingAttackAfterMove);
        assertNull("pendingAttackAttacker should be null after clear.", gs.pendingAttackAttacker);
        assertNull("pendingAttackTargetTile should be null after clear.", gs.pendingAttackTargetTile);
    }
}