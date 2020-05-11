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
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.util.FilmStrip;

/**
 * Avatar representing the possessable golems that roam the map
 * <p>
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class HostModel extends BoxObstacle {


    // Animation Related Variables

    /**
     * General texture filmstrip for Host
     */
    FilmStrip hostStrip;

    /**
     * The texture filmstrip for host walking East
     */
    FilmStrip hostStripE;

    /**
     * The texture filmstrip for host walking North
     */
    FilmStrip hostStripN;

    /**
     * The texture filmstrip for host walking North East
     */
    FilmStrip hostStripNE;

    /**
     * The texture filmstrip for host walking North West
     */
    FilmStrip hostStripNW;

    /**
     * The texture filmstrip for host walking South
     */
    FilmStrip hostStripS;

    /**
     * The texture filmstrip for host walking South East
     */
    FilmStrip hostStripSE;

    /**
     * The texture filmstrip for host walking South West
     */
    FilmStrip hostStripSW;

    /**
     * The texture filmstrip for host walking West
     */
    FilmStrip hostStripW;

    /**
     * The texture filmstrip for host waking up
     */
    FilmStrip hostWakingUp;

    /**
     * The texture for charge UI
     */
    FilmStrip hostChargeUI;

    /**
     * The texture filmstrip for the host that is the pedestal
     */
    FilmStrip pedestalHost;

    // Default physics values
    /**
     * The density of this host
     */
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
    private static final int PEDESTAL_MAX_CHARGE = -1;
    /**
     * The current charge of a pedestal
     */
    private static final int PEDESTAL_CURRENT_CHARGE = -1;

    /**
     * The gauge color for unpossessed robots
     */
    private static final Color unpossessedColor = new Color(0x6B5C5CFF);

    // FRAMES FOR SPRITE SHEET

    /**
     * The frame number for a host starting frame
     */
    public static final int HOST_START = 0;
    /**
     * The frame number for a host ending frame
     */
    public static final int HOST_FINISH = 59;

    /**
     * The frame number for a host charge gauge starting frame
     */
    public static final int HOST_CHARGE_UI_START = 0;
    /**
     * The frame number for a host charge gauge ending frame
     */
    public static final int HOST_CHARGE_UI_END = 31;


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
    private int currentCharge;
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
     * Whether the host is in a pillar or not
     */
    private boolean inPillar;

    /**
     * drawing scales to resize the host (doesn't affect hit box)
     */
    private float sx = 0.4f;
    private float sy = 0.4f;

    /**
     * The number of frames that have elapsed since the last animation update
     */
    private int elapsedFrames = 0;
    private int pedFrames = 0;

    /**
     * The number of frames that should pass before the animation updates
     * (animation framerate is the framerate of the game divided by this value)
     * 4 seems to look pretty good
     */
    private int framesPerUpdate = 2;
    private int pedFramesPerUpdate = 12;

    /**
     * Whether or not the animation should be updated on this frame
     * (though if the update is changing direction it will always happen)
     */
    private boolean updateFrame;
    private boolean pedUpdateFrame;


    /**
     * Cache object for transforming the force according the object angle
     */
    public Affine2 affineCache = new Affine2();

    /**
     * Changes the direction the robot should be moved
     * used on contacts
     */
    public void invertForwardI() {
        forwardI = !forwardI;
    }

    /**
     * returns the instruction number the robot is on
     *
     * @return instructionNumber
     */
    public int getInstructionNumber() {
        return instructionNumber;
    }

    /**
     * returns if the robot is moving
     *
     * @return moving
     */
    public boolean isMoving() {
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
     *
     * @return true if current host is a pedestal
     */
    public boolean isPedestal() {
        return isPedestal;
    }

    /**
     * Sets current host to a pedestal if pedestal is true
     *
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
    public HostModel(float width, float height, int currentCharge, float maxCharge) {
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
    public HostModel(float x, float y, float width, float height, int currentCharge, float maxCharge) {
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
    public HostModel(float x, float y, float width, float height, int currentCharge, float maxCharge, Vector2[] ins) {
        super(x, y, width, height);
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
     * @param x          Initial x position of the box center
     * @param y          Initial y position of the box center
     * @param width      The object width in physics units
     * @param height     The object width in physics units
     * @param isPedestal Whether host is a pedestal or not
     */
    public HostModel(float x, float y, float width, float height, boolean isPedestal) {
        super(x, y, width, height);
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
    public void setCurrentCharge(int currentCharge) {
        this.currentCharge = currentCharge;
    }

    /**
     * Gets the current charge of the host.
     *
     * @return the current charge of the host as a float.
     */
    public int getCurrentCharge() {
        return this.currentCharge;
    }

    /**
     * Increments the current charge of the host.
     *
     * @return whether the host has blown up or not
     */
    public boolean incCurrentCharge() {
        if (this.isPossessed && !this.isPedestal) {
            if (currentCharge >= this.maxCharge) {
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

    public boolean beenPossessed() {
        return hasBeenPossessed;
    }

//    public void setBeenPossessed(boolean b){
//        hasBeenPossessed = b;
//    }

    /**
     * Gets the full list of instructions
     *
     * @return An array of Vector2 objects containing the instructions, or null
     * if there are none.
     */
    public Vector2[] getInstructionList() {
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

    public void setInstructions(Vector2[] instructions) {
        this.instructions = instructions;
    }

    public void setPedestalStrip(FilmStrip strip) {
        this.pedestalHost = strip;
        pedestalHost.setFrame(0);
    }

    public void setChargeStrip(FilmStrip chargeStrip, int currentCharge) {
        this.hostChargeUI = chargeStrip;
        this.hostChargeUI.setFrame((int) ((currentCharge / this.maxCharge) * HOST_CHARGE_UI_END));
    }

    public void setChargeStripFrame(int currentCharge) {
        if(this.hostChargeUI != null) {
            this.hostChargeUI.setFrame((int) ((currentCharge / this.maxCharge) * HOST_CHARGE_UI_END));
        }
    }

    /**
     * Sets all the textures by direction for the host
     *
     * @param walkE  the direction of the host in East
     * @param walkN  the direction of the host in North
     * @param walkNE the direction of the host in North East
     * @param walkNW the direction of the host in the North West
     * @param walkS  the direction of the host in the South
     * @param walkSE the direction of the host in the South East
     * @param walkSW the direction of the host in the South West
     * @param walkW  the direction of the host in the West
     */
    public void setHostStrip(FilmStrip walkE, FilmStrip walkN, FilmStrip walkNE, FilmStrip walkNW, FilmStrip walkS, FilmStrip walkSE, FilmStrip walkSW, FilmStrip walkW) {

        hostStrip = walkS;
        hostStrip.setFrame(0);

        hostStripNE = walkNE;
        hostStripNE.setFrame(HOST_START);
        hostStripSE = walkSE;
        hostStripSE.setFrame(HOST_START);
        hostStripE = walkE;
        hostStripE.setFrame(HOST_START);
        hostStripNW = walkNW;
        hostStripNW.setFrame(HOST_START);
        hostStripSW = walkSW;
        hostStripSW.setFrame(HOST_START);
        hostStripW = walkW;
        hostStripW.setFrame(HOST_START);
        hostStripN = walkN;
        hostStripN.setFrame(HOST_START);
        hostStripS = walkS;
        hostStripS.setFrame(HOST_START);
    }

    private float threshold = 0.5f;
    /**
     * Animates Host Movement
     * <p>
     * Changes the animation based on the last pressed button.
     * This function should be called in host controller
     *
     * @param direction the direction the host is travelling
     */
    public void updateAnimation(Vector2 direction) {
        int frame = 0;
        if(hostStrip != null) {
            frame = hostStrip.getFrame();
        }
        if (!this.isPedestal) {

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

                //Update UI for Charge
                this.hostChargeUI.setFrame((int) ((this.currentCharge / this.maxCharge) * HOST_CHARGE_UI_END));

                if (direction.x > threshold) {
                    // NORTH EAST
                    if (direction.y > threshold) {
                        hostStrip = hostStripNE;
                        if (frame < HOST_FINISH && frame >= HOST_START) {
                            frame++;
                        } else {
                            frame = HOST_START;
                        }

                    }
                    // SOUTH EAST
                    else if (direction.y < -threshold) {
                        hostStrip = hostStripSE;
                        if (frame < HOST_FINISH && frame >= HOST_START) {
                            frame++;
                        } else {
                            frame = HOST_START;
                        }
                    }
                    // EAST
                    if (Math.abs(direction.y) < threshold) {
                        hostStrip = hostStripE;
                        if (frame < HOST_FINISH && frame >= HOST_START) {
                            frame++;
                        } else {
                            frame = HOST_START;
                        }
                    }
                } else if (direction.x < -threshold) {
                    // NORTH WEST
                    if (direction.y > threshold) {
                        hostStrip = hostStripNW;
                        if (frame < HOST_FINISH && frame >= HOST_START) {
                            frame++;
                        } else {
                            frame = HOST_START;
                        }
                    }
                    // SOUTH WEST
                    else if (direction.y < -threshold) {
                        hostStrip = hostStripSW;
                        if (frame < HOST_FINISH && frame >= HOST_START) {
                            frame++;
                        } else {
                            frame = HOST_START;
                        }
                    }
                    // WEST
                    if (Math.abs(direction.y) < threshold) {
                        hostStrip = hostStripW;
                        if (frame < HOST_FINISH && frame >= HOST_START) {
                            frame++;
                        } else {
                            frame = HOST_START;
                        }
                    }
                } else if (Math.abs(direction.x) < threshold) {
                    // NORTH
                    if (direction.y > threshold) {
                        hostStrip = hostStripN;
                        if (frame < HOST_FINISH && frame >= HOST_START) {
                            frame++;
                        } else {
                            frame = HOST_START;
                        }
                    }
                    // SOUTH
                    else if (direction.y < -threshold) {
                        hostStrip = hostStripS;
                        if (frame < HOST_FINISH && frame >= HOST_START) {
                            frame++;
                        } else {
                            frame = HOST_START;
                        }
                    }
                }
                if(hostStrip != null) {
                    hostStrip.setFrame(frame);
                }
            }
        }
    }

    /**
     * Strip animation for pedestal
     */
    public void animateStrip() {
        pedFrames++;
        pedUpdateFrame = false;
        if (pedFrames >= pedFramesPerUpdate) {
            pedUpdateFrame = true;
            pedFrames = 0;
        }
        if (pedUpdateFrame) {
            if (this.pedestalHost.getFrame() < this.pedestalHost.getSize() - 1) {
                this.pedestalHost.setFrame(this.pedestalHost.getFrame() + 1);
            } else {
                this.pedestalHost.setFrame(0);
            }
        }
    }

    /** Set whether host is in the bounds of the energy pillar or not
     * @param inPillar will be true if host is inside bounds of the pillar's radius
     */
    public void setInPillar(boolean inPillar) {
        this.inPillar = inPillar;
    }

    /**
     * Draws the host object, but not the charge UI bar.
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        if (this.isPedestal) {
            // Make pedestal clear when no longer in possession.
            if (this.isPossessed) {
                setScaling(pedestalHost);
                canvas.draw(pedestalHost, Color.WHITE, pedestalHost.getRegionWidth() / 2, pedestalHost.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1, 1);
            } else {
                setScaling(pedestalHost);
                canvas.draw(pedestalHost, Color.CLEAR, pedestalHost.getRegionWidth() / 2, pedestalHost.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1, 1);
            }
        } else {
            // Draw the host
            float chargeProgression = currentCharge / maxCharge;
            if (this.hostStrip != null && this.hostChargeUI != null) {

                // If bot has already been possessed colors should change
                if (this.hasBeenPossessed) {
                    /** Implementation of the HostModel with Charging Bar that Changes Colors and Blinks */
                    if (this.currentCharge < this.maxCharge) {
                        setScaling(hostStrip);
                        canvas.draw(hostStrip, Color.WHITE, hostStrip.getRegionWidth() / 2, hostStrip.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, getAngle(), sx , sy);
                    } else {
                        setScaling(hostStrip);
                        canvas.draw(hostStrip, Color.RED, hostChargeUI.getRegionWidth() / 2, hostChargeUI.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, getAngle(), sx, sy);
                    }
                }
                // When the bot hasn't been possessed the indicator color should be black
                else {
                    setScaling(hostStrip);
                    canvas.draw(hostStrip, Color.WHITE, hostStrip.getRegionWidth() / 2, hostStrip.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, getAngle(), sx, sy);
                }
            }
        }
    }

    /**
     * Draws the host charge UI bar.
     *
     * @param canvas Drawing context
     */
    public void drawCharge(GameCanvas canvas) {
        if (!this.isPedestal) {
            // Draw the host
            float chargeProgression = currentCharge / maxCharge;
            if (this.hostStrip != null && this.hostChargeUI != null) {

                // If bot has already been possessed colors should change
                if (this.hasBeenPossessed) {
                    /** Implementation of the HostModel with Charging Bar that Changes Colors and Blinks */
                    if (this.currentCharge < this.maxCharge) {

                        // Color changes more and more to a red or goal color here
                        // Light Blue Color
                        Color warningColor = new Color(200f/256f, 241f/256f, 238f/256f, 1);

                        //System.out.println(this.inPillar);
                        if(this.inPillar) {
                            warningColor = new Color(255f/256f, 191f/256f, 124f/256f,1);
                        }
                        if ((chargeProgression >= 0.83f && chargeProgression <= 0.86f || chargeProgression >= 0.90f && chargeProgression <= 0.93f || chargeProgression >= 0.97f && chargeProgression <= 1f)) {
                            // Color of the 3 flashes
                            warningColor = new Color (chargeProgression * 255f  / 256f , chargeProgression * 155f / 256f, chargeProgression * 222f/256f, 1);
                        }
                        setScaling(hostChargeUI);
                        canvas.draw(hostChargeUI, warningColor, hostChargeUI.getRegionWidth() / 2, hostChargeUI.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 0.9f, 0.9f);
                    }
                }
                // When the bot hasn't been possessed the indicator color should be black
                else {
                    setScaling(hostChargeUI);
                    canvas.draw(hostChargeUI, Color.BLACK, hostChargeUI.getRegionWidth() / 2,hostChargeUI.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.x, getAngle(), 0.9f, 0.9f);
                }
            }
        }
    }

    public Vector2 getVelocity() {
        return new Vector2(this.getVX(), this.getVY());
    }
}

