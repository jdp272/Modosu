package edu.cornell.gdiac.physics.obstacle;

import edu.cornell.gdiac.util.FilmStrip;

public class EnergyPillar extends BoxObstacle {

    /** The texture strip for the Energy Pillar */
    protected FilmStrip EnergyPillarStrip;

    /** The texture strip for the radius of the Energy Pillar */
    protected FilmStrip EnergyPillarRadiusStrip;


    public EnergyPillar(float width, float height) {
        this(0, 0, width, height);
    }

    public EnergyPillar(float x, float y, float width, float height) {
        super(x, y, width, height);
    }






    /**
     * sets the FilmStrip for the energy pillar
     *
     * @param strip for the energy pillar
     */
    public void setEnergyPillarStrip(FilmStrip strip) {
        EnergyPillarStrip = strip;
        EnergyPillarStrip.setFrame(0);
        this.setTexture(strip);
    }

}
