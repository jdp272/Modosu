package edu.cornell.gdiac.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.obstacle.*;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.util.FilmStrip;

public class Factory {
    private static int SMALL_MAX_CHARGE = 800;
    private static int SPIRIT_LIVES = 3;
    private static int SPIRIT_BOUNCES = 8;
    private static int DEFAULT_LIFE = 250;

    private Texture hostChargeTexture;
    private Texture hostShadow;
    private Texture hostTextureE;
    private Texture hostTextureN;
    private Texture hostTextureNE;
    private Texture hostTextureNW;
    private Texture hostTextureS;
    private Texture hostTextureSE;
    private Texture hostTextureSW;
    private Texture hostTextureW;
    private Texture glyphTextureE;
    private Texture glyphTextureN;
    private Texture glyphTextureNE;
    private Texture glyphTextureNW;
    private Texture glyphTextureS;
    private Texture glyphTextureSE;
    private Texture glyphTextureSW;
    private Texture glyphTextureW;
    private Texture hostDeathTextureE;
    private Texture hostDeathTextureN;
    private Texture hostDeathTextureNE;
    private Texture hostDeathTextureNW;
    private Texture hostDeathTextureS;
    private Texture hostDeathTextureSE;
    private Texture hostDeathTextureSW;
    private Texture hostDeathTextureW;
    private Texture hostWakingUp;
    private Texture hostNewPossession;
    private Texture hostGenPossession;
    private Texture hostArmTexture;
    private Texture wallDayTexture;
    private Texture wallNightTexture;
    private Texture waterTexture;
    private Texture waterNightTexture;
    private Texture cornerTexture;
    private Texture cornerNightTexture;
    private Texture sandTexture;
    private Texture sandNightTexture;
    private Texture cornerSandTexture;
    private Texture cornerSandNightTexture;
    private Texture pedestalTexture;
    private Texture pedestalNightTexture;
    private Texture spiritBodyTexture;
    private Texture spiritHeadTexture;
    private Texture spiritTailTexture;
    private Texture borderEdgeTexture;
    private Texture borderEdgeNightTexture;
    private Texture borderCornerTexture;
    private Texture borderCornerNightTexture;
    private Texture energyPillarBodyTexture;
    private Texture energyPillarBodyChargeTexture;
    private Texture energyPillarRadiusTexture;
    private Texture oscWallVertTexture;
    private Texture oscWallVertNightTexture;
    private Texture oscWallVertGaugeTexture;
    private Texture oscWallHorzTexture;
    private Texture oscWallHorzNightTexture;
    private Texture oscWallHorzGaugeTexture;
    private Texture rootsTexture;
    private Texture rootsNightTexture;

    /** The shadow texture for hosts. Not a film strip, so can be stored here */
    private TextureRegion hostShadowRegion;

    // Static variables for sprite sheets

    /** Number of rows in the energy pillar filmstrip */
    private static final int ENERGY_PILLAR_ROWS = 1;
    /** Number of columns in the energy pillar filmstrip */
    private static final int ENERGY_PILLAR_COLUMNS = 1;
    /** Number of frames in the energy pillar filmstrip */
    private static final int ENERGY_PILLAR_SIZE = 1;

    /** Number of rows in the spirit image filmstrip */
    private static final int SPIRIT_ROWS = 35;
    /** Number of columns in the spirit image filmstrip */
    private static final int SPIRIT_COLUMNS = 4;
    /** Total Number of frames in the spirit image filmstrip */
    private static final int SPIRIT_SIZE = 140;


    /**
     * Number of rows in the host image filmstrip
     */
    private static final int HOST_ROWS = 10;
    /**
     * Number of columns in this host image filmstrip
     */
    private static final int HOST_COLUMNS = 6;
    /**
     * Number of total hosts in the host image filmstrip
     */
    private static final int HOST_SIZE = 60;

    /**
     * Number of rows in the host arms image filmstrip
     */
    private static final int HOST_ARMS_ROWS = 1;
    /**
     * Number of columns in the host arms image filmstrip
     */
    private static final int HOST_ARMS_COLS = 8;
    /**
     * Number of total frames in the host arms image filmstrip
     */
    private static final int HOST_ARMS_SIZE = 8;


