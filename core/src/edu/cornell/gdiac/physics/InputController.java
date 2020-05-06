/*
 * InputController.java
 *
 * This class buffers in input from the devices and converts it into its
 * semantic meaning. If your game had an option that allows the player to
 * remap the control keys, you would store this information in this class.
 * That way, the main GameEngine does not have to keep track of the current
 * key mapping.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;

import edu.cornell.gdiac.util.*;

/**
 * Class for reading player input. This supports a keyboard.
 */
public class InputController {

	/** The singleton instance of the input controller */
	private static InputController theController = null;

	/**
	 * Return the singleton instance of the input controller
	 *
	 * @return the singleton instance of the input controller
	 */
	public static InputController getInstance() {
		if (theController == null) {
			theController = new InputController();
		}
		return theController;
	}

	// Fields to manage buttons
	/** Whether the reset button was pressed. */
	private boolean resetPressed;
	private boolean resetPrevious;
	/** Whether the button to advanced worlds was pressed. */
	private boolean nextPressed;
	private boolean nextPrevious;
	/** Whether the button to step back worlds was pressed. */
	private boolean prevPressed;
	private boolean prevPrevious;
	/** Whether the primary action button was pressed. */
	private boolean primePressed;
	private boolean primePrevious;
	/** Whether the secondary action button was pressed. */
	private boolean secondPressed;
	private boolean secondPrevious;
	/** Whether the teritiary action button was pressed. */
	private boolean tertiaryPressed;
	/** Whether the debug toggle was pressed. */
	private boolean debugPressed;
	private boolean debugPrevious;
	/** Whether the exit button was pressed. */
	private boolean exitPressed;
	private boolean exitPrevious;
	/** Whether the button to return to menu was pressed. */
	private boolean menuPressed;
	private boolean menuPrevious;

	/** Whether the zoom button was pressed. */
	private boolean zoomPressed;

	/** Wehther the pause button was pressed */
	private boolean pausePressed;

	/** If a new obstacle button was pressed */
	private boolean boxPressed;
	private boolean boxPrevious;
	/** If a new host button was pressed */
	private boolean hostPressed;
	private boolean hostPrevious;
	/** If a new spirit button was pressed */
	private boolean spiritPressed;
	private boolean spiritPrevious;
	/** If a clear button was pressed */
	private boolean clearPressed;
	private boolean clearPrevious;
	/** If the delete button was pressed */
	private boolean deletePressed;
	private boolean deletePrevious;
	/** If the save button was pressed */
	private boolean savePressed;
	private boolean savePrevious;
	/** If the instruction button was pressed */
	private boolean instructionPressed;
	private boolean instructionPrevious;
	/** If left mouse was just clicked */
	private boolean leftJustClicked;

	/** If left mouse is pressed or helde clicked */
	private boolean mousePressed;
	private boolean mousePressedPrevious;

	/** If up is held */
	private boolean upHeld;
	/** If down is held */
	private boolean downHeld;

	/** How much did we move horizontally? */
	private float horizontal;
	/** How much did we move vertically? */
	private float vertical;
	/** The mouse position  */
	private Vector2 mousePosition;
	/** The crosshair cache (for using as a return value) */
	private Vector2 mousePositionCache;



	/**
	 * Returns the amount of sideways movement.
	 *
	 * -1 = left, 1 = right, 0 = still
	 *
	 * @return the amount of sideways movement.
	 */
	public float getHorizontal() {
		return horizontal;
	}

	/**
	 * Returns the amount of vertical movement.
	 *
	 * -1 = down, 1 = up, 0 = still
	 *
	 * @return the amount of vertical movement.
	 */
	public float getVertical() {
		return vertical;
	}

	/**
	 * Returns the current position of the mouse on the screen.
	 *
	 * This value does not return the actual reference to the mouse position.
	 * That way this method can be called multiple times without any fair that
	 * the position has been corrupted.  However, it does return the same object
	 * each time.  So if you modify the object, the object will be reset in a
	 * subsequent call to this getter.
	 *
	 * @return the current position of the mouse on the screen.
	 */
	public Vector2 getMousePosition() {
		return mousePositionCache.set(mousePosition);
	}

