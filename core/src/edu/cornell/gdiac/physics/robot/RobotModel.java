/*
 * RobotModel.java
 *
 * Model class for Robots.
 *
 * Author: Grant Lee
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 3/1/2020
 */
package edu.cornell.gdiac.physics.robot;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.util.FilmStrip;

/**
 * Avatar representing the possessible golems that roam the map
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class RobotModel extends BoxObstacle {
    /**
     * Enumeration representing the states of the charge gauge
     * Gauge should be charging when possessed
     * Gauge should not be charging when not possessed but display the amount of charge
     */
    public enum ChargeGauge {
        /** State of being possessed and charging */
        POSSESSED,
        /** State of being not possessed and displaying gauge */
        NORMAL
    }

    // Default physics values
    /** The density of this robot */
    private static final float DEFAULT_DENSITY = 1.0f;
    /** The friction of this robot */
    private static final float DEFAULT_FRICTION = 0.1f;
    /** The restitution of this robot */
    private static final float DEFAULT_RESTITUTION = 0.4f;
    /** The thrust factor to convert player input into robot movement */
    private static final float DEFAULT_THRUST = 7.0f;
    /** The number of frames for the gauge */
    public static final int GAUGE_FRAMES = 4;

    /** The force to apply to this robot */
    private Vector2 force;

    /** The texture filmstrip for charge gauge when possessed */
    FilmStrip chargeGauge;
    /** The associated sound for the bot when possessed */
    String possessedBotSound;
    /** The animation phase for the bot when possessed */
    boolean possessedCycle = true;

    /** The texture filmstrip for charge gauge when not possessed */
    FilmStrip normalGauge;
    /** The associated sound for charge gauge when not possessed */
    String normalBotSound;
    /** The animation phase for charge gauge when not possessed */
    boolean normCycle = true;

    /** The texture filmstrip for robot's movements */
    FilmStrip robotMovement;
    /** The associated sound for robot's movements */
    String robotSounds;
    /** The animation phase for robot's movements */
    boolean botMvtCycle = true;


    /** Cache object for transforming the force according the object angle */
    public Affine2 affineCache = new Affine2();

    /**
     * Returns the force applied to this robot.
     *
     * This method returns a reference to the force vector, allowing it to be modified.
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the force applied to this robot.
     */
    public Vector2 getForce() {
        return force;
    }

    /**
     * Returns the x-component of the force applied to this robot.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the x-component of the force applied to this robot.
     */
    public float getFX() {
        return force.x;
    }

    /**
     * Sets the x-component of the force applied to this robot.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @param value the x-component of the force applied to this robot.
     */
    public void setFX(float value) {
        force.x = value;
    }

    /**
     * Returns the y-component of the force applied to this robot.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @return the y-component of the force applied to this robot.
     */
    public float getFY() {
        return force.y;
    }

    /**
     * Sets the x-component of the force applied to this robot.
     *
     * Remember to modify the input values by the thrust amount before assigning
     * the value to force.
     *
     * @param value the x-component of the force applied to this robot.
     */
    public void setFY(float value) {
        force.y = value;
    }

    /**
     * Returns the amount of thrust that this robot has.
     *
     * Multiply this value times the horizontal and vertical values in the
     * input controller to get the force.
     *
     * @return the amount of thrust that this robot has.
     */
    //    public float getthrust() {
    //        return default_thrust;
    //    }

    /**
     * Creates a new robot at the origin.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param width  The object width in physics units
     * @param height The object width in physics units
     */
    public RobotModel(float width, float height) {
        this(0, 0, width, height);
    }

    /**
     * Creates a new robot at the given position.
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
    public RobotModel(float x, float y, float width, float height) {
        super(x, y, width, height);
        force = new Vector2();
        setDensity(DEFAULT_DENSITY);
        setDensity(DEFAULT_DENSITY);
        setFriction(DEFAULT_FRICTION);
        setRestitution(DEFAULT_RESTITUTION);
        setName("robot");
    }

    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     *
     * This method overrides the base method to keep your robot from spinning.
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
     * Applies the force to the body of this robot
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

    // Animation methods

    /**
     * Returns the animation node for the given charge gauge of the robot
     *
     * @param ChargeGauge enumeration to identify the charge gauge of the robot
     * @return the animation node for the given charge gauge of the robot
     */
    public FilmStrip getChargeGaugeStrip(ChargeGauge gauge) {
        switch (gauge) {
            case POSSESSED:
                return chargeGauge;
            case NORMAL:
                return normalGauge;
        }
        assert false : "Invalid gauge enumeration";
        return null;
    }

    /**
     * Sets the animation node for the given charge gauge
     *
     * @param chargeGauge enumeration to identify the specific gauge
     * @param strip       the animation node for the given gauge
     */
    public void setChargeGauge(ChargeGauge gauge, FilmStrip strip) {
        switch (gauge) {
            case POSSESSED:
                chargeGauge = strip;
                break;
            case NORMAL:
                normalGauge = strip;
                /* If the gauge is a separate asset from the robot itself */
                //                if (strip != null) {
                //                    leftOrigin.set(strip.getRegionWidth() / 2.0f, strip.getRegionHeight() / 2.0f);
                //                }
                break;
            default:
                assert false : "Invalid gauge enumeration";
        }
    }

    /**
     * Returns the key for the sound to accompany the given charge gauge
     *
     * The key should either refer to a valid sound loaded in the AssetManager or
     * be empty ("").  If the key is "", then no sound will play.
     *
     * @param chargeGauge enumeration to identify the state of the charge gauge
     * @return the key for the sound to accompany the given charge gauge
     */
    public String getGaugeSound(ChargeGauge gauge) {
        switch (gauge) {
            case POSSESSED:
                return possessedBotSound
            case NORMAL:
                return normalBotSound
        }
        assert false : "Invalid gauge enumeration";
        return null;
    }

    /**
     * Sets the key for the sound to accompany the given charge gauge
     *
     * The key should either refer to a valid sound loaded in the AssetManager or
     * be empty ("").  If the key is "", then no sound will play.
     *
     * @param gauge enumeration to identify the state of the charge gauge
     * @param key   the key for the sound to accompany the main charge gauge
     */
    public void setGaugeSound(ChargeGauge gauge, String key) {
        switch (gauge) {
            case POSSESSED:
                possessedBotSound = key;
                break;
            case NORMAL:
                normalBotSound = key;
                break;
            default:
                assert false : "Invalid gauge enumeration";
        }
    }

    /**
     * Animates the given gauge.
     *
     * If the animation is not active, it will reset to the initial animation frame.
     *
     * @param gauge The reference to the robot's gauge
     * @param on    Whether the animation is active
     */
    public void animateGauge(ChargeGauge gauge, boolean on) {
        FilmStrip node = null;
        boolean cycle = true;

        switch (gauge) {
            case POSSESSED:
                node = chargeGauge;
                cycle = possessedCycle;
                break;
            case NORMAL:
                node = normalGauge
                cycle = normCycle;
                break;
            default:
                assert false : "Invalid gauge enumeration";
        }

        if (on) {
            // Turn on the gauge charging
            if (node.getFrame() == 0 || node.getFrame() == 1) {
                cycle = true;
            } else if (node.getFrame() == node.getSize() - 1) {
                cycle = false;
            }

            // Increment
            if (cycle) {
                node.setFrame(node.getFrame() + 1);
            } else {
                node.setFrame(node.getFrame() - 1);
            }
        } else {
            node.setFrame(0);
        }

        switch (gauge) {
            case POSSESSED:
                possessedCycle = cycle;
                break;
            case NORMAL:
                normCycle = cycle;
                break;
            default:
                assert false : "Invalid gauge enumeration";
        }
    }

    /**
     * Draws the physics object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        super.draw(canvas);  // Ship
        // Gauge
        if (chargeGauge != null) {
            float offsety = chargeGauge.getRegionHeight() - origin.y;
            canvas.draw(chargeGauge, Color.WHITE, origin.x, offsety, getX() * drawScale.x, getY() * drawScale.x, getAngle(), 1, 1);
        }
        if (normalGauge != null) {

            /* Again the location of the animation will vary based on its relation to the actual bot */
            //            canvas.draw(normalGauge, Color.WHITE, leftOrigin.x, leftOrigin.y, getX() * drawScale.x, getY() * drawScale.x, getAngle(), 1, 1);
        }
    }
}