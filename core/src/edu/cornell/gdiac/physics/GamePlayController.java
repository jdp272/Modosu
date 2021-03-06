/*
 * GamePlayController.java
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
import edu.cornell.gdiac.physics.obstacle.DecorativeRoots;
import edu.cornell.gdiac.physics.obstacle.EnergyPillar;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.obstacle.OscWall;
import edu.cornell.gdiac.physics.obstacle.Wall;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.util.MusicController;
import edu.cornell.gdiac.util.SoundController;

/**
 * GamePlay controller for Modosu.
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

	private SoundController sound;

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
	/** The asset for bird noise 1 */
	private static final String  BIRD_NOISE_1 = "shared/bird1.mp3";
	/** The asset for bird noise 2 */
	private static final String  BIRD_NOISE_2 = "shared/bird2.mp3";
	/** The asset for bird noise 3 */
	private static final String  BIRD_NOISE_3 = "shared/bird3.mp3";
	/** The asset for cricket noise */
	private static final String  CRICKET_NOISE = "shared/cricket.mp3";
	/** The asset for cicada noise */
	private static final String  CICADA_NOISE = "shared/cicadas.mp3";
	/** The asset for frog noise 1*/
	private static final String  FROG_NOISE = "shared/frog.mp3";
	/** The asset for leave noise*/
	private static final String  LEAF_NOISE = "shared/leaves.mp3";

	private AssetState assetState = AssetState.EMPTY;

	private Level level;

	protected HostModel possessed;

	protected HostModel pedestal;

	protected SpiritModel spirit;

	protected EnergyPillar[] energyPillars;

	protected OscWall[] oscWalls;

	private Vector2 cache;

	private Vector2 panTarget;

	private final int lifePerBounce = 40;

	private final float panSpeed = 10f;

	private boolean isActiveScreen;

	private boolean launchedFirstShot;

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
		manager.load(BIRD_NOISE_1, Sound.class);
		assets.add(BIRD_NOISE_1);
		manager.load(BIRD_NOISE_2, Sound.class);
		assets.add(BIRD_NOISE_2);
		manager.load(BIRD_NOISE_3, Sound.class);
		assets.add(BIRD_NOISE_3);
		manager.load(CRICKET_NOISE, Sound.class);
		assets.add(CRICKET_NOISE);
		manager.load(CICADA_NOISE, Sound.class);
		assets.add(CICADA_NOISE);
		manager.load(FROG_NOISE, Sound.class);
		assets.add(FROG_NOISE);
		manager.load(LEAF_NOISE, Sound.class);
		assets.add(LEAF_NOISE);


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
		if (assetState != AssetState.LOADING) { return; }

		sound.allocate(manager, BOUNCE_BOUND_SOUND);
		sound.allocate(manager, BOUNCE_WALL_SOUND);
		sound.allocate(manager, POSSESSION_SOUND);
		sound.allocate(manager, FAILURE_SOUND);
		sound.allocate(manager, VICTORY_SOUND);
		sound.allocate(manager, LAUNCH_SOUND);
		sound.allocate(manager, WALK_SOUND);
		sound.allocate(manager, WALK_SAND_SOUND);
		sound.allocate(manager, BIRD_NOISE_1);
		sound.allocate(manager, BIRD_NOISE_2);
		sound.allocate(manager, BIRD_NOISE_3);
		sound.allocate(manager, FROG_NOISE);
		sound.allocate(manager, CRICKET_NOISE);
		sound.allocate(manager, CICADA_NOISE);
		sound.allocate(manager, LEAF_NOISE);
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
		world.setContactListener(collisionController);

		sound = SoundController.getInstance();
		// Initialize vectors
        cache = new Vector2();

	}

	/**
	 * Setter that sets screen to be active
	 * @param isActive is true if gameplay screen is active
	 */
	public void setIsActiveScreen(boolean isActive) {
		this.isActiveScreen = isActive;
	}



	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {

	    getLevels(inCustom);
		// Reset game conditions to represent a new game
		setComplete(false);
		setFailure(false);
		setMenu(false);

		canvas.resetZoom();

		renderHUD = true;
		launchedFirstShot = false;

		MusicController.getInstance().play("gameMusic");

		Vector2 gravity = new Vector2(world.getGravity());

		FileHandle levelToLoad;

		int levelIndex = ((currentLevel%levels.size()) + levels.size()) % levels.size();

		// TODO These are currently the same so if everything works this if statement can be removed
		if (inCustom) { levelToLoad = levels.get(levelIndex); }
		else { levelToLoad = levels.get(levelIndex); }

		level = loader.loadLevel(levelToLoad, levelIndex, !inCustom);

		/* Load in Tutorial */
		tutorial.reset();
		TutorialData tutorialData = loader.getTutorialData(level.tutorialNum);

		if(tutorialData != null) {
			tutorial.addTutorial(tutorialData);
		}

		hud.clearHUD();
		Gdx.input.setInputProcessor(hud.getStage());
		hud.setNumTotalHosts(level.hosts.size());

		dimensions.set(level.dimensions);

		pedestal = level.pedestal;
		spirit = level.spirit;
		spirit.setName("spirit");

		energyPillars = level.energyPillars;

		oscWalls = level.oscWalls;

		possessed = pedestal;
		spirit.setGoToCenter(true);

		spirit.setIsPossessing(true);

		hostController = new HostController(level.hosts, scale, arrowHeadTex, arrowDashTex, pedestal, canvas, energyPillars);

		// Reset the collision controller
		collisionController.reset();

		for (Obstacle o : objects) {
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
		for(Obstacle obj : level.oscWalls) {
			addQueue.add(obj);
		}
		for(HostModel host : level.hosts) {
			addQueue.add(host);
		}
		for(DecorativeRoots roots : level.decorativeRootTiles) {
			addQueue.add(roots);
		}
		addQueue.add(level.spirit);
		addQueue.add(level.pedestal);
		collisionController.addHosts(level.hosts);
		collisionController.addSpirit(level.spirit);
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
		// Stop the walking sounds when paused
		if (pressedPause) {
			sound.stop(WALK_SAND_SOUND);
			sound.stop(WALK_SOUND);
			return;
		}

		// Animate oscWalls
		for(OscWall ow : oscWalls) {
			ow.updateAnimation();
		}
		// Check win condition
		if (hostController.checkAllPossessed() && !isComplete()){
			hud.incrementCurrHosts();
			setComplete(true);
			sound.getInstance().play(VICTORY_SOUND,VICTORY_SOUND,false, 1.2f*sound.getVolume());
		}

		// Determine if there is any possession
		if (collisionController.isPossessed()) {

			// Play possession sound if something different is possessed this frame
			if (possessed != collisionController.getHostPossessed()) {
				sound.getInstance().play(POSSESSION_SOUND,POSSESSION_SOUND,false);
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
		if (!isFailure() && !isComplete() && hostController.isMoving()) {
			// Determine if the player is in sand
			String walkingSound = collisionController.getInSand() ? WALK_SAND_SOUND : WALK_SOUND;
			// If unmuted, then play the correct walking sound
			if(!collisionController.getInSand()){
				sound.stop(WALK_SAND_SOUND);
			}
			sound.play(walkingSound, walkingSound, true, sound.getVolume()*.30f);
		}
		// Stop playing if player is no longer moving
		else {
			sound.stop(WALK_SAND_SOUND);
			sound.stop(WALK_SOUND);
		}

		// Check lose condition
		if (hostController.getPossessedBlownUp() && !isComplete() && !isFailure()) {
			setFailure(true);
			sound.play(FAILURE_SOUND, FAILURE_SOUND, false, .25f*sound.getVolume());
		}

		// Check if HUD timer should update
		if (!launchedFirstShot && spirit.hasLaunched) {
			launchedFirstShot = true;
		}

		if (launchedFirstShot) {
			HUD.update(delta);
		}

		// Get arrow and set for world controller to draw, if applicable
		arrow = hostController.getArrow();

		// Update bouncing if applicable
		if (collisionController.isBounced()) {
			String bounceSound = collisionController.getBounceOnBounds() ? BOUNCE_BOUND_SOUND : BOUNCE_WALL_SOUND;
			sound.play(bounceSound, bounceSound, false);
		}

		// Calculate spirit's screen coordinates from box2d coordinates
		if (possessed.isPedestal() && !spirit.hasLaunched) {
			if (InputController.getInstance().didTertiary()) {
				panTarget.x = pedestal.getPosition().x;
				panTarget.y = pedestal.getPosition().y;
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
        if (isActiveScreen) {
			canvas.setCamTarget(cache);
			canvas.updateCamera();
		}

		// Handle camera zooming
		if (InputController.getInstance().didZoom() && !spirit.hasLaunched) {
			canvas.toggleZoom();
		}

		else if (spirit.hasLaunched) {
			canvas.zoomIn();
		}

		if (!possessed.isPedestal() && (InputController.getInstance().getHorizontal() != 0 || InputController.getInstance().getVertical() != 0)) {
			canvas.zoomIn();
		}

		Boolean isInPillar = false;
		// Check if possessed is in energy pillar radius
        for(EnergyPillar ep : energyPillars) {
        	if((Math.pow((possessed.getPosition().x - ep.getPosition().x), 2) / Math.pow(ep.getEnergyPillarMajor(),2)) +
					((Math.pow((possessed.getPosition().y - ep.getPosition().y), 2))/(Math.pow(ep.getEnergyPillarMinor(), 2))) <= 1)  {
        	    if (spirit.hasLaunched) { isInPillar = false; }
        	    else {
					possessed.setCurrentCharge(possessed.getCurrentCharge() + 2);
					isInPillar = true;
				}
			}
		}

        possessed.setInPillar(isInPillar);

		if (spirit.getDidBounce()) {
			spirit.decCurrentLife(lifePerBounce);
			canvas.shakeCamera(15, 5);
		}

		// Deal with random ambient sounds
		if (Math.random() > .999){
			double test = Math.random();
			if (getCurrentLevel() % NUM_LEVELS < NUM_LEVELS/4){
				//frog and cricket full
				if (test >= .50) SoundController.getInstance().play(CRICKET_NOISE, CRICKET_NOISE, false,.50f * SoundController.getInstance().getVolume());
				else  SoundController.getInstance().play(FROG_NOISE, FROG_NOISE, false, .50f * SoundController.getInstance().getVolume());
			}
			else if (getCurrentLevel() % NUM_LEVELS < 2 * NUM_LEVELS/4){
				//frogs faded
				if (test >= .50) SoundController.getInstance().play(FROG_NOISE, FROG_NOISE, false, .25f * SoundController.getInstance().getVolume());
				//leaves on
				else SoundController.getInstance().play(LEAF_NOISE, LEAF_NOISE, false, .50f * SoundController.getInstance().getVolume());
			}
			else if (getCurrentLevel() % NUM_LEVELS < 3 * NUM_LEVELS/4) {
				//leaves and cicadas
				if (test >= .80) SoundController.getInstance().play(CICADA_NOISE, CICADA_NOISE, false, .40f * SoundController.getInstance().getVolume());
				else if (test >= .60) SoundController.getInstance().play(LEAF_NOISE, LEAF_NOISE, false, .40f * SoundController.getInstance().getVolume());

				//bird faded
				else if (test >= .40) SoundController.getInstance().play(BIRD_NOISE_1, BIRD_NOISE_1, false,.25f * SoundController.getInstance().getVolume());
				else if (test >= .20) SoundController.getInstance().play(BIRD_NOISE_2, BIRD_NOISE_2, false, .25f * SoundController.getInstance().getVolume());
				else SoundController.getInstance().play(BIRD_NOISE_3, BIRD_NOISE_3, false, .25f * SoundController.getInstance().getVolume());
			}
			else {
				//birds on
				if (test >= .75) SoundController.getInstance().play(BIRD_NOISE_1, BIRD_NOISE_1, false,.40f * SoundController.getInstance().getVolume());
				else if (test >= .50) SoundController.getInstance().play(BIRD_NOISE_2, BIRD_NOISE_2, false, .40f * SoundController.getInstance().getVolume());
				else if (test >= .25) SoundController.getInstance().play(BIRD_NOISE_3, BIRD_NOISE_3, false, .40f * SoundController.getInstance().getVolume());

				//cicadas faded
				else SoundController.getInstance().play(CICADA_NOISE, CICADA_NOISE, false, .25f * SoundController.getInstance().getVolume());
			}
		}

		//Update sounds
		sound.getInstance().update();

		// Not paused anymore
		wasPaused = false;

		// Clear collision controller
		collisionController.clear();
		spirit.setDidBounce(false);
	}
}