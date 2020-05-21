/*
 * LoadingMode.java
 *
 * Asset loading is a really tricky problem.  If you have a lot of sound or images,
 * it can take a long time to decompress them and load them into memory.  If you just
 * have code at the start to load all your assets, your game will look like it is hung
 * at the start.
 *
 * The alternative is asynchronous asset loading.  In asynchronous loading, you load a
 * little bit of the assets at a time, but still animate the game while you are loading.
 * This way the player knows the game is not hung, even though he or she cannot do 
 * anything until loading is complete. You know those loading screens with the inane tips 
 * that want to be helpful?  That is asynchronous loading.  
 *
 * This player mode provides a basic loading screen.  While you could adapt it for
 * between level loading, it is currently designed for loading all assets at the 
 * start of the game.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.*;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.util.*;

/**
 * Class that provides a loading screen for the state of the game.
 *
 * You still DO NOT need to understand this class for this lab.  We will talk about this
 * class much later in the course.  This class provides a basic template for a loading
 * screen to be used at the start of the game or between levels.  Feel free to adopt
 * this to your needs.
 *
 * You will note that this mode has some textures that are not loaded by the AssetManager.
 * You are never required to load through the AssetManager.  But doing this will block
 * the application.  That is why we try to have as few resources as possible for this
 * loading screen.
 */
public class LoadingMode implements Screen {
	// Textures necessary to support the loading screen
	private static final String WAKING_GOLEM_FILE = "host/wakinggolem.png";
	private static final String LOADING_TEXT_FILE = "shared/loadingspritesheet.png";
	private static final String LOADING_BACKGROUND_FILE = "shared/loading.png";

	// Textures necessary to support the menu screen
	private static final String BACKGROUND_FILE = "shared/menu.png";
	private static final String PROGRESS_FILE = "shared/progressbar.png";
	private static final String PLAY_BTN_FILE = "shared/start.png";
	private static final String LVL_DSGN_FILE = "shared/leveldesign.png";
	private static final String LVL_SLCT_FILE = "shared/levelselect.png";
	private static final String CREDITS_FILE = "shared/credits.png";
	private static final String QUIT_FILE = "shared/quit.png";
	private static final String MUTE_FILE = "shared/mute.png";
	private static final String UNMUTE_FILE = "shared/unmute.png";
	private static final String CLICK_SOUND = "shared/click.mp3";
	private static final String HOVER_SOUND = "shared/hover.mp3";

	// Textures for menu screen animation
	private static final String FLYING_1 = "shared/phoenix/flying_0-27.png";
	private static final String FLYING_2 = "shared/phoenix/flying_28-55.png";
	private static final String FLYING_3 = "shared/phoenix/flying_56-83.png";

	private static final String FLAPPING_1 = "shared/phoenix/flapping_0-27.png";
	private static final String FLAPPING_2 = "shared/phoenix/flapping_28-55.png";
	private static final String FLAPPING_3 = "shared/phoenix/flapping_56-69.png";

	/** Background texture for start-up */
	private Texture background;
	/** Start button to display when done */
	private Texture playButton;
	/** Level Design button to display when done */
	private Texture lvlDesign;
	/** Level Select button to display when done */
	private Texture lvlSelect;
	/** Credits button to display when done */
	private Texture credits;
	/** Quit button to display when done */
	private Texture quit;
	/** Mute button to display when done */
	private Texture mute;
	/** Unmute button to display active */
	private Texture unmute;

	/** Sprite sheet of Golem Waking Up */
	private Texture wakingGolemTexture;
	/** Sprite sheet of Loading Text */
	private Texture loadingTexture;
	/** Blank Loading Background */
	private Texture loadingBackgroundTexture;

	/** Sprite sheet of Loading Text */
	private Texture flyingTexture_1;
	/** Sprite sheet of Loading Text */
	private Texture flyingTexture_2;
	/** Sprite sheet of Loading Text */
	private Texture flyingTexture_3;
	/** Sprite sheet of Loading Text */
	private Texture flappingTexture_1;
	/** Sprite sheet of Loading Text */
	private Texture flappingTexture_2;
	/** Sprite sheet of Loading Text */
	private Texture flappingTexture_3;


	/** Default budget for asset loader (do nothing but load 60 fps) */
	private static int DEFAULT_BUDGET = 15;
	/** Standard window size (for scaling) */
	private static int STANDARD_WIDTH  = 800;
	/** Standard window height (for scaling) */
	private static int STANDARD_HEIGHT = 700;
	/** Standard button-x (for scaling) */
	private static int BUTTON_X  = 175;
	/** Start button-y (for scaling) */
	private static int START_Y = 260;
	/** Level Select button-y (for scaling) */
	private static int LEVEL_SELECT_Y = 200;
	/** Level Design button-y (for scaling) */
	private static int LEVEL_Y = 140;
	/** Credit button-y (for scaling) */
	private static int CREDITS_Y = 90;
	/** Quit button-y (for scaling) */
	private static int QUIT_Y = 525;
	/** Quit button-x (for scaling) */
	private static int QUIT_X = 960;

