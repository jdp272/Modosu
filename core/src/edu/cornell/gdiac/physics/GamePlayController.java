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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.host.HostList;
import edu.cornell.gdiac.physics.host.HostController;
import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.util.SoundController;

import java.util.HashMap;

/**
 * Gameplay controller for Modosu.
 *
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop, which
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class GamePlayController extends WorldController {

	private HostController hostController;

	private CollisionController collisionController;

	private Loader loader;


	/** The asset for the collision sound */
	private static final String  BOUNCE_SOUND = "host/bounce.mp3";
	/** The asset for the main afterburner sound */
	private static final String  POSSESSION_SOUND = "host/possession.mp3";
//	/** The asset for the launch sound */
//	private static final String  LAUNCH_SOUND = "host/launch.mp3";
//	/** The asset for the explosion sound */
//	private static final String  EXPLODE_SOUND = "host/afterburner.mp3";

	/** Track asset loading from all instances and subclasses */
	private AssetState assetState = AssetState.EMPTY;

	private Level level;

	private int lvl;

	protected HostModel possessed;

	protected SpiritModel spirit;

	/** Keep track of what hosts have been possessed */
	private HashMap<HostModel, Boolean> havePossessed;

	/** How many hosts need to be possessed to win this level */
	private int numHosts;

	/** How many hosts have been possessed up to this frame */
	private int numPosessed;


	// Other game objects
	/** The initial spirit start position */
	private static Vector2 SPIRIT_POS = new Vector2(500, 100);

	// The positions of the hosts in pairs, x then y coordinate
	private static final float[] HOSTS = { SPIRIT_POS.x,SPIRIT_POS.y, 200.0f, 400.0f, 800.0f, 100.0f};

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

		manager.load(BOUNCE_SOUND, Sound.class);
		assets.add(BOUNCE_SOUND);
		manager.load(POSSESSION_SOUND, Sound.class);
		assets.add(POSSESSION_SOUND);
//		manager.load(LAUNCH_SOUND, Sound.class);
//		assets.add(LAUNCH_SOUND);
//		manager.load(EXPLOSION_SOUND, Sound.class);
//		assets.add(EXPLOSION_SOUND);

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

		SoundController sounds = SoundController.getInstance();
		sounds.allocate(manager, BOUNCE_SOUND);
		sounds.allocate(manager, POSSESSION_SOUND);
//		sounds.allocate(manager, LAUNCH_SOUND);
//		sounds.allocate(manager, EXPLOSION_SOUND);
		super.loadContent(manager);
		assetState = AssetState.COMPLETE;
	}

	/**
	 * Creates and initialize a new instance of Modosu
	 */
	public GamePlayController() {
		setDebug(false);
		setComplete(false);
		setFailure(false);
		loader = new Loader();
		collisionController = new CollisionController();
		lvl = 0;
		world.setContactListener(collisionController);
		havePossessed = new HashMap<>();
		numPosessed = 0;
	}

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		// Reset game conditions to represent a new game
		setComplete(false);
		setFailure(false);

		Vector2 gravity = new Vector2(world.getGravity());
		BoxObstacle[] obs = new BoxObstacle[BOXES.length/2];

		// Reset fields of this controller
		possessed = null;
		numPosessed = 0;

		// Reset the collision controller
		collisionController.reset();

		float dwidth  = obstacleTex.getRegionWidth();
		float dheight = obstacleTex.getRegionHeight();

		// Create all the obstacles (walls) on the level
		BoxObstacle box;
		for (int i = 0; i < BOXES.length; i+=2) {
			box = new BoxObstacle(BOXES[i],BOXES[i+1], dwidth, dheight);
			box.setTexture(obstacleTex);
			box.setBodyType(BodyDef.BodyType.StaticBody);
			box.setName("wall");
			obs[i/2] = box;
		}

		// Reset the possession tracker
		havePossessed.clear();

		// Create all the hosts and fill the list
		HostList hosts = new HostList();

		dwidth  = hostTex.getRegionWidth();
		dheight = hostTex.getRegionHeight();

		HostModel host;
		for (int i = 0; i < HOSTS.length; i+=2) {
			host = new HostModel(HOSTS[i],HOSTS[i+1], dwidth, dheight, 0, 1000);
			host.setTexture(hostTex);
			host.setHostGaugeTexture(hostGaugeTex);
			hosts.add(host,false);
			havePossessed.put(host, false);
		}

		Vector2[] ins = {new Vector2(800,400),new Vector2(500,400)};
		host = new HostModel(800, 400, dwidth, dheight, 0, 10000, ins);
		host.setTexture(hostTex);
		host.setHostGaugeTexture(hostGaugeTex);
		hosts.add(host,false);
		havePossessed.put(host, false);

		// Reset the constant spirit start
		SPIRIT_POS.x = 500;
		SPIRIT_POS.y = 100;

		// Create the spirit
		SpiritModel spark = new SpiritModel(SPIRIT_POS.x,SPIRIT_POS.y,spiritTex.getRegionWidth(),spiritTex.getRegionHeight(),10, 100);
		spark.setTexture(spiritTex);

		level = new Level(null, obs, hosts, spark);
		possessed = hosts.get(0);
		spirit = spark;

		//level = loader.reset(lvl);
		//parse level
		hostController = new HostController(level.hosts, arrowTex, canvas.getHeight());

		// How many hosts need to be possessed to win
		numHosts = level.hosts.size();

		for(Obstacle o : objects) {
			o.deactivatePhysics(world);
		}

		objects.clear();
		addQueue.clear();
		world.dispose();

		world = new World(gravity,false);
		world.setContactListener(collisionController);

		populateLevel();
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		for(Obstacle obj : level.obstacles) {
			addQueue.add(obj);
		}
		for(Obstacle obj : level.hosts) {
			addQueue.add(obj);
		}
		addQueue.add(level.start);
		collisionController.addHosts(level.hosts);
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

		// Check win condition
		if (numPosessed == numHosts){
			setComplete(true);
		}

		// Determine if there is any possession
		if (collisionController.isPossessed()) {

			// Play possession sound if something different is possessed this frame
			if (possessed != collisionController.getHostPossessed()) {
				SoundController.getInstance().play(POSSESSION_SOUND,POSSESSION_SOUND,false);
			}

			possessed = collisionController.getHostPossessed();

			// Record a new possession
			if (!havePossessed.get(possessed)) {

				havePossessed.put(possessed, true);
				numPosessed++;
			}
		}

		// Calls update on hostcontroller
		hostController.update(delta, possessed, spirit);

		// Check lose condition
		if (hostController.getPossessedBlownUp() && !isComplete()) {
			setFailure(true);
		}

		// Get arrow and draw if applicable
		arrow = hostController.getArrow();
		if (arrow != null){ arrow.draw(canvas); }

		// Update bouncing if applicable
		if (collisionController.isBounced()) {
			SoundController.getInstance().play(BOUNCE_SOUND, BOUNCE_SOUND, false);
		}

		// Handle camera panning
		canvas.setCamTarget(spirit.getPosition());
		canvas.updateCamera();

		// Update sounds
		SoundController.getInstance().update();

		// Clear collision controller
		collisionController.clear();
	}
}