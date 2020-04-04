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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.host.HostController;
import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.SoundController;

import java.util.ArrayList;
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

	/** How many crate assets we have */
	private static final int MAX_CRATES = 2;

	/** The asset for the bounce sound */
	private static final String  BOUNCE_SOUND = "host/bounce.mp3";
	/** The asset for the possession sound */
	private static final String  POSSESSION_SOUND = "host/possession.mp3";
	/** The asset for the slingshot sound */
//	private static final String  LAUNCH_SOUND = "host/launch.mp3";
	private static final String  LAUNCH_SOUND = "host/launch2.mp3";
	/** The asset for the failure sound */
	private static final String  FAILURE_SOUND = "shared/failure.mp3";
	/** The asset for the victory sound */
	private static final String  VICTORY_SOUND = "shared/victory.mp3";

//	/** The asset for the explosion sound */
//	private static final String  EXPLODE_SOUND = "host/afterburner.mp3";

	/** Track asset loading from all instances and subclasses */
	private AssetState assetState = AssetState.EMPTY;

	private Level level;

	private int lvl;

	protected HostModel possessed;

	protected SpiritModel spirit;
	private Vector2 cache;

	/** Keep track of what hosts have been possessed */
	private HashMap<HostModel, Boolean> havePossessed;

	/** How many hosts need to be possessed to win this level */
	private int numHosts;

	/** How many hosts have been possessed up to this frame */
	private int numPossessed;

	/** Animation for host walking */
	private FilmStrip golemWalk;

//	private static final String GOLEM_WALK_TEXTURE = "host/golemwalk.png";


	// Other game objects
	/** The initial spirit start position */
	private static Vector2 SPIRIT_POS = new Vector2(15.f, 3.f);

	// The positions of the hosts
	private static final float[] HOSTS = { SPIRIT_POS.x,SPIRIT_POS.y, 6.0f, 12.0f, 24.0f, 3.0f};

	// The positions of the obstacles
	private static final float[] BOXES = { 11.0f, 1.0f, 11.0f, 3.0f, 11.0f, 5.f, 11.f, 7.0f, // LEFT
											1.0f, 16.0f, 3.0f, 16.0f, 5.0f, 16.0f, 7.0f, 16.0f, 9.0f, 16.0f, 11.0f, 16.0f, // TOP
											20.0f, 1.0f, 20.0f, 3.0f, 20.0f, 5.f, 20.f, 7.0f}; // RIGHT


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

//		manager.load(GOLEM_WALK_TEXTURE, Texture.class);
//		assets.add(GOLEM_WALK_TEXTURE);

		manager.load(BOUNCE_SOUND, Sound.class);
		assets.add(BOUNCE_SOUND);
		manager.load(POSSESSION_SOUND, Sound.class);
		assets.add(POSSESSION_SOUND);
		manager.load(FAILURE_SOUND, Sound.class);
		assets.add(FAILURE_SOUND);
		manager.load(VICTORY_SOUND, Sound.class);
		assets.add(VICTORY_SOUND);
		manager.load(LAUNCH_SOUND, Sound.class);
		assets.add(LAUNCH_SOUND);
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

