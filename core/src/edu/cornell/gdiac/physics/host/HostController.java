package edu.cornell.gdiac.physics.host;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.HUD;
import edu.cornell.gdiac.physics.InputController;
import edu.cornell.gdiac.physics.obstacle.EnergyPillar;
import edu.cornell.gdiac.physics.spirit.SpiritModel;

import java.util.ArrayList;

public class HostController {

    /**
     * List of all the hosts
     */
    private ArrayList<HostModel> hosts;
    /**
     * The texture for the arrow created by the shot
     */
    private Texture arrowText;
    /**
     * The arrow created by the shot
     */
    private ArrowModel arrow;

    private HostModel pedestal;

    /**
     * The draw scale of the game
     */
    private Vector2 scale;

    /**
     * Whether the possessed host has blown up
     */
    private boolean possessedBlownUp;

    /**
     * Whether a successful launch occurred this frame
     */
    private boolean launched;
    /**
     * Whether player moved host this frame
     */
    private boolean moved;

    /**
     * Spirit position in screen coordinates
     */
    private Vector2 spiritCenter;

    /**
     * Get user input through the controller
     */
    private InputController input;

    // Cache variables

    private Vector2 mousePosCache;

    private Vector2 velocityCache;

    private Vector2 spiritCache;

    private Vector2 lastPosition;

    /**
     * Constant to change the speed of golem movement
     */
    private static final float HOST_MOVEMENT_SPEED = 5f;

    /**
     * Minimum speed for shot spirit
     */
    private static final float MINIMUM_SHOT_SPEED = 7f;

    /**
     * Maximum speed for shot spirit
     */
    private static final float MAXIMUM_SHOT_SPEED = 21f;

    /**
     * Multiplier for velocity of spirit when shot
     */
    private static final float SHOOTING_MULTIPLIER = 2.1f;

    /**
     * Minimum distance to target before going to next instruction, for autonomous mode
     */
    private static final float NEXT_INSTRUCTION_DIST = 0.5f;

    /**
     * Energy pillars in the game
     */
    private EnergyPillar[] energyPillars;

    /**
     * The number of ticks since we started this controller
     */
    private long ticks;

    /**
     * The number of hosts on this level
     */
    private int numHosts;

    private GameCanvas canvas;

    /**
     * Creates and initialize a new instance of a HostController
     */
    public HostController(ArrayList<HostModel> h, Vector2 scale, Texture arrowTexture, HostModel pedestal, GameCanvas c, EnergyPillar[] energyPillars) {
        input = InputController.getInstance();
        hosts = h;
        arrowText = arrowTexture;
        possessedBlownUp = false;
        launched = false;
        this.scale = scale;
        spiritCenter = new Vector2(c.getWidth() / 2, c.getHeight() / 2);
        mousePosCache = new Vector2();
        velocityCache = new Vector2();
        spiritCache = new Vector2();
        this.pedestal = pedestal;
        numHosts = h.size();
        moved = false;
        canvas = c;
        this.energyPillars = energyPillars;
        this.lastPosition = new Vector2(pedestal.getPosition());
    }

    /**
     * Resets the status of the host.
     * <p>
     * This method disposes of the host and creates a new one.
     */
    public void reset() {
        // should host controller have a list of hosts?
        // because initally all of these are gonna be AI
        // populateLevel();
    }


