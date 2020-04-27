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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.physics.host.HostController;
import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.obstacle.EnergyPillar;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.obstacle.Wall;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.util.SoundController;

import java.io.File;

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

	/** The asset for the bounce sound */
	private static final String  BOUNCE_SOUND = "host/bounce.mp3";
	/** The asset for the possession sound */
	private static final String  POSSESSION_SOUND = "host/possession.mp3";
	/** The asset for the slingshot sound */
	private static final String  LAUNCH_SOUND = "host/launch.mp3";
	/** The asset for the failure sound */
	private static final String  FAILURE_SOUND = "shared/failure.mp3";
	/** The asset for the victory sound */
	private static final String  VICTORY_SOUND = "shared/victory.mp3";
	/** The asset for the golem walking sound */
	private static final String  WALK_SOUND = "host/walk.mp3";


	private AssetState assetState = AssetState.EMPTY;

	private Level level;

	private int lvl;

	protected HostModel possessed;

	protected HostModel pedestal;

	protected SpiritModel spirit;

	protected EnergyPillar[] energyPillars;

	private Vector2 cache;

	private final int lifePerBounce = 40;

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
		manager.load(FAILURE_SOUND, Sound.class);
		assets.add(FAILURE_SOUND);
		manager.load(VICTORY_SOUND, Sound.class);
		assets.add(VICTORY_SOUND);
		manager.load(LAUNCH_SOUND, Sound.class);
		assets.add(LAUNCH_SOUND);
		manager.load(WALK_SOUND, Sound.class);
		assets.add(WALK_SOUND);
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
		sounds.allocate(manager, FAILURE_SOUND);
		sounds.allocate(manager, VICTORY_SOUND);
		sounds.allocate(manager, LAUNCH_SOUND);
		sounds.allocate(manager, WALK_SOUND);
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

		cache = new Vector2();

		File folder = new File("levels");
		levels = folder.listFiles();

		// This method of searching through the directory doesn't work on desktop
		// once the project is converted into a .jar. They are "internal" files
		// and so the f.list will return an empty list.

		// FileHandle f = Gdx.files.internal("levels");
		// levels = f.list();
		// System.out.println(levels + "printing levels");
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
		setMenu(false);

		Vector2 gravity = new Vector2(world.getGravity());

		FileHandle levelToLoad;
		System.out.println("levels/" + levels[currentLevel%levels.length].getName());
		levelToLoad = Gdx.files.local("levels/" + levels[currentLevel%levels.length].getName());

//		if (currentLevel == 3) {
//				levelToLoad = Gdx.files.local("levels/custom3.lvl");
//		}
//		else {
//			levelToLoad = Gdx.files.internal("levels/custom" + currentLevel + ".lvl");
//		}

//		try {
		level = loader.loadLevel(levelToLoad);
