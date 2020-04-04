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

    // Fields

    /** A struct that stores data from an obstacle when read from the json */
    private class ObstacleData {
        public Vector2 origin; // Center of the box
        public Vector2 dimensions;
        // public float rotation; // TODO: add rotation
    }

    /** A struct that stores data from a host when read from the json */
    private class HostData {
        public Vector2 location;
        public float chargeTime; // Maximum amount of charge that can be stored
    }

    /** A struct that stores all the data of a level when read from the json */
    private class LevelData {
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
    private enum AssetState {
        /** No assets loaded */
        EMPTY,
        /** Still loading assets */
        LOADING,
        /** Assets are complete */
        COMPLETE
    }

    // Assets
    /** Track all loaded assets (for unloading purposes) */
    private Array<String> assets;

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

        for (int i = 0; i < obstacles.length; i++) {
            oData = levelData.obstacleData[i];
            obstacles[i] = new BoxObstacle(oData.origin.x, oData.origin.y, oData.dimensions.x, oData.origin.y);
        }

        // Create the hosts
        HostList hosts = new HostList();
        HostData rData;

        for (int i = 0; i < hosts.size(); i++) {
            rData = levelData.hostData[i];

            /* NOTES: I'm assuming that eventually we'll have a simple creator
             for hosts, like a factory method or something, which we only need
             to give coordinates and the charge time. At that point, this can be
             easily updated with that.

             This also assumes that a zero charge time means it has no time
             limit

             TODO: Make a host once HostModel constructor is ready
             */
            //hosts.add(new HostModel(rData.location.x, rData.location.y, (int)rData.chargeTime), false);
        }

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
