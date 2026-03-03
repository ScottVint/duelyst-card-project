package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * Intercepts BasicCommands messages while still forwarding them to the UI.
 * Used to capture Tile/Unit objects created by CommandDemo into GameState.
 */
public class TrackingTell implements DummyTell {

    private final ActorRef out;
    private final GameState gameState;
    private final ObjectMapper mapper = new ObjectMapper();

    public TrackingTell(ActorRef out, GameState gameState) {
        this.out = out;
        this.gameState = gameState;
    }

    @Override
    public void tell(ObjectNode msg) {
        // Forward to UI
        out.tell(msg, out);

        try {
            String type = msg.get("messagetype").asText();

            if ("drawTile".equals(type)) {
                JsonNode tileNode = msg.get("tile");
                Tile tile = mapper.treeToValue(tileNode, Tile.class);
                int x = tile.getTilex();
                int y = tile.getTiley();
//                if (gameState.inBounds(x, y)) {
//                    gameState.board[x][y] = tile;
//                }
            }

            if ("drawUnit".equals(type)) {
                JsonNode tileNode = msg.get("tile");
                JsonNode unitNode = msg.get("unit");
                Tile tile = mapper.treeToValue(tileNode, Tile.class);
                Unit unit = mapper.treeToValue(unitNode, Unit.class);

                int x = tile.getTilex();
                int y = tile.getTiley();

                gameState.unitsById.put(unit.getId(), unit);
                gameState.occupiedByUnitId.put(GameState.key(x, y), unit.getId());

                // Identify avatars by their demo spawn tiles
                if (x == 2 && y == 3) gameState.p1AvatarId = unit.getId();
                if (x == 8 && y == 3) gameState.p2AvatarId = unit.getId();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}