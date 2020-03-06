package edu.cornell.gdiac.physics.robot;


//LIMIT JAVA.UTIL TO THE INTERFACES
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import com.badlogic.gdx.graphics.*;
import edu.cornell.gdiac.physics.GameCanvas;

/**
 * This class provides a list of ships for the game.
 *
 * This object may be used in for-each loops.  However, IT IS NOT THREAD-SAFE.
 * For memory reasons, this object is backed by a single iterator object that
 * is reset every single time we start a new for-each loop.
 */
public class RobotList implements Iterable<RobotModel> {

    /** The list of ships managed by this object. */
    private ArrayList<RobotModel> robots;
    /** The amount of time that has passed since creation (for animation) */
    private float time;
    /** Custom iterator so we can use this object in for-each loops */
    private RobotIterator iterator = new RobotIterator();

    private int possessed;


    /**
     * Create a new ShipList with the given number of ships.
     *
     * @param size The number of ships to allocate
     */
    public RobotList() {
        robots = new ArrayList<RobotModel>();
    }

    public void add(RobotModel r, boolean isPossessed) {
        robots.add(r);
        if(isPossessed) {
            possessed = robots.indexOf(r);
        }
    }

    /**
     * Returns the number of ships in this list
     *
     * @return the number of ships in this list
     */
    public int size() {
        return robots.size();
    }

    /**
     * Returns the ship for the given (unique) id
     *
     * The value given must be between 0 and size-1.
     *
     * @return the ship for the given id
     */
    public RobotModel get(int id) {
        return robots.get(id);
    }

    /**
     * Returns the ship for the player
     *
     * @return the ship for the player
     */
    public RobotModel getPossessed() {
        return robots.get(possessed);
    }


    /**
     * Returns the number of ships alive at the end of an update.
     *
     * @return the number of ships alive at the end of an update.
     */
    public int numDead() {
        int robotsDead = 0;
        for (RobotModel r : this) {
            if (!r.isAlive()) {
                robotsDead++;
            }
        }
        return robotsDead;
    }

    /**
     * Returns a ship iterator, satisfying the Iterable interface.
     *
     * This method allows us to use this object in for-each loops.
     *
     * @return a ship iterator.
     */
    public Iterator<RobotModel> iterator() {
        // Take a snapshot of the current state and return iterator.
        iterator.pos = 0;
        return iterator;
    }

    /**
     * Implementation of a custom iterator.
     *
     * Iterators are notorious for making new objects all the time.  We make
     * a custom iterator to cut down on memory allocation.
     */
    private class RobotIterator implements Iterator<RobotModel> {
        /** The current position in the ship list */
        public int pos = 0;

        /**
         * Returns true if there are still items left to iterate.
         *
         * @return true if there are still items left to iterate
         */
        public boolean hasNext() {
            return pos < robots.size();
        }

        /**
         * Returns the next ship.
         *
         * Dead ships are skipped, but inactive ships are not skipped.
         */
        public RobotModel next() {
            if (pos >= robots.size()) {
                throw new NoSuchElementException();
            }
            int idx = pos++;

            return robots.get(idx);
        }

        @Override
        public void remove() {

        }
    }
}