package edu.cornell.gdiac.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.gdiac.util.MusicController;
import edu.cornell.gdiac.util.SoundController;

public class HUD {

    /** File Locations */
    private static final String FONT_FILE = "shared/AveriaSerifLibre.ttf";
    private static final String TEXTURE_ATLAS_FILE = "shared/paused/pause.txt";
    private static String INDICATOR_FONT_FILE = "shared/Asul.ttf";
    private static String HUD_FILE = "shared/hud.png";

    /** Skin file names */
    private static final String sBACKGROUND = "background";
    private static final String sKNOB = "knob";
    private static final String sBAR = "bar";
    private static final String sMENU = "menu";
    private static final String sPLAY = "play";
    private static final String sRETRY = "retry";
    private static final String sMENU_CLICKED = "menu_clicked";
    private static final String sPLAY_CLICKED = "play_clicked";
    private static final String sRETRY_CLICKED = "retry_clicked";

    /** Constants for HUD */
    private static int FONT_SIZE = 24;
    private static int FONT_COLOR = 0xc0bab2;
    private static String GOLEM_PADDING = "            ";
    private static String TIMER_PADDING = "                                      ";

    /** Constants for Pause  */
    private static final int ICON_SIZE_SMALL = 50;
    private static final int ICON_SIZE_BIG = 60;
    private static final int FONT_TITLE_SIZE = 50;
    private static final int FONT_SUBTITLE_SIZE = 48;
    private static final String TITLE_TXT = "";
    private static final String MUSIC_TXT = "";
    private static final String SFX_TXT = "";

    /** Golem & Time Tracking Variables */
    private static int numCurrentHosts;
    private static int numTotalHosts;
    private static float timeCount;
    private static int worldTimer;
    private static int minutes;
    private static int seconds;
    private static boolean pauseButtonClicked;

    /** Pause Screen Variables */
    private boolean isPaused;
    private boolean retryButtonClicked;
    private boolean menuButtonClicked;
    private boolean playButtonClicked;
    private float pauseHeight;
    private float pauseWidth;

    /** Declaration for new Stage */
    private static Stage stage;
    private Viewport viewport;

    /** Scene2D Widgets for Indicators */
    private Table table;
    private Stack stack;
    private static Label timeLabel;
    private static Label hostCounterLabel;
    private Texture hostCounterTexture;

    /** Scene 2D Widgets for Pause */
    private Skin skin;
    private Group group;
    private Table pauseTable;
    private Slider musicSlider;
    private Slider sfxSlider;
    private Label titleLabel;
    private Label musicLabel;
    private Label sfxLabel;
    private TextureAtlas textureAtlas;

    /** Initialization */
    public HUD() {
        viewport = new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        stage = new Stage(viewport);

        /*************************************************** HUD CREATION *********************************************/
        /* Setting Variables */
        numCurrentHosts = 0; numTotalHosts = 0;
        minutes = 0; seconds = 0;
        timeCount = 0; worldTimer = 0;
        pauseButtonClicked = false;

        /* Create label for the text */
        Label.LabelStyle counterLS = new Label.LabelStyle();

        /* Use Asul Font Font */
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(INDICATOR_FONT_FILE));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = FONT_SIZE;
        BitmapFont asulFont = generator.generateFont(parameter);
        generator.dispose();

        counterLS.font = asulFont;
        counterLS.fontColor = new Color(Color.SKY);
        hostCounterLabel = new Label(GOLEM_PADDING + numCurrentHosts+ " / " + numTotalHosts, counterLS);

        /* For the text and the image to overlay */
        stack = new Stack();
        hostCounterTexture = new Texture(Gdx.files.internal(HUD_FILE));
        Image counterBackground = new Image(hostCounterTexture);
        stack.add(counterBackground);
        stack.add(hostCounterLabel);

        /* Create timer counter */
        timeLabel = new Label(TIMER_PADDING + worldTimer, counterLS);
        stack.add(timeLabel);

