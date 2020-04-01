
package edu.cornell.gdiac.physics.host;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import edu.cornell.gdiac.physics.GameCanvas;


public class ArrowModel {
    /** The start position of this arrow */
    private Vector2 start;
    /** The current position of the mouse in screen coordinates */
    private Vector2 currLoc;
    /** The horizontal scaling of this arrow */
    private float sx;
    /** The texture for the arrow */
    Texture arrTexture;

    /** Creates an instance of an arrow to show direction */
    public ArrowModel(Texture arrText, Vector2 robPos) {
        this.arrTexture = arrText;
        start = robPos;
        currLoc = new Vector2(start);
    }

    public void setCurrLoc(Vector2 mousePos, Vector2 hostPos) {
        currLoc = mousePos;
        start = hostPos;
    }

    public void draw (GameCanvas canvas) {
        // Get coordinates from screen to game coordinates
        Vector3 currPos = new Vector3(currLoc, 0);
        canvas.getCamera().unproject(currPos);

        // Calculate the direction vector
        Vector2 diffCurr =  new Vector2(currPos.x, currPos.y).sub(new Vector2((start)));

        // Determine the angle
        float ang = -6.28f + diffCurr.angleRad();

        // Change the scaling if necessary
        if (diffCurr.len()/100 > 1 && diffCurr.len()/100 < 4) {
            sx = diffCurr.len()/100;
        }
        else if (diffCurr.len()/100 < 1) {
            sx = 1;
        }

        if (!currLoc.equals(start)) {
            canvas.begin();
            canvas.draw(arrTexture, Color.PURPLE, arrTexture.getWidth()/2, arrTexture.getHeight()/2, start.x - diffCurr.setLength(75f).x, start.y - diffCurr.setLength(75f).y,  ang, sx, 1);
            canvas.end();
        }
    }
}