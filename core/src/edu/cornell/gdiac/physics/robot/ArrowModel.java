package edu.cornell.gdiac.physics.spirit;

import edu.cornell.gdiac.physics.obstacle.BoxObstacle;


public class SpiritModel extends BoxObstacle {

    public int bounces;

    public SpiritModel(float x, float y) {
        super(x, y, 10, 10);
        bounces = 4;
    }

    public SpiritModel(float x, float y, int b) {
        super(x, y, 10, 10);
        bounces = b;
    }

    public SpiritModel(float x, float y, float width, float height, int b) {
        super(x, y, width, height);
        bounces = b;
    }



    public boolean decBounces(){
        if(bounces == 0){
            return false;
        }else{
            bounces--;
            return true;
        }
    }
}
