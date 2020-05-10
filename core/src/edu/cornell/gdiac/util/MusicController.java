/*
 * SoundController.java
 *
 * Sound management in LibGDX is horrible.  It is the absolute worse thing
 * about the engine.  Furthermore, because of OpenAL bugs in OS X, it is
 * even worse than that.  There is a lot of magic vodoo that you have to
 * do to get everything working properly.  This class hides all of that
 * for you and makes it easy to play sound effects.
 * 
 * Note that this class is an instance of a Singleton.  There is only one
 * SoundController at a time.  The constructor is hidden and you cannot
 * make your own sound controller.  Instead, you use the method getInstance()
 * to get the current sound controller.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;


/**
 * A singleton class for controlling sound effects in LibGDX
 * 
 * This ensures that at any given time there will only be one music track playing at once.
 */
public class MusicController {

	private HashMap<String, Music> musicHolder = new HashMap<>();

	/** The unmuted state of this Music controller. */
	private boolean isUnmuted;
	/** The music of the music that is currently playing */
	private Music music;
	/** The name of the music that is currently playing. */
	private String musicName;

	/** The default volume of sounds for this controller */
	private float musicVolume;

	/** The singleton Music controller instance */
	private static MusicController controller;

	/** Whether crossFading mode is active */
	private boolean crossFading;

	/** Only null if changing muMusic to change from */
	private float minimumThreshold;

	private float maximumThreshold;

	private Music musicNext;

	/**
	 * Creates a new Music with the default settings.
	 */
	private MusicController() {
		isUnmuted = true;
		crossFading = false;
		musicNext = null;
		musicVolume = 1f;
	}

	/**
	 * Returns the single instance for the MusicController
	 *
	 * The first time this is called, it will construct the MusicController.
	 *
	 * @return the single instance for the MusicController
	 */
	public static MusicController getInstance() {
		if (controller == null) { controller = new MusicController(); }
		return controller;
	}

	/**
	 * Returns whether this Music Controller has music stored inside of it.
	 *
	 *
	 * @return Whether this Music Controller has music
	 */
	public boolean isEmpty(){ return musicHolder.isEmpty(); }

	/// Music Management
	/**
	 *
	 * @param filename The filename for the sound asset
	 */
	public void addMusic(String musicName, String filename) {
		System.out.println("added: " + musicName);
		musicHolder.put(musicName, Gdx.audio.newMusic(Gdx.files.internal(filename)));
	}

	/**
	 * Removes the existing music from this MusicController if present.
	 *
	 * @param musicName The name of the music to remove
	 */
	public void removeMusic(String musicName) { musicHolder.remove(musicName); }

	/// Getters and Setters

	/**
	 * Sets this MusicController's unmuted state.
	 *
	 * @param value Whether this MusicController is unmuted
	 */
	public void setUnmuted(boolean value) {
		isUnmuted = value;

		// If just got muted and currently playing
		if (!isUnmuted && isPlaying()) {
			setVolume(0);
		}
		// If current playing and unmuted, but music is still silent
		if (music.isPlaying() && isUnmuted && music.getVolume() == 0f) {
			setVolume(musicVolume);
		}
	}

	 /** Returns true if this MusicController is unmuted.
	 *
	 * * @returns Whether this MusicController is unmuted
	 */
	public boolean isUnmuted() { return isUnmuted; }


	public void play(String musicNameToPlay){
		// Some music is currently playing
		if (music != null){
			if (musicName.equals(musicNameToPlay)) {
				System.out.println("not changing song cause same one is called");
				return;
			}
			else {
				Music musicToPlay = musicHolder.get(musicNameToPlay);

				// If while cross fading, another change happens
				if (crossFading && musicNameToPlay != null){
					crossFading = false;
					System.out.println("will be broken");
					music.stop();
					musicNext.stop();
				}
				else {
					System.out.println("Crossfade is true");
					crossFading = true;

					if (musicToPlay != null) {
						musicName = musicNameToPlay;
						musicNext = musicToPlay;

						// Incoming song is at 10% of current volume
						musicNext.setVolume(.10f * musicVolume);

						// New song will start and stop increasing when reaches current volume
						maximumThreshold = music.getVolume();

						// Will stop old one when it is less than 10% of the current volume
						minimumThreshold = .10f * maximumThreshold;

						// Start playing new track
						musicNext.play();
					}
				}
			}
		}
		// No music is currently playing
		else {
			Music musicToPlay = musicHolder.get(musicNameToPlay);
			if (musicToPlay != null) {
				musicName = musicNameToPlay;
				music = musicToPlay;
			}
			else {
				System.out.println("The music you're trying to play isnt in the holder.");
			}
		}

		music.setVolume( isUnmuted() ? musicVolume : 0f);
		music.setLooping(true);
		music.play();
	}

	/** Returns true if this MusicController is playing music.
	 *
	 * @returns Whether this MusicController is playing
	 */
	public boolean isPlaying() {
		if (music != null ) { return music.isPlaying(); }
		return false;
	}

	public void stop(String musicName){
		if (music != null) {
			music.stop();
		}
	}

	/**
	 * Sets the volume of the music controller. All sounds are defaulted to this volume.
	 * @param v	The sound volume in the range [0,1]
	 */
	public void setVolume(float v) {
		if (music != null) {
			musicVolume = v;
			music.setVolume(v);
		}
	}

	/** Update function for this Music Controller. */
	public void update() {
		// Normal update where music is just playing
		if (!crossFading) { musicNext = null; music.play(); }

		// Cross fade update
		else {
			// Update cross fade volumes
			music.setVolume(music.getVolume() - (.015f * music.getVolume()));
			musicNext.setVolume(musicNext.getVolume() + (.015f * musicNext.getVolume()));

			// Condition to stop cross fade
			if (music.getVolume() < minimumThreshold || musicNext.getVolume() >= maximumThreshold) {
				crossFading = false;
				music.stop();
				music = musicNext;

				music.setVolume(maximumThreshold);
				music.play();
				System.out.println("Cross fade is false;");
			}
		}
	}
}