    /**
     * The core gameplay loop of this world.
     * <p>
     * This method contains the specific update code for this mini-game. It does
     * not handle collisions, as those are managed by the parent class WorldController.
     * This method is called after input is read, but before collisions are resolved.
     * The very last thing that it should do is apply forces to the appropriate objects.
     *
     * @param dt     Number of seconds since last animation frame
     * @param inSand
     */
    public void update(float dt, HostModel possessed, SpiritModel spirit, HostModel pedestal, boolean inSand, EnergyPillar[] energyPillars, boolean wasPaused) {
        ticks++;
        // Removes the arrow that was clicked when paused
        if (wasPaused) arrow = null;

        // Brings the spirit to the center of the host
        if (spirit.getGoToCenter() && !spirit.getIsPossessing()) {
            Vector2 dirToCenter = possessed.getPosition().sub(spirit.getPosition()).setLength(150f);
            spirit.setVX(dirToCenter.x);
            spirit.setVY(dirToCenter.y);

            float posX = possessed.getPosition().x;
            float posY = possessed.getPosition().y;
            float spiritX = spirit.getPosition().x;
            float spiritY = spirit.getPosition().y;

            // Close enough to be considered on the center
            if (((posX - 1) <= spiritX && spiritX < (posX + 1)) &&
                    ((posY - 1) <= spiritY && (spiritY < (posY + 1)))) {
                // Set spirit on center,
                spirit.setPosition(possessed.getPosition());
                // Stop going towards center
                spirit.setGoToCenter(false);
                // Register real possession
                spirit.setIsPossessing(true);
            }
        }

        //tentative fix for the start of the game where the spirit is going to center
        else if (spirit.getGoToCenter() && spirit.getIsPossessing()) {
            spirit.setGoToCenter(false);
            spirit.setIsPossessing(true);
        }

        // Possessing a host, either currently or a new one
        if (possessed != null) {

            // If just possessed and not yet dynamic, make it dynamic
            if (possessed.getBodyType() != BodyDef.BodyType.DynamicBody) {
                possessed.setBodyType(BodyDef.BodyType.DynamicBody);
            }

            moved = false;

            // When possessed, spirit should not move
            if (spirit.getIsPossessing()) {
                spirit.setVX(0f);
                spirit.setVY(0f);
            }


            // If its possible to increment the charge of the host
            if (possessed.incCurrentCharge()) {

                // If the spirit isn't outside of the host
                if (!spirit.hasLaunched || spirit.getIsPossessing()) {

                    if (!possessed.isPedestal()) {
                        float chargeProgression = (float) possessed.getCurrentCharge() / possessed.getMaxCharge();

                        for (EnergyPillar ep : energyPillars) {
                            ep.setChargeProgression(chargeProgression);
                        }

//                        if(ticks % 30 == 0) {
//                            this.lastPosition = new Vector2(possessed.getPosition());
//                        }

                        float obstacleFactor = 1;
                        if (inSand) {
                            obstacleFactor = .5f;
                        }
                        possessed.setVX(HOST_MOVEMENT_SPEED * input.getHorizontal() * obstacleFactor);
                        possessed.setVY(HOST_MOVEMENT_SPEED * input.getVertical() * obstacleFactor);
                        if(input.getVertical() != 0 || input.getHorizontal() != 0) {
                            this.moved = true;
                        }else{
                            this.moved = false;
                        }


//                        if(this.moved) {
//                            possessed.updateAnimation(possessed.getLinearVelocity());
//                        }

                    }



                    if ((input.getVertical() != 0 || input.getHorizontal() != 0) && (!spirit.getGoToCenter())) {
                        spirit.setPosition(possessed.getPosition());
                    }

                    Vector2 mousePos = mousePosCache.set(input.getMousePosition());
                    Vector2 shootVector = spiritCache.set(spiritCenter).sub(mousePos);

                    float vx = SHOOTING_MULTIPLIER * shootVector.x / scale.x;
                    float vy = SHOOTING_MULTIPLIER * shootVector.y / scale.y;

                    float magnitude = (float) Math.sqrt(Math.abs(vx * vx + vy * vy));

                    // Spirit Controller that deals with shooting the spirit

                    // Player input is pressed, so makes a new arrow
                    if (input.didLeftClick()) {
                        arrow = new ArrowModel(arrowText, spiritCache.set(spirit.getPosition().scl(scale.x, scale.y)));
                    }

                    // Arrow has been created, and mouse is held down so update the arrow
                    else if (input.didIsPressed() && arrow != null) {
                        // Set where the spirit currently is for starting draw location
                        arrow.setCurrLoc(velocityCache.set(spirit.getPosition().scl(scale.x, scale.y)));
                        // Set the velocity represented by the arrow
                        arrow.setVelocityRepresented(shootVector, magnitude > MINIMUM_SHOT_SPEED);
                    }
                    // Mouse has been released so shoot and get rid of arrow
                    else if (input.didRelease() && arrow != null) {
                        arrow = null;

                        // Only shoot if the shooting speed is large enough
                        if (magnitude > MINIMUM_SHOT_SPEED) {

                            // Re-center the spirit for maximum accuracy and in case spirit is off center
                            spirit.setPosition(possessed.getPosition());

                            // Cap the speed of the shot
                            if (magnitude > MAXIMUM_SHOT_SPEED) {
                                float angle = (float) Math.atan2(vy, vx);
                                vx = MAXIMUM_SHOT_SPEED * (float) Math.cos(angle);
                                vy = MAXIMUM_SHOT_SPEED * (float) Math.sin(angle);
                            }

                            // Set the spirit's velocity
                            spirit.setVX(vx);
                            spirit.setVY(vy);

                            // Upon Release of Spirit, possessed host and spirit are no longer possessed/possessing
                            spirit.setHasLaunched(true);
                            spirit.setIsPossessing(false);
                            spirit.setGoToCenter(false);

                            possessed.setPossessed(false);

                            launched = true;
                        }
                    }
                    // No arrow exists, no player mouse input either
                    else {
                    }
                } else {
                    possessed.setVX(0);
                    possessed.setVY(0);
                }
            }

            // Host is at max charge
            else {
                possessedBlownUp = true && possessed.animateDeath();
                // Remove the arrow if dead
                arrow = null;
                possessed.setVX(0);
                possessed.setVY(0);
            }

            // Case when Host's currentCharge exceed maxCharge
            if (possessed.getCurrentCharge() > possessed.getMaxCharge()) {
                possessedBlownUp = true && possessed.animateDeath();
            }

        }


        // Update the Animation of the possessed host

        spirit.updateAnimation();
        pedestal.animatePedestal();
        possessed.updateAnimation(possessed.getLinearVelocity());

        // PORTION OF CODE THAT DEALS WITH DECREMENTING LIFE OF SPIRIT
        // When the spirit has been launched, need to decrement life of spirit
        if (spirit.hasLaunched) {
            possessed.setFizzle(false);
            possessed.setHasPlayedPossession(false);
            // If you can decrement life, decrement life
            if (spirit.decCurrentLife()) {
                // Spirit isn't dead yet
                spirit.setAlive(true);
            } else {
                // Because you can't decrement anymore, spirit is dead
                spirit.setAlive(false);
            }
        }

        // PORTION OF CODE THAT DEALS WITH JUMPING BACK TO LAST HOST AFTER DEATH

        // In the case that spirit dies return to previous possessed bot
        if ((!spirit.isAlive() && !possessedBlownUp) || (possessed == pedestal && !spirit.isAlive())) {
            spirit.setPosition(possessed.getPosition());
            // TODO: Replace 100 with variable whatever amount we want the host to go up by
            if(possessed != pedestal) {
            possessed.setCurrentCharge((int)Math.min(possessed.getCurrentCharge() + 100, possessed.getMaxCharge()));
            possessed.setFizzle(true);
            }

            if(possessed == pedestal) {
                possessed.setPossessed(true);
                spirit.setCurrentLife(spirit.getDefaultLife());
                spirit.setHasLaunched(false);
                spirit.setAlive(true);
                spirit.setGoToCenter(true);
            }
        }

        //update other robots
        for (HostModel h : hosts) {

            // Update the body type of each host
            if ((h != possessed && !h.isMoving() && h.getBodyType() != BodyDef.BodyType.StaticBody) ||
                    (h.isMoving() && h.beenPossessed() && h != possessed)) {
                h.setBodyType(BodyDef.BodyType.StaticBody);
            }

            // Updated Animation of Each Host
            h.updateAnimation(h.getLinearVelocity());


            if (h != possessed && !h.beenPossessed()) {
                Vector2 target = h.getInstruction();
                Vector2 current = h.getPosition();

                float x = target.x - current.x;
                float y = target.y - current.y;

                // If close enough to the destination, move to the next
                // instruction (note: squaring both sides instead of sqrt)
                if (x * x + y * y < NEXT_INSTRUCTION_DIST * NEXT_INSTRUCTION_DIST) {
                    h.nextInstruction();

                    // Otherwise, move towards the target
                } else {
                    double angle = Math.atan2(y, x);

                    h.setVX(HOST_MOVEMENT_SPEED * (float) Math.cos(angle));
                    h.setVY(HOST_MOVEMENT_SPEED * (float) Math.sin(angle));
                }
            }
        }
    }

    public ArrowModel getArrow() {
        return arrow;
    }

    public boolean getPossessedBlownUp() {
        return possessedBlownUp;
    }

    public boolean checkAllPossessed() {
        int counter = 0;
        for (HostModel h : hosts) {
            if (h.beenPossessed()) {
                counter++;
            }
        }
        return counter == numHosts;
    }

    public boolean getLaunched() {
        if (launched) {
            launched = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean isMoving() {
        return moved;
    }
}
