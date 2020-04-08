package edu.cornell.gdiac.physics.obstacle;

import edu.cornell.gdiac.util.FilmStrip;

public class Pedestal extends BoxObstacle {

    /**
     * The textures for the water
     */
    private FilmStrip pedestalStrip;

    protected int STARTING_FRAME = 4;

    /** The frame in the film strip for this tile */
    private int frame;
    /** If true continue to cycle the animation */
    private boolean cycle;


    /**
     * Creates a new water tile at the origin.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     */
    public Pedestal(float width, float height) {
        super(width, height);
    }

    /**
     * Creates a new water tilet.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param x  		Initial x position of the tile center
     * @param y  		Initial y position of the tile center
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     */
    public Pedestal(float x, float y, float width, float height) { super(x,y,width,height); }

    /**
     * sets the FilmStrip for the water and the corresponding gauge
     *
     * @param strip for the water
     */
    public void setPedestalStrip (FilmStrip strip) {
        pedestalStrip = strip;
        pedestalStrip.setFrame(0);
        this.setTexture(strip);
    }

    /**
     * Gets the frame for this water tile, for the filmstrip
     */
    public int getFrame() {
        return frame;
    }

    /**
     * Sets the frame for this water tile to be the given frame in the filmstrip
     *
     * @param frame The frame to set for this water tile, between 0 and 15, incl
     */
    public void setFrame(int frame) {
        pedestalStrip.setFrame(frame);
        this.frame = frame;
    }

    public void animatePedestal() {
        if(this.pedestalStrip.getFrame() < STARTING_FRAME) {
            pedestalStrip.setFrame(pedestalStrip.getFrame() + 1);
        }
        else if(this.pedestalStrip.getFrame() == this.pedestalStrip.getSize() - 1) {
           pedestalStrip.setFrame(0);
        }
    }
}
