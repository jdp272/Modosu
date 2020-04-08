package edu.cornell.gdiac.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.util.FilmStrip;

public class WaterTile extends BoxObstacle {
    /**
     * The textures for the water
     */
    private FilmStrip waterStrip;


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
    public WaterTile(float width, float height) {
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
    public WaterTile(float x, float y, float width, float height) { super(x,y,width,height); }

    /**
     * sets the FilmStrip for the water and the corresponding gauge
     *
     * @param strip for the water
     */
    public void setWaterStrip (FilmStrip strip) {
        waterStrip = strip;
        waterStrip.setFrame(0);
        this.setTexture(strip);
    }

    /**
     * Sets the correct frame of this water tile, based on the adjacent tiles.Z
     * Each boolean indicates if there is ground in the corresponding direction
     *
     * @param above If there is ground above this tile
     * @param below If there is ground below this tile
     * @param left If there is ground left of this tile
     * @param right If there is ground right of this tile
     */
    public void setFrame(boolean above, boolean below, boolean left, boolean right) {
        int index = 0;
        if(above) {
            if(below) {
                if(left) {
                    if(right) { // above, below, left, right
                        index = 3;
                    } else { // above, below, left
                        index = 15;
                    }
                } else {
                    if(right) { // above, below, right
                        index = 13;
                    } else { // above, below
                        index = 1;
                    }
                }
            } else {
                if(left) {
                    if(right) { // above, left, right
                        index = 12;
                    } else { // above, left
                        index = 7;
                    }
                } else {
                    if(right) { // above, right
                        index = 4;
                    } else { // above
                        index = 8;
                    }
                }
            }
        } else { // Not above
            if(below) {
                if(left) {
                    if(right) { // below, left, right
                        index = 14;
                    } else { // below, left
                        index = 6;
                    }
                } else {
                    if(right) { // below, right
                        index = 5;
                    } else { // below
                        index = 10;
                    }
                }
            } else {
                if(left) {
                    if(right) { // left, right
                        index = 2;
                    } else { // left
                        index = 11;
                    }
                } else {
                    if(right) { // right
                        index = 9;
                    } else { // above
                        index = 0;
                    }
                }
            }
        }
        waterStrip.setFrame(index);
    }
}
