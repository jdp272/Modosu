package edu.cornell.gdiac.physics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import edu.cornell.gdiac.physics.host.HostModel;

public class Factory {
    private static int SMALL_MAX_CHARGE = 1000;

    private TextureRegion smallHostTex;
    private TextureRegion smallHostGaugeTex;

    public Factory(TextureRegion smallHostTex, TextureRegion smallHostGaugeTex) {
        this.smallHostTex = smallHostTex;
        this.smallHostGaugeTex = smallHostGaugeTex;
    }

    public HostModel makeSmallHost(float x, float y) {
        return makeHostInternal(x, y, SMALL_MAX_CHARGE, smallHostTex, smallHostGaugeTex);
    }

    private HostModel makeHostInternal(float x, float y, int maxCharge, TextureRegion hostTex, TextureRegion gaugeTex) {
        HostModel host = new HostModel(x, y, hostTex.getRegionWidth(), hostTex.getRegionHeight(), 0, maxCharge);
        host.setTexture(smallHostTex);
        host.setHostGaugeTexture(smallHostGaugeTex);
        return host;
    }
}