	/** Quit button-y (for scaling) */
	private static int MUTE_Y = 525;
	/** Quit button-x (for scaling) */
	private static int MUTE_X = 900;

	/** Color of buttons when hovered */
	private static Color colorHovered;
	/** Color of buttons when not hovered */
	private static Color colorUnhovered;

	/** Mouse is currently hovering over a button */
	private boolean hoverButton;
	/** Volume of hover sound */
	private static float hoverVolume = .40f;

	/** Color of start button */
	private Color colorStart;
	/** Color of level design button */
	private Color colorLvlDesign;
	/** Color of level select button */
	private Color colorLvlSelect;
	/** Color of credits button */
	private Color colorCredits;
	/** Color of quit button */
	private Color colorQuit;
	/** Color of mute and unmute button */
	private Color colorMute;


	/** Row length of start animation  */
	private static int WAKING_GOLEM_ROW  = 1;
	/** Column length of start animation */
	private static int WAKING_GOLEM_COLUMN = 8;
	/** Total Size of start animation */
	private static int WAKING_GOLEM_TOTAL = 8;
	/** Row length of start text animation */
	private static int LOADING_TEXT_ROW_COL    = 2;
	/** Column length of start text animation */
	private static int LOADING_TEXT_TOTAL = 4;

	/** Row length of menu animation  */
	private static int PHOENIX_ROW = 7;
	/** Column length of menu  animation */
	private static int PHOENIX_COL = 4;
	/** Size of menu animation */
	private static int PHOENIX_TOTAL = 28;

	/** Offset */
	private static int OFFSET = 128;
	/** 2X Offset */
	private static int OFFSET_2X = 256;

	/** Amount to scale the play button */
	private static float BUTTON_SCALE  = 0.75f;


	/** AssetManager to be loading in the background */
	private AssetManager manager;
	/** Reference to GameCanvas created by the root */
	private GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	private InputController input;

	private MusicController music;

	private SoundController sound;

	/** The y-coordinate of the center of the screen */
	private int centerY;
	/** The x-coordinate of the center of the screen */
	private int centerX;
	/** The height of the canvas window (necessary since sprite origin != screen origin) */
	private int heightY;
	/** Scaling factor for when the student changes the resolution. */
	private float scale;
	
	/** Current progress (0 to 1) of the asset manager */
	private float progress;
	/** Whether the mouse is currently pressed down  */
	private boolean isPressed;
	/** The amount of time to devote to loading assets (as opposed to on screen hints, etc.) */
	private int budget;
	/** Whether or not this player mode is still active */
	private boolean active;

	/** What button is currently pressed or just released this frame, NONE at all other times */
	private pressState buttonPressed;

	private boolean isReady;

	private static enum pressState {
		START,
		SELECT,
		DESIGN,
		CREDITS,
		QUIT,
		MUTE,
		NONE
	}

	/**
	 * Returns the budget for the asset loader.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation 
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to 
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @return the budget in milliseconds
	 */
	public int getBudget() {
		return budget;
	}

	/**
	 * Sets the budget for the asset loader.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation 
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to 
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @param millis the budget in milliseconds
	 */
	public void setBudget(int millis) {
		budget = millis;
	}

	/**
	 * Creates a LoadingMode with the default budget, size and position.
	 *
	 * @param manager The AssetManager to load in the background
	 */
	public LoadingMode(GameCanvas canvas, AssetManager manager) {
		this(canvas, manager,DEFAULT_BUDGET);
	}

