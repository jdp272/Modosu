package edu.cornell.gdiac.physics.robot;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;


public class AIController extends InputController {

    private static enum FSMState {
        /** The AI does not move */
        STATIC,
        /** The AI moves back and forth from the set path */
        WANDER,
        /** The AI has been possessed by the player */
        POSSESS,
        /** The AI pathfinds back to the set path */
        DISPOSSESS
    }

    // Instance Attributes
    /** The robot being controlled by this AIController */
    private RobotModel robot;

    /** The game board; used for pathfinding */
    // private Board board;

    /** The other robots */
    private RobotList fleet; // need to know how many are possessed, etc

    /** The AI's current state in the FSM */
    private FSMState state;

    /** The AI's next action */
    private int move; // A ControlCode

    /** The number of ticks since we started this controller */
    private long ticks;

    /**
     * Creates an AIController for the robot with the given id.
     *
     * @param id The unique robot identifier
     // * @param board The game board (for pathfinding)
     // * @param ships The list of ships (for targetting)
     */
    public AIController(int id, Board board, ShipList ships) {
        this.ship = ships.get(id);
        this.board = board;
        this.fleet = ships;

        state = FSMState.SPAWN;
        move  = CONTROL_NO_ACTION;
        ticks = 0;

        // Select an initial target
        target = null;
        selectTarget();
    }




}