package edu.cornell.gdiac.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import edu.cornell.gdiac.util.MusicController;
import edu.cornell.gdiac.util.SoundController;

public class Pause {

    /**  Scene 2D Elements */
    private Skin skin;
    private Stage stage;
    private Table table;
    private Group group;
    private Viewport viewport;
    private Slider musicSlider;
    private Slider sfxSlider;
    private Label titleLabel;
    private Label musicLabel;
    private Label sfxLabel;
    private TextureAtlas textureAtlas;


    /** Constants */
    private static final int ICON_SIZE_SMALL = 50;
    private static final int ICON_SIZE_BIG = 60;
    private static final int FONT_TITLE_SIZE = 50;
    private static final int FONT_SUBTITLE_SIZE = 48;
    private static final String TITLE_TXT = "paused";
    private static final String MUSIC_TXT = "music";
    private static final String SFX_TXT = "sound fx";


    /** File Locations */
    private static final String FONT_FILE = "shared/AveriaSerifLibre.ttf";
    private static final String TEXTURE_ATLAS_FILE = "shared/paused/pause.txt";

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

    /** Static variables */
    private boolean isPaused;
    private boolean retryButtonClicked;
    private boolean menuButtonClicked;
    private boolean playButtonClicked;
    private float pauseHeight;
    private float pauseWidth;


    public Pause()  {
        /** Initialization */
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport);
        group = new Group();
        skin = new Skin();
        textureAtlas = new TextureAtlas(Gdx.files.internal(TEXTURE_ATLAS_FILE));
        skin.addRegions(textureAtlas);

        /** Setting Variables */
        isPaused = false;
        retryButtonClicked = false;
        menuButtonClicked = false;
        playButtonClicked = false;

        pauseHeight = skin.getDrawable(sBACKGROUND).getMinHeight();
        pauseWidth = skin.getDrawable(sBACKGROUND).getMinWidth();

        /** Create Table & Set Size/Location */
        table = new Table();
        table.center();
        table.setHeight(pauseHeight);
        table.setWidth(pauseWidth);
        table.setBackground(skin.getDrawable(sBACKGROUND));
        table.setPosition(Gdx.graphics.getWidth()/2 - pauseWidth/2 ,Gdx.graphics.getHeight()/2 - pauseHeight/2);

        /** Font Styles */
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_FILE));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = FONT_TITLE_SIZE;
        BitmapFont fontTitle = generator.generateFont(parameter);
        parameter.size = FONT_SUBTITLE_SIZE;
        BitmapFont fontSubtitle = generator.generateFont(parameter);
        generator.dispose();

        /** Title Label */
        Label.LabelStyle titleLS = new Label.LabelStyle(fontTitle, Color.WHITE);
        titleLabel = new Label(TITLE_TXT, titleLS);
        table.add(titleLabel).colspan(3);

        /** Add Row in Table */
        table.row().padTop(40);

        /** Music Label */
        fontSubtitle.getData().setScale(0.5f);
        Label.LabelStyle subTitleLS = new Label.LabelStyle(fontSubtitle, Color.WHITE);
        musicLabel = new Label(MUSIC_TXT, subTitleLS);
        table.add(musicLabel).colspan(1).padLeft(20);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = skin.getDrawable(sBAR);
        sliderStyle.knob = skin.getDrawable(sKNOB);
        musicSlider = new Slider(0, 100, 1f, false, sliderStyle);
        musicSlider.setValue(100);
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //System.out.println("MUSIC SLIDED: " + musicSlider.getValue());
                MusicController.getInstance().setVolume(musicSlider.getValue()/100f);
            }
        });

        table.add(musicSlider).colspan(2);

        /** Add New Row */
        table.row().padTop(20);

        /** SoundFX Label */
        sfxLabel = new Label(SFX_TXT, subTitleLS);
        table.add(sfxLabel).colspan(1).padLeft(40); // .colspan(columnNum);

        /** SFX Slider */
        sfxSlider = new Slider(0, 100, 1f, false, sliderStyle);
        sfxSlider.setValue(100);
        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //System.out.println("SFX SLIDED: " + sfxSlider.getValue());
                SoundController.getInstance().setVolume(sfxSlider.getValue()/100f);
            }
        });

        table.add(sfxSlider).colspan(2);

        /** Add New Row */
        table.row().uniform().padTop(40);

        /** Menu Button */
        Button.ButtonStyle menuStyle = new Button.ButtonStyle();
        menuStyle.up = skin.getDrawable(sMENU);
        //menuStyle.down = skin.getDrawable(sMENU_CLICKED);
        Button menu = new Button(menuStyle);

        menu.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("MENU BUTTON CLICKED");
                menuButtonClicked = true;
            }
        });

        table.add(menu).width(ICON_SIZE_SMALL).height(ICON_SIZE_SMALL).padRight(30);

        /** Retry Button */
        Button.ButtonStyle retryStyle = new Button.ButtonStyle();
        retryStyle.up = skin.getDrawable(sRETRY);
        //retryStyle.down = skin.getDrawable(sRETRY_CLICKED);
        Button retry = new Button(retryStyle);

        retry.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("RETRY BUTTON CLICKED");
                retryButtonClicked = true;
            }
        });

        table.add(retry).width(ICON_SIZE_BIG).height(ICON_SIZE_BIG);

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

        table.add(play).width(ICON_SIZE_SMALL).width(ICON_SIZE_SMALL).padLeft(30);
    }

    /** Gets the Stage */
    public Stage getStage() { return stage; }

    /** Occurs when game is first paused */
    public void pauseGame() {
        System.out.println("Paused");

        retryButtonClicked = false;
        menuButtonClicked = false;
        playButtonClicked = false;

        isPaused = true;
        group.addActor(table);
        stage.addActor(group);

        Gdx.input.setInputProcessor(stage);
    }

    /** Returns whether menu icon was clicked */
    public boolean getMenuClicked() {
        return menuButtonClicked;
    }


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
            isPaused = false;
            group.remove();
            reset();
        }
    }

    public void reset() {
        retryButtonClicked = false;
        menuButtonClicked = false;
        playButtonClicked = false;
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
        textureAtlas.dispose();
    }
}

