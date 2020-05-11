package edu.cornell.gdiac.physics;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import edu.cornell.gdiac.physics.host.HostModel;
import edu.cornell.gdiac.physics.obstacle.*;
import edu.cornell.gdiac.physics.spirit.SpiritModel;

import java.util.ArrayList;

/**
 * A static class that can be used for loading a level from a json file
 */
public class Loader {

    public static final String TUTORIAL_DATA_PATH = "levels/tutorial_data.json";

    /** A struct that stores data about the tutorial levels */
    public static class Tutorials {
        public TutorialData[] tutorials;
    }

    /** A struct that stores data from an obstacle when read from the json */
    public static class WallData {
        public Vector2 origin; // Center of the box
        public Vector2 dimensions;

        public int primaryFrame;
        public int leftFrame;
        public int rightFrame;
        public int frontEdgeFrame;
        public int backEdgeFrame;
        public int lowerLeftCornerFrame;
        public int lowerRightCornerFrame;
    }

    /** A struct that stores data from water when read from the json */
    public static class WaterData {
        public Vector2 origin; // Center of the box
        public Vector2 dimensions;
        public int frame;

        public boolean upLeft;
        public boolean upRight;
        public boolean downLeft;
        public boolean downRight;
    }

    /** A struct that stores data from an obstacle when read from the json */
    public static class BorderEdgeData {
        public Vector2 origin; // Center of the box

        public int frame;
        public BorderEdge.Side side;
    }

    /** A struct that stores data from an obstacle when read from the json */
    public static class BorderCornerData {
        public Vector2 origin; // Center of the box

        public BorderCorner.Corner corner;
    }

    /** A struct that stores data from sand when read from the json */
    public static class SandData {
        public Vector2 origin; // Center of the box
        public Vector2 dimensions;
        public int frame;

        public boolean upLeft;
        public boolean upRight;
        public boolean downLeft;
        public boolean downRight;
    }

    /** A struct that stores data from EnergyPillar when read from the json */
    public static class EnergyPillarData {
        public Vector2 origin; //Center of the pillar
        public Vector2 dimensions;
    }

    /** A struct that stores data from a host when read from the json */
    public static class HostData {
        public Vector2 location;
        public int currentCharge;
        public Vector2[] instructions;
        public boolean isPedestal;
    }

    /** A struct that stores all the data of a level when read from the json */
    public static class LevelData {
        public int tutorialNum;
        public Vector2 dimensions;
        public Vector2 startLocation;

        public WallData[] wallData;
        public WaterData[] waterData;
        public SandData[] sandData;
        public HostData[] hostData;
        public BorderEdgeData[] borderEdgeData;
        public BorderCornerData[] borderCornerData;
        public EnergyPillarData[] energyPillarData;

    }

    /** The data about the tutorial level messages */
    private Tutorials tutorials;

    /** A factory that creates the game objects */
    private Factory factory;

    /** A json object used for loading all json files */
    private Json json;

    /** An asset manager for loading assets */
    private AssetManager manager;

    // Constructors

    /**
     * Initializes the loader object
     */
    public Loader(Factory factory) {
        this.factory = factory;

        json = new Json();

//        outputTutorials();

        manager = new AssetManager();
        // Add font support to the asset manager
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        tutorials = json.fromJson(Tutorials.class, new FileHandle(TUTORIAL_DATA_PATH));
    }

