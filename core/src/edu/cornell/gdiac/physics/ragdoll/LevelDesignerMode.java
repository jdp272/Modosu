/*
 * RagdollController.java
 *
 * You are not expected to modify this file at all.  You are free to look at it, however,
 * and determine how it works.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics.ragdoll;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.host.HostList;
import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.obstacle.*;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.RandomController;
import edu.cornell.gdiac.util.SoundController;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Gameplay specific controller for the ragdoll fishtank.
 *
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop, which
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class LevelDesignerMode extends WorldController {
	/** Texture file for mouse crosshairs */
	private static final String CROSS_FILE = "ragdoll/crosshair.png";
	/** Texture file for watery foreground */
	private static final String FOREG_FILE = "ragdoll/foreground.png";
	/** Texture file for background image */
	private static final String BACKG_FILE = "shared/background.png";
	/** File for the bubble generator */
	private static final String BUBBLE_FILE = "ragdoll/bubble.png";
	/** Files for the body textures */
	private static final String[] RAGDOLL_FILES = { "ragdoll/tux_body.png", "ragdoll/ProfWhite.png",
			"ragdoll/tux_arm.png",  "ragdoll/tux_forearm.png",
			"ragdoll/tux_thigh.png", "ragdoll/tux_shin.png" };

	/** Reference to the bubble sound assets */
	private static final String[] BUBBLE_SOUNDS = {"ragdoll/bubble01.mp3", "ragdoll/bubble02.mp3",
			"ragdoll/bubble03.mp3", "ragdoll/bubble04.mp3"};

	/** Speed for changing camera position */
	private static final float CAMERA_SPEED = 5.f;

	/** Texture asset for mouse crosshairs */
	private TextureRegion crosshairTexture;
	/** Texture asset for background image */
	private TextureRegion backgroundTexture;
	/** Texture asset for watery foreground */
	private TextureRegion foregroundTexture;

	/** Track asset loading from all instances and subclasses */
	private AssetState ragdollAssetState = AssetState.EMPTY;

	/** The level that is populated and used for saving */
	private Level level;
	/** A list of obstacles in the level */
	private PooledList<BoxObstacle> obstacles;
	/** A list of hosts in the level */
	private PooledList<HostModel> hosts;
	/** The spirit for the level */
	private SpiritModel spirit;

	/** The obstacle that, when clicked, spawns a new obstacle */
	private BoxObstacle boxSpawn;

	/** If the selector should select at the next update */
	private boolean select;

	/** The camera position */
	private Vector2 camTarget;

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
		if (ragdollAssetState != AssetState.EMPTY) {
			return;
		}

		ragdollAssetState = AssetState.LOADING;
		manager.load(CROSS_FILE, Texture.class);
		assets.add(CROSS_FILE);
		manager.load(FOREG_FILE, Texture.class);
		assets.add(FOREG_FILE);
		manager.load(BACKG_FILE, Texture.class);
		assets.add(BACKG_FILE);
		manager.load(BUBBLE_FILE, Texture.class);
		assets.add(BUBBLE_FILE);
		for(int ii = 0; ii < RAGDOLL_FILES.length;  ii++) {
			manager.load(RAGDOLL_FILES[ii], Texture.class);
			assets.add(RAGDOLL_FILES[ii]);
		}
		for(int ii = 0; ii < BUBBLE_SOUNDS.length; ii++) {
			manager.load(BUBBLE_SOUNDS[ii], Sound.class);
			assets.add(BUBBLE_SOUNDS[ii]);
		}

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
		if (ragdollAssetState != AssetState.LOADING) {
			return;
		}

		crosshairTexture = createTexture(manager,CROSS_FILE,false);
		backgroundTexture = createTexture(manager,BACKG_FILE,false);
		foregroundTexture = createTexture(manager,FOREG_FILE,false);

		super.loadContent(manager);
		ragdollAssetState = AssetState.COMPLETE;
	}

	/** The new lessened gravity for this world */
	private static final float WATER_GRAVITY = -0.25f;

	// Physics constants for initialization
	/** The density for all of (external) objects */
	private static final float BASIC_DENSITY = 0.0f;
	/** The friction for all of (external) objects */
	private static final float BASIC_FRICTION = 0.1f;
	/** The restitution for all of (external) objects */
	private static final float BASIC_RESTITUTION = 0.1f;
	/** The transparency for foreground image */
	private static Color FORE_COLOR = new Color(0.0f, 0.2f, 0.3f, 0.2f);

	// Since these appear only once, we do not care about the magic numbers.
	// In an actual game, this information would go in a data file.
	// Wall vertices
	private static final float[] WALL1 = {16.0f, 18.0f, 16.0f, 17.0f,  1.0f, 17.0f,
			1.0f,  1.0f, 16.0f,  1.0f, 16.0f,  0.0f,
			0.0f,  0.0f,  0.0f, 18.0f};

	private static final float[] WALL2 =  {32.0f, 18.0f, 32.0f,  0.0f, 16.0f,  0.0f,
			16.0f,  1.0f, 31.0f,  1.0f, 31.0f, 17.0f,
			16.0f, 17.0f, 16.0f, 18.0f};

	// Other game objects
	/** The initial position of the ragdoll head */
	private static Vector2 DOLL_POS = new Vector2( 16.0f,  10.0f);

	/** Reference to the character's ragdoll */
	private RagdollModel ragdoll;

	/** Counter for sound control */
	private long soundCounter;

	/** Mouse selector to move the ragdoll */
	private ObstacleSelector selector;

	/**
	 * Creates and initialize a new instance of the ragdoll fishtank
	 *
	 * The world has lower gravity to simulate being underwater.
	 */
	public LevelDesignerMode() {
		super(DEFAULT_WIDTH,DEFAULT_HEIGHT,WATER_GRAVITY);
		setDebug(false);
		setComplete(false);
		setFailure(false);
		soundCounter = 0;

		obstacles = new PooledList<BoxObstacle>();
		hosts = new PooledList<HostModel>();

		camTarget = new Vector2();
	}

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		Vector2 gravity = new Vector2(world.getGravity());

		// The objects should be sensors
		factory.makeSensors = true;

		camTarget.set(canvas.getWidth() / 2.f, canvas.getHeight() / 2.f);

		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		world.dispose();

		obstacles.clear();

		FileHandle f = new FileHandle("out.txt");

		world = new World(gravity,false);

		level = loader.loadLevel(f);

		setComplete(false);
		setFailure(false);
		populateLevel();
	}

	public void updateShader() {
		InputController input = InputController.getInstance();

		// Set mouse pos first so default shader gets it
//		canvas.setMousePos(input.getMousePos(bounds));
//		canvas.setMousePos(input.getCrossHair().scl(scale));
//		canvas.setDefaultShader(false);
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
//		for(Obstacle ob : level.obstacles) {
//			addObject(ob);
//			addQueue.add(ob);
//			obj.activatePhysics(world);
//		}
//		for(Obstacle ob : level.hosts) {
//
//			addQueue.add(ob);
//			obj.activatePhysics(world);
//		}
//		addQueue.add(level.start);
//		level.start.activatePhysics(world);
//		collisionController.addHosts(level.hosts);
//		collisionController.addSpirit(level.start);

		boxSpawn = factory.makeObstacle(0.f, 0.f);
		boxSpawn.setX((canvas.getWidth() + camTarget.x) / scale.x - boxSpawn.getWidth() / 2.f);
		boxSpawn.setY((canvas.getHeight() + camTarget.y) / scale.y - boxSpawn.getHeight() / 2.f);
		addObject(boxSpawn);

		// Can't be selected
		boxSpawn.selectable = false;

		selector = new ObstacleSelector(world);
		selector.setTexture(crosshairTexture);
		selector.setDrawScale(scale);

		/*
		BodyDef groundDef = new BodyDef();
		groundDef.type = BodyDef.BodyType.StaticBody;
		EdgeShape groundShape = new EdgeShape();
		groundShape.set(-500.0f, 0.0f, 500.0f, 0.0f);
		ground = world.createBody(groundDef);
		ground.createFixture(groundShape,0);
		*/
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
	public void update(float dt) {
		// Move an object if touched
		InputController input = InputController.getInstance();

		/* Offset the mouse based on the camera translation.

		   Half of the canvas is subtracted because when the board is centered,
		   camTarget is the middle of the board, not (0, 0), but there should be
		   no additional mouse offset.

		   Additionally, note that the mouse uses box2d coordinates, not screen
		   coordinates
		 */

		float mouseX = input.getCrossHair().x + (camTarget.x - (canvas.getWidth() / 2.f)) / scale.x;
		float mouseY = input.getCrossHair().y + (camTarget.y - (canvas.getHeight() / 2.f)) / scale.y;

		if ((input.didTertiary() || select) && !selector.isSelected()) {
			selector.select(mouseX, mouseY);
		} else if (!input.didTertiary() && selector.isSelected()) {
			selector.deselect();
		} else {
			selector.moveTo(mouseX, mouseY);
		}

		if(boxSpawn.checkClicked()) {
			// Create a new box at the spawn point
			BoxObstacle box = factory.makeObstacle(boxSpawn.getX(), boxSpawn.getY());
			addObject(box);
			obstacles.add(box);

			// Select the new object
			selector.select(mouseX, mouseY);
		}

		// Add new objects
		if(input.newBox() && !selector.isSelected()) {
			// Add a new obstacle
			BoxObstacle box = factory.makeObstacle(mouseX, mouseY);
			addObject(box);
			obstacles.add(box);
		}
		if(input.newHost() && !selector.isSelected()) {
			// Add a new host
			HostModel host = factory.makeSmallHost(mouseX, mouseY);
			addObject(host);
			hosts.add(host);
		}
		if(input.newSpirit() && !selector.isSelected()) {
			// If the spirit already exists, move it. Otherwise, make a new one
			if(spirit == null) {
				spirit = factory.makeSpirit(mouseX, mouseY);
				addObject(spirit);
			} else {
				spirit.setX(mouseX);
				spirit.setY(mouseY);
			}
		}
		if(input.didDelete()) {
			// Get a selection if there currently isn't one
			if(!selector.isSelected()) {
				selector.select(mouseX, mouseY);
			}

			// Remove what was at the mouse location
			if(selector.isSelected()) {
				// Get the selection, then remove it from the selector
				Obstacle selection = selector.getObstacle();
				selector.deselect();

				if(selection instanceof HostModel) {
					hosts.remove(selection);
				} else if(selection instanceof SpiritModel) {
					// Do nothing- no list to remove spirit from
				} else if(selection instanceof BoxObstacle) { // Should be after above if statements
					obstacles.remove(selection);
				} else {
					assert false : "Invalid object type";
				}
				selection.markRemoved(true);
			}
		}
		if(input.didSave()) {
			save("custom.lvl");
		}

		System.out.println(camTarget);

		// Update the camera position
		camTarget.add(CAMERA_SPEED * input.getHorizontal(), CAMERA_SPEED * input.getVertical());

		// Update spawn positions after updating the camera
		boxSpawn.setX((canvas.getWidth() / 2.f + camTarget.x) / scale.x - boxSpawn.getWidth() / 2.f);
		boxSpawn.setY((canvas.getHeight() / 2.f + camTarget.y) / scale.y - boxSpawn.getHeight() / 2.f);

		canvas.setCamTarget(camTarget);
		canvas.updateCamera();

		// If we use sound, we must remember this.
		SoundController.getInstance().update();
	}

	/**
	 * Saves the current set up as a level
	 *
	 * @param levelName The name for the saved level
	 */
	private void save(String levelName) {
		FileHandle f = new FileHandle(levelName);

		// TODO: Make this not creating new objects by updating Level to use PooledList(?)

		BoxObstacle[] obstacleArray = new BoxObstacle[obstacles.size()];
		int i = 0;
		for(BoxObstacle box : obstacles) {
			obstacleArray[i++] = box;
			if(box != null) {
				System.out.println(box);
			}
		}

		HostList hostList = new HostList();
		for(HostModel host : hosts) {
			hostList.add(host, false);
		}

		level.set(null, obstacleArray, hostList, spirit);
		loader.saveLevel(f, level);
	}

	/**
	 * Draw the physics objects together with foreground and background
	 *
	 * This is completely overridden to support custom background and foreground art.
	 *
	 * @param dt Timing values from parent loop
	 */
	public void draw(float dt) {
		canvas.clear();

		// Draw background unscaled.
		canvas.begin();
		canvas.draw(backgroundTexture, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
		canvas.end();

		canvas.begin();

		for(Obstacle obj : objects) {
			obj.draw(canvas);
		}
		canvas.end();

		if (isDebug()) {
			canvas.beginDebug();
			for(Obstacle obj : objects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
		}

		// Draw foreground last.
		canvas.begin();
		canvas.draw(foregroundTexture, FORE_COLOR,  0, 0, canvas.getWidth(), canvas.getHeight());
		selector.draw(canvas);
		canvas.end();
	}

}