	/**
	 * Creates a LoadingMode with the default size and position.
	 *
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to 
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @param manager The AssetManager to load in the background
	 * @param millis The loading budget in milliseconds
	 */
	public LoadingMode(GameCanvas canvas, AssetManager manager, int millis) {
		this.manager = manager;
		this.canvas  = canvas;
		budget = millis;

		// Compute the dimensions from the canvas
		resize(canvas.getWidth(),canvas.getHeight());

		input = InputController.getInstance();
		input.setScreenHeight(canvas.getHeight());
		music = MusicController.getInstance();
		sound = SoundController.getInstance();

		colorHovered = new Color(Color.SKY);
		colorUnhovered = new Color(Color.WHITE);
		colorStart = colorUnhovered;
		colorLvlDesign = colorUnhovered;
		colorLvlSelect = colorUnhovered;
		colorCredits = colorUnhovered;
		colorQuit = colorUnhovered;
		colorMute = colorUnhovered;

		preLoadContent();

		// Load the loading screen textures
		wakingGolemTexture = new Texture(WAKING_GOLEM_FILE);
		loadingTexture = new Texture(LOADING_TEXT_FILE);
		loadingBackgroundTexture = new Texture(LOADING_BACKGROUND_FILE);

		// Load the menu screen textures
		flyingTexture_1 = new Texture(FLYING_1);
		flyingTexture_2 = new Texture(FLYING_2);
		flyingTexture_3 = new Texture(FLYING_3);
		flappingTexture_1 = new Texture(FLAPPING_1);
		flappingTexture_2 = new Texture(FLAPPING_2);
		flappingTexture_3 = new Texture(FLAPPING_3);

		// Load the next two images immediately.
		playButton = null;
		lvlDesign = null;
		lvlSelect = null;
		credits = null;
		quit = null;
		background = new Texture(BACKGROUND_FILE);

		centerY = Gdx.graphics.getHeight()/2;
		centerX = Gdx.graphics.getWidth()/2;

		mute = null;
		unmute = null;
		
		// No progress so far.		
		progress   = 0;
		isPressed = false;
		isReady = false;
		active = false;
		updateFrameLoading = true;
		updateFrameMenu = true;

		buttonPressed = pressState.NONE;

		active = true;

		// For the loading screen animations
		setFilmStripLoading(new FilmStrip(wakingGolemTexture, WAKING_GOLEM_ROW, WAKING_GOLEM_COLUMN, WAKING_GOLEM_TOTAL),
				new FilmStrip(loadingTexture, LOADING_TEXT_ROW_COL, LOADING_TEXT_ROW_COL, LOADING_TEXT_TOTAL));


		// For the main menu screen animations
		setFilmStripMenu(
				new FilmStrip(flyingTexture_1, PHOENIX_ROW, PHOENIX_COL, PHOENIX_TOTAL),
				new FilmStrip(flyingTexture_2, PHOENIX_ROW, PHOENIX_COL, PHOENIX_TOTAL),
				new FilmStrip(flyingTexture_3, PHOENIX_ROW, PHOENIX_COL, PHOENIX_TOTAL),
				new FilmStrip(flappingTexture_1, PHOENIX_ROW, PHOENIX_COL, PHOENIX_TOTAL),
				new FilmStrip(flappingTexture_2, PHOENIX_ROW, PHOENIX_COL, PHOENIX_TOTAL),
				new FilmStrip(flappingTexture_3, 4, PHOENIX_COL, 14)
		);
	}

	/**
	 * Preloads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 *
	 */
	public void preLoadContent() {
		manager.load(CLICK_SOUND, Sound.class);
		manager.load(HOVER_SOUND, Sound.class);
		if(music.isEmpty()){
			music.addMusic("menuMusic", "shared/menumusic.wav");
			music.addMusic("gameMusic", "shared/gameplaymusic.mp3");
		}
	}


	/**
	 * Loads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 *
	 */
	public void loadContent() {
		sound.allocate(manager, CLICK_SOUND);
		sound.allocate(manager, HOVER_SOUND);
		music.play("menuMusic");
	}
	
	/**
	 * Called when this screen should release all resources.
	 */
	public void dispose() {
		 loadingTexture.dispose();
		 wakingGolemTexture.dispose();
		 loadingBackgroundTexture.dispose();

		 flyingTexture_1.dispose();
		 flyingTexture_2.dispose();
		 flyingTexture_3.dispose();

		 flappingTexture_1.dispose();
		 flappingTexture_2.dispose();
		 flappingTexture_3.dispose();
	}

//	public void setCanvas(GameCanvas canvas) {
//		this.canvas = canvas;
//		resize(canvas.getWidth(), canvas.getHeight());
//	}

	/**
	 * Update the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void update(float delta) {
		if (input.didPressLeft()){
			System.out.println("LEFT");
		}
		if (input.didPressRight()) {
			System.out.println("RIGHT");
		}
		if (playButton == null) {
			manager.update(budget);
			this.progress = manager.getProgress();
			if (progress >= 1.0f) {
				this.progress = 1.0f;
				playButton = new Texture(PLAY_BTN_FILE);
				playButton.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				lvlDesign = new Texture(LVL_DSGN_FILE);
				lvlDesign.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				lvlSelect = new Texture(LVL_SLCT_FILE);
				lvlSelect.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				credits = new Texture(CREDITS_FILE);
				credits.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				quit = new Texture(QUIT_FILE);
				quit.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				mute = new Texture(MUTE_FILE);
				mute.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				unmute = new Texture(UNMUTE_FILE);
				unmute.setFilter(TextureFilter.Linear, TextureFilter.Linear);

				loadContent();
			}
		}
		else {
			music.update();

			input.readInput();
			hoverVolume = .40f*sound.getVolume();


			float screenY = input.getMousePosition().y;
			float screenX = input.getMousePosition().x;
			if (input.didLeftClick()) {
				updatePressed(screenX, screenY);
			}
			else if (input.didRelease()) {

				updateReleased(screenX, screenY);
			}
			updateHover(screenX, screenY);
		}
		// Update sounds
		sound.update();
	}

	/**
	 * Draw the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 */
	private void draw() {
		canvas.begin();

		if (playButton == null) {
			canvas.draw(loadingBackgroundTexture, 0,0);
			updateLoadingAnimation();
			drawProgress(canvas);
		}
		else {
			canvas.draw(background, 0, 0);
			updateMenuAnimation();
			drawMenuAnimation(canvas);

			canvas.draw(playButton, buttonPressed == pressState.START && isPressed ? Color.SKY : colorStart, 0, 0,
						BUTTON_X, START_Y, 0, BUTTON_SCALE*scale, BUTTON_SCALE*scale);

			canvas.draw(lvlSelect, buttonPressed == pressState.SELECT && isPressed ? Color.SKY : colorLvlSelect, 0, 0,
					BUTTON_X, LEVEL_SELECT_Y, 0, BUTTON_SCALE*scale, BUTTON_SCALE*scale);

			canvas.draw(lvlDesign, buttonPressed == pressState.DESIGN && isPressed ? Color.SKY : colorLvlDesign, 0, 0,
					BUTTON_X, LEVEL_Y, 0, BUTTON_SCALE*scale, BUTTON_SCALE*scale);

			canvas.draw(credits, buttonPressed == pressState.CREDITS && isPressed ? Color.SKY : colorCredits, 0, 0,
					BUTTON_X, CREDITS_Y, 0, BUTTON_SCALE*scale, BUTTON_SCALE*scale);

			canvas.draw(quit, buttonPressed == pressState.QUIT && isPressed ? Color.SKY : colorQuit, 0, 0,
					QUIT_X, QUIT_Y, 0, BUTTON_SCALE*scale, BUTTON_SCALE*scale);

			//System.out.println(sound.isUnmuted());
			//System.out.println(music.isUnmuted());
			if (sound.isUnmuted() && music.isUnmuted()) {
				canvas.draw(unmute, buttonPressed == pressState.MUTE && isPressed ? Color.SKY : colorMute, 0,0, MUTE_X, MUTE_Y, 0, BUTTON_SCALE*scale, BUTTON_SCALE*scale);
			}
			else {
				canvas.draw(mute, buttonPressed == pressState.MUTE && isPressed ? Color.SKY : colorMute, 0,0, MUTE_X, MUTE_Y, 0, BUTTON_SCALE*scale, BUTTON_SCALE*scale);
			}
		}
		canvas.end();
	}