    /**
     * Number of rows in the charge image filmstrip
     */
    private static final int CHARGE_ROWS = 4;
    /** Number of columns in the charge image filmstrip */
    private static final int CHARGE_COLUMNS = 8;
    /** Number of total frames in the charge image filmstrip */
    private static final int CHARGE_SIZE = 32;

    /**
     * Number of rows in the wall image filmstrip
     */
    private static final int WALL_ROWS = 4;
    /** Number of columns in this wall image filmstrip */
    private static final int WALL_COLUMNS = 8;
    /** Number of total hosts in the wall image filmstrip */
    private static final int WALL_SIZE = 32;


    /**
     * Number of rows in the water image filmstrip
     */
    private static final int WATER_ROWS = 4;
    /** Number of columns in the water image filmstrip */
    private static final int WATER_COLUMNS = 4;
    /** Number of total hosts in the water image filmstrip */
    private static final int WATER_SIZE = 16;

    /**
     * Number of rows in the water corner image filmstrip
     */
    private static final int WATER_CORNER_ROWS = 2;
    /** Number of columns in the water corner image filmstrip */
    private static final int WATER_CORNER_COLUMNS = 2;
    /** Number of total hosts in the water corner image filmstrip */
    private static final int WATER_CORNER_SIZE = 4;

    /**
     * Number of rows in the pedestal image filmstrip
     */
    private static final int PEDESTAL_ROWS = 4;
    /**
     * Number of columns in the pedestal image filmstrip
     */
    private static final int PEDESTAL_COLS = 4;
    /**
     * Number of total pedestals in the pedestal image filmstrip
     */
    private static final int PEDESTAL_SIZE = 16;

    /**
     * Number of rows in the possession image filmstrip
     */
    private static final int POSSESSION_ROWS = 1;
    /**
     * Number of total columns in the possession image filmstrip
     */
    private static final int POSSESSION_COLS = 13;
    /**
     * Number of total frames in the possession image filmstrip
     */
    private static final int POSSESSION_SIZE = 13;

    /**
     * Number of rows in the host wake up image filmstrip
     */
    private static final int HOST_WAKE_UP_ROWS = 1;
    /**
     * Number of cols in the host wake up image filmstrip
     */
    private static final int HOST_WAKE_UP_COLS = 8;
    /**
     * Number of total frames in the possession image filmstrip
     */
    private static final int HOST_WAKE_UP_SIZE = 8;

    /** Number of rows in the border edge image filmstrip */
    private static final int BORDER_EDGE_ROWS = 4;
    /** Number of columns in the border edge image filmstrip */
    private static final int BORDER_EDGE_COLUMNS = 13;

    /** Number of rows in the border corner image filmstrip */
    private static final int BORDER_CORNER_ROWS = 2;
    /** Number of columns in the border corner image filmstrip */
    private static final int BORDER_CORNER_COLUMNS = 2;

    /** Number of rows in the decorative roots image filmstrip */
    private static final int ROOTS_ROWS = 2;
    /** Number of columns in the decorative roots image filmstrip */
    private static final int ROOTS_COLUMNS = 2;

    /** Number of rows in the oscwall image filmstrip */
    private static final int OSC_WALL_VERT_ROWS = 6;
    /** Number of cols in the oscwall image filmstrip */
    private static final int OSC_WALL_VERT_COLS = 10;
    /** Number of total frames in the oscwall image filmstrip */
    private static final int OSC_WALL_SIZE = 60;


    /** The draw scale of objects */
    private Vector2 scale;

    /** Opacity of night */
    private Color opacity;


    /** Can be set. If true, instantiated objects are sensors */
    public boolean makeSensors;

    /** Can be set. If true, new objects are the size of a tile */
    public boolean makeTileSized;


