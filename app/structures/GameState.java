package structures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import structures.basic.Tile;
import structures.basic.Unit;

public class GameState {

    public boolean gameInitalised = false;

    // Selected unit
    public Unit selectedUnit = null;

    // Tiles and units captured from CommandDemo
    public Tile[][] board = new Tile[10][6]; // use indices 1..9 and 1..5
    public Map<Integer, Unit> unitsById = new HashMap<>();
    public Map<String, Integer> occupiedByUnitId = new HashMap<>(); // "x,y" -> unitId

    // For demo: identify player avatars by spawn tiles
    public Integer p1AvatarId = null; // unit on (2,3)
    public Integer p2AvatarId = null; // unit on (8,3)

    // Highlight caches
    public Set<String> validMoveTiles = new HashSet<>();
    public Set<String> validAttackTiles = new HashSet<>();

    // Animation lock
    public boolean isAnimating = false;

    public static String key(int x, int y) { return x + "," + y; }

    public boolean inBounds(int x, int y) {
        return x >= 1 && x <= 9 && y >= 1 && y <= 5;
    }

    public Integer unitIdAt(int x, int y) {
        return occupiedByUnitId.get(key(x, y));
    }

    public boolean isEmpty(int x, int y) {
        return unitIdAt(x, y) == null;
    }

    public void clearHighlights() {
        validMoveTiles.clear();
        validAttackTiles.clear();
    }

    public void clearSelection() {
        selectedUnit = null;
    }
}