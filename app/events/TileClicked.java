package events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class TileClicked implements EventProcessor {

  @Override
  public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
    if (!gameState.gameInitialised) return;
    if (!gameState.isPlayer1Turn) return;
    if (gameState.aiTurnInProgress) return;

    int tilex = message.get("tilex").asInt();
    int tiley = message.get("tiley").asInt();

    if (gameState.mode == GameState.Mode.NONE) return;

    String k = GameState.key(tilex, tiley);
    if (!gameState.validTargets.contains(k)) return;

    int handPos = gameState.selectedHandPosition;
    Card selected = safeGetCard(gameState, handPos);
    if (selected == null) {
      clearHighlightsAndSelection(out, gameState);
      return;
    }

    int mana = gameState.getPlayer1().getMana();
    int cost = selected.getManacost();
    if (mana < cost) {
      BasicCommands.addPlayer1Notification(out, "Not enough mana!", 2);
      clearHighlightsAndSelection(out, gameState);
      return;
    }

    if (gameState.mode == GameState.Mode.SPELL) {
      handleSpell(out, gameState, selected, handPos, tilex, tiley);
      clearHighlightsAndSelection(out, gameState);
      return;
    }

    if (gameState.mode == GameState.Mode.SUMMON) {
      handleSummon(out, gameState, selected, handPos, tilex, tiley);
      clearHighlightsAndSelection(out, gameState);
    }
  }

  private void handleSpell(ActorRef out, GameState gs, Card spell, int handPos, int tilex, int tiley) {
    Tile targetTile = gs.getBoard().getTile(tilex, tiley);
    if (targetTile == null) return;

    int mana = gs.getPlayer1().getMana();
    int cost = spell.getManacost();

    gs.getPlayer1().setMana(mana - cost);
    BasicCommands.setPlayer1Mana(out, gs.getPlayer1());

    // Minimal demo effect (replace to your spell config if you have multiple spells)
    EffectAnimation effect = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
	  BasicCommands.playEffectAnimation(out, effect, targetTile);

    gs.getPlayer1().getHand().removeCardAt(handPos);
    BasicCommands.deleteCard(out, handPos);
  }

  private void handleSummon(ActorRef out, GameState gs, Card unitCard, int handPos, int tilex, int tiley) {
    Tile summonTile = gs.getBoard().getTile(tilex, tiley);
    if (summonTile == null) return;

    int mana = gs.getPlayer1().getMana();
    int cost = unitCard.getManacost();

    gs.getPlayer1().setMana(mana - cost);
    BasicCommands.setPlayer1Mana(out, gs.getPlayer1());

    Unit unit = BasicObjectBuilders.loadUnit(unitCard.getUnitConfig(), 0, Unit.class);
    unit.setPositionByTile(summonTile);

    gs.getBoard().placeUnit(unit, tilex, tiley);
    BasicCommands.drawUnit(out, unit, summonTile);

    gs.getPlayer1().getHand().removeCardAt(handPos);
    BasicCommands.deleteCard(out, handPos);

    // For Story #7: summoned unit cannot act on the same turn
    gs.getBoard().markUnitSummonedThisTurn(unit.getId());
  }

  private Card safeGetCard(GameState gs, int handPos) {
    if (gs.getPlayer1() == null) return null;
    if (gs.getPlayer1().getHand() == null) return null;
    return gs.getPlayer1().getHand().getCardAt(handPos);
  }

  private void clearHighlightsAndSelection(ActorRef out, GameState gs) {
    for (int[] xy : gs.highlightedTiles) {
      Tile t = gs.getBoard() == null ? null : gs.getBoard().getTile(xy[0], xy[1]);
      if (t != null) BasicCommands.drawTile(out, t, 0);
    }

    if (gs.selectedHandPosition >= 1 && gs.selectedHandPosition <= 6) {
      Card c = safeGetCard(gs, gs.selectedHandPosition);
      if (c != null) BasicCommands.drawCard(out, c, gs.selectedHandPosition, 0);
    }

    gs.resetSelection();
  }
}