package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;

import java.util.ArrayList;
import java.util.List;

public class CardClicked implements EventProcessor {

  @Override
  public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
    if (!gameState.gameInitialised) return;
    if (!gameState.isPlayer1Turn) return;
    if (gameState.aiTurnInProgress) return;

    int handPos = message.get("position").asInt(); // 1..6

    Card clicked = safeGetCard(gameState, handPos);
    if (clicked == null) return;

    // Clear any previous selection/highlights first
    clearHighlightsAndSelection(out, gameState);

    int mana = gameState.getPlayer1().getMana();
    int cost = clicked.getManacost();

    if (mana < cost) {
      BasicCommands.addPlayer1Notification(out, "Not enough mana!", 2);
      return;
    }

    // Save selection state
    gameState.selectedHandPosition = handPos;
    gameState.selectedCardId = clicked.getId();

    // Keep the selected card highlighted
    BasicCommands.drawCard(out, clicked, handPos, 1);

    if (isCreatureCard(clicked)) {
      gameState.mode = GameState.Mode.SUMMON;

      List<Tile> summonTiles = computeValidSummonTiles(gameState);
      if (summonTiles.isEmpty()) {
        BasicCommands.addPlayer1Notification(out, "No valid summon tiles.", 2);
        // Unhighlight card + reset state
        BasicCommands.drawCard(out, clicked, handPos, 0);
        gameState.resetSelection();
        return;
      }

      highlightTiles(out, gameState, summonTiles);
      return;
    }

    // Treat as a spell card (your template cards usually encode spells by name, but adjust if you have a better flag)
    gameState.mode = GameState.Mode.SPELL;

    List<Tile> targets = computeValidSpellTargets(gameState, clicked);
    if (targets.isEmpty()) {
      BasicCommands.addPlayer1Notification(out, "No valid targets.", 2);
      BasicCommands.drawCard(out, clicked, handPos, 0);
      gameState.resetSelection();
      return;
    }

    highlightTiles(out, gameState, targets);
  }

  private Card safeGetCard(GameState gs, int handPos) {
    if (gs.getPlayer1() == null) return null;
    if (gs.getPlayer1().getHand() == null) return null;
    return gs.getPlayer1().getHand().getCardAt(handPos);
  }

  private boolean isCreatureCard(Card c) {
    // Card class in template usually has getIsCreature()
    return c.getIsCreature();
  }

  private List<Tile> computeValidSummonTiles(GameState gs) {
    List<Tile> result = new ArrayList<>();
    if (gs.getBoard() == null) return result;

    // Minimal rule: allow summon onto any empty tile.
    // If you later implement adjacency rules, replace this with your rule.
    for (int x = 1; x <= 9; x++) {
      for (int y = 1; y <= 5; y++) {
        if (!gs.getBoard().isOccupied(x, y)) {
          Tile t = gs.getBoard().getTile(x, y);
          if (t != null) result.add(t);
        }
      }
    }
    return result;
  }

  private List<Tile> computeValidSpellTargets(GameState gs, Card spell) {
    if (gs.getBoard() == null) return new ArrayList<>();

    // Common helper in your earlier Board screenshots
    // If your Board doesn't have this, replace with your own scanning logic.
    return gs.getBoard().getEnemyUnitTiles();
  }

  private void highlightTiles(ActorRef out, GameState gs, List<Tile> tiles) {
    for (Tile t : tiles) {
      BasicCommands.drawTile(out, t, 1);
      gs.validTargets.add(GameState.key(t.getTilex(), t.getTiley()));
      gs.highlightedTiles.add(new int[]{t.getTilex(), t.getTiley()});
    }
  }

  private void clearHighlightsAndSelection(ActorRef out, GameState gs) {
    // Clear tile highlights
    for (int[] xy : gs.highlightedTiles) {
      Tile t = gs.getBoard() == null ? null : gs.getBoard().getTile(xy[0], xy[1]);
      if (t != null) BasicCommands.drawTile(out, t, 0);
    }

    // Unhighlight previously selected card
    if (gs.selectedHandPosition >= 1 && gs.selectedHandPosition <= 6) {
      Card c = safeGetCard(gs, gs.selectedHandPosition);
      if (c != null) BasicCommands.drawCard(out, c, gs.selectedHandPosition, 0);
    }

    gs.resetSelection();
  }
}