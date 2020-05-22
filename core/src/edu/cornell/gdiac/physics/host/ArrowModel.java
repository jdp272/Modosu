package edu.cornell.gdiac.physics.host;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import edu.cornell.gdiac.physics.GameCanvas;


public class ArrowModel {
    /** The start position of this arrow */
    private Vector2 start;
    /** The true velocity if the spirit was shot */
    private Vector2 velocityRepresented;
    /** The true velocity if the spirit was shot cache */
    private Vector2 velocityRepresentedCache;
    /** The horizontal scaling of this arrow */
    private float sx;
    /** The texture for the arrow head*/
    private Texture arrTextureHead;
    /** The texture for the arrow dash*/
    private Texture arrTextureDash;
    /** Whether the arrow would have passed the minimum velocity for a shot*/
    private boolean pastThreshold;


    /** Creates an instance of an arrow to show direction */
    public ArrowModel(Texture arrTextHead, Texture arrTextDash, Vector2 golemPos, Vector2 v, boolean b) {
        arrTextureHead = arrTextHead;
        arrTextureDash = arrTextDash;
        start = golemPos;
        setVelocityRepresented(v, b);
        velocityRepresented = new Vector2(0,0);
        velocityRepresentedCache = new Vector2(0,0);

    }

    public void setCurrLoc(Vector2 golemPos) {
        start = golemPos;
    }

    public void draw (GameCanvas canvas) {
        Color c;

        // Determine the color based on whether the velocity passes threshold
        if (pastThreshold) { c =Color.WHITE; }
        else { c = new Color(0, 0, 0, 0.3f); }

        // Draw the arrow
        canvas.begin();
        float lengthArrow = sx * arrTextureDash.getWidth();
        canvas.draw(arrTextureDash, c, 0, arrTextureDash.getHeight()/2, start.x + velocityRepresentedCache.setLength(15f).x,
                start.y + + velocityRepresentedCache.setLength(15f).y,  velocityRepresented.angleRad(), sx, .10f);

        canvas.draw(arrTextureHead, c, arrTextureHead.getWidth()/2, arrTextureHead.getHeight()/2, start.x + velocityRepresented.setLength(lengthArrow).x,
                start.y + velocityRepresented.setLength(lengthArrow).y,  velocityRepresented.angleRad(), .22f, .22f);
        canvas.end();
    }

    public void setVelocityRepresented(Vector2 velocity, boolean metThreshold) {
        // Set velocityRepresented to the actual velocity if the shot was fired
        velocityRepresented = velocity;
        velocityRepresentedCache = velocity;
        // Set pastThreshold to whether it met the threshold or not
        pastThreshold = metThreshold;

        // Change scaling
        if (velocityRepresented.len()/200 < 1.33) {
            sx = velocityRepresented.len()/200;
        }
        else {
            sx = 1.33f;
        }
    }
}