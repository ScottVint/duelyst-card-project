package structures;

import java.util.HashSet;
import java.util.Set;

/**
 * Holds state for an on-going game.
 */
public class GameState {

    public boolean gameInitalised = false;

    // Selection state
    public boolean hasSelectedUnit = false;
    public int selectedUnitX = -1; // tile indices start at 1
    public int selectedUnitY = -1;

    // Highlight caches
    public Set<String> validMoveTiles = new HashSet<>();
    public Set<String> validAttackTiles = new HashSet<>();

    // Prevent input during animations
    public boolean isAnimating = false;

    // Utility: key for sets
    public static String key(int x, int y) {
        return x + "," + y;
    }

    public void clearSelectionAndHighlights() {
        hasSelectedUnit = false;
        selectedUnitX = -1;
        selectedUnitY = -1;
        validMoveTiles.clear();
        validAttackTiles.clear();
    }
}