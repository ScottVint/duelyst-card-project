package structures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import structures.basic.Board;
import structures.basic.PlayerWithHand;

/**
 * Holds the authoritative state for a single running game session.
 * This object is created once by GameActor and mutated by EventProcessors.
 */
public class GameState {

  // Lifecycle
  public boolean gameInitialised = false;

  // Turn state (Stories #10, #19, #15/#11 integration)
  public int turnNumber = 1;               // starts at 1
  public boolean isPlayer1Turn = true;     // true = human player's turn
  public boolean aiTurnInProgress = false; // lock/guard during AI execution if needed

  // Mana caps (Story #19)
  public int maxManaP1 = 2;
  public int maxManaP2 = 2;

  // Core model references
  private Board board;
  private PlayerWithHand player1;
  private PlayerWithHand player2;

  // Interaction mode (Stories #7, #9)
  public enum Mode { NONE, SPELL, SUMMON }
  public Mode mode = Mode.NONE;

  // Selection state (Stories #7, #9)
  public int selectedHandPosition = -1;  // 1..6
  public Integer selectedCardId = null;

  // Highlight/targeting state (Stories #7, #9)
  // Store as "x,y" keys for quick membership checks
  public Set<String> validTargets = new HashSet<>();
  // Store concrete highlighted tile coordinates to clear later
  public List<int[]> highlightedTiles = new ArrayList<>();

  // ---------- Getters / Setters ----------
  public Board getBoard() {
    return board;
  }

  public void setBoard(Board board) {
    this.board = board;
  }

  public PlayerWithHand getPlayer1() {
    return player1;
  }

  public void setPlayer1(PlayerWithHand player1) {
    this.player1 = player1;
  }

  public PlayerWithHand getPlayer2() {
    return player2;
  }

  public void setPlayer2(PlayerWithHand player2) {
    this.player2 = player2;
  }

  // ---------- Helpers ----------
  public void resetSelection() {
    mode = Mode.NONE;
    selectedHandPosition = -1;
    selectedCardId = null;
    validTargets.clear();
    highlightedTiles.clear();
  }

  public static String key(int x, int y) {
    return x + "," + y;
  }

  // Compute per-turn max mana per Story #19 (start at 2, +1 per turn, cap at 9)
  public static int computeMaxManaForTurn(int turnNumber) {
    int m = 2 + Math.max(0, turnNumber - 1);
    return Math.min(9, m);
  }
}