    public Factory(
            Vector2 scale,
            Texture spiritBodyTexture,
            Texture spiritHeadTexture,
            Texture spiritTailTexture,
            Texture hostChargeTexture,
            Texture hostShadow,
            Texture hostTextureE,
            Texture hostTextureN,
            Texture hostTextureNE,
            Texture hostTextureNW,
            Texture hostTextureS,
            Texture hostTextureSE,
            Texture hostTextureSW,
            Texture hostTextureW,
            Texture hostGlyphTextureE,
            Texture hostGlyphTextureN,
            Texture hostGlyphTextureNE,
            Texture hostGlyphTextureNW,
            Texture hostGlyphTextureS,
            Texture hostGlyphTextureSE,
            Texture hostGlyphTextureSW,
            Texture hostGlyphTextureW,
            Texture hostDeathTextureE,
            Texture hostDeathTextureN,
            Texture hostDeathTextureNE,
            Texture hostDeathTextureNW,
            Texture hostDeathTextureS,
            Texture hostDeathTextureSE,
            Texture hostDeathTextureSW,
            Texture hostDeathTextureW,
            Texture hostArmTexture,
            Texture hostNewPossession,
            Texture hostGenPossession,
            Texture hostWakeUp,
            Texture wallDayTexture,
            Texture wallNightTexture,
            Texture waterTexture,
            Texture waterNightTexture,
            Texture cornerTexture,
            Texture cornerNightTexture,
            Texture sandTexture,
            Texture sandNightTexture,
            Texture cornerSandTexture,
            Texture cornerSandNightTexture,
            Texture pedestalTexture,
            Texture pedestalNightTexture,
            Texture borderEdgeTexture,
            Texture borderEdgeNightTexture,
            Texture borderCornerTexture,
            Texture borderCornerNightTexture,
            Texture energyPillarBodyTexture,
            Texture energyPillarBodyChargeTexture,
            Texture energyPillarRadiusTexture,
            Texture oscWallVertTexture,
            Texture oscWallVertNightTexture,
            Texture oscWallVertGaugeTexture,
            Texture oscWallHorzTexture,
            Texture oscWallHorzNightTexture,
            Texture oscWallHorzGaugeTexture,
            Texture rootsTexture,
            Texture rootsNightTexture
    ) {
        this.scale = scale;
        this.spiritBodyTexture = spiritBodyTexture;
        this.spiritHeadTexture = spiritHeadTexture;
        this.spiritTailTexture = spiritTailTexture;
        this.hostChargeTexture = hostChargeTexture;
        this.hostShadow = hostShadow;
        this.hostTextureE = hostTextureE;
        this.hostTextureN = hostTextureN;
        this.hostTextureNE = hostTextureNE;
        this.hostTextureNW = hostTextureNW;
        this.hostTextureS = hostTextureS;
        this.hostTextureSE = hostTextureSE;
        this.hostTextureSW = hostTextureSW;
        this.hostTextureW = hostTextureW;
        this.glyphTextureE = hostGlyphTextureE;
        this.glyphTextureN = hostGlyphTextureN;
        this.glyphTextureNE = hostGlyphTextureNE;
        this.glyphTextureNW = hostGlyphTextureNW;
        this.glyphTextureS = hostGlyphTextureS;
        this.glyphTextureSE = hostGlyphTextureSE;
        this.glyphTextureSW = hostGlyphTextureSW;
        this.glyphTextureW = hostGlyphTextureW;
        this.hostDeathTextureE = hostDeathTextureE;
        this.hostDeathTextureN = hostDeathTextureN;
        this.hostDeathTextureNE = hostDeathTextureNE;
        this.hostDeathTextureNW = hostDeathTextureNW;
        this.hostDeathTextureS = hostDeathTextureS;
        this.hostDeathTextureSE = hostDeathTextureSE;
        this.hostDeathTextureSW = hostDeathTextureSW;
        this.hostDeathTextureW = hostDeathTextureW;
        this.hostArmTexture = hostArmTexture;
        this.hostWakingUp = hostWakeUp;
        this.hostNewPossession = hostNewPossession;
        this.hostGenPossession = hostGenPossession;
        this.wallDayTexture = wallDayTexture;
        this.wallNightTexture = wallNightTexture;
        this.waterTexture = waterTexture;
        this.waterNightTexture = waterNightTexture;
        this.cornerTexture = cornerTexture;
        this.cornerNightTexture = cornerNightTexture;
        this.sandTexture = sandTexture;
        this.sandNightTexture = sandNightTexture;
        this.cornerSandTexture = cornerSandTexture;
        this.cornerSandNightTexture = cornerSandNightTexture;
        this.pedestalTexture = pedestalTexture;
        this.pedestalNightTexture = pedestalNightTexture;
        this.borderEdgeTexture = borderEdgeTexture;
        this.borderEdgeNightTexture = borderEdgeNightTexture;
        this.borderCornerTexture = borderCornerTexture;
        this.borderCornerNightTexture = borderCornerNightTexture;
        this.energyPillarBodyChargeTexture = energyPillarBodyChargeTexture;
        this.energyPillarBodyTexture = energyPillarBodyTexture;
        this.energyPillarRadiusTexture = energyPillarRadiusTexture;
        this.oscWallVertTexture = oscWallVertTexture;
        this.oscWallVertNightTexture = oscWallVertNightTexture;
        this.oscWallVertGaugeTexture = oscWallVertGaugeTexture;
        this.oscWallHorzTexture = oscWallHorzTexture;
        this.oscWallHorzNightTexture = oscWallHorzNightTexture;
        this.oscWallHorzGaugeTexture = oscWallHorzGaugeTexture;
        this.rootsTexture = rootsTexture;
        this.rootsNightTexture = rootsNightTexture;

        this.hostShadowRegion = new TextureRegion(hostShadow);

        this.opacity = Color.WHITE;
    }

