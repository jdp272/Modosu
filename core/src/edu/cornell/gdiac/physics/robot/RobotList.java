package edu.cornell.gdiac.physics.robot;

/*
 * ShipList.java
 *
 * Like PhotonPool, this class manages a large number of objects in the game, many of
 * which can be deleted.  However, since we are never adding new ships to the game --
 * only taking them away -- this makes this class a lot simpler.
 *
 * Unlike PhotonPool, this method has no update.  Updates are different for players
 * and AI ships, so we have embedded
 *
 * This class does have an important similarity to PhotonPool. It implements
 * Iterable<Ship> so that we can use it in for-each loops. BE VERY CAREFUL with
 * java.util.  Those classes are notorious for memory allocation. You will note that,
 * to save memory, we have exactly one iterator that we reused over and over again.
 * This helps with memory, but it means that this object is not even remotely thread-safe.
 * As there is only one thread in the game-loop, this is acceptable.
 *
 * Author: Walker M. White, Cristian Zaloj
 * Based on original AI Game Lab by Yi Xu and Don Holden, 2007
 * LibGDX version, 1/24/2015
 */
package edu.cornell.gdiac.ailab;

//LIMIT JAVA.UTIL TO THE INTERFACES
import java.util.Iterator;
import java.util.NoSuchElementException;
import com.badlogic.gdx.graphics.*;
import edu.cornell.gdiac.mesh.*;

/**
 * This class provides a list of ships for the game.
 *
 * This object may be used in for-each loops.  However, IT IS NOT THREAD-SAFE.
 * For memory reasons, this object is backed by a single iterator object that
 * is reset every single time we start a new for-each loop.
 */
public class RobotList implements Iterable<RobotModel> {
    /** The color to tint the afterburner fire */
    private static final Color FIRE_COLOR = new Color(0.855f,0.647f,0.125f,1.0f);

    /** Texture+Mesh for enemy. Only need one, since all have same geometry */
    private TexturedMesh enemyMesh;
    /** Texture+Mesh for player. Only need one, since all have same geometry */
    private TexturedMesh playerMesh;
    /** Texture+Mesh for after burner. Nonstatic so that we can vary color */
    private TexturedMesh fireMesh;

    /** The list of ships managed by this object. */
    private Ship[] ships;
    /** The amount of time that has passed since creation (for animation) */
    private float time;
    /** Custom iterator so we can use this object in for-each loops */
    private ShipIterator iterator = new ShipIterator();


    /**
     * Create a new ShipList with the given number of ships.
     *
     * @param size The number of ships to allocate
     */
    public ShipList(int size) {
        ships = new Ship[size];
        for (int ii = 0; ii < size; ii++) {
            ships[ii] = new Ship(ii, 0, 0);
        }
    }

    /**
     * Returns the number of ships in this list
     *
     * @return the number of ships in this list
     */
    public int size() {
        return ships.length;
    }

    /**
     * Returns the ship for the given (unique) id
     *
     * The value given must be between 0 and size-1.
     *
     * @return the ship for the given id
     */
    public Ship get(int id) {
        return ships[id];
    }

    /**
     * Returns the ship for the player
     *
     * @return the ship for the player
     */
    public Ship getPlayer() {
        return ships[0];
    }

    /**
     * Returns the number of ships alive at the end of an update.
     *
     * @return the number of ships alive at the end of an update.
     */
    public int numActive() {
        int shipsActive = 0;
        for (Ship s : this) {
            if (s.isActive()) {
                shipsActive++;
            }
        }
        return shipsActive;
    }

    /**
     * Returns the number of ships alive at the end of an update.
     *
     * @return the number of ships alive at the end of an update.
     */
    public int numAlive() {
        int shipsAlive = 0;
        for (Ship s : this) {
            if (s.isAlive()) {
                shipsAlive++;
            }
        }
        return shipsAlive;
    }

    /**
     * Returns the textured mesh for the player
     *
     * We only need one copy of the mesh, as there is one player.
     *
     * @return the textured mesh for the player
     */
    public TexturedMesh getPlayerMesh() {
        return playerMesh;
    }

