package edu.cornell.gdiac.physics;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.files.FileHandle;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.robot.RobotList;
import edu.cornell.gdiac.physics.robot.RobotModel;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.util.PooledList;

/**
 * A static class that can be used for loading a level from a json file
 */
public class Loader {

    // Static fields

    /** Texture file for robot sprite */
    private static final String ROBOT_FILE = "assets/robot/robot.png";
    /** Texture file for HostModel Gauge */
    private static final String HOST_GAUGE_FILE = "assets/robot/host_gauge.png";
    /** Texture file for spirit sprite */
    private static final String SPIRIT_FILE = "assets/robot/spirit.png";
    /** File to texture for obstacles */
    private static String OBSTACLE_FILE = "shared/crate02.png";
    /** File to texture for walls and platforms */
    private static String EARTH_FILE = "assets/shared/earthtile.png";
    /** Retro font for displaying messages */
    private static String FONT_FILE = "shared/RetroGame.ttf";
    private static int FONT_SIZE = 64;

    // Fields

    /** A struct that stores data from an obstacle when read from the json */
    private class ObstacleData {
        public Vector2 origin; // Center of the box
        public Vector2 dimensions;
        // public float rotation; // TODO: add rotation
    }

    /** A struct that stores data from a robot when read from the json */
    private class RobotData {
        public Vector2 location;
        public float chargeTime; // Maximum amount of charge that can be stored
    }

    /** A struct that stores all the data of a level when read from the json */
    private class LevelData {
        /**
         * An ArrayList of regions on the board. Each region is an island within
         * which robots can be located. Outside each counts as out of bounds.
         *
         * Each region is represented as a list of points being the polygon
         * borders of the region.
         *
         * Example: A single square region may be represented as
         * [ [ (0, 0), (0, 100), (100, 100), (100, 0) ] ]
         */
        public Vector2[][] regions;

        public ObstacleData[] obstacleData;
        public RobotData[] robotData;

        public Vector2 startLocation;
    }

    /**
     * Tracks the asset state
     */
    private enum AssetState {
        /** No assets loaded */
        EMPTY,
        /** Still loading assets */
        LOADING,
        /** Assets are complete */
        COMPLETE
    }

    // Assets
    /** Track asset loading */
    private AssetState assetState = AssetState.EMPTY; // TODO: remove this
    /** Track all loaded assets (for unloading purposes) */
    private Array<String> assets;
    /** The texture for robots */
    private TextureRegion robotTex;
    /** The texture for the spirit */
    private TextureRegion spiritTex;
    /** The texture for the obstacle */
    private TextureRegion obstacleTex;
    /** The texture for walls and platforms */
    private TextureRegion earthTile;
    /** The texture for host gauge */
    private TextureRegion hostGaugeTex;
    /** The font for giving messages to the player */
    private BitmapFont displayFont;

    /** A json object used for loading all json files */
    private Json json;

    /** An asset manager for loading assets */
    private AssetManager manager;

    // Constructors

    /**
     * Initializes the loader object
     */
    public Loader() {
        json = new Json();

        manager = new AssetManager();
        assets = new Array<String>();
        // Add font support to the asset manager
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
    }

    // Public member functions

    /** @return The texture for robots */
    public TextureRegion getRobotTex() { return robotTex; }
    /** @return The texture for the spirit */
    private TextureRegion getSpiritTex() { return spiritTex; }
    /** @return The texture for the obstacle */
    private TextureRegion getObstacleTex() { return obstacleTex; }
    /** @return The texture for walls and platforms */
    private TextureRegion getEarthTile() { return earthTile; }
    /** @return The font for giving messages to the player */
    private BitmapFont getDisplayFont() { return displayFont; }

    /**
     * Preloads the assets for the game
     */
    public void preLoadContent() {
        if (assetState != AssetState.EMPTY) {
            return;
        }

        assetState = AssetState.LOADING;

        // Load the textures.
        manager.load(ROBOT_FILE, Texture.class);
        assets.add(ROBOT_FILE);
        manager.load(HOST_GAUGE_FILE, Texture.class);
        assets.add(HOST_GAUGE_FILE);
        manager.load(SPIRIT_FILE, Texture.class);
        assets.add(SPIRIT_FILE);
        manager.load(OBSTACLE_FILE, Texture.class);
        assets.add(OBSTACLE_FILE);
        manager.load(EARTH_FILE, Texture.class);
        assets.add(EARTH_FILE);

        // Load the font
        FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        size2Params.fontFileName = FONT_FILE;
        size2Params.fontParameters.size = FONT_SIZE;
        manager.load(FONT_FILE, BitmapFont.class, size2Params);
        assets.add(FONT_FILE);
    }

    /**
     * Loads the assets for this controller.
     */
    public void loadContent() {
        if (assetState != AssetState.LOADING) {
            return;
        }

        // Allocate the textures
        robotTex = createTexture(ROBOT_FILE, true);
        spiritTex = createTexture(SPIRIT_FILE, true);
        obstacleTex = createTexture(OBSTACLE_FILE, true);
        earthTile = createTexture(EARTH_FILE, true);
        hostGaugeTex = createTexture(HOST_GAUGE_FILE, true);

        // Allocate the font
        if (manager.isLoaded(FONT_FILE)) {
            displayFont = manager.get(FONT_FILE,BitmapFont.class);
        } else {
            displayFont = null;
        }

        assetState = AssetState.COMPLETE;
    }

