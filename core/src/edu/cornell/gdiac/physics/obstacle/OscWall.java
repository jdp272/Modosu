package edu.cornell.gdiac.physics.obstacle;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.util.FilmStrip;

import com.badlogic.gdx.graphics.Color;

public class OscWall extends BoxObstacle {

    /** The Horizontal Wall Strip */
    protected FilmStrip horzOscWallStrip;

    /** The Horizontal Charge Indicator */
    protected FilmStrip horzOscWallGaugeStrip;

    /** The Vertical Wall Strip */
    protected FilmStrip vertOscWallStrip;

    /** The Vertical Charge Indicator */
    protected FilmStrip vertOscWallGaugeStrip;

    /** The main strip this object will use */
    protected FilmStrip mainOscWallStrip;

    /** THe main gauge strip this object will use */
    protected FilmStrip mainOscWallGaugeStrip;

    /** Whether the Gate is turning on or off */
    protected boolean isGoingUp;

    /** Whether the Gate is Vertical or Horizontal (true is vertical) */
    protected boolean isVert;

    /** Whether the Gate is Up or Down for Physics */
    protected boolean isUp;


    /**
     * These constants can be used for the light indicator as well
     */

    /** Constant for Frame that is the wall up start */
    private static int WALL_RISE_START_FRAME = 0;

    /** Constant for Frame that is the wall at peak height */
    private static int WALL_RISE_FINISH_FRAME =  39;

    /** Constant for Frame that is the wall down start */
    private static int WALL_FALLING_START_FRAME =  40;

    /** Constant for Frame that is the wall down finish */
    private static int WALL_FALLING_FINISH_FRAME = 59;

    /** The number of frames that have elapsed since the last animation update */
    private int elapsedFrames = 0;

    /** The number of frames that should pass before the animation updates */
    private int framesPerUpdate = 1;

    /** Whether or not the animation should be updated on this frame */
    private boolean updateFrame;

    /** Wall Up Delay Tracker */
    private int wallDelayCount = 0;

    /** Wall up Delay Max */
    private int wallDelayMax = 250;

    /**
     * Initialize a new OscWall at the Origin
     * @param width the expected width of the OscWall
     * @param height the expected height of the OscWall
     */
    public OscWall(float width, float height) {
        this(0, 0, width, height);
    }

