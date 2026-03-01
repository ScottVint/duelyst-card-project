package events;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

public class TileClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        int tilex = message.get("tilex").asInt();
        int tiley = message.get("tiley").asInt();

        if (!gameState.gameInitalised) return;
        if (!gameState.inBounds(tilex, tiley)) return;
        if (gameState.isAnimating) return;

        Integer clickedUnitId = gameState.unitIdAt(tilex, tiley);

        // ---------- 1) Select: only allow selecting P1 avatar (minimal for your Trello tasks) ----------
        if (gameState.selectedUnit == null) {
            if (clickedUnitId != null && clickedUnitId.equals(gameState.p1AvatarId)) {
                gameState.selectedUnit = gameState.unitsById.get(clickedUnitId);

                // compute ranges
                gameState.clearHighlights();
                gameState.validMoveTiles = computeMoveTiles(gameState, tilex, tiley);
                gameState.validAttackTiles = computeAttackTiles(gameState, tilex, tiley);

                // highlight move tiles (mode 1) and attack tiles (mode 2 = red in most templates)
                for (String k : gameState.validMoveTiles) {
                    Tile t = tileFromKey(gameState, k);
                    if (t != null) BasicCommands.drawTile(out, t, 1);
                }
                for (String k : gameState.validAttackTiles) {
                    Tile t = tileFromKey(gameState, k);
                    if (t != null) BasicCommands.drawTile(out, t, 2);
                }
            }
            return;
        }

        // ---------- 2) Move: click empty tile in range ----------
        if (clickedUnitId == null) {
            if (gameState.validMoveTiles.contains(GameState.key(tilex, tiley))) {
                Unit u = gameState.selectedUnit;
                Tile target = gameState.board[tilex][tiley];

                // update occupancy map (from old position)
                int fromX = u.getPosition().getTilex();
                int fromY = u.getPosition().getTiley();
                gameState.occupiedByUnitId.remove(GameState.key(fromX, fromY));
                gameState.occupiedByUnitId.put(GameState.key(tilex, tiley), u.getId());

                // start move animation
                gameState.isAnimating = true;
                BasicCommands.moveUnitToTile(out, u, target);
            }
            return;
        }

        // ---------- 3) Attack: click enemy avatar tile in range ----------
        if (clickedUnitId.equals(gameState.p2AvatarId)) {
            if (gameState.validAttackTiles.contains(GameState.key(tilex, tiley))) {
                BasicCommands.playUnitAnimation(out, gameState.selectedUnit, UnitAnimationType.attack);
            }
            return;
        }
    }

    private Tile tileFromKey(GameState gs, String key) {
        int comma = key.indexOf(",");
        int x = Integer.parseInt(key.substring(0, comma));
        int y = Integer.parseInt(key.substring(comma + 1));
        if (!gs.inBounds(x, y)) return null;
        return gs.board[x][y];
    }

    // Movement rule for MVP: 2 cardinal OR 1 diagonal, only empty tiles
    private Set<String> computeMoveTiles(GameState gs, int fromX, int fromY) {
        Set<String> out = new HashSet<>();

        int[][] deltas = new int[][]{
            { 1, 0 }, { 2, 0 }, { -1, 0 }, { -2, 0 },
            { 0, 1 }, { 0, 2 }, { 0,-1 }, { 0,-2 },
            { 1, 1 }, { 1,-1 }, { -1, 1 }, { -1,-1 }
        };

        for (int[] d : deltas) {
            int x = fromX + d[0];
            int y = fromY + d[1];
            if (!gs.inBounds(x, y)) continue;
            if (!gs.isEmpty(x, y)) continue;
            out.add(GameState.key(x, y));
        }
        return out;
    }

    // Attack rule for MVP: adjacent 8 tiles, only those containing enemy (here: P2 avatar)
    private Set<String> computeAttackTiles(GameState gs, int fromX, int fromY) {
        Set<String> out = new HashSet<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int x = fromX + dx;
                int y = fromY + dy;
                if (!gs.inBounds(x, y)) continue;
                Integer uid = gs.unitIdAt(x, y);
                if (uid != null && uid.equals(gs.p2AvatarId)) {
                    out.add(GameState.key(x, y));
                }
            }
        }
        return out;
    }
}