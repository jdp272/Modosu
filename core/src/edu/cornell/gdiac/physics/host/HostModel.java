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

    /**
     * Enumeration to identify the animation for the Host
     */
    public enum HostState {
        /**
         * Host that has been possessed
         */
        CHARGED,
        /**
         * Host that has yet to be possessed
         */
        NOTCHARGED,
    }

    // Sound Related

    /** The associated sound when possessed */
    String possessSound;

    // Animation Related Variables

    /** The texture filmstrip for host that has yet to be possessed */
    FilmStrip notChargedHost;
    /** The animation phase for host that has yet to be possessed */
    boolean notChargedHostCycle = true;

    /** The texture filmstrip for host that has been possessed */
    FilmStrip chargedHost;
    /** The animation phase for host that has been possessed */
    boolean chargedHostCycle = true;


    /** Cache object for North Direction Origin */
    public Vector2 northOrigin = new Vector2();
    /** Cache object for NorthEast Direction Origin */
    public Vector2 northEastOrigin = new Vector2();
    /** Cache object for East Direction Origin */
    public Vector2 eastOrigin = new Vector2();
    /** Cache object for SouthEast Direction Origin */
    public Vector2 southEastOrigin = new Vector2();
    /** Cache object for South Direction Origin */
    public Vector2 southOrigin = new Vector2();
    /** Cache object for SouthWest Direction Origin */
    public Vector2 southWestOrigin = new Vector2();
    /** Cache object for West Direction Origin */
    public Vector2 westOrigin = new Vector2();
    /** Cache object for NorthWest Direction Origin */
    public Vector2 northWestOrigin = new Vector2();



    // Default physics values
    /** The density of this host */
    private static final float DEFAULT_DENSITY = 1.0f;
    /**
     * The friction of this host
     */
    private static final float DEFAULT_FRICTION = 0.1f;
    /**
     * The restitution of this host
     */
    private static final float DEFAULT_RESTITUTION = 0.4f;
    /**
     * The thrust factor to convert player input into host movement
     */
    private static final float DEFAULT_THRUST = 7.0f;


    /** The frame number for a host that just begins facing South */
    public static final int HOST_SOUTH_START = 0;
    /** The frame number for a host that just ends facing South */
    public static final int HOST_SOUTH_END = 15;
    /** The frame number for a host that just begins facing SouthEast */
    public static final int HOST_SOUTHEAST_START = 16;
    /** The frame number for a host that just ends facing SouthEast */
    public static final int HOST_SOUTHEAST_END = 31;
    /** The frame number for a host that just begins facing East */
    public static final int HOST_EAST_START = 32;
    /** The frame number for a host that just ends facing East */
    public static final int HOST_EAST_END = 47;
    /** The frame number for a host that just begins facing NorthEast */
    public static final int HOST_NORTHEAST_START = 48;
    /** The frame number for a host that just ends facing NorthEast */
    public static final int HOST_NORTHEAST_END = 63;
    /** The frame number for a host that just begins facing North */
    public static final int HOST_NORTH_START = 64;
    /** The frame number for a host that just ends facing North */
    public static final int HOST_NORTH_END = 79;
    /** The frame number for a host that just begins facing NorthWest */
    public static final int HOST_NORTHWEST_START = 80;
    /** The frame number for a host that just ends facing NorthWest */
    public static final int HOST_NORTHWEST_END = 95;
    /** The frame number for a host that just begins facing West */
    public static final int HOST_WEST_START = 96;
    /** The frame number for a host that just ends facing West */
    public static final int HOST_WEST_END = 111;
    /** The frame number for a host that just begins facing SouthWest */
    public static final int HOST_SOUTHWEST_START = 112;
    /** The frame number for a host that just ends facing SouthWest */
    public static final int HOST_SOUTHWEST_END = 127;

    /**
     * The texture for the host's gauge
     */
    protected TextureRegion hostGaugeTexture;

    // Attributes Specific to each HostModel
    /**
     * Boolean Whether HostModel is Possessed
     */
    private boolean isPossessed;
    /**
     * Boolean for whether host is alive
     */
    private boolean isAlive;
    /**
     * The current charge of the host
     */
    private float currentCharge;
    /**
     * The maximum charge of the host
     */
    private float maxCharge;
    /**
     * The force to apply to this host
     */
    private Vector2 force;
    /**
     * Instructions for robot when unpossessed
     */
    private Vector2[] instructions;
    /**
     * Current instruction index
     */
    private int instructionNumber;
    /**
     * Whether or not the robot has been possessed yet
     */
    private boolean hasBeenPossessed;
    /**
     * Whether robot is moving forward through instructions
     */
    private boolean forwardI;


    /**
     * Cache object for transforming the force according the object angle
     */
    public Affine2 affineCache = new Affine2();

    /**
     *
     */
    public void invertForwardI(){
        forwardI = !forwardI;
    }

    /**
     * Gets the max charge a host can hold before exploding
     *
     * @return float that represents the maximum charge that can be held
     */
    public float getMaxCharge() {
        return maxCharge;
    }

    /**
     * Sets the max charge a host can hold before exploding
     *
     * @param maxCharge that represents the new max charge
     */
    public void setMaxCharge(float maxCharge) {
        this.maxCharge = maxCharge;
    }

    /**
     * Returns the force applied to this host.
     * <p>
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
     * <p>
     * This method returns the texture for the gauge
     *
     * @return the texture
     */
    public TextureRegion getHostGaugeTexture() {
        return this.hostGaugeTexture;
    }

    /**
     * Sets the Hosts' Gauge Texture
     * <p>
     * This method sets the texture of the gauge
     */
    public void setHostGaugeTexture(TextureRegion hostGaugeTexture) {
        this.hostGaugeTexture = hostGaugeTexture;
    }

    /**
     * Returns the x-component of the force applied to this host.
     * <p>
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
     * <p>
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
     * <p>
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
     * <p>
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
     * <p>
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param width  The object width in physics units
     * @param height The object width in physics units
     */
    public HostModel(float width, float height, float currentCharge, float maxCharge) {
        this(0, 0, width, height, currentCharge, maxCharge);
    }

    /**
     * Creates a new host at the given position.
     * <p>
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
        this(x, y, width, height, currentCharge, maxCharge, null);
    }

    /**
     * Creates a new host at the given position.
     * <p>
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x      Initial x position of the box center
     * @param y      Initial y position of the box center
     * @param width  The object width in physics units
     * @param height The object width in physics units
     */
    public HostModel(float x, float y, float width, float height, float currentCharge, float maxCharge, Vector2[] ins) {
        super(x, y, width, height);
        force = new Vector2();
        this.currentCharge = currentCharge;
        this.maxCharge = maxCharge;
        this.instructions = ins;
        this.instructionNumber = 0;
        this.hasBeenPossessed = false;
        setDensity(DEFAULT_DENSITY);
        setFriction(DEFAULT_FRICTION);
        setRestitution(DEFAULT_RESTITUTION);
        isPossessed = false;
        isAlive = true;
        setName("host");
    }

    /**
     * Creates the physics Body(s) for this object, adding them to the world.
     * <p>
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
        hasBeenPossessed = hasBeenPossessed || possessed;
        isPossessed = possessed;
    }


    /**
     * Sets the current charge of the host.
     *
     * @param currentCharge representing the current charge of host.
     */
    public void setCurrentCharge(float currentCharge) {
        this.currentCharge = currentCharge;
    }

    /**
     * Gets the current charge of the host.
     *
     * @return the current charge of the host as a float.
     */
    public float getCurrentCharge() {
        return this.currentCharge;
    }

    /**
     * Increments the current charge of the host.
     *
     * @return whether the host has blown up or not
     */
    public boolean incCurrentCharge() {
        if (this.isPossessed) {
            if (this.currentCharge == this.maxCharge) {
                return false;
            } else {
                this.currentCharge++;
                this.isAlive = false;
                return true;
            }
        } else {
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

    public Vector2 getInstruction() {
        if (instructions == null) {
            return getPosition();
        }
        return instructions[instructionNumber];
    }

    public boolean beenPossessed(){
        return hasBeenPossessed;
    }

//    public void setBeenPossessed(boolean b){
//        hasBeenPossessed = b;
//    }

    /**
     * Gets the full list of instructions
     *
     * @return An array of Vector2 objects containing the instructions, or null
     *         if there are none.
     */
    public Vector2[] getInstructionList(){
        return instructions;
    }

    public void nextInstruction() {
        if (instructions == null) {
            return;
        }
        if (forwardI && instructionNumber + 1 >= instructions.length) {
            forwardI = false;
            instructionNumber--;
        } else if (!forwardI && instructionNumber - 1 < 0) {
            forwardI = true;
            instructionNumber++;
        } else if (forwardI) {
            instructionNumber++;
        } else {
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
     * <p>
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
     * Returns the animation node for the given state
     *
     * @param  state enumeration to identify the state of the host
     *
     * @return the animation node for the given state of the host
     */
    public FilmStrip getHostStateSprite (HostState state) {
        switch (state) {
            case CHARGED:
                return chargedHost;
            case NOTCHARGED:
                return notChargedHost;
        }
        assert false : "Invalid state enumeration";
        return null;
    }

    /**
     * Sets the animation node for the given state of the host
     *
     * @param  state enumeration to identify the state of the host
     *
     * @param  strip the animation node for the given state of the host
     */
    public void setHostStateSprite (HostState state, FilmStrip strip, Vector2 direction) {
        switch(state) {
            case CHARGED:
                chargedHost = strip;
                if(direction.x > 0) {
                    // NORTH EAST
                    if(direction.y > 0) {
                        chargedHost.setFrame(HOST_NORTHEAST_START);
                    }
                    // SOUTH EAST
                    else if(direction.y < 0) {
                        chargedHost.setFrame(HOST_SOUTHEAST_START);
                    }
                    // EAST
                    else if(direction.y == 0) {
                        chargedHost.setFrame(HOST_EAST_START);
                    }
                }
                else if(direction.x < 0) {
                    // NORTH WEST
                    if(direction.y > 0) {
                        chargedHost.setFrame(HOST_NORTHWEST_START);
                    }
                    // SOUTH WEST
                    else if(direction.y < 0) {
                       chargedHost.setFrame(HOST_SOUTHWEST_START);
                    }
                    // WEST
                    else if(direction.y == 0) {
                        chargedHost.setFrame(HOST_WEST_START);
                    }
                }
                else {
                    // NORTH
                    if(direction.y > 0) {
                        chargedHost.setFrame(HOST_NORTH_START);
                    }
                    // SOUTH
                    else if(direction.y > 0) {
                        chargedHost.setFrame(HOST_SOUTH_START);
                    }
                }
                break;
            case NOTCHARGED:
                notChargedHost = strip;
                if(direction.x > 0) {
                    // NORTH EAST
                    if(direction.y > 0) {
                        chargedHost.setFrame(HOST_NORTHEAST_START);
                    }
                    // SOUTH EAST
                    else if(direction.y < 0) {
                        chargedHost.setFrame(HOST_SOUTHEAST_START);
                    }
                    // EAST
                    else if(direction.y == 0) {
                        chargedHost.setFrame(HOST_EAST_START);
                    }
                }
                else if(direction.x < 0) {
                    // NORTH WEST
                    if(direction.y > 0) {
                        chargedHost.setFrame(HOST_NORTHWEST_START);
                    }
                    // SOUTH WEST
                    else if(direction.y < 0) {
                        chargedHost.setFrame(HOST_SOUTHWEST_START);
                    }
                    // WEST
                    else if(direction.y == 0) {
                        chargedHost.setFrame(HOST_WEST_START);
                    }
                }
                else {
                    // NORTH
                    if(direction.y > 0) {
                        chargedHost.setFrame(HOST_NORTH_START);
                    }
                    // SOUTH
                    else if(direction.y > 0) {
                        chargedHost.setFrame(HOST_SOUTH_START);
                    }
                }
                break;
        }
    }

    /**
     * Animates Host Movement
     *
     * Changes the animation based on the last pressed button.
     * This function should be called in host controller
     *
     * @param direction     the direction the host is travelling
     * @param state         the state of the host
     */
    private void updateAnimation(HostState state, Vector2 direction) {
        FilmStrip node = null;
        int frame = 0;

        switch (state) {
            case CHARGED:
                node = chargedHost;
                break;
            case NOTCHARGED:
                node = notChargedHost;
                break;
            default:
                assert false : "Invalid state enumeration";
        }

        if (node != null) {
            frame = node.getFrame();
        }

        if (direction.x > 0) {
            // NORTH EAST
            if (direction.y > 0) {
                if (frame < HOST_NORTHEAST_END && frame >= HOST_NORTHEAST_START) {
                    frame++;
                } else {
                    node.setFrame(HOST_NORTHEAST_START);
                }
            }
            // SOUTH EAST
            else if (direction.y < 0) {
                if (frame < HOST_SOUTHEAST_END && frame >= HOST_SOUTHEAST_START) {
                    frame++;
                } else {
                    node.setFrame(HOST_SOUTHEAST_START);
                }
            }
            // EAST
            if (direction.y == 0) {
                if (frame < HOST_EAST_END && frame >= HOST_EAST_START) {
                    frame++;
                } else {
                    node.setFrame(HOST_EAST_START);
                }
            }
        } else if (direction.x < 0) {
            // NORTH WEST
            if (direction.y > 0) {
                if (frame < HOST_NORTHWEST_END && frame >= HOST_NORTHWEST_START) {
                    frame++;
                } else {
                    node.setFrame(HOST_NORTHWEST_START);
                }
            }
            // SOUTH WEST
            else if (direction.y < 0) {
                if (frame < HOST_SOUTHWEST_END && frame >= HOST_SOUTHWEST_START) {
                    frame++;
                } else {
                    node.setFrame(HOST_SOUTHWEST_START);
                }
            }
            // WEST
            if (direction.y == 0) {
                if (frame < HOST_WEST_END && frame >= HOST_WEST_START) {
                    frame++;
                } else {
                    node.setFrame(HOST_WEST_START);
                }
            }
        } else {
            // NORTH
            if (direction.y < 0) {
                if (frame < HOST_NORTH_END && frame >= HOST_NORTH_START) {
                    frame++;
                } else {
                    node.setFrame(HOST_NORTH_START);
                }
            }
            // SOUTH
            else if (direction.y < 0) {
                if (frame < HOST_SOUTH_END && frame >= HOST_SOUTH_START) {
                    frame++;
                } else {
                    node.setFrame(HOST_SOUTH_START);
                }
            }
        }
        if(node != null) {
            node.setFrame(frame);
        }

        switch(state) {
            case CHARGED:
                node = chargedHost;
                break;
            case NOTCHARGED:
                node = notChargedHost;
                break;
            default:
                assert false: "Invalid host state enumeration";
        }
    }

    /**
     * Returns the key for the sound to accompany the given direction
     *
     * The key should either refer to a valid sound loaded in the AssetManager or
     * be empty ("").  If the key is "", then no sound will play.
     *
     * @param direction enumeration to identify the WalkingDirection
     *
     * @return the key for the sound to accompany the WalkingDirection
     */
//    public String getWalkingSound(WalkingDirection direction) {
//        switch (direction) {
//        }
//        assert false : "Invalid WalkingDirection enumeration";
//        return null;
//    }

    /**
     * Sets the key for the sound to accompany the given WalkingDirection
     *
     * The key should either refer to a valid sound loaded in the AssetManager or
     * be empty ("").  If the key is "", then no sound will play.
     *
     * @param  direction   enumeration to identify the WalkingDirection
     * @param  key      the key for the sound to accompany the main WalkingDirection
     */
//    public void setWalkingSound(WalkingDirection direction, String key) {
//        switch (direction) {
//            default:
//                assert false : "Invalid WalkingDirection enumeration";
//        }
//    }




    /**
     * Draws the host object.
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
                // Animation?
//                if (isPossessed) {
//                    canvas.draw(hostStrip, Color.WHITE, golemWalkOrigin.x, golemWalkOrigin.y, getX()*drawScale.x, getY()*drawScale.y, getAngle(),1,1);
//                }

                canvas.draw(texture, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.x, getAngle(), 1, 1);

                // Color changes more and more to a red or goal color here
                Color warningColor = new Color(chargeProgression * 2, 1 - chargeProgression, 1 - chargeProgression, 1);
                if (chargeProgression >= 0.86f && chargeProgression <= 0.89f || chargeProgression >= 0.91f && chargeProgression <= 0.93f || chargeProgression >= 0.95f && chargeProgression <= 0.97f) {
                    // Color of the 3 flashes
                    warningColor = Color.BLACK;
                }
                canvas.draw(hostGaugeTexture, warningColor, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1, 1);

            }
            else {
                canvas.draw(texture, Color.RED, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.x, getAngle(), 1, 1);
            }


        }
    }
}

