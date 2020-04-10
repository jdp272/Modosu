package edu.cornell.gdiac.physics.host;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.util.FilmStrip;

public class PedestalModel extends HostModel {

    /** The texture filmstrip for host that has yet to be possessed */
    FilmStrip pedstalStrip;

    /** The frame number for the pedestal starting position */
    public static final int PEDESTAL_BASE_FRAME = 4;

    /**
     * Boolean for whether host is a pedestal
     */
    private boolean isPedestal;

    /**
     * Method for getting whether host is pedestal or not
     * @return true if host object is pedestal
     */
    public boolean isPedestal() {
        return isPedestal;
    }

    /**
     * Method for setting whether host is pedestal or not
     * @param pedestal boolean stating whether host is pedestal
     */
    public void setPedestal(boolean pedestal) {
        isPedestal = pedestal;
    }

    /**
     * Creates a new Pedestal at the Origin.
     * @param width
     * @param height
     * @param currentCharge
     * @param maxCharge
     */
    public PedestalModel(float width, float height, float currentCharge, float maxCharge) {
        super(width, height, currentCharge, maxCharge);
    }

    public PedestalModel(float x, float y, float width, float height, float currentCharge, float maxCharge) {
        super(x, y, width, height, currentCharge, maxCharge);
    }

    public PedestalModel(float x, float y, float width, float height, float currentCharge, float maxCharge, Vector2[] ins) {
        super(x, y, width, height, currentCharge, maxCharge, ins);
    }
}
