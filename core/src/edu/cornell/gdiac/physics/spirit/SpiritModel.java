package edu.cornell.gdiac.physics.spirit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.GamePlayController;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.util.FilmStrip;


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

    private static final int SPIRIT_FRAME_STARTING = 0;

    /** The number of bounces of the character */
    public int bounces;
    /** If the spirit was slingshotted i.e. true if not possessing host, false if possessing host */
    public boolean hasLaunched;
    /** The default life of the spirit **/
    // TODO: Static and public for testing
    private float defaultLife;
    /** Current life of the spirit **/
    private float currentLife;
    /** Boolean indicating whether spirit is alive or not */
    private boolean isAlive;
    /** Boolean indicating whether spirit is in the center of a possessed host, false if not */
    private boolean isPossessing;
    /** Whether spirit will be moving towards the center of the possessed host, false if launched or already in the center */
    private boolean goToCenter;
    /** Whether spirit bounced or not*/
    private boolean didBounce;
    /** Position at Bounce */
    private Vector2 posAtBounce;

    // Animation related Variables

    /** The texture filmstrip for the spirit body */
    protected FilmStrip  spiritBodyStrip;
    /** The texture filmstrip for the spirit tail */
    protected FilmStrip spiritTailStrip;
    /** The texture filmstrip for the spirit head */
    protected FilmStrip spiritHeadStrip;
    /** The number of frames that have elapsed since the last animation update */
    private int elapsedFrames = 0;
    /** The number of frames that should pass before the animation updates */
    private int framesPerUpdate = 8;
    /** Whether or not animation should be updated on this frame */
    private boolean updateFrame;



    public SpiritModel(float x, float y) {
        super(x, y, 10, 10);
        bounces = SPIRIT_BOUNCES;
        hasLaunched = false;
        isAlive = true;
        goToCenter = false;
        isPossessing = false;
        this.posAtBounce = this.getPosition();
    }

    public SpiritModel(float x, float y, int b) {
        super(x, y, 10, 10);
        bounces = b;
        isAlive = true;
        this.posAtBounce = this.getPosition();
    }

    public SpiritModel(float x, float y, float width, float height, int b, float defaultLife) {
        super(x, y, width, height);

        setDensity(SPIRIT_DENSITY);
        setFriction(SPIRIT_FRICTION);
        setRestitution(SPIRIT_RESTITUTION);
        setFixedRotation(true);
//        float[] verts = {-0.25f,0,
//                -0.2f,-0.2f,
//                0,-0.25f,
//                0.2f,-0.2f,
//                0.25f,0,
//                0.2f,0.2f,
//                0,0.25f,
//                -0.2f,0.2f};
//        shape.set(verts);

        bounces = b;
        this.defaultLife = defaultLife;
        this.currentLife = this.defaultLife;
        isAlive = true;
        this.posAtBounce = this.getPosition();

    }

    /**
     * Gets the position of spirit at bounce
     * @return vector2 object of position at bounce
     */
    public Vector2 getPosAtBounce() {
        return posAtBounce;
    }

    /**
     * Sets the position of the spirit at bounce
     * @param posAtBounce indicates the position of the spirit at bounce
     */
    public void setPosAtBounce(Vector2 posAtBounce) {
        this.posAtBounce = posAtBounce;
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
     * Gets whether the spirit bounced
     * @return true if spirit has bounced
     */
    public boolean getDidBounce() {
        return this.didBounce;
    }

    /**
     * Sets whether the spirit bounced
     * @param didBounce indicates whether or not spirit bounced
     */
    public void setDidBounce(boolean didBounce) {
        this.didBounce = didBounce;
    }

    /**
     * Sets whether spirit is alive
     * @param alive indicates whether spirit is alive
     */
    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean outOfBounces() {
        return (bounces == 0);
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

    public boolean getGoToCenter() {
        return goToCenter;
    }

    public  void setGoToCenter(boolean goCenter) {
        goToCenter = goCenter;
    }

    public boolean decBounces() {
            if (bounces == 0) {
                return false;
            } else {
                bounces--;
                return true;
            }
        }


    /**
     * returns the velocity vector
     * @return velocity vector
     */
    public Vector2 getVelocity() {
        return new Vector2(this.getVX(), this.getVY());
    }

    public boolean getIsPossessing() {
        return isPossessing;
    }

    public void setIsPossessing(boolean isCurrPossessing) {
        isPossessing = isCurrPossessing;
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
     * sets all the film strips of the spirit
     * @param spiritBodyStrip filmstrip for the spirit's body
     * @param spiritHeadStrip filmstrip for the spirit's head
     * @param spiritTailStrip filmstrip for the spirit's tail
     */
    public void setFilmStrip (FilmStrip spiritBodyStrip, FilmStrip spiritHeadStrip, FilmStrip spiritTailStrip) {
        this.spiritBodyStrip = spiritBodyStrip;
        this.spiritBodyStrip.setFrame(SPIRIT_FRAME_STARTING);
        this.spiritHeadStrip = spiritHeadStrip;
        this.spiritHeadStrip.setFrame(SPIRIT_FRAME_STARTING);
        this.spiritTailStrip = spiritTailStrip;
        this.spiritTailStrip.setFrame(SPIRIT_FRAME_STARTING);
    }
    
    public void updateAnimation () {
        elapsedFrames++;
        updateFrame = true;

        if (elapsedFrames >= framesPerUpdate) {
            updateFrame = true;
            elapsedFrames = 0;
        }
        if (updateFrame) {
            if ((this.spiritBodyStrip.getFrame() < this.spiritBodyStrip.getSize() - 1) && (this.spiritHeadStrip.getFrame() < this.spiritHeadStrip.getSize() - 1) &&
                    (this.spiritTailStrip.getFrame() < this.spiritTailStrip.getSize() - 1)) {
                this.spiritBodyStrip.setFrame(this.spiritBodyStrip.getFrame() + 1);
                this.spiritTailStrip.setFrame(this.spiritTailStrip.getFrame() + 1);
                this.spiritHeadStrip.setFrame(this.spiritHeadStrip.getFrame() + 1);
            } else {
                this.spiritBodyStrip.setFrame(SPIRIT_FRAME_STARTING);
                this.spiritHeadStrip.setFrame(SPIRIT_FRAME_STARTING);
                this.spiritTailStrip.setFrame(SPIRIT_FRAME_STARTING);
            }
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
        float disFromBounce = (Vector2.dst2(this.getPosition().x, this.getPosition().y, this.getPosAtBounce().x, this.getPosAtBounce().y)) / 8;
        Color lifeColor = new Color(1, 1, 1, lifeProgression);
        Color tailColor = new Color(1, 1,1, lifeProgression * disFromBounce);



        // Only draw spirit when it's flying
        if (!isPossessing && !goToCenter) {


            if(this.didBounce) {
                canvas.draw(spiritHeadStrip, lifeColor, spiritHeadStrip.getRegionWidth() - 14, spiritHeadStrip.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, this.getVelocity().angleRad(), 0.75f, 0.75f);
                canvas.draw(spiritTailStrip, Color.CLEAR, spiritTailStrip.getRegionWidth() - 14, spiritTailStrip.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, this.getVelocity().angleRad(), 0.75f, 0.75f);
            }
            else {
                canvas.draw(spiritHeadStrip, lifeColor, spiritHeadStrip.getRegionWidth() - 14, spiritHeadStrip.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, this.getVelocity().angleRad(), 0.75f, 0.75f);
                if(Vector2.dst2(this.getPosition().x, this.getPosition().y, this.getPosAtBounce().x, this.getPosAtBounce().y) >= 8f) {
                    canvas.draw(spiritTailStrip, lifeColor, spiritTailStrip.getRegionWidth() - 14, spiritTailStrip.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, this.getVelocity().angleRad(), 0.75f, 0.75f);
                }
                else {
                    canvas.draw(spiritTailStrip, tailColor, spiritHeadStrip.getRegionWidth() - 14, spiritHeadStrip.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, this.getVelocity().angleRad(), 0.75f, 0.75f);
                }
                }
            }
        }
    }
