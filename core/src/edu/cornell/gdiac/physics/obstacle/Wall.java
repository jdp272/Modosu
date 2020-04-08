package edu.cornell.gdiac.physics.obstacle;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import edu.cornell.gdiac.util.FilmStrip;

public class Wall extends BoxObstacle {

    public int wall;

    /**
     * The texture strip for the wall
     */
    protected FilmStrip wallStrip;


    public Wall(float width, float height) {
        this(0,0,width, height);
    }

    public Wall(float x, float y, float width, float height) {
        super(x, y, width, height);
    }


    public void setWall(int n){
        if(n>23){
            wall = 0;
        }else if(n < 0){
            wall = 23;
        }else {
            wall = n;
        }
        if(wall > 17 && wall < 24 ){
            PolygonShape s = new PolygonShape();
            s.setAsBox(getWidth()/2,getHeight()/4,new Vector2(0, getHeight()/4),0);
            shape = s;
        }
        else if(wall < 4){
            PolygonShape s = new PolygonShape();
            s.setAsBox(0,0,new Vector2(0, 0),0);
            shape = s;
        }
        else{
            PolygonShape s = new PolygonShape();
            s.setAsBox(getWidth()/2,getHeight()/2,new Vector2(0, 0),0);
            shape = s;
        }
        wallStrip.setFrame(wall);
    }


    public void setWallLvlDsgn(int n){
        if(n>23){
            wall = 0;
        }else if(n < 0){
            wall = 23;
        }else {
            wall = n;
        }
        PolygonShape s = new PolygonShape();
        s.setAsBox(getWidth()/2,getHeight()/2,new Vector2(0, 0),0);
        shape = s;
        wallStrip.setFrame(wall);
    }


    /**
     * sets the FilmStrip for the charged host and the corresponding gauge
     * @param strip for the charged host
     */
    public void setWallStrip (FilmStrip strip) {
        wallStrip = strip;
        wallStrip.setFrame(20);
        this.setTexture(strip);
    }


}
