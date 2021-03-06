package edu.cornell.gdiac.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.obstacle.OscWall;
import edu.cornell.gdiac.physics.spirit.SpiritModel;

import java.util.ArrayList;

public class CollisionController implements ContactListener {

    /**
     * Whether the host was bounced against a wall this frame
     */
    private boolean bounced;

    /**
     * Whether the host is walking through sand this frame
     */
    private boolean inSand;
    /**
     * Whether the host bounced on the bounds this frame
     */
    private boolean bounceOnBounds;
    /**
     * What host was possessed this frame, null if no possession occurred
     */
    private HostModel hostPossessed;

    /**
     * What host was possessed last frame, null if no possession occurred
     */
    private HostModel prevHostPossessed;

    // Physics objects for the game
    /**
     * Reference to the hosts
     */
    private ArrayList<HostModel> hostList;

    /**
     * Reference to the spirit
     */
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
        inSand = false;
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
     * <p>
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

        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        // Collision handling to determine if the spirit collides with any hosts
        for (HostModel r : hostList) {
            if (((body1.getUserData() == spirit && body2.getUserData() == r) ||
                    (body1.getUserData() == r && body2.getUserData() == spirit)) & !spirit.getIsPossessing()) {

                hostPossessed = r;

                // Increment HUD if host has never been possessed before
                if (!hostPossessed.beenPossessed()) {
                    HUD.incrementCurrHosts();
                }

                // host is now possessed
                hostPossessed.setPossessed(true);

                // set impact for screen shake
                hostPossessed.setImpact(true);

                // Spirit's life is replenished upon possessing new host
                spirit.setCurrentLife(spirit.getDefaultLife());

                // spirit is no longer in stage of being launched
                spirit.setHasLaunched(false);

                // Spirit is alive whenever it is inside of a host
                spirit.setAlive(true);

                // Spirit is Going to Center Now
                spirit.setGoToCenter(true);

                // Spirit moves towards the possessed's center if wasn't already colliding with it last frame
                if (hostPossessed != prevHostPossessed) {
                    spirit.setGoToCenter(true);
                }
            }

            if (((body2.getUserData() == r) || (body1.getUserData() == r)) && !r.isPossessed()) {
                Vector2 c = contact.getWorldManifold().getPoints()[0].sub(r.getPosition());
                Vector2 v = r.getLinearVelocity();

                if ((Math.signum(c.x) == Math.signum(v.x) || Math.abs(v.x) < 0.1)
                        && (Math.signum(c.y) == Math.signum(v.y) || Math.abs(v.y) < 0.1)) {
                    r.invertForwardI();
                    r.nextInstruction();
                }
            }
        }
    }

    /**
     * Callback method for the start of a collision
     * This method is called when two objects cease to touch.
     */
    public void endContact(Contact contact) {
        Fixture fix1 = contact.getFixtureA();
        Fixture fix2 = contact.getFixtureB();

        Body body1 = fix1.getBody();
        Body body2 = fix2.getBody();

        if (hostList != null) {
            for (HostModel r : hostList) {
                if (((body2.getUserData() == r) || (body1.getUserData() == r)) && !r.isPossessed()) {
                    r.setLinearVelocity(new Vector2(0, 0));
                }
            }
        }
    }

    /**
     * Unused ContactListener method
     */
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    /**
     * Handles any modifications necessary before collision resolution
     * <p>
     * This method is called just before Box2D resolves a collision.  We use this method
     * to implement sound on contact, using the algorithms outlined similar to those in
     * Ian Parberry's "Introduction to Game Physics with Box2D".
     * <p>
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

        Obstacle bd1 = (Obstacle) body1.getUserData();
        Obstacle bd2 = (Obstacle) body2.getUserData();

        // Check for Collision with Pedestal
        if ((body1.getUserData() == spirit && bd2.getName() == "pedestal") ||
                (bd1.getName() == "pedestal" && body2.getUserData() == spirit) ||
                (bd1.getName() == "host" && bd2.getName() == "pedestal") ||
                (bd1.getName() == "pedestal" && bd2.getName() == "host")) {
            contact.setEnabled(false);
        }

        // Check for Spirit and Host Collision with OscWall
        if ((bd1.getName() == "oscWall" && !((OscWall) bd1).isPhysical() && body2.getUserData() == spirit) ||
                (body2.getUserData() == spirit && bd1.getName() == "oscWall" && !((OscWall) bd1).isPhysical()) ||
            (bd1.getName() == "oscWall" && !((OscWall) bd1).isPhysical() && bd2.getName() == "host") ||
                    (bd1.getName() == "host" && bd2.getName() == "oscWall" && !((OscWall) bd2).isPhysical())) {
            contact.setEnabled(false);
        }

        // Check for Collision with Water
        if ((body1.getUserData() == spirit && bd2.getName() == "water") ||
                bd1.getName() == "water" && body2.getUserData() == spirit) {
            contact.setEnabled(false);
        }

        // All collisions with Sand
        if ((bd1.getName() == "sand") || bd2.getName() == "sand") {
            contact.setEnabled(false);
        }

        // Check for Collision with Sand
        if ((body1.getUserData() == prevHostPossessed && bd2.getName() == "sand") ||
                bd1.getName() == "sand" && body2.getUserData() == prevHostPossessed) {
                inSand = true;
        }

        for (HostModel r : hostList) {
            if (((body1.getUserData() == spirit && body2.getUserData() == r) ||
                    (body1.getUserData() == r && body2.getUserData() == spirit))) {
                contact.setEnabled(false);
            }
        }

        // Recognize spirit against a wall to play sound
        if (body1.getUserData() == spirit && bd2.getName() == "wall" ||
                bd1.getName() == "wall" && body2.getUserData() == spirit ||
                body1.getUserData() == spirit && bd2.getName() == "edge" ||
                bd1.getName() == "edge" && body2.getUserData() == spirit ||
                body1.getUserData() == spirit && bd2.getName() == "corner" ||
                bd1.getName() == "corner" && body2.getUserData() == spirit ||
                bd1.getName() == "energyPillar" && body2.getUserData() == spirit ||
                body1.getUserData() == spirit && bd2.getName() == "energyPillar" ||
                bd1.getName() == "oscWall" && ((OscWall) bd1).isPhysical() && body2.getUserData() == spirit ||
                body1.getUserData() == spirit && bd2.getName() == "oscWall" && ((OscWall) bd2).isPhysical()) {
                spirit.setDidBounce(true);
                spirit.setPosAtBounce(new Vector2(spirit.getPosition()));
                bounced = true;
            if (body1.getUserData() == spirit && bd2.getName() == "edge" ||
                    bd1.getName() == "edge" && body2.getUserData() == spirit ||
                    body1.getUserData() == spirit && bd2.getName() == "corner" ||
                    bd1.getName() == "corner" && body2.getUserData() == spirit) {
                bounceOnBounds = true;
            }
        }
    }

    /**
     * Reset all the fields to reflect this current frame
     */
    public void clear() {
        bounced = false;
        inSand = false;
        bounceOnBounds = false;
    }

    // Getters

    /**
     * Getter method to return the possessed host
     */
    public HostModel getHostPossessed() {
        return hostPossessed;
    }

    /**
     * Getter method to return whether a possession occurred this frame
     */
    public boolean isPossessed() {
        return hostPossessed != null;
    }

    /**
     * Getter method to return whether a wall bounce occurred this frame
     */
    public boolean isBounced() {
        return bounced;
    }

    public boolean getInSand() {
        return inSand;
    }

    public boolean getBounceOnBounds() { return bounceOnBounds; }
}
