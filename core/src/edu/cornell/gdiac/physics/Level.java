package edu.cornell.gdiac.physics;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.robot.RobotModel;

/**
 * A simple struct-like class that holds the elements of a level.
 */
public class Level {

    /**
     * Constructs a simple object encapsulating the elements of a level
     *
     * @param regions A 2D array, where each element of the inner array
     *                      represents an "island" in the board. That island is
     *                      represented as an array of coordinates
     * @param obstacles An array of the obstacles in the level
     * @param robots An array of the robots in the level
     * @param start The "robot" where the player starts
     */
    public Level(
            Vector2[][] regions,
            BoxObstacle[] obstacles,
            RobotModel[] robots,
            RobotModel start
    ) {
       this.regions = regions;
       this.obstacles = obstacles;
       this.robots = robots;
       this.start = start;
    }

    /**
     * An array of regions on the board. Each region is an island within which
     * robots can be located. Outside each counts as out of bounds.
     *
     * Each region is represented as a list of points being the polygon
     * borders of the region.
     *
     * Example: A single square region may be represented as
     * [ [ (0, 0), (0, 100), (100, 100), (100, 0) ] ]
     */
    public Vector2[][] regions;

    /** An array of the obstacles in the level */
    public BoxObstacle[] obstacles;

    /** An array of the robots in the level */
    public RobotModel[] robots;

    /** The "robot" where the player starts */
    public RobotModel start;
}