    public void setOpacity(Color opacity) {
        this.opacity = opacity;
    }

    public DecorativeRoots makeDecorativeRoot(float x, float y, int frame) {
        DecorativeRoots roots = new DecorativeRoots(
                x,
                y,
                Constants.TILE_WIDTH,
                Constants.TILE_HEIGHT,
                frame
        );
        FilmStrip tex = new FilmStrip(rootsTexture, ROOTS_ROWS, ROOTS_COLUMNS);
        FilmStrip texNight = new FilmStrip(rootsNightTexture, ROOTS_ROWS, ROOTS_COLUMNS);
        tex.setFrame(frame);
        texNight.setFrame(frame);
        roots.setTexture(tex);
        roots.setNightTexture(texNight, opacity);
        roots.setDrawScale(scale);
        roots.setBodyType(BodyDef.BodyType.StaticBody);
        roots.setSensor(true); // They should never obstruct other objects
        roots.setName("decorative");
        roots.selectable = false;
        return roots;
    }

    public EnergyPillar makeEnergyPillar(float x, float y) {
        EnergyPillar engPill = new EnergyPillar(
                x,
                y,
                Constants.TILE_WIDTH,
                Constants.TILE_HEIGHT
        );
        engPill.setEnergyPillarStrips(new FilmStrip(energyPillarBodyTexture, ENERGY_PILLAR_ROWS, ENERGY_PILLAR_COLUMNS, ENERGY_PILLAR_SIZE),
                new FilmStrip(energyPillarBodyChargeTexture, ENERGY_PILLAR_ROWS, ENERGY_PILLAR_COLUMNS, ENERGY_PILLAR_SIZE),
                new FilmStrip(energyPillarRadiusTexture, ENERGY_PILLAR_ROWS, ENERGY_PILLAR_COLUMNS, ENERGY_PILLAR_SIZE));
        engPill.setDrawScale(scale);
        engPill.setBodyType(BodyDef.BodyType.StaticBody);
        engPill.setSensor(makeSensors);
        engPill.setName("energyPillar");
        return engPill;
    }

