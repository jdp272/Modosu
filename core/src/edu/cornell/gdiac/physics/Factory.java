package edu.cornell.gdiac.physics;

import com.badlogic.gdx.graphics.Texture;
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
    private Texture hostTextureE;
    private Texture hostTextureN;
    private Texture hostTextureNE;
    private Texture hostTextureNW;
    private Texture hostTextureS;
    private Texture hostTextureSE;
    private Texture hostTextureSW;
    private Texture hostTextureW;
    private Texture wallTexture;
    private Texture waterTexture;
    private Texture cornerTexture;
    private Texture sandTexture;
    private Texture cornerSandTexture;
    private Texture pedestalTexture;
    private Texture spiritBodyTexture;
    private Texture spiritHeadTexture;
    private Texture spiritTailTexture;
    private Texture borderEdgeTexture;
    private Texture borderCornerTexture;
    private Texture energyPillarBodyTexture;
    private Texture energyPillarBodyChargeTexture;
    private Texture energyPillarRadiusTexture;
    private Texture oscWallVertTexture;
    private Texture oscWallVertGaugeTexture;
    private Texture oscWallHorzTexture;
    private Texture oscWallHorzGaugeTexture;

    /** Static Variables for Sprite Sheet */

    /**
     * Number of rows in the energy pillar filmstrip
     */
    private static final int ENERGY_PILLAR_ROWS = 1;
    /**
     * Number of columns in the energy pillar filmstrip
     */
    private static final int ENERGY_PILLAR_COLUMNS = 1;
    /**
     * Number of frames in the energy pillar filmstrip
     */
    private static final int ENERGY_PILLAR_SIZE = 1;


    /**
     * Number of rows in the spirit image filmstrip
     */
    private static final int SPIRIT_ROWS = 35;
    /**
     * Number of columns in the spirit image filmstrip
     */
    private static final int SPIRIT_COLUMNS = 4;
    /**
     * Total Number of frames in the spirit image filmstrip
     */
    private static final int SPIRIT_SIZE = 140;


    /**
     * Number of rows in the host image filmstrip
     */
    private static final int HOST_ROWS = 6;
    /**
     * Number of columns in this host image filmstrip
     */
    private static final int HOST_COLUMNS = 10;
    /**
     * Number of total hosts in the host image filmstrip
     */
    private static final int HOST_SIZE = 60;


    /**
     * Number of rows in the charge image filmstrip
     */
    private static final int CHARGE_ROWS = 4;
    /**
     * Number of columns in the charge image filmstrip
     */
    private static final int CHARGE_COLUMNS = 8;
    /**
     * Number of total frames in the charge image filmstrip
     */
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
    /**
     * Number of columns in the water image filmstrip
     */
    private static final int WATER_COLUMNS = 4;
    /**
     * Number of total hosts in the water image filmstrip
     */
    private static final int WATER_SIZE = 16;


    /**
     * Number of rows in the water corner image filmstrip
     */
    private static final int WATER_CORNER_ROWS = 2;
    /**
     * Number of columns in the water corner image filmstrip
     */
    private static final int WATER_CORNER_COLUMNS = 2;
    /**
     * Number of total hosts in the water corner image filmstrip
     */
    private static final int WATER_CORNER_SIZE = 4;


    /**
     * Number of rows in the pedestal image filmstrip
     */
    private static final int PEDESTAL_ROWS = 1;
    /**
     * Number of columns in the pedestal image filmstrip
     */
    private static final int PEDESTAL_COLS = 4;
    /**
     * Number of total pedestals in the pedestal image filmstrip
     */
    private static final int PEDESTAL_SIZE = 4;


    /** Number of rows in the border edge image filmstrip */
    private static final int BORDER_EDGE_ROWS = 4;
    /** Number of columns in the border edge image filmstrip */
    private static final int BORDER_EDGE_COLS = 9;
    /** Number of rows in the border corner image filmstrip */
    private static final int BORDER_CORNER_ROWS = 2;
    /** Number of columns in the border corner image filmstrip */
    private static final int BORDER_CORNER_COLS = 2;


    /** Number of rows in the oscwall image filmstrip */
    private static final int OSC_WALL_VERT_ROWS = 6;
    /** Number of cols in the oscwall image filmstrip */
    private static final int OSC_WALL_VERT_COLS = 10;
    /** Number of total frames in the oscwall image filmstrip */
    private static final int OSC_WALL_SIZE = 60;


    /** The draw scale of objects */
    private Vector2 scale;


    /**
     * Can be set. If true, instantiated objects are sensors
     */
    public boolean makeSensors;

    public Factory(
            Vector2 scale,
            Texture spiritBodyTexture,
            Texture spiritHeadTexture,
            Texture spiritTailTexture,
            Texture hostChargeTexture,
            Texture hostTextureE,
            Texture hostTextureN,
            Texture hostTextureNE,
            Texture hostTextureNW,
            Texture hostTextureS,
            Texture hostTextureSE,
            Texture hostTextureSW,
            Texture hostTextureW,
            Texture wallTexture,
            Texture waterTexture,
            Texture cornerTexture,
            Texture sandTexture,
            Texture cornerSandTexture,
            Texture pedestalTexture,
            Texture borderEdgeTexture,
            Texture borderCornerTexture,
            Texture energyPillarBodyTexture,
            Texture energyPillarBodyChargeTexture,
            Texture energyPillarRadiusTexture,
            Texture oscWallVertTexture,
            Texture oscWallVertGaugeTexture,
            Texture oscWallHorzTexture,
            Texture oscWallHorzGaugeTexture

    ) {
        this.scale = scale;
        this.spiritBodyTexture = spiritBodyTexture;
        this.spiritHeadTexture = spiritHeadTexture;
        this.spiritTailTexture = spiritTailTexture;
        this.hostChargeTexture = hostChargeTexture;
        this.hostTextureE = hostTextureE;
        this.hostTextureN = hostTextureN;
        this.hostTextureNE = hostTextureNE;
        this.hostTextureNW = hostTextureNW;
        this.hostTextureS = hostTextureS;
        this.hostTextureSE = hostTextureSE;
        this.hostTextureSW = hostTextureSW;
        this.hostTextureW = hostTextureW;
        this.wallTexture = wallTexture;
        this.waterTexture = waterTexture;
        this.cornerTexture = cornerTexture;
        this.sandTexture = sandTexture;
        this.cornerSandTexture = cornerSandTexture;
        this.pedestalTexture = pedestalTexture;
        this.borderEdgeTexture = borderEdgeTexture;
        this.borderCornerTexture = borderCornerTexture;
        this.energyPillarBodyChargeTexture = energyPillarBodyChargeTexture;
        this.energyPillarBodyTexture = energyPillarBodyTexture;
        this.energyPillarRadiusTexture = energyPillarRadiusTexture;
        this.oscWallVertTexture = oscWallVertTexture;
        this.oscWallVertGaugeTexture = oscWallVertGaugeTexture;
        this.oscWallHorzTexture = oscWallHorzTexture;
        this.oscWallHorzGaugeTexture = oscWallHorzGaugeTexture;
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
                64 / scale.x,
                64 / scale.y,
                new FilmStrip(wallTexture, WALL_ROWS, WALL_COLUMNS, WALL_SIZE)
        );
        wall.setDrawScale(scale);
        wall.setSX(0.26f);
        wall.setSY(0.26f);
        wall.setBodyType(BodyDef.BodyType.StaticBody);
        wall.setSensor(makeSensors);
        wall.setName("wall");
        return wall;
    }

    public Wall makeWall(float x, float y, int primaryFrame, int leftFrame, int rightFrame,
                         int frontEdgeFrame, int backEdgeFrame,
                         int lowerLeftCornerFrame, int lowerRightCornerFrame) {
        Wall wall = new Wall(
                x,
                y,
                Constants.TILE_WIDTH,
                Constants.TILE_HEIGHT,
                new FilmStrip(wallTexture, WALL_ROWS, WALL_COLUMNS, WALL_SIZE),
                primaryFrame,
                leftFrame,
                rightFrame,
                frontEdgeFrame,
                backEdgeFrame,
                lowerLeftCornerFrame,
                lowerRightCornerFrame
        );
        wall.setDrawScale(scale);
        wall.setSX(0.26f);
        wall.setSY(0.26f);
        wall.setBodyType(BodyDef.BodyType.StaticBody);
        wall.setSensor(makeSensors);
        wall.setName("wall");
        return wall;
    }

    public BorderEdge makeBorder(float x, float y, BorderEdge.Side side) {
        BorderEdge edge = new BorderEdge(
                x,
                y,
                64 / scale.x,
                64 / scale.y,
                side,
                new FilmStrip(borderEdgeTexture, BORDER_EDGE_ROWS, BORDER_EDGE_COLS)
        );
        edge.selectable = false;
        edge.setDrawScale(scale);
        edge.setSX(0.25f);
        edge.setSY(0.25f);
        edge.setBodyType(BodyDef.BodyType.StaticBody);
        edge.setSensor(makeSensors);
        edge.setName("edge");
        return edge;
    }

    public BorderEdge makeBorder(float x, float y, BorderEdge.Side side, int frame) {
        BorderEdge edge = new BorderEdge(
                x,
                y,
                64 / scale.x,
                64 / scale.y,
                side,
                frame,
                new FilmStrip(borderEdgeTexture, BORDER_EDGE_ROWS, BORDER_EDGE_COLS)
        );
        edge.selectable = false;
        edge.setDrawScale(scale);
        edge.setSX(0.25f);
        edge.setSY(0.25f);
        edge.setBodyType(BodyDef.BodyType.StaticBody);
        edge.setSensor(makeSensors);
        edge.setName("edge");
        return edge;
    }

    public BorderCorner makeBorderCorner(float x, float y, BorderCorner.Corner c) {
        BorderCorner corner = new BorderCorner(
                x,
                y,
                64 / scale.x,
                64 / scale.y,
                c,
                new FilmStrip(borderCornerTexture, BORDER_CORNER_ROWS, BORDER_CORNER_COLS)
        );
        corner.selectable = false;
        corner.setDrawScale(scale);
        corner.setSX(0.25f);
        corner.setSY(0.25f);
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
        water.setCornerStrip(new FilmStrip(cornerTexture, WATER_CORNER_ROWS, WATER_CORNER_COLUMNS, WATER_CORNER_SIZE));
        water.setDrawScale(scale);
        water.setSX(0.26f);
        water.setSY(0.26f);
        water.setFrame(frame);
        water.setBodyType(BodyDef.BodyType.StaticBody);
        water.setSensor(makeSensors);
        water.setName("water");
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
        sand.setCornerStrip(new FilmStrip(cornerSandTexture, WATER_CORNER_ROWS, WATER_CORNER_COLUMNS, WATER_CORNER_SIZE));
        sand.setDrawScale(scale);
        sand.setSX(0.26f);
        sand.setSY(0.26f);
        sand.setFrame(frame);
        sand.setBodyType(BodyDef.BodyType.KinematicBody);
        sand.setSensor(makeSensors);
        sand.setName("sand");
        return sand;
    }

    public OscWall makeOscWall(float x, float y) {
        return makeOscWall(x,y,false, false);
    }

    public OscWall makeOscWall(float x, float y, boolean isVert, boolean isGoingUp) {
        OscWall oscWall = new OscWall(
                x,
                y,
                Constants.TILE_WIDTH,
                Constants.TILE_HEIGHT / 4f
        );
        oscWall.setOscWallStrips(new FilmStrip(oscWallHorzTexture, OSC_WALL_VERT_ROWS, OSC_WALL_VERT_COLS, OSC_WALL_SIZE), new FilmStrip(oscWallHorzGaugeTexture, OSC_WALL_VERT_ROWS, OSC_WALL_VERT_COLS, OSC_WALL_SIZE), new FilmStrip(oscWallVertTexture, OSC_WALL_VERT_ROWS, OSC_WALL_VERT_COLS, OSC_WALL_SIZE), new FilmStrip(oscWallVertGaugeTexture, OSC_WALL_VERT_ROWS, OSC_WALL_VERT_COLS, OSC_WALL_SIZE));
        oscWall.setMainStrip(isVert, isGoingUp);
        oscWall.setVert(isVert);
        oscWall.setGoingUp(isGoingUp);
        oscWall.setDrawScale(scale);
        oscWall.setBodyType(BodyDef.BodyType.KinematicBody);
        oscWall.setSensor(makeSensors);
        oscWall.setName("oscWall");
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
        spirit.setFilmStrip(new FilmStrip(spiritBodyTexture, SPIRIT_ROWS, SPIRIT_COLUMNS, SPIRIT_SIZE), new FilmStrip(spiritHeadTexture, SPIRIT_ROWS, SPIRIT_COLUMNS, SPIRIT_SIZE), new FilmStrip(spiritTailTexture, SPIRIT_ROWS, SPIRIT_COLUMNS, SPIRIT_SIZE));
        spirit.setSensor(makeSensors);
        return spirit;
    }

    public HostModel makeSmallHost(float x, float y) {
        return makeSmallHost(x, y, null, 0);
    }

    public HostModel makeSmallHost(float x, float y, Vector2[] instructions, int currentCharge) {
        return makeHostInternal(x, y, instructions, SMALL_MAX_CHARGE, currentCharge, hostTextureE, hostTextureN, hostTextureNE, hostTextureNW, hostTextureS, hostTextureSE, hostTextureSW, hostTextureW, hostChargeTexture);
    }

    public HostModel makePedestal(float x, float y) {
        return makePedestalInternal(x, y, pedestalTexture);
    }

    // TODO: add medium and large host make functions

    private HostModel makePedestalInternal(float x, float y, Texture pedestalTexture) {
        HostModel ped = new HostModel(
                x,
                y,
                Constants.TILE_WIDTH,
                Constants.TILE_HEIGHT,
                true
        );
        ped.setDrawScale(scale);
        ped.setPedestalStrip(new FilmStrip(pedestalTexture, PEDESTAL_ROWS, PEDESTAL_COLS, PEDESTAL_SIZE));
        ped.setName("pedestal");
        ped.setSensor(makeSensors);
        return ped;
    }

    private HostModel makeHostInternal(float x, float y, Vector2[] instructions, int maxCharge, int currentCharge, Texture hostTextureE, Texture hostTextureN, Texture hostTextureNE, Texture hostTextureNW, Texture hostTextureS, Texture hostTextureSE, Texture hostTextureSW, Texture hostTextureW, Texture hostChargeTexture) {
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
        host.setHostStrip(new FilmStrip(hostTextureE, HOST_ROWS, HOST_COLUMNS, HOST_SIZE), new FilmStrip(hostTextureN, HOST_ROWS, HOST_COLUMNS, HOST_SIZE),
                new FilmStrip(hostTextureNE, HOST_ROWS, HOST_COLUMNS, HOST_SIZE), new FilmStrip(hostTextureNW, HOST_ROWS, HOST_COLUMNS, HOST_SIZE),
                new FilmStrip(hostTextureS, HOST_ROWS, HOST_COLUMNS, HOST_SIZE), new FilmStrip(hostTextureSE, HOST_ROWS, HOST_COLUMNS, HOST_SIZE),
                new FilmStrip(hostTextureSW, HOST_ROWS, HOST_COLUMNS, HOST_SIZE), new FilmStrip(hostTextureW, HOST_ROWS, HOST_COLUMNS, HOST_SIZE));
        host.setCurrentCharge(currentCharge);
        host.setName("host");
        host.setSensor(makeSensors);
        return host;
    }
}