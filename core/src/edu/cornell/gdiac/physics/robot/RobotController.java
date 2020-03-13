package edu.cornell.gdiac.physics.robot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

public class RobotController {

    /** Texture assets for the robot */
    private TextureRegion robotTexture;

    /** List of all the robots */
    private RobotList robots;

    /** The click position of the cursor */
    private Vector2 clickPosition;

    /** The vector created by the shot */
    private Vector2 shootVector;

    private InputController inputController;


    /** The number of ticks since we started this controller */
    private long ticks;

    /**
     * Creates and initialize a new instance of the rocket lander game
     *
     * The game has default gravity and other settings
     */
    public RobotController(RobotList r) {
        inputController = new InputController();
        clickPosition = new Vector2(-1,-1);
        robots = r;
    }

    /**
     * Resets the status of the robot.
     *
     * This method disposes of the robot and creates a new one.
     */
    public void reset() {
        // should robot controller have a list of robots?
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
    public void update(float dt, RobotModel possessed, SpiritModel spirit) {

        //RobotModel robot = possessed;

        InputController input = InputController.getInstance();

        if (possessed != null) {
            if (possessed.incCurrentCharge()){
                possessed.setVX(500000 * input.getHorizontal());
                possessed.setVY(500000 * input.getVertical());

                // Shooting the
                if (input.didTertiary() && clickPosition.x == -1 && clickPosition.y == -1) {
                    // Clicked Mouse
                    clickPosition = new Vector2(Gdx.input.getX(),Gdx.input.getY());;//input.getCrossHair();
                } else if (!input.didTertiary() && clickPosition.x != -1 && clickPosition.y != -1) {// Released Mouse -- Shoot
                    shootVector = new Vector2(Gdx.input.getX(),Gdx.input.getY());
                    shootVector = shootVector.sub(clickPosition);
                    shootVector.x = -shootVector.x;

                    clickPosition.x = -1;
                    clickPosition.y = -1;

                    float vx = 50 * shootVector.x;
                    float vy = 50 * shootVector.y;

                    spirit.setPosition(possessed.getPosition());
                    spirit.setVX(vx);
                    spirit.setVY(vy);
                } else if (input.didTertiary() && clickPosition.x != -1 && clickPosition.y != -1) {
                    // Arrow Direction?
                }

            }
            else { // ROBOT HAS BLOWN UP
                possessed.setVX(0);
                possessed.setVY(0);

                // change texture because it blew up
            }
        }

        // Update Animations

        // If we use sound, we must remember this.
        //SoundController.getInstance().update();
    }

}
