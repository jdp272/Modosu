package edu.cornell.gdiac.physics;

import java.io.File;
import java.io.FilenameFilter;

public class Constants {
    /** The height of a single tile in Box2D coordinates */
    public static final int TILE_WIDTH = 2;
    /** The height of a single tile in Box2D coordinates */
    public static final int TILE_HEIGHT = 2;

    /** The minimum number of tiles that the board can be */
    public static final int MINIMUM_BOARD_WIDTH = 6;
    /** The minimum number of tiles that the board can be */
    public static final int MINIMUM_BOARD_HEIGHT = 6;

    /** Filteres out non levels from directory, for use opening levels */
    public static final FilenameFilter filenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.substring(name.length() - 4).equals(".lvl");
        }
    };

    /**
     * Used by InputController to scale a coordinate with a zoom based on a
     * dimension
     *
     * @param f The coordinate to scale
     * @param zoom The zoom of the screen
     * @param width The width or height, based on if the coordinate is x or y
     *
     * @return The scaled coordinate
     */
    public static float scalePoint(float f, float zoom, float width) {
        return ((f - width/2) * zoom) + width/2;
    }
}
