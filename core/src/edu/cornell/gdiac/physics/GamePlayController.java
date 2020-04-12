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
import com.badlogic.gdx.graphics.Texture;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
	/** The asset for the golem walking sound */
	private static final String  WALK_SOUND = "host/walk.mp3";

//	/** The asset for the explosion sound */
//	private static final String  EXPLODE_SOUND = "host/afterburner.mp3";


	private AssetState assetState = AssetState.EMPTY;

	private Level level;

	private int lvl;

	protected HostModel possessed;

	protected HostModel pedestal;

	protected SpiritModel spirit;

	private Vector2 cache;

	private int currentLevel = 0;

	private FileHandle[] levels;

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

		// This method of searching through the directory doesn't work on desktop
		// once the project is converted into a .jar. They are "internal" files
		// and so the f.list will return an empty list.

		// FileHandle f = Gdx.files.internal("levels");
		// levels = f.list();
		// System.out.println(levels + "printing levels");

		currentLevel = 0;
	}

	public void setCurrentLevel(int l) {
		currentLevel = l;
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


		FileHandle levelToLoad = Gdx.files.internal("levels/custom" + currentLevel + ".lvl");

//		FileHandle f = new FileHandle(levelName);
//		loader.saveLevel(f, level);

		//System.out.println("loading level: " + levelName);
		level = loader.loadLevel(levelToLoad);

		HUD.clearHUD();
		HUD.setNumTotalHosts(level.hosts.size());

		pedestal = level.pedestal;
		spirit = level.start;
		spirit.setName("spirit");

		possessed = pedestal;
		spirit.setGoToCenter(true);

		System.out.println(System.getProperty("user.dir"));

		spirit.setIsPossessing(true);

		//level = loader.reset(lvl);
		//parse level

		hostController = new HostController(level.hosts, scale, arrowTex, pedestal);

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
		for(Obstacle obj : level.water) {
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
			spirit.setPosition(bounds.x + bounds.width, pos.y);
			pos.x = bounds.x + bounds.width;
			spirit.setVX(-spirit.getVX());
		}
		else if(pos.x < bounds.x){
			spirit.setPosition(bounds.x, pos.y);
			pos.x = bounds.x;
			spirit.setVX(-spirit.getVX());
		}

		if(pos.y > bounds.y + bounds.height){
			spirit.setPosition(pos.x, bounds.y + bounds.height);
			spirit.setVY(-spirit.getVY());
		}
		else if(pos.y < bounds.y){
			spirit.setPosition(pos.x, bounds.y);
			spirit.setVY(-spirit.getVY());
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
		keepInBounds();


		// Check win condition
		if (hostController.checkAllPossessed() && !isComplete()){
			HUD.incrementCurrHosts();
			setComplete(true);
			SoundController.getInstance().play(VICTORY_SOUND,VICTORY_SOUND,false);
		}

		// Determine if there is any possession
		if (collisionController.isPossessed()) {

			// Play possession sound if something different is possessed this frame
			if (possessed != collisionController.getHostPossessed()) {
				SoundController.getInstance().play(POSSESSION_SOUND,POSSESSION_SOUND,false);

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
		hostController.update(delta, possessed, spirit, level.pedestal);
		if (hostController.getLaunched()){
			SoundController.getInstance().play(LAUNCH_SOUND,LAUNCH_SOUND,false);
		}

		if (hostController.isMoving()) {
			SoundController.getInstance().play(WALK_SOUND, WALK_SOUND, true);
		}
		else {
			SoundController.getInstance().stop(WALK_SOUND);
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
		if (InputController.getInstance().didTertiary()) {
			canvas.zoomIn();
		}

		// Update sounds
		SoundController.getInstance().update();

		// Clear collision controller
		collisionController.clear();
	}
}