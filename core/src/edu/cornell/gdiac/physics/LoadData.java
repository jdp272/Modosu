package edu.cornell.gdiac.physics;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.files.FileHandle;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.robot.RobotModel;
import edu.cornell.gdiac.util.PooledList;

/**
 * A static class that can be used for loading a level from a json file
 */
public class LoadData {
    /** A struct that stores data from an obstacle when read from the json */
    private static class ObstacleData {
        public Vector2 origin; // Center of the box
        public Vector2 dimensions;
        // public float rotation; // TODO: add rotation
    }

    /** A struct that stores data from a robot when read from the json */
    private static class RobotData {
        public Vector2 location;
        public float chargeTime; // Maximum amount of charge that can be stored
    }

    /** A struct that stores all the data of a level when read from the json */
    private static class LevelData {
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
        public ArrayList<ArrayList<Vector2> > regions;

        public ArrayList<ObstacleData> obstacleData;
        public ArrayList<RobotData> robotData;

        public Vector2 startLocation;
    }

//    private int nRobots;

    /** A static json object used for loading all json files */
    private static Json json = new Json();

    /**
     * A function to get a Level from a json file storing the data of the level
     *
     * @param f A LibGDX FileHandle to a json file that holds the level data
     *          Requires: f is a json file that correctly stores the level data
     *
     * @return A complete Level object that is the json file deserialized
     */
    public static Level parseLevel(FileHandle f) {
        LevelData levelData = json.fromJson(LevelData.class, f);

        Vector2[][] regions = new Vector2[levelData.regions.size()][];
        for(int i = 0; i < regions.length; i++) {
            regions[i] = (Vector2[])levelData.regions.get(i).toArray();
        }

        Obstacle[] obstacles = new Obstacle[levelData.obstacleData.size()];
        ObstacleData oData; // A simple reference to the data being processed

        for (int i = 0; i < obstacles.length; i++) {
            oData = levelData.obstacleData.get(i);
            obstacles[i] = new BoxObstacle(
                    oData.origin.x,
                    oData.origin.y,
                    oData.dimensions.x,
                    oData.origin.y
            );
        }

        RobotModel[] robots = new RobotModel[levelData.robotData.size()];
        RobotData rData;

        for (int i = 0; i < robots.length; i++) {
            rData = levelData.robotData.get(i);

            /* NOTES: I'm assuming that eventually we'll have a simple creator
             for robots, like a factory method or something, which we only need
             to give coordinates and the charge time. At that point, this can be
             easily updated with that.

             This also assumes that a zero charge time means it has no time
             limit

             TODO: Make a robot once RobotModel constructor is ready
             */
            robots[i] = new RobotModel(
                    rData.location.x,
                    rData.location.y,
                    rData.chargeTime
            );
        }

        RobotModel start = new RobotModel(
                levelData.startLocation.x,
                levelData.startLocation.y,
                0 // TODO: ensure implementation of 0 charge time means no cap
        );

        return new Level(regions, obstacles, robots, start);
    }

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
//    public void reset(int level){}
}
