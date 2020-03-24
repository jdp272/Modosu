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

    /** Whether the possessed host has blown up */
    private boolean possessedBlownUp;

    private InputController input;

    /** Height of the screen used to convert mouse y-coordinates */
    private float height;

    /** Constant to change the speed of host movement */
    private static final float HOST_MOVEMENT_SPEED = 500000;
    private static final float MINIMUM_SHOT_SPEED = 50;

    /** The number of ticks since we started this controller */
    private long ticks;

    /**
     * Creates and initialize a new instance of a HostController
     */
    public HostController(HostList r, Texture arrowTexture, float heightY) {
        input = InputController.getInstance();
        clickPosition = new Vector2(-1,-1);
        hosts = r;
        arrowText = arrowTexture;
        height = heightY;
        possessedBlownUp = false;
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
                        arrow = new ArrowModel(arrowText, possessed.getPosition());

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

                        spirit.setPosition(possessed.getPosition());

                        spirit.setVX(shootVector.x);
                        spirit.setVY(shootVector.y);

                        if (Math.abs(shootVector.x) > MINIMUM_SHOT_SPEED || Math.abs(shootVector.y) > MINIMUM_SHOT_SPEED) {
                            spirit.setHasLaunched(true);
                        }

                        // Upon Release of Spirit, Possessed bot is not longer possessed
                        possessed.setPossessed(false);

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

            // Case when Host's currentCharge exceed maxCharge
            if(possessed.getCurrentCharge() > possessed.getMaxCharge()) {
                possessedBlownUp = true;
            }
        }


        // PORTION OF CODE THAT DEALS WITH DECREMENTING LIFE OF SPIRIT

        // When the spirit has been launched, need to decrement life of spirit
        if(spirit.hasLaunched) {
            // If you can decrement life, decrement life
            if(spirit.decCurrentLife()) {
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
        if(!spirit.isAlive() && !possessedBlownUp) {
            spirit.setPosition(possessed.getPosition());
            // TODO: Replace 100 with variable whatever amount we want the host to go up by
            possessed.setCurrentCharge(possessed.getCurrentCharge() + 100);
        }



        //update other robots
        for (HostModel r: hosts) {
            Vector2 n = r.getInstruction();
            Vector2 curr = r.getPosition();
            if (r != possessed && Math.abs(curr.x - n.x) < 5 && Math.abs(curr.y - n.y) < 5 && !r.beenPossessed()) {
                //go to next instruction
                r.nextInstruction();
                n = r.getInstruction();
                float l = (float)Math.sqrt(Math.pow(n.x-curr.x,2) + Math.pow(n.y-curr.y,2) );

                r.setVX((n.x - curr.x) * HOST_MOVEMENT_SPEED);
                r.setVY((n.y - curr.y) * HOST_MOVEMENT_SPEED);

            }
            else if (r != possessed && !r.beenPossessed()) {
                r.setVX((n.x - curr.x) * HOST_MOVEMENT_SPEED);
                r.setVY((n.y - curr.y) * HOST_MOVEMENT_SPEED);
            }
        }

        // Update Animations

        // If we use sound, we must remember this.
        //SoundController.getInstance().update();
    }

    public ArrowModel getArrow() { return arrow; }

    public boolean getPossessedBlownUp() { return possessedBlownUp; }

}