    public Wall makeWall(float x, float y) {
        Wall wall = new Wall(
                x,
                y,
                Constants.TILE_WIDTH,
                Constants.TILE_HEIGHT,
                new FilmStrip(wallNightTexture, WALL_ROWS, WALL_COLUMNS, WALL_SIZE),
                new FilmStrip(wallDayTexture, WALL_ROWS, WALL_COLUMNS, WALL_SIZE)
        );
        wall.setDrawScale(scale);
        wall.setBodyType(BodyDef.BodyType.StaticBody);
        wall.setSensor(makeSensors);
        wall.setName("wall");
        if(makeTileSized) {
            wall.setWidth(Constants.TILE_WIDTH);
            wall.setHeight(Constants.TILE_HEIGHT);
        }
        return wall;
    }

    public Wall makeWall(float x, float y, int primaryFrame, int leftFrame, int rightFrame,
                         int frontEdgeFrame, int backEdgeFrame,
                         int lowerLeftCornerFrame, int lowerRightCornerFrame, Color opacity) {

        this.opacity = opacity;
        Wall wall = new Wall(
                x,
                y,
                Constants.TILE_WIDTH,
                Constants.TILE_HEIGHT,
                new FilmStrip(wallDayTexture, WALL_ROWS, WALL_COLUMNS, WALL_SIZE),
                new FilmStrip(wallNightTexture, WALL_ROWS, WALL_COLUMNS, WALL_SIZE),
                primaryFrame,
                leftFrame,
                rightFrame,
                frontEdgeFrame,
                backEdgeFrame,
                lowerLeftCornerFrame,
                lowerRightCornerFrame,
                opacity
        );

        wall.setDrawScale(scale);
        wall.setBodyType(BodyDef.BodyType.StaticBody);
        wall.setSensor(makeSensors);
        wall.setName("wall");
        if(makeTileSized) {
            wall.setWidth(Constants.TILE_WIDTH);
            wall.setHeight(Constants.TILE_HEIGHT);
        }
        return wall;
    }

    public BorderEdge makeBorder(float x, float y, BorderEdge.Side side) {
        BorderEdge edge = new BorderEdge(
                x,
                y,
                Constants.TILE_WIDTH,
                Constants.TILE_HEIGHT,
                side,
                new FilmStrip(borderEdgeTexture, BORDER_EDGE_ROWS, BORDER_EDGE_COLUMNS),
                new FilmStrip(borderEdgeNightTexture, BORDER_EDGE_ROWS, BORDER_EDGE_COLUMNS)
        );
        edge.selectable = false;
        edge.setDrawScale(scale);
        edge.setBodyType(BodyDef.BodyType.StaticBody);
        edge.setSensor(makeSensors);
        edge.setName("edge");
        return edge;
    }

    public BorderEdge makeBorder(float x, float y, BorderEdge.Side side, int frame) {
        BorderEdge edge = new BorderEdge(
                x,
                y,
                Constants.TILE_WIDTH,
                Constants.TILE_HEIGHT,
                side,
                frame,
                new FilmStrip(borderEdgeTexture, BORDER_EDGE_ROWS, BORDER_EDGE_COLUMNS),
                new FilmStrip(borderEdgeNightTexture, BORDER_EDGE_ROWS, BORDER_EDGE_COLUMNS),
                opacity
                //new FilmStrip(borderEdgeNightTexture, BORDER_EDGE_ROWS, BORDER_EDGE_COLUMNS),
                //opacity
        );
        edge.selectable = false;
        edge.setDrawScale(scale);
        edge.setBodyType(BodyDef.BodyType.StaticBody);
        edge.setSensor(makeSensors);
        edge.setName("edge");
        return edge;
    }

    public BorderCorner makeBorderCorner(float x, float y, BorderCorner.Corner c) {
        BorderCorner corner = new BorderCorner(
                x,
                y,
                Constants.TILE_WIDTH,
                Constants.TILE_HEIGHT,
                c,
                new FilmStrip(borderCornerTexture, BORDER_CORNER_ROWS, BORDER_CORNER_COLUMNS),
                new FilmStrip(borderCornerNightTexture, BORDER_CORNER_ROWS, BORDER_CORNER_COLUMNS),
                opacity
        );
        corner.selectable = false;
        corner.setDrawScale(scale);
        corner.setBodyType(BodyDef.BodyType.StaticBody);
        corner.setSensor(makeSensors);
        corner.setName("corner");
        return corner;
    }

