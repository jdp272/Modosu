package edu.cornell.gdiac.physics.obstacle;

import com.badlogic.gdx.graphics.Color;
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
    public void drawPillar(GameCanvas canvas) {
        canvas.draw(EnergyPillarStrip, Color.WHITE, EnergyPillarStrip.getRegionWidth() / 2, EnergyPillarStrip.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1, 1);
    }

    public void drawPillarDynamics(GameCanvas canvas,  float chargeProgression) {
        Color warningColor = new Color((104f/256f) + (chargeProgression * (151f/256f)), (241f/256f) - ((241f/256f) * chargeProgression), (233f/256f) - ((185f/256f) * chargeProgression), 1);
        canvas.draw(EnergyPillarRune, warningColor, EnergyPillarStrip.getRegionWidth() / 2, EnergyPillarStrip.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 1, 1);
        canvas.draw(EnergyPillarFieldStrip, warningColor, EnergyPillarFieldStrip.getRegionWidth() / 2, EnergyPillarFieldStrip.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, 4 * chargeProgression, 4 * chargeProgression);
    }
        // Color of Pillar Should Match The ChargeRatio of Possessed Golem



        // The radius
}