	/** The current filmstrip to draw */
	protected FilmStrip drawStrip;

	/** The texture filmstrip for the spirit body */
	protected FilmStrip  golemWakingStrip;
	/** The texture filmstrip for the spirit body */
	protected FilmStrip  loadingTextStrip;

	/** The texture filmstrip for the phoenix spirit flying */
	protected FilmStrip  flyingPhoenixStrip_1;
	/** The texture filmstrip for the phoenix spirit flying */
	protected FilmStrip  flyingPhoenixStrip_2;
	/** The texture filmstrip for the phoenix spirit flying */
	protected FilmStrip  flyingPhoenixStrip_3;
	/** The texture filmstrip for the phoenix spirit flapping */
	protected FilmStrip  flappingPhoenixStrip_1;
	/** The texture filmstrip for the phoenix spirit flapping */
	protected FilmStrip  flappingPhoenixStrip_2;
	/** The texture filmstrip for the phoenix spirit flapping */
	protected FilmStrip  flappingPhoenixStrip_3;

	/** Which number of film strip are you on */
	private int onFilmStrip = 0;
	/** The number of frames that have elapsed since the last animation update */
	private int elapsedFramesMenu = 0;
	/** The number of frames that should pass before the animation updates */
	private int framesPerUpdateMenu = 2;
	/** Whether or not animation should be updated on this frame */
	private boolean updateFrameMenu;

	/** The number of frames that have elapsed since the last animation update */
	private int elapsedFramesLoading = 0;
	/** The number of frames that should pass before the animation updates */
	private int framesPerUpdateLoading = 16;
	/** Whether or not animation should be updated on this frame */
	private boolean updateFrameLoading;

	private static final int FRAME_START = 0;

	/**
	 * sets all the film strips of the spirit
	 * @param golemWakingUp filmstrip for the spirit's body
	 *
	 */
	public void setFilmStripLoading(FilmStrip golemWakingUp,
							  FilmStrip loadingTextStrip) {

		this.golemWakingStrip = golemWakingUp;
		this.golemWakingStrip.setFrame(FRAME_START);

		this.loadingTextStrip = loadingTextStrip;
		this.loadingTextStrip.setFrame(FRAME_START);
	}

	/**
	 * sets all the film strips of the spirit
	 * @param flyingPhoenixStrip_1 for the spirit's phoenix
	 * @param flyingPhoenixStrip_2 for the spirit's phoenix
	 * @param flyingPhoenixStrip_3 for the spirit's phoenix
	 * @param flappingPhoenixStrip_1 for the spirit's phoenix
	 * @param flappingPhoenixStrip_2 for the spirit's phoenix
	 * @param flappingPhoenixStrip_3 for the spirit's phoenix
	 *
	 */
	public void setFilmStripMenu (
							FilmStrip flyingPhoenixStrip_1,
							FilmStrip flyingPhoenixStrip_2,
							FilmStrip flyingPhoenixStrip_3,
							FilmStrip flappingPhoenixStrip_1,
							FilmStrip flappingPhoenixStrip_2,
							FilmStrip flappingPhoenixStrip_3
							  ) {

		this.flyingPhoenixStrip_1 = flyingPhoenixStrip_1;
		this.flyingPhoenixStrip_2 = flyingPhoenixStrip_2;
		this.flyingPhoenixStrip_3 = flyingPhoenixStrip_3;

		this.flyingPhoenixStrip_1.setFrame(FRAME_START);
		this.flyingPhoenixStrip_2.setFrame(FRAME_START);
		this.flyingPhoenixStrip_3.setFrame(FRAME_START);

		this.flappingPhoenixStrip_1 = flappingPhoenixStrip_1;
		this.flappingPhoenixStrip_2 = flappingPhoenixStrip_2;
		this.flappingPhoenixStrip_3 = flappingPhoenixStrip_3;

		this.flappingPhoenixStrip_1.setFrame(FRAME_START);
		this.flappingPhoenixStrip_2.setFrame(FRAME_START);
		this.flappingPhoenixStrip_3.setFrame(FRAME_START);
	}

