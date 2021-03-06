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
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import edu.cornell.gdiac.util.ScreenListener;
import edu.cornell.gdiac.util.SoundController;

import java.io.File;
import java.util.*;


/**
 * Manages the level selection of Modosu
 */
public class LevelSelectMode extends WorldController implements Screen {
    /** Texture file for background image */
    private static final String BACKG_FILE = "shared/levelselectbackground.png";
    private static final String NEXT_FILE = "shared/next.png";
    private static final String CLICK_SOUND = "shared/click.mp3";
    private static final String HOVER_SOUND = "shared/hover.mp3";
    private static final String BACK_FILE = "shared/backbutton.png";

    private static final String FONT_FILE = "shared/Asul.ttf";
    private static final int FONT_SIZE = 42;

    private BitmapFont levelFont;

    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;

    /** Texture asset for background image */
    private TextureRegion backgroundTexture;
    private TextureRegion nextTexture;
    private TextureRegion prevTexture;
    private TextureRegion backTexture;

    /** Vectors that maintain the positions of the level buttons */
    private Vector2 oneStart;
    private Vector2 oneEnd;
    private Vector2 twoStart;
    private Vector2 twoEnd;
    private Vector2 threeStart;
    private Vector2 threeEnd;
    private Vector2 fourStart;
    private Vector2 fourEnd;
    private Vector2 nextStart;
    private Vector2 nextEnd;
    private Vector2 prevStart;
    private Vector2 prevEnd;
    private Vector2 customStart;
    private Vector2 customEnd;
    private Vector2 backStart;
    private Vector2 backEnd;

    private boolean onCustom;

    /** Color of buttons when hovered */
    private static Color colorHovered;
    /** Color of buttons when not hovered */
    private static Color colorUnhovered;

    /** Mouse is currently hovering over a button */
    private boolean hoverButton;
    /** Mouse is currently hovering over the Custom Button */
    private boolean hoverCustom;
    /** Mouse is currently hovering over the Back Button */
    private boolean hoverBack;
    /** Volume of hover sound */
    private static float hoverVolume = .25f;

    /** Color of level one button */
    private Color colorOne;
    /** Color of level two button */
    private Color colorTwo;
    /** Color of level three button */
    private Color colorThree;
    /** Color of level four button */
    private Color colorFour;
    /** Color of next button */
    private Color colorNext;
    /** Color of prev button */
    private Color colorPrev;
    /** Color of back button */
    private Color colorBack;
    /** Color of custom button */
    private Color customColor;


    /** Track asset loading from all instances and subclasses */
    private AssetState assetState = AssetState.EMPTY;

    /** Get user input through the controller */
    private InputController input;

    /** Page of levels user is on */
    private int page;
    /** Number of pages needed to display all levels available */
    private int pages;

    public boolean goToDesigner;

    /** Whether the mouse is currently pressed down  */
    private boolean  isPressed;

    /** State of what was pressed down  */
    private int pressState;

    /**
     * A boolean indicating if preset levels should be offered. For normal
     * playing, preset levels are likely wanted, but before going to level
     * designer, preset levels should not be available.
     */
    private boolean forceCustom = false;

    /**
     * A boolean indicating if preset levels should be offered. For normal
     * playing, preset levels are likely wanted, but before going to level
     * designer, preset levels should not be available.
     *
     * @param value The new value for force custom
     */
    public void setForceCustom(boolean value) {
        forceCustom = value; // Set this first so setCustom will work properly
        setCustom(value);
    }

    /**
     * Set whether to use custom levels or not. Will not function if forceCustom
     * is true
     *
     * @param custom Whether custom levels should be used or not
     */
    private void setCustom(boolean custom) {
        if(forceCustom && !custom) {
            // If forceCustom, make sure custom is set to true, regardless of
            // the input to this function
            setCustom(true);
            return;
        }

        // Only reset the page if the mode actually switched
        if(onCustom != custom) {
            page = 0;
        }

        onCustom = custom;

        getLevels(custom);
        setPages();
    }

    /**
     * Sets the number of pages
     */
    public void setPages() {
        pages = (int) Math.ceil(levels.size() / 4.0);
    }

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
        if (assetState != AssetState.EMPTY) { return; }

