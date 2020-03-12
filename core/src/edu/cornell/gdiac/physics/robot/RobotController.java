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

    private InputController input;

    /** Constant to change the speed of golem movement */
    private static final float GOLEM_MOVEMENT_SPEED = 500000;
    private static final float MINIMUM_SHOT_SPEED = 50;

    /** The number of ticks since we started this controller */
    private long ticks;

    /**
     * Creates and initialize a new instance of the rocket lander game
     *
     * The game has default gravity and other settings
     */
    public RobotController(RobotList r) {
        input = InputController.getInstance();
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
        input = InputController.getInstance();

        if (possessed != null) {
            if (!spirit.getHasLaunched()) { spirit.setPosition(possessed.getPosition()); }

            if (possessed.decCharge()) {
                if (!spirit.getHasLaunched()) {
                    possessed.setVX(GOLEM_MOVEMENT_SPEED * input.getHorizontal());
                    possessed.setVY(GOLEM_MOVEMENT_SPEED * input.getVertical());
                    // Shooting the spirit
                    if (input.didTertiary() && clickPosition.x == -1 && clickPosition.y == -1) { // Clicked Mouse
                        spirit.setPosition(possessed.getPosition());
                        clickPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY()); //input.getCrossHair();
                    } else if (!input.didTertiary() && clickPosition.x != -1 && clickPosition.y != -1) { // Released Mouse -- Shoot
                        shootVector = new Vector2(Gdx.input.getX(), Gdx.input.getY());
                        shootVector = shootVector.sub(clickPosition);
                        shootVector.x = -shootVector.x;

                        clickPosition.x = -1;
                        clickPosition.y = -1;

                        float vx = shootVector.x;
                        float vy = shootVector.y;

                        spirit.setPosition(possessed.getPosition());
                        spirit.setVX(vx);
                        spirit.setVY(vy);

                        if (Math.abs(vx) > MINIMUM_SHOT_SPEED || Math.abs(vy) > MINIMUM_SHOT_SPEED) spirit.setHasLaunched(true);

                    } else if (input.didTertiary() && clickPosition.x != -1 && clickPosition.y != -1) {
                        // Arrow Direction?
                        spirit.setPosition(possessed.getPosition());
                    }
                } else {
                    possessed.setVX(0);
                    possessed.setVY(0);
                }
            }
            else {
                System.out.println("THE GOLEM HAS BLOWN UP.");
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