	public void updateMenuAnimation () {
		elapsedFramesMenu++;

		if (elapsedFramesMenu >= framesPerUpdateMenu) {
			updateFrameMenu = true;
			elapsedFramesMenu = 0;
		}
		if (updateFrameMenu) {
			if (this.flyingPhoenixStrip_1.getFrame() < this.flyingPhoenixStrip_1.getSize() - 1) {
				this.flyingPhoenixStrip_1.setFrame(this.flyingPhoenixStrip_1.getFrame() + 1);
					//System.out.println("UPDATED STRIP 1: " + this.flyingPhoenixStrip_1.getFrame());
					onFilmStrip = 1; }
			else if ((this.flyingPhoenixStrip_1.getFrame() >= this.flyingPhoenixStrip_1.getSize() - 1)
					&& this.flyingPhoenixStrip_2.getFrame() < this.flyingPhoenixStrip_2.getSize() - 1) {
						this.flyingPhoenixStrip_2.setFrame(this.flyingPhoenixStrip_2.getFrame() + 1);
					//System.out.println("UPDATED STRIP 2: " + this.flyingPhoenixStrip_2.getFrame());
					onFilmStrip = 2; }
			else if ((this.flyingPhoenixStrip_2.getFrame() >= this.flyingPhoenixStrip_2.getSize() - 1)
					&& this.flyingPhoenixStrip_3.getFrame() < this.flyingPhoenixStrip_3.getSize() - 1) {
					this.flyingPhoenixStrip_3.setFrame(this.flyingPhoenixStrip_3.getFrame() + 1);
					//System.out.println("UPDATED STRIP 3: " + this.flyingPhoenixStrip_3.getFrame());
					onFilmStrip = 3; }
			else if ((this.flyingPhoenixStrip_3.getFrame() >= this.flyingPhoenixStrip_3.getSize() - 1)
					&& this.flappingPhoenixStrip_1.getFrame() < this.flappingPhoenixStrip_1.getSize() - 1) {
					this.flappingPhoenixStrip_1.setFrame(this.flappingPhoenixStrip_1.getFrame() + 1);
					//System.out.println("UPDATED STRIP 4: " + this.flappingPhoenixStrip_1.getFrame());
					onFilmStrip = 4; }
			else if ((this.flappingPhoenixStrip_1.getFrame() >= this.flappingPhoenixStrip_1.getSize() - 1)
					&& this.flappingPhoenixStrip_2.getFrame() < this.flappingPhoenixStrip_2.getSize() - 1) {
					this.flappingPhoenixStrip_2.setFrame(this.flappingPhoenixStrip_2.getFrame() + 1);
					//System.out.println("UPDATED STRIP 5: " + this.flappingPhoenixStrip_2.getFrame());
					onFilmStrip = 5; }
			else if ((this.flappingPhoenixStrip_2.getFrame() >= this.flappingPhoenixStrip_2.getSize() - 1)
					&& this.flappingPhoenixStrip_3.getFrame() < this.flappingPhoenixStrip_3.getSize() - 1) {
					this.flappingPhoenixStrip_3.setFrame(this.flappingPhoenixStrip_3.getFrame() + 1);
					//System.out.println("UPDATED STRIP 6: " + this.flappingPhoenixStrip_3.getFrame());
					onFilmStrip = 6; }
			else {
				this.flappingPhoenixStrip_1.setFrame(FRAME_START);
				this.flappingPhoenixStrip_2.setFrame(FRAME_START);
				this.flappingPhoenixStrip_3.setFrame(FRAME_START);
				onFilmStrip = 4;
			}
			updateFrameMenu = false;
		}
	}

	/**
	 * Called when we leave the screen
	 * Will update the animations to reset
	 */
	public void resetAnimation() {
		// Compute the drawing scale
		onFilmStrip = 3;
		this.flappingPhoenixStrip_1.setFrame(FRAME_START);
		this.flappingPhoenixStrip_2.setFrame(FRAME_START);
		this.flappingPhoenixStrip_3.setFrame(FRAME_START);

		elapsedFramesMenu = 0;
		updateFrameMenu = false;
	}

