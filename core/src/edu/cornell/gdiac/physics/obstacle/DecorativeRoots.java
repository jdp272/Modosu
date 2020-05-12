package edu.cornell.gdiac.physics.obstacle;

public class DecorativeRoots extends BoxObstacle {

    /**
     * The frame in the roots image
     */
    private int frame;

    /**
     *
     * @param x The x coordinate of the tile
     * @param y The y coordinate of the tile
     * @param width The tile width
     * @param height The tile height
     * @param frame The frame in the texture to store. The texture frame still
     *              needs to be set externally. It should be set to this frame
     */
    public DecorativeRoots(float x, float y, float width, float height, int frame) {
        super(x, y, width, height);
        this.frame = frame;
    }

    /**
     * @return The frame of this decorative root tile
     */
    public int getFrame() {
        return frame;
    }
}