    public WaterTile makeWater(float x, float y) {
        return makeWater(x, y, 0);
    }

    public WaterTile makeWater(float x, float y, int frame) {
        WaterTile water = new WaterTile(
                x,
                y,
                Constants.TILE_WIDTH,
                Constants.TILE_HEIGHT
        );

        water.setTerrainStrip(new FilmStrip(waterTexture, WATER_ROWS, WATER_COLUMNS, WATER_SIZE));
        water.setTerrainNightStrip(new FilmStrip(waterNightTexture, WATER_ROWS, WATER_COLUMNS, WATER_SIZE), opacity);
        water.setCornerStrip(new FilmStrip(cornerTexture, WATER_CORNER_ROWS, WATER_CORNER_COLUMNS, WATER_CORNER_SIZE),
                             new FilmStrip(cornerNightTexture, WATER_CORNER_ROWS, WATER_CORNER_COLUMNS, WATER_CORNER_SIZE));
        water.setDrawScale(scale);
        water.setFrame(frame);
        water.setBodyType(BodyDef.BodyType.StaticBody);
        water.setSensor(makeSensors);
        water.setName("water");
        if(makeTileSized) {
            water.setWidth(Constants.TILE_WIDTH);
            water.setHeight(Constants.TILE_HEIGHT);
        }
        return water;
    }

    public SandTile makeSand(float x, float y) {
        return makeSand(x, y, 0);
    }

    public SandTile makeSand(float x, float y, int frame) {
        SandTile sand = new SandTile(
                x,
                y,
                Constants.TILE_WIDTH,
                Constants.TILE_HEIGHT
        );
        sand.setTerrainStrip(new FilmStrip(sandTexture, WATER_ROWS, WATER_COLUMNS, WATER_SIZE));
        sand.setTerrainNightStrip(new FilmStrip(sandNightTexture, WATER_ROWS, WATER_COLUMNS, WATER_SIZE), opacity);
        sand.setCornerStrip(new FilmStrip(cornerSandTexture, WATER_CORNER_ROWS, WATER_CORNER_COLUMNS, WATER_CORNER_SIZE),
                            new FilmStrip(cornerSandNightTexture, WATER_CORNER_ROWS, WATER_CORNER_COLUMNS, WATER_CORNER_SIZE));
        sand.setDrawScale(scale);
        sand.setFrame(frame);
        sand.setBodyType(BodyDef.BodyType.KinematicBody);
        sand.setSensor(makeSensors);
        sand.setName("sand");
        if(makeTileSized) {
            sand.setWidth(Constants.TILE_WIDTH);
            sand.setHeight(Constants.TILE_HEIGHT);
        }
        return sand;
    }

    public OscWall makeOscWall(float x, float y) {
        return makeOscWall(x,y,false, false);
    }

    public OscWall makeOscWall(float x, float y, boolean isVert, boolean isGoingUp) {
        OscWall oscWall;
        if(isVert) {
            oscWall = new OscWall(
                    x,
                    y,
                    Constants.TILE_WIDTH /4f,
                    Constants.TILE_HEIGHT
            );
        } else {
            oscWall = new OscWall(
                    x,
                    y,
                    Constants.TILE_WIDTH,
                    Constants.TILE_HEIGHT/4f);
        }
        oscWall.setOscWallStrips(new FilmStrip(oscWallHorzTexture, OSC_WALL_VERT_ROWS, OSC_WALL_VERT_COLS, OSC_WALL_SIZE),
                new FilmStrip(oscWallHorzGaugeTexture, OSC_WALL_VERT_ROWS, OSC_WALL_VERT_COLS, OSC_WALL_SIZE),
                new FilmStrip(oscWallVertTexture, OSC_WALL_VERT_ROWS, OSC_WALL_VERT_COLS, OSC_WALL_SIZE),
                new FilmStrip(oscWallVertGaugeTexture, OSC_WALL_VERT_ROWS, OSC_WALL_VERT_COLS, OSC_WALL_SIZE));

        oscWall.setOscWallNightStrips(new FilmStrip(oscWallHorzNightTexture, OSC_WALL_VERT_ROWS, OSC_WALL_VERT_COLS, OSC_WALL_SIZE),
                new FilmStrip(oscWallVertNightTexture, OSC_WALL_VERT_ROWS, OSC_WALL_VERT_COLS, OSC_WALL_SIZE),
                opacity);

        oscWall.setMainStrip(isVert, isGoingUp);
        oscWall.setVert(isVert);
        oscWall.setGoingUp(isGoingUp);
        oscWall.setDrawScale(scale);
        oscWall.setBodyType(BodyDef.BodyType.KinematicBody);
        oscWall.setSensor(makeSensors);
        oscWall.setName("oscWall");
        if(makeTileSized) {
            oscWall.setWidth(Constants.TILE_WIDTH);
            oscWall.setHeight(Constants.TILE_HEIGHT);
        }
        return oscWall;
    }


