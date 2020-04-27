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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


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

	/** Texture file for mouse crosshairs */
	private static final String CROSS_FILE = "shared/crosshair.png";
	/** Texture file for watery foreground */
	private static final String FOREG_FILE = "shared/foreground.png";
	/** Texture file for background image */
	private static final String BACKG_FILE = "shared/background.png";
	/** Texture file for host footprints */
	private static final String FOOTPRINT_FILE = "host/footprints.png";

	/** Speed for changing camera position */
	private static final float CAMERA_SPEED = 5.f;

	/** Width of each tile, in box2D coordinates */
	private static final float TILE_WIDTH = 2.f;

	/** The maximum width and height of the board, in tile coordinates */
	private static final int MAX_BOARD_TILES = 127;

	/** The minimum number of tiles that the board can be */
	private static final int MINIMUM_BOARD_WIDTH = 6;

	/** Texture asset for mouse crosshairs */
	private TextureRegion crosshairTexture;
	/** Texture asset for background image */
	private TextureRegion backgroundTexture;
	/** Texture asset for foreground */
	private TextureRegion foregroundTexture;
	/** Texture asset for footprint */
	private TextureRegion footprintTexture;

	/** The level to be loaded in reset() */
	private int currentLevel = 0;
	/** A boolean indicating if the board should be reloaded from the file */
	private boolean loadBoard = true;

	/** Track asset loading from all instances and subclasses */
	private AssetState assetState = AssetState.EMPTY;

	/** The level that is populated and used for saving */
	private Level level;

	/** The collection of spawning objects, for making new game elements */
	private SpawnerList spawnList;

	/** The camera position */
	private Vector2 camTarget;

	/** Intermediate vector used for arithmetic */
	private Vector2 cache;

	/** The 2D array that is the board */
	private Obstacle[][] board;

	/** A reference to the last golem placed, for placing instructions */
	private HostModel lastGolem;

	/** A boolean to determine if you are currently placing instructions */
	private boolean instructionMode;

	/** A cached arraylist to represent the instruction list */
	private ArrayList<Vector2> instructionListCache;

	/** A cached vector2 to hold one instruction */
	private Vector2 instructionCache;

	/** A list of footprints for instruction visuals */
	private ArrayList<FootPrintModel> footprints;

	/** A list of the golems, for purposes of instructions */
	private ArrayList<HostModel> golems;

	/** If a selection is currently happening. Even if nothing is selected by
	 * the object selector, this will be true until the mouse is released, and
	 * it prevent another object from being picked up */
	private boolean selecting;

	PooledList<BorderEdge> edges;
	PooledList<BorderCorner> corners;

	private enum Corner {
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}

	public class CornerObstacle extends BoxObstacle {
		public CornerObstacle(TextureRegion tex, Corner corner) {
			super(Constants.TILE_WIDTH,Constants.TILE_HEIGHT);
			this.corner = corner;

			setTexture(tex);
			setDrawScale(scale);
			setSX(1);
			setSY(1);
			setBodyType(BodyDef.BodyType.StaticBody);
			setSensor(true);
			setName("corner");
		}

		public final Corner corner;
	}

	private int topBorder;
	private int bottomBorder;
	private int leftBorder;
	private int rightBorder;

	private int initialBottomBorder;
	private int initialLeftBorder;

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
		cache = new Vector2();

		board = new Obstacle[MAX_BOARD_TILES][MAX_BOARD_TILES];
		lastGolem = null;
		instructionListCache = new ArrayList<Vector2>();
		instructionMode = false;
		instructionCache = new Vector2();
		footprints = new ArrayList<FootPrintModel>();
		golems = new ArrayList<HostModel>();

		File folder = new File("levels");
		levels = folder.listFiles();

		edges = new PooledList<>();
		corners = new PooledList<>();
	}

	/**
	 * Gets the level name, using the current level number
	 *
	 * @return A string representing the level name
	 */
	public String getLevelName() {
		return "levels/" + levels[currentLevel%levels.length].getName();
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
		for(int x = 0; x < board.length; x++) {
			Arrays.fill(board[x], null);
		}

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
            bottomBorder = (board[0].length / 2) - (screenTileHeight() / 2);
            leftBorder = (board.length / 2) - (screenTileWidth() / 2);

            initialBottomBorder = bottomBorder;
            initialLeftBorder = leftBorder;

            topBorder = bottomBorder + screenTileHeight();
            rightBorder = leftBorder + screenTileWidth();

            loadBoard = true;
        }

        // After populateLevel(), when the borders are set from the loaded level
        camTarget.set(scale.x * Constants.TILE_WIDTH * (rightBorder - leftBorder) / 2.f,
                scale.y * Constants.TILE_WIDTH * (topBorder - bottomBorder) / 2.f);

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

        EnergyPillar energyPillarSpawn = factory.makeEnergyPillar(0.f, 0.f);
        addObject(energyPillarSpawn);

        spawnList = new SpawnerList(canvas, scale);

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

		spawnList.addSpawner(energyPillarSpawn, new SpawnerList.CallbackFunction() {
			public Obstacle makeObject(float x, float y, Obstacle lastCreated) {
				return factory.makeEnergyPillar(x,y);
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
		bottomLeft.inHUD = true;
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
        float top = yTileToCoord(topBorder) - (Constants.TILE_WIDTH / 2.f);
        float bottom = yTileToCoord(bottomBorder) - (Constants.TILE_WIDTH / 2.f);

        float left = xTileToCoord(leftBorder) - (Constants.TILE_WIDTH / 2.f);
        float right = xTileToCoord(rightBorder) - (Constants.TILE_WIDTH / 2.f);

        topLeft.setPosition(left, top);
        topRight.setPosition(right, top);
        bottomLeft.setPosition(left, bottom);
        bottomRight.setPosition(right, bottom);
    }

    /**
     * Lays out the game geography.
     */
    private void populateLevel() {
//		spawnList.addSpawner(spiritSpawn, new SpawnerList.CallbackFunction() {
//			public Obstacle makeObject(float x, float y, Obstacle lastCreated) {
//				if (lastCreated == null) {
//					return factory.makeSpirit(x, y);
//				}
//				lastCreated.setX(x);
//				lastCreated.setY(y);
//
//				return null;
//			}
//		});

        // TODO: Change this with GamePlayController!!
        FileHandle levelToLoad;
//		if (currentLevel == 3) {
//			levelToLoad = Gdx.files.local("levels/custom3.lvl");
//		} else {
//			levelToLoad = Gdx.files.internal("levels/custom" + currentLevel + ".lvl");
//		}
        System.out.println(getLevelName());
        levelToLoad = Gdx.files.local(getLevelName());
        //levelToLoad = Gdx.files.internal("levels/custom" + currentLevel + ".lvl");


//		try {
//		level = new Level(new Vector2(10, 10), new Wall[0], new WaterTile[0],
//				new SandTile[0], new BorderEdge[0], new BorderCorner[0], new EnergyPillar[0],
//				new ArrayList<HostModel>(), factory.makeSpirit(5, 5), factory.makePedestal(5, 5));
        level = loader.loadLevel(levelToLoad);
//		} catch (Exception e) {
//			System.out.println(e);
//			System.out.println(levelToLoad);
//			level = loader.loadLevel(new FileHandle("levels/custom0.lvl"));
//		}

		dimensions = level.dimensions;
		lowerLeft.setZero();

		bottomBorder = (board[0].length / 2) - (int)((dimensions.y / TILE_WIDTH) / 2);
		leftBorder = (board.length / 2) - (int)((dimensions.x / TILE_WIDTH) / 2);

		initialBottomBorder = bottomBorder;
		initialLeftBorder = leftBorder;

		topBorder = bottomBorder + Math.max((int)(dimensions.y / TILE_WIDTH), MINIMUM_BOARD_WIDTH);
		rightBorder = leftBorder + Math.max((int)(dimensions.x / TILE_WIDTH), MINIMUM_BOARD_WIDTH);

		for(Obstacle obj : level.walls) {
			addNewObstacle(obj);
		}
		for(Obstacle obj : level.hosts) {
			addNewObstacle(obj);
		}
		for(Obstacle obj : level.water) {
			addNewObstacle(obj);
		}
		for(Obstacle obj : level.sand) {
			addNewObstacle(obj);
		}
		for(Obstacle obj : level.borderEdges) {
			addNewObstacle(obj);
		}
		for(Obstacle obj : level.borderCorners) {
			addNewObstacle(obj);
		}
		for (Obstacle obj : level.energyPillars) {
			addNewObstacle(obj);
		}
		addNewObstacle(level.pedestal);
		addNewObstacle(level.start);

		setBordersAndUpdateTerrain();
	}

	/**
	 * Adds the given obstacle to the world and to the board array, if it is in
	 * bounds and nothing is at its tile. It is also snapped to the grid
	 *
	 * @param obj The obstacle to add. It should not already be in the world
	 */
	private void addNewObstacle(Obstacle obj) {
		int x = xCoordToTile(obj.getX());
		int y = yCoordToTile(obj.getY());

		if(x >= leftBorder && y >= bottomBorder && x < rightBorder && y < topBorder && board[x][y] == null) {
			obj.setPosition(xTileToCoord(x), yTileToCoord(y));

			board[x][y] = obj;
			addObject(obj);

			// TODO: uncomment this when hasPedestal is added
//			if(obj.getName() == "pedestal") {
//				hasPedestal = true;
//			}
		}
	}

	/**
	 * Gets the x index of tile index that an x coordinate is in
	 *
	 * @param coord The box2D x coordinate
	 *
	 * @return The x value of the tile index
	 */
	private int xCoordToTile(float coord) {
		return Math.round((coord - (TILE_WIDTH / 2.f)) / TILE_WIDTH) + initialLeftBorder;
	}

	/**
	 * Gets the y index of tile index that an y coordinate is in
	 *
	 * @param coord The box2D y coordinate
	 *
	 * @return The y value of the tile index
	 */
	private int yCoordToTile(float coord) {
		return Math.round((coord - (TILE_WIDTH / 2.f)) / TILE_WIDTH) + initialBottomBorder;
	}

	/**
	 * Gets the x coordinate of the center of a tile
	 *
	 * @param index The x value of the tile index
	 *
	 * @return The box2D x coordinate of the tile center
	 */
	private float xTileToCoord(int index) {
		return xTileToCoord(index, false);
	}

	/**
	 * Gets the y coordinate of the center of a tile
	 *
	 * @param index The y value of the tile index
	 *
	 * @return The box2D y coordinate of the tile center
	 */
	private float yTileToCoord(int index) {
		return yTileToCoord(index, false);
	}

	/**
	 * Gets the x coordinate of a tile
	 *
	 * @param index The x value of the tile index
	 * @param corner If true, gets the lower left corner coordinate. Otherwise,
	 *               gets the center coordinate
	 *
	 * @return The box2D x coordinate of the tile
	 */
	private float xTileToCoord(int index, boolean corner) {
		return ((index - initialLeftBorder) + 0.5f) * TILE_WIDTH - (corner ? TILE_WIDTH / 2.f : 0);
	}

	/**
	 * Gets the y coordinate of a tile
	 *
	 * @param index The y value of the tile index
	 * @param corner If true, gets the lower left corner coordinate. Otherwise,
	 *               gets the center coordinate
	 *
	 * @return The box2D y coordinate of the tile corner
	 */
	private float yTileToCoord(int index, boolean corner) {
		return ((index - initialBottomBorder) + 0.5f) * TILE_WIDTH - (corner ? TILE_WIDTH / 2.f : 0);
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
	 *
	 * @return True if a water tile was updated, false otherwise
	 */
	private boolean updateWaterTile(int x, int y) {
		if(x < leftBorder || y < bottomBorder || x >= rightBorder || y >= topBorder || !(board[x][y] instanceof WaterTile)) {
			return false;
		}

		Obstacle above = null, below = null, left = null, right = null;
		boolean hasGroundAbove = false, hasGroundBelow = false, hasGroundLeft = false, hasGroundRight = false;
		boolean upLeftCorner = false, upRightCorner = false, downLeftCorner = false, downRightCorner = false;

		// Set the adjacent tiles if they are in bounds, and if so, check if a
		// ground border is needed
		if(y + 1 < topBorder) {
			above = board[x][y + 1];
			hasGroundAbove = !(above instanceof WaterTile);
		}
		if(y - 1 >= bottomBorder) {
			below = board[x][y - 1];
			hasGroundBelow = !(below instanceof WaterTile);
		}
		if(x - 1 >= leftBorder) {
			left = board[x - 1][y];
			hasGroundLeft = !(left instanceof WaterTile);
		}
		if(x + 1 < rightBorder) {
			right = board[x + 1][y];
			hasGroundRight = !(right instanceof WaterTile);
		}

		// If the corner should be drawn for each side, based on if adjacent
		// water tiles have ground
		if (above instanceof WaterTile && left instanceof WaterTile) {
			upLeftCorner = !(board[x - 1][y + 1] instanceof WaterTile);
		}
		if (above instanceof WaterTile && right instanceof WaterTile) {
			upRightCorner = !(board[x + 1][y + 1] instanceof WaterTile);
		}
		if (below instanceof WaterTile && left instanceof WaterTile) {
			downLeftCorner = !(board[x - 1][y - 1] instanceof WaterTile);
		}
		if (below instanceof WaterTile && right instanceof WaterTile) {
			downRightCorner = !(board[x + 1][y - 1] instanceof WaterTile);
		}

		((WaterTile) board[x][y]).setFrame(hasGroundAbove, hasGroundBelow, hasGroundLeft, hasGroundRight, true);
		((WaterTile) board[x][y]).setCorners(upLeftCorner, upRightCorner, downLeftCorner, downRightCorner);

		return true;
	}

	/**
	 * Updates the texture for water tile at and around (x, y) in the board.
	 * this function calls updateWaterTile
	 *
	 * If the tile is not a water tile, or if the tile is out of bounds, nothing
	 * happens.
	 *
	 * @param x The x index in the board of the tile center to update
	 * @param y The y index in the board of the tile center to update
	 */
	private void updateWaterAroundRegion(int x, int y) {
		updateWaterTile(x, y);

		updateWaterTile(x - 1, y);
		updateWaterTile(x + 1, y);
		updateWaterTile(x, y - 1);
		updateWaterTile(x, y + 1);

		updateWaterTile(x - 1, y + 1);
		updateWaterTile(x + 1, y + 1);
		updateWaterTile(x - 1, y - 1);
		updateWaterTile(x + 1, y - 1);
	}


	/**
	 * Updates the texture for sand tile at index x, y in the board based on
	 * its surroundings (if they are sand or ground)
	 *
	 * If the tile is not a sand tile, or if the tile is out of bounds, nothing
	 * happens.
	 *
	 * @param x The x index in the board of the tile to update
	 * @param y The y index in the board of the tile to update
	 *
	 * @return True if a sand tile was updated, false otherwise
	 */
	private boolean updateSandTile(int x, int y) {
		if(x < leftBorder || y < bottomBorder || x >= rightBorder || y >= topBorder || !(board[x][y] instanceof SandTile)) {
			return false;
		}

		Obstacle above = null, below = null, left = null, right = null;
		boolean hasGroundAbove = false, hasGroundBelow = false, hasGroundLeft = false, hasGroundRight = false;
		boolean upLeftCorner = false, upRightCorner = false, downLeftCorner = false, downRightCorner = false;

		// Set the adjacent tiles if they are in bounds, and if so, check if a
		// ground border is needed
		if(y + 1 < topBorder) {
			above = board[x][y + 1];
			hasGroundAbove = !(above instanceof SandTile);
		}
		if(y - 1 >= bottomBorder) {
			below = board[x][y - 1];
			hasGroundBelow = !(below instanceof SandTile);
		}
		if(x - 1 >= leftBorder) {
			left = board[x - 1][y];
			hasGroundLeft = !(left instanceof SandTile);
		}
		if(x + 1 < rightBorder) {
			right = board[x + 1][y];
			hasGroundRight = !(right instanceof SandTile);
		}

		// If the corner should be drawn for each side, based on if adjacent
		// sand tiles have ground
		if (above instanceof SandTile && left instanceof SandTile) {
			upLeftCorner = !(board[x - 1][y + 1] instanceof SandTile);
		}
		if (above instanceof SandTile && right instanceof SandTile) {
			upRightCorner = !(board[x + 1][y + 1] instanceof SandTile);
		}
		if (below instanceof SandTile && left instanceof SandTile) {
			downLeftCorner = !(board[x - 1][y - 1] instanceof SandTile);
		}
		if (below instanceof SandTile && right instanceof SandTile) {
			downRightCorner = !(board[x + 1][y - 1] instanceof SandTile);
		}

		((SandTile) board[x][y]).setFrame(hasGroundAbove, hasGroundBelow, hasGroundLeft, hasGroundRight, true);
		((SandTile) board[x][y]).setCorners(upLeftCorner, upRightCorner, downLeftCorner, downRightCorner);

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
	private void updateSandAroundRegion(int x, int y) {
		updateSandTile(x, y);

		updateSandTile(x - 1, y);
		updateSandTile(x + 1, y);
		updateSandTile(x, y - 1);
		updateSandTile(x, y + 1);

		updateSandTile(x - 1, y + 1);
		updateSandTile(x + 1, y + 1);
		updateSandTile(x - 1, y - 1);
		updateSandTile(x + 1, y - 1);
	}

	/**
	 * Updates the texture for wall tile at index x, y in the board based on
	 * its surroundings (if they are wall or not)
	 *
	 * If the tile is not a wall, or if the tile is out of bounds, nothing
	 * happens.
	 *
	 * @param x The x index in the board of the tile to update
	 * @param y The y index in the board of the tile to update
	 *
	 * @return True if a wall tile was updated, false otherwise
	 */
	private boolean updateWallTile(int x, int y) {
		if(x < leftBorder || y < bottomBorder || x >= rightBorder || y >= topBorder || !(board[x][y] instanceof Wall)) {
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
		if(y + 1 < topBorder) {
			above = board[x][y + 1];
			wallAbove = above instanceof Wall;
		}
		if(y - 1 >= bottomBorder) {
			below = board[x][y - 1];
			wallBelow = below instanceof Wall;
			if(wallBelow) {
				belowIsTop = !((Wall)below).isFrontWall();
			}
		}
		if(x - 1 >= leftBorder) {
			left = board[x - 1][y];
			wallLeft = left instanceof Wall;
			if(wallLeft) {
				leftIsTop = !((Wall)left).isFrontWall();
			}
		}
		if(x + 1 < rightBorder) {
			right = board[x + 1][y];
			wallRight = right instanceof Wall;
			if(wallRight) {
				rightIsTop = !((Wall)right).isFrontWall();
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
			wallLowerLeft = board[x - 1][y - 1] instanceof Wall;
			if(wallLowerLeft) {
				lowerLeftIsTop = !((Wall)board[x - 1][y - 1]).isFrontWall();
			}
		}
		if (below instanceof Wall && right instanceof Wall) {
			wallLowerRight = board[x + 1][y - 1] instanceof Wall;
			if(wallLowerRight) {
				lowerRightIsTop = !((Wall)board[x + 1][y - 1]).isFrontWall();
			}
		}

		// Switch up the alt boolean every tile
		((Wall)board[x][y]).setFrame(wallAbove, wallBelow, wallLeft, wallRight,
				belowIsTop, leftIsTop, rightIsTop,
				lowerLeftIsTop, lowerRightIsTop,x + y % 2 == 0);

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
		// Offset the location so the corner sits at a corner of the tile, not
		// the center
		int x = xCoordToTile(corner.getX() + (TILE_WIDTH / 2.f));
		int y = yCoordToTile(corner.getY() + (TILE_WIDTH / 2.f));

		// Ensure there is at least 1 row and 1 column of the array that can be
		// used
		int top    = Math.min(Math.max(y, 1), board[0].length);
		int bottom = Math.min(Math.max(y, 0), board[0].length - 1);
		int left   = Math.min(Math.max(x, 0), board.length - 1);
		int right  = Math.min(Math.max(x, 1), board.length);

		// Ensure the board always has the minimum size
		top    = Math.max(top, bottomBorder + MINIMUM_BOARD_WIDTH);
		bottom = Math.min(bottom, topBorder - MINIMUM_BOARD_WIDTH);
		left   = Math.min(left, rightBorder - MINIMUM_BOARD_WIDTH);
		right  = Math.max(right, leftBorder + MINIMUM_BOARD_WIDTH);

		// In each case, reset the variables that shouldn't change
		switch(corner.corner) {
		case TOP_LEFT:
			bottom = bottomBorder;
			right = rightBorder;

			break;
		case TOP_RIGHT:
			bottom = bottomBorder;
			left = leftBorder;

			break;
		case BOTTOM_LEFT:
			top = topBorder;
			right = rightBorder;

			break;
		case BOTTOM_RIGHT:
			top = topBorder;
			left = leftBorder;

			break;
		default:
			System.out.println("Invalid corner being processed");
			assert false;
			break;
		}

		// Remove everything that has become out of bounds
		for(int i = leftBorder; i < rightBorder; i++) {
			for(int j = bottomBorder; j < topBorder; j++) {
				if((i < left || i >= right || j < bottom || j >= top) && board[i][j] != null) {
					board[i][j].markRemoved(true);
					board[i][j] = null;
				}
			}
		}

		// Assign the new borders
		topBorder = top;
		bottomBorder = bottom;
		leftBorder = left;
		rightBorder = right;

		setBordersAndUpdateTerrain();

		dimensions.set(TILE_WIDTH * (rightBorder - leftBorder), TILE_WIDTH * (topBorder - bottomBorder));
		lowerLeft.set(TILE_WIDTH * (leftBorder - initialLeftBorder), TILE_WIDTH * (bottomBorder - initialBottomBorder));

		updateCornerPositions();

		System.out.println("x: [" + leftBorder + ", " + rightBorder + "], y: [" + bottomBorder + ", " + topBorder + "]");
	}

	/**
	 * Sets the borders around the edge of the board with the correct
	 * orientations. Also updates each terrain tile (water, sand, and wall)
	 */
	private void setBordersAndUpdateTerrain() {
// Update terrain tiles based on the new borders and add walls
		for(int i = leftBorder; i < rightBorder; i++) {
			for(int j = bottomBorder; j < topBorder; j++) {
				float xCoord = xTileToCoord(i);
				float yCoord = yTileToCoord(j);

				// Add corners and borders
				if(i == leftBorder && j == bottomBorder) {
					if (board[i][j] != null) {
						board[i][j].markRemoved(true);
						board[i][j] = null;
					}
					addNewObstacle(factory.makeBorderCorner(xCoord, yCoord, BorderCorner.Corner.BOTTOM_LEFT));
				} else if(i == rightBorder - 1 && j == bottomBorder) {
					if (board[i][j] != null) {
						board[i][j].markRemoved(true);
						board[i][j] = null;
					}
					addNewObstacle(factory.makeBorderCorner(xCoord, yCoord, BorderCorner.Corner.BOTTOM_RIGHT));
				} else if(i == leftBorder && j == topBorder - 1) {
					if (board[i][j] != null) {
						board[i][j].markRemoved(true);
						board[i][j] = null;
					}
					addNewObstacle(factory.makeBorderCorner(xCoord, yCoord, BorderCorner.Corner.TOP_LEFT));
				} else if(i == rightBorder - 1 && j == topBorder - 1) {
					if (board[i][j] != null) {
						board[i][j].markRemoved(true);
						board[i][j] = null;
					}
					addNewObstacle(factory.makeBorderCorner(xCoord, yCoord, BorderCorner.Corner.TOP_RIGHT));
				} else if(j == bottomBorder) {
//					if(board[i][j] instanceof BorderEdge) {
//						((BorderEdge)board[i][j]).setSide(BorderEdge.Side.BOTTOM);
//					} else {
						if (board[i][j] != null) {
							board[i][j].markRemoved(true);
							board[i][j] = null;
						}
						BorderEdge border = factory.makeBorder(xCoord, yCoord, BorderEdge.Side.BOTTOM);
						if(i - leftBorder == 1) {
							border.setNextToSide(1, BorderEdge.Side.LEFT);
						} else if((rightBorder - 1) - i == 1) {
							border.setNextToSide(1, BorderEdge.Side.RIGHT);
						}
						addNewObstacle(border);
//					}
				} else if(i == leftBorder) {
//					if(board[i][j] instanceof BorderEdge) {
//						((BorderEdge)board[i][j]).setSide(BorderEdge.Side.LEFT);
//					} else {
						if (board[i][j] != null) {
							board[i][j].markRemoved(true);
							board[i][j] = null;
						}
						BorderEdge border = factory.makeBorder(xCoord, yCoord, BorderEdge.Side.LEFT);
						if(j - bottomBorder == 1) {
							border.setNextToSide(1, BorderEdge.Side.BOTTOM);
						} else if((topBorder - 1) - j == 1) {
							border.setNextToSide(1, BorderEdge.Side.TOP);
						}
						addNewObstacle(border);
//					}
				} else if(i == rightBorder - 1) {
//					if(board[i][j] instanceof BorderEdge) {
//						((BorderEdge)board[i][j]).setSide(BorderEdge.Side.RIGHT);
//					} else {
						if (board[i][j] != null) {
							board[i][j].markRemoved(true);
							board[i][j] = null;
						}
						BorderEdge border = factory.makeBorder(xCoord, yCoord, BorderEdge.Side.RIGHT);
						if(i - bottomBorder == 1) {
							border.setNextToSide(1, BorderEdge.Side.BOTTOM);
						} else if((topBorder - 1) - i == 1) {
							border.setNextToSide(1, BorderEdge.Side.TOP);
						}
						addNewObstacle(border);
//					}
				} else if(j == topBorder - 1) {
//					if(board[i][j] instanceof BorderEdge) {
//						((BorderEdge)board[i][j]).setSide(BorderEdge.Side.TOP);
//					} else {
						if (board[i][j] != null) {
							board[i][j].markRemoved(true);
							board[i][j] = null;
						}
						BorderEdge border = factory.makeBorder(xCoord, yCoord, BorderEdge.Side.TOP);
						if(i - leftBorder <= 2) {
							border.setNextToSide(i - leftBorder, BorderEdge.Side.LEFT);
						} else if((rightBorder - 1) - i <= 2) {
							border.setNextToSide((rightBorder - 1) - i, BorderEdge.Side.RIGHT);
						}
						addNewObstacle(border);
//					}
				} else {
					if(board[i][j] instanceof BorderEdge) {
						edges.add((BorderEdge)board[i][j]);
					} else if(board[i][j] instanceof BorderCorner) {
						corners.add((BorderCorner)board[i][j]);
					}
					updateWaterAroundRegion(i, j);
					updateSandAroundRegion(i, j);
					updateWallAroundRegion(i, j);
				}
			}
		}

		// Remove all the border pieces that are still unused from the game
		for(BorderEdge border : edges) {
			border.markRemoved(true);
		}
		for(BorderCorner border : corners) {
			border.markRemoved(true);
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
        float mouseX = input.getCrossHair().x + (camTarget.x - (canvas.getWidth() / 2.f)) / scale.x;
        float mouseY = input.getCrossHair().y + (camTarget.y - (canvas.getHeight() / 2.f)) / scale.y;

		/* Only reset selecting if the mouse is released. Prevents selecting a
		   new object without releasing and clicking the mouse.
		 */
        if (!input.didTertiary()) {
            selecting = false;
        }

		if (lastGolem != null && input.didInstruction() && !instructionMode) {
			instructionMode = true;
			instructionListCache.clear();
			lastGolem.setInstructions(null);
			System.out.println("Instruction Mode");
			refreshFootprints();
		}

		else if (instructionMode && input.didInstruction()) {
			instructionMode = false;
			Vector2[] instructions = new Vector2[instructionListCache.size()];
			for (int i = 0; i < instructionListCache.size(); i++) {
				instructions[i] = instructionListCache.get(i);
				System.out.println(instructions[i]);
			}
			if (instructionListCache.size() != 0) {
				lastGolem.setInstructions(instructions);
			}
			else {
				lastGolem.setInstructions(null);
			}
			refreshFootprints();
			System.out.println("Instructions saved to golem");
			/*
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

			 */
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

		if (lastGolem != null && input.didInstruction() && !instructionMode) {
			instructionMode = true;
			instructionListCache.clear();
			lastGolem.setInstructions(null);
			System.out.println("Instruction Mode");
			refreshFootprints();
		}

		else if (instructionMode && input.didInstruction()) {
			instructionMode = false;
			Vector2[] instructions = new Vector2[instructionListCache.size()];
			for (int i = 0; i < instructionListCache.size(); i++) {
				instructions[i] = instructionListCache.get(i);
				System.out.println(instructions[i]);
			}
			if (instructionListCache.size() != 0) {
				lastGolem.setInstructions(instructions);
			}
			else {
				lastGolem.setInstructions(null);
			}
			refreshFootprints();
			System.out.println("Instructions saved to golem");
			/*
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

			 */
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
			if(x >= leftBorder && y >= bottomBorder && x < rightBorder && y < topBorder) {
				if(board[x][y] == selector.getObstacle()) { // Note: Purposefully comparing references
					board[x][y] = null;
				}

				// Update the surroundings after removing old obstacles
				updateWaterAroundRegion(x, y);
				updateSandAroundRegion(x, y);
				updateWallAroundRegion(x, y);
			}

		}


		else if (!input.didTertiary() && selector.isSelected()) {
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

				// Remove this body if it's out of bounds
				if (x < leftBorder || y < bottomBorder || x >= rightBorder || y >= topBorder) {
					deselected.markRemoved(true);

				// Add the body to the board array and set the position
				} else {
					// Remove the replaced body if necessary
					if (board[x][y] != null) {
						board[x][y].markRemoved(true);
					}

					deselected.setPosition(xTileToCoord(x), yTileToCoord(y));
					board[x][y] = deselected;

					// Update tiles whose textures depend on the nearby ones
					updateWaterAroundRegion(x, y);
					updateSandAroundRegion(x, y);
					updateWallAroundRegion(x, y);
				}
			}
		} else {
			selector.moveTo(mouseX, mouseY);
		}

		// Spawn a new object if a spawner was clicked
		Obstacle obj = spawnList.update(camTarget);

		if(obj != null) {
		    // If the selected object is a pedestal then check if pedestal is already in the game
			if((obj.getName() == "pedestal" && !hasPed) || obj.getName() != "pedestal") {
				addObject(obj);
				selector.select(mouseX, mouseY);
				selecting = true;
			}
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

		dimensions.set(TILE_WIDTH * (rightBorder - leftBorder), TILE_WIDTH * (topBorder - bottomBorder));
		lowerLeft.set(TILE_WIDTH * (leftBorder - initialLeftBorder), TILE_WIDTH * (bottomBorder - initialBottomBorder));

		updateSelector(hasPed);

		if(input.didPrimary()) {
//			//change wall texture that is currently selected at mouse location
			if (selector.isSelected()) {
				// If its a golem then increment the current charge of the golem
				Obstacle selection = selector.getObstacle();
				if (selection.getName() == "host") {
					if (((HostModel) selection).getCurrentCharge() < MAX_CHARGE_CAPACITY) {
						((HostModel) selection).setCurrentCharge(((HostModel) selection).getCurrentCharge() + 10);
						((HostModel) selection).setChargeStripFrame(((HostModel) selection).getCurrentCharge());
					}
				}
			}
		}
		if(input.didSecondary()) {
//			//change wall texture that is currently selected at mouse location
			if (selector.isSelected()) {
//				// Get the selection, then remove it from the selector
				Obstacle selection = selector.getObstacle();
				if (selection.getName() == "host") {
					if (((HostModel) selection).getCurrentCharge() > MIN_CHARGE_CAPACITY) {
						((HostModel) selection).setCurrentCharge(((HostModel) selection).getCurrentCharge() - 10);
						((HostModel) selection).setChargeStripFrame(((HostModel) selection).getCurrentCharge());
					}
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
            if (hasPed) {
                save(getLevelName());
                System.out.println("Saving level as: " + getLevelName());
            } else {
                System.out.println("Did not save level: no pedestal found");
            }
        }

        canvas.setCamTarget(camTarget);
        canvas.updateCamera();

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
        FileHandle f = Gdx.files.local(levelName);

        // TODO: Make this not creating new objects by updating Level to use PooledList(?)

        SpiritModel spirit = null;
        ArrayList<BoxObstacle> waterList = new ArrayList<>();
        ArrayList<BoxObstacle> sandList = new ArrayList<>();
        ArrayList<HostModel> hostList = new ArrayList<>();
		ArrayList<Wall> wallList = new ArrayList<>();
		ArrayList<BorderEdge> borderEdgeList = new ArrayList<>();
		ArrayList<BorderCorner> borderCornerList = new ArrayList<>();
        ArrayList<EnergyPillar> energyPillarList = new ArrayList<>();
        HostModel pedestal = null;

        System.out.println(cache);

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

        // TODO: what if spirit is null

        level.set(dimensions, wallArray, waterArray, sandArray, borderEdgeArray, borderCornerArray, energyPillarArray, hostList, spirit, pedestal);
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