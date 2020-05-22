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
    /** The true velocity if the spirit was shot */
    private Vector2 velocityRepresentedCache;
    /** The horizontal scaling of this arrow */
    private float sx;
    /** The texture for the arrow */
    private Texture arrTexture;
    /** Whether the arrow would have passed the minimum velocity for a shot*/
    private boolean pastThreshold;


    /** Creates an instance of an arrow to show direction */
    public ArrowModel(Texture arrText, Vector2 golemPos) {
        this.arrTexture = arrText;
        start = golemPos;
        currLoc = golemPos;
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
        else { c = Color.RED; }

//        // Draw the arrow
//        canvas.begin();
//        canvas.draw(arrTexture, c, 0, arrTexture.getHeight()/2, start.x + velocityRepresented.setLength(40f).x,
//                start.y + velocityRepresented.setLength(40f).y,  velocityRepresented.angleRad(), sx/5f,  1f/5f);
//        canvas.end();
//    }
//
//    public void setVelocityRepresented(Vector2 velocity, boolean metThreshold) {
//        // Set velocityRepresented to the actual velocity if the shot was fired
//        velocityRepresented = velocity;
//        // Set pastThreshold to whether it met the threshold or not
//        pastThreshold = metThreshold;
//
//        // Change scaling
//        if (velocityRepresented.len()/200 < 2.2) {
//            sx = velocityRepresented.len()/200;
//        }
//        else {
//            sx = 2.2f;
//        }
//    }
//}

        // Draw the arrow
        canvas.begin();
        System.out.println(velocityRepresented.len());
        if (velocityRepresented.len() < 50) {
            canvas.draw(arrTexture, c, 0, arrTexture.getHeight() / 2, start.x + velocityRepresented.x,
                    start.y + velocityRepresented.y, velocityRepresented.angleRad(), .25f, .25f);
            System.out.println("offset");
        }
        else {
            canvas.draw(arrTexture, c, 0, arrTexture.getHeight()/2, start.x + velocityRepresentedCache.setLength(50f).x,
                start.y + velocityRepresentedCache.setLength(50f).y,  velocityRepresented.angleRad(), sx,  .25f);
            System.out.println("stretch");
        }
        canvas.end();
    }

    public void setVelocityRepresented(Vector2 velocity, boolean metThreshold) {
        // Set velocityRepresented to the actual velocity if the shot was fired
        velocityRepresented = velocity.scl(.25f);
        velocityRepresentedCache = velocityRepresented.cpy();
        // Set pastThreshold to whether it met the threshold or not
        pastThreshold = metThreshold;

        // Change scaling
        if (velocityRepresented.len()/200 < 2.2) {
            sx = velocityRepresented.len()/200;
        }
        else {
            sx = 2.2f;
        }
    }
}
