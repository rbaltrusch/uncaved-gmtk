package com.mygdx.game.util;

import com.badlogic.gdx.audio.Music;

public final class MusicUtil {
	private MusicUtil() {
	}

	public static void fadeIn(Music music, float volumeIncrease, DelayedRunnableHandler handler) {
		handler.add(() -> {
			float newVolume = changeVolume(music, volumeIncrease);
			if (newVolume < 1f) {
				fadeIn(music, volumeIncrease, handler);
			}
		}, 0);
	}

	/**
	 * volumeIncrease must be negative to fade out properly
	 */
	public static void fadeOut(Music music, float volumeIncrease, DelayedRunnableHandler handler) {
		handler.add(() -> {
			float newVolume = changeVolume(music, volumeIncrease);
			if (newVolume > 0f) {
				fadeOut(music, volumeIncrease, handler);
			}
		}, 0);
	}

	/**
	 * Increases volume by specified increase, then returns new volume
	 */
	private static float changeVolume(Music music, float volumeIncrease) {
		float newVolume = Math.max(0, Math.min(1, music.getVolume() + volumeIncrease));
		music.setVolume(newVolume);
		return newVolume;
	}
}
