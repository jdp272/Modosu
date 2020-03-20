package edu.cornell.gdiac.physics.host;

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

    private static enum Move {
        /** The AI does not move */
        LEFT,
        /** The AI moves back and forth from the set path */
        RIGHT,
        /** The AI has been possessed by the player */
        UP,
        /** The AI pathfinds back to the set path */
        DOWN,

        STOP
    }

    // Instance Attributes
    /** The host being controlled by this AIController */
    private HostModel host;

    /** The game board; used for pathfinding */
    // private Board board;

    /** The other hosts */
    private HostList fleet; // need to know how many are possessed, etc

    /** The AI's current state in the FSM */
    private FSMState state;

    /** The AI's next action */
    private Move move; // A ControlCode

    /** The number of ticks since we started this controller */
    private long ticks;

    private int target;

    /**
     * Creates an AIController for the host with the given id.
     *
     * @param id The unique host identifier
     // * @param board The game board (for pathfinding)
     // * @param ships The list of hosts (for targetting)
     */
    public AIController(int id, HostList hosts) {

    }

    public void selectTarget(){

    }




}