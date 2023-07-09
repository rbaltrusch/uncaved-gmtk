package com.mygdx.game;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

public class SoundCollection implements Disposable {

	private List<Sound> sounds;

	public SoundCollection(List<Sound> sounds) {
		this.sounds = Objects.requireNonNull(sounds);
	}

	public void play(float volume) {
		getRandomSound().ifPresent(x -> x.play(volume));
	}

	public Optional<Sound> getRandomSound() {
		int index = (int) Math.min(sounds.size() - 1, Math.max(0, MathUtils.random((float) sounds.size())));
		try {
			return Optional.of(sounds.get(index));
		} catch (IndexOutOfBoundsException exc) {
			return Optional.empty();
		}
	}

	@Override
	public void dispose() {
		sounds.stream().forEach(Disposable::dispose);
	}
}