	public void updateLoadingAnimation () {
		elapsedFramesLoading++;

		if (elapsedFramesLoading >= framesPerUpdateLoading) {
			updateFrameLoading = true;
			elapsedFramesLoading = 0;
		}
		if (updateFrameLoading) {
			if ((this.golemWakingStrip.getFrame() < this.golemWakingStrip.getSize() - 1)) {
				this.golemWakingStrip.setFrame(this.golemWakingStrip.getFrame() + 1); }
			else {
				this.golemWakingStrip.setFrame(4);
			}

			if ((this.loadingTextStrip.getFrame() < this.loadingTextStrip.getSize() - 1)) {
				this.loadingTextStrip.setFrame(this.loadingTextStrip.getFrame() + 1);
			} else {
				this.loadingTextStrip.setFrame(FRAME_START);
			}
			updateFrameLoading = false;
		}
	}


	/**
	 * Updates the progress bar according to loading progress
	 *
	 * The progress bar is composed of parts: two rounded caps on the end, 
	 * and a rectangle in a middle.  We adjust the size of the rectangle in
	 * the middle to represent the amount of progress.
	 *
	 * @param canvas The drawing context
	 */	
	private void drawProgress(GameCanvas canvas) {
		if (progress > 0) {
			canvas.draw(golemWakingStrip, Color.WHITE, centerX - OFFSET, centerY - OFFSET, OFFSET_2X, OFFSET_2X);
			canvas.draw(loadingTextStrip, Color.WHITE, centerX - OFFSET, centerY - OFFSET_2X, OFFSET_2X, OFFSET_2X);
		}
	}

	/**
	 * Updates the main menu animation
	 *
	 * @param canvas The drawing context
	 */
	private void drawMenuAnimation(GameCanvas canvas) {
		if (progress > 0 ) {
			switch (onFilmStrip) {
				case 1:
					drawStrip = flyingPhoenixStrip_1;
					break;
				case 2:
					drawStrip = flyingPhoenixStrip_2;
					break;
				case 3:
					drawStrip = flyingPhoenixStrip_3;
					break;
				case 4:
					drawStrip = flappingPhoenixStrip_1;
					break;
				case 5:
					drawStrip = flappingPhoenixStrip_2;
					break;
				case 6:
					drawStrip = flappingPhoenixStrip_3;
					break;
			}
			if (drawStrip != null) {
				//System.out.println("DRAWING: " + onFilmStrip);
				canvas.draw(drawStrip, Color.WHITE, 0, 0, 1024, 576);
			}

		}
	}

	// ADDITIONAL SCREEN METHODS
	/**
	 * Called when the Screen should render itself.
	 *
	 * We defer to the other methods update() and draw().  However, it is VERY important
	 * that we only quit AFTER a draw.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void render(float delta) {
		if (active) {
			update(delta);
			draw();

			// We are are ready, notify our listener
			if (isReady && buttonPressed == pressState.START && listener != null) {
				buttonPressed = pressState.NONE;
				resetAnimation();
				listener.exitScreen(this, WorldController.EXIT_PLAY);
			}

			// Go to level design mode
			if (isReady && buttonPressed == pressState.DESIGN && listener != null) {
				buttonPressed = pressState.NONE;
				resetAnimation();
				listener.exitScreen(this,WorldController.EXIT_DESIGN);
			}

			// Go to level select mode
			if(isReady && buttonPressed == pressState.SELECT && listener != null) {
				buttonPressed = pressState.NONE;
				resetAnimation();
				listener.exitScreen(this,WorldController.EXIT_SELECT);
			}

			// Go to credits mode
			if (isReady && buttonPressed == pressState.CREDITS && listener != null) {
				buttonPressed = pressState.NONE;
				resetAnimation();
				listener.exitScreen(this, WorldController.EXIT_CREDITS);
			}

			// Close game
			if(isReady && buttonPressed == pressState.QUIT && listener != null) {
				buttonPressed = pressState.NONE;
				listener.exitScreen(this,WorldController.EXIT_QUIT);
			}
		}
	}

	/**
	 * Called when the Screen is resized. 
	 *
	 * This can happen at any point during a non-paused state but will never happen 
	 * before a call to show().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		// Compute the drawing scale
		float sx = ((float)width)/STANDARD_WIDTH;
		float sy = ((float)height)/STANDARD_HEIGHT;
		scale = Math.min(sx, sy);

		heightY = height;
	}

	/**
	 * Called when the Screen is paused.
	 * 
	 * This is usually when it's not active or visible on screen. An Application is 
	 * also paused before it is destroyed.
	 */
	public void pause() {
		// TODO Auto-generated method stub

	}

