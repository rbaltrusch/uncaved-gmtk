package com.mygdx.game.util;

import com.badlogic.gdx.audio.Music;

public final class MusicUtil {
	private MusicUtil() {
	}

	public static void fadeIn(Music music, float volumeIncrease, DelayedRunnableHandler handler) {
		handler.add(() -> {
			boolean maxVolume = increaseVolume(music, volumeIncrease);
			System.out.println(maxVolume);
			if (!maxVolume) {
				fadeIn(music, volumeIncrease, handler);
			}
		}, 0);
	}

	/**
	 * Increases volume by specified increase, then returns true if music is at max
	 * volume, else false.
	 */
	public static boolean increaseVolume(Music music, float volumeIncrease) {
		float newVolume = Math.min(1, music.getVolume() + volumeIncrease);
		System.out.println(newVolume);
		music.setVolume(newVolume);
		return music.getVolume() >= 1f;
	}
}
