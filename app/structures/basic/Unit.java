package structures.basic;

import akka.actor.ActorRef;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import commands.BasicCommands;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile. 
 * 
 * @author Dr. Richard McCreadie
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
	int maxHealth;
	int health;
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

	/**
	 * Constructs a unit from a card. Sure hope this works!
	 * @param id
	 * @param animations
	 * @param correction
	 * @param unitDetails
	 * @author Scott
	 */
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, BigCard unitDetails) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;

		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;

		this.maxHealth = unitDetails.getHealth();
		this.attack = unitDetails.getAttack();
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

	// Extended getter/setters

	public int getHealth() {
		return health;
	}

	/** Sets health. Ensures health does not go above maximum.
	 * @param value
	 * @author Scott
	 */
	public void setHealth (int value) {
		if(value < 0) {
			health = 0;
		} else if (value > maxHealth) {
			health = maxHealth;
		} else {
			health = value;
		}
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

	/**
	 * This command automatically calls setUnitHealth to the
	 * new health value based on the damage taken, and
	 * displays it to the UI.
	 * @param out
	 * @param damage
	 * @author Scott
	 */
	@JsonIgnore
	public void takeDamage(ActorRef out, int damage) {
		setHealth(health-damage);
		BasicCommands.setUnitHealth(out, this, health);
	}
}
