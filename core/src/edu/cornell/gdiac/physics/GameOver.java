package edu.cornell.gdiac.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import edu.cornell.gdiac.util.ScreenListener;

import java.nio.ByteBuffer;


public class GameOver extends WorldController implements Screen {

    /** Basic variables to keep track of state */
    private static boolean isFail;
    private boolean retryButtonClicked;
    private boolean menuButtonClicked;
    private boolean nextButtonClicked;
    private int columnNum;
    private String labelText;
    private TextureRegion golemTexture;

    /** Scene2D Variables */
    private Stage stage;
    private ScreenViewport viewport;
    private TextureAtlas textureAtlas;
    private Skin skin;
    private Table table;
    private Label levelLabel;
    private Image golemImg;
    private Image semiBackground;
    private Image screenShotImage;

//    private Pixmap pixmap;
//    private Texture screenShotTexture;
    private Texture transparentTexture;

    /** Is added in WorldController */
    //public static TextureRegion screenShotTexture;
//    public static Pixmap screenShotPixmap;

    /** Constants */
    private static final String WIN_LEVEL_TEXT = "level complete!";
    private static final String FAIL_LEVEL_TEXT = "level failed";
    private static final int ICON_SIZE_SMALL = 60;
    private static final int ICON_SIZE_BIG = 75;
    private static final float ALPHA_BKG = .75f;
    private static final int TITLE_COLOR = 0xbef5fd;

    /** Texture files for game icons */
    private static final String TEXTURE_ATLAS_FILE = "shared/gameIcons.txt";
    private static final String WINGOLEM_ICON = "shared/GameComplete/wingolem_icon.png";
    private static final String LOSEGOLEM_ICON = "shared/GameComplete/wingolem_icon.png";

    /** Skin file names */
    private static final String sMENU = "menu_icon";
    private static final String sNEXT = "next_icon";
    private static final String sRETRY = "retry_icon";
    private static final String sMENU_CLICKED = "menu_clicked_icon";
    private static final String sNEXT_CLICKED = "next_clicked_icon";
    private static final String sRETRY_CLICKED = "retry_clicked_icon";

    /** Texture assets for game icons */
    private TextureRegion winGolemTexture;
    private TextureRegion loseGolemTexture;

    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;

    /** Track asset loading from all instances and subclasses */
    private AssetState assetState = AssetState.EMPTY;

    /**
     * Preloads the assets for this controller.
     *
     * To make the game modes more for-loop friendly, we opted for nonstatic loaders
     * this time.  However, we still want the assets themselves to be static.  So
     * we have an AssetState that determines the current loading state.  If the
     * assets are already loaded, this method will do nothing.
     *
     * @param manager Reference to global asset manager.
     */
    public void preLoadContent(AssetManager manager) {
        if (assetState != AssetState.EMPTY) {
            return;
        }

        assetState = AssetState.LOADING;

        /** Load/Add the Texture Atlas */
        manager.load(TEXTURE_ATLAS_FILE, TextureAtlas.class);
        assets.add(TEXTURE_ATLAS_FILE);

        manager.load(WINGOLEM_ICON, Texture.class);
        assets.add(WINGOLEM_ICON);
        manager.load(LOSEGOLEM_ICON, Texture.class);
        assets.add(LOSEGOLEM_ICON);

        super.preLoadContent(manager);
    }

    /**
     * Loads the assets for this controller.
     *
     * To make the game modes more for-loop friendly, we opted for nonstatic loaders
     * this time.  However, we still want the assets themselves to be static.  So
     * we have an AssetState that determines the current loading state.  If the
     * assets are already loaded, this method will do nothing.
     *
     * @param manager Reference to global asset manager.
     */
    public void loadContent(AssetManager manager) {
        if (assetState != WorldController.AssetState.LOADING) {
            return;
        }

        textureAtlas = manager.get(TEXTURE_ATLAS_FILE);
        winGolemTexture = createTexture(manager,WINGOLEM_ICON,false);
        loseGolemTexture = createTexture(manager,LOSEGOLEM_ICON,false);

        super.loadContent(manager);
        assetState = AssetState.COMPLETE;
    }

    public GameOver() {
        nextButtonClicked = false;
        retryButtonClicked = false;
        menuButtonClicked = false;
        isFail = true;
        //screenShotTexture = null;
    }

    public static void setFail(boolean isFailure) {
        isFail = isFailure;
    }

