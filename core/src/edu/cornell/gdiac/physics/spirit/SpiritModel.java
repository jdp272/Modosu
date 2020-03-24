package edu.cornell.gdiac.physics.spirit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.physics.GameCanvas;
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
    /** If the spirit was slingshotted i.e. true if not possessing host, false if possessing host */
    public boolean hasLaunched;
    /** The default life of the spirit **/
    private float defaultLife;
    /** Current life of the spirit **/
    private float currentLife;
    /** Boolean indicating whether spirit is alive or not */
    private boolean isAlive;

    public SpiritModel(float x, float y) {
        super(x, y, 10, 10);
        bounces = SPIRIT_BOUNCES;
        hasLaunched = false;
        isAlive = true;
    }

    public SpiritModel(float x, float y, int b) {
        super(x, y, 10, 10);
        bounces = b;
        isAlive = true;
    }

    public SpiritModel(float x, float y, float width, float height, int b, float defaultLife) {
        super(x, y, width, height);

        setDensity(SPIRIT_DENSITY);
        setFriction(SPIRIT_FRICTION);
        setRestitution(SPIRIT_RESTITUTION);

        bounces = b;
        this.defaultLife = defaultLife;
        this.currentLife = this.defaultLife;
        isAlive = true;
    }

    /**
     * Gets the default life amount of the spirit
     * @return float indicating the starting life of the spirit
     */
    public float getDefaultLife() {
        return defaultLife;
    }

    /**
     * Sets the default life amount of spirit
     * @param defaultLife indicating the new defaultLife to set to
     */
    public void setDefaultLife(float defaultLife) {
        this.defaultLife = defaultLife;
    }

    /**
     * Gets the current life amount of the spirit
     * @return float indicating the current life
     */
    public float getCurrentLife() {
        return currentLife;
    }

    /**
     * Sets the current life amount of the spirit
     * @param currentLife indicating the new current life to set to
     */
    public void setCurrentLife(float currentLife) {
        this.currentLife = currentLife;
    }

    /**
     * Gets whether spirit is alive
     * @return true if spirit is alive
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Sets whether spirit is alive
     * @param alive indicates whether spirit is alive
     */
    public void setAlive(boolean alive) {
        isAlive = alive;
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

    /**
     * returns boolean indicating whether or not decrementing of health is possible
     * @return false if current life can be decremented
     */
    public boolean decCurrentLife() {
        // When the
        if(this.hasLaunched) {
            if (this.currentLife == 0) {
                // spirit is dead
                this.isAlive = false;
                return false;
            }
            else {
                this.currentLife--;
                return true;
            }
        }
        // In the case spirit is back in the bot it should reset the life to default
        else {
            this.setCurrentLife(this.getDefaultLife());
            return false;
        }
    }

    /**
     * Draws the Spirit
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        // Color fades as life progression decreases
        float lifeProgression = this.currentLife / this.defaultLife;
        Color lifeColor = new Color(1, 1, 1, lifeProgression);
        canvas.draw(texture, lifeColor, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1, 1);
    }
}
