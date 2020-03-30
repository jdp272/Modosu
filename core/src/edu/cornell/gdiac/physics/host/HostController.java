package edu.cornell.gdiac.physics.host;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.*;

import edu.cornell.gdiac.physics.spirit.SpiritModel;
import edu.cornell.gdiac.physics.*;

public class HostController {

    /** Texture assets for the host */
    private TextureRegion hostTexture;

    /** List of all the hosts */
    private HostList hosts;
    /** The click position of the cursor */
    private Vector2 clickPosition;
    /** The vector created by the shot */
    private Vector2 shootVector;

    private InputController input;

    /** Constant to change the speed of golem movement */
    private static final float GOLEM_MOVEMENT_SPEED = 5.f;

    /** Minimum speed for shot spirit */
    private static final float MINIMUM_SHOT_SPEED = 4.f;

    /** Multiplier for velocity of spirit when shot */
    private static final float SHOOTING_MULTIPLIER = 0.05f;

    /** Minimum distance to target before going to next instruction, for autonomous mode */
    private static final float NEXT_INSTRUCTION_DIST = 0.005f;

    /** The number of ticks since we started this controller */
    private long ticks;

    /**
     * Creates and initialize a new instance of the rocket lander game
     *
     * The game has default gravity and other settings
     */
    public HostController(HostList r) {
        input = InputController.getInstance();
        clickPosition = new Vector2(-1,-1);
        hosts = r;
    }

    /**
     * Resets the status of the host.
     *
     * This method disposes of the host and creates a new one.
     */
    public void reset() {
        // should host controller have a list of hosts?
        // because initally all of these are gonna be AI
        // populateLevel();
    }


    /**
     * The core gameplay loop of this world.
     *
     * This method contains the specific update code for this mini-game. It does
     * not handle collisions, as those are managed by the parent class WorldController.
     * This method is called after input is read, but before collisions are resolved.
     * The very last thing that it should do is apply forces to the appropriate objects.
     *
     * @param dt Number of seconds since last animation frame
     */
    public void update(float dt, HostModel possessed, SpiritModel spirit) {

        //HostModel host = possessed;
        input = InputController.getInstance();

        if (possessed != null) {
            if (!spirit.getHasLaunched()) { spirit.setPosition(possessed.getPosition()); }

            if (possessed.incCurrentCharge()) {
                if (!spirit.getHasLaunched()) {
                    possessed.setVX(GOLEM_MOVEMENT_SPEED * input.getHorizontal());
                    possessed.setVY(GOLEM_MOVEMENT_SPEED * input.getVertical());
                    // Shooting the spirit
                    if (input.didTertiary() && clickPosition.x == -1 && clickPosition.y == -1) { // Clicked Mouse
                        spirit.setPosition(possessed.getPosition());
                        clickPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY()); //input.getCrossHair();
                    } else if (!input.didTertiary() && clickPosition.x != -1 && clickPosition.y != -1) { // Released Mouse -- Shoot
                        shootVector = new Vector2(Gdx.input.getX(), Gdx.input.getY());
                        shootVector = shootVector.sub(clickPosition);
                        shootVector.x = -shootVector.x;

                        clickPosition.x = -1;
                        clickPosition.y = -1;

                        float vx = SHOOTING_MULTIPLIER * shootVector.x;
                        float vy = SHOOTING_MULTIPLIER * shootVector.y;

                        spirit.setPosition(possessed.getPosition());
                        spirit.setVX(vx);
                        spirit.setVY(vy);

                        if (Math.abs(vx) > MINIMUM_SHOT_SPEED || Math.abs(vy) > MINIMUM_SHOT_SPEED) spirit.setHasLaunched(true);

                    } else if (input.didTertiary() && clickPosition.x != -1 && clickPosition.y != -1) {
                        // Arrow Direction?
                        spirit.setPosition(possessed.getPosition());
                    }
                } else {
                    possessed.setVX(0);
                    possessed.setVY(0);
                }
            }
            else {
                System.out.println("THE GOLEM HAS BLOWN UP.");
                possessed.setVX(0);
                possessed.setVY(0);
                // change texture because it blew up
            }
        }

//        System.out.println();

        //update other robots
        for(HostModel r: hosts){
//            System.out.println("has instructions: " + r.getInstructionList() != null);

            Vector2 n = r.getInstruction();
            Vector2 curr = r.getPosition();
            if(r != possessed && Math.abs(curr.x - n.x) < NEXT_INSTRUCTION_DIST && Math.abs(curr.y - n.y) < NEXT_INSTRUCTION_DIST && !r.beenPossessed()){
//                System.out.println("curr: " + curr + ", n: " + n);

                //go to next instruction
                r.nextInstruction();
                n = r.getInstruction();
                // float l = (float)Math.sqrt(Math.pow(n.x-curr.x,2) + Math.pow(n.y-curr.y,2) );

                r.setVX(Math.signum(n.x - curr.x) * GOLEM_MOVEMENT_SPEED);
                r.setVY(Math.signum(n.y - curr.y) * GOLEM_MOVEMENT_SPEED);

            }
            else if(r != possessed && !r.beenPossessed()){
//                System.out.println("Golem moving");

                r.setVX(Math.signum(n.x - curr.x) * GOLEM_MOVEMENT_SPEED);
                r.setVY(Math.signum(n.y - curr.y) * GOLEM_MOVEMENT_SPEED);
            }
            else{
                //keep going i guess, don't really have to change anything

            }
        }

        // Update Animations

        // If we use sound, we must remember this.
        //SoundController.getInstance().update();
    }

}