//		}
//		catch(Exception e) {
//			level = loader.loadLevel(new FileHandle("levels/custom1.lvl"));
//		}

		HUD.clearHUD();
		HUD.setNumTotalHosts(level.hosts.size());

		dimensions.set(level.dimensions);
		System.out.println("dimensions: " + dimensions);

		pedestal = level.pedestal;
		spirit = level.start;
		spirit.setName("spirit");

		energyPillars = level.energyPillars;

		possessed = pedestal;
		spirit.setGoToCenter(true);

		System.out.println(System.getProperty("user.dir"));

		spirit.setIsPossessing(true);

		//level = loader.reset(lvl);
		//parse level

		hostController = new HostController(level.hosts, scale, arrowTex, pedestal, canvas, energyPillars);

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
		for(Obstacle obj : level.walls) {
			// Set the hitbox of the wall to be dependent on its texture
			if(obj instanceof Wall) {
				((Wall)obj).setAltHitbox();
			}
			addQueue.add(obj);
		}
		for(Obstacle obj : level.water) {
			addQueue.add(obj);
		}
		for(Obstacle obj : level.sand) {
			addQueue.add(obj);
		}
		for(Obstacle obj : level.borderEdges) {
			addQueue.add(obj);
		}
		for(Obstacle obj : level.borderCorners) {
			addQueue.add(obj);
		}
		for(Obstacle obj : level.energyPillars) {
			addQueue.add(obj);
		}
		for(HostModel host : level.hosts) {
			addQueue.add(host);
		}
		addQueue.add(level.start);
		addQueue.add(level.pedestal);
		collisionController.addHosts(level.hosts);
		collisionController.addSpirit(level.start);
	}

	/**
	 * Keeps all hosts and spirits in bounds.
	 */
	public void keepInBounds(){

		Vector2 pos = spirit.getPosition();
		if(pos.x > bounds.x + bounds.width){
			if (getSound()) { SoundController.getInstance().play(BOUNCE_SOUND, BOUNCE_SOUND, false); }
			spirit.setDidBounce(true);
			spirit.setPosition(bounds.x + bounds.width, pos.y);
			pos.x = bounds.x + bounds.width;
			spirit.setVX(-spirit.getVX());
			spirit.setPosAtBounce(new Vector2(spirit.getPosition()));
		}
		else if(pos.x < bounds.x){
			if (getSound()) { SoundController.getInstance().play(BOUNCE_SOUND, BOUNCE_SOUND, false); }
			spirit.setDidBounce(true);
			spirit.setPosition(bounds.x, pos.y);
			pos.x = bounds.x;
			spirit.setVX(-spirit.getVX());
			spirit.setPosAtBounce(new Vector2(spirit.getPosition()));
		}

		if(pos.y > bounds.y + bounds.height){
			if (getSound()) { SoundController.getInstance().play(BOUNCE_SOUND, BOUNCE_SOUND, false); }
			spirit.setDidBounce(true);
			spirit.setPosition(pos.x, bounds.y + bounds.height);
			spirit.setVY(-spirit.getVY());
			spirit.setPosAtBounce(new Vector2(spirit.getPosition()));
		}
		else if(pos.y < bounds.y){
			if (getSound()) { SoundController.getInstance().play(BOUNCE_SOUND, BOUNCE_SOUND, false); }
			spirit.setDidBounce(true);
			spirit.setPosition(pos.x, bounds.y);
			spirit.setVY(-spirit.getVY());
			spirit.setPosAtBounce(new Vector2(spirit.getPosition()));
		}

		for(HostModel h : level.hosts){
			pos = h.getPosition();
			if(pos.x > bounds.x + bounds.width){
				h.setPosition(bounds.x + bounds.width, pos.y);
				pos.x = bounds.x + bounds.width;
			}
			else if(pos.x < bounds.x){
				pos.x = bounds.x;
				h.setPosition(bounds.x, pos.y);
			}

			if(pos.y > bounds.y + bounds.height){
				h.setPosition(pos.x, bounds.y + bounds.height);
			}
			else if(pos.y < bounds.y){
				h.setPosition(pos.x, bounds.y);
			}

		}
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

		//keep everything in bounds
//		keepInBounds();

		// Check win condition
		if (hostController.checkAllPossessed() && !isComplete()){
			HUD.incrementCurrHosts();
			setComplete(true);
			if (getSound()) { SoundController.getInstance().play(VICTORY_SOUND,VICTORY_SOUND,false); }
		}

		// Determine if there is any possession
		if (collisionController.isPossessed()) {

			// Play possession sound if something different is possessed this frame
			if (possessed != collisionController.getHostPossessed()) {
				if (getSound()) { SoundController.getInstance().play(POSSESSION_SOUND,POSSESSION_SOUND,false); }

				// A new host has been possessed that has never been possessed before
				if (collisionController.isNewPossession()) { HUD.incrementCurrHosts(); }
			}

			possessed = collisionController.getHostPossessed();
		}

		// Remove Pedestal Once Possessing a New Host
		if(pedestal != possessed) {
			pedestal.markRemoved(true);
		}


		// Calls update on hostController
		hostController.update(delta, possessed, spirit, level.pedestal, collisionController.getInSand(), energyPillars);
		if (hostController.getLaunched()){
			if (getSound()) { SoundController.getInstance().play(LAUNCH_SOUND,LAUNCH_SOUND,false); }
		}

		if (!isFailure() & !isComplete() & hostController.isMoving()) {
			if (getSound()) { SoundController.getInstance().play(WALK_SOUND, WALK_SOUND, true); }
		}
		else {
			if (getSound()) { SoundController.getInstance().stop(WALK_SOUND); }
		}


		// Check lose condition
		if ((hostController.getPossessedBlownUp() || spirit.hasNoLivesLeft()) && !isComplete() && !isFailure()) {
			setFailure(true);
			if (getSound()) { SoundController.getInstance().play(FAILURE_SOUND, FAILURE_SOUND, false); }
		}


		// Get arrow and draw if applicable
		arrow = hostController.getArrow();
		if (arrow != null){ arrow.draw(canvas); }

		// Update bouncing if applicable
		if (collisionController.isBounced()) {
			if (getSound()) { SoundController.getInstance().play(BOUNCE_SOUND, BOUNCE_SOUND, false); }
		}

		// Calculate spirit's screen coordinates from box2d coordinates
		cache.set(spirit.getPosition());
		cache.scl(scale.x, scale.y);

		// Handle camera panning
		canvas.setCamTarget(cache);
		canvas.updateCamera();

		// Handle camera zooming
		if (InputController.getInstance().didZoom() && !spirit.hasLaunched) {
			canvas.toggleZoom();
		}

		else if (spirit.hasLaunched) {
			canvas.zoomIn();
		}


		// Uncomment this if we want to zoom in when a shot is fired, but not when it's being aimed
		/*
		if (spirit.hasLaunched) {
			canvas.zoomIn();
		}
		 */

		if (InputController.getInstance().getHorizontal() != 0 || InputController.getInstance().getVertical() != 0) {
			canvas.zoomIn();
		}

		// Zoom back in if you click to aim a shot; Want to see what players think about this
		/*
		if (InputController.getInstance().didTertiary()) {
			canvas.zoomIn();
		}
		*/


		// CHECK IF POSSESSED IS IN ENERGY PILLAR RADIUS
        for(EnergyPillar ep : energyPillars) {
            System.out.println(possessed.getPosition());
            System.out.println(ep.getPosition());
        	if((Math.pow((possessed.getPosition().x - ep.getPosition().x), 2) / Math.pow(ep.getEnergyPillarMajor() + possessed.getWidth() / 2,2)) + ((Math.pow((possessed.getPosition().y - ep.getPosition().y), 2))/(Math.pow(ep.getEnergyPillarMinor() + possessed.getHeight() / 2, 2))) <= 1)  {
        	    possessed.setCurrentCharge((int)possessed.getMaxCharge());
			}
		}


		if (spirit.getDidBounce()) {
			spirit.decCurrentLife(lifePerBounce);
		}

		// Update sounds
		SoundController.getInstance().update();

		// Clear collision controller
		collisionController.clear();
		spirit.setDidBounce(false);
	}
}