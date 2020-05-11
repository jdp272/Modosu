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
import com.badlogic.gdx.audio.Music;
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
import edu.cornell.gdiac.util.MusicController;
import edu.cornell.gdiac.util.SoundController;

import javax.print.DocFlavor;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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

	/** The asset for the bounce sound of a wall and spirit */
	private static final String  BOUNCE_WALL_SOUND = "host/bouncewall.mp3";
	/** The asset for the bounce sound of an edge or corner and spirit */
	private static final String  BOUNCE_BOUND_SOUND = "host/bouncebound.mp3";
	/** The asset for the possession sound */
	private static final String  POSSESSION_SOUND = "host/possession2.wav";
	/** The asset for the slingshot sound */
	private static final String  LAUNCH_SOUND = "host/launch.mp3";
	/** The asset for the failure sound */
	private static final String  FAILURE_SOUND = "shared/failure.mp3";
	/** The asset for the victory sound */
	private static final String  VICTORY_SOUND = "shared/victory.mp3";
	/** The asset for the golem walking sound */
	private static final String  WALK_SOUND = "host/walk.mp3";
	/** The asset for the golem walking sound on sand */
	private static final String  WALK_SAND_SOUND = "host/sandwalk.mp3";


	private AssetState assetState = AssetState.EMPTY;

	private Level level;

	private int lvl;

	protected HostModel possessed;

	protected HostModel pedestal;

	protected SpiritModel spirit;

	protected EnergyPillar[] energyPillars;

	private Tutorial tutorial;


	private Vector2 cache;

	private Vector2 panTarget;

	private final int lifePerBounce = 40;

	private final float panSpeed = 10f;

	public boolean inCustom;

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

		manager.load(BOUNCE_WALL_SOUND, Sound.class);
		assets.add(BOUNCE_WALL_SOUND);
		manager.load(BOUNCE_BOUND_SOUND, Sound.class);
		assets.add(BOUNCE_BOUND_SOUND);
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
		manager.load(WALK_SAND_SOUND, Sound.class);
		assets.add(WALK_SAND_SOUND);


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
		sounds.allocate(manager, BOUNCE_BOUND_SOUND);
		sounds.allocate(manager, BOUNCE_WALL_SOUND);
		sounds.allocate(manager, POSSESSION_SOUND);
		sounds.allocate(manager, FAILURE_SOUND);
		sounds.allocate(manager, VICTORY_SOUND);
		sounds.allocate(manager, LAUNCH_SOUND);
		sounds.allocate(manager, WALK_SOUND);
		sounds.allocate(manager, WALK_SAND_SOUND);
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

		// Initialize vectors

        cache = new Vector2();
		cache = new Vector2();

		// TODO Change level loading here
		File folder = new File("levels");
		levels = new ArrayList(Arrays.asList(folder.listFiles(Constants.filenameFilter)));
		Collections.sort(levels);
	}



	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		if(inCustom){
			File folder = new File("Custom");
			levels = new ArrayList(Arrays.asList(folder.listFiles(Constants.filenameFilter)));
			Collections.sort(levels);
		}else{
			File folder = new File("levels");
			levels = new ArrayList(Arrays.asList(folder.listFiles(Constants.filenameFilter)));
			Collections.sort(levels);
		}
		// Reset game conditions to represent a new game
		setComplete(false);
		setFailure(false);
		setMenu(false);

		System.out.println("just called play game music");
		MusicController.getInstance().play("gameMusic");
		// MusicController.getInstance().setVolume(40);

		Vector2 gravity = new Vector2(world.getGravity());

		FileHandle levelToLoad;

		int levelIndex = ((currentLevel%levels.size()) + levels.size()) % levels.size();

		if(inCustom){
			System.out.println("Custom/" + levels.get(levelIndex).getName());
			levelToLoad = Gdx.files.local("Custom/" + levels.get(levelIndex).getName());
		}else {
			System.out.println("levels/" + levels.get(levelIndex).getName());
			levelToLoad = Gdx.files.local("levels/" + levels.get(levelIndex).getName());
		}

		level = loader.loadLevel(levelToLoad);

		hud.clearHUD();
		Gdx.input.setInputProcessor(hud.getStage());
		hud.setNumTotalHosts(level.hosts.size());

		// Do I really need to make a new one everytime? Can just declare outside once!
		tutorial = new Tutorial();

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

		panTarget = new Vector2(pedestal.getPosition().x * scale.x, pedestal.getPosition().y * scale.y);

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

		tutorial.addTutorial();
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
		if (hostController.checkAllPossessed() && !isComplete()){
			hud.incrementCurrHosts();
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
		}

		// Remove Pedestal Once Possessing a New Host
		if(pedestal != possessed) {
			pedestal.markRemoved(true);
		}

		// Calls update on hostController
		hostController.update(delta, possessed, spirit, level.pedestal, collisionController.getInSand(), energyPillars, wasPaused);

		if (hostController.getLaunched()){
			SoundController.getInstance().play(LAUNCH_SOUND,LAUNCH_SOUND,false);
		}

		// If player is still playing and moving
		if (!isFailure() & !isComplete() & hostController.isMoving()) {

			// Determine if the player is in sand
			String walkingSound = collisionController.getInSand() ? WALK_SAND_SOUND : WALK_SOUND;
			// If unmuted, then play the correct walking sound
			SoundController.getInstance().play(walkingSound, walkingSound, true, .30f);
		}
		// Stop playing if player is no longer moving
		else {
			SoundController.getInstance().stop(WALK_SAND_SOUND);
			SoundController.getInstance().stop(WALK_SOUND);
		}

		// Check lose condition
		if ((hostController.getPossessedBlownUp() || !spirit.isAlive()) && !isComplete() && !isFailure()) {
			setFailure(true);
			SoundController.getInstance().play(FAILURE_SOUND, FAILURE_SOUND, false);
		}

		HUD.update(delta);

		// Check to hid tutorial
		if (!tutorial.didCompleteTutorial()) {
			tutorial.updateTutorial(delta);
			tutorial.drawTutorial(delta);
		}

		// Get arrow and draw if applicable
		arrow = hostController.getArrow();
		if (arrow != null){ arrow.draw(canvas); }

		// Update bouncing if applicable
		if (collisionController.isBounced()) {
			String bounceSound = collisionController.getBounceOnBounds() ? BOUNCE_BOUND_SOUND : BOUNCE_WALL_SOUND;
			SoundController.getInstance().play(bounceSound, bounceSound, false, .2f);
		}

		// Calculate spirit's screen coordinates from box2d coordinates
		if (possessed.isPedestal() && !spirit.hasLaunched){

			if (InputController.getInstance().didTertiary()) {
				panTarget = pedestal.getPosition();
				panTarget.x *= scale.x;
				panTarget.y *= scale.y;
			}

		    panTarget.x += InputController.getInstance().getHorizontal() * panSpeed;
		    panTarget.y += InputController.getInstance().getVertical() * panSpeed;
		    cache.set(panTarget);
		}
		else {
			cache.set(spirit.getPosition());
			cache.scl(scale.x, scale.y);
		}

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


		if (!possessed.isPedestal() && (InputController.getInstance().getHorizontal() != 0 || InputController.getInstance().getVertical() != 0)) {
			canvas.zoomIn();
		}

		// Zoom back in if you click to aim a shot; Want to see what players think about this
		/*
		if (InputController.getInstance().didTertiary()) {
			canvas.zoomIn();
		}
		*/

		Boolean isInPillar = false;
		// CHECK IF POSSESSED IS IN ENERGY PILLAR RADIUS
        for(EnergyPillar ep : energyPillars) {
            //System.out.println(possessed.getPosition());
            //System.out.println(ep.getPosition());
//        	if((Math.pow((possessed.getPosition().x - ep.getPosition().x), 2) / Math.pow(ep.getEnergyPillarMajor() + possessed.getWidth() / 2,2)) + ((Math.pow((possessed.getPosition().y - ep.getPosition().y), 2))/(Math.pow(ep.getEnergyPillarMinor() + possessed.getHeight() / 2, 2))) <= 1)  {
        	if((Math.pow((possessed.getPosition().x - ep.getPosition().x), 2) / Math.pow(ep.getEnergyPillarMajor(),2)) + ((Math.pow((possessed.getPosition().y - ep.getPosition().y), 2))/(Math.pow(ep.getEnergyPillarMinor(), 2))) <= 1)  {
        		isInPillar = true;
        	    possessed.setCurrentCharge((int)possessed.getCurrentCharge() + 2);
			}
		}

        possessed.setInPillar(isInPillar);

		if (spirit.getDidBounce()) {
			spirit.decCurrentLife(lifePerBounce);
		}

		// Update sounds
		SoundController.getInstance().update();

		// Not paused anymore
		wasPaused = false;

		// Clear collision controller
		collisionController.clear();
		spirit.setDidBounce(false);
	}
}