        /* Create golem table to add stack */
        table = new Table();
        table.top();
        table.setFillParent(true);
        table.add(stack).expandX().padTop(5).padLeft(5).left();
        // golemTable.setDebug(true);

        /* Pause Button */
        Button.ButtonStyle pauseStyle = new Button.ButtonStyle();
        pauseStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("shared/pauseButton.png")));
        Button pause = new Button(pauseStyle);

        pause.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                pauseButtonClicked = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        table.add(pause).width(60).height(60).padTop(5).padRight(10).right();
        stage.addActor(table);

        /*************f************************************* PAUSE SCREEN **********************************************/

        skin = new Skin();
        textureAtlas = new TextureAtlas(Gdx.files.internal(TEXTURE_ATLAS_FILE));
        skin.addRegions(textureAtlas);

        /** Setting Variables Pause */
        isPaused = false;
        retryButtonClicked = false;
        menuButtonClicked = false;
        playButtonClicked = false;

        pauseHeight = skin.getDrawable(sBACKGROUND).getMinHeight();
        pauseWidth = skin.getDrawable(sBACKGROUND).getMinWidth();

        /** Create Table & Set Size/Location */
        group = new Group();
        pauseTable = new Table();
        pauseTable.center();
        pauseTable.setHeight(pauseHeight);
        pauseTable.setWidth(pauseWidth);
        pauseTable.setBackground(skin.getDrawable(sBACKGROUND));
        pauseTable.setPosition(Gdx.graphics.getWidth()/2 - pauseWidth/2 + 40,Gdx.graphics.getHeight()/2 - pauseHeight/2);
        pauseTable.padRight(80);

        /** Font Styles */
        BitmapFont font = new BitmapFont();

        /** Title Label */
        Label.LabelStyle titleLS = new Label.LabelStyle(font, Color.WHITE);
        titleLabel = new Label(TITLE_TXT, titleLS);
        pauseTable.add(titleLabel).colspan(3);

        /** Add Row in Table */
        pauseTable.row().padTop(70);

        /** Music Label */
        Label.LabelStyle subTitleLS = new Label.LabelStyle(font, Color.WHITE);
        musicLabel = new Label(MUSIC_TXT, subTitleLS);
        pauseTable.add(musicLabel).colspan(1).padLeft(20);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = skin.getDrawable(sBAR);
        sliderStyle.knob = skin.getDrawable(sKNOB);
        musicSlider = new Slider(0, 100, 1f, false, sliderStyle);
        musicSlider.setValue(MusicController.getInstance().getVolume()*100f);
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) { MusicController.getInstance().setVolume(musicSlider.getValue()/100f); }
        });

        pauseTable.add(musicSlider).colspan(2);

        /** Add New Row */
        pauseTable.row().padTop(40);

        /** SoundFX Label */
        sfxLabel = new Label(SFX_TXT, subTitleLS);
        pauseTable.add(sfxLabel).colspan(1).padLeft(40); // .colspan(columnNum);

        /** SFX Slider */
        sfxSlider = new Slider(0, 100, 1f, false, sliderStyle);
        sfxSlider.setValue(SoundController.getInstance().getVolume());
        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) { SoundController.getInstance().setVolume(sfxSlider.getValue()/100f); }
        });

        pauseTable.add(sfxSlider).colspan(2);

        /** Add New Row */
        pauseTable.row().uniform().padTop(40);

        /** Menu Button */
        Button.ButtonStyle menuStyle = new Button.ButtonStyle();
        menuStyle.up = skin.getDrawable(sMENU);
        //menuStyle.down = skin.getDrawable(sMENU_CLICKED);
        Button menu = new Button(menuStyle);

        menu.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) { menuButtonClicked = true; }
        });

        pauseTable.add(menu).width(ICON_SIZE_SMALL).height(ICON_SIZE_SMALL).padRight(60);

        /** Retry Button */
        Button.ButtonStyle retryStyle = new Button.ButtonStyle();
        retryStyle.up = skin.getDrawable(sRETRY);
        //retryStyle.down = skin.getDrawable(sRETRY_CLICKED);
        Button retry = new Button(retryStyle);

        retry.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) { retryButtonClicked = true; }
        });

        pauseTable.add(retry).width(ICON_SIZE_BIG).height(ICON_SIZE_BIG);

        /** Play Button */
        Button.ButtonStyle playStyle = new Button.ButtonStyle();
        playStyle.up = skin.getDrawable(sPLAY);
        //nextStyle.down = skin.getDrawable(sNEXT_CLICKED);
        Button play = new Button(playStyle);

        play.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                playButtonClicked = true;
            }
        });

        pauseTable.add(play).width(ICON_SIZE_SMALL).width(ICON_SIZE_SMALL).padLeft(60);

        /** SET INPUT PROCESSOR TO THIS SCREEN */
        Gdx.input.setInputProcessor(stage);
    }


    /** Gets the Stage */
    public static Stage getStage() { return stage; }

    /** Updates the Timer */
    public static void update(float dt) {
        timeCount += dt;

        if (timeCount >= 1) {
            worldTimer++;
            minutes = (int) Math.floor(worldTimer / 60);
            seconds = worldTimer - (minutes * 60);
            timeLabel.setText(String.format(TIMER_PADDING + "%d:%02d", minutes, seconds));
            timeCount = 0;
        }
    }

    /** Increments the number of current hosts possessed */
    public static void incrementCurrHosts() {
        if (numCurrentHosts < numTotalHosts) {
            numCurrentHosts+= 1;
            hostCounterLabel.setText(GOLEM_PADDING + numCurrentHosts + " / " + numTotalHosts);
        }
    }

    /** Sets the total number of hosts that need to be possessed */
    public void setNumTotalHosts(int num) {
        numTotalHosts = num;
        hostCounterLabel.setText(GOLEM_PADDING + numCurrentHosts + " / " + numTotalHosts);
    }

    /** Returns whether the pause button has been clicked */
    public boolean getPauseClicked() {
        if (pauseButtonClicked) {
            pauseButtonClicked = false;
            return true;
        }

        return false;
    }

    public void pauseGame() {
        retryButtonClicked = false;
        menuButtonClicked = false;
        playButtonClicked = false;

        isPaused = true;
        group.addActor(pauseTable);
        stage.addActor(group);

        musicSlider.setValue(MusicController.getInstance().getVolume()*100f);
        sfxSlider.setValue(SoundController.getInstance().getVolume()*100f);
    }

    /** Returns whether menu icon was clicked */
    public boolean getMenuClicked() {
        return menuButtonClicked;
    }

    /** Returns whether retry icon was clicked */
    public boolean getRetryClicked() {
        return retryButtonClicked;
    }

    /** Returns whether play icon was clicked */
    public boolean getPlayClicked() {
        return playButtonClicked;
    }

    /** When game is resumed */
    public void resumeGame(){
        if ( isPaused ){
            reset();
        }
    }

    public void reset() {
        group.clear();
        isPaused = false;
        retryButtonClicked = false;
        menuButtonClicked = false;
        playButtonClicked = false;
    }

    /** Clears the HUD */
    public void clearHUD() {
        minutes = 0;
        seconds = 0;
        timeCount = 0;
        worldTimer = 0;
        numTotalHosts = 0;
        numCurrentHosts = 0;
        pauseButtonClicked = false;
        group.clear();
        isPaused = false;
        timeLabel.setText(String.format(TIMER_PADDING + "%d:%02d", minutes, seconds));
    }

    /** Disposes the elements used */
    public void dispose() {
        stage = null;
        textureAtlas.dispose();
        hostCounterTexture.dispose();
    }
}