    public SpiritModel makeSpirit(float x, float y) {
        SpiritModel spirit = new SpiritModel(
                x,
                y,
                (spiritHeadTexture.getWidth() / (SPIRIT_COLUMNS * 12)) / scale.x,
                (spiritHeadTexture.getHeight() / (SPIRIT_ROWS * 4)) / scale.y,
                SPIRIT_LIVES,
                SPIRIT_BOUNCES,
                DEFAULT_LIFE
        );
        spirit.setDrawScale(scale);
//        spirit.setFilmStrip(new FilmStrip(spiritBodyTexture, SPIRIT_ROWS, SPIRIT_COLUMNS, SPIRIT_SIZE),
//                new FilmStrip(spiritHeadTexture, SPIRIT_ROWS, SPIRIT_COLUMNS, SPIRIT_SIZE),
//                new FilmStrip(spiritTailTexture, SPIRIT_ROWS, SPIRIT_COLUMNS, SPIRIT_SIZE));
        spirit.setFilmStrip(
                new FilmStrip(spiritHeadTexture, SPIRIT_ROWS, SPIRIT_COLUMNS, SPIRIT_SIZE),
                new FilmStrip(spiritTailTexture, SPIRIT_ROWS, SPIRIT_COLUMNS, SPIRIT_SIZE));
        spirit.setSensor(makeSensors);
        return spirit;
    }

    public HostModel makeSmallHost(float x, float y) {
        return makeSmallHost(x, y, null, 0);
    }

    public HostModel makeSmallHost(float x, float y, Vector2[] instructions, int currentCharge) {
        return makeHostInternal(x, y, instructions, SMALL_MAX_CHARGE, currentCharge);
    }

    public HostModel makePedestal(float x, float y) {
        return makePedestalInternal(x, y, pedestalTexture, pedestalNightTexture);
    }

    // TODO: add medium and large host make functions

    private HostModel makePedestalInternal(float x, float y, Texture pedestalTexture, Texture pedestalNightTexture) {
        HostModel ped = new HostModel(
                x,
                y,
                Constants.TILE_WIDTH,
                Constants.TILE_HEIGHT,
                true
        );
        ped.setDrawScale(scale);
        ped.setPedestalStrip(new FilmStrip(pedestalTexture, PEDESTAL_ROWS, PEDESTAL_COLS, PEDESTAL_SIZE));
        ped.setPedestalNightStrip(new FilmStrip(pedestalNightTexture, PEDESTAL_ROWS, PEDESTAL_COLS, PEDESTAL_SIZE), opacity);
        ped.setName("pedestal");
        ped.setSensor(makeSensors);
        if(makeTileSized) {
            ped.setWidth(Constants.TILE_WIDTH);
            ped.setHeight(Constants.TILE_HEIGHT);
        }
        return ped;
    }

