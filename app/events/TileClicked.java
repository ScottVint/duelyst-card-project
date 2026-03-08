package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

/**
 * Indicates that the user has clicked a tile on the game canvas.
 */
public class TileClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        if (!gameState.isPlayer1Turn()) {
            BasicCommands.addPlayer1Notification(out, "It is not your turn.", 2);
            return;
        }

        int tilex = message.get("tilex").asInt();
        int tiley = message.get("tiley").asInt();

        Tile clickedTile = gameState.getBoard().getTile(tilex, tiley);

        // Clicked on a unit
        if (clickedTile.getUnit() != null) {
            Unit clickedUnit = clickedTile.getUnit();

            // If a non-creature card is selected, try using it on this unit first
            if (gameState.getSelectedHandPosition() != null) {
                int handPosition = gameState.getSelectedHandPosition();
                List<Card> hand = gameState.getPlayer1().getHand();

                int cardIndex = handPosition - 1;
                if (cardIndex >= 0 && cardIndex < hand.size()) {
                    Card selectedCard = hand.get(cardIndex);

                    if (!selectedCard.isCreature()) {
                        useNonCreatureCardOnUnit(out, gameState, selectedCard, clickedUnit, cardIndex);
                        return;
                    }
                }
            }

            if (clickedUnit.getOwner() == gameState.getPlayer1()) {
                gameState.getBoard().clearSelection(out);
                gameState.setSelectedHandPosition(null);
                gameState.setSelectedUnit(clickedUnit);
                BasicCommands.drawTile(out, clickedTile, 1);
                gameState.getBoard().highlightMovement(out, clickedTile);
            } else {
                BasicCommands.addPlayer1Notification(out, "Clicked enemy unit", 2);
            }
            return;
        }

        // Empty tile + selected card
        if (gameState.getSelectedHandPosition() != null) {
            int handPosition = gameState.getSelectedHandPosition();
            List<Card> hand = gameState.getPlayer1().getHand();
            int cardIndex = handPosition - 1;

            if (cardIndex >= 0 && cardIndex < hand.size()) {
                Card selectedCard = hand.get(cardIndex);

                if (selectedCard.isCreature()) {
                    summonSelectedUnit(out, gameState, clickedTile);
                } else {
                    BasicCommands.addPlayer1Notification(out, "This card must target a unit.", 2);
                }
            }
            return;
        }
    }

    private void useNonCreatureCardOnUnit(ActorRef out,
                                          GameState gameState,
                                          Card card,
                                          Unit targetUnit,
                                          int cardIndex) {

        if ("Horn of the Forsaken".equals(card.getCardname())) {

            if (targetUnit != gameState.getPlayer1().getAvatar()) {
                BasicCommands.addPlayer1Notification(out, "Horn must target your avatar", 2);
                return;
            }

            if (gameState.getPlayer1().getMana() < card.getManacost()) {
                BasicCommands.addPlayer1Notification(out, "Not enough mana", 2);
                return;
            }

            gameState.equipPlayer1Horn();

            gameState.getPlayer1().setMana(gameState.getPlayer1().getMana() - card.getManacost());
            BasicCommands.setPlayer1Mana(out, gameState.getPlayer1());

            List<Card> hand = gameState.getPlayer1().getHand();
            hand.remove(cardIndex);
            redrawPlayerHand(out, hand);

            gameState.clearSelections(out);

            BasicCommands.addPlayer1Notification(out, "Horn equipped (3)", 2);
            return;
        }

        BasicCommands.addPlayer1Notification(out, "Spell/artifact not implemented yet.", 2);
    }

    private void summonSelectedUnit(ActorRef out, GameState gameState, Tile clickedTile) {
        int handPosition = gameState.getSelectedHandPosition();
        List<Card> hand = gameState.getPlayer1().getHand();

        int cardIndex = handPosition - 1;
        if (cardIndex < 0 || cardIndex >= hand.size()) return;

        Card card = hand.get(cardIndex);

        if (!card.isCreature()) return;
        if (clickedTile.getUnit() != null) return;

        if (!isValidSummonTile(gameState, clickedTile)) {
            BasicCommands.addPlayer1Notification(out, "Invalid summon tile.", 2);
            return;
        }

        if (gameState.getPlayer1().getMana() < card.getManacost()) {
            BasicCommands.addPlayer1Notification(out, "Not enough mana.", 2);
            return;
        }

        Unit summonedUnit = BasicObjectBuilders.loadUnit(
                card.getUnitConfig(),
                gameState.getNextUnitId(),
                Unit.class
        );

        summonedUnit.setOwner(gameState.getPlayer1());
        summonedUnit.setAttack(card.getBigCard().getAttack());
        summonedUnit.setMaxHealth(card.getBigCard().getHealth());
        summonedUnit.setHealth(card.getBigCard().getHealth());
        summonedUnit.setPositionByTile(clickedTile);
        clickedTile.setUnit(summonedUnit);

        BasicCommands.drawUnit(out, summonedUnit, clickedTile);
        BasicCommands.setUnitAttack(out, summonedUnit, summonedUnit.getAttack());
        BasicCommands.setUnitHealth(out, summonedUnit, summonedUnit.getHealth());

        gameState.getPlayer1().setMana(gameState.getPlayer1().getMana() - card.getManacost());
        BasicCommands.setPlayer1Mana(out, gameState.getPlayer1());

        hand.remove(cardIndex);
        redrawPlayerHand(out, hand);

        gameState.clearSelections(out);
    }

    private boolean isValidSummonTile(GameState gameState, Tile clickedTile) {
        int x = clickedTile.getTilex();
        int y = clickedTile.getTiley();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                int nx = x + dx;
                int ny = y + dy;

                if (nx < 0 || nx >= 9 || ny < 0 || ny >= 5) continue;

                Tile neighbour = gameState.getBoard().getTile(nx, ny);
                Unit unit = neighbour.getUnit();

                if (unit != null && unit.getOwner() == gameState.getPlayer1()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void redrawPlayerHand(ActorRef out, List<Card> hand) {
        for (int i = 1; i <= 6; i++) {
            BasicCommands.deleteCard(out, i);
        }

        for (int i = 0; i < hand.size() && i < 6; i++) {
            BasicCommands.drawCard(out, hand.get(i), i + 1, 0);
        }
    }
}