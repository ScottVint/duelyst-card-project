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
import structures.basic.unittypes.BetterUnit;
import structures.basic.unittypes.GloomChaser;
import structures.basic.unittypes.Unit;
import structures.basic.unittypes.Wraithling;
import structures.logic.BoardLogic;
import utils.BasicObjectBuilders;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class AbilityTest {
    GameState gs = new GameState();
    HumanPlayer player = gs.player1;
    AIPlayer opponent = gs.player2;
    CheckMessageIsNotNullOnTell altTell;

    @Before
    public void setUp() {
        altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
        BasicCommands.altTell = altTell; // specify that the alternative tell should be used
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
        for (Tile[] row : gs.getBoard().getTiles()) {
            for (Tile tile : row) {
                tile.setUnit(null);
            }
        }
    }


    ///////////// Gloom Chaser //////////////
    @Test
    public void gloomChaserGambitSummonedTest () {
        // Gloomchaser summons wraithling in emtpy space
        Card testCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/1_3_c_u_gloom_chaser.json", 12, Card.class);

        player.getHand().add(testCard);
        Tile summonTile = gs.getBoard().getTile(3, 3);
        player.useCard(null, gs, 0, summonTile, 0);
        Unit unitOnTile = gs.getBoard().getTile(2, 3).getUnit();
        assertNotNull("Unit should be summoned", unitOnTile);
        assertEquals("Wraithling should appear in empty space behind summoned unit.", Wraithling.class, unitOnTile.getClass());
    }

    @Test
    public void gloomChaserGambitDoesNotOverwriteTest () {
        // Gloomchaser does not overwrite occupied tile
        Card testCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/1_3_c_u_gloom_chaser.json", 1, Card.class);
        player.getHand().add(testCard);
        Tile summonTile = gs.getBoard().getTile(4, 2);
        player.useCard(null, gs, 0, summonTile, 0);
        Unit unitOnTile = gs.getBoard().getTile(3, 2).getUnit();
        assertNotNull("Unit should be summoned", unitOnTile);
        assertEquals("Wraithling should not overwrite existing unit.", BetterUnit.class, unitOnTile.getClass());
    }

    @Test
    public void gloomChaserDoesNotCrash() {
        // Summon does not crash if done at edge of board
        Card testCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/1_3_c_u_gloom_chaser.json", 1, Card.class);
        player.getHand().add(testCard);
        Tile summonTile = gs.getBoard().getTile(0, 3);
        player.useCard(null, gs, 0, summonTile, 0);
        Unit unitOnTile = gs.getBoard().getTile(0, 3).getUnit();

        assertNotNull("Unit should be summoned", unitOnTile);
        assertEquals("Wraithling should not overwrite existing unit.", GloomChaser.class, unitOnTile.getClass());
    }

    // /////////// Nightsorrow Assassin /////////////
    @Test
    public void nightsorrowAssassinOpeningGambitTest() {
        // Check if valid target is killed
        Card opponentCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_6_c_u_young_flamewing.json", 2, Card.class);
        opponent.getHand().add(opponentCard);

        Card testCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/1_6_c_u_nightsorrow_assassin.json", 1, Card.class);
        player.getHand().add(testCard);

        Tile targetTile = gs.getBoard().getTile(3, 0);
        opponent.useCard(null, gs, 0, targetTile, 0);
        Unit target = targetTile.getUnit();
        target.takeDamage(null, 1);

        Tile summonTile = gs.getBoard().getTile(4, 1);
        player.useCard(null, gs, 0, summonTile, 0);

        assertNull("Target should be deleted.", targetTile.getUnit());
    }

    @Test
    public void nightsorrowAssassinGambitFullHPStaysTest() {
        // Check if full HP target is not deleted
        Card opponentCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_6_c_u_young_flamewing.json", 2, Card.class);
        Card testCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/1_6_c_u_nightsorrow_assassin.json", 1, Card.class);
        Tile  targetTile = gs.getBoard().getTile(3, 0);
        Tile summonTile = gs.getBoard().getTile(4, 1);

        opponent.getHand().add(opponentCard);
        player.getHand().add(testCard);

        opponent.useCard(null, gs, 0, targetTile, 0);
        player.useCard(null, gs, 0, summonTile, 0);

        assertNotNull("Full HP unit should not be targeted", targetTile.getUnit());
        summonTile.setUnit(null);
    }

    @Test
    public void nightsorrowAssassinGambitNoAllyKillTest() {
        // Check if allied unit is deleted
        Card opponentCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_6_c_u_young_flamewing.json", 2, Card.class);
        Card testCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/1_6_c_u_nightsorrow_assassin.json", 1, Card.class);
        Tile targetTile = gs.getBoard().getTile(3, 0);
        Tile summonTile = gs.getBoard().getTile(4, 1);

        player.getHand().add(opponentCard);
        player.getHand().add(testCard);

        player.useCard(null, gs, 0, targetTile, 0);
        player.useCard(null, gs, 0, summonTile, 0);

        assertNotNull("Allied unit should not be targeted", targetTile.getUnit());
        summonTile.setUnit(null);
    }

    @Test
    public void nightsorrowAssassinGambitNoAvatarKillTest() {
        Card testCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/1_6_c_u_nightsorrow_assassin.json", 1, Card.class);

        player.getHand().add(testCard);
        Tile avatarTile = gs.getBoard().getTile(7, 2);
        Tile nextToAvatar = gs.getBoard().getTile(7, 3);
        opponent.getAvatar().takeDamage(null, gs, 1);
        player.useCard(null, gs, 0, nextToAvatar, 0);
        assertNotNull("Avatar unit should not be targeted", avatarTile.getUnit());
        nextToAvatar.setUnit(null);
    }

    @Test
    public void nightsorrowAssassinGambit1UnitKilledTest() {
        Card opponentCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_6_c_u_young_flamewing.json", 2, Card.class);
        Card testCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/1_6_c_u_nightsorrow_assassin.json", 1, Card.class);
        Tile summonTile = gs.getBoard().getTile(4, 1);

        // Check if only one unit is killed
        int after = 0;
        for (int i = 0; i < 8; i++)
            opponent.getHand().add(opponentCard);
        Set<Tile> surroundingTargets = BoardLogic.findAdjacentTiles(summonTile, gs.getBoard());
        for  (Tile tile : surroundingTargets) {
            opponent.useCard(null, gs, 0, tile, 0);
            tile.getUnit().takeDamage(null, gs, 1);
        }
        player.getHand().add(testCard);
        player.useCard(null, gs, 0, summonTile, 0);
        for (Tile tile : surroundingTargets) {
            if (tile.getUnit() != null)
                after++;
        }
        assertEquals("Only one target should be deleted.",  7, after);
    }

    /// /////////// Silverguard Squire /////////////
    @Test
    public void silverguardSquireBuffOneUnitTest() {
        Card allyCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json",  1, Card.class);
        Card squireCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_7_c_u_silverguard_squire.json", 1 , Card.class);
        Tile summonLeft = gs.getBoard().getTile(2,2);
        Tile unitSummon = gs.getBoard().getTile(0,0);

        player.getHand().add(allyCard);
        player.getHand().add(squireCard);

        player.useCard(null, gs, 0, summonLeft, 0);
        Unit ally = summonLeft.getUnit();
        ally.takeDamage(null, gs, 1);
        int startHP = ally.getHealth(), startMax = ally.getMaxHealth(), startAttack = ally.getAttack();
        int expHP = startHP + 1, expMax = startMax + 1, expAttack = startAttack + 1;

        player.useCard(null, gs, 0, unitSummon, 0);

        assertEquals("HP should increase.", expHP, ally.getHealth());
        assertEquals("Max HP should increase.", expMax, ally.getMaxHealth());
        assertEquals("Attack should increase.", expAttack, ally.getAttack());
    }

    @Test
    public void silverguardSquireBuffBothUnitsTest() {
        Card allyCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json",  1, Card.class);
        Card squireCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_7_c_u_silverguard_squire.json", 1 , Card.class);
        Tile summonLeft = gs.getBoard().getTile(2,2);
        Tile summonRight = gs.getBoard().getTile(4,2);
        Tile unitSummon = gs.getBoard().getTile(0,0);

        player.getHand().add(allyCard);
        player.getHand().add(allyCard);
        player.getHand().add(squireCard);

        player.useCard(null, gs, 0, summonLeft, 0);
        player.useCard(null, gs, 0, summonRight, 0);
        Unit ally = summonLeft.getUnit();
        Unit allyRight = summonRight.getUnit();
        ally.takeDamage(null, gs, 1);
        allyRight.takeDamage(null, gs, 1);

        int startHP = ally.getHealth(), startMax = ally.getMaxHealth(), startAttack = ally.getAttack();
        int expHP = startHP + 1, expMax = startMax + 1, expAttack = startAttack + 1;

        player.useCard(null, gs, 0, unitSummon, 0);

        assertEquals("HP should increase.", expHP, ally.getHealth());
        assertEquals("Both allies' HP should increase.", expHP, allyRight.getHealth());
        assertEquals("Max HP should increase.", expMax, ally.getMaxHealth());
        assertEquals("Both allies' max HP should increase.", expMax, allyRight.getMaxHealth());
        assertEquals("Attack should increase.", expAttack, ally.getAttack());
        assertEquals("Both allies' attack should increase.", expAttack, allyRight.getAttack());
    }

    @Test
    public void silverguardSquireDontBuffEnemiesTest() {
        Card enemyCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json",  1, Card.class);
        Card squireCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_7_c_u_silverguard_squire.json", 1 , Card.class);
        Tile summonLeft = gs.getBoard().getTile(2,2);
        Tile unitSummon = gs.getBoard().getTile(0,0);

        opponent.getHand().add(enemyCard);
        player.getHand().add(squireCard);

        opponent.useCard(null, gs, 0, summonLeft, 0);
        Unit enemy = summonLeft.getUnit();
        enemy.takeDamage(null, gs, 1);
        int startHP = enemy.getHealth(), startMax = enemy.getMaxHealth(), startAttack = enemy.getAttack();

        player.useCard(null, gs, 0, unitSummon, 0);

        assertEquals("Enemy HP should not increase.", startHP, enemy.getHealth());
        assertEquals("Enemy max HP should not increase.", startMax, enemy.getMaxHealth());
        assertEquals("Enemy attack should not increase.", startAttack, enemy.getAttack());
    }

    @Test
    public void silverguardSquireDontBuffAboveUnitsTest() {
            Card allyCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json",  1, Card.class);
            Card squireCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_7_c_u_silverguard_squire.json", 1 , Card.class);
            Tile summonUp = gs.getBoard().getTile(3,1);
            Tile summonDown = gs.getBoard().getTile(3,3);
            Tile unitSummon = gs.getBoard().getTile(0,0);

            player.getHand().add(allyCard);
            player.getHand().add(allyCard);
            player.getHand().add(squireCard);

            player.useCard(null, gs, 0, summonUp, 0);
            player.useCard(null, gs, 0, summonDown, 0);
            Unit ally = summonUp.getUnit();
            Unit allyRight = summonDown.getUnit();
            ally.takeDamage(null, gs, 1);
            allyRight.takeDamage(null, gs, 1);

            int startHP = ally.getHealth(), startMax = ally.getMaxHealth(), startAttack = ally.getAttack();

            player.useCard(null, gs, 0, unitSummon, 0);

            assertEquals("HP should not increase.", startHP, ally.getHealth());
            assertEquals("Both allies' HP should not increase.", startHP, allyRight.getHealth());
            assertEquals("Max HP should not increase.", startMax, ally.getMaxHealth());
            assertEquals("Both allies' max HP should not increase.", startMax, allyRight.getMaxHealth());
            assertEquals("Attack should not increase.", startAttack, ally.getAttack());
            assertEquals("Both allies' attack should not increase.", startAttack, allyRight.getAttack());
    }

    // /////////// Silverspine Tiger /////////////
    @Test
    public void saberspineTigerRushIsTrueTest() {
        Card tigerCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json", 2, Card.class);
        opponent.getHand().add(tigerCard);
        Tile summontile =  gs.getBoard().getTile(2,2);
        opponent.useCard(null, gs, 0, summontile, 0);
        Unit tiger =  summontile.getUnit();

        assertTrue("Silverspine Tiger should have rush attribute as true.", tiger.hasRush());
        assertEquals("Values should initialise as normal.", 2, tiger.getMaxHealth());
        assertEquals("Values should initialise as normal.", 3, tiger.getAttack());
    }

    @Test
    public void saberspineTigerRushWorksTest() {
        Card tigerCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json", 2, Card.class);
        opponent.getHand().add(tigerCard);
        Tile summontile =  gs.getBoard().getTile(2,2);
        opponent.useCard(null, gs, 0, summontile, 0);
        Unit tiger =  summontile.getUnit();

        assertFalse("Movement and Attack should be refreshed.", tiger.hasMoved);
        assertFalse("Movement and Attack should be refreshed.", tiger.hasAttacked);
    }

    // /////////// Young Flamewing /////////////
    @Test
    public void youngFlamewingFlyingIsTrueTest() {
        Card flamewingCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_6_c_u_young_flamewing.json", 2, Card.class);
        opponent.getHand().add(flamewingCard);
        Tile summontile =  gs.getBoard().getTile(2,2);
        opponent.useCard(null, gs, 0, summontile, 0);
        Unit flamewing =  summontile.getUnit();

        assertTrue("Young Flamewing should have flying attribute as true.", flamewing.hasFlying());
        assertEquals("Values should initialise as normal.", 4, flamewing.getMaxHealth());
        assertEquals("Values should initialise as normal.", 5, flamewing.getAttack());
    }

    @Test
    public void youngFlamewingFliesEverywhereTest() {
        player.getAvatar().getCurrentTile().setUnit(null);
        opponent.getAvatar().getCurrentTile().setUnit(null);

        Card flamewingCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_6_c_u_young_flamewing.json", 2, Card.class);
        opponent.getHand().add(flamewingCard);
        Tile summontile =  gs.getBoard().getTile(2,2);
        opponent.useCard(null, gs, 0, summontile, 0);
        Unit flamewing =  summontile.getUnit();
        flamewing.hasMoved = false;

        Set<Tile> expectedMovement =  new HashSet<>();
        for (Tile[] row : gs.getBoard().getTiles())
            Collections.addAll(expectedMovement, row);

        expectedMovement.remove(gs.getBoard().getTile(2,2));

        assertEquals(expectedMovement, BoardLogic.findValidMovement(summontile, flamewing, gs.getBoard()));
    }

    @Test
    public void youngFlamewingFlyingRespectsOtherUnitsTest() {
        Card flamewingCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_6_c_u_young_flamewing.json", 2, Card.class);
        opponent.getHand().add(flamewingCard);
        Tile summontile =  gs.getBoard().getTile(2,2);
        opponent.useCard(null, gs, 0, summontile, 0);
        Unit flamewing =  summontile.getUnit();
        flamewing.hasMoved = false;

        Tile playerAvatarTile = player.getAvatar().getCurrentTile();
        Tile opponentAvatarTile = opponent.getAvatar().getCurrentTile();

        Set<Tile> expectedMovement =  new HashSet<>();
        for (Tile[] row : gs.getBoard().getTiles())
            expectedMovement.addAll(Arrays.asList(row));

        expectedMovement.remove(gs.getBoard().getTile(2,2));
        expectedMovement.remove(playerAvatarTile);
        expectedMovement.remove(opponentAvatarTile);


        assertEquals(expectedMovement, BoardLogic.findValidMovement(summontile, flamewing, gs.getBoard()));
    }

    /// ////////  Units with provoke //////////
    @Test
    public void unitsProvokeIsTrueTest() {
        // Units with provoke: Rock Pulveriser, Swamp Entangler, Silverguard Knight, Ironcliffe Guardian
        Card rockCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/1_7_c_u_rock_pulveriser.json", 2, Card.class);
        Card swampCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_2_c_u_swamp_entangler.json", 2, Card.class);
        Card knightCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_3_c_u_silverguard_knight.json", 2, Card.class);
        Card guardianCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_8_c_u_ironcliff_guardian.json", 2, Card.class);

        opponent.getHand().add(rockCard);
        opponent.getHand().add(swampCard);
        opponent.getHand().add(knightCard);
        opponent.getHand().add(guardianCard);

        List<Unit> summonedUnits = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            opponent.useCard(null, gs, 0, gs.getBoard().getTile(i, 0), 0);
            summonedUnits.add(gs.getBoard().getTile(i, 0).getUnit());
        }
        assertTrue("Rock Pulveriser should have Provoke attribute as true.", summonedUnits.get(0).hasProvoke());
        assertTrue("Swamp Entangler should have Provoke attribute as true.", summonedUnits.get(0).hasProvoke());
        assertTrue("Silverguard Knight should have Provoke attribute as true.", summonedUnits.get(0).hasProvoke());
        assertTrue("Ironcliffe Guardian should have Provoke attribute as true.", summonedUnits.get(0).hasProvoke());
    }

    @Test
    public void provokePreventsMovementTest() {
        Card guardianCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_8_c_u_ironcliff_guardian.json", 2, Card.class);
        opponent.getHand().add(guardianCard);

        Tile summontile =  gs.getBoard().getTile(2,2);
        opponent.useCard(null, gs, 0, summontile, 0);

        Tile avatarTile = player.getAvatar().getCurrentTile();

        assertTrue("Avatar should not be able to move.", BoardLogic.findValidMovement(avatarTile, gs.getBoard().getTile(3,2).getUnit(), gs.getBoard()).isEmpty());
    }

    @Test
    public void provokeForcesAttackTest() {
        Card guardianCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_8_c_u_ironcliff_guardian.json", 2, Card.class);
        Card otherUnitCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json",  2, Card.class);
        opponent.getHand().add(guardianCard);
        opponent.getHand().add(otherUnitCard);
        opponent.getHand().add(otherUnitCard);

        Tile summontile =  gs.getBoard().getTile(2,2), dummyTile1 = gs.getBoard().getTile(4,2),  dummyTile2 = gs.getBoard().getTile(4,3);
        opponent.useCard(null, gs, 0, summontile, 0);
        opponent.useCard(null, gs, 0, dummyTile1, 0);
        opponent.useCard(null, gs, 0, dummyTile2, 0);

        Tile avatarTile = player.getAvatar().getCurrentTile();
        Set<Tile> expected = new HashSet<>();
        expected.add(summontile);

        assertEquals("Only Ironcliffe Guardian should be able to be attacked.", BoardLogic.findValidAttackUnits(avatarTile, avatarTile.getUnit(), gs.getBoard()), expected);
    }

    @Test
    public void provokeMultipleTargetsTest() {
        Card guardianCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_8_c_u_ironcliff_guardian.json", 2, Card.class);
        Card knightCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_3_c_u_silverguard_knight.json", 2, Card.class);
        Card otherUnitCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json",  2, Card.class);
        opponent.getHand().add(guardianCard);
        opponent.getHand().add(knightCard);
        opponent.getHand().add(otherUnitCard);
        opponent.getHand().add(otherUnitCard);

        Tile summontile =  gs.getBoard().getTile(2,2), dummyTile1 = gs.getBoard().getTile(4,2),  dummyTile2 = gs.getBoard().getTile(4,3);
        Tile otherTile = gs.getBoard().getTile(3,3);

        opponent.useCard(null, gs, 0, summontile, 0);
        opponent.useCard(null, gs, 0, otherTile, 0);
        opponent.useCard(null, gs, 0, dummyTile1, 0);
        opponent.useCard(null, gs, 0, dummyTile2, 0);

        Tile avatarTile = player.getAvatar().getCurrentTile();
        Set<Tile> expected = new HashSet<>();
        expected.add(summontile);
        expected.add(otherTile);

        assertEquals("Both units should be able to be attacked, but no other.", BoardLogic.findValidAttackUnits(avatarTile, avatarTile.getUnit(), gs.getBoard()), expected);
    }

    @Test
    public void alliesDontTriggerProvokeTest() {
        Card guardianCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_8_c_u_ironcliff_guardian.json", 2, Card.class);
        Card knightCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_3_c_u_silverguard_knight.json", 2, Card.class);
        Card otherUnitCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_4_c_u_saberspine_tiger.json",  2, Card.class);
        player.getHand().add(guardianCard);
        player.getHand().add(knightCard);
        opponent.getHand().add(otherUnitCard);
        opponent.getHand().add(otherUnitCard);

        Tile summontile =  gs.getBoard().getTile(2,2), dummyTile1 = gs.getBoard().getTile(4,2),  dummyTile2 = gs.getBoard().getTile(4,3);
        Tile otherTile = gs.getBoard().getTile(3,3);
        Tile avatarTile = player.getAvatar().getCurrentTile();

        Set<Tile> expectedMovement = Stream.of(
                        new int[][]{ {2,1}, {3,1}, {2,3}, {3,4}, {4,1}, {3,0}, {1,2} }
                ).map(coord -> gs.getBoard().getTile(coord[0], coord[1]))
                .collect(Collectors.toSet());

        Set<Tile> expectedAttack = new HashSet<>();
        expectedAttack.add(dummyTile1);
        expectedAttack.add(dummyTile2);

        player.useCard(null, gs, 0, summontile, 0);
        player.useCard(null, gs, 0, otherTile, 0);
        opponent.useCard(null, gs, 0, dummyTile1, 0);
        opponent.useCard(null, gs, 0, dummyTile2, 0);

        assertEquals("Allied units should not trigger Provoke.", expectedMovement, BoardLogic.findValidMovement(avatarTile, avatarTile.getUnit(), gs.getBoard()));
        assertEquals("Allied units should not trigger Provoke.", expectedAttack, BoardLogic.findValidAttackUnits(avatarTile, avatarTile.getUnit(), gs.getBoard()));
    }

    /// //////////////  Zeal /////////////////
    @Test
    public void silverguardKnightZealIsTrueTest() {
        Card knightCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_3_c_u_silverguard_knight.json", 2, Card.class);
        player.getHand().add(knightCard);

        Tile summonTile = gs.getBoard().getTile(2, 2);
        player.useCard(null, gs, 0, summonTile, 0);
        Unit knight = summonTile.getUnit();

        assertTrue("Silverguard Knight should have Zeal attribute as true.", knight.hasZeal());
    }

    @Test
    public void allyAvatarHitTriggersZealTest() {
        Card knightCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_3_c_u_silverguard_knight.json", 2, Card.class);
        player.getHand().add(knightCard);

        Tile summonTile = gs.getBoard().getTile(2, 2);
        player.useCard(null, gs, 0, summonTile, 0);
        Unit knight = summonTile.getUnit();

        Unit avatar = player.getAvatar();
        avatar.takeDamage(null, gs, 1);

        assertEquals("Silverguard Knight Attack should raise by 2.", 3, knight.getAttack());
    }

    @Test
    public void enemyAvatarHitDoesNotTriggerZealTest() {
        Card knightCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_3_c_u_silverguard_knight.json", 2, Card.class);
        player.getHand().add(knightCard);

        Tile summonTile = gs.getBoard().getTile(2, 2);
        player.useCard(null, gs, 0, summonTile, 0);
        Unit knight = summonTile.getUnit();

        Unit avatar = opponent.getAvatar();
        avatar.takeDamage(null, 1);

        assertEquals("Silverguard Attack HP should not raise.", 1, knight.getAttack());
    }

    @Test
    public void zealStacksTest() {
        Card knightCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_3_c_u_silverguard_knight.json", 2, Card.class);
        player.getHand().add(knightCard);

        Tile summonTile = gs.getBoard().getTile(2, 2);
        player.useCard(null, gs, 0, summonTile, 0);
        Unit knight = summonTile.getUnit();

        Unit avatar = player.getAvatar();
        avatar.takeDamage(null, gs,1);
        avatar.takeDamage(null, gs,1);
        avatar.takeDamage(null, gs,1);
        avatar.takeDamage(null, gs,1);

        assertEquals("Silverguard Knight HP should raise by 9, 2 per hit.", 9, knight.getAttack());
    }

    @Test
    public void healingDoesNotTriggerZealTest() {
        Card knightCard = BasicObjectBuilders.loadCard("conf/gameconfs/cards/2_3_c_u_silverguard_knight.json", 2, Card.class);
        player.getHand().add(knightCard);

        Tile summonTile = gs.getBoard().getTile(2, 2);
        player.useCard(null, gs, 0, summonTile, 0);
        Unit knight = summonTile.getUnit();

        Unit avatar = player.getAvatar();
        avatar.setHealth(null, 10);
        avatar.takeDamage(null, gs,-1);
        avatar.takeDamage(null, gs,-1);

        assertEquals("Silverguard Knight HP should not raise.", 1, knight.getAttack());

    }


}
