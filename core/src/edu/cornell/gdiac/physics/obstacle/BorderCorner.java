package edu.cornell.gdiac.physics.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.util.FilmStrip;

public class BorderCorner extends BoxObstacle {

    /** The width of a tile in Box2D coordinates */
    public static float TILE_WIDTH = 2.f;
    /** The number of tiles that the corner takes up */
    public static float CORNER_SCALE = 2.f;

    /**
     * An enum for the different possible border sides this edge can be on.
     * Angle stores the associated rotation of the texture for that side
     */
    public enum Corner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    /** The side that this edge goes along */
    private Corner corner;

    /** The filmstrip to be used for rendering this corner */
    private FilmStrip cornerStrip;
    /** The filmstrip to be used for rendering this corner */
    private FilmStrip cornerNightStrip;
    /** The filmstrip to be used for rendering this corner */
    private TextureRegion cornerNightTexture;

//    /** The frame within the border edge filmstrip to be used */
//    private int frame;

    /** A cache vector for computation and for passing as a parameter */
    private Vector2 cache;
    /** Color of night opacity */
    private Color opacity;

    public BorderCorner(float x, float y, float width, float height, Corner corner, FilmStrip edgeStrip, FilmStrip edgeNightStrip, Color opacity) {
        super(x, y, width, height);
        this.opacity = opacity;
        this.cornerStrip = edgeStrip;
        this.cornerNightStrip = edgeNightStrip;
        setTexture(this.cornerStrip);
        cornerNightTexture = this.cornerNightStrip;
        origin.set(cornerNightTexture.getRegionWidth()/2.0f, cornerNightTexture.getRegionHeight()/2.0f);

        setCorner(corner);

//        this.frame = frame;
//        this.edgeStrip.setFrame(frame);

        this.cache = new Vector2();
    }

    /**
     * @return The frame for this border corner
     */
    public Corner getCorner() { return corner; }

    /**
     * Set the side of this border to the specified side. This also updates the
     * frame to correspond to the default image for the side
     *
     * @param corner The new side for this border edge
     */
    public void setCorner(Corner corner) {
        this.corner = corner;
        switch(this.corner) {
        case TOP_LEFT:
            this.cornerStrip.setFrame(0);
            this.cornerNightStrip.setFrame(0);
            break;
        case TOP_RIGHT:
            this.cornerStrip.setFrame(1);
            this.cornerNightStrip.setFrame(1);
            break;
        case BOTTOM_LEFT:
            this.cornerStrip.setFrame(2);
            this.cornerNightStrip.setFrame(2);
            break;
        case BOTTOM_RIGHT:
            this.cornerStrip.setFrame(3);
            this.cornerNightStrip.setFrame(3);
            break;
        }
    }

//    /**
//     * @return The frame for this border edge
//     */
//    public int getFrame() { return frame; }

//    /**
//     * Sets the correct frame of this wall tile, based on the adjacent tiles.
//     * Each boolean indicates if there is a wall in the corresponding direction
//     *
//     * @param above If there is a wall above this tile
//     * @param below If there is a wall below this tile
//     * @param left If there is a wall left of this tile
//     * @param right If there is a wall right of this tile
//     * @param belowIsTop If there is a wall below, and it not a front wall
//     * @param leftIsTop If there is a wall to the left, and it not a front wall
//     * @param rightIsTop If there is a wall to the right, and it not a front wall
//     * @param lowerLeftIsTop If there is a wall down and to the left, and it is not a front wall
//     * @param lowerRightIsTop If there is a wall down and to the right, and it is not a front wall
//     * @param alt If the position has an alternate texture, this indicates if it
//     *            should be used instead of the primary one
//     */
//    public void setFrame(boolean above, boolean below, boolean left, boolean right,
//                         boolean belowIsTop, boolean leftIsTop, boolean rightIsTop,
//                         boolean lowerLeftIsTop, boolean lowerRightIsTop,
//                         boolean alt) {
//
//        // Draw the ceiling only if a wall is below this one
//        if(below) {
//            primaryFrame = alt ? WALL_TOP_B : WALL_TOP_A;
//            if(!belowIsTop) {
//                frontEdgeFrame = FRONT_EDGE;
//            } else {
//                frontEdgeFrame = NO_SIDE;
//            }
//        } else {
//            primaryFrame = WALL_FRONT;
//            frontEdgeFrame = NO_SIDE;
//        }
//
//        // Draw the left and right borders if there is no wall to that side,
//        // or if it is a front wall and this wall is not
//        if(leftIsTop || (primaryFrame == WALL_FRONT && left)) {
//            leftFrame = NO_SIDE;
//        } else {
//            if(primaryFrame == WALL_FRONT) {
//                leftFrame = WALL_LEFT_FRONT;
//            } else {
//                leftFrame = WALL_LEFT_TOP;
//            }
//        }
//        if(rightIsTop || (primaryFrame == WALL_FRONT && right)) {
//            rightFrame = NO_SIDE;
//        } else {
//            if(primaryFrame == WALL_FRONT) {
//                rightFrame = WALL_RIGHT_FRONT;
//            } else {
//                rightFrame = WALL_RIGHT_TOP;
//            }
//        }
//
//        // Draw the back rim only if there is no wall behind this one
//        if(!above) {
//            backEdgeFrame = BACK_EDGE;
//        } else {
//            backEdgeFrame = NO_SIDE;
//        }
//
//        // Draw the corners only if below is a top, that side is a top, and the
//        // tile diagonal below is not a top wall
//        if(belowIsTop && leftIsTop && !lowerLeftIsTop) {
//            lowerLeftCornerFrame = LOWER_LEFT_CORNER;
//        } else {
//            lowerLeftCornerFrame = NO_SIDE;
//        }
//        if(belowIsTop && rightIsTop && !lowerRightIsTop) {
//            lowerRightCornerFrame = LOWER_RIGHT_CORNER;
//        } else {
//            lowerRightCornerFrame = NO_SIDE;
//        }
//    }

    @Override
    public void draw(GameCanvas canvas) {
        if(texture == null) {
            System.out.println("draw() called on border edge with null texture");
            return;
        }

        // Calculate how to position the corners. The lower left corner
        // initially starts in the center of the corner tile.
        float x = getX(), y = getY();
        switch(corner) {
        case TOP_LEFT:
            x -= 1.5 * TILE_WIDTH;
            y -= 0.5 * TILE_WIDTH;
            break;
        case TOP_RIGHT:
            x -= 0.5 * TILE_WIDTH;
            y -= 0.5 * TILE_WIDTH;
            break;
        case BOTTOM_LEFT:
            x -= 1.5 * TILE_WIDTH;
            y -= 1.5 * TILE_WIDTH;
            break;
        case BOTTOM_RIGHT:
            x -= 0.5 * TILE_WIDTH;
            y -= 1.5 * TILE_WIDTH;
            break;
        }

        x *= drawScale.x;
        y *= drawScale.y;

        canvas.draw(texture, Color.WHITE, x, y, drawScale.x * CORNER_SCALE * TILE_WIDTH, drawScale.y * CORNER_SCALE * TILE_WIDTH);
        //canvas.draw(cornerNightTexture, opacity, x, y, drawScale.x * CORNER_SCALE * TILE_WIDTH, drawScale.y * CORNER_SCALE * TILE_WIDTH);

//        canvas.draw(texture, Color.WHITE,origin.x + TILE_WIDTH,origin.y + TILE_WIDTH,getX()*drawScale.x,getY()*drawScale.y,getAngle(),sx,sy);
    }
}
