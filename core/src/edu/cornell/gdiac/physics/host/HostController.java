package edu.cornell.gdiac.physics.host;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.*;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.physics.*;

import java.util.ArrayList;

public class HostController {

    /** List of all the hosts */
    private ArrayList<HostModel> hosts;
    /** The initial click position of the cursor */
    private Vector2 clickPosition;
    /** The vector created by the shot */
    private Vector2 shootVector;
    /** The texture for the arrow created by the shot */
    private Texture arrowText;
    /** The arrow created by the shot */
    private ArrowModel arrow;
    /** The current position of the mouse */
    private Vector2 currMouse;

    private HostModel pedestal;

    /** The draw scale of the game */
    private Vector2 scale;

    /** Whether the possessed host has blown up */
    private boolean possessedBlownUp;

    /** Whether a successful launch occurred this frame */
    private boolean launched;
    /** Whether player moved host this frame */
    private boolean moved;


    private InputController input;

    // Cache variables
    /** Arrow position cache */
    private Vector2 arrowCache;

    /** Constant to change the speed of golem movement */
    private static final float HOST_MOVEMENT_SPEED = 5.f;

    /** Minimum speed for shot spirit */
    private static final float MINIMUM_SHOT_SPEED = 10.f;

    /** Maximum speed for shot spirit */
    private static final float MAXIMUM_SHOT_SPEED = 20.f;

    /** Multiplier for velocity of spirit when shot */
    private static final float SHOOTING_MULTIPLIER = 5.f;

    /** Minimum distance to target before going to next instruction, for autonomous mode */
    private static final float NEXT_INSTRUCTION_DIST = 0.5f;

    /** The number of ticks since we started this controller */
    private long ticks;

    private int numHosts;
    /**
     * Creates and initialize a new instance of a HostController
     */
    public HostController(ArrayList<HostModel> h, Vector2 scale, Texture arrowTexture, HostModel pedestal) {
        input = InputController.getInstance();
        clickPosition = new Vector2(-1,-1);
        hosts = h;
        arrowText = arrowTexture;
        shootVector = new Vector2(0,0);
        possessedBlownUp = false;
        launched = false;
        this.scale = scale;
        arrowCache = new Vector2();
        this.pedestal = pedestal;
        numHosts = h.size();
        moved = false;
    }

    /**
     * Resets the status of the host.
     *
     * This method disposes of the host and creates a new one.
     */
    public void reset() {
        // should host controller have a list of hosts?
        // because initally all of these are gonna be AI
        // populateLevel();
    }