//		golemWalk = createFilmStrip(manager, GOLEM_WALK_TEXTURE, 1, 4, 5);
		SoundController sounds = SoundController.getInstance();
		sounds.allocate(manager, BOUNCE_SOUND);
		sounds.allocate(manager, POSSESSION_SOUND);
		sounds.allocate(manager, FAILURE_SOUND);
		sounds.allocate(manager, VICTORY_SOUND);
		sounds.allocate(manager, LAUNCH_SOUND);
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
		collisionController = new CollisionController();
		lvl = 0;
		world.setContactListener(collisionController);

		havePossessed = new HashMap<>();
		numPossessed = 0;

		cache = new Vector2();
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
//		BoxObstacle[] obs = new BoxObstacle[BOXES.length/2];
//
//		// Reset fields of this controller
//		possessed = null;
//		numPossessed = 0;
//
//		// Reset the collision controller
//		collisionController.reset();
//
//		float dwidth  = obstacleTex.getRegionWidth() / scale.x;
//		float dheight = obstacleTex.getRegionHeight() / scale.y;
//
//		// Create all the obstacles (walls) on the level
//		BoxObstacle box;
//		for (int i = 0; i < BOXES.length; i+=2) {
//			box = new BoxObstacle(BOXES[i],BOXES[i+1], dwidth, dheight);
//			box.setDrawScale(scale);
//			box.setTexture(obstacleTex);
//			box.setBodyType(BodyDef.BodyType.StaticBody);
//			box.setName("wall");
//			obs[i/2] = box;
//		}
//
//		// Reset the possession tracker
//		havePossessed.clear();
//
//		// Create all the hosts and fill the list
//		ArrayList<HostModel> hosts = new ArrayList<>();
//
//		dwidth  = hostTex.getRegionWidth() / scale.x;
//		dheight = hostTex.getRegionHeight() / scale.y;
//
//		HostModel host;
//		for (int i = 0; i < HOSTS.length; i+=2) {
//			host = new HostModel(HOSTS[i],HOSTS[i+1], dwidth, dheight, 0, 1000);
//			host.setDrawScale(scale);
//			host.setTexture(hostTex);
//			host.setHostGaugeTexture(hostGaugeTex);
//			host.setBodyType(BodyDef.BodyType.DynamicBody);
//			hosts.add(host);
//			havePossessed.put(host, false);
//		}
//
//		Vector2[] ins = {new Vector2(24,12),new Vector2(15,12)};
//		host = new HostModel(24, 12, dwidth, dheight, 0, 1000, ins);
//		host.setDrawScale(scale);
//		host.setTexture(hostTex);
//		host.setHostGaugeTexture(hostGaugeTex);
//		hosts.add(host);
//		havePossessed.put(host, false);
//
////		SPIRIT_POS.x = 15;
////		SPIRIT_POS.y = 3;
//
//		dwidth  = spiritTex.getRegionWidth() / scale.x;
//		dheight = spiritTex.getRegionHeight() / scale.y;
//
//		SpiritModel spark = factory.makeSpirit(SPIRIT_POS.x, SPIRIT_POS.y);
//		spark.setDrawScale(scale);
//
//		level = new Level(null, obs, hosts, spark);

		String levelName;

		// TODO: Somehow do this with input controller!
		if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
			levelName = "levels/custom.lvl";
		} else {
			levelName = "levels/out.lvl";
		}

		FileHandle f = new FileHandle(levelName);
//		loader.saveLevel(f, level);

		System.out.println("loading level: " + levelName);
		level = loader.loadLevel(f);

		spirit = level.start;
		spirit.setName("spirit");

		possessed = level.hosts.get(0);

		System.out.println(System.getProperty("user.dir"));

		spirit.setIsPossessing(true);

		//level = loader.reset(lvl);
		//parse level
		hostController = new HostController(level.hosts, scale, arrowTex, canvas.getHeight());

		// How many hosts need to be possessed to win
		numHosts = level.hosts.size();
		numPossessed = 0;

		// Reset the collision controller
		collisionController.reset();

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
		for(HostModel host : level.hosts) {
			addQueue.add(host);
			havePossessed.put(host, false);
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
		if ((numPossessed == numHosts) && !isComplete()){
			setComplete(true);
			SoundController.getInstance().play(VICTORY_SOUND,VICTORY_SOUND,false);
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
				numPossessed++;
			}
		}

		// Calls update on hostController
		hostController.update(delta, possessed, spirit);
		if (hostController.getLaunched()){
			SoundController.getInstance().play(LAUNCH_SOUND,LAUNCH_SOUND,false);
		}

		if (collisionController.isAgainstWall() && !spirit.hasLaunched) {
			spirit.setVX(0f);
			spirit.setVY(0f);
		}

		// Check lose condition
		if (hostController.getPossessedBlownUp() && !isComplete() && !isFailure()) {
			setFailure(true);
			SoundController.getInstance().play(FAILURE_SOUND, FAILURE_SOUND, false);
		}

		// Get arrow and draw if applicable
		arrow = hostController.getArrow();
		if (arrow != null){ arrow.draw(canvas); }

		// Update bouncing if applicable
		if (collisionController.isBounced()) {
			SoundController.getInstance().play(BOUNCE_SOUND, BOUNCE_SOUND, false);
		}

		// Calculate spirit's screen coordinates from box2d coordinates
		cache.set(spirit.getPosition());
		cache.scl(scale.x, scale.y);

		// Handle camera panning
		canvas.setCamTarget(cache);
		canvas.updateCamera();

		// Handle camera zooming
		if (InputController.getInstance().didZoom()) {
			canvas.toggleZoom();
		}

		// Update sounds
		SoundController.getInstance().update();

		// Clear collision controller
		collisionController.clear();
	}
}