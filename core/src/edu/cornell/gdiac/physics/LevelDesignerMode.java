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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.physics.host.FootPrintModel;
import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.obstacle.*;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.SoundController;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Gameplay specific controller for the ragdoll fishtank.
 * <p>
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop, which
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class LevelDesignerMode extends WorldController {
    /**
     * Default maximum charge of golems
     */
    public static final int MAX_CHARGE_CAPACITY = 800;
    /**
     * Default minimum charge of golem
     */
    public static final int MIN_CHARGE_CAPACITY = 0;

    /**
     * Texture file for mouse crosshairs
     */
    private static final String CROSS_FILE = "shared/crosshair.png";
    /**
     * Texture file for watery foreground
     */
    private static final String FOREG_FILE = "shared/foreground.png";
    /**
     * Texture file for background image
     */
    private static final String BACKG_FILE = "shared/background.png";
    /**
     * Texture file for host footprints
     */
    private static final String FOOTPRINT_FILE = "host/footprints.png";

    /**
     * Speed for changing camera position
     */
    private static final float CAMERA_SPEED = 5.f;

    /**
     * Width of each tile, in box2D coordinates
     */
    private static final float TILE_WIDTH = 2.f;

    /**
     * The maximum width and height of the board, in tile coordinates
     */
    private static final int MAX_BOARD_TILES = 127;

    /**
     * Texture asset for mouse crosshairs
     */
    private TextureRegion crosshairTexture;
    /**
     * Texture asset for background image
     */
    private TextureRegion backgroundTexture;
    /**
     * Texture asset for foreground
     */
    private TextureRegion foregroundTexture;
    /**
     * Texture asset for footprint
     */
    private TextureRegion footprintTexture;

    /**
     * The level to be loaded in reset()
     */
    private int currentLevel = 0;
    /**
     * A boolean indicating if the board should be reloaded from the file
     */
    private boolean loadBoard = true;

	// For the decorative roots
	/** The index of the left most root in the decorative roots array */
	private static final int LEFT_LEFT_ROOT_INDEX = 0;
	/** The index of the 2nd most left root in the decorative roots array */
	private static final int MID_LEFT_ROOT_INDEX = 1;
	/** The index of the 2nd most right root in the decorative roots array */
	private static final int MID_RIGHT_ROOT_INDEX = 2;
	/** The index of the right most root in the decorative roots array */
	private static final int RIGHT_RIGHT_ROOT_INDEX = 3;
	/** The number of decorative root tiles */
	private static final int NUM_DECORATIVE_ROOTS = 4;

	private boolean fromCustom = false;

	private boolean newLevel = false;

	/** Track asset loading from all instances and subclasses */
	private AssetState assetState = AssetState.EMPTY;

    /**
     * The level that is populated and used for saving
     */
    private Level level;

    /**
     * The collection of spawning objects, for making new game elements
     */
    private SpawnerList spawnList;

    /**
     * The camera target
     */
    private Vector2 camTarget;
    /**
     * The camera position
     */
    private Vector2 camPos;

    /**
     * Intermediate vector used for arithmetic
     */
    private Vector2 cache;

    /**
     * The board storing the level in a tile based system
     */
    private Board board;

    /**
     * A reference to the last golem placed, for placing instructions
     */
    private HostModel lastGolem;

    /**
     * A boolean to determine if you are currently placing instructions
     */
    private boolean instructionMode;

    /**
     * A cached arraylist to represent the instruction list
     */
    private ArrayList<Vector2> instructionListCache;

    /**
     * A cached vector2 to hold one instruction
     */
    private Vector2 instructionCache;

    /**
     * A list of footprints for instruction visuals
     */
    private ArrayList<FootPrintModel> footprints;

    /**
     * A list of the golems, for purposes of instructions
     */
    private ArrayList<HostModel> golems;

	public String levelName;

	/** If a selection is currently happening. Even if nothing is selected by
	 * the object selector, this will be true until the mouse is released, and
	 * it prevent another object from being picked up */
	private boolean selecting;

    PooledList<BorderEdge> edges;
    PooledList<BorderCorner> corners;

	/** An array of the decorative tiles on the board
	 *
	 * Index 0 is the upper left corner
	 * Index 1 is just right of the upper left corner
	 * Index 2 is just left of the upper right corner
	 * Index 3 is the upper right corner
	 */
	public BoxObstacle[] decorativeRoots;

    public enum Corner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public class CornerObstacle extends BoxObstacle {
        public CornerObstacle(TextureRegion tex, Corner corner) {
            super(Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
            this.corner = corner;

			setTexture(tex);
			setDrawScale(scale);
//			setSX(1);
//			setSY(1);
			setBodyType(BodyDef.BodyType.StaticBody);
			setSensor(true);
			setName("corner");
		}

        public final Corner corner;
    }


//	private int topBorder;
//	private int bottomBorder;
//	private int leftBorder;
//	private int rightBorder;
//
//	private int initialBottomBorder;
//	private int initialLeftBorder;

	private CornerObstacle topLeft;
	private CornerObstacle topRight;
	private CornerObstacle bottomLeft;
	private CornerObstacle bottomRight;

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
		manager.load(FOOTPRINT_FILE, Texture.class);
		assets.add(FOOTPRINT_FILE);

		super.preLoadContent(manager);
	}

	public void setLoadBoard(boolean b){ loadBoard = b;}

	public void setFromCustom(boolean b){ fromCustom = b;}

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
		footprintTexture = createTexture(manager, FOOTPRINT_FILE, false);

		super.loadContent(manager);
		assetState = AssetState.COMPLETE;
	}

	/** The new lessened gravity for this world */
	private static final float WATER_GRAVITY = -0.25f;

	/** Mouse selector to move game objects */
	private ObstacleSelector selector;

	/**
	 * Creates and initialize a new instance of the level designer

	 */
	public LevelDesignerMode() {
		super(DEFAULT_WIDTH,DEFAULT_HEIGHT,WATER_GRAVITY);
		setDebug(false);
		setComplete(false);
		setFailure(false);

		// No need to render HUD in level designer
		renderHUD = false;

		camTarget = new Vector2();
		camPos = new Vector2();

		cache = new Vector2();

		board = new Board(MAX_BOARD_TILES, MAX_BOARD_TILES);
		decorativeRoots = new BoxObstacle[NUM_DECORATIVE_ROOTS];
		lastGolem = null;
		instructionListCache = new ArrayList<Vector2>();
		instructionMode = false;
		instructionCache = new Vector2();
		footprints = new ArrayList<FootPrintModel>();
		golems = new ArrayList<HostModel>();
		if(!loadBoard){
			level = new Level();
		}

		File folder = new File("levels");
		levels = new ArrayList<File>(Arrays.asList(folder.listFiles(Constants.filenameFilter)));
		Collections.sort(levels);
	}

	/**
	 * Gets the level name, using the current level number
	 *
	 * @return A string representing the level name
	 */
	public String getLevelName() {
		if(levelName != null){
			return levelName;
		}
		if(!fromCustom && !newLevel) {
			levelName = "levels/" + levels.get(currentLevel).getName();
		}else if(newLevel) {
			levelName = "Custom/c" + currentLevel + ".lvl";
		}else{
			levelName = "Custom/" + levels.get(currentLevel).getName();
		}
		return levelName;
	}

	/**
	 * Gets the current width of the screen in tiles, rounded down to the
	 * nearest tile index.
	 */
	public int screenTileWidth() {
		return (int)(canvas.getWidth() / scale.x / TILE_WIDTH);
	}

	/**
	 * Gets the current height of the screen in tiles, rounded down to the
	 * nearest tile index.
	 */
	public int screenTileHeight() {
		return (int)(canvas.getHeight() / scale.y / TILE_WIDTH);
	}

	/**
	 *  Sets the number of the level that is loaded in the reset() function
	 *
	 * @param l The level number
	 */
	public void setCurrentLevel(int l) {
		currentLevel = l;
	}

	/**
	 * Refreshes the footprints on the screen to match the instructions of the golems
	 */
	private void refreshFootprints() {
		footprints.clear();
		for (Obstacle ob : objects) {
			if (ob instanceof HostModel) {
				Vector2[] list = ((HostModel)ob).getInstructionList();
				if (list != null) {
					for (Vector2 instr : list) {
						FootPrintModel ft = new FootPrintModel(footprintTexture, new Vector2(instr.x * scale.x, instr.y * scale.y));
						footprints.add(ft);
					}
				}
			}
		}
	}

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {

		if(currentLevel == -1){
			newLevel = true;
		}else{
			newLevel = false;
		}

		if(fromCustom || newLevel){
			File folder = new File("Custom");
			levels = new ArrayList<File>(Arrays.asList(folder.listFiles(Constants.filenameFilter)));
			Collections.sort(levels);
		}else{
			File folder = new File("levels");
			levels = new ArrayList<File>(Arrays.asList(folder.listFiles(Constants.filenameFilter)));
			Collections.sort(levels);
		}

	    refreshFootprints();

		Vector2 gravity = new Vector2(world.getGravity());

		// The objects should be sensors
		factory.makeSensors = true;

		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		world.dispose();

		// Clear the board
		board.clear(false);

		FileHandle f = new FileHandle("out.lvl");

		world = new World(gravity,false);

//		level = loader.loadLevel(f);

        setComplete(false);
        setFailure(false);

        if (loadBoard) {
//			bottomBorder = 0;
//			leftBorder = 0;

//			initialBottomBorder = bottomBorder

            // Not checking to ensure that these borders are within the array
            // bounds because no screen should be so large that the max board
            // size of 127x127 fits inside the screen.

            // Populate the level once the board boundaries are set up
            populateLevel();
        } else {
            // Reset the level size based on the size of the screen
			board.reset(screenTileWidth(), screenTileHeight());
			level = new Level();
			currentLevel = levels.size();

			setBordersAndUpdateTerrain();
			loadBoard = true;
        }
		getLevelName();

        // After populateLevel(), when the borders are set from the loaded level
        camTarget.set(scale.x * Constants.TILE_WIDTH * (board.getWidth()) / 2.f,
                scale.y * Constants.TILE_HEIGHT * (board.getHeight()) / 2.f);

        // Update the canvas here so that spawnerList gets the correct cam pos
        canvas.setCamTarget(camTarget);
        camPos.set(canvas.updateCamera());

        // Setup the spawner list
        Wall boxSpawn = factory.makeWall(0.f, 0.f);
        addObject(boxSpawn);

        WaterTile waterSpawn = factory.makeWater(0.f, 0.f);
        addObject(waterSpawn);

        SandTile sandSpawn = factory.makeSand(0.f, 0.f);
        addObject(sandSpawn);

        HostModel hostSpawn = factory.makeSmallHost(0.f, 0.f);
        addObject(hostSpawn);

//		SpiritModel spiritSpawn = factory.makeSpirit(0.f, 0.f);
//		addObject(spiritSpawn);

        HostModel pedestalSpawn = factory.makePedestal(0.f, 0.f);
        addObject(pedestalSpawn);

        OscWall oscWallSpawn = factory.makeOscWall(0.f, 0.f);
        addObject(oscWallSpawn);

        EnergyPillar energyPillarSpawn = factory.makeEnergyPillar(0.f, 0.f);
        addObject(energyPillarSpawn);

        spawnList = new SpawnerList(canvas, scale, camPos);

        spawnList.addSpawner(boxSpawn, new SpawnerList.CallbackFunction() {
            public Obstacle makeObject(float x, float y, Obstacle lastCreated) {
                Wall boxSpawn = factory.makeWall(x, y);
                return boxSpawn;
            }
        });

        spawnList.addSpawner(waterSpawn, new SpawnerList.CallbackFunction() {
            public Obstacle makeObject(float x, float y, Obstacle lastCreated) {
                return factory.makeWater(x, y);
            }
        });

        spawnList.addSpawner(sandSpawn, new SpawnerList.CallbackFunction() {
            public Obstacle makeObject(float x, float y, Obstacle lastCreated) {
                return factory.makeSand(x, y);
            }
        });

        spawnList.addSpawner(hostSpawn, new SpawnerList.CallbackFunction() {
            public Obstacle makeObject(float x, float y, Obstacle lastCreated) {
                return factory.makeSmallHost(x, y);
            }
        });

        spawnList.addSpawner(pedestalSpawn, new SpawnerList.CallbackFunction() {
            public Obstacle makeObject(float x, float y, Obstacle lastCreated) {
                return factory.makePedestal(x, y);
            }
        });

        spawnList.addSpawner(oscWallSpawn, new SpawnerList.CallbackFunction() {
            @Override
            public Obstacle makeObject(float x, float y, Obstacle lastCreated) {
                return factory.makeOscWall(x, y);
            }
        });

        spawnList.addSpawner(energyPillarSpawn, new SpawnerList.CallbackFunction() {
            public Obstacle makeObject(float x, float y, Obstacle lastCreated) {
                return factory.makeEnergyPillar(x, y);
            }
        });

//		spawnList.addSpawner(edgeSpawn, new SpawnerList.CallbackFunction() {
//			public Obstacle makeObject(float x, float y, Obstacle lastCreated) {
//				return factory.makeBorder(x, y);
//			}
//		});

        selector = new ObstacleSelector(world);
        selector.setTexture(crosshairTexture);
        selector.setDrawScale(scale);


        // Add the corner objects

        topLeft = new CornerObstacle(crosshairTexture, Corner.TOP_LEFT);
        topLeft.inGame = false;
        topLeft.inHUD = true;
        addObject(topLeft);

        topRight = new CornerObstacle(crosshairTexture, Corner.TOP_RIGHT);
        topRight.inGame = false;
        topRight.inHUD = true;
        addObject(topRight);

        bottomLeft = new CornerObstacle(crosshairTexture, Corner.BOTTOM_LEFT);
        bottomLeft.inGame = false;
        bottomLeft.inHUD = true;
        addObject(bottomLeft);

        bottomRight = new CornerObstacle(crosshairTexture, Corner.BOTTOM_RIGHT);
        bottomRight.inGame = false;
        bottomRight.inHUD = true;
        addObject(bottomRight);

        updateCornerPositions();

        // Properly set the borders to use up the center of the board array, so
        // it can be expanded in all directions

//		topBorder =    (MAX_BOARD_TILES / 2) + (screenTileWidth()  / 2);
//		bottomBorder = (MAX_BOARD_TILES / 2) - (screenTileWidth()  / 2);
//
//		leftBorder =   (MAX_BOARD_TILES / 2) - (screenTileHeight() / 2);
//		rightBorder =  (MAX_BOARD_TILES / 2) + (screenTileHeight() / 2);
    }

    /**
     * Updates the positions of the corners, based on the game borders. This
     * should be called whenever the borders are changed.
     */
    private void updateCornerPositions() {
        float top = yTileToCoord(board.getTopBorder()) - (Constants.TILE_WIDTH / 2.f);
        float bottom = yTileToCoord(board.getBottomBorder()) - (Constants.TILE_WIDTH / 2.f);

        float left = xTileToCoord(board.getLeftBorder()) - (Constants.TILE_WIDTH / 2.f);
        float right = xTileToCoord(board.getRightBorder()) - (Constants.TILE_WIDTH / 2.f);

        topLeft.setPosition(left, top);
        topRight.setPosition(right, top);
        bottomLeft.setPosition(left, bottom);
        bottomRight.setPosition(right, bottom);
    }

    /**
     * Lays out the game geography.
     */
    private void populateLevel() {

        // TODO: Change this with GamePlayController!!
        FileHandle levelToLoad;

        System.out.println(getLevelName());

		if(fromCustom || newLevel) {
			levelToLoad = Gdx.files.local(getLevelName());
		}else{
			levelToLoad = Gdx.files.internal(getLevelName());
		}
        //levelToLoad = Gdx.files.internal("levels/custom" + currentLevel + ".lvl");

       level = loader.loadLevel(levelToLoad);

		dimensions = level.dimensions;
		lowerLeft.setZero();
		board.reset((int)(dimensions.x / Constants.TILE_WIDTH), (int)(dimensions.y / Constants.TILE_HEIGHT));
        dimensions.x = board.getWidth();
        dimensions.y = board.getHeight();
        lowerLeft.x = board.getLeftOffset();
        lowerLeft.y = board.getBottomOffset();

//		System.out.println("dimensions: " + dimensions + ", lowerLeft: " + lowerLeft);

        for (Obstacle obj : level.walls) {
            if (board.addNewObstacle(obj)) {
                addObject(obj);
            }
        }
        for (Obstacle obj : level.hosts) {
            if (board.addNewObstacle(obj)) {
                addObject(obj);
            }
        }
        for (Obstacle obj : level.water) {
            if (board.addNewObstacle(obj)) {
                addObject(obj);
            }
        }
        for (Obstacle obj : level.sand) {
            if (board.addNewObstacle(obj)) {
                addObject(obj);
            }
        }
        for (Obstacle obj : level.borderEdges) {
            if (board.addNewObstacle(obj)) {
                addObject(obj);
            }
        }
        for (Obstacle obj : level.borderCorners) {
            if (board.addNewObstacle(obj)) {
                addObject(obj);
            }
        }
        for (Obstacle obj : level.energyPillars) {
            if (board.addNewObstacle(obj)) {
                addObject(obj);
            }
        }

        for (Obstacle obj : level.oscWalls) {
            if(board.addNewObstacle(obj)) {
                addObject(obj);
            }
        }

        if (board.addNewObstacle(level.pedestal)) {
            addObject(level.pedestal);
        }
        if (board.addNewObstacle(level.start)) {
            addObject(level.start);
        }

		// Initialize the decorative roots if they haven't yet been
		for(int i = 0; i < NUM_DECORATIVE_ROOTS; i++) {
			// Will be repositioned by setBordersAndUpdateTerrain()
			if(decorativeRoots[i] != null) {
				decorativeRoots[i].markRemoved(true);
			}
			decorativeRoots[i] = factory.makeDecorativeRoot(0, 0, i);
			addObject(decorativeRoots[i]);
		}

		setBordersAndUpdateTerrain();
	}

    /**
     * Gets the x index of tile index that an x coordinate is in
     *
     * @param coord The box2D x coordinate
     * @return The x value of the tile index
     */
    private int xCoordToTile(float coord) {
        return Math.round((coord - (TILE_WIDTH / 2.f)) / TILE_WIDTH) + board.getInitialLeftBorder();
    }

    /**
     * Gets the y index of tile index that an y coordinate is in
     *
     * @param coord The box2D y coordinate
     * @return The y value of the tile index
     */
    private int yCoordToTile(float coord) {
        return Math.round((coord - (TILE_WIDTH / 2.f)) / TILE_WIDTH) + board.getInitialBottomBorder();
    }

    /**
     * Gets the x coordinate of the center of a tile
     *
     * @param index The x value of the tile index
     * @return The box2D x coordinate of the tile center
     */
    private float xTileToCoord(int index) {
        return xTileToCoord(index, false);
    }

    /**
     * Gets the y coordinate of the center of a tile
     *
     * @param index The y value of the tile index
     * @return The box2D y coordinate of the tile center
     */
    private float yTileToCoord(int index) {
        return yTileToCoord(index, false);
    }

    /**
     * Gets the x coordinate of a tile
     *
     * @param index  The x value of the tile index
     * @param corner If true, gets the lower left corner coordinate. Otherwise,
     *               gets the center coordinate
     * @return The box2D x coordinate of the tile
     */
    private float xTileToCoord(int index, boolean corner) {
        return ((index - board.getInitialLeftBorder()) + 0.5f) * TILE_WIDTH - (corner ? TILE_WIDTH / 2.f : 0);
    }

    /**
     * Gets the y coordinate of a tile
     *
     * @param index  The y value of the tile index
     * @param corner If true, gets the lower left corner coordinate. Otherwise,
     *               gets the center coordinate
     * @return The box2D y coordinate of the tile corner
     */
    private float yTileToCoord(int index, boolean corner) {
        return ((index - board.getInitialBottomBorder()) + 0.5f) * TILE_WIDTH - (corner ? TILE_WIDTH / 2.f : 0);
    }

    /**
     * Updates the texture for water tile at index x, y in the board based on
     * its surroundings (if they are water or ground)
     * <p>
     * If the tile is not a water tile, or if the tile is out of bounds, nothing
     * happens.
     *
     * @param x The x index in the board of the tile to update
     * @param y The y index in the board of the tile to update
     * @return True if a water tile was updated, false otherwise
     */
    private boolean updateTerrainTile(int x, int y) {
        if (x < board.getLeftBorder() || y < board.getBottomBorder() || x >= board.getRightBorder() || y >= board.getTopBorder() || !(board.get(x, y) instanceof Terrain)) {
            return false;
        }

        Terrain terrain = (Terrain) board.get(x, y);

        Obstacle above = null, below = null, left = null, right = null;
        boolean hasGroundAbove = false, hasGroundBelow = false, hasGroundLeft = false, hasGroundRight = false;
        boolean upLeftCorner = false, upRightCorner = false, downLeftCorner = false, downRightCorner = false;

        // Set the adjacent tiles if they are in bounds, and if so, check if a
        // ground border is needed
        if (y + 1 < board.getTopBorder()) {
            above = board.get(x, y + 1);
            hasGroundAbove = !(terrain.continuousWithTile(above));
        }
        if (y - 1 >= board.getBottomBorder()) {
            below = board.get(x, y - 1);
            hasGroundBelow = !(terrain.continuousWithTile(below));
        }
        if (x - 1 >= board.getLeftBorder()) {
            left = board.get(x - 1, y);
            hasGroundLeft = !(terrain.continuousWithTile(left));
        }
        if (x + 1 < board.getRightBorder()) {
            right = board.get(x + 1, y);
            hasGroundRight = !(terrain.continuousWithTile(right));
        }

        // If the corner should be drawn for each side, based on if adjacent
        // water tiles have ground
        if (terrain.continuousWithTile(above) && terrain.continuousWithTile(left)) {
            upLeftCorner = !terrain.continuousWithTile(board.get(x - 1, y + 1));
        }
        if (terrain.continuousWithTile(above) && terrain.continuousWithTile(right)) {
            upRightCorner = !terrain.continuousWithTile(board.get(x + 1, y + 1));
        }
        if (terrain.continuousWithTile(below) && terrain.continuousWithTile(left)) {
            downLeftCorner = !terrain.continuousWithTile(board.get(x - 1, y - 1));
        }
        if (terrain.continuousWithTile(below) && terrain.continuousWithTile(right)) {
            downRightCorner = !terrain.continuousWithTile(board.get(x + 1, y - 1));
        }

        ((Terrain) board.get(x, y)).setFrame(hasGroundAbove, hasGroundBelow, hasGroundLeft, hasGroundRight, true);
        ((Terrain) board.get(x, y)).setCorners(upLeftCorner, upRightCorner, downLeftCorner, downRightCorner);

        return true;
    }

    /**
     * Updates the texture for water tile at and around (x, y) in the board.
     * this function calls updateWaterTile
     * <p>
     * If the tile is not a water tile, or if the tile is out of bounds, nothing
     * happens.
     *
     * @param x The x index in the board of the tile center to update
     * @param y The y index in the board of the tile center to update
     */
    private void updateTerrainAroundRegion(int x, int y) {
        updateTerrainTile(x, y);

        updateTerrainTile(x - 1, y);
        updateTerrainTile(x + 1, y);
        updateTerrainTile(x, y - 1);
        updateTerrainTile(x, y + 1);

        updateTerrainTile(x - 1, y + 1);
        updateTerrainTile(x + 1, y + 1);
        updateTerrainTile(x - 1, y - 1);
        updateTerrainTile(x + 1, y - 1);
    }

    /**
     * If the given tile is either a Wall, a BorderEdge, or a BorderCorner
     *
     * @param o The object to check
     */
    public boolean isWallOrBorder(Obstacle o) {
        return o instanceof Wall || o instanceof BorderEdge || o instanceof BorderCorner;
    }

    /**
     * Updates the texture for wall tile at index x, y in the board based on
     * its surroundings (if they are wall or not)
     * <p>
     * If the tile is not a wall, or if the tile is out of bounds, nothing
     * happens.
     *
     * @param x The x index in the board of the tile to update
     * @param y The y index in the board of the tile to update
     * @return True if a wall tile was updated, false otherwise
     */
    private boolean updateWallTile(int x, int y) {
        if (x < board.getLeftBorder() || y < board.getBottomBorder() || x >= board.getRightBorder() || y >= board.getTopBorder() || !(board.get(x, y) instanceof Wall)) {
            return false;
        }

        Obstacle above = null, below = null, left = null, right = null;

        // If the walls exist
        boolean wallAbove = false, wallBelow = false, wallLeft = false,
                wallRight = false, wallLowerLeft = false, wallLowerRight = false;
        // If the walls are top walls (not a front facing wall)
        boolean belowIsTop = false, leftIsTop = false, rightIsTop = false,
                lowerLeftIsTop = false, lowerRightIsTop = false;

        // Set the adjacent tiles if they are in bounds, and if so, check if a
        // ground border is needed
        if (y + 1 < board.getTopBorder()) {
            above = board.get(x, y + 1);
            wallAbove = above instanceof Wall;
        }
        if (y - 1 >= board.getBottomBorder()) {
            below = board.get(x, y - 1);
            // NOTE:
            // ONLY FOR BELOW DOES IT COUNT AS A WALL IF IT IS A BORDER!
            // This is so that there is not a front wall next to the border,
            // because a front wall has a smaller hitbox and would mean there
            // would be a tunnel between the wall and the edge
            wallBelow = isWallOrBorder(below);
            if (below instanceof Wall) {
                belowIsTop = !((Wall) below).isFrontWall();
            }
        }
        if (x - 1 >= board.getLeftBorder()) {
            left = board.get(x - 1, y);
            wallLeft = left instanceof Wall;
            if (wallLeft) {
                leftIsTop = !((Wall) left).isFrontWall();
            }
        }
        if (x + 1 < board.getRightBorder()) {
            right = board.get(x + 1, y);
            wallRight = right instanceof Wall;
            if (wallRight) {
                rightIsTop = !((Wall) right).isFrontWall();
            }
        }

//		// If the corner should be drawn for each side, based on if adjacent
//		// sand tiles have ground
//		if (above instanceof Wall && left instanceof Wall) {
//			upLeftCorner = !(board[x - 1][y + 1] instanceof Wall);
//		}
//		if (above instanceof Wall && right instanceof Wall) {
//			upRightCorner = !(board[x + 1][y + 1] instanceof Wall);
//		}
		if (below instanceof Wall && left instanceof Wall) {
			wallLowerLeft = board.get(x - 1, y - 1) instanceof Wall;
			if(wallLowerLeft) {
				lowerLeftIsTop = !((Wall)board.get(x - 1, y - 1)).isFrontWall();
			}
		}
		if (below instanceof Wall && right instanceof Wall) {
			wallLowerRight = board.get(x + 1,y - 1) instanceof Wall;
			if(wallLowerRight) {
				lowerRightIsTop = !((Wall)board.get(x + 1, y - 1)).isFrontWall();
			}
		}

		// Switch up the alt boolean every tile
		((Wall)board.get(x, y)).setFrame(wallAbove, wallBelow, wallLeft, wallRight,
				belowIsTop, leftIsTop, rightIsTop,
				lowerLeftIsTop, lowerRightIsTop, x, y);

		return true;
	}

	/**
	 * Updates the texture for sand tile at and around (x, y) in the board.
	 * this function calls updateSandTile
	 *
	 * If the tile is not a sand tile, or if the tile is out of bounds, nothing
	 * happens.
	 *
	 * @param x The x index in the board of the tile center to update
	 * @param y The y index in the board of the tile center to update
	 */
	private void updateWallAroundRegion(int x, int y) {
		// Update the center tile
		updateWallTile(x, y);

		// Update adjacent tiles
		updateWallTile(x - 1, y);
		updateWallTile(x + 1, y);
		updateWallTile(x, y - 1);
		updateWallTile(x, y + 1);

		// Update diagonal tiles
		updateWallTile(x - 1, y + 1);
		updateWallTile(x + 1, y + 1);
		updateWallTile(x - 1, y - 1);
		updateWallTile(x + 1, y - 1);

		// Update tiles in the row two above, in case the above tile is no
		// longer, or has become, a front wall
		updateWallTile(x - 1, y + 2);
		updateWallTile(x, y + 2);
		updateWallTile(x + 1, y + 2);
	}

	/**
	 * Processes the object deselected
	 */
	private void processBorderChange(CornerObstacle corner) {
		board.processBorderChange(corner.corner, corner.getX(), corner.getY());

		setBordersAndUpdateTerrain();

		dimensions.set(Constants.TILE_WIDTH * (board.getWidth()), Constants.TILE_HEIGHT * (board.getHeight()));
		lowerLeft.set(TILE_WIDTH * (board.getLeftOffset()), TILE_WIDTH * (board.getBottomOffset()));

		updateCornerPositions();

	}

	/**
	 * Sets the borders around the edge of the board with the correct
	 * orientations. Also updates each terrain tile (water, sand, and wall)
	 */
	private void setBordersAndUpdateTerrain() {
		// Update terrain tiles based on the new borders and add walls
		for(int i = board.getLeftBorder(); i < board.getRightBorder(); i++) {
			for(int j = board.getBottomBorder(); j < board.getTopBorder(); j++) {
				float xCoord = xTileToCoord(i);
				float yCoord = yTileToCoord(j);

				// Bottom left corner
				if(i == board.getLeftBorder() && j == board.getBottomBorder()) {
					board.set(null, i, j);
					BorderCorner border = factory.makeBorderCorner(xCoord, yCoord, BorderCorner.Corner.BOTTOM_LEFT);

					board.addNewObstacle(border);
					addObject(border);

				// Bottom right corner
				} else if(i == board.getRightBorder() - 1 && j == board.getBottomBorder()) {
					board.set(null, i, j);
					BorderCorner border = factory.makeBorderCorner(xCoord, yCoord, BorderCorner.Corner.BOTTOM_RIGHT);

					board.addNewObstacle(border);
					addObject(border);

				// Top left corner
				} else if(i == board.getLeftBorder() && j == board.getTopBorder() - 1) {
					board.set(null, i, j);
					BorderCorner border = factory.makeBorderCorner(xCoord, yCoord, BorderCorner.Corner.TOP_LEFT);

					board.addNewObstacle(border);
					addObject(border);

				// Top right corner
				} else if(i == board.getRightBorder() - 1 && j == board.getTopBorder() - 1) {
					board.set(null, i, j);
					BorderCorner border = factory.makeBorderCorner(xCoord, yCoord, BorderCorner.Corner.TOP_RIGHT);

					board.addNewObstacle(border);
					addObject(border);

				// Bottom edge
				} else if(j == board.getBottomBorder()) {
					board.set(null, i, j);
					BorderEdge border = factory.makeBorder(xCoord, yCoord, BorderEdge.Side.BOTTOM);

					// Indicate that the edge is next to the left or right side
					if(i - board.getLeftBorder() == 1) {
						border.setNextToSide(1, BorderEdge.Side.LEFT);
					} else if((board.getRightBorder() - 1) - i == 1) {
						border.setNextToSide(1, BorderEdge.Side.RIGHT);
					}
					board.addNewObstacle(border);
					addObject(border);

				// Left edge
				} else if(i == board.getLeftBorder()) {
					board.set(null, i, j);
					BorderEdge border = factory.makeBorder(xCoord, yCoord, BorderEdge.Side.LEFT);
					if(j - board.getBottomBorder() == 1) {
						border.setNextToSide(1, BorderEdge.Side.BOTTOM);
					} else if((board.getTopBorder() - 1) - j == 1) {
						border.setNextToSide(1, BorderEdge.Side.TOP);
					}
					board.addNewObstacle(border);
					addObject(border);

				// Right edge
				} else if(i == board.getRightBorder() - 1) {
					board.set(null, i, j);
					BorderEdge border = factory.makeBorder(xCoord, yCoord, BorderEdge.Side.RIGHT);
					if(i - board.getBottomBorder() == 1) {
						border.setNextToSide(1, BorderEdge.Side.BOTTOM);
					} else if((board.getTopBorder() - 1) - i == 1) {
						border.setNextToSide(1, BorderEdge.Side.TOP);
					}
					board.addNewObstacle(border);
					addObject(border);

				// Top edge
				} else if(j == board.getTopBorder() - 1) {
					board.set(null, i, j);
					BorderEdge border = factory.makeBorder(xCoord, yCoord, BorderEdge.Side.TOP);
					if(i - board.getLeftBorder() <= 2) {
						border.setNextToSide(i - board.getLeftBorder(), BorderEdge.Side.LEFT);
					} else if((board.getRightBorder() - 1) - i <= 2) {
						border.setNextToSide((board.getRightBorder() - 1) - i, BorderEdge.Side.RIGHT);
					}
					board.addNewObstacle(border);
					addObject(border);

				// Everything else
				} else {
					// Remove any stray border pieces
					if(board.get(i, j) instanceof BorderEdge || board.get(i, j) instanceof BorderCorner) {
						board.set(null, i, j);
					}

					// If the location is next to an upper corner, add the
					// proper decorative tile
					if(j == board.getTopBorder() - 2) {
						// Furthest left on the board
						if(i == board.getLeftBorder() + 1) {
							decorativeRoots[LEFT_LEFT_ROOT_INDEX].setPosition(xCoord, yCoord);

						// 2nd furthest left on the board
						} else if(i == board.getLeftBorder() + 2) {
							decorativeRoots[MID_LEFT_ROOT_INDEX].setPosition(xCoord, yCoord);

						// 2nd furthest right on the board
						}
						if(i == board.getRightBorder() - 3) {
							decorativeRoots[MID_RIGHT_ROOT_INDEX].setPosition(xCoord, yCoord);

						// Furthest right on the board
						} else if(i == board.getRightBorder() - 2) {
							decorativeRoots[RIGHT_RIGHT_ROOT_INDEX].setPosition(xCoord, yCoord);
						}
					}

					updateTerrainAroundRegion(i, j);
					updateWallAroundRegion(i, j);
				}
			}
		}
	}

	/**
	 * Does the updating for the selector.
	 *
	 * This method sets picks up, moves, and drops off objects from the selector
	 * based on the mouse. It updates the board 2D array of tiles. It also
	 * handles updating the spawner and creating new objects from the spawner,
	 * which would be immediately picked up by the selector.
	 */
	private void updateSelector(boolean hasPed) {
		InputController input = InputController.getInstance();

		/* Offset the mouse based on the camera translation.

		   Half of the canvas is subtracted because when the board is centered,
		   camTarget is the middle of the board, not (0, 0), but there should be
		   no additional mouse offset.

		   Additionally, note that the mouse uses box2d coordinates, not screen
		   coordinates
		 */
        float mouseX = input.getMousePosition().x + (camPos.x - (canvas.getWidth() / 2.f)) / scale.x;
        float mouseY = input.getMousePosition().y + (camPos.y - (canvas.getHeight() / 2.f)) / scale.y;

		/* Only reset selecting if the mouse is released. Prevents selecting a
		   new object without releasing and clicking the mouse.
		 */
        if (!input.didTertiary()) {
            selecting = false;
        }

        // Process instructions
		if (lastGolem != null && input.didInstruction() && !instructionMode) {
			instructionMode = true;
			instructionListCache.clear();
			lastGolem.setInstructions(null);
			System.out.println("Instruction Mode");
			refreshFootprints();
		} else if (instructionMode && input.didInstruction()) {
			instructionMode = false;
			Vector2[] instructions = new Vector2[instructionListCache.size()];
			for (int i = 0; i < instructionListCache.size(); i++) {
				instructions[i] = instructionListCache.get(i);
			}
			if (instructionListCache.size() != 0) {
				lastGolem.setInstructions(instructions);
			}
			else {
				lastGolem.setInstructions(null);
			}
			refreshFootprints();
			System.out.println("Instructions saved to golem");
		}

		if (instructionMode && input.didLeftClick()) {
			int instructionTileX = xCoordToTile(mouseX);
			int instructionTileY = yCoordToTile(mouseY);
			float instructionRawX = xTileToCoord(instructionTileX);
			float instructionRawY = yTileToCoord(instructionTileY);

			FootPrintModel fp = new FootPrintModel(footprintTexture, new Vector2(instructionRawX * scale.x,instructionRawY*scale.y));
			footprints.add(fp);
			super.setFootprints(footprints);
			Vector2 instruction = new Vector2(instructionRawX, instructionRawY);
			instructionListCache.add(instruction);
			System.out.println("Instruction placed");
		}

		// Spawn a new object if a spawner was clicked
		Obstacle spawnedObj = spawnList.update(camPos);

		if(spawnedObj != null) {
			// If the selected object is a pedestal then check if pedestal is already in the game
			if((spawnedObj.getName() == "pedestal" && !hasPed) || spawnedObj.getName() != "pedestal") {
				addObject(spawnedObj);
//				selector.select(mouseX, mouseY);
				selector.select(spawnedObj);
				selecting = true;
			}
		}

		if (!selecting && (input.didTertiary()) && !selector.isSelected()) {
			selector.select(mouseX, mouseY);
			selecting = true;

            // The tile indices
            int x = xCoordToTile(mouseX);
            int y = yCoordToTile(mouseY);

			/* Remove the object from its previous location in the board. If a
			   different object is located there (when the selected object just
			   spawns, for example), don't remove it
			 */
			if(x >= board.getLeftBorder() && y >= board.getBottomBorder() && x < board.getRightBorder() && y < board.getTopBorder()) {
				if(board.get(x, y) != null && board.get(x, y) == selector.getObstacle()) { // Note: Purposefully comparing references
					if(!board.removeFromBoard(x, y)) {
						System.out.println("Selected item not removed from board");
					}
				}

				// Update the surroundings after removing old obstacles
				updateTerrainAroundRegion(x, y);
				updateWallAroundRegion(x, y);
			}

		} else if (!input.didTertiary() && selector.isSelected()) {
			Obstacle deselected = selector.deselect();

			if(deselected instanceof CornerObstacle) {
				processBorderChange((CornerObstacle)deselected);
			} else if(deselected != null) {
				if (deselected instanceof HostModel) {
					System.out.println("Golem placed");
					lastGolem = (HostModel)deselected;
				}
				// The tile indices
				int x = xCoordToTile(deselected.getX());
				int y = yCoordToTile(deselected.getY());

				// TODO: remove destroyed bodies from the pooled list, if we do that

				if(board.set(deselected, x, y, false)) {
					// If the new object was deselected, move to the tile center
					deselected.setPosition(xTileToCoord(x), yTileToCoord(y));

					// Update terrain and walls around the new object
					updateTerrainAroundRegion(x, y);
					updateWallAroundRegion(x, y);
				} else {
					// If it was not added, it is outside the board and should
					// be removed
					deselected.markRemoved(true);
				}
			}
		} else {
			selector.moveTo(mouseX, mouseY);
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

		// TODO This is probably super inefficient but it does the job
		boolean hasPed = false;
		// looks for pedestal object in the game thats been placed on the board
		for(Obstacle obj : objects) {
			if (obj.getName() == "pedestal" && obj.inGame) {
				hasPed = true;
			}
		}


		if(input.didClear()) {
			loadBoard = false; // Remove everything from the board
			reset();
		}
		if(input.didReset()) {
			loadBoard = true; // Reset the board based on the level
			reset();
		}

		// Update the camera position
		camTarget.add(CAMERA_SPEED * input.getHorizontal(), CAMERA_SPEED * input.getVertical());

		canvas.setCamTarget(camTarget);
		camPos.set(canvas.updateCamera());

		dimensions.set(TILE_WIDTH * (board.getWidth()), TILE_WIDTH * (board.getHeight()));
		lowerLeft.set(TILE_WIDTH * (board.getLeftOffset()), TILE_WIDTH * (board.getBottomOffset()));

		updateSelector(hasPed);

		if (input.didPressUp() && selector.isSelected()) {
			Obstacle selection = selector.getObstacle();
			if(selector.getObstacle().getName() == "host") {
				if (((HostModel) selection).getCurrentCharge() < MAX_CHARGE_CAPACITY) {
					((HostModel) selection).setCurrentCharge(((HostModel) selection).getCurrentCharge() + 5);
					((HostModel) selection).setChargeStripFrame(((HostModel) selection).getCurrentCharge());
				}
			}
            else if(selector.getObstacle().getName() == "oscWall") {
                ((OscWall) selection).setGoingUp(false);
				((OscWall) selection).setVert(false);
				((OscWall) selection).setMainStrip(((OscWall) selection).isVert(), false);
            }
		}
		if (input.didPressDown() && selector.isSelected()) {
			Obstacle selection = selector.getObstacle();
			if(selector.getObstacle().getName() == "host") {
				if (((HostModel) selection).getCurrentCharge() > MIN_CHARGE_CAPACITY) {
					((HostModel) selection).setCurrentCharge(((HostModel) selection).getCurrentCharge() - 5);
					((HostModel) selection).setChargeStripFrame(((HostModel) selection).getCurrentCharge());
				}
			}
            else if(selector.getObstacle().getName() == "oscWall") {
                ((OscWall) selection).setGoingUp(true);
				((OscWall) selection).setVert(true);
				((OscWall) selection).setMainStrip(((OscWall) selection).isVert(), true);
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
            if (hasPed) {
                save(getLevelName());
                System.out.println("Saving level as: " + getLevelName());
            } else {
                System.out.println("Did not save level: no pedestal found");
            }
        }

        // If we use sound, we must remember this.
        SoundController.getInstance().update();
    }

    /**
     * Saves the current set up as a level
     * <p>
     * Requires: the level is valid, and thus has a pedestal
     *
     * @param levelName The name for the saved level
     */
    private void save(String levelName) {
        // This has to be made local instead of the default, which is "internal" and can't be
        // modified by a jar
        FileHandle f;
        if(fromCustom || newLevel) {
			f = Gdx.files.local(levelName);
		}else{
        	f = Gdx.files.internal(levelName);
		}

        // TODO: Make this not creating new objects by updating Level to use PooledList(?)

        SpiritModel spirit = null;
        ArrayList<BoxObstacle> waterList = new ArrayList<>();
        ArrayList<BoxObstacle> sandList = new ArrayList<>();
        ArrayList<HostModel> hostList = new ArrayList<>();
        ArrayList<Wall> wallList = new ArrayList<>();
        ArrayList<BorderEdge> borderEdgeList = new ArrayList<>();
        ArrayList<BorderCorner> borderCornerList = new ArrayList<>();
        ArrayList<EnergyPillar> energyPillarList = new ArrayList<>();
        ArrayList<OscWall> oscWallList = new ArrayList<>();
		ArrayList<BoxObstacle> decorativeList = new ArrayList<>();
        HostModel pedestal = null;

        //System.out.println(cache);

        for (Obstacle obj : objects) {
            if (!obj.inGame) {
                continue;
            }

            // To update the position of each object, offset its position by the
            // lower left offset. It has to be negated, because lower left is
            // initially the offset of the ground. To move objects in the
            // opposite direction (to be the same distance from the corner of
            // the ground), they must move in the other direction.
            cache.set(lowerLeft);
            cache.scl(-1.f);
            cache.add(obj.getPosition());

            obj.setPosition(cache);

            if (obj instanceof SpiritModel) {
                spirit = (SpiritModel) obj;
                // Spirit is already saved in a field
            } else if (obj instanceof HostModel && obj.getName() != "pedestal") {
                hostList.add((HostModel) obj);
            } else if ((obj.getName()) == "pedestal") {
                pedestal = (HostModel) obj;
            } else if (obj instanceof WaterTile) {
                waterList.add((WaterTile) obj);
            } else if (obj instanceof SandTile) {
                sandList.add((SandTile) obj);
            } else if (obj instanceof EnergyPillar) {
                energyPillarList.add((EnergyPillar) obj);
            } else if (obj instanceof Wall) {
                wallList.add((Wall) obj);
            } else if (obj instanceof BorderEdge) {
                borderEdgeList.add((BorderEdge) obj);
            } else if (obj instanceof BorderCorner) {
                borderCornerList.add((BorderCorner) obj);
            } else if (obj instanceof OscWall) {
                oscWallList.add((OscWall) obj);
            } else if (obj instanceof BoxObstacle && obj.getName() == "decorative") {
            	decorativeList.add((BoxObstacle)obj);
			}
        }

//		dimensionsCache.set((rightBorder - leftBorder) * Constants.TILE_WIDTH, (topBorder - bottomBorder) * Constants.TILE_WIDTH);
//		System.out.println(dimensionsCache);

        // For now, until the types used for levels are fixed
        Wall[] wallArray = new Wall[wallList.size()];
        wallList.toArray(wallArray);

        WaterTile[] waterArray = new WaterTile[waterList.size()];
        waterList.toArray(waterArray);

        SandTile[] sandArray = new SandTile[sandList.size()];
        sandList.toArray(sandArray);

        BorderEdge[] borderEdgeArray = new BorderEdge[borderEdgeList.size()];
        borderEdgeList.toArray(borderEdgeArray);

        BorderCorner[] borderCornerArray = new BorderCorner[borderCornerList.size()];
        borderCornerList.toArray(borderCornerArray);

        EnergyPillar[] energyPillarArray = new EnergyPillar[energyPillarList.size()];
        energyPillarList.toArray(energyPillarArray);

        OscWall[] oscWallArray = new OscWall[oscWallList.size()];
        oscWallList.toArray(oscWallArray);

        // TODO: what if spirit is null

		DecorativeRoots[] decorativeArray = new DecorativeRoots[decorativeList.size()];
		decorativeList.toArray(decorativeArray);

        // TODO: what if spirit is null

        level.set(dimensions, wallArray, waterArray, sandArray, borderEdgeArray, borderCornerArray, energyPillarArray, oscWallArray, decorativeArray, hostList, spirit, pedestal);
        loader.saveLevel(f, level);

        reset();
    }

//	/**
//	 * Draw the physics objects together with foreground and background
//	 *
//	 * This is completely overridden to support custom background and foreground art.
//	 *
//	 * @param dt Timing values from parent loop
//	 */
//	public void draw(float dt) {
//		canvas.clear();
//
//		// Draw background unscaled.
//		canvas.begin();
//
//		// Use the lower left corner of tiles, not the center, to start drawing the canvas
//		for(float x = xTileToCoord(leftBorder, true); x < xTileToCoord(rightBorder, true); x += Constants.TILE_WIDTH * screenTileWidth()) {
//			for(float y = yTileToCoord(bottomBorder, true); y < yTileToCoord(topBorder, true); y += Constants.TILE_WIDTH * screenTileHeight()) {
//
//				// Calculate the width and height of the canvas segment. If the
//				// board doesn't extend the entire way, find the desired dimensions
//				float width = Math.min(canvas.getWidth(), scale.x * (xTileToCoord(rightBorder, true) - x));
//				float height = Math.min(canvas.getHeight(), scale.y * (yTileToCoord(topBorder, true) - y));
//
//				// Draw only the part of the texture that is in game
//				canvas.draw(backgroundTexture.getTexture(), scale.x * x, scale.y * y,  width, height,
//						0.f, 0.f, width / canvas.getWidth(), height / canvas.getHeight());
//
////				canvas.draw(backgroundTexture, Color.WHITE, Constants.TILE_WIDTH * scale.x * x, Constants.TILE_WIDTH * scale.y * y,canvas.getWidth(),canvas.getHeight());
//			}
//		}
////		canvas.draw(backgroundTexture, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
//		canvas.end();
//
//		canvas.begin();
//
//		for(Obstacle obj : objects) {
//			obj.draw(canvas);
//		}
//		canvas.end();
//
//		if (isDebug()) {
//			canvas.beginDebug();
//			for(Obstacle obj : objects) {
//				obj.drawDebug(canvas);
//			}
//			canvas.endDebug();
//		}
//
//		// Draw foreground last. The foreground indicates what is within the bounds of the game and what is outside
////		canvas.begin();
////		canvas.draw(foregroundTexture, FORE_COLOR,  Constants.TILE_WIDTH * scale.x * leftBorder, Constants.TILE_WIDTH * scale.y * bottomBorder, scale.x * Constants.TILE_WIDTH * (rightBorder - leftBorder), scale.y * Constants.TILE_WIDTH * (topBorder - bottomBorder));
////		selector.draw(canvas);
////		canvas.end();
//	}

}