    /**
     * Unloads the resources of the this object. Should be called when the
     * application is closed
     */
    public void dispose() {
        for(String s : assets) {
            if (manager.isLoaded(s)) {
                manager.unload(s);
            }
        }

        // Unload all of the resources
        manager.clear();
        manager.dispose();
    }

    /**
     * A function to save a level as a json file storing the data of the level
     *
     * @param f A LibGDX FileHandle to a json file that holds the level data
     *          Requires: f is an open file handle
     * @param level A level struct which will be stored
     */
    public void saveLevel(FileHandle f, Level level) {
        LevelData levelData = new LevelData();

        // Store the region information
        levelData.regions = level.regions;

        // Store the obstacle data
        levelData.obstacleData = new ObstacleData[level.obstacles.length];
        for(int i = 0; i < level.obstacles.length; i++) {
            ObstacleData oData = new ObstacleData();
            oData.dimensions = level.obstacles[i].getDimension();
            oData.origin = new Vector2(level.obstacles[i].getX(), level.obstacles[i].getY());
            levelData.obstacleData[i] = oData;
        }

        // Store the robot data
        levelData.robotData = new RobotData[level.robots.size()];
        for(int i = 0; i < level.robots.size(); i++) {
            RobotData rData = new RobotData();
            rData.location = new Vector2(level.robots.get(i).getX(), level.robots.get(i).getY());
            rData.chargeTime = level.robots.get(i).getDefaultPossessionTime();
            levelData.robotData[i] = rData;
        }

        // Store the starting information
        levelData.startLocation = new Vector2(level.start.getX(), level.start.getY());

        // Store the now-populated level data
        json.toJson(levelData, f);
    }

    /**
     * A function to get a Level from a json file storing the data of the level
     *
     * @param f A LibGDX FileHandle to a json file that holds the level data
     *          Requires: f is a json file that correctly stores the level data
     *
     * @return A complete Level object that is the json file deserialized
     */
    public Level loadLevel(FileHandle f) {
        LevelData levelData = json.fromJson(LevelData.class, f);

        // Load the map regions
        Vector2[][] regions = levelData.regions;

        // Create the obstacles
        BoxObstacle[] obstacles = new BoxObstacle[levelData.obstacleData.length];
        ObstacleData oData; // A simple reference to the data being processed

        for (int i = 0; i < obstacles.length; i++) {
            oData = levelData.obstacleData[i];
            obstacles[i] = new BoxObstacle(oData.origin.x, oData.origin.y, oData.dimensions.x, oData.origin.y);
        }

        // Create the robots
        RobotList robots = new RobotList();
        RobotData rData;

        for (int i = 0; i < robots.size(); i++) {
            rData = levelData.robotData[i];

            /* NOTES: I'm assuming that eventually we'll have a simple creator
             for robots, like a factory method or something, which we only need
             to give coordinates and the charge time. At that point, this can be
             easily updated with that.

             This also assumes that a zero charge time means it has no time
             limit

             TODO: Make a robot once RobotModel constructor is ready
             */
            //robots.add(new RobotModel(rData.location.x, rData.location.y, (int)rData.chargeTime), false);
        }

        // Create the starting "robot" (with no charge capacity)
        SpiritModel start = new SpiritModel(levelData.startLocation.x, levelData.startLocation.y, 0);
        // TODO: ensure implementation of 0 charge time means no cap

        return new Level(regions, obstacles, robots, start);
    }

    public Level reset(int level){
        return null;
//        BoxObstacle[] obs = {new BoxObstacle(50,50,10,10)};
//        RobotList robs = new RobotList();
//        RobotModel rob = new RobotModel(30,30,10,10, 100);
//        //rob.setTexture(robotTex);
//        robs.add(rob,true);
//        SpiritModel spir = new SpiritModel(70,70,10,10,4);
//        Level lvl = new Level(null, obs, robs, spir);
//        return lvl; //loadLevel();
    }

    // Private member functions

    /**
     * Returns a newly loaded texture region for the given file.
     *
     * This helper methods is used to set texture settings (such as scaling, and
     * whether or not the texture should repeat) after loading.
     *
     * This method was taken from lab 4, which was written by Walker M. White
     *
     * @param file		The texture (region) file
     * @param repeat	Whether the texture should be repeated
     *
     * @return a newly loaded texture region for the given file.
     */
    private TextureRegion createTexture(String file, boolean repeat) {
        if (manager.isLoaded(file)) {
            TextureRegion region = new TextureRegion(manager.get(file, Texture.class));
            region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            if (repeat) {
                region.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            }
            return region;
        }
        System.out.println(file + " was never loaded");
        return null;
    }

//    private int nRobots;
//
//    /** reads json data, creates objects and returns them to
//     * the gameplay controller
//     * @param json
//     * @return Obstacle array
//     */
//    public Obstacle[] parse(Json json){
//
//        return null;
//    }
//
//    public void preLoadContent(AssetManager manager){}
//
//    public PooledList<Obstacle> loadContent(int level) {}
//
//    public int getnRobots(){return nRobots;}
//

}
