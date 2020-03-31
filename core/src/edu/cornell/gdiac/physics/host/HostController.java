package edu.cornell.gdiac.physics.host;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.*;

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

    /** Whether the possessed host has blown up */
    private boolean possessedBlownUp;

    private boolean launched;

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
    public HostController(ArrayList<HostModel> h, Texture arrowTexture, float heightY) {
        input = InputController.getInstance();
        clickPosition = new Vector2(-1,-1);
        hosts = h;
        arrowText = arrowTexture;
        height = heightY;
        possessedBlownUp = false;
        launched = false;
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

        if (spirit.getGoToCenter() && !spirit.getIsPossessing()) {
            Vector2 dirToCenter = possessed.getPosition().sub(spirit.getPosition()).setLength(200f);
            spirit.setVX(dirToCenter.x);
            spirit.setVY(dirToCenter.y);

            float posX = possessed.getPosition().x;
            float posY = possessed.getPosition().y;
            float spiritX = spirit.getPosition().x;
            float spiritY = spirit.getPosition().y;

            // Close enough to be considered on the center
            if ( ((posX-1) <= spiritX  && spiritX < (posX+1) ) && ((posY-1) <= spiritY  && (spiritY < (posY+1))) ) {
                // Set spirit on center,
                spirit.setPosition(possessed.getPosition());
                // Stop going towards center
                spirit.setGoToCenter(false);
                // Register real possession
                spirit.setIsPossessing(true);
            }
        }

        // Possessing a host, either currently or a new one
        if (possessed != null) {
            if (spirit.getIsPossessing()) {
                spirit.setVX(0f);
                spirit.setVY(0f);
                spirit.setPosition(possessed.getPosition());
            }

            if (possessed.incCurrentCharge()) {

                if (!spirit.hasLaunched || spirit.getIsPossessing()) {
//                    possessed.animateWalk(true);
                    // Move using player input
                    possessed.setVX(HOST_MOVEMENT_SPEED * input.getHorizontal());
                    possessed.setVY(HOST_MOVEMENT_SPEED * input.getVertical());

                    if ((input.getVertical() != 0 || input.getHorizontal() != 0) && (!spirit.getGoToCenter())) {
                        spirit.setVX(HOST_MOVEMENT_SPEED * input.getHorizontal());
                        spirit.setVY(HOST_MOVEMENT_SPEED * input.getVertical());
                    }

                    // Shooting the spirit
                    if (input.didTertiary() && clickPosition.x == -1 && clickPosition.y == -1) {
                        // Clicked Mouse
                        clickPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
                        arrow = new ArrowModel(arrowText, possessed.getPosition());

//                  TODO (MAY) :
//                  NEED TO ADD CONTROL TO ONLY RECOGNIZE IT WHEN ON ROBOT BODY
//                  NEED TO DRAW ONLY WHEN MEETS VELOCITY THRESHOLD

                    }

                    // Released Mouse -- Shoot
                    else if (!input.didTertiary() && clickPosition.x != -1 && clickPosition.y != -1) {
                        arrow = null;

                        // Calculate the new velocity vector
                        shootVector = new Vector2(Gdx.input.getX(), Gdx.input.getY());
                        shootVector = shootVector.sub(clickPosition);
                        shootVector.x = -shootVector.x;

                        clickPosition.x = -1;
                        clickPosition.y = -1;

                        // Shoot velocity meets threshold requirements so shoot the spirit
                        if (Math.abs(shootVector.x) > MINIMUM_SHOT_SPEED || Math.abs(shootVector.y) > MINIMUM_SHOT_SPEED) {
                            spirit.setHasLaunched(true);
                            spirit.setVX(shootVector.x);
                            spirit.setVY(shootVector.y);

                            // Upon Release of Spirit, possessed host and spirit are no longer possessed/possessing
                            spirit.setIsPossessing(false);
                            possessed.setPossessed(false);
                            launched = true;
                            spirit.setHasLaunched(true);
                            spirit.setGoToCenter(false);
                        }


                    }
                    else if (input.didTertiary() && clickPosition.x != -1 && clickPosition.y != -1) {
                        // Save current mouse location in arrowModel
                        currMouse = new Vector2(Gdx.input.getX(),Gdx.input.getY());
                        arrow.setCurrLoc(currMouse);
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

    }

    public ArrowModel getArrow() { return arrow; }

    public boolean getPossessedBlownUp() { return possessedBlownUp; }

    public boolean getLaunched() {
        if (launched) {
            launched = false;
            return true;
        }
        else {
            return false;
        }
    }

}