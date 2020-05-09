package edu.cornell.gdiac.physics.obstacle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.util.FilmStrip;

import java.util.Arrays;
import java.util.Random;

public class Wall extends BoxObstacle {

    /*
        Here is a list of enums. Each enum holds the frame or frames that should
        be drawn.

        They enforce the correct frames for each section. For example, an edge
        can only use frames that correspond to edges, and can't use the frame
        for a different wall type.

     */

//    private enum PrimaryFrame {
//        WALL_FRONT(24),
//        WALL_TOP_A(16),
//        WALL_TOP_B(17);
//
//        public final int frame;
//        PrimaryFrame(int f) { frame = f; }
//    }
//
//    PrimaryFrame primaryFrame;



    /** The tile width */
    private static final float TILE_WIDTH = 2.f;

    private static int NO_SIDE = 0;

    private static int WALL_FRONT = 24;
    private static int[] WALL_FRONT_ALT = { 27, 28, 29, 30, 31 };

    private static int WALL_TOP_A = 16;
    private static int WALL_TOP_B = 17;
    private static int[] WALL_TOP_ALT = { 5, 6, 7, 13, 14, 15 };

    private static int WALL_LEFT_FRONT  = 25;
    private static int WALL_RIGHT_FRONT = 26;

    private static int WALL_LEFT_TOP    = 18;
    private static int WALL_RIGHT_TOP   = 19;

    private static int FRONT_EDGE = 22;
    private static int BACK_LINE  = 23;
    private static int BACK_EDGE  = 8;
    private static int[] BACK_EDGE_ALT = { 2, 3, 4, 10, 11, 12 };

    private static int LOWER_LEFT_CORNER =  20;
    private static int LOWER_RIGHT_CORNER = 21;

    /** The texture strip for the wall */
    protected FilmStrip wallStrip;

    private boolean isFrontWall;

    /** If the hitbox should update with the wall texture */
    private boolean updateHitbox;

    private int primaryFrame;
    private int leftFrame;
    private int rightFrame;
    private int frontEdgeFrame;
    private int backEdgeFrame;
    private int lowerLeftCornerFrame;
    private int lowerRightCornerFrame;

    // For generating random numbers for the art variants
    private final int seed;
    private Random random;

    /** A cache vector for computation and for passing as a parameter */
    private Vector2 cache;

    public Wall(float x, float y, float width, float height, FilmStrip wallStrip) {
        this(x, y, width, height, wallStrip, WALL_FRONT,
                NO_SIDE, NO_SIDE, NO_SIDE, NO_SIDE, NO_SIDE, NO_SIDE);
    }

    public Wall(float x, float y, float width, float height, FilmStrip wallStrip,
                int primaryFrame, int leftFrame, int rightFrame,
                int frontEdgeFrame, int backEdgeFrame,
                int lowerLeftCornerFrame, int lowerRightCornerFrame) {
        super(x, y, width, height);
        seed = (int)(Math.random() * 1000);
        random = new Random(seed);

        this.wallStrip = wallStrip;
        setTexture(this.wallStrip);

        this.primaryFrame = primaryFrame;
        this.leftFrame = leftFrame;
        this.rightFrame = rightFrame;
        this.frontEdgeFrame = frontEdgeFrame;
        this.backEdgeFrame = backEdgeFrame;
        this.lowerLeftCornerFrame = lowerLeftCornerFrame;
        this.lowerRightCornerFrame = lowerRightCornerFrame;

        updateFrontWall();

        this.cache = new Vector2();
    }

    /**
     * @return The primary frame for this wall
     */
    public int getPrimaryFrame() { return primaryFrame; }

    /**
     * @return The left frame for this wall
     */
    public int getLeftFrame() { return leftFrame; }

    /**
     * @return The right frame for this wall
     */
    public int getRightFrame() { return rightFrame; }

    /**
     * @return The front edge frame for this wall
     */
    public int getFrontEdgeFrame() { return frontEdgeFrame; }

    /**
     * @return The back edge frame for this wall
     */
    public int getBackEdgeFrame() { return backEdgeFrame; }

    /**
     * @return The lower left corner frame for this wall
     */
    public int getLowerLeftCornerFrame() { return lowerLeftCornerFrame; }

    /**
     * @return The lower right corner frame for this wall
     */
    public int getLowerRightCornerFrame() { return lowerRightCornerFrame; }

    /**
     * @return Whether this wall is a front wall
     */
    public boolean isFrontWall() {
        return isFrontWall;
    }

