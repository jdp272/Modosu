package edu.cornell.gdiac.physics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.host.HostModel;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.util.FilmStrip;

public class Factory {
    private static int SMALL_MAX_CHARGE = 1000;
    private static int SPIRIT_BOUNCES = 10;
    private static int DEFAULT_LIFE = 400;

    private TextureRegion obstacleTex;
    private TextureRegion spiritTex;
    private TextureRegion smallHostTex;
    private Texture smallHostGaugeTexture;
    private Texture smallHostTexture;

    /** Static Variables for Sprite Sheet */

    /** Number of rows in the host image filmstrip */
    private static final int HOST_ROWS = 8;
    /** Number of columns in this host image filmstrip */
    private static final int HOST_COLUMNS = 16;
    /** Number of total hosts in the host image filmstrip */
    private static final int HOST_SIZE = 128;
    /** Track asset loading from all instances and subclasses */

    /** The draw scale of objects */
    private Vector2 scale;

    /** Can be set. If true, instantiated objects are sensors */
    public boolean makeSensors;

    public Factory(
            Vector2 scale,
            TextureRegion obstacleTex,
            TextureRegion spiritTex,
            TextureRegion smallHostTex,
            Texture smallHostTexture,
            Texture smallHostGaugeTexture
    ) {
        this.scale = scale;
        this.obstacleTex = obstacleTex;
        this.spiritTex = spiritTex;
        this.smallHostTex = smallHostTex;
        this.smallHostTexture = smallHostTexture;
        this.smallHostGaugeTexture = smallHostGaugeTexture;
    }

    public BoxObstacle makeObstacle(float x, float y) {
        BoxObstacle box = new BoxObstacle(
                x,
                y,
                obstacleTex.getRegionWidth() / scale.x,
                obstacleTex.getRegionWidth() / scale.y
        );
        box.setDrawScale(scale);
        box.setTexture(obstacleTex);
        box.setBodyType(BodyDef.BodyType.StaticBody);
        box.setSensor(makeSensors);
        return box;
    }

    public SpiritModel makeSpirit(float x, float y) {
        SpiritModel spirit = new SpiritModel(
                x,
                y,
                spiritTex.getRegionWidth() / scale.x,
                spiritTex.getRegionHeight() / scale.y,
                SPIRIT_BOUNCES,
                DEFAULT_LIFE
        );
        spirit.setDrawScale(scale);
        spirit.setTexture(spiritTex);
        spirit.setSensor(makeSensors);
        return spirit;
    }

    public HostModel makeSmallHost(float x, float y) {
        return makeSmallHost(x, y, null);
    }

    public HostModel makeSmallHost(float x, float y, Vector2[] instructions) {
        return makeHostInternal(x, y, instructions, SMALL_MAX_CHARGE, smallHostTex, smallHostGaugeTexture, smallHostTexture);
    }

    // TODO: add medium and large host make functions

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
        host.setHostStateSprite(host.beenPossessed(), new FilmStrip(hostTexture, HOST_ROWS, HOST_COLUMNS, HOST_SIZE), new Vector2(1, 1));
        host.setSensor(makeSensors);
        return host;
    }
}
