/*
 * GDXRoot.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX.  In the first lab, we extended ApplicationAdapter.  In previous lab
 * we extended Game.  This is because of a weird graphical artifact that we do not
 * understand.  Transparencies (in 3D only) is failing when we use ApplicationAdapter. 
 * There must be some undocumented OpenGL code in setScreen.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
 package edu.cornell.gdiac.physics;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import edu.cornell.gdiac.util.ScreenListener;


/**
 * Root class for a LibGDX.  
 * 
 * This class is technically not the ROOT CLASS. Each platform has another class above
 * this (e.g. PC games use DesktopLauncher) which serves as the true root.  However, 
 * those classes are unique to each platform, while this class is the same across all 
 * plaforms. In addition, this functions as the root class all intents and purposes, 
 * and you would draw it as a root class in an architecture specification.  
 */
public class GDXRoot extends Game implements ScreenListener {
	/** AssetManager to load game assets (textures, sounds, etc.) */
	private AssetManager manager;

	/** AssetManager to load game assets (textures, sounds, etc.) */
	private AssetManager managerLoad;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas; 
	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;
	/** Main game controller */
	private GamePlayController controller;
	/** Level designer controller */
	private LevelDesignerMode levelDesigner;
	/** Level selection screen controller */
	private LevelSelectMode levelSelect;
	/** Credits screen controller */
	private Credits credits;



	/** Stores whether to exit to level designer after a level is selected */
	private boolean goLevelDesigner = false;

	private GameOver gameOver;
	
	/**
	 * Creates a new game from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets
	 * or assign any screen.
	 */
	public GDXRoot() {
		// Start loading with the asset manager
		manager = new AssetManager();
		managerLoad = new AssetManager();
		
		// Add font support to the asset manager
		FileHandleResolver resolver = new InternalFileHandleResolver();
		manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
	}

	/** 
	 * Called when the Application is first created.
	 * 
	 * This is method immediately loads assets for the loading screen, and prepares
	 * the asynchronous loader for all other assets.
	 */
	public void create() {
		canvas  = new GameCanvas();
		loading = new LoadingMode(canvas,manager,1);

		controller = new GamePlayController();
		levelDesigner = new LevelDesignerMode();
		levelSelect = new LevelSelectMode();
		gameOver = new GameOver();
		credits = new Credits();

		controller.preLoadContent(manager);
		levelDesigner.preLoadContent(manager);
		levelSelect.preLoadContent(manager);
		gameOver.preLoadContent(manager);

		loading.setScreenListener(this);
		setScreen(loading);
	}

	/** 
	 * Called when the Application is destroyed. 
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		setScreen(null);

		controller.unloadContent(manager);
		loading.dispose();
		controller.dispose();
		levelSelect.dispose();
		levelDesigner.dispose();

		canvas.dispose();
		canvas = null;

		// Unload all of the resources
		manager.clear();
		manager.dispose();
		super.dispose();
	}
	
	/**
	 * Called when the Application is resized. 
	 *
	 * This can happen at any point during a non-paused state but will never happen 
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width,height);
	}

	private void reset() {

		canvas  = new GameCanvas();
		loading = new LoadingMode(canvas,manager,1);

		loading.setScreenListener(this);

		setScreen(loading);

	}



	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		// Going to gameplay mode from main menu through start
		if (screen == loading && exitCode == WorldController.EXIT_PLAY) {
			goLevelDesigner = false;

			controller.loadContent(manager);
			controller.setScreenListener(this);
			controller.setCanvas(canvas);
			controller.reset();
			//loading.dispose();
			setScreen(controller);
		}

		// Going to designer mode from main menu
		else if (screen == loading && exitCode == WorldController.EXIT_DESIGN) {
			goLevelDesigner = true;
			levelSelect.goToDesigner = true;
			levelSelect.loadContent(manager);
			levelSelect.setScreenListener(this);
			levelSelect.setCanvas(canvas);
			levelSelect.reset();
			setScreen(levelSelect);
		}
		// Going to level select mode from main menu
		else if (screen == loading && exitCode == WorldController.EXIT_SELECT) {
			goLevelDesigner = false;
			levelSelect.goToDesigner = false;
			levelSelect.loadContent(manager);
			levelSelect.setScreenListener(this);
			levelSelect.setCanvas(canvas);
			levelSelect.reset();
			setScreen(levelSelect);
		}

		else if (exitCode == WorldController.EXIT_GAME) {
			goLevelDesigner = false;
			gameOver.loadContent(manager);
			gameOver.setScreenListener(this);
			gameOver.setCanvas(canvas);
			gameOver.reset();
			setScreen(gameOver);
		}
		else if (exitCode == WorldController.EXIT_NEXT) {
			goLevelDesigner = false;
			controller.reset();
			//loading.dispose();
			setScreen(controller);
		}
		else if (exitCode == WorldController.EXIT_PREV) {
			goLevelDesigner = false;
			controller.reset();
			//loading.dispose();
			setScreen(controller);
		}

		else if (exitCode == WorldController.EXIT_QUIT) {
			// We quit the main application
			loading.dispose();
			Gdx.app.exit();
		}
		else if (exitCode == WorldController.EXIT_MENU) {
			goLevelDesigner = false;
			reset();
		}
		else if (exitCode == WorldController.EXIT_CREDITS) {
			goLevelDesigner = false;
			credits.setScreenListener(this);
			setScreen(credits);
		}
	}


	/**
	 * The given screen has made a request to exit its player mode
	 * and enter the game mode on a specific level. ONLY CALLED FROM GAME OVER
	 *
	 * @param level The level to start the game at
	 */
	public void exitScreenLevel(int level) {
		controller.loadContent(manager);
		controller.setScreenListener(this);
		controller.setCanvas(canvas);
		controller.setCurrentLevel(level);
		controller.reset();

		setScreen(controller);

	}

	/**
	 * The given screen has made a request to exit its player mode
	 * and enter the game mode on a specific level.
	 *
	 * @param level The level to start the game at
	 */
	public void exitScreenLevel(int level, int page) {

		if(goLevelDesigner) {
			levelDesigner.loadContent(manager);
			levelDesigner.setScreenListener(this);
			levelDesigner.setCanvas(canvas);
			levelDesigner.setCurrentLevel(level + (page*4));
			if(level == -1){
				levelDesigner.setLoadBoard(false);
				System.out.println("TEST");
			}
			levelDesigner.reset();

			setScreen(levelDesigner);
		}
		else {
			exitScreenLevel(level + (page*4));
		}

	}

}
