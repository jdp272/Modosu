package edu.cornell.gdiac.physics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.host.HostModel;

import com.badlogic.gdx.math.Vector2;

public class Factory {
    private static int SMALL_MAX_CHARGE = 1000;
    private static int SPIRIT_BOUNCES = 10;

    private TextureRegion obstacleTex;
    private TextureRegion spiritTex;
    private TextureRegion smallHostTex;
    private TextureRegion smallHostGaugeTex;

    private Vector2 scale;

    public Factory(
            Vector2 scale,
            TextureRegion obstacleTex,
            TextureRegion spiritTex,
            TextureRegion smallHostTex,
            TextureRegion smallHostGaugeTex
    ) {
        this.scale = scale;
        this.obstacleTex = obstacleTex;
        this.spiritTex = spiritTex;
        this.smallHostTex = smallHostTex;
        this.smallHostGaugeTex = smallHostGaugeTex;
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
        return box;
    }

    public SpiritModel makeSpirit(float x, float y) {
        SpiritModel spirit = new SpiritModel(
                x,
                y,
                spiritTex.getRegionWidth() / scale.x,
                spiritTex.getRegionHeight() / scale.y,
                SPIRIT_BOUNCES
        );
        spirit.setDrawScale(scale);
        spirit.setTexture(spiritTex);
        return spirit;
    }

    public HostModel makeSmallHost(float x, float y) {
        return makeHostInternal(x, y, SMALL_MAX_CHARGE, smallHostTex, smallHostGaugeTex);
    }

    // TODO: add medium and large host make functions

    private HostModel makeHostInternal(float x, float y, int maxCharge, TextureRegion hostTex, TextureRegion gaugeTex) {
        HostModel host = new HostModel(
                x,
                y,
                hostTex.getRegionWidth() / scale.x,
                hostTex.getRegionHeight() / scale.y,
                0,
                maxCharge
        );
        host.setDrawScale(scale);
        host.setTexture(smallHostTex);
        host.setHostGaugeTexture(smallHostGaugeTex);
        return host;
    }
}
