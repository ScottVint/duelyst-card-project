package structures.basic;
import structures.basic.players.*;

import java.util.Set;

public interface Spell {
    public void cast(Player player, Card selectedCard);

    public Set<Tile> validTargets(Player player);
}
