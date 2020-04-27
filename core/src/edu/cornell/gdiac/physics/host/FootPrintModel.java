package edu.cornell.gdiac.physics.host;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.physics.GameCanvas;


public class FootPrintModel {
    Vector2 position;
    private TextureRegion texture;
    private Color color;

    public FootPrintModel(TextureRegion texture, Vector2 position) {
        this.texture = texture;
        this.position = position;
        color = Color.GRAY;
    }

    public void draw(GameCanvas canvas) {
        canvas.draw(texture, color, texture.getRegionWidth()/2, texture.getRegionHeight()/2, position.x, position.y, 0, 0.2f, 0.2f);
    }
}
