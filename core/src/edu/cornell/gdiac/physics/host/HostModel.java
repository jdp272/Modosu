/*
 * HostModel.java
 *
 * Model class for Hosts.
 *
 * Author: Grant Lee
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 3/1/2020
 */
package edu.cornell.gdiac.physics.host;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.util.FilmStrip;

/**
 * Avatar representing the possessable golems that roam the map
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class HostModel extends BoxObstacle {

    // Default physics values
    /** The density of this host */
    private static final float DEFAULT_DENSITY = 1.0f;
    /** The friction of this host */
    private static final float DEFAULT_FRICTION = 0.1f;
    /** The restitution of this host */
    private static final float DEFAULT_RESTITUTION = 0.4f;
    /** The thrust factor to convert player input into host movement */
    private static final float DEFAULT_THRUST = 7.0f;
    /** The number of frames for the gauge */
    public static final int GAUGE_FRAMES = 4;

    /** The texture for the host's gauge */
    protected TextureRegion hostGaugeTexture;

    /** The texture filmstrip for charge gauge when possessed */
    FilmStrip chargeGauge;
    /** The associated sound for the HostModel when possessed */
    String possessedHostSound;
    /** The animation phase for the HostModel when possessed */
    boolean possessedCycle = true;

    /** The texture filmstrip for charge gauge when not possessed */
    FilmStrip normalGauge;
    /** The associated sound for charge gauge when not possessed */
    String normalHostSound;
    /** The animation phase for charge gauge when not possessed */
    boolean normCycle = true;

    /** The texture filmstrip for host's movements */
    FilmStrip hostStrip;
    /** The associated sound for host's movements */
    String hostSound;
    /** The animation phase for host's movements */
    boolean hostMvtCycle = true;

    // Attributes Specific to each HostModel
    /** Boolean Whether HostModel is Possessed */
    private boolean isPossessed;
    /** Boolean for whether host is alive */
    private boolean isAlive;
    /** The current charge of the host */
    private float currentCharge;
    /** The maximum charge of the host */
    private float maxCharge;
    /** The force to apply to this host */
    private Vector2 force;
    /** Instructions for robot when unpossessed*/
    private Vector2[] instructions;
    /** Current instruction index */
    private int instructionNumber;
    /** Whether or not the robot has been possessed yet*/
    private boolean hasBeenPossessed;
    /** Whether robot is moving forward through instructions */
    private boolean forwardI;



    /** Cache object for transforming the force according the object angle */
    public Affine2 affineCache = new Affine2();

    /**
     * Gets the max charge a host can hold before exploding
     * @return float that represents the maximum charge that can be held
     */
    public float getMaxCharge() {
        return maxCharge;
    }

    /**
     * Sets the max charge a host can hold before exploding
     * @param maxCharge that represents the new max charge
     */
    public void setMaxCharge(float maxCharge) {
        this.maxCharge = maxCharge;
    }

    /**
     * Returns the force applied to this host.
     *
     * This method returns a reference to the force vector, allowing it to be modified.
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the force applied to this host.
     */
    public Vector2 getForce() {
        return force;
    }

    /**
     * Returns the Hosts' Gauge Texture
     *
     * This method returns the texture for the gauge
     *
     * @return the texture
     */
    public TextureRegion getHostGaugeTexture() {
        return this.hostGaugeTexture;
    }

    /**
     * Sets the Hosts' Gauge Texture
     *
     * This method sets the texture of the gauge
     */
    public void setHostGaugeTexture(TextureRegion hostGaugeTexture) {
        this.hostGaugeTexture = hostGaugeTexture;
    }

    /**
     * Returns the x-component of the force applied to this host.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the x-component of the force applied to this host.
     */
    public float getFX() {
        return force.x;
    }

    /**
     * Sets the x-component of the force applied to this host.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @param value the x-component of the force applied to this host.
     */
    public void setFX(float value) {
        force.x = value;
    }

    /**
     * Returns the y-component of the force applied to this host.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the y-component of the force applied to this host.
     */
    public float getFY() {
        return force.y;
    }

    /**
     * Sets the x-component of the force applied to this host.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @param value the x-component of the force applied to this host.
     */
    public void setFY(float value) {
        force.y = value;
    }

    /**
     * Creates a new host at the origin.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param width  The object width in physics units
     * @param height The object width in physics units
     */
    public HostModel(float width, float height, float currentCharge, float maxCharge) {
        super(0, 0, width, height);
        this.currentCharge = currentCharge;
        this.maxCharge = maxCharge;
        this.instructions = null;
        this.instructionNumber = 0;
        this.hasBeenPossessed = false;
        setDensity(DEFAULT_DENSITY);
        setDensity(DEFAULT_DENSITY);
        setFriction(DEFAULT_FRICTION);
        setRestitution(DEFAULT_RESTITUTION);
        isPossessed = false;
        isAlive = true;
        setName("host");
    }

    /**
     * Creates a new host at the given position.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x      Initial x position of the box center
     * @param y      Initial y position of the box center
     * @param width  The object width in physics units
     * @param height The object width in physics units
     */
    public HostModel(float x, float y, float width, float height, float currentCharge, float maxCharge) {
        super(x, y, width, height);
        force = new Vector2();
        this.currentCharge = currentCharge;
        this.maxCharge = maxCharge;
        this.instructions = null;
        this.instructionNumber = 0;
        this.hasBeenPossessed = false;
        setDensity(DEFAULT_DENSITY);
        setDensity(DEFAULT_DENSITY);
        setFriction(DEFAULT_FRICTION);
        setRestitution(DEFAULT_RESTITUTION);
        isPossessed = false;
        isAlive = true;
        setName("host");
    }

    public HostModel(float x, float y, float width, float height, float currentCharge, float maxCharge, Vector2[] ins) {
        super(x, y, width, height);
        force = new Vector2();
        this.currentCharge = currentCharge;
        this.maxCharge = maxCharge;
        this.instructions = ins;
        this.instructionNumber = 0;
        this.hasBeenPossessed = false;
        setDensity(DEFAULT_DENSITY);
        setDensity(DEFAULT_DENSITY);
        setFriction(DEFAULT_FRICTION);
        setRestitution(DEFAULT_RESTITUTION);
        isPossessed = false;
        isAlive = true;
        setName("host");
    }

    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     *
     * This method overrides the base method to keep your host from spinning.
     *
     * @param world Box2D world to store body
     * @return true if object allocation succeeded
     */
    public boolean activatePhysics(World world) {
        // Get the box body from our parent class
        if (!super.activatePhysics(world)) {
            return false;
        }
        body.setFixedRotation(true);
        return true;
    }

    /**
     * Gets whether the host is in the state of possession or not.
     *
     * @return boolean representing whether state of possession or not
     */
    public boolean isPossessed() {
        return isPossessed;
    }

    /**
     * Sets whether the host is in the state of possession or not.
     *
     * @param possessed representing new state of possession
     */
    public void setPossessed(boolean possessed) {
        isPossessed = possessed;
    }


    /**
     * Sets the current charge of the host.
     *
     * @param currentCharge representing the current charge of host.
     */
    public void setCurrentCharge(float currentCharge){
        currentCharge = currentCharge;
    }

    /**
     * Gets the current charge of the host.
     *
     * @return the current charge of the host as a float.
     */
    public float getCurrentCharge(){
        return this.currentCharge;
    }

    /**
     * Increments the current charge of the host.
     *
     * @return whether the bot has blown up or not
     */
    public boolean incCurrentCharge(){
        if (this.currentCharge == this.maxCharge){
            return false;
        }
        else {
            this.currentCharge++;
            this.isAlive = false;
            return true;
        }
    }

    /**
     * Gets whether the host is alive or not.
     *
     * @return boolean representing host is alive or not
     */
    public boolean isAlive() {
        return isAlive;
    }

    public Vector2 getInstruction(){
        if(instructions == null){
            return getPosition();
        }
        return instructions[instructionNumber];
    }

    public boolean beenPossessed(){
        return hasBeenPossessed;
    }

    public void setBeenPossessed(boolean b){
        hasBeenPossessed = b;
    }

    public void nextInstruction() {
        if(instructions == null){
            return;
        }
        if(forwardI && instructionNumber + 1 >= instructions.length){
            forwardI = false;
            instructionNumber--;
        }
        else if(!forwardI && instructionNumber - 1 < 0){
            forwardI = true;
            instructionNumber++;
        }
        else if(forwardI){
            instructionNumber++;
        }else{
            instructionNumber--;
        }
    }

    /**
     * Sets whether the host is alive or not.
     *
     * @param alive representing whether host is alive or not
     */
    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    /**
     * Applies the force to the body of this host
     *
     * This method should be called after the force attribute is set.
     */
    public void applyForce() {
        if (!isActive()) {
            return;
        }

        // Orient the force with rotation.
        affineCache.setToRotationRad(getAngle());
        affineCache.applyTo(force);
        body.applyForce(force, body.getLocalCenter(), true);
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        //Draw the host
        float chargeProgression = currentCharge / maxCharge;
        if (this.texture != null && this.hostGaugeTexture != null) {

            /** Implementation of the HostModel with Charging Bar that Changes in Size */
            //            if(this.currentCharge < this.maxCharge) {
            //                canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1,1);
            //                canvas.draw(hostGaugeTexture, Color.GOLD, origin.x, origin.y , getX() * drawScale.x, (getY() * drawScale.y) - ((hostGaugeTexture.getRegionHeight()/2 * (1-chargeProgression))), getAngle(), 1, chargeProgression);
            //            }
            //            else {
            //                canvas.draw(texture,Color.RED,origin.x,origin.y,getX()*drawScale.x,getY() * drawScale.y ,getAngle(),1,1);
            //            }

            /** Implementation of the HostModel with Charging Bar that Changes Colors and Blinks */
            if (this.currentCharge < this.maxCharge) {
                canvas.draw(texture, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.x, getAngle(), 1, 1);
                // Color changes more and more to a red or goal color here
                Color warningColor = new Color(1, 1 - chargeProgression, 0, 1);
                if (chargeProgression >= 0.86f && chargeProgression <= 0.89f || chargeProgression >= 0.91f && chargeProgression <= 0.93f || chargeProgression >= 0.95f && chargeProgression <= 0.97f) {
                    // Color of the 3 flashes
                    warningColor = Color.BLACK;
                }
                canvas.draw(hostGaugeTexture, warningColor, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1, 1);

            } else {
                canvas.draw(texture, Color.RED, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.x, getAngle(), 1, 1);
            }


        }
    }
}


