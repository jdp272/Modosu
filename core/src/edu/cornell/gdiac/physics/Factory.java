package edu.cornell.gdiac.physics;

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
    private static int DEFAULT_LIFE = 300;

    private TextureRegion obstacleTex;
    private TextureRegion smallHostTex;
    private Texture smallHostGaugeTexture;
    private Texture smallHostTexture;
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

    /** Static Variables for Sprite Sheet */

    /** Number of rows in the spirit image filmstrip */
    private static final int SPIRIT_ROWS =  35;
    /** Number of columns in the spirit image filmstrip */
    private static final int SPIRIT_COLUMNS = 4;
    /** Total Number of frames in the spirit image filmstrip */
    private static final int SPIRIT_SIZE = 140;

    /** Number of rows in the host image filmstrip */
    private static final int HOST_ROWS = 8;
    /** Number of columns in this host image filmstrip */
    private static final int HOST_COLUMNS = 16;
    /** Number of total hosts in the host image filmstrip */
    private static final int HOST_SIZE = 128;

    /** Number of rows in the wall image filmstrip */
    private static final int WALL_ROWS = 4;
    /** Number of columns in this wall image filmstrip */
    private static final int WALL_COLUMNS = 8;
    /** Number of total hosts in the wall image filmstrip */
    private static final int WALL_SIZE = 32;

    /** Number of rows in the water image filmstrip */
    private static final int WATER_ROWS = 4;
    /** Number of columns in the water image filmstrip */
    private static final int WATER_COLUMNS = 4;
    /** Number of total hosts in the water image filmstrip */
    private static final int WATER_SIZE = 16;

    /** Number of rows in the water corner image filmstrip */
    private static final int WATER_CORNER_ROWS = 2;
    /** Number of columns in the water corner image filmstrip */
    private static final int WATER_CORNER_COLUMNS = 2;
    /** Number of total hosts in the water corner image filmstrip */
    private static final int WATER_CORNER_SIZE = 4;

    /** Number of rows in the pedestal image filmstrip */
    private static final int PEDESTAL_ROWS = 1;
    /** Number of columns in the pedestal image filmstrip */
    private static final int PEDESTAL_COLS = 4;
    /** Number of total pedestals in the pedestal image filmstrip */
    private static final int PEDESTAL_SIZE = 4;

    /** Number of rows in the border edge image filmstrip */
    private static final int BORDER_EDGE_ROWS = 4;
    /** Number of columns in the border edge image filmstrip */
    private static final int BORDER_EDGE_COLS = 9;

    /** Number of rows in the border corner image filmstrip */
    private static final int BORDER_CORNER_ROWS = 2;
    /** Number of columns in the border corner image filmstrip */
    private static final int BORDER_CORNER_COLS = 2;


    /** The draw scale of objects */
    private Vector2 scale;

    /** Can be set. If true, instantiated objects are sensors */
    public boolean makeSensors;

    public Factory(
            Vector2 scale,
            TextureRegion obstacleTex,
            Texture spiritBodyTexture,
            Texture spiritHeadTexture,
            Texture spiritTailTexture,
            TextureRegion smallHostTex,
            Texture smallHostTexture,
            Texture smallHostGaugeTexture,
            Texture wallTexture,
            Texture waterTexture,
            Texture cornerTexture,
            Texture sandTexture,
            Texture cornerSandTexture,
            Texture pedestalTexture,
            Texture borderEdgeTexture,
            Texture borderCornerTexture
    ) {
        this.scale = scale;
        this.obstacleTex = obstacleTex;
        this.spiritBodyTexture = spiritBodyTexture;
        this.spiritHeadTexture = spiritHeadTexture;
        this.spiritTailTexture = spiritTailTexture;
        this.smallHostTex = smallHostTex;
        this.smallHostTexture = smallHostTexture;
        this.smallHostGaugeTexture = smallHostGaugeTexture;
        this.wallTexture = wallTexture;
        this.waterTexture = waterTexture;
        this.cornerTexture = cornerTexture;
        this.sandTexture = sandTexture;
        this.cornerSandTexture = cornerSandTexture;
        this.pedestalTexture = pedestalTexture;
        this.borderEdgeTexture = borderEdgeTexture;
        this.borderCornerTexture = borderCornerTexture;
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
        wall.setSX(0.25f);
        wall.setSY(0.25f);
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
                64 / scale.x,
                64 / scale.y,
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
        wall.setSX(0.25f);
        wall.setSY(0.25f);
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
                obstacleTex.getRegionWidth() / scale.x,
                obstacleTex.getRegionHeight() / scale.y
        );
        water.setWaterStrip(new FilmStrip(waterTexture, WATER_ROWS, WATER_COLUMNS, WATER_SIZE));
        water.setCornerStrip(new FilmStrip(cornerTexture, WATER_CORNER_ROWS, WATER_CORNER_COLUMNS, WATER_CORNER_SIZE));
        water.setDrawScale(scale);
        water.setSX(0.25f);
        water.setSY(0.25f);
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
                obstacleTex.getRegionWidth() / scale.x,
                obstacleTex.getRegionHeight() / scale.y
        );
        sand.setSandtrip(new FilmStrip(sandTexture, WATER_ROWS, WATER_COLUMNS, WATER_SIZE));
        sand.setCornerStrip(new FilmStrip(cornerSandTexture, WATER_CORNER_ROWS, WATER_CORNER_COLUMNS, WATER_CORNER_SIZE));
        sand.setDrawScale(scale);
        sand.setSX(0.25f);
        sand.setSY(0.25f);
        sand.setFrame(frame);
        sand.setBodyType(BodyDef.BodyType.KinematicBody);
        sand.setSensor(makeSensors);
        sand.setName("sand");
        return sand;
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
        spirit.setFilmStrip(new FilmStrip(spiritBodyTexture,  SPIRIT_ROWS, SPIRIT_COLUMNS, SPIRIT_SIZE), new FilmStrip(spiritHeadTexture, SPIRIT_ROWS, SPIRIT_COLUMNS, SPIRIT_SIZE), new FilmStrip(spiritTailTexture, SPIRIT_ROWS, SPIRIT_COLUMNS, SPIRIT_SIZE));
        spirit.setSensor(makeSensors);
        return spirit;
    }

    public HostModel makeSmallHost(float x, float y) {
        return makeSmallHost(x, y, null);
    }

    public HostModel makeSmallHost(float x, float y, Vector2[] instructions) {
        return makeHostInternal(x, y, instructions, SMALL_MAX_CHARGE, smallHostTex, smallHostGaugeTexture, smallHostTexture);
    }

    public HostModel makePedestal(float x, float y) {
        return makePedestalInternal(x, y, pedestalTexture);
    }

    // TODO: add medium and large host make functions

    private HostModel makePedestalInternal(float x, float y, Texture pedestalTexture) {
        HostModel ped = new HostModel(
                x,
                y,
                ((pedestalTexture.getWidth() / 4) / scale.x),
                (pedestalTexture.getHeight()  * 2/ scale.y),
                true
        );
        ped.setDrawScale(scale);
        ped.setPedestalStrip(new FilmStrip(pedestalTexture, PEDESTAL_ROWS, PEDESTAL_COLS, PEDESTAL_SIZE));
        ped.setSensor(makeSensors);
        return ped;
    }

    private HostModel makeHostInternal(float x, float y, Vector2[] instructions, int maxCharge, TextureRegion hostTex, Texture smallHostGaugeTexture, Texture hostTexture) {
        HostModel host = new HostModel(
                x,
                y,
                // TODO Check that this is right
                (hostTex.getRegionWidth()*(1f / 32f)) / scale.x,
                // TODO Check that this is right
                (hostTex.getRegionHeight() * (1f / 16f)) / scale.y,
                0,
                maxCharge,
                instructions
        );
        host.setDrawScale(scale);
        host.setHostGaugeTexture(new FilmStrip(smallHostGaugeTexture, HOST_ROWS, HOST_COLUMNS, HOST_SIZE));
        host.setChargedHostStrip(new FilmStrip(hostTexture, HOST_ROWS, HOST_COLUMNS, HOST_SIZE));
        host.setNotChargedHostStrip(new FilmStrip(hostTexture, HOST_ROWS, HOST_COLUMNS, HOST_SIZE));
        host.setHostStateSprite(host.beenPossessed(), new FilmStrip(hostTexture, HOST_ROWS, HOST_COLUMNS, HOST_SIZE), new Vector2(0, -1));
        host.setSensor(makeSensors);
        return host;
    }
}