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
package edu.cornell.gdiac.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.obstacle.*;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.util.SoundController;

import java.util.ArrayList;
import java.util.ListIterator;
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
	private static final String CROSS_FILE = "shared/crosshair.png";
	/** Texture file for watery foreground */
	private static final String FOREG_FILE = "shared/foreground.png";
	/** Texture file for background image */
	private static final String BACKG_FILE = "shared/background.png";

	/** Speed for changing camera position */
	private static final float CAMERA_SPEED = 5.f;

	/** Width of each tile, in box2D coordinates */
	private static final float TILE_WIDTH = 2.f;

	/** Texture asset for mouse crosshairs */
	private TextureRegion crosshairTexture;
	/** Texture asset for background image */
	private TextureRegion backgroundTexture;
	/** Texture asset for foreground */
	private TextureRegion foregroundTexture;

	/** Track asset loading from all instances and subclasses */
	private AssetState assetState = AssetState.EMPTY;

	/** The level that is populated and used for saving */
	private Level level;

	/** The collection of spawning objects, for making new game elements */
	private SpawnerList spawnList;

	/** The camera position */
	private Vector2 camTarget;

	/** The 2D array that is the board */
	private Obstacle[][] board;

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
		manager.load(CROSS_FILE, Texture.class);
		assets.add(CROSS_FILE);
		manager.load(FOREG_FILE, Texture.class);
		assets.add(FOREG_FILE);
		manager.load(BACKG_FILE, Texture.class);
		assets.add(BACKG_FILE);

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

		crosshairTexture = createTexture(manager,CROSS_FILE,false);
		backgroundTexture = createTexture(manager,BACKG_FILE,false);
		foregroundTexture = createTexture(manager,FOREG_FILE,false);

		super.loadContent(manager);
		assetState = AssetState.COMPLETE;
	}

	/** The new lessened gravity for this world */
	private static final float WATER_GRAVITY = -0.25f;

	/** The transparency for foreground image */
	private static Color FORE_COLOR = new Color(0.0f, 0.2f, 0.3f, 0.2f);

	/** Mouse selector to move game objects */
	private ObstacleSelector selector;

	/**
	 * Creates and initialize a new instance of the level designer
	 *
	 * The world has lower gravity to simulate being underwater.
	 */
	public LevelDesignerMode() {
		super(DEFAULT_WIDTH,DEFAULT_HEIGHT,WATER_GRAVITY);
		setDebug(false);
		setComplete(false);
		setFailure(false);

		camTarget = new Vector2();

		board = new Obstacle[16][9];
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

		FileHandle f = new FileHandle("out.txt");

		world = new World(gravity,false);

		level = loader.loadLevel(f);

		setComplete(false);
		setFailure(false);
		populateLevel();
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {

		BoxObstacle boxSpawn = factory.makeObstacle(0.f, 0.f);
		addObject(boxSpawn);

		WaterTile waterSpawn = factory.makeWater(0.f, 0.f);
		addObject(waterSpawn);

		HostModel hostSpawn = factory.makeSmallHost(0.f, 0.f);
		addObject(hostSpawn);

		SpiritModel spiritSpawn = factory.makeSpirit(0.f, 0.f);

		addObject(spiritSpawn);

		spawnList = new SpawnerList(canvas, scale);

		spawnList.addSpawner(boxSpawn, new SpawnerList.CallbackFunction() {
			public Obstacle makeObject(float x, float y, Obstacle lastCreated) {
				return factory.makeObstacle(x, y);
			}
		});

		spawnList.addSpawner(waterSpawn, new SpawnerList.CallbackFunction() {
			public Obstacle makeObject(float x, float y, Obstacle lastCreated) {
				return factory.makeWater(x, y);
			}
		});

		spawnList.addSpawner(hostSpawn, new SpawnerList.CallbackFunction() {
			public Obstacle makeObject(float x, float y, Obstacle lastCreated) {
				return factory.makeSmallHost(x, y);
			}
		});

		spawnList.addSpawner(spiritSpawn, new SpawnerList.CallbackFunction() {
			public Obstacle makeObject(float x, float y, Obstacle lastCreated) {
				if (lastCreated == null) {
					return factory.makeSpirit(x, y);
				}
				lastCreated.setX(x);
				lastCreated.setY(y);

				return null;
			}
		});

		selector = new ObstacleSelector(world);
		selector.setTexture(crosshairTexture);
		selector.setDrawScale(scale);
	}

	/**
	 * Gets the tile index that a coordinate is in. Can be used for either x or
	 * y coordinates
	 *
	 * @param coord The box2D coordinate
	 *
	 * @return The index of the tile
	 */
	private int coordToTile(float coord) {
		return Math.round((coord - (TILE_WIDTH / 2.f)) / TILE_WIDTH);
	}

	/**
	 * Gets the coordinate of the center of a tile. Can be used for either x or
	 * y coordinates
	 *
	 * @param index The tile index
	 *
	 * @return The box2D coordinate of the tile center
	 */
	private float tileToCoord(int index) {
		return (index + 0.5f) * TILE_WIDTH;
	}

	/**
	 * Updates the texture for water tile at index x, y in the board based on
	 * its surroundings (if they are water or ground)
	 *
	 * If the tile is not a water tile, or if the tile is out of bounds, nothing
	 * happens.
	 *
	 * @param x The x index in the board of the tile to update
	 * @param y The y index in the board of the tile to update
	 */
	private void updateWaterTile(int x, int y) {
		if(x < 0 || y < 0 || x >= board.length || y >= board[x].length || !(board[x][y] instanceof WaterTile)) {
			return;
		}

		boolean above = false;
		if(y + 1 < board[x].length && !(board[x][y + 1] instanceof WaterTile)) {
			above = true;
		}

		boolean below = false;
		if(y - 1 >= 0 && !(board[x][y - 1] instanceof WaterTile)) {
			below = true;
		}

		boolean left = false;
		if(x - 1 >= 0 && !(board[x - 1][y] instanceof WaterTile)) {
			left = true;
		}

		boolean right = false;
		if(x + 1 < board.length && !(board[x + 1][y] instanceof WaterTile)) {
			right = true;
		}

		((WaterTile)board[x][y]).setFrame(above, below, left, right);
	}

	/**
	 * Does the updating for the selector.
	 *
	 * This method sets picks up, moves, and drops off objects from the selector
	 * based on the mouse. It updates the board 2D array of tiles. It also
	 * handles updating the spawner and creating new objects from the spawner,
	 * which would be immediately picked up by the selector.
	 */
	private void updateSelector() {
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

		if ((input.didTertiary()) && !selector.isSelected()) {
			selector.select(mouseX, mouseY);

			// The tile indices
			int x = coordToTile(mouseX);
			int y = coordToTile(mouseY);

			/* Remove the object from its previous location in the board. If a
			   different object is located there (when the selected object just
			   spawns, for example), don't remove it
			 */
			if((x >= 0 && y >= 0 && x < board.length && y < board[x].length)
					&& board[x][y] == selector.getObstacle()) { // Note: Purposefully comparing references

				if(board[x][y] != null)
					System.out.println("Obj removed from (" + x + "," + y + ")");
				board[x][y] = null;
			}

		} else if (!input.didTertiary() && selector.isSelected()) {
			Obstacle deselected = selector.deselect();

			if(deselected != null) {

				// The tile indices
				int x = coordToTile(deselected.getX());
				int y = coordToTile(deselected.getY());

				// TODO: remove destroyed bodies from the pooled list, if we do that

				// Remove this body if it's out of bounds
				if (x < 0 || y < 0 || x >= board.length || y >= board[0].length) {
					deselected.markRemoved(true);

					// Add the body to the board array and set the position
				} else {
					// Remove the replaced body if necessary
					if (board[x][y] != null) {
						System.out.println("Obj removed at (" + x + "," + y + ")");
						board[x][y].markRemoved(true);
					}

					System.out.println("Obj placed at (" + x + "," + y + ")");
					deselected.setPosition(tileToCoord(x), tileToCoord(y));
					board[x][y] = deselected;

					// Update water tile images
					updateWaterTile(x, y);
					updateWaterTile(x - 1, y);
					updateWaterTile(x + 1, y);
					updateWaterTile(x, y - 1);
					updateWaterTile(x, y + 1);
				}
			}
		} else {
			selector.moveTo(mouseX, mouseY);
		}

		// Spawn a new object if a spawner was clicked
		Obstacle obj = spawnList.update(camTarget);
		if(obj != null) {
			addObject(obj);
			selector.select(mouseX, mouseY);
		}
	}

	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for the level designer. It does
	 * not handle collisions, as those are managed by the parent class WorldController.
	 * This method is called after input is read, but before collisions are resolved.
	 * The very last thing that it should do is apply forces to the appropriate objects.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void update(float dt) {
		// Move an object if touched
		InputController input = InputController.getInstance();

		// Update the camera position
		camTarget.add(CAMERA_SPEED * input.getHorizontal(), CAMERA_SPEED * input.getVertical());

		updateSelector();

		if(input.didPrimary()) {
			//change wall texture that is currently selected at mouse location
			if(selector.isSelected()) {
				// Get the selection, then remove it from the selector
				Obstacle selection = selector.getObstacle();
				if(selection.getName() == "wall") {
					((BoxObstacle)selection).setWall(((BoxObstacle)selection).wall+1);
				}
			}
		}
		if(input.didSecondary()) {
			//change wall texture that is currently selected at mouse location
			if(selector.isSelected()) {
				// Get the selection, then remove it from the selector
				Obstacle selection = selector.getObstacle();
				if(selection.getName() == "wall") {
					((BoxObstacle)selection).setWall(((BoxObstacle)selection).wall-1);
				}
			}
		}

		if(input.didDelete()) {
			// Remove what is currently selected at the mouse location
			if(selector.isSelected()) {
				// Get the selection, then remove it from the selector
				Obstacle selection = selector.getObstacle();
				selector.deselect();

				selection.markRemoved(true);
			}
		}
		if(input.didSave()) {

			System.out.println();

//			// TODO: COMBINE THIS WITH InputController SOMEHOW!
//			Gdx.input.getTextInput(new Input.TextInputListener() {
//				@Override
//				public void input (String levelName) {
//					System.out.println("Saving level as levels/" + levelName + ".lvl");
//					save("levels/" + levelName + ".lvl");
//				}
//
//				@Override
//				public void canceled () {
//					System.out.println("Cancelling save");
//				}
//			}, "Input custom level name", "custom", "");

//			save("custom.lvl");
		}

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

		SpiritModel spirit = null;
		ArrayList<HostModel> hostList = new ArrayList<>();
		ArrayList<BoxObstacle> obstacleList = new ArrayList<>();
		for(Obstacle obj : objects) {
			if(!obj.inGame) {
				continue;
			}

			if (obj instanceof SpiritModel) {
				spirit = (SpiritModel)obj;
				// Spirit is already saved in a field
			} else if (obj instanceof HostModel) {
				hostList.add((HostModel)obj);
			} else if (obj instanceof BoxObstacle) {
				obstacleList.add((BoxObstacle)obj);
			}
		}

		// For now, until the types used for levels are fixed
		BoxObstacle[] obstacleArray = new BoxObstacle[obstacleList.size()];
		obstacleList.toArray(obstacleArray);

		// TODO: what if spirit is null

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