package edu.cornell.gdiac.physics;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Comparator;

public class Constants {
    public static final int TILE_WIDTH = 2;
    public static final int TILE_HEIGHT = 2;

    public static final FilenameFilter filenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.substring(name.length() - 4).equals(".lvl");
        }
    };

}
