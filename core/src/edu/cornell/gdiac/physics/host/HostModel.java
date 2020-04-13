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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
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


    // Animation Related Variables

    /** The texture filmstrip for host that has yet to be possessed */
    FilmStrip notChargedHost;

    /** The texture filmstrip for host that has been possessed */
    FilmStrip chargedHost;

    /** The texture filmstrip for the host that is the pedestal */
    FilmStrip pedestalHost;

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
    /**
     * The max charge of a pedestal
     */
    private static final float PEDESTAL_MAX_CHARGE = -1f;
    /**
     *  The current charge of a pedestal
     */
    private static final float PEDESTAL_CURRENT_CHARGE = -1f;


    // FRAMES FOR SPRITE SHEET

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
    protected FilmStrip hostGaugeStrip;


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
     * Instructions for host when unpossessed
     */
    private Vector2[] instructions;
    /**
     * Current instruction index
     */
    private int instructionNumber;
    /**
     * Whether or not the host has been possessed yet
     */
    private boolean hasBeenPossessed;
    /**
     * Whether host is moving forward through instructions
     */
    private boolean forwardI;
    /**
     * Whether the host is supposed to move or not
     */
    private boolean moving;
    /**
     * Whether the host is a pedestal or not
     */
    private boolean isPedestal;

    /**
     * drawing scales to resize the host (doesn't affect hit box)
     */
    private float sx = 0.3f;
    private float sy = 0.3f;

    /**
     * The number of frames that have elapsed since the last animation update
     */
    private int elapsedFrames = 0;

    /**
     * The number of frames that should pass before the animation updates
     * (animation framerate is the framerate of the game divided by this value)
     * 4 seems to look pretty good
     */
    private int framesPerUpdate = 4;

    /**
     * Whether or not the animation should be updated on this frame
     * (though if the update is changing direction it will always happen)
     */
    private boolean updateFrame;


    /**
     * Cache object for transforming the force according the object angle
     */
    public Affine2 affineCache = new Affine2();

    /**
     * Changes the direction the robot should be moved
     * used on contacts
     */
    public void invertForwardI(){
        forwardI = !forwardI;
    }

    /**
     * returns the instruction number the robot is on
     * @return instructionNumber
     */
    public int getInstructionNumber(){
        return instructionNumber;
    }

    /**
     * returns if the robot is moving
     * @return moving
     */
    public boolean isMoving(){
        return moving;
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
     * Returns whether current host it a pedestal or not
     * @return true if current host is a pedestal
     */
    public boolean isPedestal() {
        return isPedestal;
    }

    /**
     * Sets current host to a pedestal if pedestal is true
     * @param pedestal is true if pedestal
     */
    public void setPedestal(boolean pedestal) {
        isPedestal = pedestal;
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
     * Sets the Hosts' Gauge Texture
     * <p>
     * This method sets the texture of the gauge
     */
    public void setHostGaugeTexture(FilmStrip hostGaugeStrip) {
        this.hostGaugeStrip = hostGaugeStrip;
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
        super(x, y, width*0.3f, height*0.5f);
        force = new Vector2();
        this.currentCharge = currentCharge;
        this.maxCharge = maxCharge;
        this.instructions = ins;
        this.instructionNumber = 0;
        this.hasBeenPossessed = false;
        this.updateFrame = true;
        this.moving = (ins != null);
        setDensity(DEFAULT_DENSITY);
        setFriction(DEFAULT_FRICTION);
        setRestitution(DEFAULT_RESTITUTION);
        isPossessed = false;
        isAlive = true;
        setName("host");
    }

    /**
     * Creates a new pedestal at the given position.
     * <p>
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x      Initial x position of the box center
     * @param y      Initial y position of the box center
     * @param width  The object width in physics units
     * @param height The object width in physics units
     * @param isPedestal Whether host is a pedestal or not
     */
    public HostModel(float x, float y, float width, float height, boolean isPedestal) {
        super(x, y, width*0.3f, height*0.5f);
        force = new Vector2();
        this.currentCharge = PEDESTAL_CURRENT_CHARGE;
        this.maxCharge = PEDESTAL_MAX_CHARGE;
        this.instructions = null;
        this.instructionNumber = 0;
        this.hasBeenPossessed = true;
        this.updateFrame = true;
        this.isPedestal = isPedestal;
        setDensity(DEFAULT_DENSITY);
        setFriction(DEFAULT_FRICTION);
        setRestitution(DEFAULT_RESTITUTION);
        isPossessed = true;
        isAlive = false;
        setName("pedestal");
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
        if (this.isPossessed && !this.isPedestal) {
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

    public void setPedestalStrip(FilmStrip strip) {
        this.pedestalHost = strip;
        pedestalHost.setFrame(4);
    }

    /**
     * sets the FilmStrip for the charged host and the corresponding gauge
     * @param strip for the charged host
     */
    public void setChargedHostStrip (FilmStrip strip) {
        chargedHost = strip;
        chargedHost.setFrame(HOST_SOUTH_START);
        hostGaugeStrip.setFrame(HOST_SOUTH_START);
        this.setTexture(strip);
    }

    /**
     * sets the FilmStrip for the uncharged host and the corresponding gauge
     * @param strip for the charged host
     */
    public void setNotChargedHostStrip (FilmStrip strip) {
        notChargedHost = strip;
        notChargedHost.setFrame(HOST_SOUTH_START);
        hostGaugeStrip.setFrame(HOST_SOUTH_START);
        this.setTexture(strip);
    }

    /**
     * Sets the animation node for the given state of the host
     *
     * @param  beenPossesed enumeration to identify the state of the host
     *
     * @param  strip the animation node for the given state of the host
     */
    public void setHostStateSprite (boolean beenPossesed, FilmStrip strip, Vector2 direction) {
        if (beenPossesed) {
            chargedHost = strip;
            if (direction.x > 0) {
                // NORTH EAST
                if (direction.y > 0) {
                    chargedHost.setFrame(HOST_NORTHEAST_START);
                    hostGaugeStrip.setFrame(HOST_NORTHEAST_START);
                }
                // SOUTH EAST
                else if (direction.y < 0) {
                    chargedHost.setFrame(HOST_SOUTHEAST_START);
                    hostGaugeStrip.setFrame(HOST_SOUTHEAST_START);
                }
                // EAST
                else if (direction.y == 0) {
                    chargedHost.setFrame(HOST_EAST_START);
                    hostGaugeStrip.setFrame(HOST_EAST_START);
                }
            } else if (direction.x < 0) {
                // NORTH WEST
                if (direction.y > 0) {
                    chargedHost.setFrame(HOST_NORTHWEST_START);
                    hostGaugeStrip.setFrame(HOST_NORTHWEST_START);
                }
                // SOUTH WEST
                else if (direction.y < 0) {
                    chargedHost.setFrame(HOST_SOUTHWEST_START);
                    hostGaugeStrip.setFrame(HOST_SOUTHWEST_START);
                }
                // WEST
                else if (direction.y == 0) {
                    chargedHost.setFrame(HOST_WEST_START);
                    hostGaugeStrip.setFrame(HOST_WEST_START);
                }
            } else {
                // NORTH
                if (direction.y > 0) {
                    chargedHost.setFrame(HOST_NORTH_START);
                    hostGaugeStrip.setFrame(HOST_NORTH_START);
                }
                // SOUTH
                else if (direction.y > 0) {
                    chargedHost.setFrame(HOST_SOUTH_START);
                    hostGaugeStrip.setFrame(HOST_SOUTH_START);
                }
            }
        } else {
            notChargedHost = strip;
            if (direction.x > 0) {
                // NORTH EAST
                if (direction.y > 0) {
                    chargedHost.setFrame(HOST_NORTHEAST_START);
                    hostGaugeStrip.setFrame(HOST_NORTHEAST_START);
                }
                // SOUTH EAST
                else if (direction.y < 0) {
                    chargedHost.setFrame(HOST_SOUTHEAST_START);
                    hostGaugeStrip.setFrame(HOST_SOUTHEAST_START);
                }
                // EAST
                else if (direction.y == 0) {
                    chargedHost.setFrame(HOST_EAST_START);
                    hostGaugeStrip.setFrame(HOST_EAST_START);
                }
            } else if (direction.x < 0) {
                // NORTH WEST
                if (direction.y > 0) {
                    chargedHost.setFrame(HOST_NORTHWEST_START);
                    hostGaugeStrip.setFrame(HOST_NORTHWEST_START);
                }
                // SOUTH WEST
                else if (direction.y < 0) {
                    chargedHost.setFrame(HOST_SOUTHWEST_START);
                    hostGaugeStrip.setFrame(HOST_SOUTHWEST_START);
                }
                // WEST
                else if (direction.y == 0) {
                    chargedHost.setFrame(HOST_WEST_START);
                    hostGaugeStrip.setFrame(HOST_WEST_START);
                }
            } else {
                // NORTH
                if (direction.y > 0) {
                    chargedHost.setFrame(HOST_NORTH_START);
                    hostGaugeStrip.setFrame(HOST_NORTH_START);
                }
                // SOUTH
                else if (direction.y > 0) {
                    chargedHost.setFrame(HOST_SOUTH_START);
                    hostGaugeStrip.setFrame(HOST_SOUTH_START);
                }
            }
        }
    }

    /**
     * Animates Host Movement
     *
     * Changes the animation based on the last pressed button.
     * This function should be called in host controller
     *
     * @param state         the state of the host
     * @param direction     the direction the host is travelling
     */
    public void updateAnimation(boolean state, Vector2 direction) {
        FilmStrip node = null;
        int frame = 0;

        if (state) {
            node = chargedHost;
        } else {
            node = notChargedHost;
        }

        if (node != null) {
            frame = node.getFrame();
        }

        // To allow framerate control of this animation
        elapsedFrames++;
        updateFrame = false;
        if (elapsedFrames >= framesPerUpdate) {
            updateFrame = true;
            elapsedFrames = 0;
        }

        // I'm a little concerned about slowing all animation within the host using one thing, because
        // if the framerate is sufficiently low it might feel unresponsive because the golem does not immediately
        // turn in the direction you are moving. For now, because framrate is relatively high, disregard this.
        if (updateFrame) {
        if (direction.x > 0) {
            // NORTH EAST
            if (direction.y > 0 && Math.abs(direction.y) > 0.1) {
                if (frame < HOST_NORTHEAST_END && frame >= HOST_NORTHEAST_START) {
                    frame++;
                } else {
                    frame = HOST_NORTHEAST_START;
                }
            }
            // SOUTH EAST
            else if (direction.y < 0 && Math.abs(direction.y) > 0.1) {
                if (frame < HOST_SOUTHEAST_END && frame >= HOST_SOUTHEAST_START) {
                    frame++;
                } else {
                    frame = HOST_SOUTHEAST_START;
                }
            }
            // EAST
            if (direction.y == 0 || Math.abs(direction.y) < 0.1) {
                if (frame < HOST_EAST_END && frame >= HOST_EAST_START) {
                    frame++;
                } else {
                    frame = HOST_EAST_START;
                }
            }
        } else if (direction.x < 0) {
            // NORTH WEST
            if (direction.y > 0 && Math.abs(direction.y) > 0.1) {
                if (frame < HOST_NORTHWEST_END && frame >= HOST_NORTHWEST_START) {
                    frame++;
                } else {
                    frame = HOST_NORTHWEST_START;
                }
            }
            // SOUTH WEST
            else if (direction.y < 0 && Math.abs(direction.y) > 0.1) {
                if (frame < HOST_SOUTHWEST_END && frame >= HOST_SOUTHWEST_START) {
                    frame++;
                } else {
                    frame = HOST_SOUTHWEST_START;
                }
            }
            // WEST
            if (direction.y == 0 || Math.abs(direction.y) < 0.1) {
                if (frame < HOST_WEST_END && frame >= HOST_WEST_START) {
                    frame++;
                } else {
                    frame = HOST_WEST_START;
                }
            }
        } else if (direction.x == 0 || Math.abs(direction.x) < 0.1) {
            // NORTH
            if (direction.y > 0 && Math.abs(direction.y) > 0.1) {
                if (frame < HOST_NORTH_END && frame >= HOST_NORTH_START) {
                    frame++;
                } else {
                    frame = HOST_NORTH_START;
                }
            }
            // SOUTH
            else if (direction.y < 0 && Math.abs(direction.y) > 0.1) {
                if (frame < HOST_SOUTH_END && frame >= HOST_SOUTH_START) {
                    frame++;
                } else {
                    frame = HOST_SOUTH_START;
                }
            }
        }
    }

        if(node != null) {
            node.setFrame(frame);
            hostGaugeStrip.setFrame(frame);
        }

        if(state) {
            node = chargedHost;
        }
        else {
            node = notChargedHost;
        }
    }

    /**
     * Strip animation for pedestal
     */
    public void animateStrip() {
        if(this.pedestalHost.getFrame() < this.pedestalHost.getSize() - 1) {
            this.pedestalHost.setFrame(this.pedestalHost.getFrame() + 1);
        }
        else {
            this.pedestalHost.setFrame(0);
        }
    }

    /**
     * Draws the host object.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {

        if(this.isPedestal) {
            // Make pedestal clear when no longer in possession.
            if(this.isPossessed) {
                canvas.draw(pedestalHost, Color.WHITE, pedestalHost.getRegionWidth() / 2, pedestalHost.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1, 1);
            } else {
                canvas.draw(pedestalHost, Color.CLEAR, pedestalHost.getRegionWidth() / 2, pedestalHost.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1, 1);
            }
        }


        else {

        //Draw the host
        float chargeProgression = currentCharge / maxCharge;
        if (this.chargedHost != null && this.hostGaugeStrip != null) {

            // If bot has already been possessed colors should change
            if (this.hasBeenPossessed) {
                /** Implementation of the HostModel with Charging Bar that Changes Colors and Blinks */
                if (this.currentCharge < this.maxCharge) {
                    canvas.draw(chargedHost, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), sx, sy);

                    // Color changes more and more to a red or goal color here
                    Color warningColor = new Color(chargeProgression * 5, 4 - (4.5f * chargeProgression), 4 - (9 * chargeProgression), 1);
                    if (chargeProgression >= 0.86f && chargeProgression <= 0.89f || chargeProgression >= 0.91f && chargeProgression <= 0.93f || chargeProgression >= 0.95f && chargeProgression <= 0.97f) {
                        // Color of the 3 flashes
                        warningColor = Color.BLACK;
                    }
                    canvas.draw(hostGaugeStrip, warningColor, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), sx, sy);

                } else {
                    canvas.draw(chargedHost, Color.RED, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), sx, sy);
                }
            }
            // When the bot hasn't been possessed the indicator color should be black
            else {
                canvas.draw(chargedHost, Color.WHITE, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.y, getAngle(), sx, sy);
                canvas.draw(hostGaugeStrip, Color.BLACK, origin.x, origin.y, getX() * drawScale.x, getY() * drawScale.x, getAngle(), sx, sy);
            }
        }
        }
    }

    public Vector2 getVelocity() {
        return new Vector2(this.getVX(), this.getVY());
    }
}

