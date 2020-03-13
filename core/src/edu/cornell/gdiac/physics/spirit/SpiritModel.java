package edu.cornell.gdiac.physics.spirit;

import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;


public class SpiritModel extends BoxObstacle {

    // Physics constants
    /** The density of the character */
    private static final float SPIRIT_DENSITY = 1.0f;
    /** The factor to multiply by the input */
    private static final float SPIRIT_FRICTION = 0.0f;
    /** The restitution of the character */
    private static final float SPIRIT_RESTITUTION = 0.8f;

    // Spirit constants
    private static final int SPIRIT_BOUNCES = 4;

    /** The number of bounces of the character */
    public int bounces;
    /** If the spirit was slingshotted */
    public boolean hasLaunched;


    public SpiritModel(float x, float y) {
        super(x, y, 10, 10);
        bounces = SPIRIT_BOUNCES;
        hasLaunched = false;
    }

    public SpiritModel(float x, float y, int b) {
        super(x, y, 10, 10);
        bounces = b;
    }

    public SpiritModel(float x, float y, float width, float height, int b) {
        super(x, y, width, height);

        setDensity(SPIRIT_DENSITY);
        setFriction(SPIRIT_FRICTION);
        setRestitution(SPIRIT_RESTITUTION);

        bounces = b;
    }


    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     *
     * This method overrides the base method to keep your ship from spinning.
     *
     * @param world Box2D world to store body
     *
     * @return true if object allocation succeeded
     */
    public boolean activatePhysics(World world) {
        // create the box from our superclass
        if (!super.activatePhysics(world)) {
            return false;
        }

        return true;
    }

    public boolean getHasLaunched() {
        return hasLaunched;
    }

    public void setHasLaunched(boolean launched) {
        hasLaunched = launched;
    }

    public boolean decBounces(){
        if (bounces == 0){
            return false;
        } else {
            bounces--;
            return true;
        }
    }
}
