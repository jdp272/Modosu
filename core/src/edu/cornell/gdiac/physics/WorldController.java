/*
 * WorldController.java
 *
 * This is the most important new class in this lab.  This class serves as a combination 
 * of the CollisionController and GameplayController from the previous lab.  There is not 
 * much to do for collisions; Box2d takes care of all of that for us.  This controller 
 * invokes Box2d and then performs any after the fact modifications to the data 
 * (e.g. gameplay).
 *
 * If you study this class, and the contents of the edu.cornell.cs3152.physics.obstacles
 * package, you should be able to understand how the Physics engine works.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import edu.cornell.gdiac.physics.host.ArrowModel;
import edu.cornell.gdiac.physics.host.FootPrintModel;
import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.obstacle.BorderEdge;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.obstacle.Wall;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.util.MusicController;
import edu.cornell.gdiac.util.PooledList;
import edu.cornell.gdiac.util.ScreenListener;

import java.io.File;
import java.util.*;

/**
 * Base class for a world-specific controller.
 *
 *
 * A world has its own objects, assets, and input controller.  Thus this is 
 * really a mini-GameEngine in its own right.  The only thing that it does
 * not do is create a GameCanvas; that is shared with the main application.
 *
 * You will notice that asset loading is not done with static methods this time.  
 * Instance asset loading makes it easier to process our game modes in a loop, which 
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public abstract class WorldController implements Screen {
	/** The number of levels */
	protected static final int NUM_LEVELS = 24; //////////////// CHANGE DEPENDING ON AMOUNT OF LEVELS ///////////////

	/**
	 * Tracks the asset state.  Otherwise subclasses will try to load assets 
	 */
	protected enum AssetState {
		/** No assets loaded */
		EMPTY,
		/** Still loading assets */
		LOADING,
		/** Assets are complete */
		COMPLETE
	}

	/** Track asset loading from all instances and subclasses */
	protected AssetState worldAssetState = AssetState.EMPTY;
	/** Track all loaded assets (for unloading purposes) */
	protected Array<String> assets;

	/** A factory class for easily creating game objects. */
	protected Factory factory;
	/** A loader class for loading level files. */
	protected Loader loader;
	
	// Pathnames to shared assets
	/** Retro font for displaying messages */

	private static String FONT_FILE = "shared/AveriaSerifLibre.ttf";
	/** Texture file for background image */
	private static final String BACKG_FILE = "shared/background.png";
	/** Texture file for host sprite EAST */
	private static final String HOST_FILE_E = "host/golemWalk_E.png";
	/** Texture file for host sprite NORTH */
	private static final String HOST_FILE_N = "host/golemWalk_N.png";
	/** Texture file for host sprite NORTH EAST */
	private static final String HOST_FILE_NE = "host/golemWalk_NE.png";
	/** Texture file for host sprite NORTH WEST */
	private static final String HOST_FILE_NW = "host/golemWalk_NW.png";
	/** Texture file for host sprite SOUTH */
	private static final String HOST_FILE_S = "host/golemWalk_S.png";
	/** Texture file for host sprite SOUTH EAST */
	private static final String HOST_FILE_SE = "host/golemWalk_SE.png";
	/** Texture file for host sprite SOUTH WEST */
	private static final String HOST_FILE_SW = "host/golemWalk_SW.png";
	/** Texture file for host sprite WEST */
	private static final String HOST_FILE_W = "host/golemWalk_W.png";

	/** Texture file for host glyph sprite EAST */
	private static final String HOST_GLYPH_FILE_E = "host/E_RuneSpritesheet.png";
	/** Texture file for host glyph sprite NORTH */
	private static final String HOST_GLYPH_FILE_N = "host/N_RuneSpritesheet.png";
	/** Texture file for host glyph sprite NORTH EAST */
	private static final String HOST_GLYPH_FILE_NE = "host/NE_RuneSpritesheet.png";
	/** Texture file for host sprite NORTH WEST */
	private static final String HOST_GLYPH_FILE_NW = "host/NW_RuneSpritesheet.png";
	/** Texture file for host sprite SOUTH */
	private static final String HOST_GLYPH_FILE_S = "host/S_RuneSpritesheet.png";
	/** Texture file for host sprite SOUTH EAST */
	private static final String HOST_GLYPH_FILE_SE = "host/SE_RuneSpritesheet.png";
	/** Texture file for host sprite SOUTH WEST */
	private static final String HOST_GLYPH_FILE_SW = "host/SW_RuneSpritesheet.png";
	/** Texture file for host sprite WEST */
	private static final String HOST_GLYPH_FILE_W = "host/W_RuneSpritesheet.png";

	/** Texture file for host death sprite EAST */
	private static final String HOST_DEATH_FILE_E = "host/E_DeathSpritesheet.png";
	/** Texture file for host death sprite NORTH */
	private static final String HOST_DEATH_FILE_N = "host/N_DeathSpritesheet.png";
	/** Texture file for host death sprite NORTH EAST */
	private static final String HOST_DEATH_FILE_NE = "host/NE_DeathSpritesheet.png";
	/** Texture file for host death NORTH WEST */
	private static final String HOST_DEATH_FILE_NW = "host/NW_DeathSpritesheet.png";
	/** Texture file for host death SOUTH */
	private static final String HOST_DEATH_FILE_S = "host/S_DeathSpritesheet.png";
	/** Texture file for host death SOUTH EAST */
	private static final String HOST_DEATH_FILE_SE = "host/SE_DeathSpritesheet.png";
	/** Texture file for host death SOUTH WEST */
	private static final String HOST_DEATH_FILE_SW = "host/SW_DeathSpritesheet.png";
	/** Texture file for host death WEST */
	private static final String HOST_DEATH_FILE_W = "host/W_DeathSpritesheet.png";

	/** Arms Sprite Sheet */
	private static final String HOST_ARMS_FILE = "host/ArmSpritesheet.png";

	/** Host General Possession Sprite Sheet */
	private static final String HOST_GEN_POSSESSION_FILE = "host/golemGenPossession.png";
	/** Host New Possession Sprite Sheet */
	private static final String HOST_NEW_POSSESSION_FILE = "host/golemNewPossession.png";

	/** Golem Waking Up Sprite Sheet */
	private static final String HOST_WAKING_UP_FILE = "host/wakinggolem.png";

	/** Texture file for spirit head sprite */
	private static String SPIRIT_HEAD_FILE = "host/SpiritHeadSpritesheet_v01.png";
	/** Texture file for spirit tail sprite */
	private static String SPIRIT_TAIL_FILE = "host/SpiritTailSpritesheet_v01.png";

	/** File to texture for Hosts' Gauge */
	private static String HOST_GAUGE_FILE = "host/chargeGauge.png";
	/** File to texture for Hosts' Shadow */
	private static String HOST_SHADOW_FILE = "host/shadow.png";
	/** Texture file for arrow sprite */
	private static final String ARROW_FILE = "shared/arrow.png";
	/** File to texture for Walls */
	private static String WALL_FILE = "shared/wallSpritesheet_v04.png";
	/** File to texture for Water */
	private static String WATER_FILE = "shared/waterspritesheet.png";
	/** File to texture for Water corners */
	private static String CORNER_FILE = "shared/water_corners_spritesheet.png";
	/** File to texture for sand */
	private static String SAND_FILE = "shared/sandspritesheet.png";
	/** File to texture for Sand corners */
	private static String CORNER_SAND_FILE = "shared/sand_corners_spritesheet.png";
	/** File to texture for Pedestal */
	private static String PEDESTAL_FILE = "shared/spirit_pedestal.png";
	/** File to texture for borders */
	private static String BORDER_EDGE_FILE = "shared/forest.png";
	/** File to texture for borders */
	private static String BORDER_CORNER_FILE = "shared/forestcorners.png";
	/** File to texture for Energy Pillar body */
	private static String  ENERGY_PILLAR_BODY_FILE = "shared/energyPillar_base.png";
	/** File to texture for Energy Pillar Charge */
	private static String ENERGY_PILLAR_BODY_CHARGE_FILE = "shared/energyPillar_lights.png";
	/** File to texture for Energy Pillar Radius */
	private static String ENERGY_PILLAR_RADIUS_FILE = "shared/energyRing.png";
	/** File to texture for Energy Pillar Radius */
	private static String DECORATIVE_ROOTS_FILE = "shared/rootsspritesheet.png";


	/** File to texture for OscWall Horz */
	public static String OSC_WALL_HORZ_FILE = "shared/horizontalGateSpritesheet.png";
	/** File to texture for OscWall Horz Gauge */
	public static String OSC_WALL_HORZ_GAUGE_FILE = "shared/horizontalGateLightSpritesheet.png";

	/** File to texture for OscWall Horz */
	public static String OSC_WALL_VERT_FILE = "shared/verticalGateSpritesheet.png";
	/** File to texture for OscWall Horz Gauge */
	public static String OSC_WALL_VERT_GAUGE_FILE = "shared/verticalGateLightSpritesheet.png";

	private static int FONT_SIZE = 56;

	/** The font for giving messages to the player */
	protected BitmapFont displayFont;
	/** fonts for level numbers in level select */
	protected BitmapFont oneFont;
	protected BitmapFont twoFont;
	protected BitmapFont threeFont;
	protected BitmapFont fourFont;
	/** Texture asset for background image */
	private TextureRegion backgroundTexture;
	/** The texture for walls */
	protected TextureRegion wallTex;
	/** The texture for the arrow */
	protected Texture arrowTex;
	/** Texture for Host SpriteSheet EAST*/
	private static Texture hostTextureE;
	/** Texture for Host SpriteSheet NORTH*/
	private static Texture hostTextureN;
	/** Texture for Host SpriteSheet NORTH EAST*/
	private static Texture hostTextureNE;
	/** Texture for Host SpriteSheet NORTH WEST*/
	private static Texture hostTextureNW;
	/** Texture for Host SpriteSheet SOUTH */
	private static Texture hostTextureS;
	/** Texture for Host SpriteSheet SOUTH EAST*/
	private static Texture hostTextureSE;
	/** Texture for Host SpriteSheet SOUTH WEST*/
	private static Texture hostTextureSW;
	/** Texture for Host SpriteSheet WEST*/
	private static Texture hostTextureW;
	/** Texture for Host Glyph SpriteSheet EAST*/
	private static Texture hostGlyphTextureE;
	/** Texture for Host Glyph SpriteSheet NORTH*/
	private static Texture hostGlyphTextureN;
	/** Texture for Host Glyph SpriteSheet NORTH EAST*/
	private static Texture hostGlyphTextureNE;
	/** Texture for Host Glyph SpriteSheet NORTH WEST*/
	private static Texture hostGlyphTextureNW;
	/** Texture for Host Glyph SpriteSheet SOUTH */
	private static Texture hostGlyphTextureS;
	/** Texture for Host Glyph SpriteSheet SOUTH EAST*/
	private static Texture hostGlyphTextureSE;
	/** Texture for Host Glyph SpriteSheet SOUTH WEST*/
	private static Texture hostGlyphTextureSW;
	/** Texture for Host Glyph SpriteSheet WEST*/
	private static Texture hostGlyphTextureW;
	/** Texture for Host death SpriteSheet EAST*/
	private static Texture hostDeathTextureE;
	/** Texture for Host death SpriteSheet NORTH*/
	private static Texture hostDeathTextureN;
	/** Texture for Host death SpriteSheet NORTH EAST*/
	private static Texture hostDeathTextureNE;
	/** Texture for Host death SpriteSheet NORTH WEST*/
	private static Texture hostDeathTextureNW;
	/** Texture for Host death SpriteSheet SOUTH */
	private static Texture hostDeathTextureS;
	/** Texture for Host death SpriteSheet SOUTH EAST*/
	private static Texture hostDeathTextureSE;
	/** Texture for Host death SpriteSheet SOUTH WEST*/
	private static Texture hostDeathTextureSW;
	/** Texture for Host death SpriteSheet WEST*/
	private static Texture hostDeathTextureW;
	/** Texture for Host Arms */
	private static Texture hostArmsTexture;
	/** Texture for Host Gen Possession */
	private static Texture hostGenPossessionTexture;
	/** Texture for Host New Possession */
	private static Texture hostNewPossessionTexture;
	/** Texture for Host Waking Up */
	private static Texture hostWakingUpTexture;
	/** Texture for Host Gauge SpriteSheet */
	private static Texture hostGaugeTexture;
	/** Texture for Host Shadow */
	private static Texture hostShadowTexture;
	/** Texture for Wall SpriteSheet */
	private static Texture wallTexture;
	/** Texture for Water SpriteSheet */
	private static Texture waterTexture;
	/** Texture for Water Corner SpriteSheet */
	private static Texture cornerTexture;
	/** Texture for Sand SpriteSheet */
	private static Texture sandTexture;
	/** Texture for Sand Corner SpriteSheet */
	private static Texture cornerSandTexture;
	/** Texture for Pedestal SpriteSheet */
	private static Texture pedestalTexture;
	/** Texture for Spirit Head Texture */
	private static Texture spiritHeadTexture;
	/** Texture for Spirit Body Texture */
	private static Texture spiritBodyTexture;
	/** Texture for Spirit Tail Texture */
	private static Texture spiritTailTexture;
	/** Texture for border edges */
	private static Texture borderEdgeTexture;
	/** Texture for border corners */
	private static Texture borderCornerTexture;
	/** Texture for Energy Pillar Body Texture */
	private static Texture energyPillarBody;
	/** Texture for Energy Pillar Body Lights Texture */
	private static Texture energyPillarCharge;
	/** Texture for Energy Pillar Radius Texture */
	private static Texture energyPillarRadius;
	/** Texture for Osc Wall Horz */
	private static Texture oscWallHorz;
	/** Texture for Osc Wall Gauge Horz */
	private static Texture oscWallGaugeHorz;
	/** Texture for Osc Wall Vert */
	private static Texture oscWallVert;
	/** Texture for Osc Wall Vert Gauge */
	private static Texture oscWallVertGauge;
	/** Texture for decorative roots */
	private static Texture rootsTexture;

	/** List of footprints for level editor */
	private ArrayList<FootPrintModel> footprints;

	public ArrowModel arrow;

	public HUD hud;

	public Tutorial tutorial;

	/** Whether to render the HUD */
	protected boolean renderHUD;

	// These are all lists which are filled each draw frame, in order to draw
	// all game objects in the correct order
	/** Objects drawn above everything else (HUD elements, selected objects) */
	private PooledList<Obstacle> topDrawLayer;
	/** Border edge objects to be drawn */
	private PooledList<BorderEdge> edgeDrawLayer;
	/** Wall objects to be drawn */
	private PooledList<Wall> wallDrawLayer;
	/** Host objects to be drawn */
	private PooledList<HostModel> hostDrawLayer;
	/** Spirit objects to be drawn */
	private PooledList<SpiritModel> spiritDrawLayer;
	/** Misc objects to be drawn underneath */
	private PooledList<Obstacle> miscDrawLayer;

	/** The dimensions of the board */
	protected Vector2 dimensions;
	/** Offset of the lower left corner. Allows for the ground to be offset */
	protected Vector2 lowerLeft;

	/**
	 *
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
		if (worldAssetState != AssetState.EMPTY) {
			return;
		}
		
		worldAssetState = AssetState.LOADING;
		// Load the shared tiles.
		manager.load(BACKG_FILE,Texture.class);
		assets.add(BACKG_FILE);
		manager.load(HOST_FILE_E, Texture.class);
		assets.add(HOST_FILE_E);
		manager.load(HOST_FILE_N, Texture.class);
		assets.add(HOST_FILE_N);
		manager.load(HOST_FILE_NE, Texture.class);
		assets.add(HOST_FILE_NE);
		manager.load(HOST_FILE_NW, Texture.class);
		assets.add(HOST_FILE_NW);
		manager.load(HOST_FILE_S, Texture.class);
		assets.add(HOST_FILE_S);
		manager.load(HOST_FILE_SE, Texture.class);
		assets.add(HOST_FILE_SE);
		manager.load(HOST_FILE_SW, Texture.class);
		assets.add(HOST_FILE_SW);
		manager.load(HOST_FILE_W, Texture.class);
		assets.add(HOST_FILE_W);
		manager.load(HOST_GLYPH_FILE_E, Texture.class);
		assets.add(HOST_GLYPH_FILE_E);
		manager.load(HOST_GLYPH_FILE_N, Texture.class);
		assets.add(HOST_GLYPH_FILE_N);
		manager.load(HOST_GLYPH_FILE_NE, Texture.class);
		assets.add(HOST_GLYPH_FILE_NE);
		manager.load(HOST_GLYPH_FILE_NW, Texture.class);
		assets.add(HOST_GLYPH_FILE_NW);
		manager.load(HOST_GLYPH_FILE_S, Texture.class);
		assets.add(HOST_GLYPH_FILE_S);
		manager.load(HOST_GLYPH_FILE_SE, Texture.class);
		assets.add(HOST_GLYPH_FILE_SE);
		manager.load(HOST_GLYPH_FILE_SW, Texture.class);
		assets.add(HOST_GLYPH_FILE_SW);
		manager.load(HOST_GLYPH_FILE_W, Texture.class);
		assets.add(HOST_GLYPH_FILE_W);
		manager.load(HOST_DEATH_FILE_E, Texture.class);
		assets.add(HOST_DEATH_FILE_E);
		manager.load(HOST_DEATH_FILE_N, Texture.class);
		assets.add(HOST_DEATH_FILE_N);
		manager.load(HOST_DEATH_FILE_NE, Texture.class);
		assets.add(HOST_DEATH_FILE_NE);
		manager.load(HOST_DEATH_FILE_NW, Texture.class);
		assets.add(HOST_DEATH_FILE_NW);
		manager.load(HOST_DEATH_FILE_S, Texture.class);
		assets.add(HOST_DEATH_FILE_S);
		manager.load(HOST_DEATH_FILE_SE, Texture.class);
		assets.add(HOST_DEATH_FILE_SE);
		manager.load(HOST_DEATH_FILE_SW, Texture.class);
		assets.add(HOST_DEATH_FILE_SW);
		manager.load(HOST_DEATH_FILE_W, Texture.class);
		assets.add(HOST_DEATH_FILE_W);
		manager.load(HOST_ARMS_FILE, Texture.class);
		assets.add(HOST_ARMS_FILE);
		manager.load(HOST_GEN_POSSESSION_FILE, Texture.class);
		assets.add(HOST_GEN_POSSESSION_FILE);
		manager.load(HOST_NEW_POSSESSION_FILE, Texture.class);
		assets.add(HOST_NEW_POSSESSION_FILE);
		manager.load(HOST_WAKING_UP_FILE, Texture.class);
		assets.add(HOST_WAKING_UP_FILE);
		manager.load(HOST_GAUGE_FILE, Texture.class);
		assets.add(HOST_GAUGE_FILE);
		manager.load(HOST_SHADOW_FILE, Texture.class);
		assets.add(HOST_SHADOW_FILE);
		manager.load(WALL_FILE, Texture.class);
		assets.add(WALL_FILE);
		manager.load(WATER_FILE, Texture.class);
		assets.add(WATER_FILE);
		manager.load(CORNER_FILE, Texture.class);
		assets.add(CORNER_FILE);
		manager.load(SAND_FILE, Texture.class);
		assets.add(SAND_FILE);
		manager.load(CORNER_SAND_FILE, Texture.class);
		assets.add(CORNER_SAND_FILE);
		manager.load(ARROW_FILE, Texture.class);
		assets.add(ARROW_FILE);
		manager.load(PEDESTAL_FILE, Texture.class);
		assets.add(PEDESTAL_FILE);
		manager.load(SPIRIT_HEAD_FILE, Texture.class);
		assets.add(SPIRIT_HEAD_FILE);
		manager.load(SPIRIT_TAIL_FILE, Texture.class);
		assets.add(SPIRIT_TAIL_FILE);
		manager.load(BORDER_EDGE_FILE, Texture.class);
		assets.add(BORDER_EDGE_FILE);
		manager.load(BORDER_CORNER_FILE, Texture.class);
		assets.add(BORDER_CORNER_FILE);
		manager.load(ENERGY_PILLAR_BODY_CHARGE_FILE, Texture.class);
		assets.add(ENERGY_PILLAR_BODY_CHARGE_FILE);
		manager.load(ENERGY_PILLAR_BODY_FILE, Texture.class);
		assets.add(ENERGY_PILLAR_BODY_FILE);
		manager.load(ENERGY_PILLAR_RADIUS_FILE, Texture.class);
		assets.add(ENERGY_PILLAR_RADIUS_FILE);
		manager.load(OSC_WALL_HORZ_FILE, Texture.class);
		assets.add(OSC_WALL_HORZ_FILE);
		manager.load(OSC_WALL_HORZ_GAUGE_FILE, Texture.class);
		assets.add(OSC_WALL_HORZ_GAUGE_FILE);
		manager.load(OSC_WALL_VERT_FILE, Texture.class);
		assets.add(OSC_WALL_VERT_FILE);
		manager.load(OSC_WALL_VERT_GAUGE_FILE, Texture.class);
		assets.add(OSC_WALL_VERT_GAUGE_FILE);
		manager.load(DECORATIVE_ROOTS_FILE, Texture.class);
		assets.add(DECORATIVE_ROOTS_FILE);

		// Load the font
		FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		size2Params.fontFileName = FONT_FILE;
		size2Params.fontParameters.size = FONT_SIZE;
		manager.load(FONT_FILE, BitmapFont.class, size2Params);
		assets.add(FONT_FILE);
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
		if (worldAssetState != AssetState.LOADING) {
			return;
		}
		
		// Allocate the tiles
		backgroundTexture = createTexture(manager,BACKG_FILE, true);
		wallTex = createTexture(manager, WALL_FILE, true);
		arrowTex = new Texture(ARROW_FILE);

		// Allocate the font
		if (manager.isLoaded(FONT_FILE)) {
			displayFont = manager.get(FONT_FILE,BitmapFont.class);
			oneFont = manager.get(FONT_FILE,BitmapFont.class);
			twoFont = manager.get(FONT_FILE,BitmapFont.class);
			threeFont = manager.get(FONT_FILE,BitmapFont.class);
			fourFont = manager.get(FONT_FILE,BitmapFont.class);
		} else {
			displayFont = null;
			oneFont = null;
			twoFont = null;
			threeFont = null;
			fourFont = null;
		}

		worldAssetState = AssetState.COMPLETE;

		hostTextureE = manager.get(HOST_FILE_E,Texture.class);
		hostTextureN = manager.get(HOST_FILE_N, Texture.class);
		hostTextureNE = manager.get(HOST_FILE_NE, Texture.class);
		hostTextureNW = manager.get(HOST_FILE_NW, Texture.class);
		hostTextureS = manager.get(HOST_FILE_S, Texture.class);
		hostTextureSE = manager.get(HOST_FILE_SE, Texture.class);
		hostTextureSW = manager.get(HOST_FILE_SW, Texture.class);
		hostTextureW = manager.get(HOST_FILE_W, Texture.class);
		hostGlyphTextureE = manager.get(HOST_GLYPH_FILE_E,Texture.class);
		hostGlyphTextureN = manager.get(HOST_GLYPH_FILE_N, Texture.class);
		hostGlyphTextureNE = manager.get(HOST_GLYPH_FILE_NE, Texture.class);
		hostGlyphTextureNW = manager.get(HOST_GLYPH_FILE_NW, Texture.class);
		hostGlyphTextureS = manager.get(HOST_GLYPH_FILE_S, Texture.class);
		hostGlyphTextureSE = manager.get(HOST_GLYPH_FILE_SE, Texture.class);
		hostGlyphTextureSW = manager.get(HOST_GLYPH_FILE_SW, Texture.class);
		hostGlyphTextureW = manager.get(HOST_GLYPH_FILE_W, Texture.class);
		hostDeathTextureE = manager.get(HOST_DEATH_FILE_E,Texture.class);
		hostDeathTextureN = manager.get(HOST_DEATH_FILE_N, Texture.class);
		hostDeathTextureNE = manager.get(HOST_DEATH_FILE_NE, Texture.class);
		hostDeathTextureNW = manager.get(HOST_DEATH_FILE_NW, Texture.class);
		hostDeathTextureS = manager.get(HOST_DEATH_FILE_S, Texture.class);
		hostDeathTextureSE = manager.get(HOST_DEATH_FILE_SE, Texture.class);
		hostDeathTextureSW = manager.get(HOST_DEATH_FILE_SW, Texture.class);
		hostDeathTextureW = manager.get(HOST_DEATH_FILE_W, Texture.class);
		hostArmsTexture = manager.get(HOST_ARMS_FILE, Texture.class);
		hostNewPossessionTexture = manager.get(HOST_NEW_POSSESSION_FILE, Texture.class);
		hostGenPossessionTexture = manager.get(HOST_GEN_POSSESSION_FILE, Texture.class);
		hostWakingUpTexture = manager.get(HOST_WAKING_UP_FILE, Texture.class);
		hostGaugeTexture = manager.get(HOST_GAUGE_FILE, Texture.class);
		hostShadowTexture = manager.get(HOST_SHADOW_FILE, Texture.class);
		wallTexture = manager.get(WALL_FILE, Texture.class);
		waterTexture = manager.get(WATER_FILE, Texture.class);
		cornerTexture = manager.get(CORNER_FILE, Texture.class);
		sandTexture = manager.get(SAND_FILE, Texture.class);
		cornerSandTexture = manager.get(CORNER_SAND_FILE, Texture.class);
		pedestalTexture = manager.get(PEDESTAL_FILE, Texture.class);
		spiritHeadTexture = manager.get(SPIRIT_HEAD_FILE, Texture.class);
		spiritTailTexture = manager.get(SPIRIT_TAIL_FILE, Texture.class);
		borderEdgeTexture = manager.get(BORDER_EDGE_FILE, Texture.class);
		borderCornerTexture = manager.get(BORDER_CORNER_FILE, Texture.class);
		energyPillarBody = manager.get(ENERGY_PILLAR_BODY_FILE, Texture.class);
		energyPillarCharge = manager.get(ENERGY_PILLAR_BODY_CHARGE_FILE, Texture.class);
		energyPillarRadius = manager.get(ENERGY_PILLAR_RADIUS_FILE, Texture.class);
		oscWallHorz = manager.get(OSC_WALL_HORZ_FILE, Texture.class);
		oscWallGaugeHorz = manager.get(OSC_WALL_HORZ_GAUGE_FILE, Texture.class);
		oscWallVert = manager.get(OSC_WALL_VERT_FILE, Texture.class);
		oscWallVertGauge = manager.get(OSC_WALL_VERT_GAUGE_FILE, Texture.class);
		rootsTexture = manager.get(DECORATIVE_ROOTS_FILE, Texture.class);


		// Set the proper textures in the factory
		factory = new Factory(scale, spiritBodyTexture, spiritHeadTexture, spiritTailTexture,
				hostGaugeTexture, hostShadowTexture, hostTextureE, hostTextureN, hostTextureNE, hostTextureNW,
				hostTextureS, hostTextureSE, hostTextureSW, hostTextureW, hostGlyphTextureE,
				hostGlyphTextureN, hostGlyphTextureNE, hostGlyphTextureNW, hostGlyphTextureS,
				hostGlyphTextureSE, hostGlyphTextureSW, hostGlyphTextureW, hostDeathTextureE,
				hostDeathTextureN, hostDeathTextureNE, hostDeathTextureNW, hostDeathTextureS,
				hostDeathTextureSE, hostDeathTextureSW, hostDeathTextureW, hostArmsTexture,
				hostNewPossessionTexture, hostGenPossessionTexture, hostWakingUpTexture,
				wallTexture, waterTexture, cornerTexture, sandTexture, cornerSandTexture,
				pedestalTexture, borderEdgeTexture, borderCornerTexture, energyPillarBody,
				energyPillarCharge, energyPillarRadius, oscWallVert, oscWallVertGauge,
				oscWallHorz, oscWallGaugeHorz, rootsTexture);

		// Set the proper textures in the factory
		loader = new Loader(factory);
	}
	
	/**
	 * Returns a newly loaded texture region for the given file.
	 *
	 * This helper methods is used to set texture settings (such as scaling, and
	 * whether or not the texture should repeat) after loading.
	 *
	 * @param manager 	Reference to global asset manager.
	 * @param file		The texture (region) file
	 * @param repeat	Whether the texture should be repeated
	 *
	 * @return a newly loaded texture region for the given file.
	 */
	protected TextureRegion createTexture(AssetManager manager, String file, boolean repeat) {
		if (manager.isLoaded(file)) {
			TextureRegion region = new TextureRegion(manager.get(file, Texture.class));
			region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			if (repeat) {
				region.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
			}
			return region;
		}
		return null;
	}
	

	/** 
	 * Unloads the assets for this game.
	 * 
	 * This method erases the static variables.  It also deletes the associated textures 
	 * from the asset manager. If no assets are loaded, this method does nothing.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public void unloadContent(AssetManager manager) {
    	for(String s : assets) {
    		if (manager.isLoaded(s)) {
    			manager.unload(s);
    		}
    	}
	}
	
	/** Exit code for quitting the game */
	public static final int EXIT_QUIT = 0;
	/** Exit code for advancing to next level */
	public static final int EXIT_NEXT = 1;
	/** Exit code for jumping back to previous level */
	public static final int EXIT_PREV = 2;
	/** Exit code for going to the play screen */
	public static final int EXIT_PLAY = 3;
	/** Exit code for going to the level design screen */
	public static final int EXIT_DESIGN = 4;
	/** Exit code for going to the level select screen */
	public static final int EXIT_SELECT = 5;
	/** Exit code for going to the main menu screen */
	public static final int EXIT_MENU = 6;
	/** Exit code for going to game complete screen */
	public static final int EXIT_GAME = 7;
	/** Exit code for going to the credits screen */
	public static final int EXIT_CREDITS = 8;

    /** How many frames after winning/losing do we continue? */
	public static final int EXIT_COUNT = 50;

	/** The amount of time for a physics engine step. */
	public static final float WORLD_STEP = 1/60.0f;
	/** Number of velocity iterations for the constrain solvers */
	public static final int WORLD_VELOC = 6;
	/** Number of position iterations for the constrain solvers */
	public static final int WORLD_POSIT = 2;
	
	/** Width of the game world in Box2d units */
	protected static final float DEFAULT_WIDTH  = 32.0f;
	/** Height of the game world in Box2d units */
	protected static final float DEFAULT_HEIGHT = 18.0f;
	/** The default value of gravity (going down) */
	protected static final float DEFAULT_GRAVITY = -4.9f;

	/** Reference to the game canvas */
	protected GameCanvas canvas;
	/** All the objects in the world. */
	protected PooledList<Obstacle> objects  = new PooledList<Obstacle>();
	/** Queue for adding objects */
	protected PooledList<Obstacle> addQueue = new PooledList<Obstacle>();
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	/** The Box2D world */
	protected World world;
	/** The boundary of the world */
	protected Rectangle bounds;
	/** The world scale */
	protected Vector2 scale;
	
	/** Whether or not this is an active controller */
	private boolean active;
	/** Whether we have completed this level */
	private boolean complete;
	/** Whether we have failed at this world (and need a reset) */
	protected boolean failed;
	/** Whether or not debug mode is active */
	private boolean debug;
	/** Countdown active for winning or losing */
	private int countdown;
	/** Controls the menu */
	public boolean menu;
	/** Current level */
	protected static int currentLevel;
	/** Whether to update Gameplay controller */
	private	boolean updateGP;


	/** list of level files*/
	protected ArrayList<FileHandle> levels;

	/** Input if paused was pressed */
	private boolean pressedPause = false;
	/** Whether game is currently paused */
	private boolean isPaused = false;
	/** Whether game was just paused */
	protected boolean wasPaused = false;

	/**
	 * Returns true if debug mode is active.
	 *
	 * If true, all objects will display their physics bodies.
	 *
	 * @return true if debug mode is active.
	 */
	public boolean isDebug( ) {
		return debug;
	}

	/**
	 * Sets whether debug mode is active.
	 *
	 * If true, all objects will display their physics bodies.
	 *
	 * @param value whether debug mode is active.
	 */
	public void setDebug(boolean value) {
		debug = value;
	}

	public void getLevels(boolean custom) {
		if (custom) {
			FileHandle folder = Gdx.files.local("Custom");
			ArrayList<File> levelFiles = new ArrayList<File>(Arrays.asList(folder.file().listFiles(Constants.filenameFilter)));
			levels = new ArrayList<FileHandle>();
			for (File f : levelFiles) {
				levels.add(Gdx.files.local("Custom/" + f.getName()));
			}
			System.out.println("(Custom) level size: " + levels.size());
		}
		else {
			levels = new ArrayList<FileHandle>();
			for (int i = 1; i <= NUM_LEVELS; i++) {
				// Ensure one digit number has a leading 0 in the string
				String num = i < 10 ? "0" + i : "" + i;
				levels.add(Gdx.files.internal("levels/" + num + ".lvl"));
			}
			System.out.println("(preset) level size: " + levels.size());
		}
		Collections.sort(levels, new Comparator<FileHandle>() {
			@Override
			public int compare(FileHandle o1, FileHandle o2) {
				String name1 = o1.nameWithoutExtension(), name2 = o2.nameWithoutExtension();
				return name1.compareTo(name2);
			}
		});
	}

	/**
	 *  Sets the current level to param
	 *
	 * @param l The level number
	 */
	public void setCurrentLevel(int l) {
		System.out.println("SET CURRENT LEVEL TO:" + l);
		currentLevel = l;
	}

	/**
	 *  Increments the current level when won
	 *
	 */
	public void incrementCurrentLevel() {
		// currentLevel = (int) Math.min(currentLevel + 1, MAX_NUM_LEVELS);
		// TODO: Game Complete When Beat All Levels -- currently loops
		currentLevel = (currentLevel + 1) % NUM_LEVELS;
	}

	/**
	 * Returns true if the level is completed.
	 *
	 * If true, the level will advance after a countdown
	 *
	 * @return true if the level is completed.
	 */
	public boolean isComplete( ) {
		return complete;
	}

	/**
	 * Sets whether the level is completed.
	 *
	 * If true, the level will advance after a countdown
	 *
	 * @param value whether the level is completed.
	 */
	public void setComplete(boolean value) {
		if (value) {
			countdown = EXIT_COUNT;
		}
		complete = value;
	}

	/**
	 * Returns true if the level is failed.
	 *
	 * If true, the level will reset after a countdown
	 *
	 * @return true if the level is failed.
	 */
	public boolean isFailure( ) {
		return failed;
	}

	/**
	 * Sets whether the level is failed.
	 *
	 * If true, the level will reset after a countdown
	 *
	 * @param value whether the level is failed.
	 */
	public void setFailure(boolean value) {
		if (value) {
			countdown = EXIT_COUNT;
		}
		failed = value;
	}

	public void setMenu(boolean value) {
		menu = value;
	}
	
	/**
	 * Returns true if this is the active screen
	 *
	 * @return true if this is the active screen
	 */
	public boolean isActive( ) {
		return active;
	}

	/**
	 * Returns the canvas associated with this controller
	 *
	 * The canvas is shared across all controllers
	 *
	 * the canvas associated with this controller
	 */
	public GameCanvas getCanvas() {
		return canvas;
	}
	
	/**
	 * Sets the canvas associated with this controller
	 *
	 * The canvas is shared across all controllers.  Setting this value will compute
	 * the drawing scale from the canvas size.
	 *
	 * @param canvas the canvas associated with this controller
	 */
	public void setCanvas(GameCanvas canvas) {
		this.canvas = canvas;
		this.scale.x = canvas.getWidth()/bounds.getWidth();
		this.scale.y = canvas.getHeight()/bounds.getHeight();
	}
	
	/**
	 * Creates a new game world with the default values.
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 */
	protected WorldController() {
		this(new Rectangle(0,0,DEFAULT_WIDTH,DEFAULT_HEIGHT), 
			 new Vector2(0,0));
	}

	/**
	 * Creates a new game world
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param width  	The width in Box2d coordinates
	 * @param height	The height in Box2d coordinates
	 * @param gravity	The downward gravity
	 */
	protected WorldController(float width, float height, float gravity) {
		this(new Rectangle(0,0,width,height), new Vector2(0,0));
	}

	/**
	 * Creates a new game world
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param bounds	The game bounds in Box2d coordinates
	 * @param gravity	The gravitational force on this Box2d world
	 */
	protected WorldController(Rectangle bounds, Vector2 gravity) {
		assets = new Array<String>();
		world = new World(gravity, false);
		this.bounds = new Rectangle(bounds);
		this.scale = new Vector2(1, 1);
		complete = false;
		failed = false;
		debug = false;
		active = false;
		renderHUD = true;
		countdown = -1;
		currentLevel = 0;
		dimensions = new Vector2();
		lowerLeft = new Vector2();
		footprints = new ArrayList<>();
		topDrawLayer = new PooledList<>();
		edgeDrawLayer = new PooledList<>();
		wallDrawLayer = new PooledList<>();
		hostDrawLayer = new PooledList<>();
		spiritDrawLayer = new PooledList<>();
		miscDrawLayer = new PooledList<>();


		hud = new HUD();
		tutorial = new Tutorial();
	}
	
	/**
	 * Dispose of all (non-static) resources allocated to this mode.
	 */
	public void dispose() {
		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		world.dispose();
		hud.dispose();
		tutorial.dispose();
		objects = null;
		addQueue = null;
		bounds = null;
		scale  = null;
		world  = null;
		canvas = null;
		hud = null;
	}

	/**
	 *
	 * Adds a physics object in to the insertion queue.
	 *
	 * Objects on the queue are added just before collision processing.  We do this to 
	 * control object creation.
	 *
	 * param obj The object to add
	 */
	public void addQueuedObject(Obstacle obj) {
		assert inBounds(obj) : "Object is not in bounds";
		addQueue.add(obj);
	}

	/**
	 * Immediately adds the object to the physics world
	 *
	 * @param obj The object to add
	 */
	protected void addObject(Obstacle obj) {
		assert inBounds(obj) : "Object is not in bounds";
		objects.add(obj);
		obj.activatePhysics(world);
	}

	/**
	 * Returns true if the object is in bounds.
	 *
	 * This assertion is useful for debugging the physics.
	 *
	 * @param obj The object to check.
	 *
	 * @return true if the object is in bounds.
	 */
	public boolean inBounds(Obstacle obj) {
		boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x+bounds.width);
		boolean vert  = (bounds.y <= obj.getY() && obj.getY() <= bounds.y+bounds.height);
		return horiz && vert;
	}
	
	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public abstract void reset();
	
	/**
	 * Returns whether to process the update loop
	 *
	 * At the start of the update loop, we check if it is time
	 * to switch to a new game mode.  If not, the update proceeds
	 * normally.
	 *
	 * @param dt Number of seconds since last animation frame
	 * 
	 * @return whether to process the update loop
	 */
	public boolean preUpdate(float dt) {
		InputController input = InputController.getInstance();
		if (this instanceof LevelDesignerMode) {
			input.readInput(bounds, scale, canvas.getZoom());
		}
		else{
			input.readInput();
		}
		if (listener == null) {
			return true;
		}

		// Toggle debug
		if (input.didDebug()) {
			debug = !debug;
		}
		
		// Handle resets
		if (input.didReset()) {
			reset();
		}

		if (input.didPause() || HUD.getPauseClicked()) {
			arrow = null;
			pressedPause = true;
			return false;
		}

		// Now it is time to maybe switch screens.
		if (input.didExit()) {
			isPaused = false;
			listener.exitScreen(this, EXIT_QUIT);
			return false;
		} else if (input.didAdvance()) {
			isPaused = false;
			listener.exitScreenLevel(currentLevel+1);
			return false;
		} else if (input.didRetreat()) {
			isPaused = false;
			listener.exitScreenLevel(currentLevel-1);
			return false;
		} else if (input.didMenu()) {
			isPaused = false;
			setMenu(true);
			listener.exitScreen(this, EXIT_MENU);
		}
		else if (countdown > 0) {
			countdown--;
		} else if (countdown == 0) {
			// TODO: REMOVE IF THINGS START GETTING LAGGY
			// Creates a screenshot of last screen
			// GameOver.screenShotPixmap = ScreenUtils.getFrameBufferPixmap(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			if (failed) {
				GameOver.setFail(true);
				isPaused = false;
				listener.exitScreen(this, EXIT_GAME);
			} else if (complete) {
				// TODO: go to the next level
				GameOver.setFail(false);
				isPaused = false;
				listener.exitScreen(this, EXIT_GAME);
				return false;
			}
		}
		return true;
	}

	public void setFootprints(ArrayList<FootPrintModel> list) {
		footprints = list;
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
	public abstract void update(float dt);
	
	/**
	 * Processes physics
	 *
	 * Once the update phase is over, but before we draw, we are ready to handle
	 * physics.  The primary method is the step() method in world.  This implementation
	 * works for all applications and should not need to be overwritten.
	 *
	 * @param dt Number of seconds since last animation frame
	 */
	public void postUpdate(float dt) {
		// Add any objects created by actions
		while (!addQueue.isEmpty()) {
			addObject(addQueue.poll());
		}

		// Turn the physics engine crank.
		world.step(WORLD_STEP,WORLD_VELOC,WORLD_POSIT);

		// Garbage collect the deleted objects.
		// Note how we use the linked list nodes to delete O(1) in place.
		// This is O(n) without copying.
		Iterator<PooledList<Obstacle>.Entry> iterator = objects.entryIterator();
		while (iterator.hasNext()) {
			PooledList<Obstacle>.Entry entry = iterator.next();
			Obstacle obj = entry.getValue();
//			if(obj instanceof BoxObstacle){
//				((BoxObstacle) obj).alive -= 1;
//				if(((BoxObstacle) obj).alive == 0){
//					obj.markRemoved(true);
//				}
//			}
			if (obj.isRemoved()) {
				obj.deactivatePhysics(world);
				entry.remove();
			} else {
				// Note that update is called last!
				obj.update(dt);
			}
		}
	}
	
	/**
	 * Draw the physics objects to the canvas
	 *
	 * For simple worlds, this method is enough by itself.  It will need
	 * to be overriden if the world needs fancy backgrounds or the like.
	 *
	 * The method draws all objects in the order that they were added.
	 *
	 * @param delta The drawing context
	 */
	public void draw(float delta) {
		canvas.clear();

		// Clear the lists so they can be repopulated
		miscDrawLayer.clear();
		edgeDrawLayer.clear();
		wallDrawLayer.clear();
		hostDrawLayer.clear();
		spiritDrawLayer.clear();
		topDrawLayer.clear();


		canvas.begin();

//		System.out.println("Drawing");
//		System.out.println("dimensions: " + dimensions);
//		System.out.println("scale: " + scale);
//		System.out.println("canvas size: (" + canvas.getWidth() + ", " + canvas.getHeight() + ")");
		// Use the lower left corner of tiles, not the center, to start drawing the canvas
		for(float x = 0; x < scale.x * dimensions.x; x += canvas.getWidth()) {
//			System.out.println("x = " + x);
			for(float y = 0; y < scale.y * dimensions.y; y += canvas.getHeight()) {
//				System.out.println("y = " + y);

				// Calculate the width and height of the canvas segment. If the
				// board doesn't extend the entire way, find the desired dimensions
				float width = Math.min(canvas.getWidth(), (scale.x * dimensions.x) - x);
				float height = Math.min(canvas.getHeight(), (scale.y * dimensions.y) - y);

				// Draw only the part of the texture that is in game, using the
				// texture coordinates
				canvas.draw(backgroundTexture.getTexture(), Color.WHITE,
						(scale.x * lowerLeft.x) + x, (scale.y * lowerLeft.y) + y,  width, height,
						0.f, 0.f, width / canvas.getWidth(), height / canvas.getHeight());

//				canvas.draw(backgroundTexture, Color.WHITE, TILE_WIDTH * scale.x * x, TILE_WIDTH * scale.y * y,canvas.getWidth(),canvas.getHeight());
			}
		}
//		canvas.draw(backgroundTexture, Color.WHITE, 0, 0,canvas.getWidth(),canvas.getHeight());
		canvas.end();

		for(Obstacle obj : objects) {
			if(obj.inHUD || obj.selected) {
				topDrawLayer.add(obj);
			} else if(obj instanceof BorderEdge) {
				edgeDrawLayer.add((BorderEdge)obj);
			} else if(obj instanceof Wall) {
				wallDrawLayer.add((Wall)obj);
			} else if(obj instanceof HostModel) {
				hostDrawLayer.add((HostModel)obj);
			} else if(obj instanceof SpiritModel) {
				spiritDrawLayer.add((SpiritModel)obj);
			} else {
				miscDrawLayer.add(obj);
			}
		}

		canvas.begin();

		for(Obstacle obj : miscDrawLayer) {
			obj.draw(canvas);
		}
		for(BorderEdge edge : edgeDrawLayer) {
			edge.drawTop(canvas);
		}
		for(Wall wall : wallDrawLayer) {
			wall.drawFront(canvas);
		}
		for(HostModel host : hostDrawLayer) {
			host.drawShadow(canvas);
		}
		for(HostModel host : hostDrawLayer) {
			host.drawBody(canvas);
		}
		for(SpiritModel spirit : spiritDrawLayer) {
			spirit.draw(canvas);
		}
		for(Wall wall : wallDrawLayer) {
			wall.drawTop(canvas);
		}
		for(HostModel host : hostDrawLayer) {
			host.drawCharge(canvas);
		}
		for(BorderEdge edge : edgeDrawLayer) {
			edge.drawNotTop(canvas);
		}
		for(Obstacle obj : topDrawLayer) {
			obj.draw(canvas);
		}

//		for(Obstacle obj : objects) {
//			if(!(obj instanceof Wall) && !(obj instanceof HostModel) && !obj.inHUD && !obj.selected) {
//				obj.draw(canvas);
//			}
//		}
//		for(Obstacle obj : objects) {
//			if(obj instanceof Wall && !obj.inHUD && !obj.selected) {
//				obj.draw(canvas);
//			}
//		}
//		for(Obstacle obj : objects) {
//			if((obj instanceof HostModel || obj instanceof SpiritModel) && !obj.inHUD && !obj.selected) {
//				obj.draw(canvas);
//			}
//		}
//		for(Obstacle obj : objects) {
//			if(obj instanceof Wall && !obj.inHUD && !obj.selected) {
//				((Wall)obj).drawTop(canvas);
//			}
//		}
//		for(Obstacle obj : objects) {
//			if(obj.inHUD || obj.selected) {
//				obj.draw(canvas);
//			}
//		}

//		for(Obstacle obj : objects) {
//			if(!(obj instanceof Wall) && !(obj instanceof HostModel) && !obj.inHUD && !obj.selected) {
//				obj.draw(canvas);
//			}
//		}
//		for(Obstacle obj : objects) {
//			if(obj instanceof Wall && !obj.inHUD && !obj.selected) {
//				obj.draw(canvas);
//			}
//		}
//		for(Obstacle obj : objects) {
//			if((obj instanceof HostModel || obj instanceof SpiritModel) && !obj.inHUD && !obj.selected) {
//				obj.draw(canvas);
//			}
//		}
//		for(Obstacle obj : objects) {
//			if(obj instanceof Wall && !obj.inHUD && !obj.selected) {
//				((Wall)obj).drawTop(canvas);
//			}
//		}
//		for(Obstacle obj : objects) {
//			if(obj.inHUD || obj.selected) {
//				obj.draw(canvas);
//			}
//		}

		// Draw footprints
		for (FootPrintModel fp : footprints) {
			fp.draw(canvas);
		}
		canvas.end();

		if (debug) {
			canvas.beginDebug();
			for(Obstacle obj : objects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
		}

		if (arrow != null && !failed){
			arrow.draw(canvas);
		}

		// Check to hid tutorial
		if (!tutorial.didCompleteTutorial()) {
			tutorial.drawTutorial(delta);
			tutorial.updateTutorial(delta);
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
		// IGNORE FOR NOW
	}

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
			updateGP = preUpdate(delta);

			/** If it was the first time the player pressed pause */
			if (pressedPause) {
				System.out.println("PRESSED PAUSE");
				isPaused = true;
				hud.pauseGame();
				pressedPause = false;
			}

			draw(delta);

			/** If the game isnt paused or switching screens, continue updating GP */
			if (updateGP && !isPaused) {
				update(delta); // This is the one that must be defined.
				postUpdate(delta);
			}

			/** Draw the HUD (on top of the environment */
			if (renderHUD) {
				hud.getStage().act(delta);
				hud.getStage().draw();
			}

			/** If the game is currently paused */
			if (isPaused) {
				pause();
			}
			MusicController.getInstance().update();
		}
	}

	/**
	 * Called when the Screen is paused.
	 * 
	 * This is usually when it's not active or visible on screen. An Application is 
	 * also paused before it is destroyed.
	 */
	public void pause() {
		// TODO Auto-generated method stub

		if (hud.getMenuClicked()) {
			isPaused = false;
			setMenu(true);
			listener.exitScreen(this, EXIT_MENU);
			hud.reset();
		}
		if (hud.getRetryClicked()) {
			isPaused = false;
			hud.reset();
			reset();
		}
		if (hud.getPlayClicked()) {
			hud.resumeGame();
			isPaused = false;
			wasPaused = true;
		}
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
}