package edu.cornell.gdiac.physics.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.util.FilmStrip;

import java.util.Random;

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

//    /**
//     * An enum that represents the edge frame
//     */
//    private enum Frame {
//        // Top frames
//        DEFAULT_A_TOP_FRAME(0, false),
//        DEFAULT_B_TOP_FRAME(1, false),
//        DEFAULT_C_TOP_FRAME(2, false),
//        LEFT_GATE_A_TOP_FRAME(3, true),
//        RIGHT_GATE_A_TOP_FRAME(4, false),
//        LEFT_GATE_B_TOP_FRAME(5, true),
//        RIGHT_GATE_B_TOP_FRAME(6, false),
//        LEFT_PAIR_TOP_FRAME(7, true),
//        RIGHT_PAIR_TOP_FRAME(8, false),
//        LEFT_LEFT_TOP_FRAME(9, true),
//        LEFT_MID_TOP_FRAME(10, false),
//        RIGHT_MID_TOP_FRAME(11, true),
//        RIGHT_RIGHT_TOP_FRAME(12, false),
//
//        RIGHT_RIGHT_TOP_FRAME(12, false),
//
//        ;
//
//        /**
//         * Constructs the frame
//         *
//         * @param frame The frame number
//         * @param startsPair Whether the frame is on the left side of a pair,
//         *                   and the adjacent edge must match
//         */
//        Frame(int frame, boolean startsPair) {
//            this.frame = frame;
//            this.startsPair = startsPair;
//        }
//
//        /** The frame number */
//        public final int frame;
//        /** Whether the frame is on the left/bottom side of a pair, and the
//         *  adjacent edge must match */
//        public final boolean startsPair;
//    };


    /** The side that this edge goes along */
    private Side side;

    /** Whether the frame is on the left/bottom side of a pair, and the
     *  adjacent edge must match */
    private boolean startsPair;

    /** The filmstrip to be used for rendering this edge */
    private FilmStrip edgeStrip;
    /** The filmstrip to be used for rendering this edge - AT NIGHT*/
    private FilmStrip nightStrip;

    // For generating random numbers for the art variants
    private final int seed;
    private Random random;

    /** A cache vector for computation and for passing as a parameter */
    private Vector2 cache;

    public BorderEdge(float x, float y, float width, float height, Side side, FilmStrip edgeStrip, FilmStrip nightStrip) {
        super(x, y, width, height);
        seed = (int)(Math.random() * 1000);
        random = new Random(seed);

        this.edgeStrip = edgeStrip;
        setTexture(this.edgeStrip);
        this.nightStrip = edgeStrip;
        setNightTexture(this.nightStrip, Color.WHITE);
        setSide(side);

//        this.frame = frame;
//        this.edgeStrip.setFrame(frame);

        this.cache = new Vector2();
    }

    public BorderEdge(float x, float y, float width, float height, Side side, int frame, FilmStrip edgeStrip, FilmStrip nightStrip, Color opacity) {
        super(x, y, width, height);
        seed = (int)(Math.random() * 1000);
        random = new Random(seed);

        this.edgeStrip = edgeStrip;
        this.nightStrip = nightStrip;
        setTexture(this.edgeStrip);
        setNightTexture(this.nightStrip, opacity);
        setSide(side);

//        this.frame = frame;
        setFrame(frame);

        this.cache = new Vector2();
    }

    /**
     * Sets the frame of both the edge strip and the night strip
     *
     * @param frame The frame to set
     */
    private void setFrame(int frame) {
        edgeStrip.setFrame(frame);
        nightStrip.setFrame(frame);
    }

    /**
     * @return The frame for this border edge
     */
    public int getFrame() { return edgeStrip.getFrame(); }

    /**
     * @return The side for this border edge
     */
    public Side getSide() { return side; }