    /**
     * Sets the textured mesh for the player
     *
     * We only need one copy of the mesh, as there is one player.
     *
     * @param mesh the textured mesh for the player
     */
    public void setPlayerMesh(TexturedMesh mesh) {
        playerMesh = mesh;
    }

    /**
     * Returns the textured mesh for the enemy
     *
     * We only need one copy of the mesh, as all enemies look the same.
     *
     * @return the textured mesh for the enemy
     */
    public TexturedMesh getEnemyMesh() {
        return playerMesh;
    }

    /**
     * Sets the textured mesh for the enemy
     *
     * We only need one copy of the mesh, as all enemies look the same.
     *
     * @param mesh the textured mesh for the enemy
     */
    public void setEnemyMesh(TexturedMesh mesh) {
        enemyMesh = mesh;
    }

    /**
     * Returns the textured mesh for the afterburner
     *
     * We only need one copy of the mesh, as all ships use the same afterburner.
     *
     * @return the textured mesh for the afterburner
     */
    public TexturedMesh getFireMesh() {
        return fireMesh;
    }

    /**
     * Sets the textured mesh for the afterburner
     *
     * We only need one copy of the mesh, as all ships use the same afterburner.
     *
     * @param mesh the textured mesh for the afterburner
     */
    public void setFireMesh(TexturedMesh mesh) {
        fireMesh = mesh;
        if (mesh != null) {
            fireMesh.setColor(new Color(FIRE_COLOR));
        }
    }

    /**
     * Draws the ships to the given canvas.
     *
     * This method draws all of the ships in this list. It should be the second drawing
     * pass in the GameEngine.
     *
     * @param canvas the drawing context
     */
    public void draw(GameCanvas canvas) {
        // Increment the animation factor
        time += 0.05f;

        for (Ship s : this) {
            // Draw the ship
            TexturedMesh model = (s.getId() == 0 ? enemyMesh : playerMesh);
            canvas.drawShip(model, s.getX(), s.getY(), s.getFallAmount(), s.getAngle());
        }

        for (Ship s : this) {
            // Draw the after burner
            fireMesh.getColor().a = generateNoise(time % 1.0f)*Math.min(1, s.getVelocity().len2());
            canvas.drawFire(fireMesh, s.getX(), s.getY(), s.getFallAmount(), s.getAngle());
        }
    }

    /**
     * Generates the Perlin Noise for the after burner
     *
     * Cristian came up with these numbers (and did not document them :( ).  I have
     * no idea what they mean.
     *
     * @param fx seed value for random noise.
     */
    private float generateNoise(float fx) {
        float noise = (float)(188768.0 * Math.pow(fx, 10));
        noise -= (float)(874256.0 * Math.pow(fx, 9));
        noise += (float)(1701310.0 * Math.pow(fx, 8));
        noise -= (float)(1804590.0 * Math.pow(fx, 7));
        noise += (float)(1130570.0 * Math.pow(fx, 6));
        noise -= (float)(422548.0 * Math.pow(fx, 5));
        noise += (float)(89882.7 * Math.pow(fx, 4));
        noise -= (float)(9425.33 * Math.pow(fx, 3));
        noise += (float)(276.413 * fx * fx);
        noise += (float)(14.3214 * fx);
        return noise;
    }

    /**
     * Returns a ship iterator, satisfying the Iterable interface.
     *
     * This method allows us to use this object in for-each loops.
     *
     * @return a ship iterator.
     */
    public Iterator<Ship> iterator() {
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
    private class ShipIterator implements Iterator<Ship> {
        /** The current position in the ship list */
        public int pos = 0;

        /**
         * Returns true if there are still items left to iterate.
         *
         * @return true if there are still items left to iterate
         */
        public boolean hasNext() {
            return pos < ships.length;
        }

        /**
         * Returns the next ship.
         *
         * Dead ships are skipped, but inactive ships are not skipped.
         */
        public Ship next() {
            if (pos >= ships.length) {
                throw new NoSuchElementException();
            }
            int idx = pos;
            do {
                pos++;
            } while (pos < ships.length && !ships[pos].isAlive());
            return ships[idx];
        }
    }
}