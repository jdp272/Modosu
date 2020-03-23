package edu.cornell.gdiac.physics;

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
import edu.cornell.gdiac.physics.host.HostList;
import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.spirit.SpiritModel;

/**
 * A static class that can be used for loading a level from a json file
 */
public class Loader {

    // Static fields

    /** Texture file for host sprite */
    private static final String HOST_FILE = "assets/host/host.png";
    /** Texture file for HostModel Gauge */
    private static final String HOST_GAUGE_FILE = "assets/host/host_gauge.png";
    /** Texture file for spirit sprite */
    private static final String SPIRIT_FILE = "assets/host/spirit.png";
    /** File to texture for obstacles */
    private static String OBSTACLE_FILE = "shared/crate02.png";
    /** File to texture for walls and platforms */
    private static String EARTH_FILE = "assets/shared/earthtile.png";
    /** Retro font for displaying messages */
    private static String FONT_FILE = "shared/RetroGame.ttf";
    private static int FONT_SIZE = 64;

    // Fields

    public enum ImageFile {
        Robot,
        Crate
    }

    /** A struct that stores data from an obstacle when read from the json */
    public static class ObstacleData {
        public Vector2 origin; // Center of the box
        public Vector2 dimensions;
        public ImageFile imageFile;
        // public float rotation; // TODO: add rotation
    }

    /** A struct that stores data from a host when read from the json */
    public static class HostData {
        public Vector2 location;
        public float chargeTime; // Maximum amount of charge that can be stored
        public ImageFile imageFile;
    }

    /** A struct that stores all the data of a level when read from the json */
    public static class LevelData {
        /**
         * An ArrayList of regions on the board. Each region is an island within
         * which hosts can be located. Outside each counts as out of bounds.
         *
         * Each region is represented as a list of points being the polygon
         * borders of the region.
         *
         * Example: A single square region may be represented as
         * [ [ (0, 0), (0, 100), (100, 100), (100, 0) ] ]
         */
        public Vector2[][] regions;

        public ObstacleData[] obstacleData;
        public HostData[] hostData;

        public Vector2 startLocation;
    }

    /**
     * Tracks the asset state
     */
    public enum AssetState {
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
    /** The texture for hosts */
    private TextureRegion hostTex;
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

    /** A factory that creates the game objects */
    private Factory factory;

    /** A json object used for loading all json files */
    private Json json;

    /** An asset manager for loading assets */
    private AssetManager manager;

    // Constructors

    /**
     * Initializes the loader object
     */
    public Loader(Factory factory) {
        this.factory = factory;

        json = new Json();

        manager = new AssetManager();
        assets = new Array<String>();
        // Add font support to the asset manager
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
    }

    // Public member functions

    /** @return The texture for hosts */
    public TextureRegion getHostTex() { return hostTex; }
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
        manager.load(HOST_FILE, Texture.class);
        assets.add(HOST_FILE);
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
        hostTex = createTexture(HOST_FILE, true);
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

        // Store the host data
        levelData.hostData = new HostData[level.hosts.size()];
        for(int i = 0; i < level.hosts.size(); i++) {
            HostData rData = new HostData();
            rData.location = new Vector2(level.hosts.get(i).getX(), level.hosts.get(i).getY());
            rData.chargeTime = level.hosts.get(i).getMaxCharge();
            levelData.hostData[i] = rData;
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

        for (int i = 0; i < levelData.obstacleData.length; i++) {
            oData = levelData.obstacleData[i];
            obstacles[i] = new BoxObstacle(oData.origin.x, oData.origin.y, oData.dimensions.x, oData.origin.y);
        }

        // Create the hosts
        HostList hosts = new HostList();
        HostData hData;

        for (int i = 0; i < levelData.hostData.length; i++) {
            hData = levelData.hostData[i];

            /* NOTES: I'm assuming that eventually we'll have a simple creator
             for hosts, like a factory method or something, which we only need
             to give coordinates and the charge time. At that point, this can be
             easily updated with that.

             This also assumes that a zero charge time means it has no time
             limit


             TODO: Make a host once HostModel constructor is ready
             */
            System.out.println("iteration " + i);
            hosts.add(factory.makeSmallHost(hData.location.x, hData.location.y), false);
//            hosts.add(new HostModel(rData.location.x, rData.location.y, (int)Data.chargeTime), false);
        }

        System.out.println("hosts size: " + hosts.size());

        // Create the starting "host" (with no charge capacity)
        SpiritModel start = new SpiritModel(levelData.startLocation.x, levelData.startLocation.y, 0);
        // TODO: ensure implementation of 0 charge time means no cap

        return new Level(regions, obstacles, hosts, start);
    }

    public Level reset(int level){
        return null;
//        BoxObstacle[] obs = {new BoxObstacle(50,50,10,10)};
//        HostList robs = new HostList();
//        HostModel rob = new HostModel(30,30,10,10, 100);
//        //rob.setTexture(hostTex);
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

//    private int nHosts;
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
//    public int getnHosts(){return nHosts;}
//

}