	/**
	 * Returns true if the primary action button was pressed.
	 *
	 * This is a one-press button. It only returns true at the moment it was
	 * pressed, and returns false at any frame afterwards.
	 *
	 * @return true if the primary action button was pressed.
	 */
	public boolean didPrimary() {
		return primePressed && !primePrevious;
	}

	/**
	 * Returns true if the secondary action button was pressed.
	 *
	 * This is a one-press button. It only returns true at the moment it was
	 * pressed, and returns false at any frame afterwards.
	 *
	 * @return true if the secondary action button was pressed.
	 */
	public boolean didSecondary() {
		return secondPressed && !secondPrevious;
	}

	/**
	 * Returns true if the tertiary action button was pressed.
	 *
	 * This is a sustained button. It will returns true as long as the player
	 * holds it down.
	 *
	 * @return true if the secondary action button was pressed.
	 */
	public boolean didTertiary() {
		return tertiaryPressed;
	}

	/**
	 * Returns true if the reset button was pressed.
	 *
	 * @return true if the reset button was pressed.
	 */
	public boolean didReset() {
		return resetPressed && !resetPrevious;
	}

	/**
	 * Returns true if the player wants to go to the next level.
	 *
	 * @return true if the player wants to go to the next level.
	 */
	public boolean didAdvance() {
		return nextPressed && !nextPrevious;
	}

	/**
	 * Returns true if the player wants to go to the previous level.
	 *
	 * @return true if the player wants to go to the previous level.
	 */
	public boolean didRetreat() { return prevPressed && !prevPrevious; }

	/**
	 * Returns true if the player wants to go toggle the debug mode.
	 *
	 * @return true if the player wants to go toggle the debug mode.
	 */
	public boolean didDebug() {
		return debugPressed && !debugPrevious;
	}

	/**
	 * Returns true if the exit button was pressed.
	 *
	 * @return true if the exit button was pressed.
	 */
	public boolean didExit() {
		return exitPressed && !exitPrevious;
	}

	/**
	 * Returns true if the zoom button was pressed.
	 *
	 * @return true if the zoom button was pressed.
	 */
	public boolean didZoom() { return zoomPressed; }

	/**
	 * Returns true if the pause button was pressed.
	 *
	 * @return true if the pause button was pressed.
	 */
	public boolean didPause() { return pausePressed; }


	public boolean didIsPressed() { return mousePressed; }

	/**
	 * Returns true if the player wants to go toggle the debug mode.
	 *
	 * @return true if the player wants to go toggle the debug mode.
	 */
	public boolean didRelease() { return mousePressedPrevious && !mousePressed; }


	/**
	 * Returns true if the new box button was pressed.
	 *
	 * @return true if the new box button was pressed.
	 */
	public boolean newBox() { return boxPressed && !boxPrevious; }

	/**
	 * Returns true if the new host button was pressed.
	 *
	 * @return true if the new host button was pressed.
	 */
	public boolean newHost() { return hostPressed && !hostPrevious; }

	/**
	 * Returns true if the new spirit button was pressed.
	 *
	 * @return true if the new spirit button was pressed.
	 */
	public boolean newSpirit() { return spiritPressed && !spiritPrevious; }

	/**
	 * Returns true if the clear button was pressed.
	 *
	 * @return true if the clear button was pressed.
	 */
	public boolean didClear() { return clearPressed && !clearPrevious; }

	/**
	 * Returns true if the delete button was pressed.
	 *
	 * @return true if the delete button was pressed.
	 */
	public boolean didDelete() { return deletePressed && !deletePrevious; }

	/**
	 * Returns true if the save button was pressed.
	 *
	 * @return true if the save button was pressed.
	 */
	public boolean didSave() { return savePressed && !savePrevious; }

	/**
	 * Returns true if the main menu button was pressed.
	 *
	 * @return true if the main menu button was pressed.
	 */
	public boolean didMenu() { return menuPressed && !menuPrevious; }

	/**
	 * Returns true if the instruction editor button was pressed.
	 *
	 * @return true if the instruction editor button was pressed
	 */
	public boolean didInstruction() { return instructionPressed && !instructionPrevious; }

	/**
	 * Returns true if the left mouse button was just pressed.
	 *
	 * @return true if the left mouse button was just pressed
	 */
	public boolean didLeftClick() { return leftJustClicked; }

	/**
	 * Returns true if the up button is pressed.
	 *
	 * @return true if the up button pressed
	 */
	public boolean didPressUp() { return upHeld; }

