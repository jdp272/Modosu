/*
 * RagdollController.java
 *
 * You are not expected to modify this file at all.  You are free to look at it, however,
 * and determine how it works.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.obstacle.*;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.SoundController;

import java.util.ArrayList;

/**
 * Manages the level selection of Modosu
 *
 */
public class LevelSelectMode extends WorldController implements Screen, InputProcessor {
    /** Texture file for background image */
    private static final String BACKG_FILE = "shared/levelselectbackground.png";
    private static final String ONE_FILE = "shared/1.png";
    private static final String TWO_FILE = "shared/2.png";
    private static final String THREE_FILE = "shared/3.png";

    private static int LEVEL_X_START = 100;
    private static int LEVEL_Y = 20;
    private static int LEVEL_BUTTON_SPACING = 100;

    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;

    /** Texture asset for background image */
    private TextureRegion backgroundTexture;
    private TextureRegion oneTexture;
    private TextureRegion twoTexture;
    private TextureRegion threeTexture;

    private Vector2 oneStart;
    private Vector2 oneEnd;
    private Vector2 twoStart;
    private Vector2 twoEnd;
    private Vector2 threeStart;
    private Vector2 threeEnd;


    /** Track asset loading from all instances and subclasses */
    private AssetState assetState = AssetState.EMPTY;


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
        if (assetState != AssetState.EMPTY) {
            return;
        }

        assetState = AssetState.LOADING;
        manager.load(BACKG_FILE, Texture.class);
        assets.add(BACKG_FILE);
        manager.load(ONE_FILE, Texture.class);
        assets.add(ONE_FILE);
        manager.load(TWO_FILE, Texture.class);
        assets.add(TWO_FILE);
        manager.load(THREE_FILE, Texture.class);
        assets.add(THREE_FILE);

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
        if (assetState != AssetState.LOADING) {
            return;
        }
        backgroundTexture = createTexture(manager,BACKG_FILE,false);
        oneTexture = createTexture(manager,ONE_FILE,false);
        twoTexture = createTexture(manager,TWO_FILE,false);
        threeTexture = createTexture(manager,THREE_FILE,false);

        oneStart = new Vector2(LEVEL_X_START, LEVEL_Y);
        oneEnd = new Vector2(LEVEL_X_START + oneTexture.getRegionWidth(), LEVEL_Y+oneTexture.getRegionHeight());
        twoStart = new Vector2(oneEnd.x + LEVEL_BUTTON_SPACING, LEVEL_Y);
        twoEnd = new Vector2(twoStart.x+twoTexture.getRegionWidth(), LEVEL_Y+twoTexture.getRegionHeight());
        threeStart = new Vector2(twoEnd.x + LEVEL_BUTTON_SPACING, LEVEL_Y);
        threeEnd = new Vector2(threeStart.x+threeTexture.getRegionWidth(), LEVEL_Y+threeTexture.getRegionHeight());