    /**
     * A function to save a level as a json file storing the data of the level
     *
     * @param f A LibGDX FileHandle to a json file that holds the level data
     *          Requires: f is an open file handle
     * @param level A level struct which will be stored
     */
    public void saveLevel(FileHandle f, Level level) {
        LevelData levelData = new LevelData();

        // Save the level number in the tutorial, for loading messages. They are
        // 1 indexed, so 0 or negatives indicate no tutorial message
        levelData.tutorialNum = level.tutorialNum;

        // Store the ground information
        levelData.dimensions = level.dimensions;

        // Store the starting information
        levelData.startLocation = new Vector2(level.pedestal.getX(), level.pedestal.getY());

        // Store the obstacle data
        levelData.wallData = new WallData[level.walls.length];
        for(int i = 0; i < level.walls.length; i++) {
            WallData oData = new WallData();
            oData.dimensions = level.walls[i].getDimension();
            oData.origin = new Vector2(level.walls[i].getX(), level.walls[i].getY());
            if(level.walls[i] instanceof Wall) {
                oData.primaryFrame = level.walls[i].getPrimaryFrame();
                oData.leftFrame = level.walls[i].getLeftFrame();
                oData.rightFrame = level.walls[i].getRightFrame();
                oData.frontEdgeFrame = level.walls[i].getFrontEdgeFrame();
                oData.backEdgeFrame = level.walls[i].getBackEdgeFrame();
                oData.lowerLeftCornerFrame = level.walls[i].getLowerLeftCornerFrame();
                oData.lowerRightCornerFrame = level.walls[i].getLowerRightCornerFrame();
            } else {
                oData.primaryFrame = -1;
                oData.leftFrame = -1;
                oData.rightFrame = -1;
            }

            levelData.wallData[i] = oData;
        }

        // Store the water data
        levelData.waterData = new WaterData[level.water.length];
        for(int i = 0; i < level.water.length; i++) {
            WaterData wData = new WaterData();
            wData.dimensions = level.water[i].getDimension();
            wData.origin = new Vector2(level.water[i].getX(), level.water[i].getY());
            wData.frame = level.water[i].getFrame();

            wData.upLeft = level.water[i].getUpLeftCorner();
            wData.upRight = level.water[i].getUpRightCorner();
            wData.downLeft = level.water[i].getDownLeftCorner();
            wData.downRight = level.water[i].getDownRightCorner();

            levelData.waterData[i] = wData;
        }

        // Store the sand data
        levelData.sandData = new SandData[level.sand.length];
        for(int i = 0; i < level.sand.length; i++) {
            SandData sData = new SandData();
            sData.dimensions = level.sand[i].getDimension();
            sData.origin = new Vector2(level.sand[i].getX(), level.sand[i].getY());
            sData.frame = level.sand[i].getFrame();

            sData.upLeft = level.sand[i].getUpLeftCorner();
            sData.upRight = level.sand[i].getUpRightCorner();
            sData.downLeft = level.sand[i].getDownLeftCorner();
            sData.downRight = level.sand[i].getDownRightCorner();

            levelData.sandData[i] = sData;
        }

        // Store the border edge data
        levelData.borderEdgeData = new BorderEdgeData[level.borderEdges.length];
        for(int i = 0; i < level.borderEdges.length; i++) {
            BorderEdgeData beData = new BorderEdgeData();
            beData.origin = level.borderEdges[i].getPosition();
            beData.side = level.borderEdges[i].getSide();
            beData.frame = level.borderEdges[i].getFrame();

            levelData.borderEdgeData[i] = beData;
        }

        // Store the border edge data
        levelData.borderCornerData = new BorderCornerData[level.borderCorners.length];
        for(int i = 0; i < level.borderCorners.length; i++) {
            BorderCornerData bcData = new BorderCornerData();
            bcData.origin = level.borderCorners[i].getPosition();
            bcData.corner = level.borderCorners[i].getCorner();

            levelData.borderCornerData[i] = bcData;
        }

        // Store the energy pillar data
        levelData.energyPillarData = new EnergyPillarData[level.energyPillars.length];
        for(int i = 0; i < level.energyPillars.length; i++) {
            EnergyPillarData epData = new EnergyPillarData();
            epData.dimensions = level.energyPillars[i].getDimension();
            epData.origin = new Vector2(level.energyPillars[i].getX(), level.energyPillars[i].getY());

            levelData.energyPillarData[i] = epData;
        }

        // Store the host data
        levelData.hostData = new HostData[level.hosts.size()];
        for(int i = 0; i < level.hosts.size(); i++) {
            if(!level.hosts.get(i).isPedestal()) {
                HostData hData = new HostData();
                hData.location = new Vector2(level.hosts.get(i).getX(), level.hosts.get(i).getY());
                hData.instructions = level.hosts.get(i).getInstructionList();
                hData.isPedestal = level.hosts.get(i).isPedestal();
                hData.currentCharge = level.hosts.get(i).getCurrentCharge();

                levelData.hostData[i] = hData;
            }
        }

        // Store the now-populated level data
        String output = json.toJson(levelData);
        f.writeString(output, false);
    }

