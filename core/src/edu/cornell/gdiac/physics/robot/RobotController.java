package edu.cornell.gdiac.physics.robot;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import edu.cornell.gdiac.physics.InputController;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;

public class RobotController {


    public void reset() {}

    public void update(float delta) {
        float thrust = rocket.getThrust();
        InputController input = new InputController();
        input.readInput(bounds, scale);
        rocket.setVX(thrust * input.getHorizontal());
        rocket.setVY(thrust * input.getVertical());

        if(input.didTertiary() && CLICK_POS.x == -1 && CLICK_POS.y == -1){
            CLICK_POS = input.getCrossHair();

        }else if (!input.didTertiary() && CLICK_POS.x != -1 && CLICK_POS.y != -1){
            SHOOT_VEC = input.getCrossHair().sub(CLICK_POS);
            CLICK_POS.x = -1;
            CLICK_POS.y = -1;

            TextureRegion texture = spiritTexture;
            float dwidth  = texture.getRegionWidth()/scale.x;
            float dheight = texture.getRegionHeight()/scale.y;
            BoxObstacle spirit = new BoxObstacle(rocket.getX(),rocket.getY(),dwidth,dheight);
            spirit.setDensity(CRATE_DENSITY);
            spirit.setFriction(CRATE_FRICTION);
            spirit.setRestitution(BASIC_RESTITUTION);
            spirit.setName("spirit");
            spirit.setDrawScale(scale);
            spirit.setTexture(texture);
            float vx = (thrust/3)*(-2)*(SHOOT_VEC.x);
            float vy = (thrust/3)*(-2)*(SHOOT_VEC.y);
            spirit.setVX(vx);
            spirit.setVY(vy);
            Filter filter = spirit.getFilterData();
            filter.groupIndex = -1;
            spirit.setFilterData(filter);
            spirit.alive = 60;
            spirit.setRestitution(0.8f);
            addQueue.add(spirit);
        }
    }


}
