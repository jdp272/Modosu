package edu.cornell.gdiac.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class Pause {

    /* Declaration for new Stage */
    private Stage stage;
    private Group group;

    /* Scene 2D */
    private Slider musicSlider;
    private Slider soundFXSlider;
    private Label levelLabel;

    /* Files */
    private static final String BACKGROUND_FILE = "shared/paused/background.png";

    /* Static variables */
    private boolean isPaused;

    public Pause() {
        stage = new Stage();
//        group = new Group();
//        stage.addActor(group);

        isPaused = false;
    }

    /* Gets the Stage */
    public Stage getStage() { return stage; }

    public void pauseGame(){

        isPaused = true;
        //group = new Group();

        /** Create table */
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        //Skin skin = new Skin(Gdx.files.internal("shared/uiskin.json"));
//        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
//        musicSlider = new Slider(0, 100, 0.1f, false, sliderStyle);
//        musicSlider.setPosition(200, 20);
//        musicSlider.addListener(new ChangeListener() {
//
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                System.out.println("SLIDED");
//            }
//        });
//        table.addActor(musicSlider);


        Texture backgroundTexture = new Texture(Gdx.files.internal(BACKGROUND_FILE));
        Image counterBackground = new Image(backgroundTexture);
        table.addActor(counterBackground);

        //group.addActor();

        stage.addActor(table);
        //stage.addActor(group);

        Gdx.input.setInputProcessor(stage);
        System.out.println("PAUSED");
    }

    public void resumeGame(){
        if ( isPaused ){
            isPaused = false;
            group.remove();
        }
    }

    public void dispose() {

    }

}