    /**
     * Initialize a new OscWall at desired location
     * @param x the x-coordinate of the desired placement
     * @param y the y-coordinate of the desired placement
     * @param width the expected width of the OscWall
     * @param height the expected height of the OscWall
     */
    public OscWall(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    /**
     * Get whether the wall is actually up or down
     * @return true if the wall is up and physics should work
     */
    public boolean isUp() {
        return isUp;
    }

    /**
     * Set whether the wall is up or down
     * @param up should be true if the wall is up and physics should work
     */
    public void setUp(boolean up) {
        isUp = up;
    }

    /**
     * Get whether the wall is on its way going up
     * @return true if wall is on its way going up
     */
    public boolean isGoingUp() {
        return isGoingUp;
    }

    /**
     * Set whether the wall is on its way going up
     * @param goingUp true if wall is on its way going up
     */
    public void setGoingUp(boolean goingUp) {
        isGoingUp = goingUp;
    }

    /**
     * Get whether the placement of the wall is vertical or horizontal
     * @return true if vertical, false if horizontal
     */
    public boolean isVert() {
        return isVert;
    }

    /**
     * Set the placement of the wall
     * @param vert is true if vertical, false if horizontal
     */
    public void setVert(boolean vert) {
        isVert = vert;
    }

    /**
     * Animate the OscWall
     */
    public void updateAnimation() {
        int frame = 0;

        if (mainOscWallStrip != null) {
            frame = mainOscWallStrip.getFrame();
        }

        // Updating of the frame count
        elapsedFrames++;
        updateFrame = false;

        // Allow framerate control of this animation
        if(elapsedFrames >= framesPerUpdate) {
            updateFrame = true;
            elapsedFrames = 0;
        }

        if (updateFrame) {

            // Is vertical and the wall is up -> need to animate it going down and pause a little when down
            if (isVert) {
                mainOscWallStrip = vertOscWallStrip;
                mainOscWallGaugeStrip = vertOscWallGaugeStrip;
            } else {
                mainOscWallStrip = horzOscWallStrip;
                mainOscWallGaugeStrip = horzOscWallGaugeStrip;
            }

            if (isGoingUp) {
                if(frame < WALL_RISE_FINISH_FRAME && frame >= WALL_RISE_START_FRAME) {
                    frame++;
                } else if(frame == WALL_RISE_FINISH_FRAME) {
                    isUp = true;
                    wallDelayCount++;

                    if(wallDelayCount >= wallDelayMax) {
                        wallDelayCount = 0;
                        isUp = false;
                    }
                    if(!isUp) {
                        frame = WALL_FALLING_START_FRAME;
                        this.isGoingUp = false;
                    }
                }
            } else {
                if (frame < WALL_FALLING_FINISH_FRAME && frame >= WALL_FALLING_START_FRAME) {
                    isUp = true;
                    frame++;
                } else if(frame == WALL_FALLING_FINISH_FRAME) {
                    isUp = false;
                    wallDelayCount++;

                    if(wallDelayCount >= wallDelayMax) {
                        wallDelayCount = 0;
                        isUp = true;
                    }

                    if(isUp) {
                        frame = WALL_RISE_START_FRAME;
                        this.isGoingUp = true;
                    }
                }
            }
        }

        if(mainOscWallStrip != null && mainOscWallGaugeStrip != null) {
            mainOscWallStrip.setFrame(frame);
            mainOscWallGaugeStrip.setFrame(frame);
        }
    }


    /**
     * Set the actual filmstrip of the oscwall depending on the direction of the OscWall
     * @param isVert true if the oscwall is a vertically placed wall
     * @param isGoingUp true if the oscwall is on the path to going up
     */
    public void setMainStrip(boolean isVert, boolean isGoingUp) {
        this.isGoingUp = isGoingUp;
        this.isVert = isVert;

        if(isVert) {
            this.mainOscWallStrip = vertOscWallStrip;
            this.mainOscWallGaugeStrip = vertOscWallGaugeStrip;
        }
        else {
            this.mainOscWallStrip = horzOscWallStrip;
            this.mainOscWallGaugeStrip = horzOscWallGaugeStrip;
        }

        if(this.isGoingUp) {
            this.mainOscWallStrip.setFrame(WALL_RISE_START_FRAME);
            this.mainOscWallGaugeStrip.setFrame(WALL_RISE_START_FRAME);
        }
        else {
            this.mainOscWallStrip.setFrame(WALL_FALLING_START_FRAME);
            this.mainOscWallGaugeStrip.setFrame(WALL_FALLING_START_FRAME);
        }
    }

    /**
     * Sets the default strips for the OscWall
     * @param horzOscWallStrip the strip that corresponds to the strip of the wall in horizontal formation
     * @param horzOscWallGaugeStrip the strip that corresponds to the gauge for the horizontal formation
     * @param vertOscWallStrip the strip that corresponds to the strip of the wall in vertical formation
     * @param vertOscWallGaugeStrip the strip that corresponds to the gauge for the vertical formation
     */
    public void setOscWallStrips(FilmStrip horzOscWallStrip, FilmStrip horzOscWallGaugeStrip, FilmStrip vertOscWallStrip, FilmStrip vertOscWallGaugeStrip) {

        this.horzOscWallStrip = horzOscWallStrip;
        this.horzOscWallGaugeStrip = horzOscWallGaugeStrip;

        this.vertOscWallStrip = vertOscWallStrip;
        this.vertOscWallGaugeStrip = vertOscWallGaugeStrip;
    }

    /**
     * Draws the Pillar, Radius, and Charge of Pillar
     *
     * @param canvas Drawing context
     */
    public void draw(GameCanvas canvas) {
        Color goingUpColor = Color.valueOf("#9EE1E5");
        Color goingDownColor = Color.valueOf("#A29382");
        Color mainColor = Color.WHITE;
        float delayPercentage = (float) wallDelayCount / (float) wallDelayMax;

        if(this.mainOscWallStrip != null && this.mainOscWallGaugeStrip != null) {
            canvas.draw(mainOscWallStrip,Color.WHITE, (float)mainOscWallStrip.getRegionWidth() / 2f, (float)mainOscWallStrip.getRegionHeight() / 2f, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 0.25f, 0.25f);
            if(!this.isUp && !this.isGoingUp) {
                mainColor = goingDownColor;
                if((delayPercentage <= 0.6f && delayPercentage > 0.4f) || (delayPercentage <= 1f && delayPercentage > 0.8f)) {
                    mainColor = goingUpColor;
                }
                canvas.draw(mainOscWallGaugeStrip, mainColor, (float)mainOscWallGaugeStrip.getRegionWidth() / 2f, (float)mainOscWallGaugeStrip.getRegionHeight() / 2f, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 0.25f, 0.25f);
            }
            if(this.isGoingUp) {
                mainColor = goingUpColor;
                canvas.draw(mainOscWallGaugeStrip, mainColor, (float)mainOscWallGaugeStrip.getRegionWidth() / 2f, (float)mainOscWallGaugeStrip.getRegionHeight() / 2f, getX() * drawScale.x, getY() * drawScale.y, getAngle(), 0.25f, 0.25f);
            }
        }


    }
}
