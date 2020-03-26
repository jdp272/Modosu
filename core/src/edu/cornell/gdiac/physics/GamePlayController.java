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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.host.HostList;
import edu.cornell.gdiac.physics.host.HostController;
import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.util.FilmStrip;
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

	private HostController hostController;

	private CollisionController collisionController;

	/** Reference to the rocket texture */
	private static final String ROCK_TEXTURE = "host/host.png";
	private static final String HOST_GAUGE_TEXTURE = "host/host_gauge.png";

	private static final String SPIRIT_TEXTURE = "host/spirit.png";
	/** Texture file for mouse crosshairs */
	private static final String CROSS_FILE = "ragdoll/crosshair.png";
	/** Reference to the crate image assets */
	private static final String CRATE_PREF = "host/crate0";
	/** How many crate assets we have */
	private static final int MAX_CRATES = 2;

	/** The asset for the collision sound */
	private static final String  COLLISION_SOUND = "host/bump.mp3";
	/** The asset for the main afterburner sound */
	private static final String  MAIN_FIRE_SOUND = "host/afterburner.mp3";
	/** The asset for the right afterburner sound */
	private static final String  RGHT_FIRE_SOUND = "host/sideburner-right.mp3";
	/** The asset for the left afterburner sound */
	private static final String  LEFT_FIRE_SOUND = "host/sideburner-left.mp3";
	
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

	protected HostModel possessed;

	protected SpiritModel spirit;
	private Vector2 cache;


	// Other game objects
	/** The initial rocket position */
	private static Vector2 SPIRIT_POS = new Vector2(15.f, 3.f);

	// The positions of the crate pyramid
	private static final float[] HOSTS = { SPIRIT_POS.x,SPIRIT_POS.y, 6.0f, 12.0f, 24.0f, 3.0f};

	// The positions of the walls
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
		collisionController = new CollisionController();
		lvl = 0;
		world.setContactListener(collisionController);

		cache = new Vector2();
	}



	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		Vector2 gravity = new Vector2(world.getGravity());
		BoxObstacle[] obs = new BoxObstacle[BOXES.length/2];


		float dwidth  = obstacleTex.getRegionWidth() / scale.x;
		float dheight = obstacleTex.getRegionHeight() / scale.y;

		BoxObstacle box;
		for (int i = 0; i < BOXES.length; i+=2) {
			box = new BoxObstacle(BOXES[i],BOXES[i+1], dwidth, dheight);
			box.setDrawScale(scale);
			box.setTexture(obstacleTex);
			box.setBodyType(BodyDef.BodyType.StaticBody);
			obs[i/2] = box;
		}

		HostList hosts = new HostList();

		dwidth  = hostTex.getRegionWidth() / scale.x;
		dheight = hostTex.getRegionHeight() / scale.y;

		HostModel host;
		for (int i = 0; i < HOSTS.length; i+=2) {
			host = new HostModel(HOSTS[i],HOSTS[i+1], dwidth, dheight, 0, 1000);
			host.setDrawScale(scale);
			host.setTexture(hostTex);
			host.setHostGaugeTexture(hostGaugeTex);
			hosts.add(host,false);
		}
		Vector2[] ins = {new Vector2(24,12),new Vector2(15,12)};
		host = new HostModel(24, 12, dwidth, dheight, 0, 1000, ins);
		host.setDrawScale(scale);
		host.setTexture(hostTex);
		host.setHostGaugeTexture(hostGaugeTex);
		hosts.add(host,false);

//		SPIRIT_POS.x = 15;
//		SPIRIT_POS.y = 3;

		dwidth  = spiritTex.getRegionWidth() / scale.x;
		dheight = spiritTex.getRegionHeight() / scale.y;

		SpiritModel spark = new SpiritModel(SPIRIT_POS.x,SPIRIT_POS.y,dwidth,dheight,10);
		spark.setDrawScale(scale);
		spark.setTexture(spiritTex);

		level = new Level(null, obs, hosts, spark);

		FileHandle f = new FileHandle("out.txt");
//		loader.saveLevel(f, level);

		level = loader.loadLevel(f);

		spark = level.start;
		hosts = level.hosts;
		obs = level.obstacles;

		spirit = spark;
		possessed = hosts.get(0);


		//level = loader.reset(lvl);
		//parse level
		hostController = new HostController(level.hosts);

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
		//calls update on hostcontroller

		if (collisionController.isPossessed()) {
			possessed = collisionController.getHostPossessed();
		} else { possessed = null; }

		hostController.update(delta, possessed, spirit);

		if (collisionController.isBounced()) {
			if (spirit.bounces == 0) {
				spirit.setPosition(-10,-10);
			} else {
				spirit.decBounces();
			}
		}

		// Calculate spirit's screen coordinates from box2d coordinates
		cache.set(spirit.getPosition());
		cache.scl(scale.x, scale.y);

		// Handle camera panning
		canvas.setCamTarget(cache);
		canvas.updateCamera();

	    // If we use sound, we must remember this.
	    SoundController.getInstance().update();
	}



}