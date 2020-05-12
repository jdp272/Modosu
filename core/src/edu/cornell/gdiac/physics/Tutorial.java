package edu.cornell.gdiac.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Tutorial {

    /** File Locations */
    private static final String FONT_FILE = "shared/AveriaSerifLibre.ttf";
    private static final String BACKGROUND_FILE = "shared/tutorial.png";

    /** Constants for Tutorial Boxes */
    private static int FONT_SIZE = 22;
    private static int FONT_SIZE_CONTINUE = 16;
    private static int FONT_COLOR = 0xc0bab2;
    private static int DELAY_TIME = 5;
    private static int MIN_LABEL_WIDTH = 250;
    private static int DEFAULT_HEIGHT = 125;
    private static int DEFAULT_WIDTH = 275;
    private static String CONTINUE_TEXT = "press enter to continue";

    /** Declaration for new Stage */
    private Stage stage;
    private Viewport viewport;

    /** Scene2D Widgets for Indicators */
    private Table table;
    private Label instructionLabel;
    private Texture boxTexture;

    /** Variables */
    private int currentIndex;
    private int countdown;
    private float counter;
    private String instruction;
    private int updateHeight;
    private boolean completedTutoral;
    private TutorialData currentTutorial;

    public Tutorial() {
        boxTexture = new Texture(Gdx.files.internal(BACKGROUND_FILE));
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport);
        table = new Table();
        table.top();
        table.setBackground(new Image(boxTexture).getDrawable());

        counter = 0;
        currentIndex = 0;
        currentTutorial = null;
        updateHeight = 0;
        completedTutoral = false;
        countdown = Integer.MAX_VALUE;

        /* Use Asul Font Font */
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_FILE));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = FONT_SIZE;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        /* Create label for the instruction */
        Label.LabelStyle instructionLS = new Label.LabelStyle();
        instructionLS.font = font;
        instructionLS.fontColor = new Color(Color.LIGHT_GRAY);
        instructionLabel = new Label(instruction, instructionLS);
        instructionLabel.setWrap(true);

        table.add(instructionLabel).width(MIN_LABEL_WIDTH).padTop(10);

        // Change dimensions of table
        table.setWidth(DEFAULT_WIDTH);
        table.setHeight(DEFAULT_HEIGHT);

        stage.addActor(table);
    }

    public void addTutorial(TutorialData tutorialData) {
        reset();

        currentTutorial = tutorialData;

        if (currentTutorial != null) {
            instructionLabel.setText(currentTutorial.instructions);
            countdown = currentTutorial.countdown;
            table.setPosition(currentTutorial.location.x, currentTutorial.location.y);
        }
    }

    public void drawTutorial(float dt) {
        stage.act(dt);

        if(didCompleteTutorial()) return;

        stage.draw();
    }

    public boolean didCompleteTutorial() {
        return completedTutoral || currentTutorial == null;
    }

    /** Updates the tutorial to the next tutorial */
    public void updateTutorial(float dt) {
        if (updateHeight < 2) {
            updateHeight += 1;
            table.setHeight(table.getCell(instructionLabel).getPrefHeight() + 25);
        }

        if (countdown == 0) {
            completedTutoral = true;
        } else {
            counter = counter + dt;
            if (counter >= 1) {
                countdown--;
                counter = 0;
            }
        }

        // Fade out the box
        if(countdown <= 1) {
            if(counter >= 0.5) {
                table.setColor(1.f, 1.f, 1.f, 1.f - ((counter - 0.5f) * 2.f));
            }
        } else {
            table.setColor(1.f, 1.f, 1.f, 1.f);
        }
    }

    public void reset() {
        counter = 0;
        countdown = Integer.MAX_VALUE;
        currentIndex = -1;
        updateHeight = 0;
        currentTutorial = null;
        completedTutoral = false;
    }

    public void dispose() {
        stage.dispose();
        boxTexture.dispose();
        stage = null;
        boxTexture = null;
    }

}
