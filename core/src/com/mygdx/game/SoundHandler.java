package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Rudimentary sound and music handler to centralize volume handling.
 */
public class SoundHandler {

	private float volume = 1;
	private float volumeBeforeMute;
	private boolean muted = false;

	// sound handler does not own the music and does not dispose of it!
	private Music music;

	public SoundHandler() {
	}

	public void toggleMute() {
		Gdx.app.log("sound", "Toggling mute...");
		muted = !muted;
		if (muted) {
			volumeBeforeMute = volume;
			setVolume(0);
		} else {
			setVolume(volumeBeforeMute);
		}
	}

	public void setVolume(float volume) {
		this.volume = volume;
		if (music != null) {
			music.setVolume(volume);
		}
	}

	public float getVolume() {
		return volume;
	}

	public void play(Sound sound) {
		sound.play(this.volume);
	}

	public void play(Music music) {
		this.music = music;
		music.setVolume(volume);
		music.play();
	}
}