	/**
	 * Called when the Screen is resumed from a paused state.
	 *
	 * This is usually when it regains focus.
	 */
	public void resume() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	public void show() {
		// Useless if called in outside animation loop
		active = true;
	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	public void hide() {
		// Useless if called in outside animation loop
		active = false;
	}
	
	/**
	 * Sets the ScreenListener for this mode
	 *
	 * The ScreenListener will respond to requests to quit.
	 */
	public void setScreenListener(ScreenListener listener) {
		this.listener = listener;
	}
	
	// PROCESSING PLAYER INPUT
	/** 
	 * Called when the screen was touched or a mouse button was pressed.
	 *
	 * This method checks to see if the play button is available and if the click
	 * is in the bounds of the play button.  If so, it signals the that the button
	 * has been pressed and is currently down. Any mouse button is accepted.
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 */
	public void updatePressed(float screenX, float screenY) {

		if(screenX >= BUTTON_X && screenX <= BUTTON_X + (playButton.getWidth()*scale*BUTTON_SCALE) ) {
			if (screenY >= START_Y && screenY <= START_Y + (playButton.getHeight()*scale*BUTTON_SCALE) ) {
				buttonPressed = pressState.START;
				sound.play(CLICK_SOUND, CLICK_SOUND, false, hoverVolume);
				isPressed = true;
			}
		}

		if(screenX >= BUTTON_X && screenX <= BUTTON_X + (lvlSelect.getWidth()*scale*BUTTON_SCALE) ) {
			if (screenY >= LEVEL_SELECT_Y && screenY <= LEVEL_SELECT_Y + (lvlSelect.getHeight()*scale*BUTTON_SCALE) ) {
				buttonPressed = pressState.SELECT;
				sound.play(CLICK_SOUND, CLICK_SOUND, false, hoverVolume);
				isPressed = true;
			}
		}

		if(screenX >= BUTTON_X && screenX <= BUTTON_X + (lvlDesign.getWidth()*scale*BUTTON_SCALE) ) {
			if (screenY >= LEVEL_Y && screenY <= LEVEL_Y + (lvlDesign.getHeight()*scale*BUTTON_SCALE) ) {
				buttonPressed = pressState.DESIGN;
				sound.play(CLICK_SOUND, CLICK_SOUND, false, hoverVolume);
				isPressed = true;
			}
		}

		if (screenY >= CREDITS_Y && screenY <= CREDITS_Y + (credits.getHeight()*scale*BUTTON_SCALE)) {
			if (screenX >= BUTTON_X && screenX <= BUTTON_X + (credits.getWidth() * scale * BUTTON_SCALE)) {
				buttonPressed = pressState.CREDITS;
				sound.play(CLICK_SOUND, CLICK_SOUND, false, hoverVolume);
				isPressed = true;
			}
		}

		if(screenX >= QUIT_X && screenX <= QUIT_X + (quit.getWidth()*scale*BUTTON_SCALE) ) {
			if (screenY >= QUIT_Y && screenY <= QUIT_Y + (quit.getHeight()*scale*BUTTON_SCALE) ) {
				buttonPressed = pressState.QUIT;
				sound.play(CLICK_SOUND, CLICK_SOUND, false, hoverVolume);
				isPressed = true;
			}
		}

		if (screenX >= MUTE_X && screenX <= MUTE_X + (mute.getWidth()*scale*BUTTON_SCALE) ) {
			if (screenY >= MUTE_Y && screenY <= MUTE_Y + (mute.getHeight()*scale*BUTTON_SCALE) ) {
				isPressed = true;
				buttonPressed = pressState.MUTE;
			}
		}

	}
	
	/** 
	 * Called when a finger was lifted or a mouse button was released.
	 *
	 * This method checks to see if the play button is currently pressed down. If so, 
	 * it signals the that the player is ready to go.
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 */	
	public void updateReleased(float screenX, float screenY) {
		//System.out.println("released!");
		if (isPressed) {
			if(screenX >= BUTTON_X && screenX <= BUTTON_X + (playButton.getWidth()*scale*BUTTON_SCALE) ) {
				if (screenY >= START_Y && screenY <= START_Y + (playButton.getHeight()*scale*BUTTON_SCALE) ) {
					if (buttonPressed == pressState.START) { isReady = true; }
					else {
						isReady = false;
						buttonPressed = pressState.NONE;
					}
				}
			}

			if(screenX >= BUTTON_X && screenX <= BUTTON_X + (lvlSelect.getWidth()*scale*BUTTON_SCALE) ) {
				if (screenY >= LEVEL_SELECT_Y && screenY <= LEVEL_SELECT_Y + (lvlSelect.getHeight()*scale*BUTTON_SCALE) ) {
					if (buttonPressed == pressState.SELECT) { isReady = true; }
					else {
						isReady = false;
						buttonPressed = pressState.NONE;
					}
				}
			}

			if(screenX >= BUTTON_X && screenX <= BUTTON_X + (lvlDesign.getWidth()*scale*BUTTON_SCALE) ) {
				if (screenY >= LEVEL_Y && screenY <= LEVEL_Y + (lvlDesign.getHeight()*scale*BUTTON_SCALE) ) {
					if (buttonPressed == pressState.DESIGN) { isReady = true; }
					else {
						isReady = false;
						buttonPressed = pressState.NONE;
					}
				}
			}

			if (screenY >= CREDITS_Y && screenY <= CREDITS_Y + (credits.getHeight()*scale*BUTTON_SCALE)) {
				if (screenX >= BUTTON_X && screenX <= BUTTON_X + (credits.getWidth() * scale * BUTTON_SCALE)) {
					if (buttonPressed == pressState.CREDITS) { isReady = true; }
					else {
						isReady = false;
						buttonPressed = pressState.NONE;
					}
				}
			}

			if(screenX >= QUIT_X && screenX <= QUIT_X + (quit.getWidth()*scale*BUTTON_SCALE) ) {
				if (screenY >= QUIT_Y && screenY <= QUIT_Y + (quit.getHeight()*scale*BUTTON_SCALE) ) {
					if (buttonPressed == pressState.QUIT) { isReady = true; }
					else {
						isReady = false;
						buttonPressed = pressState.NONE;
					}
				}
			}

			if (screenX >= MUTE_X && screenX <= MUTE_X + (mute.getWidth()*scale*BUTTON_SCALE) ) {
				if (screenY >= MUTE_Y && screenY <= MUTE_Y + (mute.getHeight()*scale*BUTTON_SCALE) ) {
					if (buttonPressed == pressState.QUIT) { isReady = true; }
					else {
						isReady = false;
						buttonPressed = pressState.NONE;
					}
					System.out.println("music unmuted is:" + !music.isUnmuted());
					music.setUnmuted(!music.isUnmuted());
					sound.setUnmuted(!sound.isUnmuted());
				}
			}
		}
		colorStart = colorUnhovered;
		colorLvlDesign = colorUnhovered;
		colorLvlSelect = colorUnhovered;
		colorCredits = colorUnhovered;
		colorQuit = colorUnhovered;
		colorMute = colorUnhovered;

		hoverButton = false;

		isPressed = false;
	}

	

	
	/** 
	 * Called when the mouse was moved without any buttons being pressed. (UNSUPPORTED)
	 *
	 * @param screenX the x-coordinate of the mouse on the screen
	 * @param screenY the y-coordinate of the mouse on the screen
	 *
	 */	
	public void updateHover(float screenX, float screenY) {
		if (active && playButton != null){
			if (screenY >= START_Y && screenY <= START_Y + (playButton.getHeight()*scale*BUTTON_SCALE)) {
				if (screenX >= BUTTON_X && screenX <= BUTTON_X + (playButton.getWidth()*scale*BUTTON_SCALE)) {
					colorStart = colorHovered;
					if (!hoverButton) {
						sound.play(HOVER_SOUND, HOVER_SOUND, false, hoverVolume);
						hoverButton = true;
					}
				}
				else {
					colorStart = colorUnhovered;
					hoverButton = false;
				}
			}

			else if (screenY >= LEVEL_SELECT_Y && screenY <= LEVEL_SELECT_Y + (lvlSelect.getHeight()*scale*BUTTON_SCALE)) {
				if (screenX >= BUTTON_X && screenX <= BUTTON_X+(lvlSelect.getWidth()*scale*BUTTON_SCALE)) {
					colorLvlSelect = colorHovered;
					if (!hoverButton) {
						sound.play(HOVER_SOUND, HOVER_SOUND, false, hoverVolume);
						hoverButton = true;
					}
				}
				else {
					colorLvlSelect = colorUnhovered;
					hoverButton = false;
				}
			}

			else if(screenY >= LEVEL_Y && screenY <= LEVEL_Y + (lvlDesign.getHeight()*scale*BUTTON_SCALE)) {
				if (screenX >= BUTTON_X && screenX <= BUTTON_X + (lvlDesign.getWidth()*scale*BUTTON_SCALE)) {
					colorLvlDesign = colorHovered;
					if (!hoverButton) {
						sound.play(HOVER_SOUND, HOVER_SOUND, false, hoverVolume);
						hoverButton = true;
					}
				}
				else {
					colorLvlDesign = colorUnhovered;
					hoverButton = false;
				}
			}

			else if (screenY >= CREDITS_Y && screenY <= CREDITS_Y + (credits.getHeight()*scale*BUTTON_SCALE)) {
				if (screenX >= BUTTON_X && screenX <= BUTTON_X + (credits.getWidth()*scale*BUTTON_SCALE)) {
					colorCredits = colorHovered;
					if (!hoverButton) {
						sound.play(HOVER_SOUND, HOVER_SOUND, false, hoverVolume);
						hoverButton = true;
					}
				}
				else {
					colorCredits = colorUnhovered;
					hoverButton = false;
				}
			}
			else if (screenY >= QUIT_Y && screenY <= QUIT_Y + (quit.getHeight()*scale*BUTTON_SCALE)) {
				if (screenX >= QUIT_X && screenX <= QUIT_X + (quit.getWidth()*scale*BUTTON_SCALE)) {
					colorQuit = colorHovered;
					if (!hoverButton) {
						sound.play(HOVER_SOUND, HOVER_SOUND, false, hoverVolume);
						hoverButton = true;
					}
				}
				else if (screenX >= MUTE_X && screenX <= MUTE_X + (mute.getWidth()*scale*BUTTON_SCALE)) {
					colorMute = colorHovered;
					if (!hoverButton) {
						sound.play(HOVER_SOUND, HOVER_SOUND, false, hoverVolume);
						hoverButton = true;
					}
				}

				else {
					colorQuit = colorUnhovered;
					colorMute = colorUnhovered;
					hoverButton = false;
				}
			}

			else {
				colorStart = colorUnhovered;
				colorLvlDesign = colorUnhovered;
				colorLvlSelect = colorUnhovered;
				colorCredits = colorUnhovered;
				colorQuit = colorUnhovered;
				colorMute = colorUnhovered;
				hoverButton = false;
			}
		}
	}
}