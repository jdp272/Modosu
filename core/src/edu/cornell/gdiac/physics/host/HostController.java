package edu.cornell.gdiac.physics.host;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.*;

import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.physics.*;

public class HostController {

    /** List of all the hosts */
    private HostList hosts;
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

    /** The draw scale of the game */
    private Vector2 scale;

    /** Whether the possessed host has blown up */
    private boolean possessedBlownUp;

    // Cache variables
    /** Arrow position cache */
    private Vector2 arrowCache;

    private InputController input;

    /** Height of the screen used to convert mouse y-coordinates */
    private float height;

    /** Constant to change the speed of golem movement */
    private static final float HOST_MOVEMENT_SPEED = 5.f;

    /** Minimum speed for shot spirit */
    private static final float MINIMUM_SHOT_SPEED = 10.f;

    /** Minimum speed for shot spirit */
    private static final float MAXIMUM_SHOT_SPEED = 20.f;

    /** Multiplier for velocity of spirit when shot */
    private static final float SHOOTING_MULTIPLIER = 5.f;

    /** Minimum distance to target before going to next instruction, for autonomous mode */
    private static final float NEXT_INSTRUCTION_DIST = 0.5f;

    /** The number of ticks since we started this controller */
    private long ticks;

    /**
     * Creates and initialize a new instance of a HostController
     */
    public HostController(HostList r, Vector2 scale, Texture arrowTexture, float heightY) {
        input = InputController.getInstance();
        clickPosition = new Vector2(-1,-1);
        hosts = r;
        arrowText = arrowTexture;
        height = heightY;
        possessedBlownUp = false;

        this.scale = scale;

        arrowCache = new Vector2();
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
    public void update(float dt, HostModel possessed, SpiritModel spirit) {

        input = InputController.getInstance();

        if (possessed != null) {
            if (!spirit.getHasLaunched()) {
                spirit.setPosition(possessed.getPosition());
            }

            if (possessed.incCurrentCharge()) {
                if (!spirit.getHasLaunched()) {
                    possessed.setVX(HOST_MOVEMENT_SPEED * input.getHorizontal());
                    possessed.setVY(HOST_MOVEMENT_SPEED * input.getVertical());

                    // Shooting the spirit
                    if (input.didTertiary() && clickPosition.x == -1 && clickPosition.y == -1) { // Clicked Mouse
                        spirit.setPosition(possessed.getPosition());

                        clickPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());

                        arrowCache.set(possessed.getPosition());
                        arrowCache.scl(scale);
                        arrow = new ArrowModel(arrowText, arrowCache);

//                  TODO (MAY) :
//                  NEED TO ADD CONTROL TO ONLY RECOGNIZE IT WHEN ON ROBOT BODY
//                  NEED TO DRAW ONLY WHEN MEETS VELOCITY THRESHOLD

                    }

                    // Released Mouse -- Shoot
                    else if (!input.didTertiary() && clickPosition.x != -1 && clickPosition.y != -1) {

                        arrow = null;

                        shootVector = new Vector2(Gdx.input.getX(), Gdx.input.getY());
                        shootVector = shootVector.sub(clickPosition);
                        shootVector.x = -shootVector.x;

                        clickPosition.x = -1;
                        clickPosition.y = -1;

                        float vx = SHOOTING_MULTIPLIER * shootVector.x / scale.x;
                        float vy = SHOOTING_MULTIPLIER * shootVector.y / scale.y;

                        float magnitude = Math.abs(vx*vx + vy*vy);

                        // Only shoot if the shooting speed is large enough
                        if (magnitude > MINIMUM_SHOT_SPEED) {
                            spirit.setHasLaunched(true);

                            spirit.setPosition(possessed.getPosition());

                            // Cap the speed of the shot
                            if (magnitude > MAXIMUM_SHOT_SPEED) {
                                float angle = (float)Math.atan2(vy, vx);
                                vx = MAXIMUM_SHOT_SPEED * (float)Math.cos(angle);
                                vy = MAXIMUM_SHOT_SPEED * (float)Math.sin(angle);
                            }

                            spirit.setVX(vx);
                            spirit.setVY(vy);
                        }

                    }
                    else if (input.didTertiary() && clickPosition.x != -1 && clickPosition.y != -1) {
                        // Arrow Direction?
                        currMouse = new Vector2(Gdx.input.getX(),Gdx.input.getY());
                        arrow.setCurrLoc(currMouse);
                        spirit.setPosition(possessed.getPosition());
                    }
                }
                else {
                    possessed.setVX(0);
                    possessed.setVY(0);
                }
            }
            else {
                possessedBlownUp = true;
                possessed.setVX(0);
                possessed.setVY(0);
            }
        }

//        System.out.println();

        //update other robots
        for(HostModel r: hosts){
            Vector2 n = r.getInstruction();
            Vector2 curr = r.getPosition();
            if(r != possessed && !r.beenPossessed()) {
                // If the destination was reached, move to the next instruction
                if(Math.abs(curr.x - n.x) < NEXT_INSTRUCTION_DIST && Math.abs(curr.y - n.y) < NEXT_INSTRUCTION_DIST) {
                    r.nextInstruction();
                    n = r.getInstruction();
                    // float l = (float)Math.sqrt(Math.pow(n.x-curr.x,2) + Math.pow(n.y-curr.y,2) );
                }

                r.setVX(Math.signum(n.x - curr.x) * HOST_MOVEMENT_SPEED);
                r.setVY(Math.signum(n.y - curr.y) * HOST_MOVEMENT_SPEED);

            }
        }

        // Update Animations

        // If we use sound, we must remember this.
        //SoundController.getInstance().update();
    }

    public ArrowModel getArrow() { return arrow; }

    public boolean getPossessedBlownUp() { return possessedBlownUp; }

}