    /**
     * A function to get a Level from a json file storing the data of the level
     *
     * @param f A LibGDX FileHandle to a json file that holds the level data
     *          Requires: f is a json file that correctly stores the level data
     *
     * @return A complete Level object that is the json file deserialized
     */
    public Level loadLevel(FileHandle f) {
        // If this ever breaks try putting .readString() at the end of internal(f)
        // Can't load from a file handle because the file system is weird when
        // exported to a .jar
        LevelData levelData = json.fromJson(LevelData.class, f);

        // Load the map regions
        Vector2 dimensions = levelData.dimensions;

        // Load tutorial data
        int tutorialNum = levelData.tutorialNum;

        // Create the walls
        Wall[] walls = new Wall[levelData.wallData.length];
        WallData oData; // A simple reference to the data being processed
        for (int i = 0; i < levelData.wallData.length; i++) {
            oData = levelData.wallData[i];
            walls[i] = factory.makeWall(oData.origin.x, oData.origin.y,
                    oData.primaryFrame, oData.leftFrame, oData.rightFrame,
                    oData.frontEdgeFrame, oData.backEdgeFrame,
                    oData.lowerLeftCornerFrame, oData.lowerRightCornerFrame);

            // walls[i] = new BoxObstacle(oData.origin.x, oData.origin.y, oData.dimensions.x, oData.dimensions.y);
        }

        WaterTile[] water = new WaterTile[levelData.waterData.length];
        WaterData wData; // A simple reference to the data being processed
        for (int i = 0; i < levelData.waterData.length; i++) {
            wData = levelData.waterData[i];
            water[i] = factory.makeWater(wData.origin.x, wData.origin.y, wData.frame);
            water[i].setCorners(wData.upLeft, wData.upRight, wData.downLeft, wData.downRight);
        }

        SandTile[] sand = new SandTile[levelData.sandData.length];
        SandData sData;
        for (int i = 0; i < levelData.sandData.length; i++) {
            sData = levelData.sandData[i];
            sand[i] = factory.makeSand(sData.origin.x, sData.origin.y, sData.frame);
            sand[i].setCorners(sData.upLeft, sData.upRight, sData.downLeft, sData.downRight);
        }

        BorderEdge[] borderEdges = new BorderEdge[levelData.borderEdgeData.length];
        BorderEdgeData beData;
        for (int i = 0; i < levelData.borderEdgeData.length; i++) {
            beData = levelData.borderEdgeData[i];
            borderEdges[i] = factory.makeBorder(beData.origin.x, beData.origin.y, beData.side, beData.frame);
        }

        BorderCorner[] borderCorners = new BorderCorner[levelData.borderCornerData.length];
        BorderCornerData bcData;
        for (int i = 0; i < levelData.borderCornerData.length; i++) {
            bcData = levelData.borderCornerData[i];
            borderCorners[i] = factory.makeBorderCorner(bcData.origin.x, bcData.origin.y, bcData.corner);
        }

        EnergyPillar[] energyPillars = new EnergyPillar[levelData.energyPillarData.length];
        EnergyPillarData epData;
        for(int i = 0; i < levelData.energyPillarData.length; i++) {
            epData = levelData.energyPillarData[i];
            energyPillars[i] = factory.makeEnergyPillar(epData.origin.x, epData.origin.y);
        }

        // Create the hosts
        ArrayList<HostModel> hosts = new ArrayList<HostModel>();
        HostData hData; // A simple reference to the data being processed
        for (int i = 0; i < levelData.hostData.length; i++) {
            hData = levelData.hostData[i];


            /* NOTES: I'm assuming that eventually we'll have a simple creator
             for hosts, like a factory method or something, which we only need
             to give coordinates and the charge time. At that point, this can be
             easily updated with that.

             This also assumes that a zero charge time means it has no time
             limit

             */
            hosts.add(factory.makeSmallHost(hData.location.x, hData.location.y, hData.instructions, hData.currentCharge));
        }

        // Create the starting "host" (with no charge capacity)
        HostModel pedestal = factory.makePedestal(levelData.startLocation.x, levelData.startLocation.y);
        SpiritModel spirit = factory.makeSpirit(levelData.startLocation.x, levelData.startLocation.y);
        return new Level(dimensions, walls, water, sand, borderEdges, borderCorners, energyPillars, hosts, pedestal, spirit, tutorialNum);
    }

    /**
     * Get tutorial data. If no tutorial file was initially loaded, nothing will
     * be returned
     *
     * @param tutorialNum The index of the tutorial level, 1 indexed. If outside
     *                    the range of tutorials, null will be returned
     */
    public TutorialData getTutorialData(int tutorialNum) {
        if(tutorials != null && tutorialNum > 0 && tutorialNum <= tutorials.tutorials.length) {
            return tutorials.tutorials[tutorialNum - 1];
        }
        return null;
    }

//    /**
//     * Output tutorials. For development purposes only
//     */
//    public void outputTutorials() {
//        Tutorials tutorials = new Tutorials();
//        tutorials.tutorials = new TutorialData[1];
//
//        tutorials.tutorials[0] = new TutorialData();
//        tutorials.tutorials[0].instructions = "TEST TUTORIAL MESSAGE";
//        tutorials.tutorials[0].countdown = 60;
//        tutorials.tutorials[0].location = new Vector2(200, 200);
//
//        json.toJson(tutorials, new FileHandle(TUTORIAL_DATA_PATH));
//    }
}
