package edu.cornell.gdiac.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.gdiac.util.ScreenListener;

public class Credits extends WorldController implements Screen {

    /** Declaration for new Stage */
    private Stage stage;
    private Viewport viewport;

    /** Textures */
    private Texture background;
    private Texture backButtonTexture;
    private Texture backButtonClickedTexture;

    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;

    /** The file name for the assets */
    private static final String BACKGROUND_FILE = "shared/creditsbackground.png";
    private static final String BACK_BUTTON_FILE = "shared/backbutton.png";
    private static final String BACK_BUTTON_CLICKED_FILE = "shared/backbuttonclicked.png";

    /** Constants*/
    private static final int PADDING = 20;

    /** Variables */
    private boolean backButtonClicked;

    /** INITIALIZATION */
    public Credits() {
        backButtonClicked = false;
    }

    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    //////////////////////////////////////////////// SCREEN METHODS ///////////////////////////////////////////////////
    @Override
    public void show() {
        viewport = new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        stage = new Stage(viewport);

        /** Background */
        background = new Texture(Gdx.files.internal(BACKGROUND_FILE));
        backButtonTexture = new Texture(Gdx.files.internal(BACK_BUTTON_FILE));
        backButtonClickedTexture = new Texture(Gdx.files.internal(BACK_BUTTON_CLICKED_FILE));
        Image credits = new Image(background);
        stage.addActor(credits);

        /** Back Button */
        Button.ButtonStyle backStyle = new Button.ButtonStyle();
        backStyle.up = new TextureRegionDrawable(new TextureRegion(backButtonTexture));
        backStyle.over = new TextureRegionDrawable(new TextureRegion(backButtonClickedTexture));
        backStyle.down = new TextureRegionDrawable(new TextureRegion(backButtonClickedTexture));
        Button back = new Button(backStyle);
        back.setPosition(PADDING, Gdx.graphics.getHeight() - (4 *PADDING));

        back.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("BACK BUTTON CLICKED");
                backButtonClicked = true;
            }
        });

        stage.addActor(back);

        /** Set input processor to stage for draw to work */
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        /** Checks if any buttons are clicked */
        if (backButtonClicked) {
            listener.exitScreen(this, EXIT_MENU);
            backButtonClicked = false;
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    ///////////////////////////////////////////// WORLD CONTROLLER METHODS /////////////////////////////////////////////
    @Override
    public void dispose() {
        background.dispose();
        backButtonTexture.dispose();
        stage.dispose();
    }

    @Override
    public void reset() {
    }

    @Override
    public void update(float dt) {

    }
}
