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
import com.badlogic.gdx.utils.viewport.Viewport;

public class Tutorial {

    /** File Locations */
    private static final String FONT_FILE = "shared/AveriaSerifLibre.ttf";

    /** Constants for Tutorial Boxes */
    private static int FONT_SIZE = 24;
    private static int FONT_COLOR = 0xc0bab2;
    private static int DELAY_TIME = 5;
    private static String CONTINUE_TEXT = "press enter to continue";

    /** Declaration for new Stage */
    private Stage stage;
    private Viewport viewport;

    /** Scene2D Widgets for Indicators */
    private Table table;
    private Stack stack;
    private Label instructionLabel;
    private Label continueLabel;
    private Texture boxTexture;

    /** Variables */
    private String instruction;
    private int delayContinue;
    private int currentIndex;
    private boolean completedTutoral;
    private TutorialData currentTutorial;


    /** A struct that stores all the data of a tutorial textbox when read from the json */
    public class TutorialData {
        public Vector2 dimensions;
        public Vector2 location;
        public String instructions;
    }

    private TutorialData[] tutorials;

    public Tutorial() {

        //currentTutorial = [];
        currentTutorial = null;
        completedTutoral = false;
        delayContinue = DELAY_TIME;

        // get in the tutorial data

        /* Use Asul Font Font */
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_FILE));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = FONT_SIZE;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        /* Create label for the instruction */
        Label.LabelStyle instructionLS = new Label.LabelStyle();
        instructionLS.font = font;
        instructionLS.fontColor = new Color(Color.SKY);
        instructionLabel = new Label(instruction, instructionLS);

        /* Create label for continue */
        Label.LabelStyle continueLS = new Label.LabelStyle();
        continueLS.font = font;
        continueLS.fontColor = new Color(Color.SKY);
        continueLabel = new Label(CONTINUE_TEXT, continueLS);
        // delay showing this

        table.add(instructionLabel);
        table.add(continueLabel);
    }

    // public createTutorialData()

    public int getCurrentIndex() {
        return currentIndex;
    }

    /** Updates the tutorial to the next tutorial */
    public boolean updateTutorial() {
        if (currentIndex >= tutorials.length - 1) {
            completedTutoral = true;
            return true;
        }

        currentTutorial = tutorials[currentIndex];

        currentIndex++;
        table.setPosition(currentTutorial.location.x, currentTutorial.location.y);
        table.setWidth(currentTutorial.dimensions.x);
        table.setHeight(currentTutorial.dimensions.y);

        instructionLabel.setText(currentTutorial.instructions);

        return false;
    }

}
