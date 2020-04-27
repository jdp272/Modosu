package edu.cornell.gdiac.physics.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.util.FilmStrip;

public class BorderEdge extends BoxObstacle {
    /**
     * An enum for the different possible border sides this edge can be on.
     * Angle stores the associated rotation of the texture for that side
     */
    public enum Side {
        TOP(0.f),
        BOTTOM(0.f),
        LEFT((float)(-Math.PI / 2.0)),
        RIGHT((float)(-Math.PI / 2.0));

        Side(float angle) {
            this.angle = angle;
        }
        public final float angle;
    }

    /** The side that this edge goes along */
    private Side side;

    /** The filmstrip to be used for rendering this edge */
    private FilmStrip edgeStrip;

//    /** The frame within the border edge filmstrip to be used */
//    private int frame;

    /** A cache vector for computation and for passing as a parameter */
    private Vector2 cache;

    public BorderEdge(float x, float y, float width, float height, Side side, FilmStrip edgeStrip) {
        super(x, y, width, height);
        this.edgeStrip = edgeStrip;
        setTexture(this.edgeStrip);
        setSide(side);

//        this.frame = frame;
//        this.edgeStrip.setFrame(frame);

        this.cache = new Vector2();
    }

    public BorderEdge(float x, float y, float width, float height, Side side, int frame, FilmStrip edgeStrip) {
        super(x, y, width, height);
        this.edgeStrip = edgeStrip;
        setTexture(this.edgeStrip);
        setSide(side);

//        this.frame = frame;
        this.edgeStrip.setFrame(frame);

        this.cache = new Vector2();
    }

    /**
     * @return The frame for this border edge
     */
    public int getFrame() { return edgeStrip.getFrame(); }

    /**
     * @return The side for this border edge
     */
    public Side getSide() { return side; }

    /**
     * Set the side of this border to the specified side. This also updates the
     * frame to correspond to the default image for the side
     *
     * @param side The new side for this border edge
     */
    public void setSide(Side side) {
        this.side = side;
        switch(this.side) {
        case TOP:
            this.edgeStrip.setFrame(0);
            break;
        case BOTTOM:
            this.edgeStrip.setFrame(9);
            break;
        case LEFT:
            this.edgeStrip.setFrame(18);
            break;
        case RIGHT:
            this.edgeStrip.setFrame(27);
            break;
        }
    }

    /**
     * A function that sets this tile to be near another side
     *
     * @param distance How far this edge is from that side
     * @param nearbySide Which side this edge is near
     */
    public void setNextToSide(int distance, Side nearbySide) {
        switch(this.side) {
            case TOP:
                if(distance == 1) {
                    if(nearbySide == Side.LEFT) {
                        this.edgeStrip.setFrame(5);
                    } else if(nearbySide == Side.RIGHT) {
                        this.edgeStrip.setFrame(7);
                    }
                } else if(distance == 2) {
                    if(nearbySide == Side.LEFT) {
                        this.edgeStrip.setFrame(6);
                    } else if(nearbySide == Side.RIGHT) {
                        this.edgeStrip.setFrame(8);
                    }
                }
                break;
            case BOTTOM:
                if(distance == 1) {
                    if(nearbySide == Side.LEFT) {
                        this.edgeStrip.setFrame(11);
                    } else if(nearbySide == Side.RIGHT) {
                        this.edgeStrip.setFrame(12);
                    }
                }
                break;
            case LEFT:
                if(distance == 1) {
                    if(nearbySide == Side.BOTTOM) {
                        this.edgeStrip.setFrame(21);
                    } else if(nearbySide == Side.TOP) {
                        this.edgeStrip.setFrame(22);
                    }
                }
                break;
            case RIGHT:
                if(distance == 1) {
                    if(nearbySide == Side.BOTTOM) {
                        this.edgeStrip.setFrame(30);
                    } else if(nearbySide == Side.TOP) {
                        this.edgeStrip.setFrame(31);
                    }
                }
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

        canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,side.angle,sx,sy);
    }
}
