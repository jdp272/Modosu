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

	private HashMap<String, FileHandle> fileNames = new HashMap<>();
	private boolean isUnmuted;
	private Music music;
	private String musicName;

	/** The singleton Music controller instance */
	private static MusicController controller;

	/**
	 * Creates a new Music with the default settings.
	 */
	private MusicController() {
		isUnmuted = true;
	}

	/**
	 * Returns the single instance for the MusicController
	 *
	 * The first time this is called, it will construct the MusicController.
	 *
	 * @return the single instance for the MusicController
	 */
	public static MusicController getInstance() {
		if (controller == null) {
			controller = new MusicController();
		}
		return controller;
	}

	/// Music Management
	/**
	 *
	 * @param filename The filename for the sound asset
	 */
	public void add(String musicName, String filename) {
		fileNames.put(musicName, Gdx.files.internal(filename));
	}


	/// Properties
	/**ADDDDDDD COMMMENTTT
	 */
	public void setUnmuted(boolean value) {
		System.out.println("just set music unmuted to be: " + value);
		isUnmuted = value;
		if (!isUnmuted && isPlaying()) {
			setVolume(0);
		}
		if (isPlaying() && isUnmuted() && music.getVolume() == 0f) {
			setVolume(40);
		}
	}

	public boolean isUnmuted() { return isUnmuted; }

	public void play(String musicName){
		if (isUnmuted) {
			if (music != null){
				if (this.musicName.equals(musicName)) {
					//System.out.println("not changing song cause same one is called");
					return;
				}
				music.dispose();
			}
			music = Gdx.audio.newMusic(fileNames.get(musicName));
			this.musicName = musicName;
			music.setLooping(true);
			//System.out.println("new music is successlfully playing");
			music.play();
		}
	}

	public boolean isPlaying() {
		if (music != null) { return music.isPlaying(); }
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
		if (music != null) { music.setVolume(v/100); }
	}

	public void update() {
		if(music == null) System.out.println("music became null for some reason");
		if (music != null) music.play();
	}
}
