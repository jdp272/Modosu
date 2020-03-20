/**
 * HostList.java
 *
 * A list class for the Host object
 *
 * Based on Lab2 provided by CS 3152 @ Cornell University
 */
package edu.cornell.gdiac.physics.host;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;



/**
 * This class provides a list of ships for the game.
 *
 * This object may be used in for-each loops.  However, IT IS NOT THREAD-SAFE.
 * For memory reasons, this object is backed by a single iterator object that
 * is reset every single time we start a new for-each loop.
 */
public class HostList implements Iterable<HostModel> {

    /** The list of hosts managed by this object. */
    private ArrayList<HostModel> hosts;
    /** The amount of time that has passed since creation (for animation) */
    private float time;
    /** Custom iterator so we can use this object in for-each loops */
    private HostIterator iterator = new HostIterator();

    private int possessed;


    /**
     * Create a new ShipList with the given number of ships.
     *
     * @param size The number of ships to allocate
     */
    public HostList() {
        hosts = new ArrayList<HostModel>();
    }

    public void add(HostModel r, boolean isPossessed) {
        hosts.add(r);
        if(isPossessed) {
            possessed = hosts.indexOf(r);
        }
    }

    /**
     * Returns the number of ships in this list
     *
     * @return the number of ships in this list
     */
    public int size() {
        return hosts.size();
    }

    /**
     * Returns the ship for the given (unique) id
     *
     * The value given must be between 0 and size-1.
     *
     * @return the ship for the given id
     */
    public HostModel get(int id) {
        return hosts.get(id);
    }

    /**
     * Returns the ship for the player
     *
     * @return the ship for the player
     */
    public HostModel getPossessed() {
        return hosts.get(possessed);
    }


    /**
     * Returns the number of ships alive at the end of an update.
     *
     * @return the number of ships alive at the end of an update.
     */
    public int numDead() {
        int hostsDead = 0;
        for (HostModel r : this) {
            if (!r.isAlive()) {
                hostsDead++;
            }
        }
        return hostsDead;
    }

    /**
     * Returns a ship iterator, satisfying the Iterable interface.
     *
     * This method allows us to use this object in for-each loops.
     *
     * @return a ship iterator.
     */
    public Iterator<HostModel> iterator() {
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
    private class HostIterator implements Iterator<HostModel> {
        /** The current position in the ship list */
        public int pos = 0;

        /**
         * Returns true if there are still items left to iterate.
         *
         * @return true if there are still items left to iterate
         */
        public boolean hasNext() {
            return pos < hosts.size();
        }

        /**
         * Returns the next ship.
         *
         * Dead ships are skipped, but inactive ships are not skipped.
         */
        public HostModel next() {
            if (pos >= hosts.size()) {
                throw new NoSuchElementException();
            }
            int idx = pos++;

            return hosts.get(idx);
        }

        @Override
        public void remove() {

        }
    }
}