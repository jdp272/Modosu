package edu.cornell.gdiac.physics.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.util.FilmStrip;

public class SandTile extends Terrain {
    /**
     * Creates a new sand tile at the origin.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     */
    public SandTile(float width, float height) {
        super(width, height);
    }

    /**
     * Sets the frame for this terrain tile to be the given frame in the filmstrip
     *
     * @param frame The frame to set for this terrain tile, between 0 and 15, incl
     */
    public void setFrame(int frame) {
        terrainStrip.setFrame(frame);
        this.frame = frame;
        if(frame==1 || frame==3 || frame==5 || frame==6 || frame==10 || frame>12){
            PolygonShape s = new PolygonShape();
            s.setAsBox(getWidth()/4,getHeight()/4,new Vector2(0, getHeight()/4),0);
            shape = s;
        }else{
            PolygonShape s = new PolygonShape();
            s.setAsBox(getWidth()/4,getHeight()/2,new Vector2(0, 0),0);
            shape = s;
        }
    }

    /**
     * Creates a new sand tile.
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
    public SandTile(float x, float y, float width, float height) { super(x,y,width,height); }

    @Override
    public boolean continuousWithTile(Obstacle obj) {
        return obj instanceof SandTile;
    }
}
