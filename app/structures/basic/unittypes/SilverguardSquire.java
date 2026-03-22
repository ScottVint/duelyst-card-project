package structures.basic.unittypes;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Tile;
import structures.logic.BoardLogic;

import java.util.HashSet;
import java.util.Set;

public class SilverguardSquire extends Unit {
    public void openingGambit(ActorRef out, GameState gameState) {
        BetterUnit avatar = this.owner.getAvatar();
        int avatarx = avatar.getPosition().getTilex(), avatary = avatar.getPosition().getTiley();
        int[] tileFrontxy = {avatarx - 1, avatary}, tileBackxy = {avatarx + 1, avatary};
        Set<Tile> validTargets = new HashSet<>(BoardLogic.findAdjacentTiles(avatar.tileOccupied, gameState.getBoard()));
        validTargets.removeIf(tile -> tile.getUnit() == null || tile.getUnit().getOwner() != this.owner ||
                tile != gameState.getBoard().getTile(tileFrontxy[0], tileFrontxy[1]) ||
                tile != gameState.getBoard().getTile(tileBackxy[0], tileBackxy[1])
        );

        for (Tile tile : validTargets) {
            Unit unit = tile.getUnit();
            unit.setMaxHealth(unit.getMaxHealth() + 1);
            unit.setHealth(out, unit.getHealth() + 1);
            unit.setAttack(out, unit.getAttack() + 1);
        }

    }
}