        super.loadContent(manager);
        assetState = AssetState.COMPLETE;
    }

    /** The new lessened gravity for this world */
    private static final float WATER_GRAVITY = -0.25f;


    /**
     * Creates and initialize a new instance of the level designer
     *
     * The world has lower gravity to simulate being underwater.
     */
    public LevelSelectMode() {
        super(DEFAULT_WIDTH,DEFAULT_HEIGHT,WATER_GRAVITY);
        setDebug(false);
        setComplete(false);
        setFailure(false);
    }

    /**
     * Resets the status of the game so that we can play again.
     *
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
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
    }


    /**
     * Draw the physics objects together with foreground and background
     *
     * This is completely overridden to support custom background and foreground art.
     *
     * @param dt Timing values from parent loop
     */
    public void draw(float dt) {
        canvas.begin();
        canvas.draw(backgroundTexture, 0, 0);
        canvas.draw(oneTexture, oneStart.x,oneStart.y);
        canvas.draw(twoTexture, twoStart.x, twoStart.y);
        canvas.draw(threeTexture, threeStart.x, threeStart.y);
        canvas.end();
    }

    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
        Gdx.input.setInputProcessor(this);
    }

    /**
     * Called when the screen was touched or a mouse button was pressed.
     *
     * This method checks to see if the play button is available and if the click
     * is in the bounds of the play button.  If so, it signals the that the button
     * has been pressed and is currently down. Any mouse button is accepted.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        // Flip to match graphics coordinates
        screenY = 576-screenY;

        if(screenX >=  oneStart.x && screenX <= oneEnd.x) {
            if (screenY >= oneStart.y && screenY <= oneEnd.y) {
                System.out.println("opening level 1, custom.lvl");
                listener.exitScreenLevel(0);
            }
        }

        if(screenX >= twoStart.x && screenX <= twoEnd.x ) {
            if (screenY >= twoStart.y && screenY <= twoEnd.y) {
                System.out.println("opening level 2, out.lvl");
                listener.exitScreenLevel(1);
            }
        }

        if(screenX >= threeStart.x && screenX <= threeEnd.x) {
            if (screenY >= threeStart.y && screenY <= threeEnd.y)  {
                System.out.println("opening level 3, mays.lvl");
            }
            listener.exitScreenLevel(2);
        }

        return false;
    }


    /**
     * Called when a finger was lifted or a mouse button was released.
     *
     * This method checks to see if the play button is currently pressed down. If so,
     * it signals the that the player is ready to go.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//        if (pressState == 1) {
//            pressState = 2;
//            return false;
//        }
//        if (pressState == 3) {
//            pressState = 4;
//            return false;
//        }
        return true;
    }

    /**
     * Called when a button on the Controller was pressed.
     *
     * The buttonCode is controller specific. This listener only supports the start
     * button on an X-Box controller.  This outcome of this method is identical to
     * pressing (but not releasing) the play button.
     *
     * @param controller The game controller
     * @param buttonCode The button pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean buttonDown (Controller controller, int buttonCode) {
//        if (buttonCode == startButton && pressState == 0) {
//            pressState = 1;
//            return false;
//        }

        return true;
    }

    /**
     * Called when a button on the Controller was released.
     *
     * The buttonCode is controller specific. This listener only supports the start
     * button on an X-Box controller.  This outcome of this method is identical to
     * releasing the the play button after pressing it.
     *
     * @param controller The game controller
     * @param buttonCode The button pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean buttonUp (Controller controller, int buttonCode) {
//        if (pressState == 1 && buttonCode == startButton) {
//            pressState = 2;
//            return false;
//        }

        return true;
    }

    // UNSUPPORTED METHODS FROM InputProcessor

    /**
     * Called when a key is pressed (UNSUPPORTED)
     *
     * @param keycode the key pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean keyDown(int keycode) {
        return true;
    }

    /**
     * Called when a key is typed (UNSUPPORTED)
     *
     * @param character the key typed
     * @return whether to hand the event to other listeners.
     */
    public boolean keyTyped(char character) {
        return true;
    }

    /**
     * Called when a key is released.
     *
     * We allow key commands to start the game this time.
     *
     * @param keycode the key released
     * @return whether to hand the event to other listeners.
     */
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.N || keycode == Input.Keys.P) {
//            pressState = 2;
            System.out.println("pressed N or P");
            return false;
        }
        return true;
    }

    /**
     * Called when the mouse was moved without any buttons being pressed. (UNSUPPORTED)
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @return whether to hand the event to other listeners.
     */
    public boolean mouseMoved(int screenX, int screenY) {
        screenY = 576-screenY;

        if(screenX >=  oneStart.x && screenX <= oneEnd.x) {
            if (screenY >= oneStart.y && screenY <= oneEnd.y) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }
        }

        if(screenX >= twoStart.x && screenX <= twoEnd.x ) {
            if (screenY >= twoStart.y && screenY <= twoEnd.y) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }
        }

        if(screenX >= threeStart.x && screenX <= threeEnd.x) {
            if (screenY >= threeStart.y && screenY <= threeEnd.y)  {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }
        }
        return true;
    }

    /**
     * Called when the mouse wheel was scrolled. (UNSUPPORTED)
     *
     * @param amount the amount of scroll from the wheel
     * @return whether to hand the event to other listeners.
     */
    public boolean scrolled(int amount) {
        return true;
    }

    /**
     * Called when the mouse or finger was dragged. (UNSUPPORTED)
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

}