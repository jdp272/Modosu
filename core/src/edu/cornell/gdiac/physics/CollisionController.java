package edu.cornell.gdiac.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.spirit.SpiritModel;

import java.util.ArrayList;

public class CollisionController implements ContactListener {

    /** Whether the host was bounced against a wall this frame */
    private boolean bounced;

    /** Whether the robot has touched against a wall this frame */
    private boolean againstWall;

    /** What host was possessed this frame, null if no possession occurred */
    private HostModel hostPossessed;

    /** What host was possessed last frame, null if no possession occurred */
    private HostModel prevHostPossessed;

    // Physics objects for the game
    /** Reference to the hosts */
    private ArrayList<HostModel> hostList;

    /** Reference to the spirit */
    private SpiritModel spirit;

    /**
     * Creates and initializes a new instance of a CollisionController
     */
    public CollisionController() {
        this.spirit = null;
        hostList = null;
        bounced = false;
        hostPossessed = null;
        prevHostPossessed = null;
        againstWall = false;
    }

    /**
     * Resets the CollisionController to reflect an initial state
     */
    public void reset() {
        spirit = null;
        hostList = null;
        bounced = false;
        hostPossessed = null;
    }

    /**
     * Sets all the hosts
     */
    public void addHosts(ArrayList<HostModel> hosts) {
        hostList = hosts;
    }

    /**
     * Sets the spirit
     */
    public void addSpirit(SpiritModel spirit) {
        this.spirit = spirit;
    }

    // ContactListener methods

    /**
     * Callback method for the start of a collision
     *
     * This method is called when we first get a collision between two objects.  We use
     * this method to test if it is the "right" kind of collision.  In particular, we
     * use it to test if the spirit bounced against a wall or if the spirit bounced into a host.
     *
     * @param contact The two bodies that collided
     */
    public void beginContact(Contact contact) {
        // Reset all the fields to reflect this current frame if needed
        clear();
        prevHostPossessed = hostPossessed;
        possessed = false;
        hostPossessed = null;

        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        Obstacle bd1 = (Obstacle) body1.getUserData();
        Obstacle bd2 = (Obstacle) body2.getUserData();

        // Collision handling to determine if the spirit collides with any hosts
        for (HostModel r : hostList) {
            if (((body1.getUserData() == spirit && body2.getUserData() == r) ||
                    (body1.getUserData() == r && body2.getUserData() == spirit))) {

                hostPossessed = r;

                // host is now possessed
                hostPossessed.setPossessed(true);

                // Spirit's life is replenished upon possessing new host
                spirit.setCurrentLife(spirit.getDefaultLife());

                // spirit is no longer in stage of being launched
                spirit.setHasLaunched(false);

                // Spirit is alive whenever it is inside of a host
                spirit.setAlive(true);

                // Spirit moves towards the possessed's center if wasn't already colliding with it last frame
                if (hostPossessed != prevHostPossessed) {
                    spirit.setGoToCenter(true);
                }
            }
        }


        // Collision handling to determine if the spirit collides with any walls
        if (body1.getUserData() == spirit && bd2.getName() == "wall" ||
                bd1.getName() == "wall" && body2.getUserData() == spirit) {
            bounced = true;
        }


    }

    /**
     * Callback method for the start of a collision
     * This method is called when two objects cease to touch.  We do not use it.
     */
    public void endContact(Contact contact) {
    }

    private Vector2 cache = new Vector2();

    /** Unused ContactListener method */
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    /**
     * Handles any modifications necessary before collision resolution
     *
     * This method is called just before Box2D resolves a collision.  We use this method
     * to implement sound on contact, using the algorithms outlined similar to those in
     * Ian Parberry's "Introduction to Game Physics with Box2D".
     *
     * However, we cannot use the proper algorithms, because LibGDX does not implement
     * b2GetPointStates from Box2D.  The danger with our approximation is that we may
     * get a collision over multiple frames (instead of detecting the first frame), and
     * so play a sound repeatedly.  Fortunately, the cooldown hack in SoundController
     * prevents this from happening.
     *
     * @param contact     The two bodies that collided
     * @param oldManifold The collision manifold before contact
     */
    // Will need to modify this when we include sound effects upon wall and possession collisions
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        // Turn off collision handling if spirit already in the golem
        for (HostModel r : hostList) {
            if (((body1.getUserData() == spirit && body2.getUserData() == r) ||
                    (body1.getUserData() == r && body2.getUserData() == spirit)) && isPossessed()) {
                contact.setEnabled(false);
            }
        }
    }

    /** Reset all the fields to reflect this current frame */
    public void clear() {
        bounced = false;
        againstWall = false;
    }

    // Getters

    /** Getter method to return the possessed host */
    public HostModel getHostPossessed() { return hostPossessed; }

    /** Getter method to return whether a possession occurred this frame */
    public boolean isPossessed() { return hostPossessed != null; }

    /** Getter method to return whether a wall bounce occurred this frame */
    public boolean isBounced() { return bounced; }

    public boolean isAgainstWall() {
        return againstWall;
    }
}
