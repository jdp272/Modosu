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
import edu.cornell.gdiac.physics.robot.RobotList;
import edu.cornell.gdiac.physics.robot.RobotModel;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.RandomController;
import edu.cornell.gdiac.util.SoundController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

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

	private Loader loader;

	/** Reference to the rocket texture */
	private static final String ROCK_TEXTURE = "robot/robot.png";

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
	private AssetState assetState = AssetState.EMPTY;
	/** Texture asset for mouse crosshairs */
	private TextureRegion crosshairTexture;

	private Level level;

	private int lvl;

	protected RobotModel possessed;

	protected SpiritModel spirit;


	// Other game objects
	/** The initial rocket position */
	private static Vector2 SPIRIT_POS = new Vector2(500, 100);

	// The positions of the crate pyramid
	private static final float[] ROBOTS = { SPIRIT_POS.x,SPIRIT_POS.y, 200.0f, 400.0f, 800.0f, 100.0f};

	// The positions of the walls
	private static final float[] BOXES = { 350.0f, 32.0f, 350.0f, 96.0f, 350.0f, 160.f, 350.f, 224.0f, // LEFT
											32.0f, 500.0f, 96.0f, 500.0f, 160.0f, 500.0f, 224.0f, 500.0f, 288.0f, 500.0f, 352.0f, 500.0f, // TOP
											650.0f, 32.0f, 650.0f, 96.0f, 650.0f, 160.f, 650.f, 224.0f}; // RIGHT

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

		if (assetState != AssetState.EMPTY) {
			return;
		}
		assetState = AssetState.LOADING;

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
		if (assetState != AssetState.LOADING) {
			return;
		}

		
		super.loadContent(manager);
		assetState = AssetState.COMPLETE;
	}

	/**
	 * Creates and initialize a new instance of the rocket lander game
	 *
	 * The game has default gravity and other settings
	 */
	public GamePlayController() {
		setDebug(false);
		setComplete(false);
		setFailure(false);
		loader = new Loader();
		collisionController = new CollisionController();
		lvl = 0;
		world.setContactListener(collisionController);
	}



	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		Vector2 gravity = new Vector2(world.getGravity());
		BoxObstacle[] obs = new BoxObstacle[BOXES.length/2];

		float dwidth  = obstacleTex.getRegionWidth();
		float dheight = obstacleTex.getRegionHeight();

		BoxObstacle box;
		for (int i = 0; i < BOXES.length; i+=2) {
			box = new BoxObstacle(BOXES[i],BOXES[i+1], dwidth, dheight);
			box.setTexture(obstacleTex);
			box.setBodyType(BodyDef.BodyType.StaticBody);
			obs[i/2] = box;
		}

		RobotList robs = new RobotList();

		dwidth  = robotTex.getRegionWidth();
		dheight = robotTex.getRegionHeight();

		RobotModel rob;
		for (int i = 0; i < ROBOTS.length; i+=2) {
			rob = new RobotModel(ROBOTS[i],ROBOTS[i+1], dwidth, dheight, 10000);
			rob.setTexture(robotTex);
			robs.add(rob,false);
		}

		SpiritModel spark = new SpiritModel(SPIRIT_POS.x,SPIRIT_POS.y,spiritTex.getRegionWidth(),spiritTex.getRegionHeight(),10);
		spark.setTexture(spiritTex);

		level = new Level(null, obs, robs, spark);
		possessed = robs.get(0);
		spirit = spark;
		//level = loader.reset(lvl);
		//parse level
		robotController = new RobotController(level.robots);

		for(Obstacle o : objects) {
			o.deactivatePhysics(world);
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
		for(Obstacle obj : level.obstacles) {
			addQueue.add(obj);
		}
		for(Obstacle obj : level.robots) {
			addQueue.add(obj);
		}
		addQueue.add(level.start);
		collisionController.addRobots(level.robots);
		collisionController.addSpirit(level.start);
	}


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

		robotController.update(delta, possessed, spirit);

		if (collisionController.isPossessed()) {
			possessed = collisionController.getRobotPossessed();
		} else { possessed = null; }

		if (collisionController.isBounced()) {
			if (spirit.bounces == 0) {
				spirit.setPosition(-10,-10);
			} else {
				spirit.decBounces();
			}
		}

	    // If we use sound, we must remember this.
	    SoundController.getInstance().update();
	}



}