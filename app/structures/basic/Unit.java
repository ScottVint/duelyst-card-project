package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile.
 * <p>
 * Extended to include combat stats (health, maxHealth, attack).
 *
 * @author Dr. Richard McCreadie
 * @author Minghao
 *
 */
public class Unit {

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	int id;
	UnitAnimationType animation;
	Position position;
	UnitAnimationSet animations;
	ImageCorrection correction;
	/** The player who owns this unit. Not serialised to JSON. @author Minghao */
	@JsonIgnore
	Player owner;
	/** Current hit points of this unit. @author Minghao */
	int health;
	/** Maximum hit points of this unit. @author Minghao */
	int maxHealth;
	/** Attack damage this unit deals per strike. @author Minghao */
	int attack;
	
	public Unit() {}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;
	}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(currentTile.getXpos(),currentTile.getYpos(),currentTile.getTilex(),currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
	}
	
	
	
	public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UnitAnimationType getAnimation() {
		return animation;
	}
	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	public ImageCorrection getCorrection() {
		return correction;
	}

	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public UnitAnimationSet getAnimations() {
		return animations;
	}

	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}
	
	/**
	 * Returns the player who owns this unit.
	 * @author Minghao
	 */
	public Player getOwner() {
		return owner;
	}
	/**
	 * Sets the owning player of this unit.
	 * @param owner the player to assign as owner
	 * @author Minghao
	 */
	public void setOwner(Player owner) {
		this.owner = owner;
	}

	/**
	 * Returns the current health of the unit.
	 * @author Minghao
	 */
	public int getHealth() {
		return health;
	}
	/**
	 * Sets the current health of the unit.
	 * @param health new health value
	 * @author Minghao
	 */
	public void setHealth(int health) {
		this.health = health;
	}
	/**
	 * Returns the maximum health of the unit.
	 * @author Minghao
	 */
	public int getMaxHealth() {
		return maxHealth;
	}
	/**
	 * Sets the maximum health of the unit.
	 * @param maxHealth new maximum health value
	 * @author Minghao
	 */
	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}
	/**
	 * Returns the attack damage of the unit.
	 * @author Minghao
	 */
	public int getAttack() {
		return attack;
	}
	/**
	 * Sets the attack damage of the unit.
	 * @param attack new attack value
	 * @author Minghao
	 */
	public void setAttack(int attack) {
		this.attack = attack;
	}

	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
	}


}