    /**
     * Updates the front wall boolean internally, based on primaryFrame
     */
    private void updateFrontWall() {
        isFrontWall = (primaryFrame == WALL_FRONT);
        for(int i = 0; i < WALL_FRONT_ALT.length; i++) {
            if (primaryFrame == WALL_FRONT_ALT[i]) isFrontWall = true;
        }
    }

    /**
     * Sets the hitbox of this wall to be dependent on its texture.
     *
     * If this wall is a front wall, the hitbox won't extend all the way to the
     * bottom, after this function is called.
     *
     * This functions should NOT be called from the level designer, only from
     * the main game.
     */
    public void setAltHitbox() {
        // Change the hitbox only if this is a front wall
        if (isFrontWall()) {
            PolygonShape s = new PolygonShape();
            s.setAsBox(getWidth() / 2, getHeight() / 4, cache.set(0, getHeight() / 4), 0);
            shape = s;
        }
    }

    /**
     * Sets the correct frame of this wall tile, based on the adjacent tiles.
     * Each boolean indicates if there is a wall in the corresponding direction
     *
     * @param above If there is a wall above this tile
     * @param below If there is a wall below this tile
     * @param left If there is a wall left of this tile
     * @param right If there is a wall right of this tile
     * @param belowIsTop If there is a wall below, and it not a front wall
     * @param leftIsTop If there is a wall to the left, and it not a front wall
     * @param rightIsTop If there is a wall to the right, and it not a front wall
     * @param lowerLeftIsTop If there is a wall down and to the left, and it is not a front wall
     * @param lowerRightIsTop If there is a wall down and to the right, and it is not a front wall
     * @param x The x coordinate of this tile
     * @param y The y coordinate of this tile
     *
     */
    public void setFrame(boolean above, boolean below, boolean left, boolean right,
                         boolean belowIsTop, boolean leftIsTop, boolean rightIsTop,
                         boolean lowerLeftIsTop, boolean lowerRightIsTop,
                         int x, int y) {

        // x + 1 and y + 1 ensure that that term continues to change even when
        // either is 0

        random.setSeed(seed);
        int index;

        // Draw the ceiling only if a wall is below this one
        if(below) {
            index = random.nextInt(2 * WALL_TOP_ALT.length);

            if(index >= WALL_TOP_ALT.length) {
                primaryFrame = ((x + y) % 2 == 0 ? WALL_TOP_A : WALL_TOP_B);
            } else {
                primaryFrame = WALL_TOP_ALT[index];
            }

            if(!belowIsTop) {
                frontEdgeFrame = FRONT_EDGE;
            } else {
                frontEdgeFrame = NO_SIDE;
            }
        } else {
            index = random.nextInt(2 * WALL_FRONT_ALT.length);
            if(index >= WALL_FRONT_ALT.length) {
                primaryFrame = WALL_FRONT;
            } else {
                primaryFrame = WALL_FRONT_ALT[index];
            }

            frontEdgeFrame = NO_SIDE;
        }

        updateFrontWall();

        // Draw the left and right borders if there is no wall to that side,
        // or if it is a front wall and this wall is not
        if(leftIsTop || (isFrontWall() && left)) {
            leftFrame = NO_SIDE;
        } else {
            if(isFrontWall()) {
                leftFrame = WALL_LEFT_FRONT;
            } else {
                leftFrame = WALL_LEFT_TOP;
            }
        }
        if(rightIsTop || (isFrontWall() && right)) {
            rightFrame = NO_SIDE;
        } else {
            if(isFrontWall()) {
                rightFrame = WALL_RIGHT_FRONT;
            } else {
                rightFrame = WALL_RIGHT_TOP;
            }
        }

        // Draw the back rim only if there is no wall behind this one
        if(!above) {
            index = random.nextInt(2 * BACK_EDGE_ALT.length);
            if(index >= BACK_EDGE_ALT.length) {
                backEdgeFrame = BACK_EDGE;
            } else {
                backEdgeFrame = BACK_EDGE_ALT[index];
            }
        } else {
            backEdgeFrame = NO_SIDE;
        }

        // Draw the corners only if below is a top, that side is a top, and the
        // tile diagonal below is not a top wall
        if(belowIsTop && leftIsTop && !lowerLeftIsTop) {
            lowerLeftCornerFrame = LOWER_LEFT_CORNER;
        } else {
            lowerLeftCornerFrame = NO_SIDE;
        }
        if(belowIsTop && rightIsTop && !lowerRightIsTop) {
            lowerRightCornerFrame = LOWER_RIGHT_CORNER;
        } else {
            lowerRightCornerFrame = NO_SIDE;
        }
    }

    @Override
    public void draw(GameCanvas canvas) {
        // Note that this function only draws the front of the wall
        drawWall(canvas, true);
        drawWall(canvas, false);
    }