	/**
	 * Returns true if the down button is pressed.
	 *
	 * @return true if the down button is pressed
	 */
	public boolean didPressDown() { return downHeld; }

	/**
	 * Creates a new input controller
	 *
	 * The input controller attempts to connect to the X-Box controller at device 0,
	 * if it exists.  Otherwise, it falls back to the keyboard control.
	 */
	public InputController() {
		mousePosition = new Vector2();
		mousePositionCache = new Vector2();
	}

	/**
	 * Reads the input for the player and converts the result into game logic.
	 *
	 * The method provides both the input bounds and the drawing scale.  It needs
	 * the drawing scale to convert screen coordinates to world coordinates.  The
	 * bounds are for the crosshair.  They cannot go outside of this zone.
	 *
	 */
	public void readInput() {
		// Copy state from last animation frame
		// Helps us ignore buttons that are held down
		primePrevious  = primePressed;
		secondPrevious = secondPressed;
		resetPrevious  = resetPressed;
		debugPrevious  = debugPressed;
		exitPrevious = exitPressed;
		nextPrevious = nextPressed;
		prevPrevious = prevPressed;

		boxPrevious = boxPressed;
		hostPrevious = hostPressed;
		spiritPrevious = spiritPressed;
		clearPrevious = clearPressed;
		deletePrevious = deletePressed;
		savePrevious = savePressed;
		menuPrevious = menuPressed;
		instructionPrevious = instructionPressed;
		mousePressedPrevious = mousePressed;

		readKeyboard();

	}

	/**
	 *
	 * This controller reads input from the keyboard.
	 *
	 */
	private void readKeyboard() {

		resetPressed = Gdx.input.isKeyPressed(Input.Keys.R);
		debugPressed = Gdx.input.isKeyPressed(Input.Keys.H);
		primePressed = primePressed || Gdx.input.isKeyPressed(Input.Keys.UP);
		secondPressed = secondPressed || Gdx.input.isKeyPressed(Input.Keys.DOWN);
		prevPressed =  Gdx.input.isKeyPressed(Input.Keys.P);
		nextPressed =  Gdx.input.isKeyPressed(Input.Keys.N);
		exitPressed  = Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
		zoomPressed = Gdx.input.isKeyJustPressed(Input.Keys.Z);
		upHeld = upHeld || Gdx.input.isKeyPressed(Input.Keys.UP);
		downHeld = downHeld || Gdx.input.isKeyPressed(Input.Keys.DOWN);

		pausePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);

		boxPressed = clearPressed  || Gdx.input.isKeyPressed(Input.Keys.B);
		hostPressed = clearPressed  || Gdx.input.isKeyPressed(Input.Keys.G);
		spiritPressed = clearPressed  || Gdx.input.isKeyPressed(Input.Keys.Z);
		clearPressed = clearPressed || Gdx.input.isKeyPressed(Input.Keys.C);
		deletePressed = deletePressed || Gdx.input.isKeyPressed(Input.Keys.BACKSPACE);
		savePressed = savePressed || Gdx.input.isKeyPressed(Input.Keys.ENTER);
		menuPressed = Gdx.input.isKeyPressed(Input.Keys.M);
		instructionPressed = instructionPressed || Gdx.input.isKeyPressed(Input.Keys.I);


