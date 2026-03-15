package structures.basic.spells;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.players.*;

import java.util.Set;

public abstract class Spell {
    public Set<Tile> validTargets(Player player) {return null;}

    public void highlightTargets(ActorRef out, Player player) {}

    public void cast(ActorRef out, GameState gameState, Player player, Tile clickedTile, Board board, Card usedCard) {}
}
