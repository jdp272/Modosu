
package edu.cornell.gdiac.physics.host;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import edu.cornell.gdiac.physics.GameCanvas;


public class ArrowModel {
    /** The start position of this arrow */
    private Vector2 start;
    /** The current position of the mouse in screen coordinates */
    private Vector2 currLoc;
    /** The true velocity if the spirit was shot */
    private Vector2 velocityRepresented;
    /** The horizontal scaling of this arrow */
    private float sx;
    /** The texture for the arrow */
    Texture arrTexture;

    /** Creates an instance of an arrow to show direction */
    public ArrowModel(Texture arrText, Vector2 robPos) {
        this.arrTexture = arrText;
        start = robPos;
        currLoc = robPos;
        velocityRepresented = new Vector2(0,0);
    }

    public void setCurrLoc(Vector2 mousePos, Vector2 hostPos) {
        currLoc = mousePos;
    }

    public void draw (GameCanvas canvas) {
        if (velocityRepresented.len()/100 > .2) {
            canvas.begin();
            canvas.draw(arrTexture, Color.PURPLE, arrTexture.getWidth()/2, arrTexture.getHeight()/2, start.x + velocityRepresented.setLength(75f).x, start.y + velocityRepresented.setLength(75f).y,  velocityRepresented.angleRad(), sx, 1);
            canvas.end();
        }
    }

    public void setVelocityRepresented(Vector2 velocity) {
        // Set velocityRepresented to the actual velocity if the shot was fired
        velocityRepresented = velocity;

        // Change scaling
        if (velocityRepresented.len()/100 > 1 && velocityRepresented.len()/100 < 4.4) {
            sx = velocityRepresented.len()/100;
        }
        else if (velocityRepresented.len()/100 < 1) {
            sx = 1;
        }

    }
}