		// Directional controls
		horizontal = 0.0f;
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			horizontal += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			horizontal -= 1.0f;
		}

		vertical = 0.0f;
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			vertical += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			vertical -= 1.0f;
		}

		// Mouse results
		tertiaryPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		if (tertiaryPressed) { mousePressed = true; }
		else { mousePressed = false; }

		leftJustClicked = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);

		mousePosition.set(Gdx.input.getX(), Gdx.input.getY());
	}



	/**
	 * ONLY USED IN LEVEL DESIGNER
	 * Reads the input for the player and converts the result into game logic.
	 *
	 * The method provides both the input bounds and the drawing scale.  It needs
	 * the drawing scale to convert screen coordinates to world coordinates.  The
	 * bounds are for the crosshair.  They cannot go outside of this zone.
	 *
	 * @param bounds The input bounds for the crosshair.
	 * @param scale  The drawing scale
	 */
	public void readInput(Rectangle bounds, Vector2 scale) {
		// Copy state from last animation frame
		// Helps us ignore buttons that are held down
		primePrevious  = primePressed;
		secondPrevious = secondPressed;
		resetPrevious  = resetPressed;
		debugPrevious  = debugPressed;
		exitPrevious = exitPressed;
		nextPrevious = nextPressed;
		prevPrevious = prevPressed;

		boxPrevious = boxPressed;
		hostPrevious = hostPressed;
		spiritPrevious = spiritPressed;
		clearPrevious = clearPressed;
		deletePrevious = deletePressed;
		savePrevious = savePressed;
		menuPrevious = menuPressed;
		instructionPrevious = instructionPressed;

		readKeyboard(bounds, scale, false);

	}

	/**
	 * ONLY USED IN LEVEL DESIGNER
	 * Reads input from the keyboard.
	 * This controller reads from the keyboard regardless of whether or not an X-Box
	 * controller is connected.  However, if a controller is connected, this method
	 * gives priority to the X-Box controller.
	 *
	 * @param secondary true if the keyboard should give priority to a gamepad
	 */
	private void readKeyboard(Rectangle bounds, Vector2 scale, boolean secondary) {
		// Give priority to gamepad results
		resetPressed = (secondary && resetPressed) || (Gdx.input.isKeyPressed(Input.Keys.R));
		debugPressed = (secondary && debugPressed) || (Gdx.input.isKeyPressed(Input.Keys.H));
		primePressed = (secondary && primePressed) || (Gdx.input.isKeyPressed(Input.Keys.UP));
		secondPressed = (secondary && secondPressed) || (Gdx.input.isKeyPressed(Input.Keys.DOWN));
		prevPressed = (secondary && prevPressed) || (Gdx.input.isKeyPressed(Input.Keys.P));
		nextPressed = (secondary && nextPressed) || (Gdx.input.isKeyPressed(Input.Keys.N));
		exitPressed  = (secondary && exitPressed) || (Gdx.input.isKeyPressed(Input.Keys.ESCAPE));
		zoomPressed = Gdx.input.isKeyJustPressed(Input.Keys.Z);
		upHeld = Gdx.input.isKeyPressed(Input.Keys.UP);
		downHeld = Gdx.input.isKeyPressed(Input.Keys.DOWN);

		boxPressed = (secondary && clearPressed)  || (Gdx.input.isKeyPressed(Input.Keys.B));
		hostPressed = (secondary && clearPressed)  || (Gdx.input.isKeyPressed(Input.Keys.G));
		spiritPressed = (secondary && clearPressed)  || (Gdx.input.isKeyPressed(Input.Keys.Z));
		clearPressed = (secondary && clearPressed)  || (Gdx.input.isKeyPressed(Input.Keys.C));
		deletePressed = (secondary && deletePressed)  || (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE));
		savePressed = (secondary && savePressed)  || (Gdx.input.isKeyPressed(Input.Keys.ENTER));
		menuPressed = (secondary && menuPressed) || (Gdx.input.isKeyPressed(Input.Keys.M));
		instructionPressed = (secondary && instructionPressed) || (Gdx.input.isKeyPressed(Input.Keys.I));
//		instructionPressed = Gdx.input.isKeyJustPressed(Input.Keys.I);


		// Directional controls
		horizontal = (secondary ? horizontal : 0.0f);
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			horizontal += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			horizontal -= 1.0f;
		}

		vertical = (secondary ? vertical : 0.0f);
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			vertical += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			vertical -= 1.0f;
		}

		// Mouse results
		tertiaryPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		leftJustClicked = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		mousePosition.set(Gdx.input.getX(), Gdx.input.getY());
		mousePosition.scl(1/scale.x,-1/scale.y);
		mousePosition.y += bounds.height;
		clampPosition(bounds);
	}

	/**
	 * ONLY USED IN LEVEL DESIGNER
	 * Clamp the cursor position so that it does not go outside the window
	 *
	 * While this is not usually a problem with mouse control, this is critical
	 * for the gamepad controls.
	 */
	private void clampPosition(Rectangle bounds) {
		mousePosition.x = Math.max(bounds.x, Math.min(bounds.x+bounds.width, mousePosition.x));
		mousePosition.y = Math.max(bounds.y, Math.min(bounds.y+bounds.height, mousePosition.y));
	}






}