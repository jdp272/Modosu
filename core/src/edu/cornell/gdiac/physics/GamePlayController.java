/*
 * RocketWorldController.java
 *
 * This is one of the files that you are expected to modify. Please limit changes to 
 * the regions that say INSERT CODE HERE.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.physics.InputController;
import edu.cornell.gdiac.physics.WorldController;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.obstacle.ObstacleSelector;
import edu.cornell.gdiac.physics.obstacle.PolygonObstacle;
import edu.cornell.gdiac.physics.robot.RobotController;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.RandomController;
import edu.cornell.gdiac.util.SoundController;

/**
 * Gameplay specific controller for the rocket lander game.
 *
 * You will notice that asset loading is not done with static methods this time.  
 * Instance asset loading makes it easier to process our game modes in a loop, which 
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class GamePlayController extends WorldController {

	private RobotController robotController;

	private CollisionController collisionController;

	/** Reference to the rocket texture */
	private static final String ROCK_TEXTURE = "robot/robot.png";
	/** The reference for the afterburner textures  */
	private static final String MAIN_FIRE_TEXTURE = "robot/flames.png";
	private static final String RGHT_FIRE_TEXTURE = "robot/flames-right.png";
	private static final String LEFT_FIRE_TEXTURE = "robot/flames-left.png";

	private static final String SPIRIT_TEXTURE = "robot/spirit.png";
	/** Texture file for mouse crosshairs */
	private static final String CROSS_FILE = "ragdoll/crosshair.png";
	/** Reference to the crate image assets */
	private static final String CRATE_PREF = "robot/crate0";
	/** How many crate assets we have */
	private static final int MAX_CRATES = 2;

	/** The asset for the collision sound */
	private static final String  COLLISION_SOUND = "robot/bump.mp3";
	/** The asset for the main afterburner sound */
	private static final String  MAIN_FIRE_SOUND = "robot/afterburner.mp3";
	/** The asset for the right afterburner sound */
	private static final String  RGHT_FIRE_SOUND = "robot/sideburner-right.mp3";
	/** The asset for the left afterburner sound */
	private static final String  LEFT_FIRE_SOUND = "robot/sideburner-left.mp3";
	
	/** Texture assets for the rocket */
	private TextureRegion rocketTexture;
	/** Texture filmstrip for the main afterburner */
	private FilmStrip mainTexture;
	/** Texture filmstrip for the main afterburner */
	private FilmStrip leftTexture;
	/** Texture filmstrip for the main afterburner */
	private FilmStrip rghtTexture;

	private TextureRegion spiritTexture;

	
	/** Texture assets for the crates */
	private TextureRegion[] crateTextures = new TextureRegion[MAX_CRATES];
	/** Track asset loading from all instances and subclasses */
	private AssetState rocketAssetState = AssetState.EMPTY;
	/** Texture asset for mouse crosshairs */
	private TextureRegion crosshairTexture;
	
	/**
	 * Preloads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public void preLoadContent(AssetManager manager) {
		if (rocketAssetState != AssetState.EMPTY) {
			return;
		}
		
		rocketAssetState = AssetState.LOADING;
		for (int ii = 0; ii < crateTextures.length; ii++) {
			manager.load(CRATE_PREF + (ii + 1) +".png", Texture.class);
			assets.add(CRATE_PREF + (ii + 1) +".png");
		}
		
		// Ship textures
		manager.load(ROCK_TEXTURE, Texture.class);
		assets.add(ROCK_TEXTURE);
		manager.load(MAIN_FIRE_TEXTURE, Texture.class);
		assets.add(MAIN_FIRE_TEXTURE);
		manager.load(LEFT_FIRE_TEXTURE, Texture.class);
		assets.add(LEFT_FIRE_TEXTURE);
		manager.load(RGHT_FIRE_TEXTURE, Texture.class);
		assets.add(RGHT_FIRE_TEXTURE);
		manager.load(CROSS_FILE, Texture.class);
		assets.add(CROSS_FILE);
		manager.load(SPIRIT_TEXTURE, Texture.class);
		assets.add(SPIRIT_TEXTURE);
		
		// Ship sounds
		manager.load(MAIN_FIRE_SOUND, Sound.class);
		assets.add(MAIN_FIRE_SOUND);
		manager.load(LEFT_FIRE_SOUND, Sound.class);
		assets.add(LEFT_FIRE_SOUND);
		manager.load(RGHT_FIRE_SOUND, Sound.class);
		assets.add(RGHT_FIRE_SOUND);
		manager.load(COLLISION_SOUND, Sound.class);
		assets.add(COLLISION_SOUND);

		super.preLoadContent(manager);
	}

	/**
	 * Loads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public void loadContent(AssetManager manager) {
		if (rocketAssetState != AssetState.LOADING) {
			return;
		}
		
		for (int ii = 0; ii < crateTextures.length; ii++) {
			String filename = CRATE_PREF + (ii + 1) +".png";
			crateTextures[ii] = createTexture(manager,filename,false);
		}
		
		rocketTexture = createTexture(manager,ROCK_TEXTURE,false);
		mainTexture  = createFilmStrip(manager,MAIN_FIRE_TEXTURE,1, RobotModel.FIRE_FRAMES, RobotModel.FIRE_FRAMES);
		leftTexture  = createFilmStrip(manager,LEFT_FIRE_TEXTURE,1, RobotModel.FIRE_FRAMES, RobotModel.FIRE_FRAMES);
		rghtTexture  = createFilmStrip(manager,RGHT_FIRE_TEXTURE,1, RobotModel.FIRE_FRAMES, RobotModel.FIRE_FRAMES);
		crosshairTexture = createTexture(manager,CROSS_FILE, false);
		spiritTexture = createTexture(manager, SPIRIT_TEXTURE, false);

		SoundController sounds = SoundController.getInstance();
		sounds.allocate(manager,MAIN_FIRE_SOUND);
		sounds.allocate(manager,LEFT_FIRE_SOUND);
		sounds.allocate(manager,RGHT_FIRE_SOUND);
		sounds.allocate(manager,COLLISION_SOUND);

		
		super.loadContent(manager);
		rocketAssetState = AssetState.COMPLETE;
	}
	
	// Physics constants for initialization
	/** Density of non-crate objects */
	private static final float BASIC_DENSITY   = 0.0f;
	/** Density of the crate objects */
	private static final float CRATE_DENSITY   = 1.0f;
	/** Friction of non-crate objects */
	private static final float BASIC_FRICTION  = 0.1f;
	/** Friction of the crate objects */
	private static final float CRATE_FRICTION  = 0.3f;
	/** Collision restitution for all objects */
	private static final float BASIC_RESTITUTION = 0.1f;
	/** Threshold for generating sound on collision */
	private static final float SOUND_THRESHOLD = 1.0f;

	// Since these appear only once, we do not care about the magic numbers.
	// In an actual game, this information would go in a data file.
	// Wall vertices
	private static final float[] WALL1 = { 0.0f, 18.0f, 16.0f, 18.0f, 16.0f, 17.0f,
										   8.0f, 15.0f,  1.0f, 17.0f,  2.0f,  7.0f,
										   3.0f,  5.0f,  3.0f,  1.0f, 16.0f,  1.0f,
										  16.0f,  0.0f,  0.0f,  0.0f};
	private static final float[] WALL2 = {32.0f, 18.0f, 32.0f,  0.0f, 16.0f,  0.0f,
										  16.0f,  1.0f, 31.0f,  1.0f, 30.0f, 10.0f,
										  31.0f, 16.0f, 16.0f, 17.0f, 16.0f, 18.0f};
	private static final float[] WALL3 = { 4.0f, 10.5f,  8.0f, 10.5f,
            							   8.0f,  9.5f,  4.0f,  9.5f};

	// The positions of the crate pyramid
	private static final float[] BOXES = { 14.5f, 14.25f,
            							   13.0f, 12.00f, 16.0f, 12.00f,
            							   11.5f,  9.75f, 14.5f,  9.75f, 17.5f, 9.75f,
            							   13.0f,  7.50f, 16.0f,  7.50f,
            							   11.5f,  5.25f, 14.5f,  5.25f, 17.5f, 5.25f,
            							   10.0f,  3.00f, 13.0f,  3.00f, 16.0f, 3.00f, 19.0f, 3.0f};

	// Other game objects
	/** The initial rocket position */
	private static Vector2 ROCK_POS = new Vector2(24, 4);
	/** The goal door position */
	private static Vector2 GOAL_POS = new Vector2( 6, 12);

	private static Vector2 CLICK_POS = new Vector2(-1,-1);
	private static Vector2 SHOOT_VEC = new Vector2(0,0);

	// Physics objects for the game
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;
	/** Reference to the rocket/player avatar */
	private RobotModel rocket;

	/**
	 * Creates and initialize a new instance of the rocket lander game
	 *
	 * The game has default gravity and other settings
	 */
	public GamePlayController() {
		setDebug(false);
		setComplete(false);
		setFailure(false);
		robotController = new RobotController();
		collisionController = new CollisionController();
		world.setContactListener(collisionController);
	}
	
	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		Vector2 gravity = new Vector2(0,0);//Vector2(world.getGravity() );
		robotController.reset();
		//parse level

		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		world.dispose();
		
		world = new World(gravity,false);
		world.setContactListener(collisionController);
		setComplete(false);
		setFailure(false);
		populateLevel();
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {

		// Add level goal
		float dwidth  = goalTile.getRegionWidth()/scale.x;
		float dheight = goalTile.getRegionHeight()/scale.y;
		goalDoor = new BoxObstacle(GOAL_POS.x,GOAL_POS.y,dwidth,dheight);
		goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
		goalDoor.setDensity(0.0f);
		goalDoor.setFriction(0.0f);
		goalDoor.setRestitution(0.0f);
		goalDoor.setSensor(true);
		goalDoor.setDrawScale(scale);
		goalDoor.setTexture(goalTile);
		addObject(goalDoor);

		// Create ground pieces
		PolygonObstacle obj;
		obj = new PolygonObstacle(WALL1, 0, 0);
		obj.setBodyType(BodyDef.BodyType.StaticBody);
		obj.setDensity(BASIC_DENSITY);
		obj.setFriction(BASIC_FRICTION);
		obj.setRestitution(BASIC_RESTITUTION);
		obj.setDrawScale(scale);
		obj.setTexture(earthTile);
		obj.setName("wall1");
		addObject(obj);

		obj = new PolygonObstacle(WALL2, 0, 0);
		obj.setBodyType(BodyDef.BodyType.StaticBody);
		obj.setDensity(BASIC_DENSITY);
		obj.setFriction(BASIC_FRICTION);
		obj.setRestitution(BASIC_RESTITUTION);
		obj.setDrawScale(scale);
		obj.setTexture(earthTile);
		obj.setName("wall2");
		addObject(obj);

		obj = new PolygonObstacle(WALL3, 0, 0);
		obj.setBodyType(BodyDef.BodyType.StaticBody);
		obj.setDensity(BASIC_DENSITY);
		obj.setFriction(BASIC_FRICTION);
		obj.setRestitution(BASIC_RESTITUTION);
		obj.setDrawScale(scale);
		obj.setTexture(earthTile);
		obj.setName("wall3");
		addObject(obj);

		// Create the pile of boxes
		for (int ii = 0; ii < BOXES.length; ii += 2) {
			int id = RandomController.rollInt(0,crateTextures.length-1);
			TextureRegion texture = crateTextures[id];
			dwidth  = texture.getRegionWidth()/scale.x;
			dheight = texture.getRegionHeight()/scale.y;
			BoxObstacle box = new BoxObstacle(BOXES[ii], BOXES[ii+1], dwidth, dheight);
			box.setDensity(CRATE_DENSITY);
			box.setFriction(CRATE_FRICTION);
			box.setRestitution(BASIC_RESTITUTION);
			box.setName("crate"+id);
			box.setDrawScale(scale);
			box.setTexture(texture);
			box.setLinearDamping(10.0f);
			box.setAngularDamping(10.0f);
			addObject(box);
		}

		// Create the rocket avatar
		dwidth  = rocketTexture.getRegionWidth()/scale.x;
		dheight = rocketTexture.getRegionHeight()/scale.y;
		rocket = new RobotModel(ROCK_POS.x, ROCK_POS.y, dwidth, dheight);
		rocket.setDrawScale(scale);
		rocket.setTexture(rocketTexture);
	    rocket.setBurnerStrip(RobotModel.Burner.MAIN,  mainTexture);
	    rocket.setBurnerStrip(RobotModel.Burner.LEFT,  leftTexture);
	    rocket.setBurnerStrip(RobotModel.Burner.RIGHT,  rghtTexture);
		Filter filter = rocket.getFilterData();
		filter.groupIndex = -1;
		rocket.setFilterData(filter);
	  
	    // Add the sound names
	    rocket.setBurnerSound(RobotModel.Burner.MAIN,  MAIN_FIRE_SOUND);
	    rocket.setBurnerSound(RobotModel.Burner.LEFT,  LEFT_FIRE_SOUND);
	    rocket.setBurnerSound(RobotModel.Burner.RIGHT,  RGHT_FIRE_SOUND);
		addObject(rocket);
	}

	public Vector2 getShootVec(){ return SHOOT_VEC;}

	public void setShootVec(Vector2 vec){ SHOOT_VEC = vec;}


	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class WorldController.
	 * This method is called after input is read, but before collisions are resolved.
	 * The very last thing that it should do is apply forces to the appropriate objects.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void update(float delta) {
		//calls update on robotcontroller
		robotController.update(delta);

		//#region INSERT CODE HERE
		// Read from the input and add the force to the rocket model
		// Then apply the force using the method you modified in RocketObject

		//#endregion


	    // If we use sound, we must remember this.
	    SoundController.getInstance().update();
	}
	

	

		
	}
}