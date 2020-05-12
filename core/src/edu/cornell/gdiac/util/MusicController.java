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

	private String musicNextName;

	private boolean reverse;

	private float prevMusicVolume;

	/**
	 * Creates a new Music with the default settings.
	 */
	private MusicController() {
		isUnmuted = true;
		crossFading = false;
		reverse = false;
		musicNext = null;
		musicVolume = 1f;
		prevMusicVolume = 1f;
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
	public void addMusic(String musicName, String filename) { musicHolder.put(musicName, Gdx.audio.newMusic(Gdx.files.internal(filename))); }

	/**
	 * Removes the existing music from this MusicController if present.
	 *
	 * @param musicNames The list of music names to remove.
	 */
	public void removeMusic(String[] musicNames) {
		for (int i = 0; i < musicNames.length; i++) {
			Music m = musicHolder.get(musicNames[i]);
			m.dispose();
		}
	}

	/** Removes all existing music from this MusicController */
	public void removeAll() {
		String[] listToRemove = new String[musicHolder.size()];
		int i = 0;
		for (String m : musicHolder.keySet()) {
			listToRemove[i] = m;
			i++;
		}
		removeMusic(listToRemove);
	}

	/// Getters and Setters

	/**
	 * Sets this MusicController's unmuted state.
	 *
	 * @param value Whether this MusicController is unmuted
	 */
	public void setUnmuted(boolean value) {
		isUnmuted = value;
		//System.out.println("set music.unmuted to " + value);
		// If just got muted and currently playing
		if (!isUnmuted && isPlaying()) {
			if (musicNext != null && crossFading) {
				if (!reverse) {
					music.stop();
					music = musicNext;
				}
				else {
					musicNext.setVolume(0f);
					musicNext = null;
				}
				crossFading = false;
				reverse = false;
				music.play();
			}
			//System.out.println("set volulme of music to 0");
			music.setVolume(0);
			if (musicNext != null) { musicNext.setVolume(0); }
			prevMusicVolume = musicVolume;
			setVolume(0);
		}
		// If current playing and unmuted, but music is still silent
		if (music.isPlaying() && isUnmuted && music.getVolume() == 0f) {
			//System.out.println("set music back to volume " + prevMusicVolume);
			setVolume(prevMusicVolume);
		}

	}

	public float getVolume() {
		return (musicVolume);
	}

	 /** Returns true if this MusicController is unmuted.
	 *
	 * * @returns Whether this MusicController is unmuted
	 */
	public boolean isUnmuted() { return isUnmuted; }


	public void play(String musicNameToPlay){
		// Some music is currently playing
		if (music != null){
			//System.out.println("trying to play: " + musicNameToPlay);
			Music musicToPlay = musicHolder.get(musicNameToPlay);

			// If while cross fading, another change happens
			if (crossFading && musicNameToPlay != null){
					music.setVolume(music.getVolume());
					if(reverse){
						reverse = false;
					}
					else{
						reverse = true;
					}

			}
			else if (musicNameToPlay != musicName) {
				//System.out.println("get into start");
				crossFading = true;

				if (musicToPlay != null) {
					musicNext = musicToPlay;
					musicNextName = musicNameToPlay;

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
			else{
				System.out.println("Should go into here");
			}

		}
		// No music is currently playing
		else {
			//System.out.println("no music is playing so making it!");
			Music musicToPlay = musicHolder.get(musicNameToPlay);
			if (musicToPlay != null) {
				musicName = musicNameToPlay;
				music = musicToPlay;
			}
			else {
				//System.out.println("The music you're trying to play isnt in the holder.");
			}
		}

		if (music != null && !crossFading && !reverse) {
			music.setVolume( isUnmuted() ? musicVolume : 0f);
			//System.out.println("this is the musics volume " + musicVolume);
			music.setLooping(true);
			music.play();
		}

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
		if (v > 0) { isUnmuted = true; }
	}

	/** Update function for this Music Controller. */
	public void update() {
		// If volume is 0, mute this controller
		if (musicVolume == 0) {
			isUnmuted = false;
		}

		// Normal update where music is just playing
		if (music != null) {
			if (!crossFading) {
				musicNext = null; music.play();
			}

			// Cross fade update
			else {
				// Forward cross fade
				if (!reverse) {
					//System.out.println("forward");
					// Update cross fade volumes
					music.setVolume(music.getVolume() - (.015f * music.getVolume()));
					musicNext.setVolume(musicNext.getVolume() + (.015f * musicNext.getVolume()));

					// Condition to stop cross fade
					if (music.getVolume() < minimumThreshold || musicNext.getVolume() >= maximumThreshold || !isUnmuted) {
						crossFading = false;
						music.stop();
						music = musicNext;
						music.setVolume(maximumThreshold);
						music.play();
						musicName = musicNextName;
						musicNextName = null;
						//System.out.println("finished forward");
					}
				}
				// Reversed cross fade
				else {
					//System.out.println("backward");
					// Update cross fade volumes
					musicNext.setVolume(musicNext.getVolume() - (.015f * musicNext.getVolume()));
					music.setVolume(music.getVolume() + (.015f * music.getVolume()));

					// Condition to stop cross fade
					if (musicNext.getVolume() < minimumThreshold || music.getVolume() >= maximumThreshold && !isUnmuted) {
						crossFading = false;
						reverse = false;
						musicNext.setVolume(0f);
						musicNext = null;
						music.setVolume(maximumThreshold);
						musicNextName = null;
						music.play();
						//System.out.println("finished reverse");
					}
				}
			}
		}
	}
}
