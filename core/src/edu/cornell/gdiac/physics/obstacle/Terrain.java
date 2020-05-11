package edu.cornell.gdiac.physics.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.util.FilmStrip;

public abstract class Terrain extends BoxObstacle {

    /** The textures for the terrain */
    protected FilmStrip terrainStrip;
    /** The textures for the corner layover that goes over the tile */
    private FilmStrip cornerStrip;

    /** The frame in the main terrain film strip for this tile */
    protected int frame;

    private boolean upLeft;
    private boolean upRight;
    private boolean downLeft;
    private boolean downRight;


    /**
     * Creates a new terrain tile at the origin.
     *
     * The size is expressed in physics units NOT pixels.  In order for
     * drawing to work properly, you MUST set the drawScale. The drawScale
     * converts the physics units to pixels.
     *
     * @param width		The object width in physics units
     * @param height	The object width in physics units
     */
    public Terrain(float width, float height) {
        super(width, height);
    }

    /**
     * Creates a new terrain tilet.
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
    public Terrain(float x, float y, float width, float height) { super(x,y,width,height); }

    /**
     * If the given tile is "similar" to this tile, such that no edge would be
     * needed between them. For example, if this tile is water and the given
     * tile is water, this would return true, because there does not need to be
     * an edge between two water tiles
     *
     * @param obj The tile to compare
     *
     * @return If a visual edge should be between this tile and the given tile
     */
    public abstract boolean continuousWithTile(Obstacle obj);

    /**
     * sets the FilmStrip for the terrain
     *
     * @param strip for the terrain
     */
    public void setTerrainStrip (FilmStrip strip) {
        terrainStrip = strip;
        terrainStrip.setFrame(0);
        this.setTexture(strip);
    }

    /**
     * sets the FilmStrip for the corner texture
     *
     * @param strip for the corner
     */
    public void setCornerStrip (FilmStrip strip) {
        cornerStrip = strip;
        cornerStrip.setFrame(0);
    }

    /**
     * Gets the frame for this terrain tile, for the filmstrip
     */
    public int getFrame() {
        return frame;
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
            s.setAsBox(getWidth()/2,getHeight()/4,new Vector2(0, getHeight()/4),0);
            shape = s;
        }else{
            PolygonShape s = new PolygonShape();
            s.setAsBox(getWidth()/2,getHeight()/2,new Vector2(0, 0),0);
            shape = s;
        }
    }

    public void setFrameLvlDsgn(int frame) {
        terrainStrip.setFrame(frame);
        this.frame = frame;
    }

    /**
     * Sets the correct frame of this terrain tile, based on the adjacent tiles.
     * Each boolean indicates if there is ground in the corresponding direction
     *
     * @param above If there is ground above this tile
     * @param below If there is ground below this tile
     * @param left If there is ground left of this tile
     * @param right If there is ground right of this tile
     */
    public void setFrame(boolean above, boolean below, boolean left, boolean right, boolean lvlDsgn) {
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
        if(lvlDsgn) {
            setFrameLvlDsgn(index);
        } else {
            setFrame(index);
        }
    }

    /**
     * @return If there is a corner in the upper left
     */
    public boolean getUpLeftCorner() {
        return upLeft;
    }

    /**
     * @return If there is a corner in the upper right
     */
    public boolean getUpRightCorner() {
        return upRight;
    }

    /**
     * @return If there is a corner in the lower left
     */
    public boolean getDownLeftCorner() {
        return downLeft;
    }

    /**
     * @return If there is a corner in the lower right
     */
    public boolean getDownRightCorner() {
        return downRight;
    }

    /**
     * Sets the correct corners, if any, of this terrain tile, based on adjacent
     * tiles. Each boolean indicates if there is ground in the corresponding
     * diagonal direction
     *
     * @param upLeft If there is ground in the tile above and to the left of this tile
     * @param upRight If there is ground in the tile above and to the right of this tile
     * @param downLeft If there is ground in the tile below and to the left of this tile
     * @param downRight If there is ground in the tile below and to the right of this tile
     */
    public void setCorners(boolean upLeft, boolean upRight, boolean downLeft, boolean downRight) {
        this.upLeft = upLeft;
        this.upRight = upRight;
        this.downLeft = downLeft;
        this.downRight = downRight;
    }

    @Override
    public void draw(GameCanvas canvas) {
        super.draw(canvas);

        setScaling(cornerStrip);

        if(upLeft) {
            cornerStrip.setFrame(0);
            canvas.draw(cornerStrip, Color.WHITE, cornerStrip.getRegionWidth() / 2, cornerStrip.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, getAngle(), (64.f / cornerStrip.getRegionWidth())+0.01f, (64.f / cornerStrip.getRegionHeight())+0.01f);
        }
        if(upRight) {
            cornerStrip.setFrame(1);
            canvas.draw(cornerStrip, Color.WHITE, cornerStrip.getRegionWidth() / 2, cornerStrip.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, getAngle(), (64.f / cornerStrip.getRegionWidth())+0.01f, (64.f / cornerStrip.getRegionHeight())+0.01f);
        }
        if(downLeft) {
            cornerStrip.setFrame(2);
            canvas.draw(cornerStrip, Color.WHITE, cornerStrip.getRegionWidth() / 2, cornerStrip.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, getAngle(), (64.f / cornerStrip.getRegionWidth())+0.01f, (64.f / cornerStrip.getRegionHeight())+0.01f);
        }
        if(downRight) {
            cornerStrip.setFrame(3);
            canvas.draw(cornerStrip, Color.WHITE, cornerStrip.getRegionWidth() / 2, cornerStrip.getRegionHeight() / 2, getX() * drawScale.x, getY() * drawScale.y, getAngle(), (64.f / cornerStrip.getRegionWidth())+0.01f, (64.f / cornerStrip.getRegionHeight())+0.01f);
        }
    }
}
