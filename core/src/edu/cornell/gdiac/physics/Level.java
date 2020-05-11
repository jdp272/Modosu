package edu.cornell.gdiac.physics;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.obstacle.*;
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
        set(null, null, null, null, null, null, null, null, null, null, null);
    }

    /**
     * Constructs a simple object encapsulating the elements of a level
     *
     * @param dimensions The dimensions of the rectangular board in Box2D
     *                   coordinates
     * @param walls An array of the walls in the level
     * @param water An array of the water tiles in the level
     * @param sand An array of the sand tiles in the level
     * @param borderEdges An array of the border edges in the level
     * @param borderCorners An array of the border corners in the level
     * @param hosts An array of the hosts in the level
     * @param spirit The spirit of the player
     * @param pedestal The "host" where the player starts
     */
    public Level(
            Vector2 dimensions,
            Wall[] walls,
            WaterTile[] water,
            SandTile[] sand,
            BorderEdge[] borderEdges,
            BorderCorner[] borderCorners,
            EnergyPillar[] energyPillars,
            ArrayList<HostModel> hosts,
            HostModel pedestal,
            SpiritModel spirit
    ) {
        set(dimensions, walls, water, sand, borderEdges, borderCorners, energyPillars, hosts, pedestal, spirit, null);
    }

    /**
     * Constructs a simple object encapsulating the elements of a level
     *
     * @param dimensions The dimensions of the rectangular board in Box2D
     *                   coordinates
     * @param walls An array of the walls in the level
     * @param water An array of the water tiles in the level
     * @param sand An array of the sand tiles in the level
     * @param borderEdges An array of the border edges in the level
     * @param borderCorners An array of the border corners in the level
     * @param hosts An array of the hosts in the level
     * @param pedestal The "host" where the player starts
     * @param spirit The spirit of the player
     * @param message An optional message to display on the level
     */
    public Level(
            Vector2 dimensions,
            Wall[] walls,
            WaterTile[] water,
            SandTile[] sand,
            BorderEdge[] borderEdges,
            BorderCorner[] borderCorners,
            EnergyPillar[] energyPillars,
            ArrayList<HostModel> hosts,
            HostModel pedestal,
            SpiritModel spirit,
            String message
    ) {
        set(dimensions, walls, water, sand, borderEdges, borderCorners, energyPillars, hosts, pedestal, spirit, message);
    }

    /** The dimensions of the rectangular board, in Box2D coordinates */
    public Vector2 dimensions;

    /** An array of the walls in the level */
    public Wall[] walls;

    /** An array of the water tiles in the level */
    public WaterTile[] water;

    /** An array of the sand tiles in the level */
    public SandTile[] sand;

    /** An array of the border edges in the level */
    public BorderEdge[] borderEdges;

    /** An array of the border corners in the level */
    public BorderCorner[] borderCorners;

    /** An array of the energy pillars in the level */
    public EnergyPillar[] energyPillars;

    /** An array of the hosts in the level */
    public ArrayList<HostModel> hosts;

    /** The pedestal where the starting spirit starts */
    public HostModel pedestal;

    /** The spirit for the level */
    public SpiritModel spirit;

    /** An optional message to display on the level */
    public String message;

    /**
     * Constructs a simple object encapsulating the elements of a level
     *
     * @param dimensions The dimensions of the rectangular board in Box2D
     *                   coordinates
     * @param walls An array of the walls in the level
     * @param water An array of the water tiles in the level
     * @param sand An array of the sand tiles in the level
     * @param borderEdges An array of the border edges in the level
     * @param borderCorners An array of the border corners in the level
     * @param hosts An array of the hosts in the level
     * @param pedestal The pedestal where the player starts
     * @param spirit The spirit of the player
     * @param message An optional message to display on the level
     */
    public void set(
            Vector2 dimensions,
            Wall[] walls,
            WaterTile[] water,
            SandTile[] sand,
            BorderEdge[] borderEdges,
            BorderCorner[] borderCorners,
            EnergyPillar[] energyPillars,
            ArrayList<HostModel> hosts,
            HostModel pedestal,
            SpiritModel spirit,
            String message
    ) {
        this.dimensions = dimensions;
        this.walls = walls;
        this.water = water;
        this.sand = sand;
        this.borderEdges = borderEdges;
        this.borderCorners = borderCorners;
        this.energyPillars = energyPillars;
        this.hosts = hosts;
        this.pedestal = pedestal;
        this.spirit = spirit;
        this.message = message;
    }

}