        assetState = AssetState.LOADING;
        manager.load(BACKG_FILE, Texture.class);
        assets.add(BACKG_FILE);
        manager.load(NEXT_FILE, Texture.class);
        assets.add(NEXT_FILE);
        manager.load(BACK_FILE, Texture.class);
        assets.add(BACK_FILE);

        // Load the font
        FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        size2Params.fontFileName = FONT_FILE;
        size2Params.fontParameters.size = FONT_SIZE;
        manager.load(FONT_FILE, BitmapFont.class, size2Params);
        assets.add(FONT_FILE);


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
        nextTexture = createTexture(manager,NEXT_FILE,false);
        prevTexture = createTexture(manager,NEXT_FILE, false);
        backTexture = createTexture(manager,BACK_FILE, false);
        prevTexture.flip(true,false);

        levelFont = manager.get(FONT_FILE,BitmapFont.class);
        float height = backgroundTexture.getRegionHeight();
        float width = backgroundTexture.getRegionWidth();
        oneStart = new Vector2(width*0.14f,height*0.15f);
        oneEnd = new Vector2(width*0.22f,height*0.59f);
        twoStart = new Vector2( width*0.34f,height*0.25f);
        twoEnd = new Vector2(width*0.41f, height*0.59f);
        threeStart = new Vector2( width*0.59f,height*0.13f);
        threeEnd = new Vector2(width*0.65f, height*0.59f);
        fourStart = new Vector2( width*0.80f,height*0.23f);
        fourEnd = new Vector2(width*0.88f, height*0.59f);
        nextStart = new Vector2(width*0.95f, height*0.45f);
        nextEnd = new Vector2(nextStart.x+nextTexture.getRegionWidth(), nextStart.y+nextTexture.getRegionHeight());
        prevStart = new Vector2(width*0.05f , nextStart.y);
        prevEnd = new Vector2(prevStart.x+nextTexture.getRegionWidth(), prevStart.y+nextTexture.getRegionHeight());
        backStart = new Vector2(width*0.05f,  height*0.83f);
        backEnd = new Vector2(backStart.x + backTexture.getRegionWidth(), backStart.y + backTexture.getRegionHeight());
        customStart = new Vector2(width - 150,height - 50);
        customEnd = new Vector2(customStart.x+100, customStart.y+nextTexture.getRegionHeight());

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

        input = InputController.getInstance();
        page = 0;

        colorHovered = new Color(Color.rgb565(210f,251f,248f));
        colorUnhovered = Color.WHITE;
        colorOne = colorUnhovered;
        colorTwo = colorUnhovered;
        colorThree = colorUnhovered;
        colorFour = colorUnhovered;
        colorNext = colorUnhovered;
        colorPrev = colorUnhovered;
        colorBack = colorUnhovered;
        customColor = colorUnhovered;

        onCustom = false;
        hoverCustom = false;
        hoverBack = false;