    /**
     * Sets the ScreenListener for this mode
     *
     * The ScreenListener will respond to requests to quit.
     */
    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
        Gdx.input.setInputProcessor(stage);
    }

    /************************* SCREEN METHODS *************************/
    @Override
    public void show() {
        /** Initialization */
        viewport = new ScreenViewport();
        stage = new Stage(viewport);
        skin = new Skin();
        skin.addRegions(textureAtlas);

        /** Create table */
        table = new Table();
        table.center();
        table.setFillParent(true);

        // TODO: REMOVE IF THINGS START GETTING LAGGY
        /** Create semi-transparent background */
        // Flip the pixmap upside down
//        ByteBuffer pixels = screenShotPixmap.getPixels();
//        int w = Gdx.graphics.getWidth();
//        int h = Gdx.graphics.getHeight();
//        int numBytes = w * h * 4;
//        byte[] lines = new byte[numBytes];
//        int numBytesPerLine = w * 4;
//        for(int i = 0; i < h; i++) {
//            pixels.position((h - i - 1) * numBytesPerLine);
//            pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
//        }
//        pixels.clear();
//        pixels.put(lines);
//        pixels.clear();
//
//        // Create image of pixmap
//        screenShotTexture = new Texture(screenShotPixmap);
//        screenShotImage = new Image(screenShotTexture);
//        stage.addActor(screenShotImage);
//
//        pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
//        pixmap.setColor(Color.BLACK);
//        pixmap.fillRectangle(0, 0, 1, 1);
//        transparentTexture = new Texture(pixmap);
//
//        semiBackground = new Image(transparentTexture);
//        semiBackground.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
//        semiBackground.getColor().a= ALPHA_BKG;
//        stage.addActor(semiBackground);

        /** Change variables depending on WIN vs FAIL */
        columnNum = isFail ? 2 : 3;
        labelText = isFail ? FAIL_LEVEL_TEXT : WIN_LEVEL_TEXT;
        golemTexture = isFail ? winGolemTexture : loseGolemTexture;

        /** Level Label */
        Label.LabelStyle font = new Label.LabelStyle(displayFont, Color.WHITE);
        levelLabel = new Label(labelText, font);
        table.add(levelLabel).colspan(columnNum);

        /** Add Row in Table */
        table.row().padTop(30);

        /** Golem Image */
        golemImg = new Image(winGolemTexture);
        table.add(golemImg).colspan(columnNum);

        /** Add Row in Table */
        table.row().uniform().pad(30);

        /** Menu Button */
        Button.ButtonStyle menuStyle = new Button.ButtonStyle();
        menuStyle.up = skin.getDrawable(sMENU);
        menuStyle.down = skin.getDrawable(sMENU_CLICKED);
        Button menu = new Button(menuStyle);

        menu.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("MENU BUTTON CLICKED");
                menuButtonClicked = true;
            }
        });

        table.add(menu).width(ICON_SIZE_SMALL).height(ICON_SIZE_SMALL);

        /** Next Button */
        if (!isFail) {
            Button.ButtonStyle nextStyle = new Button.ButtonStyle();
            nextStyle.up = skin.getDrawable(sNEXT);
            nextStyle.down = skin.getDrawable(sNEXT_CLICKED);
            Button next = new Button(nextStyle);

            next.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("NEXT BUTTON CLICKED");
                    nextButtonClicked = true;
                }
            });

            table.add(next).width(ICON_SIZE_BIG).width(ICON_SIZE_BIG).padLeft(40);
        }

        /** Retry Button */
        Button.ButtonStyle retryStyle = new Button.ButtonStyle();
        retryStyle.up = skin.getDrawable(sRETRY);
        retryStyle.down = skin.getDrawable(sRETRY_CLICKED);
        Button retry = new Button(retryStyle);

        retry.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("RETRY BUTTON CLICKED");
                retryButtonClicked = true;
            }
        });

        table.add(retry).width(ICON_SIZE_SMALL).height(ICON_SIZE_SMALL);

        /** Adds the Table */
        stage.addActor(table);

        /** Set input processor to stage for draw to work */
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        /** Checks if any buttons are clicked */
        if (menuButtonClicked) {
            listener.exitScreen(this, EXIT_MENU, getSound());
            dispose();
        }
        else if (retryButtonClicked) {
            listener.exitScreen(this, EXIT_NEXT, getSound());
            dispose();
        }
        else if (nextButtonClicked) {
            listener.exitScreen(this, EXIT_NEXT, getSound());
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

    @Override
    public void dispose() {
        stage.dispose();
//        pixmap.dispose();
//        screenShotPixmap.dispose();
//        screenShotTexture.dispose();
//        transparentTexture.dispose();
    }

    /************************* WORLD CONTROLLER METHODS *************************/
    @Override
    public void reset() {
        nextButtonClicked = false;
        retryButtonClicked = false;
        menuButtonClicked = false;
    }

    @Override
    public void update(float dt) {

    }

}
