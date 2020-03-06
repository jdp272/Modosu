package edu.cornell.gdiac.physics.robot;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

// import com.badlogic.gdx.graphics.g2d.TextureRegion;
// import com.badlogic.gdx.physics.box2d.*;
// import edu.cornell.gdiac.physics.InputController;
// import edu.cornell.gdiac.physics.obstacle.BoxObstacle;

public class RobotController extends GamePlayController {

    /** Texture assets for the robot */
    private TextureRegion robotTexture;

    /** Track asset loading from all instances and subclasses */
    private AssetState robotAssetState = AssetState.EMPTY;

    //get the list from robotlist

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
        //world.setContactListener(this);
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

        InputController input = InputController.getInstance();

        input.readInput(bounds, scale); // do we need this?

        possessed.setVX(input.getHorizontal());
        possessed.setVY(input.getVertical());

        // check if timer is up? then lose the game? or is that in gameplay controller?
        // update timer
        // If almost blow up, add specific animations??
        // Check if Robot will blow up

        // Update Animations??



        // If we use sound, we must remember this.
        //SoundController.getInstance().update();

         //click position, shoot vector
         if(input.didTertiary() && CLICK_POS.x == -1 && CLICK_POS.y == -1){ // initalized with -1,-1
            CLICK_POS = input.getCrossHair();
      // no arrow
        }else if (!input.didTertiary() && CLICK_POS.x != -1 && CLICK_POS.y != -1){ // letting go
            SHOOT_VEC = input.getCrossHair().sub(CLICK_POS);
            CLICK_POS.x = -1;
            CLICK_POS.y = -1;

//            TextureRegion texture = spiritTexture;
//            float dwidth  = texture.getRegionWidth()/scale.x;
//            float dheight = texture.getRegionHeight()/scale.y;
//
//            BoxObstacle spirit = new BoxObstacle(rocket.getX(),rocket.getY(),dwidth,dheight);
//            spirit.setDensity(CRATE_DENSITY);
//            spirit.setFriction(CRATE_FRICTION);
//            spirit.setRestitution(BASIC_RESTITUTION);
//            spirit.setName("spirit");
//            spirit.setDrawScale(scale);
//            spirit.setTexture(texture);
//            float vx = (thrust/3)*(-2)*(SHOOT_VEC.x);
//            float vy = (thrust/3)*(-2)*(SHOOT_VEC.y);
//            spirit.setVX(vx);
//            spirit.setVY(vy);
//            Filter filter = spirit.getFilterData();
//            filter.groupIndex = -1;
//            spirit.setFilterData(filter);
//            spirit.alive = 60;
//            spirit.setRestitution(0.8f);
//            addQueue.add(spirit);
        }
    }
}