//    private void setFrameObj(int frame) {
//        switch(frame) {
//            case 0:
//                frameObj = Frame.DEFAULT_A_TOP_FRAME;
//                break;
//            case 1:
//                frameObj = Frame.DEFAULT_B_TOP_FRAME;
//                break;
//            case 2:
//                frameObj = Frame.DEFAULT_C_TOP_FRAME;
//                break;
//            case 3:
//                frameObj = Frame.LEFT_GATE_A_TOP_FRAME;
//                break;
//            case 4:
//                frameObj = Frame.RIGHT_GATE_A_TOP_FRAME;
//                break;
//            case 5:
//                frameObj = Frame.LEFT_GATE_B_TOP_FRAME;
//                break;
//            case 6:
//                frameObj = Frame.RIGHT_GATE_B_TOP_FRAME;
//                break;
//            case 7:
//                frameObj = Frame.LEFT_PAIR_TOP_FRAME;
//                break;
//            case 8:
//                frameObj = Frame.RIGHT_PAIR_TOP_FRAME;
//                break;
//            case 9:
//                frameObj = Frame.LEFT_LEFT_TOP_FRAME;
//                break;
//            case 10:
//                frameObj = Frame.LEFT_MID_TOP_FRAME;
//                break;
//            case 11:
//                frameObj = Frame.RIGHT_MID_TOP_FRAME;
//                break;
//            case 12:
//                frameObj = Frame.RIGHT_RIGHT_TOP_FRAME;
//                break;
//        }
//
//    }

    public int getInt() {
        random.setSeed(seed);
        return random.nextInt(Integer.MAX_VALUE);
    }

    /**
     * Set the side of this border to the specified side. This also updates the
     * frame to correspond to the default image for the side and based on the
     * given adjacent left or above edge (if applicable).
     *
     * @param side The new side for this border edge
     */
    public void setSide(Side side) {
        random.setSeed(seed);
        this.side = side;

        int frame;

        switch(this.side) {
        case TOP:
            frame = random.nextInt(6);
            int numSingleEdges = 3;

            // If frame is one of the pairs, make it the left side
            if(frame >= numSingleEdges) {
                frame -= numSingleEdges;
                frame *= 2; // Have to skip the right side of the pairs
                frame += numSingleEdges;
            }

            break;
        case BOTTOM:
            // For some reason, nextInt(2) never seemed to give 0...
            frame = (random.nextInt(4) % 2) + 13;
            break;
        case LEFT:
            frame = random.nextInt(3) + 26;
            break;
        case RIGHT:
            frame = random.nextInt(3) + 39;
            break;
        default:
            System.out.println("Invalid side from BorderEdge.setSide()");
            assert false;
            return;
        }

        setFrame(frame);
//        setFrameObj(frame); // Updates the internally stored frame object

        // An adjacent frame is required to complete the pair for these frames
        // These are the pairs that start the pairs (on the left or bottom side)
        startsPair = (frame == 3 || frame == 5 || frame == 7 || frame == 28 || frame == 41);
    }


    /**
     * Set the side of this border to the specified side. This also updates the
     * frame to correspond to the default image for the side and based on the
     * given adjacent left or below edge (if applicable).
     *
     * @param canStartPair Whether this tile should be able to start a pair.
     *                     May be false if a pair just ended or if the next tile
     *                     is the automatically placed one next to a corner
     * @param leftOrBelow The adjacent border edge. When side is TOP or BOTTOM,
     *                 that edge should be left, and when side is LEFT or RIGHT,
     *                 that edge should be below. If there is none, use null.
     */
    public void resetFrame(boolean canStartPair, BorderEdge leftOrBelow) {
        if(leftOrBelow == null) return;

        random.setSeed(seed);
        this.side = side;

        int frame;

        int adjFrame = leftOrBelow.edgeStrip.getFrame();
        switch(this.side) {
            case TOP:
                if(adjFrame == 3 || adjFrame == 5 || adjFrame == 7) {
                    frame = adjFrame + 1;
                } else if (canStartPair) {
                    frame = random.nextInt(6);
                    int numSingleEdges = 3;

                    // If frame is one of the pairs, make it the left side
                    if (frame >= numSingleEdges) {
                        frame -= numSingleEdges;
                        frame *= 2; // Have to skip the right side of the pairs
                        frame += numSingleEdges;
                    }
                } else {
                    frame = random.nextInt(3);
                }

                break;
            case BOTTOM:
                // For some reason, nextInt(2) never seemed to give 0...
                frame = (random.nextInt(4) % 2) + 13;
                break;
            case LEFT:
                if(adjFrame == 29) {
                    // Pair order is reversed
                    frame = adjFrame - 1;
                } else if (canStartPair) {
                    frame = random.nextInt(3) + 26;

                    // Ensure frame never starts at the wrong one
                    if(frame == 28) frame = 29;

                } else {
                    frame = (random.nextInt(4) % 2) + 26;
                }

                break;
            case RIGHT:
                if(adjFrame == 42) {
                    frame = adjFrame - 1;
                } else if (canStartPair) {
                    frame = random.nextInt(3) + 39;

                    // Ensure frame never starts at the wrong one
                    if(frame == 41) frame = 42;
                } else {
                    frame = (random.nextInt(4) % 2) + 39;
                }
                break;
            default:
                System.out.println("Invalid side from BorderEdge.setSide()");
                assert false;
                return;
        }

        setFrame(frame);
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
                        setFrame(9);
                    } else if(nearbySide == Side.RIGHT) {
                        setFrame(12);
                    }
                } else if(distance == 2) {
                    if(nearbySide == Side.LEFT) {
                        setFrame(10);
                    } else if(nearbySide == Side.RIGHT) {
                        setFrame(11);
                    }
                }
                break;
            case BOTTOM:
                if(distance == 1) {
                    if(nearbySide == Side.LEFT) {
                        setFrame(15);
                    } else if(nearbySide == Side.RIGHT) {
                        setFrame(16);
                    }
                }
                break;
            case LEFT:
                if(distance == 1) {
                    if(nearbySide == Side.BOTTOM) {
                        setFrame(31);
                    } else if(nearbySide == Side.TOP) {
                        setFrame(30);
                    }
                }
                break;
            case RIGHT:
                if(distance == 1) {
                    if(nearbySide == Side.BOTTOM) {
                        setFrame(44);
                    } else if(nearbySide == Side.TOP) {
                        setFrame(43);
                    }
                }
                break;
        }
    }

    @Override
    protected void setScaling(TextureRegion tex) {
        super.setScaling(tex);
        sy *= 3; // Want the border to be 1x3 tiles, so manually scale 1 by 3
    }

    @Override
    public void draw(GameCanvas canvas) {
        draw(canvas, opacity);
    }

    public void draw(GameCanvas canvas, Color opacity) {
        if(texture == null) {
            System.out.println("draw() called on border edge with null texture");
            return;
        }

        setScaling(edgeStrip);
        canvas.draw(edgeStrip, Color.WHITE, origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,side.angle,sx,sy);
        setScaling(nightStrip);
        canvas.draw(nightStrip, opacity, origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,side.angle,sx,sy);
    }

    /**
     * Draw the edge if it is a top edge. Otherwise, nothing happens
     *
     * @param canvas The canvas for drawing
     */
    public void drawTop(GameCanvas canvas) {
        if(side == Side.TOP) {
            draw(canvas);
        }
    }


    /**
     * Draw the edge if it is not a top edge. Otherwise, nothing happens
     *
     * @param canvas The canvas for drawing
     */
    public void drawNotTop(GameCanvas canvas) {
        if(side != Side.TOP) {
            draw(canvas);
        }
    }
}