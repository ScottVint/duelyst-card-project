import akka.actor.ActorRef;
import org.junit.Before;
import org.junit.Test;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.players.HumanPlayer;
import structures.basic.unittypes.BetterUnit;
import structures.basic.unittypes.SkyrockGolem;
import structures.basic.unittypes.Unit;
import structures.basic.unittypes.Wraithling;
import structures.logic.BoardLogic;
import utils.BasicObjectBuilders;

public class SpellTest {

    ActorRef out;
    GameState gs = new GameState();
    HumanPlayer player = new HumanPlayer();

    @Before
    public void setUp() {
        player.setAvatar(out, gs);
        player.getAvatar().setPositionByTile(gs.getBoard().getTile(3,2));
    }

    @Test
    public void TestHornOfTheForsaken() {
        BetterUnit avatar = player.getAvatar();
        avatar.setHornCharges(3);
        avatar.takeDamage(out, gs, player, 2);

        assert avatar.getHornCharges() == 2;
        assert avatar.getHealth() == 18;

        boolean hasWraithling = false;
        for (Tile tile : BoardLogic.findAdjacentTiles(avatar.getTileOccupied(), gs.getBoard())) {
            if (tile.getUnit() != null && tile.getUnit() instanceof Wraithling) {
                hasWraithling = true;
                break;
            }
        }
        assert hasWraithling;
    }

    @Test
    public void TestHornNoValidSummon() {
        BetterUnit avatar = player.getAvatar();
        avatar.setHornCharges(3);
        Unit fillerUnit = BasicObjectBuilders.loadUnit("conf/gameconfs/units/skyrock_golem.json", 2, SkyrockGolem.class);

        avatar.takeDamage(out, gs, player, 2);


        boolean hasWraithling = false;
        for (Tile tile : BoardLogic.findAdjacentTiles(avatar.getTileOccupied(), gs.getBoard()))
            tile.setUnit(fillerUnit);
        for (Tile tile : BoardLogic.findAdjacentTiles(avatar.getTileOccupied(), gs.getBoard()))
            if (tile.getUnit() != null && tile.getUnit() instanceof Wraithling) {
                hasWraithling = true;
                break;
            }
        assert !hasWraithling;
    }
}
