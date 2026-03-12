package events;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.basic.BetterUnit;
import utils.BasicObjectBuilders;

/**
 * Indicates that the user has clicked a tile on the game canvas.
 *  * <p>
 *  * Handles Story Card #3: when the human player clicks one of their units, the unit is
 *  * selected and its valid movement range is highlighted in white.
 *  * <pre>
 *  * {
 *  *   messageType = "tileClicked"
 *  *   tilex = &lt;x index of the tile&gt;
 *  *   tiley = &lt;y index of the tile&gt;
 *  * }
 *  * </pre>
 *  *
 *  * @author Dr. Richard McCreadie
 *  * @author Minghao
 */
public class TileClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        if (!gameState.isPlayer1Turn()) {
            BasicCommands.addPlayer1Notification(out, "It is not your turn.", 2);
            return;
        }

        if (gameState.isUnitMoving()) {
            BasicCommands.addPlayer1Notification(out, "A unit is already moving.", 2);
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

            // If a friendly unit is already selected and player clicks an enemy -> try attack
            if (gameState.getSelectedUnit() != null && clickedUnit.getOwner() != gameState.getPlayer1()) {
                tryAttackSelectedUnit(out, gameState, clickedUnit);
                return;
            }

            // Select friendly unit for movement / attack
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
                    useNonCreatureCardOnEmptyTile(out, gameState, selectedCard, clickedTile, cardIndex);
                }
            }
            return;
        }

        // Empty tile + selected unit => try move
        if (gameState.getSelectedUnit() != null) {
            moveSelectedUnit(out, gameState, clickedTile);
        }
    }

    private void tryAttackSelectedUnit(ActorRef out, GameState gameState, Unit targetUnit) {
        Unit attacker = gameState.getSelectedUnit();
        if (attacker == null || targetUnit == null) return;

        if (attacker.getOwner() != gameState.getPlayer1()) {
            BasicCommands.addPlayer1Notification(out, "Only your unit can attack.", 2);
            return;
        }

        if (targetUnit.getOwner() == gameState.getPlayer1()) {
            BasicCommands.addPlayer1Notification(out, "Cannot attack friendly unit.", 2);
            return;
        }

        if (!isAdjacent(attacker, targetUnit)) {
            BasicCommands.addPlayer1Notification(out, "Target is not adjacent.", 2);
            return;
        }

        gameState.getBoard().clearSelection(out);

        BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
        BasicCommands.playUnitAnimation(out, targetUnit, UnitAnimationType.hit);

        gameState.dealDamage(out, attacker, targetUnit);

        gameState.setSelectedUnit(null);
        gameState.setSelectedHandPosition(null);
    }

    private boolean isAdjacent(Unit a, Unit b) {
        int dx = Math.abs(a.getPosition().getTilex() - b.getPosition().getTilex());
        int dy = Math.abs(a.getPosition().getTiley() - b.getPosition().getTiley());
        return dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0);
    }

    private void moveSelectedUnit(ActorRef out, GameState gameState, Tile clickedTile) {
        Unit selectedUnit = gameState.getSelectedUnit();
        if (selectedUnit == null) return;

        if (clickedTile.getUnit() != null) {
            BasicCommands.addPlayer1Notification(out, "Tile occupied.", 2);
            return;
        }

        if (!isValidMoveTile(gameState, selectedUnit, clickedTile)) {
            BasicCommands.addPlayer1Notification(out, "Invalid move tile.", 2);
            return;
        }

        gameState.setMovingUnit(selectedUnit);
        gameState.setMoveTargetTile(clickedTile);
        gameState.setUnitMoving(true);

        gameState.getBoard().clearSelection(out);
        gameState.setSelectedUnit(null);
        gameState.setSelectedHandPosition(null);

        BasicCommands.moveUnitToTile(out, selectedUnit, clickedTile);
    }

    private boolean isValidMoveTile(GameState gameState, Unit selectedUnit, Tile clickedTile) {
        int sx = selectedUnit.getPosition().getTilex();
        int sy = selectedUnit.getPosition().getTiley();
        int tx = clickedTile.getTilex();
        int ty = clickedTile.getTiley();

        int dx = tx - sx;
        int dy = ty - sy;

        if (dx == 0 && dy == 0) return false;
        if (clickedTile.getUnit() != null) return false;

        // Diagonal: exactly 1
        if (Math.abs(dx) == 1 && Math.abs(dy) == 1) {
            return true;
        }

        // Horizontal: 1 or 2, no blocking
        if (dy == 0 && (Math.abs(dx) == 1 || Math.abs(dx) == 2)) {
            int step = dx > 0 ? 1 : -1;
            for (int i = 1; i <= Math.abs(dx); i++) {
                Tile pathTile = gameState.getBoard().getTile(sx + i * step, sy);
                if (i < Math.abs(dx) && pathTile.getUnit() != null) {
                    return false;
                }
            }
            return true;
        }

        // Vertical: 1 or 2, no blocking
        if (dx == 0 && (Math.abs(dy) == 1 || Math.abs(dy) == 2)) {
            int step = dy > 0 ? 1 : -1;
            for (int i = 1; i <= Math.abs(dy); i++) {
                Tile pathTile = gameState.getBoard().getTile(sx, sy + i * step);
                if (i < Math.abs(dy) && pathTile.getUnit() != null) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private void useNonCreatureCardOnEmptyTile(ActorRef out,
                                               GameState gameState,
                                               Card card,
                                               Tile clickedTile,
                                               int cardIndex) {

        String cardName = card.getCardname();

        if ("Wraithling Swarm".equals(cardName)) {
            castWraithlingSwarm(out, gameState, clickedTile, cardIndex);
            return;
        }

        if ("Horn of the Forsaken".equals(cardName)) {
            BasicCommands.addPlayer1Notification(out, "Horn must target your avatar", 2);
            return;
        }

        BasicCommands.addPlayer1Notification(out, "This card must target a unit.", 2);
    }

    private void useNonCreatureCardOnUnit(ActorRef out,
                                          GameState gameState,
                                          Card card,
                                          Unit targetUnit,
                                          int cardIndex) {

        String cardName = card.getCardname();

        if ("Horn of the Forsaken".equals(cardName)) {
            if (targetUnit != gameState.getPlayer1().getAvatar()) {
                BasicCommands.addPlayer1Notification(out, "Horn must target your avatar", 2);
                return;
            }

            if (gameState.getPlayer1().getMana() < card.getManacost()) {
                BasicCommands.addPlayer1Notification(out, "Not enough mana", 2);
                return;
            }

            gameState.equipPlayer1Horn();
            spendManaRemoveCardAndClear(out, gameState, cardIndex, card.getManacost());
            BasicCommands.addPlayer1Notification(out, "Horn equipped (3)", 2);
            return;
        }

        if ("Truestrike".equals(cardName)) {
            if (targetUnit.getOwner() != gameState.getPlayer2()) {
                BasicCommands.addPlayer1Notification(out, "Truestrike must target an enemy unit", 2);
                return;
            }

            if (gameState.getPlayer1().getMana() < card.getManacost()) {
                BasicCommands.addPlayer1Notification(out, "Not enough mana", 2);
                return;
            }

            gameState.dealDirectDamage(out, targetUnit, 2);
            spendManaRemoveCardAndClear(out, gameState, cardIndex, card.getManacost());
            BasicCommands.addPlayer1Notification(out, "Truestrike cast", 2);
            return;
        }

        if ("Sundrop Elixir".equals(cardName)) {
            if (gameState.getPlayer1().getMana() < card.getManacost()) {
                BasicCommands.addPlayer1Notification(out, "Not enough mana", 2);
                return;
            }

            gameState.healUnit(out, targetUnit, 5);
            spendManaRemoveCardAndClear(out, gameState, cardIndex, card.getManacost());
            BasicCommands.addPlayer1Notification(out, "Sundrop Elixir cast", 2);
            return;
        }

        if ("Dark Terminus".equals(cardName)) {
            if (targetUnit.getOwner() != gameState.getPlayer2()) {
                BasicCommands.addPlayer1Notification(out, "Dark Terminus must target an enemy unit", 2);
                return;
            }

            if (targetUnit == gameState.getPlayer2().getAvatar()) {
                BasicCommands.addPlayer1Notification(out, "Dark Terminus cannot target enemy avatar", 2);
                return;
            }

            if (gameState.getPlayer1().getMana() < card.getManacost()) {
                BasicCommands.addPlayer1Notification(out, "Not enough mana", 2);
                return;
            }

            Tile deathTile = gameState.getBoard().getTile(
                    targetUnit.getPosition().getTilex(),
                    targetUnit.getPosition().getTiley()
            );

            gameState.removeUnit(out, targetUnit);
            gameState.summonWraithling(out, deathTile, gameState.getPlayer1());

            spendManaRemoveCardAndClear(out, gameState, cardIndex, card.getManacost());
            BasicCommands.addPlayer1Notification(out, "Dark Terminus cast", 2);
            return;
        }

        BasicCommands.addPlayer1Notification(out, "Spell/artifact not implemented yet.", 2);
    }

    private void castWraithlingSwarm(ActorRef out,
                                     GameState gameState,
                                     Tile clickedTile,
                                     int cardIndex) {

        Card card = gameState.getPlayer1().getHand().get(cardIndex);

        if (gameState.getPlayer1().getMana() < card.getManacost()) {
            BasicCommands.addPlayer1Notification(out, "Not enough mana", 2);
            return;
        }

        int summoned = 0;
        Set<String> used = new HashSet<>();

        if (clickedTile != null && clickedTile.getUnit() == null && isValidSummonTile(gameState, clickedTile)) {
            gameState.summonWraithling(out, clickedTile, gameState.getPlayer1());
            used.add(clickedTile.getTilex() + "," + clickedTile.getTiley());
            summoned++;
        }

        List<int[]> friendlyPositions = new ArrayList<>();
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 5; y++) {
                Tile tile = gameState.getBoard().getTile(x, y);
                Unit unit = tile.getUnit();
                if (unit != null && unit.getOwner() == gameState.getPlayer1()) {
                    friendlyPositions.add(new int[]{x, y});
                }
            }
        }

        for (int[] pos : friendlyPositions) {
            if (summoned >= 3) break;

            int x = pos[0];
            int y = pos[1];

            for (int dx = -1; dx <= 1; dx++) {
                if (summoned >= 3) break;

                for (int dy = -1; dy <= 1; dy++) {
                    if (summoned >= 3) break;
                    if (dx == 0 && dy == 0) continue;

                    int nx = x + dx;
                    int ny = y + dy;

                    if (nx < 0 || nx >= 9 || ny < 0 || ny >= 5) continue;

                    Tile candidate = gameState.getBoard().getTile(nx, ny);
                    String key = nx + "," + ny;

                    if (used.contains(key)) continue;
                    if (candidate.getUnit() != null) continue;

                    gameState.summonWraithling(out, candidate, gameState.getPlayer1());
                    used.add(key);
                    summoned++;
                }
            }
        }

        if (summoned == 0) {
            BasicCommands.addPlayer1Notification(out, "No valid space for Wraithling Swarm", 2);
            return;
        }

        spendManaRemoveCardAndClear(out, gameState, cardIndex, card.getManacost());
        BasicCommands.addPlayer1Notification(out, "Wraithling Swarm cast", 2);
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
                BetterUnit.class
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

        spendManaRemoveCardAndClear(out, gameState, cardIndex, card.getManacost());
    }

    private void spendManaRemoveCardAndClear(ActorRef out,
                                             GameState gameState,
                                             int cardIndex,
                                             int manaCost) {

        int newMana = gameState.getPlayer1().getMana() - manaCost;
        gameState.getPlayer1().setMana(out, newMana);

        List<Card> hand = gameState.getPlayer1().getHand();
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