    private HostModel makeHostInternal(float x, float y, Vector2[] instructions, int maxCharge, int currentCharge) {
        HostModel host = new HostModel(
                x,
                y,
                // TODO Check that this is right
                Constants.TILE_WIDTH * 0.9f,
                // TODO Check that this is right
                Constants.TILE_HEIGHT * 0.9f,
                0,
                maxCharge,
                instructions
        );
        host.setDrawScale(scale);
        host.setChargeStrip(new FilmStrip(hostChargeTexture, CHARGE_ROWS, CHARGE_COLUMNS, CHARGE_SIZE), currentCharge);
        host.setHostShadow(hostShadowRegion);
        host.setHostStrip(new FilmStrip(hostTextureE, HOST_ROWS, HOST_COLUMNS, HOST_SIZE), new FilmStrip(hostTextureN, HOST_ROWS, HOST_COLUMNS, HOST_SIZE),
                new FilmStrip(hostTextureNE, HOST_ROWS, HOST_COLUMNS, HOST_SIZE), new FilmStrip(hostTextureNW, HOST_ROWS, HOST_COLUMNS, HOST_SIZE),
                new FilmStrip(hostTextureS, HOST_ROWS, HOST_COLUMNS, HOST_SIZE), new FilmStrip(hostTextureSE, HOST_ROWS, HOST_COLUMNS, HOST_SIZE),
                new FilmStrip(hostTextureSW, HOST_ROWS, HOST_COLUMNS, HOST_SIZE), new FilmStrip(hostTextureW, HOST_ROWS, HOST_COLUMNS, HOST_SIZE),
                new FilmStrip(glyphTextureE, HOST_ROWS, HOST_COLUMNS, HOST_SIZE), new FilmStrip(glyphTextureN, HOST_ROWS, HOST_COLUMNS, HOST_SIZE),
                new FilmStrip(glyphTextureNE, HOST_ROWS, HOST_COLUMNS, HOST_SIZE), new FilmStrip(glyphTextureNW, HOST_ROWS, HOST_COLUMNS, HOST_SIZE),
                new FilmStrip(glyphTextureS, HOST_ROWS, HOST_COLUMNS, HOST_SIZE), new FilmStrip(glyphTextureSE, HOST_ROWS, HOST_COLUMNS, HOST_SIZE),
                new FilmStrip(glyphTextureSW, HOST_ROWS, HOST_COLUMNS, HOST_SIZE), new FilmStrip(glyphTextureW, HOST_ROWS, HOST_COLUMNS, HOST_SIZE),
                new FilmStrip(hostDeathTextureE, HOST_COLUMNS, HOST_ROWS, HOST_SIZE), new FilmStrip(hostDeathTextureN, HOST_COLUMNS, HOST_ROWS, HOST_SIZE),
                new FilmStrip(hostDeathTextureNE, HOST_COLUMNS, HOST_ROWS, HOST_SIZE), new FilmStrip(hostDeathTextureNW, HOST_COLUMNS, HOST_ROWS, HOST_SIZE),
                new FilmStrip(hostDeathTextureS, HOST_COLUMNS, HOST_ROWS, HOST_SIZE), new FilmStrip(hostDeathTextureSE, HOST_COLUMNS, HOST_ROWS, HOST_SIZE),
                new FilmStrip(hostDeathTextureSW, HOST_COLUMNS, HOST_ROWS, HOST_SIZE), new FilmStrip(hostDeathTextureW, HOST_COLUMNS, HOST_ROWS, HOST_SIZE),
                new FilmStrip(hostArmTexture, HOST_ARMS_ROWS, HOST_ARMS_COLS, HOST_ARMS_SIZE));
        host.setJuiceStrips(new FilmStrip(hostGenPossession, POSSESSION_ROWS, POSSESSION_COLS, POSSESSION_SIZE),
                new FilmStrip(hostNewPossession, POSSESSION_ROWS, POSSESSION_COLS, POSSESSION_SIZE),
                new FilmStrip(hostWakingUp, HOST_WAKE_UP_ROWS, HOST_WAKE_UP_COLS, HOST_WAKE_UP_SIZE));
        host.setCurrentCharge(currentCharge);
        host.setName("host");
        host.setSensor(makeSensors);
        if(makeTileSized) {
            host.setWidth(Constants.TILE_WIDTH);
            host.setHeight(Constants.TILE_HEIGHT);
        }
        return host;
    }
}