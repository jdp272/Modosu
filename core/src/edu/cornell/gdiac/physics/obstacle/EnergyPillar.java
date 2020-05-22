package edu.cornell.gdiac.physics.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.util.FilmStrip;

public class EnergyPillar extends BoxObstacle {

    /** The texture strip for the Energy Pillar */
    protected FilmStrip energyPillarStrip;

    /** The texture strip for the glowing of the Pillar */
    protected FilmStrip energyPillarRune;

    /** The texture strip for the radius of the Energy Pillar */
    protected FilmStrip energyPillarFieldStrip;

    /** chargeProgression of Possessed Host */
    protected float chargeProgression;


    public EnergyPillar(float width, float height) {
        this(0, 0, width, height);
    }

    public EnergyPillar(float x, float y, float width, float height) {

        super(x, y, width, height);
        PolygonShape s = new PolygonShape();
        s.setAsBox(getWidth() / 2, getHeight() / 4, new Vector2(0, getHeight() / 4), 0);
        shape = s;
    }


    /**
     * Gets the filmstrip of the energy pillar radius
     * @return the fieldstrip of the energypillar
     */
    public float getEnergyPillarMajor() {
        return ((this.energyPillarFieldStrip.getRegionWidth() * (this.chargeProgression / 3f)) / drawScale.x)/2f;
    }
    public float getEnergyPillarMinor() {
        return ((this.energyPillarFieldStrip.getRegionHeight() * (this.chargeProgression / 3f)) / drawScale.y)/2f;
    }

    /**
     * sets the FilmStrip for the energy pillar
     *
     * @param bodyColorStrip for the energy pillar rune
     * @param bodyStrip for the pillar
     * @param bodyRadiusStrip for the pillar's radius
     */
    public void setEnergyPillarStrips(FilmStrip bodyStrip, FilmStrip bodyColorStrip, FilmStrip bodyRadiusStrip) {

        energyPillarStrip = bodyStrip;
        energyPillarStrip.setFrame(0);

        energyPillarFieldStrip = bodyRadiusStrip;
        energyPillarFieldStrip.setFrame(0);

        energyPillarRune = bodyColorStrip;
        energyPillarRune.setFrame(0);
    }

    public void setChargeProgression(float chargeProgression) {
        this.chargeProgression = MathUtils.lerp(this.chargeProgression, chargeProgression, 0.1f);
    }

    /**
     * Draws the Pillar, Radius, and Charge of Pillar
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        Color warningColor = new Color(200f/256f, 241f/256f, 238f/256f, 1);

//        setScaling(energyPillarStrip);
        canvas.draw(energyPillarStrip, Color.WHITE, energyPillarStrip.getRegionWidth() / 2f, energyPillarStrip.getRegionHeight() / 2f, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 0.3f, 0.3f);
//        setScaling(energyPillarRune);
        canvas.draw(energyPillarRune, warningColor, energyPillarStrip.getRegionWidth() / 2f, energyPillarStrip.getRegionHeight() / 2f, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 0.3f, 0.3f);
//        setScaling(energyPillarFieldStrip);
        canvas.draw(energyPillarFieldStrip, warningColor, energyPillarFieldStrip.getRegionWidth() / 2f, energyPillarFieldStrip.getRegionHeight() / 2f, getX() * drawScale.x, getY() * drawScale.y, getAngle(), chargeProgression / 3, chargeProgression / 3);
    }
}
