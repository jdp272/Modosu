package edu.cornell.gdiac.physics;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Comparator;

public class Constants {
    /** The height of a single tile in Box2D coordinates */
    public static final int TILE_WIDTH = 2;
    /** The height of a single tile in Box2D coordinates */
    public static final int TILE_HEIGHT = 2;

    /** The minimum number of tiles that the board can be */
    public static final int MINIMUM_BOARD_WIDTH = 6;
    /** The minimum number of tiles that the board can be */
    public static final int MINIMUM_BOARD_HEIGHT = 6;

    public static final FilenameFilter filenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.substring(name.length() - 4).equals(".lvl");
        }
    };

}
