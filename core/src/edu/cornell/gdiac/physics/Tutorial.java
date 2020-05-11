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
    private Label continueLabel;
    private Texture boxTexture;

    /** Variables */
    private int currentIndex;
    private int delayContinue;
    private String instruction;
    private boolean completedTutoral;
    private TutorialData currentTutorial;
    private TutorialData[] tutorials;

    /** A struct that stores all the data of a tutorial textbox when read from the json */
    public class TutorialData {
        public Vector2 location;
        public String instructions;
    }

    public Tutorial() {
        boxTexture = new Texture(Gdx.files.internal(BACKGROUND_FILE));
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport);
        table = new Table();
        table.top();
        table.setBackground(new Image(boxTexture).getDrawable());

        currentIndex = 0;
        tutorials = null;
        currentTutorial = null;
        completedTutoral = false;
        delayContinue = DELAY_TIME;

        // get in the tutorial data
        addTutorial();

        if (tutorials != null || tutorials.length != 0) {
            currentTutorial = tutorials[currentIndex];
            instruction = currentTutorial.instructions;
            table.setPosition(currentTutorial.location.x, currentTutorial.location.y);
        }

        /* Use Asul Font Font */
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_FILE));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = FONT_SIZE;
        BitmapFont font = generator.generateFont(parameter);

        /* Create label for the instruction */
        Label.LabelStyle instructionLS = new Label.LabelStyle();
        instructionLS.font = font;
        instructionLS.fontColor = new Color(Color.LIGHT_GRAY);
        instructionLabel = new Label(instruction, instructionLS);
        instructionLabel.setWrap(true);

        table.add(instructionLabel).width(MIN_LABEL_WIDTH).padTop(10);

        table.row();

        /* Create label for continue */
        parameter.size = FONT_SIZE_CONTINUE;
        font = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle continueLS = new Label.LabelStyle();
        continueLS.font = font;
        continueLS.fontColor = new Color(Color.TEAL);
        continueLabel = new Label(CONTINUE_TEXT, continueLS);

        table.add(continueLabel).padTop(5).padBottom(5);

        // Change dimensions of table
        table.setWidth(DEFAULT_WIDTH);
        table.setHeight(DEFAULT_HEIGHT);

        stage.addActor(table);
    }

    public void addTutorial() {
        // do stuff here
        tutorials = new TutorialData[4];
        tutorials[0] = new TutorialData();
        tutorials[0].location = new Vector2(200,100);
        tutorials[0].instructions = "Click and drag back to shoot ur spirit to and from golems.";

        tutorials[1] = new TutorialData();
        tutorials[1].location = new Vector2(700, 400);
        tutorials[1].instructions = "Use WASD to move the golem back and forth";

        tutorials[2] = new TutorialData();
        tutorials[2].location = new Vector2(200, 300);
        tutorials[2].instructions = "Watch out for the energy pillars; they will increase ur charge faster!";

        tutorials[3] = new TutorialData();
        tutorials[3].location = new Vector2(600, 400);
        tutorials[3].instructions = "Watch out! SAND.";
    }


    public void drawTutorial(float dt) {
        stage.act(dt);
        stage.draw();

        // use spritebatch instead?
    }

    public boolean didCompleteTutorial() {
        return completedTutoral || tutorials == null;
    }

    /** Updates the tutorial to the next tutorial */
    public void updateTutorial() {
        System.out.println("Update Tutorial");
        if (tutorials == null || currentIndex >= tutorials.length - 1) {
            completedTutoral = true;
            return;
        }

        currentIndex++;
        currentTutorial = tutorials[currentIndex];

        instructionLabel.setText(currentTutorial.instructions);

        table.setHeight(table.getCell(instructionLabel).getPrefHeight() + table.getCell(continueLabel).getPrefHeight() + 25);
        table.setPosition(currentTutorial.location.x, currentTutorial.location.y);
    }

    public void reset() {
        currentIndex = -1;
        tutorials = null;
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