        getLevels(false);
    }

    /**
     * Resets the status of the game so that we can play again.
     *
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
        renderHUD = false;

        getLevels(onCustom);
        setPages();
    }



    /**
     * The core update loop of this menu screen.
     *
     * This method contains the specific update code for this mini-game. It does
     * not handle collisions, as those are managed by the parent class WorldController.
     * This method is called after input is read, but before collisions are resolved.
     * The very last thing that it should do is apply forces to the appropriate objects.
     *
     * @param dt Number of seconds since last animation frame
     */
    public void update(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.M)) {
            listener.exitScreen(this,WorldController.EXIT_MENU);
        }
        // Update sounds
        SoundController.getInstance().update();

        Vector2 pos = input.getMousePosition();

        float screenY = pos.y;
        float screenX = pos.x;
        if (input.didLeftClick()) { updatePressed(screenX, screenY); }
        else if (input.didRelease()) { updateReleased(screenX, screenY);}
        updateHover(screenX, screenY);

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

        // Draw back button
        canvas.draw(backTexture, colorBack, 0f, 0f, backStart.x, backStart.y, 0, 1, 1);

        if (page != pages-1) {
            canvas.draw(nextTexture, colorNext, 0f, 0f, nextStart.x, nextStart.y, 0, 1, 1);
        }
        if(page != 0) {
            canvas.draw(prevTexture, colorPrev, 0f, 0f, prevStart.x, prevStart.y, 0, 1, 1);
        }
        levelFont.setColor(colorOne);
        try {
            String name = levels.get(page * 4).file().getName();

        Vector2 center = new Vector2((oneEnd.x+oneStart.x)/2,(oneEnd.y+oneStart.y)/2);
        canvas.drawText(name.substring(0,name.length()-4), levelFont, center.x, center.y);

        if (page*4 + 1 < levels.size()) {
            center.x = (twoEnd.x+twoStart.x)/2;
            levelFont.setColor(colorTwo);
            name = levels.get(page * 4 + 1).file().getName();
            canvas.drawText(name.substring(0, name.length() - 4), levelFont, center.x, center.y+backgroundTexture.getRegionHeight()*0.1f);
        }
        if (page*4 + 2 < levels.size()) {
            center.x = (threeEnd.x+threeStart.x)/2;
            levelFont.setColor(colorThree);
            name = levels.get(page * 4 + 2).file().getName();
            canvas.drawText(name.substring(0, name.length() - 4), levelFont, center.x, center.y-backgroundTexture.getRegionHeight()*0.07f);
        }
        if (page*4 + 3 < levels.size()) {
            center.x = (fourEnd.x+fourStart.x)/2;
            levelFont.setColor(colorFour);
            name = levels.get(page * 4 + 3).file().getName();
            canvas.drawText(name.substring(0, name.length() - 4), levelFont, center.x, center.y+backgroundTexture.getRegionHeight()*0.1f);
        }

        center.x = (customEnd.x+customStart.x)/2;
        center.y = customEnd.y;//(customEnd.y + customStart.y)/2;
        levelFont.setColor(customColor);
        levelFont.getData().setScale(0.75f, 0.75f);
        if(!onCustom || forceCustom){
            // Show the word custom when it is a button to go to the custom
            // presets, or if the player must stay on custom levels
            name = "Custom";
        }else{
            name = "Presets";
        }
        canvas.drawText(name, levelFont, center.x, center.y);
        }
        catch (Exception e) {
            System.out.println("No levels to load on this page");
        }
        if(onCustom)
            levelFont.getData().setScale(.8f, .8f);
        else
            levelFont.getData().setScale(1f, 1f);

        levelFont.setColor(colorUnhovered);
        canvas.end();
    }

    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }


    /**
     * This is called when the mouse was pressed. It checks to see if the button press
     * was on a valid button region. If so, it signals the that a valid button
     * has been pressed and is currently down.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     *
     */
    public void updatePressed(float screenX, float screenY) {

        if(screenX >=  oneStart.x && screenX <= oneEnd.x) {
            if (screenY >= oneStart.y && screenY <= oneEnd.y) {
                SoundController.getInstance().play(CLICK_SOUND, CLICK_SOUND, false);
                pressState = 0;
                isPressed = true;
            }
        }

        if(screenX >= twoStart.x && screenX <= twoEnd.x && (page*4 + 1 < levels.size() || goToDesigner)) {
            if (screenY >= twoStart.y && screenY <= twoEnd.y) {
                SoundController.getInstance().play(CLICK_SOUND, CLICK_SOUND, false);
                pressState = 1;
                isPressed = true;
            }
        }

        if(screenX >= threeStart.x && screenX <= threeEnd.x && (page*4 + 2 < levels.size() || goToDesigner)) {
            if (screenY >= threeStart.y && screenY <= threeEnd.y)  {
                SoundController.getInstance().play(CLICK_SOUND, CLICK_SOUND, false);
                pressState = 2;
                isPressed = true;
            }
        }

        if(screenX >= fourStart.x && screenX <= fourEnd.x && (page*4 + 3 < levels.size() || goToDesigner)) {
            if (screenY >= fourStart.y && screenY <= fourEnd.y)  {
                SoundController.getInstance().play(CLICK_SOUND, CLICK_SOUND, false);
                pressState = 3;
                isPressed = true;
            }
        }

        if(screenX >= nextStart.x && screenX <= nextEnd.x && page != pages-1) {
            if (screenY >= nextStart.y && screenY <= nextEnd.y)  {
                SoundController.getInstance().play(CLICK_SOUND, CLICK_SOUND, false);
                pressState = 4;
                isPressed = true;
            }
        }
        if(screenX >= prevStart.x && screenX <= prevEnd.x && page != 0) {
            if (screenY >= prevStart.y && screenY <= prevEnd.y)  {
                SoundController.getInstance().play(CLICK_SOUND, CLICK_SOUND, false);
                pressState = 5;
                isPressed = true;
            }
        }

        if(screenX >= customStart.x && screenX <= customEnd.x) {
            if (screenY >= customStart.y && screenY <= customEnd.y)  {
                if(!forceCustom) {
                    SoundController.getInstance().play(CLICK_SOUND, CLICK_SOUND, false);
                    pressState = 6;
                    isPressed = true;
                }
            }
        }

        if(screenX >= backStart.x && screenX <= backEnd.x) {
            if (screenY >= backStart.y && screenY <= backEnd.y) {
                pressState = 7;
                isPressed = true;
            }
        }
    }


    /**
     * This method is called when a mouse was released. It is part of the mode update to see if
     * there was a button in the release region. If so, then the player is moved to the level selected.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     *
     */
    public void updateReleased(float screenX, float screenY) {
        if (isPressed) {
            if (screenX >= oneStart.x && screenX <= oneEnd.x) {
                if (screenY >= oneStart.y && screenY <= oneEnd.y && pressState == 0 ) {
                    listener.exitScreenLevel(0, page, onCustom);
                }

            }
            if (screenX >= twoStart.x && screenX <= twoEnd.x) {
                if (screenY >= twoStart.y && screenY <= twoEnd.y && pressState == 1) {
                    if(page * 4 + 1 < levels.size()) {
                        listener.exitScreenLevel(1, page, onCustom);
                    }else{
                        listener.exitScreenLevel(-1, page, onCustom);
                    }
                }
            }
            if (screenX >= threeStart.x && screenX <= threeEnd.x) {
                if (screenY >= threeStart.y && screenY <= threeEnd.y && pressState == 2) {
                    if(page * 4 + 2 < levels.size()) {
                        listener.exitScreenLevel(2, page, onCustom);
                    }else{
                        listener.exitScreenLevel(-1, page, onCustom);
                    }
                }
            }
            if (screenX >= fourStart.x && screenX <= fourEnd.x) {
                if (screenY >= fourStart.y && screenY <= fourEnd.y && pressState == 3) {
                    if(page * 4 + 3 < levels.size()) {
                        listener.exitScreenLevel(3, page, onCustom);
                    }else{
                        listener.exitScreenLevel(-1, page, onCustom);
                    }
                }

            }
            if (screenX >= nextStart.x && screenX <= nextEnd.x && page != pages - 1) {
                if (screenY >= nextStart.y && screenY <= nextEnd.y && pressState == 4) { page++; }
            }

            if (screenX >= prevStart.x && screenX <= prevEnd.x && page != 0) {
                if (screenY >= prevStart.y && screenY <= prevEnd.y && pressState == 5) { page--; }
            }

            if (screenX >= customStart.x && screenX <= customEnd.x) { setCustom(!onCustom); }

            if (screenX >= backStart.x && screenX <= backEnd.x) {
                if (screenY >= backStart.y && screenY <= backEnd.y && pressState == 7) {
                    listener.exitScreen(this, WorldController.EXIT_MENU);
                }
            }
        }
        colorOne = colorUnhovered;
        colorTwo = colorUnhovered;
        colorThree = colorUnhovered;
        colorFour = colorUnhovered;
        colorNext = colorUnhovered;
        colorPrev = colorUnhovered;
        colorBack = colorUnhovered;
        hoverButton = false;
        isPressed = false;
    }

    /**
     * This is always called in the mode update method.
     *
     * This method checks to see if the player mouse is hovering over any level buttons.
     * If so, the color of the button should change when drawn.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     *
     */
    public void updateHover(float screenX, float screenY) {
        if (canvas != null) {

            colorOne = colorUnhovered;
            colorTwo = colorUnhovered;
            colorThree = colorUnhovered;
            colorFour = colorUnhovered;
            colorNext = colorUnhovered;
            colorPrev = colorUnhovered;
            colorBack = colorUnhovered;
            customColor = colorUnhovered;

            if (screenX >= customStart.x && screenX <= customEnd.x) {
                if(!forceCustom) {
                    if (screenY >= customStart.y && screenY <= customEnd.y) {
                        customColor = colorHovered;
                        if (!hoverCustom) {
                            SoundController.getInstance().playHover(HOVER_SOUND, HOVER_SOUND, false, hoverVolume);
                            hoverCustom = true;
                        }
                    } else {
                        customColor = colorUnhovered;
                        hoverCustom = false;
                    }
                }
            }
            if (screenX >= backStart.x && screenX <= backEnd.x) {
                if (screenY >= backStart.y && screenY <= backEnd.y) {
                    colorBack = Color.TEAL;
                    if (!hoverBack) {
                        SoundController.getInstance().playHover(HOVER_SOUND, HOVER_SOUND, false, hoverVolume);
                        hoverBack = true;
                    }
                }
                else {
                    colorBack = colorUnhovered;
                    hoverBack = false;
                }
            }
            if (screenX >= oneStart.x && screenX <= oneEnd.x) {
                if (screenY >= oneStart.y && screenY <= oneEnd.y) {
                    colorOne = colorHovered;
                    if (!hoverButton) {
                        SoundController.getInstance().playHover(HOVER_SOUND, HOVER_SOUND, false, hoverVolume);
                        hoverButton = true;
                    }
                }
                else {
                    colorOne = colorUnhovered;
                    hoverButton = false;
                }
            }
            else if (screenX >= twoStart.x && screenX <= twoEnd.x && (page*4 + 1 < levels.size() || goToDesigner)) {
                if (screenY >= twoStart.y && screenY <= twoEnd.y) {
                    colorTwo = colorHovered;
                    if (!hoverButton) {
                        SoundController.getInstance().playHover(HOVER_SOUND, HOVER_SOUND, false, hoverVolume);
                        hoverButton = true;
                    }
                }
                else {
                    colorTwo = colorUnhovered;
                    hoverButton = false;
                }
            }
            else if (screenX >= threeStart.x && screenX <= threeEnd.x && (page*4 + 2 < levels.size() || goToDesigner)) {
                if (screenY >= threeStart.y && screenY <= threeEnd.y) {
                    colorThree = colorHovered;
                    if (!hoverButton) {
                        SoundController.getInstance().playHover(HOVER_SOUND, HOVER_SOUND, false, hoverVolume);
                        hoverButton = true;
                    }
                }
                else {
                    colorThree = colorUnhovered;
                    hoverButton = false;
                }
            }
            else if (screenX >= fourStart.x && screenX <= fourEnd.x && (page*4 + 3 < levels.size() || goToDesigner)) {
                if (screenY >= fourStart.y && screenY <= fourEnd.y) {
                    colorFour = colorHovered;
                    if (!hoverButton) {
                        SoundController.getInstance().playHover(HOVER_SOUND, HOVER_SOUND, false, hoverVolume);
                        hoverButton = true;
                    }
                }
                else {
                    colorFour = colorUnhovered;
                    hoverButton = false;
                }
            }
            else if (screenX >= nextStart.x && screenX <= nextEnd.x && page != pages-1) {
                if (screenY >= nextStart.y && screenY <= nextEnd.y) {
                    colorNext = colorHovered;
                    if (!hoverButton) {
                        SoundController.getInstance().playHover(HOVER_SOUND, HOVER_SOUND, false, hoverVolume);
                        hoverButton = true;
                    }
                }
                else {
                    colorPrev = colorUnhovered;
                    hoverButton = false;
                }
            }
            else if (screenX >= prevStart.x && screenX <= prevEnd.x && page != 0) {
                if (screenY >= prevStart.y && screenY <= prevEnd.y) {
                    colorPrev = colorHovered;
                    if (!hoverButton) {
                        SoundController.getInstance().playHover(HOVER_SOUND, HOVER_SOUND, false, hoverVolume);
                        hoverButton = true;
                    }
                }
                else {
                    colorPrev = colorUnhovered;
                    hoverButton = false;
                }
            }
            else {
                colorPrev = colorUnhovered;
                hoverButton = false;
            }
        }
    }
}