package edu.cornell.gdiac.physics;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.obstacle.Obstacle;

import javax.security.auth.callback.Callback;
import java.util.ArrayList;

public class SpawnerList {

    /**
     * A simple struct for storing the information required for each spawner
     */
    private static class Spawner {
        /** The obstacle that represents the spawner */
        public BoxObstacle obstacle;
        /** The location of the spawner when camTarget is at the screen center */
        public Vector2 location;
        /** The callback function for when the spawner is clicked. Typically
         * makes a new object */
        public CallbackFunction callback;

        /**  The obstacle that was spawned most recently by this spawner. This
         * allows spawners that only create one item to see if their item was
         * created, and if so, edit it */
        public Obstacle lastObstacle = null;

        /**
         * Creates a new spawner object.
         *
         * @param obstacle The obstacle that represents the spawner
         * @param location The location of the spawner when camTarget is at the
         *                 screen center
         * @param callback The callback function for when the spawner is
         *                 clicked. Typically makes a new object
         */
        public Spawner(BoxObstacle obstacle, Vector2 location, CallbackFunction callback) {
            this.obstacle = obstacle;
            this.location = location;
            this.callback = callback;
        }
    }

    @FunctionalInterface
    public interface CallbackFunction {
        /**
         * The callback function that constructs the new game element at the
         * spawn location
         *
         * @param x The x location of the game element
         * @param y The y location of the game element
         * @param lastCreated The obstacle that was spawned most recently by
         *                    this spawner. This allows spawners that only
         *                    create one item to see if their item was created,
         *                    and if so, edit it.
         *
         * @return The new game object
         */
        Obstacle makeObject(float x, float y, Obstacle lastCreated);
    }

    /** The bottom of the stack of spawn objects. New objects are added below */
    private float bottom;

    /** The list of spawners, each of which spawns a different game object */
    private ArrayList<Spawner> spawnerList;

    // References to external variables
    /** A reference to the game canvas, for calculating positions */
    private GameCanvas canvas;
    /** The draw scale of the game */
    private Vector2 scale;
    /** The camera position */
    private Vector2 camPos;

    public SpawnerList(GameCanvas canvas, Vector2 scale, Vector2 camPos) {
        this.canvas = canvas;
        this.scale = scale;
        this.camPos = camPos;

        // This starts at the middle of the screen, because with the camera
        // offset, it will be the upper right corner
        bottom = canvas.getZoom() * canvas.getHeight() / 2.f / scale.y;

        spawnerList = new ArrayList<Spawner>();
    }

    // Add to documentation: obj should have already been added to the world

    /**
     * Adds a new spawner obstacle to the list of spawners. The obstacle will be
     * like a button, and the new object will be created centered at the
     * obstacle center. The obstacle will be repositioned (the initial position
     * doesn't matter, it will not be movable (can't be selected by
     * ObstacleSelector), and it will not be saved into a level.
     *
     * The obstacle should have been already added to the world.
     *
     * @param obj The Obstacle to represent the spawn object. This should be
     *            the same type of object as the callback function returns
     * @param func A functional interface that creates a new object of the
     *             correct type at a given location. This will be the function
     *             that is called when the spawner is clicked.
     */
    public void addSpawner(BoxObstacle obj, CallbackFunction func) {

//        obj.setX((canvas.getWidth() + camTarget.x) / scale.x - boxSpawn.getWidth() / 2.f);
//        obj.setY((canvas.getHeight() + camTarget.y) / scale.y - boxSpawn.getHeight() / 2.f);
        obj.setWidth(Constants.TILE_WIDTH);
        obj.setHeight(Constants.TILE_HEIGHT);

        bottom -= obj.getHeight() / 2.f;
        obj.setX(canvas.getZoom() * canvas.getWidth() / 2.f / scale.x - obj.getWidth() / 2.f);
        obj.setY(bottom);
        bottom -= obj.getHeight() / 2.f;

        spawnerList.add(new Spawner(obj, new Vector2(obj.getX(), obj.getY()), func));
        Spawner newSpawn = spawnerList.get(spawnerList.size() - 1);

        // Set the spawn position so it moves with the camera
        newSpawn.obstacle.setX(newSpawn.location.x + (camPos.x / scale.x));
        newSpawn.obstacle.setY(newSpawn.location.y + (camPos.y / scale.y));

        // Can't be selected or added to the game
        obj.inGame = false;
        obj.inHUD = true;
        obj.selectable = false;
    }

    /**
     * Iterates through each spawner, updating the position based on the camera.
     *
     * Also gets the object from spawn that was clicked. If none were clicked,
     * null is returned.
     *
     * @param camTarget The offset of the camera. Used to move the spawners so
     *                  they stay in the corner as the camera moves
     *
     * @return A spawned object, based on the clicked spawn object, or null, if
     *         no spawns were clicked. Although only one is returned, in a frame
     *         only one at most should be created.
     */
    public Obstacle update(Vector2 camTarget) {
        this.camPos = camTarget;

        Obstacle returnObj = null;
        for(int i = 0; i < spawnerList.size(); i++) {
            Spawner spawner = spawnerList.get(i);

            // Update the spawn position so it moves with the camera
            spawner.obstacle.setX(spawner.location.x + (camPos.x / scale.x));
            spawner.obstacle.setY(spawner.location.y + (camPos.y / scale.y));

            // Assuming only one spawner can be clicked in a frame. Checking
            // returnObj == null guarantees that
            if(returnObj == null && spawner.obstacle.checkClicked()) {
                // If the spawn was clicked since the last frame, then return
                // the corresponding object
                returnObj = spawner.callback.makeObject(spawner.obstacle.getX(), spawner.obstacle.getY(), spawner.lastObstacle);
                spawner.lastObstacle = returnObj;

                // TODO: make sure that this is using PooledList instead of making new objects
            }
        }
        return returnObj;
    }
}