    /**
     * Draws the wall, depending on if the wall is a top wall or a front wall
     *
     * @param canvas The drawing context
     * @param front If front, it draws the front elements of the wall. Otherwise
     *              the top parts of the wall are drawn
     */
    public void drawWall(GameCanvas canvas, boolean front) {
        if(texture == null) {
            System.out.println("draw() called on wall with null texture");
            return;
        }

        if(isFrontWall() != front) {
            return;
        }

        // Draw the primary frame
        wallStrip.setFrame(primaryFrame);
        canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),sx,sy);

        // Draw the left side
        if(leftFrame != NO_SIDE) {
            wallStrip.setFrame(leftFrame);
            canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),sx,sy);
        }

        // Draw the right side
        if(rightFrame != NO_SIDE) {
            wallStrip.setFrame(rightFrame);
            canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),sx,sy);
        }

        // Draw the front edge
        if(frontEdgeFrame != NO_SIDE) {
            wallStrip.setFrame(frontEdgeFrame);
            canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),sx,sy);
        }

        // Draw the corners
        if(lowerLeftCornerFrame != NO_SIDE) {
            wallStrip.setFrame(lowerLeftCornerFrame);
            canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),sx,sy);
        }
        if(lowerRightCornerFrame != NO_SIDE) {
            wallStrip.setFrame(lowerRightCornerFrame);
            canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),sx,sy);
        }

        // Draw the back side
//        if(backFrame != NO_SIDE) {
//            wallStrip.setFrame(backFrame);
//
//            // Add a tile width so that it is drawn above
//            canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,(getY() + TILE_WIDTH)*drawScale.y,getAngle(),sx,sy);
//        }
    }

    /**
     * Simply draws the front of the walls, so that they will be drawn under
     * other tiles
     *
     * @param canvas Drawing context
     */
    public void drawFront(GameCanvas canvas) {
        if (texture == null) {
            System.out.println("draw() called on wall with null texture");
            return;
        }

        // Draw the front of the wall
        drawWall(canvas, true);
    }

    /**
     * Simply draws the top of the walls, so that they can be drawn over other
     * tiles
     *
     * @param canvas Drawing context
     */
    public void drawTop(GameCanvas canvas) {
        if (texture == null) {
            System.out.println("draw() called on wall with null texture");
            return;
        }

        // Draw the top of the wall
        drawWall(canvas, false);

        // Draw the back edge
        if(backEdgeFrame != NO_SIDE) {
            wallStrip.setFrame(backEdgeFrame);
            canvas.draw(texture, Color.WHITE,(int)origin.x,(int)origin.y,getX()*drawScale.x,(getY() + TILE_WIDTH)*drawScale.y,getAngle(),sx,sy);

            // Draw the line behind the back edge and the wall, if this is a top wall
            if(primaryFrame != WALL_FRONT) {
                wallStrip.setFrame(BACK_LINE);
                canvas.draw(texture, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),sx,sy);
            }
        }
    }

//    public void setWall(int n){
////        if(n>23){
////            wall = 0;
////        }else if(n < 0){
////            wall = 23;
////        }else {
////            wall = n;
////        }
////        if(wall > 17 && wall < 24 ){
////            PolygonShape s = new PolygonShape();
////            s.setAsBox(getWidth()/2,getHeight()/4,new Vector2(0, getHeight()/4),0);
////            shape = s;
////        }
////        else if(wall < 4){
////            PolygonShape s = new PolygonShape();
////            s.setAsBox(0,0,new Vector2(0, 0),0);
////            shape = s;
////        }
////        else{
////            PolygonShape s = new PolygonShape();
////            s.setAsBox(getWidth()/2,getHeight()/2,new Vector2(0, 0),0);
////            shape = s;
////        }
////        wall = n;
////        wallStrip.setFrame(wall);
//        setWallLvlDsgn(n);
//    }
//
//
//    public void setWallLvlDsgn(int n){
////        if(n>23){
////            wall = 0;
////        }else if(n < 0){
////            wall = 23;
////        }else {
////            wall = n;
////        }
////        PolygonShape s = new PolygonShape();
////        s.setAsBox(getWidth()/2,getHeight()/2,new Vector2(0, 0),0);
////        shape = s;
////        wall = n;
////        wallStrip.setFrame(wall);
//    }
//
//
//    /**
//     * sets the FilmStrip for the charged host and the corresponding gauge
//     * @param strip for the charged host
//     */
//    public void setWallStrip (FilmStrip strip) {
//        wallStrip = strip;
//        wallStrip.setFrame(20);
//        this.setTexture(strip);
//    }
}