    /**
     * The core gameplay loop of this world.
     *
     * This method contains the specific update code for this mini-game. It does
     * not handle collisions, as those are managed by the parent class WorldController.
     * This method is called after input is read, but before collisions are resolved.
     * The very last thing that it should do is apply forces to the appropriate objects.
     *
     * @param dt Number of seconds since last animation frame
     */
    public void update(float dt, HostModel possessed, SpiritModel spirit, HostModel pedestal) {


        input = InputController.getInstance();


        // Brings the spirit to the center of the host
        if (spirit.getGoToCenter() && !spirit.getIsPossessing()) {
            Vector2 dirToCenter = possessed.getPosition().sub(spirit.getPosition()).setLength(200f);
            spirit.setVX(dirToCenter.x);
            spirit.setVY(dirToCenter.y);

            float posX = possessed.getPosition().x;
            float posY = possessed.getPosition().y;
            float spiritX = spirit.getPosition().x;
            float spiritY = spirit.getPosition().y;

            // Close enough to be considered on the center
            if ( ((posX-1) <= spiritX  && spiritX < (posX+1) ) &&
                    ((posY-1) <= spiritY  && (spiritY < (posY+1))) ) {
                // Set spirit on center,
                spirit.setPosition(possessed.getPosition());
                // Stop going towards center
                spirit.setGoToCenter(false);
                // Register real possession
                spirit.setIsPossessing(true);
            }
        }

        //tentative fix for the start of the game where the spirit is going to center
        else if (spirit.getGoToCenter() && spirit.getIsPossessing()){
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

            // Zoom the camera out on z, or back in if it is out
            if (input.didZoom()) {

            }

            if (possessed.incCurrentCharge()) {

                if (!spirit.hasLaunched || spirit.getIsPossessing()) {

                    // Move using player input
//                    if (!possessed.isPedestal() && (input.getVertical() != 0 || input.getHorizontal() != 0)){
                    if (!possessed.isPedestal()) {
                        possessed.setVX(HOST_MOVEMENT_SPEED * input.getHorizontal());
                        possessed.setVY(HOST_MOVEMENT_SPEED * input.getVertical());

                        if (input.getVertical() != 0 || input.getHorizontal() != 0) {
                            moved = true;
                        }
                    }

                    if ((input.getVertical() != 0 || input.getHorizontal() != 0) && (!spirit.getGoToCenter())) {
                        spirit.setPosition(possessed.getPosition());
                    }

                    // Shooting the spirit
                    if (input.didTertiary() && clickPosition.x == -1 && clickPosition.y == -1) {
                        // Clicked Mouse
                        clickPosition = clickPosition.set(Gdx.input.getX(), Gdx.input.getY());
                        arrowCache.set(possessed.getPosition());
                        arrowCache.scl(scale);
                        arrow = new ArrowModel(arrowText, arrowCache);
                    }

                    // Released Mouse -- Shoot
                    else if (!input.didTertiary() && clickPosition.x != -1 && clickPosition.y != -1) {
                        arrow = null;

                        // Calculate the new velocity vector
                        shootVector = shootVector.set(Gdx.input.getX(), Gdx.input.getY());
                        shootVector = shootVector.sub(clickPosition);
                        shootVector.x = -shootVector.x;

                        clickPosition.x = -1;
                        clickPosition.y = -1;

                        float vx = SHOOTING_MULTIPLIER * shootVector.x / scale.x;
                        float vy = SHOOTING_MULTIPLIER * shootVector.y / scale.y;

                        float magnitude = Math.abs(vx * vx + vy * vy);

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
                    else if (input.didTertiary() && clickPosition.x != -1 && clickPosition.y != -1) {
                        // Save current mouse location in arrowModel
                        // Save possessed current position as the starting drawing point

                        currMouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
                        arrowCache.set(possessed.getPosition());
                        arrowCache.scl(scale);
                        arrow.setCurrLoc(currMouse, arrowCache);

                        //would be velocity
                        shootVector = shootVector.set(Gdx.input.getX(), Gdx.input.getY());
                        shootVector = shootVector.sub(clickPosition);
                        shootVector.x = -shootVector.x;
                        arrow.setVelocityRepresented(shootVector);
                    }
                }
                else {
                    possessed.setVX(0);
                    possessed.setVY(0);
                }
            }

                // Host is at max charge
                else {
                    possessedBlownUp = true;
                    possessed.setVX(0);
                    possessed.setVY(0);
                }

            // Case when Host's currentCharge exceed maxCharge
            if (possessed.getCurrentCharge() > possessed.getMaxCharge()) {
                possessedBlownUp = true;
            }
        }

        // Update the Animation of the possessed host

        possessed.updateAnimation(possessed.beenPossessed(), possessed.getLinearVelocity());
        pedestal.animateStrip();

        // PORTION OF CODE THAT DEALS WITH DECREMENTING LIFE OF SPIRIT

        // When the spirit has been launched, need to decrement life of spirit
        if (spirit.hasLaunched) {
            // If you can decrement life, decrement life
            if (spirit.decCurrentLife()) {
                // Spirit isn't dead yet
                spirit.setAlive(true);
            }
            else {
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
                possessed.setCurrentCharge(possessed.getCurrentCharge() + 100);
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
            h.updateAnimation(true, h.getLinearVelocity());

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
                }
                else {
                    double angle = Math.atan2(y, x);

                    h.setVX(HOST_MOVEMENT_SPEED * (float) Math.cos(angle));
                    h.setVY(HOST_MOVEMENT_SPEED * (float) Math.sin(angle));
                }
            }
        }
    }

    public ArrowModel getArrow() { return arrow; }

    public boolean getPossessedBlownUp() { return possessedBlownUp; }

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
        }
        else {
            return false;
        }
    }

    public boolean isMoving() { return moved; }
}
