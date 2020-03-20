package edu.cornell.gdiac.physics.robot;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.physics.GameCanvas;


public class ArrowModel {
    private Vector2 start;
    private Vector2 offset;
    private Vector2 prevLoc;
    private Vector2 currLoc;
    private float width;
    private float height;
    private float sx;
    private float sy;
    private float ang;

    Texture arrTexture;

    public ArrowModel(Texture arrText, Vector2 clickPosStart, Vector2 robPos) {
        this.arrTexture = arrText;
        start = robPos;
        this.width = width;
        this.height = height;
        offset = new Vector2(0,0);
        ang = robPos.sub(clickPosStart).angleRad() ;
        sx = 1;
        sy = 1;
        prevLoc = null;
        currLoc = new Vector2(start);
    }

    public void setScale(Vector2 pos){
        prevLoc = currLoc;
        currLoc = pos;
        Vector2 diffCurr =  new Vector2(currLoc).sub(new Vector2((start)));

//        if (pos.y > start.y || pos.x > start.x){
//            sy = sy + .01f;
//        }
//        else if (pos.y < start.y || pos.x < start.x){
//            sy = sy - .01f;
//        }
        System.out.println(diffCurr);
        //System.out.println(diffCurr.angle());
        ang = -6.28f + diffCurr.angleRad();
        offset = diffCurr.setLength(75f);
    }

    public void draw (GameCanvas canvas) {
        canvas.begin();
        canvas.draw(arrTexture, Color.PURPLE, arrTexture.getWidth()/2, arrTexture.getHeight()/2, start.x - offset.x, start.y - offset.y,  ang, sx, sy);
        canvas.end();
    }
}