// Animation methods in the case we decide to change the animation method

/**
 * Returns the animation node for the given charge gauge of the host
 *
 * @param gauge enumeration to identify the charge gauge of the host
 * @return the animation node for the given charge gauge of the host
 */
//    public FilmStrip getChargeGaugeStrip(ChargeGauge gauge) {
//        switch (gauge) {
//            case POSSESSED:
//                return chargeGauge;
//            case NORMAL:
//                return normalGauge;
//        }
//        assert false : "Invalid gauge enumeration";
//        return null;
//    }

/**
 * Sets the animation node for the given charge gauge
 *
 * @param gauge enumeration to identify the specific gauge
 * @param strip the animation node for the given gauge
 */
//    public void setChargeGauge(ChargeGauge gauge, FilmStrip strip) {
//        switch (gauge) {
//            case POSSESSED:
//                chargeGauge = strip;
//                break;
//            case NORMAL:
//                normalGauge = strip;
//                /* If the gauge is a separate asset from the host itself */
//                //                if (strip != null) {
//                //                    leftOrigin.set(strip.getRegionWidth() / 2.0f, strip.getRegionHeight() / 2.0f);
//                //                }
//                break;
//            default:
//                assert false : "Invalid gauge enumeration";
//        }
//    }

/**
 * Returns the key for the sound to accompany the given charge gauge
 *
 * The key should either refer to a valid sound loaded in the AssetManager or
 * be empty ("").  If the key is "", then no sound will play.
 *
 * @param gauge enumeration to identify the state of the charge gauge
 * @return the key for the sound to accompany the given charge gauge
 */
