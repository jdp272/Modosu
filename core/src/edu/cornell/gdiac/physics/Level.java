package edu.cornell.gdiac.physics;

import com.badlogic.gdx.math.Vector2;

import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.obstacle.Pedestal;
import edu.cornell.gdiac.physics.obstacle.WaterTile;
import edu.cornell.gdiac.physics.spirit.SpiritModel;

import java.util.ArrayList;

/**
 * A simple struct-like class that holds the elements of a level.
 */
public class Level {

    /**
     * An empty constructor for a level. Everything is null.
     */
    public Level() {
        set(null, null, null, null, null, null);
    }

    /**
     * Constructs a simple object encapsulating the elements of a level
     *
     * @param regions A 2D array, where each element of the inner array
     *                      represents an "island" in the board. That island is
     *                      represented as an array of coordinates
     * @param obstacles An array of the obstacles in the level
     * @param hosts An array of the hosts in the level
     * @param start The "host" where the player starts
     */
    public Level(
            Vector2[][] regions,
            BoxObstacle[] obstacles,
            WaterTile[] water,
            ArrayList<HostModel> hosts,
            //TODO REMOVE THIS
            SpiritModel start,
            Pedestal ped
    ) {
        set(regions, obstacles, water, hosts, start, ped);
    }

    /**
     * An array of regions on the board. Each region is an island within which
     * hosts can be located. Outside each counts as out of bounds.
     *
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

    /** An array of the water tiles in the level */
    public WaterTile[] water;

    /** An array of the hosts in the level */
    public ArrayList<HostModel> hosts;

    /** The "host" where the player starts */
    public SpiritModel start;

    /** The pedestal where the player starts */
    public Pedestal ped;


    public void set(
            Vector2[][] regions,
            BoxObstacle[] obstacles,
            WaterTile[] water,
            ArrayList<HostModel> hosts,
            SpiritModel start,
            Pedestal ped
    ) {
        this.regions = regions;
        this.obstacles = obstacles;
        this.water = water;
        this.hosts = hosts;
        this.start = start;
        this.ped = ped;
    }

}
