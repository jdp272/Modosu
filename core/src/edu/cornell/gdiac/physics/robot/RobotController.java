package edu.cornell.gdiac.physics.robot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

public class RobotController extends GamePlayController {

    /** Texture assets for the robot */
    private TextureRegion robotTexture;

    /** Track asset loading from all instances and subclasses */
    private AssetState robotAssetState = AssetState.EMPTY;

<<<<<<< HEAD
    /** List of all the robots */
    private RobotList robotList;

    /** The click position of the cursor */
    private Vector2 clickPosition = new Vector2(-1,-1);

    /** The vector created by the shot */
    private Vector2 shootVector;

    /** Velocity that the robot travels */
    private Vector2 robotVelocity;

    /** Velocity that the spirit travels travels */
    private Vector2 spiritVelocity;
=======
    private Vector2 CLICK_POS;

    private Vector2 SHOOT_VEC;

    //get the list from robotlist
>>>>>>> 3f2a1b1e2eca152f8d0995acfb766be3f0e5aae7

    /**
     * Preloads the assets for this controller.
     *
     * To make the game modes more for-loop friendly, we opted for nonstatic loaders
     * this time.  However, we still want the assets themselves to be static.  So
     * we have an AssetState that determines the current loading state.  If the
     * assets are already loaded, this method will do nothing.
     *
     * @param manager Reference to global asset manager.
     */
    public void preLoadContent(AssetManager manager) {
        if (robotAssetState != AssetState.EMPTY) {
            return;
        }

        robotAssetState = AssetState.LOADING;

        // Robot textures
        // manager.load(ROCK_TEXTURE, Texture.class);
        // assets.add(ROCK_TEXTURE);

        // Robot sounds
        // manager.load(MAIN_FIRE_SOUND, Sound.class);
        // assets.add(MAIN_FIRE_SOUND);

        super.preLoadContent(manager);
    }

    /**
     * Loads the assets for this controller.
     *
     * To make the game modes more for-loop friendly, we opted for nonstatic loaders
     * this time.  However, we still want the assets themselves to be static.  So
     * we have an AssetState that determines the current loading state.  If the
     * assets are already loaded, this method will do nothing.
     *
     * @param manager Reference to global asset manager.
     */
    public void loadContent(AssetManager manager) {
        if (robotAssetState != AssetState.LOADING) {
            return;
        }

        // robotTexture = createTexture(manager,ROCK_TEXTURE,false);

        SoundController sounds = SoundController.getInstance();
        // sounds.allocate(manager,MAIN_FIRE_SOUND);

        super.loadContent(manager);
        robotAssetState = AssetState.COMPLETE;
    }

    /** The number of ticks since we started this controller */
    private long ticks;

    /**
     * Creates and initialize a new instance of the rocket lander game
     *
     * The game has default gravity and other settings
     */
    public RobotController() {
        setDebug(false);
        setComplete(false);
        setFailure(false);
    }

    /**
     * Resets the status of the robot.
     *
     * This method disposes of the robot and creates a new one.
     */
    public void reset() {
        // should robot controller have a list of robots?
        // because initally all of these are gonna be AI
        setComplete(false);
        setFailure(false);
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
    public void update(float dt) {

        RobotModel robot = collisionController.getRobotPossessed();

        InputController input = InputController.getInstance();

        input.readInput(bounds, scale); // do we need this?
        if(possessed != null) {
            if(possessed.decCharge()){
                possessed.setVX(robot.getVX() * input.getHorizontal());
                possessed.setVY(robot.getVX() * input.getVertical());
            }else{
                possessed.setVX(0);
                possessed.setVY(0);
                //change texture because it blew up
            }
        }
        // check if timer is up? then lose the game? or is that in gameplay controller?
        // update timer
        // If almost blow up, add specific animations??
        // Check if Robot will blow up

        if (robot.willExplode()) {
            // robot explode animation
            // reset()

            setFailure(true);
            setComplete(false);
        }

        // if not about to die...
        // Update Animations

        // If we use sound, we must remember this.
        //SoundController.getInstance().update();

        // Shooting the
        if (input.didTertiary() && clickPosition.x == -1 && clickPosition.y == -1) {
            // Clicked Mouse
            clickPosition = input.getCrossHair();
        } else if (!input.didTertiary() && clickPosition.x != -1 && clickPosition.y != -1) {
            // Released Mouse -- Shoot
            shootVector = input.getCrossHair().sub(clickPosition);
            clickPosition.x = -1;
            clickPosition.y = -1;

            float vx = spirit.getVX() * shootVector.x;
            float vy = spirit.getVY() * shootVector.y;

            spirit.setVX(vx);
            spirit.setVY(vy);
         } else if (input.didTertiary() && clickPosition.x != -1 && clickPosition.y != -1) {
             // Arrow Direction?
         }
    }

}