//    public String getGaugeSound(ChargeGauge gauge) {
//        switch (gauge) {
//            case POSSESSED:
//                return possessedHostSound;
//            case NORMAL:
//                return normalHostSound;
//        }
//        assert false : "Invalid gauge enumeration";
//        return null;
//    }

/**
 * Sets the key for the sound to accompany the given charge gauge
 *
 * The key should either refer to a valid sound loaded in the AssetManager or
 * be empty ("").  If the key is "", then no sound will play.
 *
 * @param gauge enumeration to identify the state of the charge gauge
 * @param key   the key for the sound to accompany the main charge gauge
 */
//    public void setGaugeSound(ChargeGauge gauge, String key) {
//        switch (gauge) {
//            case POSSESSED:
//                possessedHostSound = key;
//                break;
//            case NORMAL:
//                normalHostSound = key;
//                break;
//            default:
//                assert false : "Invalid gauge enumeration";
//        }
//    }

/**
 * Animates the given gauge.
 *
 * If the animation is not active, it will reset to the initial animation frame.
 *
 * @param gauge The reference to the host's gauge
 * @param on    Whether the animation is active
 */
//    public void animateGauge(ChargeGauge gauge, boolean on) {
//        FilmStrip node = null;
//        boolean cycle = true;
//
//        switch (gauge) {
//            case POSSESSED:
//                node = chargeGauge;
//                cycle = possessedCycle;
//                break;
//            case NORMAL:
//                node = normalGauge;
//                cycle = normCycle;
//                break;
//            default:
//                assert false : "Invalid gauge enumeration";
//        }
//
//        if (on) {
//            // Turn on the gauge charging
//            if (node.getFrame() == 0 || node.getFrame() == 1) {
//                cycle = true;
//            } else if (node.getFrame() == node.getSize() - 1) {
//                cycle = false;
//            }
//
//            // Increment
//            if (cycle) {
//                node.setFrame(node.getFrame() + 1);
//            } else {
//                node.setFrame(node.getFrame() - 1);
//            }
//        } else {
//            node.setFrame(0);
//        }
//
//        switch (gauge) {
//            case POSSESSED:
//                possessedCycle = cycle;
//                break;
//            case NORMAL:
//                normCycle = cycle;
//                break;
//            default:
//                assert false : "Invalid gauge enumeration";
//        }
//    }
