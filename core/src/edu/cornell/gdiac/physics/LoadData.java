package edu.cornell.gdiac.physics;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Json;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.util.PooledList;

public class LoadData extends GamePlayController {

    private int nRobots;

    /** reads json data, creates objects and returns them to
     * the gameplay controller
     * @param json
     * @return Obstacle array
     */
    public Obstacle[] parse(Json json){

        return null;
    }

    public void preLoadContent(AssetManager manager){}

    public PooledList<Obstacle> loadContent(int level) {}

    public int getnRobots(){return nRobots;}

    public void reset(int level){}

}
