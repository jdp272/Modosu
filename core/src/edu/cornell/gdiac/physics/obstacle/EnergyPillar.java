package edu.cornell.gdiac.physics.obstacle;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.util.FilmStrip;

public class EnergyPillar extends BoxObstacle {

    /** The texture strip for the Energy Pillar */
    protected FilmStrip EnergyPillarStrip;

    /** The texture strip for the glowing of the Pillar */
    protected FilmStrip EnergyPillarRune;

    /** The texture strip for the radius of the Energy Pillar */
    protected FilmStrip EnergyPillarFieldStrip;


    public EnergyPillar(float width, float height) {
        this(0, 0, width, height);
    }

    public EnergyPillar(float x, float y, float width, float height) {
        super(x, y, width, height);
    }


    /**
     * sets the FilmStrip for the energy pillar
     *
     * @param bodyColorStrip for the energy pillar rune
     * @param bodyStrip for the pillar
     * @param bodyRadiusStrip for the pillar's radius
     */
    public void setEnergyPillarStrips(FilmStrip bodyStrip, FilmStrip bodyColorStrip, FilmStrip bodyRadiusStrip) {
        EnergyPillarStrip = bodyStrip;
        EnergyPillarStrip.setFrame(0);


        EnergyPillarFieldStrip = bodyRadiusStrip;
        EnergyPillarFieldStrip.setFrame(0);

        EnergyPillarRune = bodyColorStrip;
        EnergyPillarRune.setFrame(0);
    }

    /**
     * Draws the Pillar, Radius, and Charge of Pillar
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas, float chargeRatio) {
    }
        // Color of Pillar Should Match The ChargeRatio of Possessed Golem



        // The radius
}
