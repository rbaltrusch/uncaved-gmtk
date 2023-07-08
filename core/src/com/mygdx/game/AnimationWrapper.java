package com.mygdx.game;

import java.util.Objects;
import java.util.function.Function;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public class AnimationWrapper<T> implements Disposable {

	private Texture texture;
	private Animation<T> animation;

	public AnimationWrapper(Texture texture, Animation<T> animation) {
		this.texture = Objects.requireNonNull(texture);
		this.animation = Objects.requireNonNull(animation);
	}

	public static AnimationWrapperBuilder of(Texture texture) {
		return new AnimationWrapperBuilder(texture);
	}

	public T getKeyFrame(float stateTime) {
		return animation.getKeyFrame(stateTime);
	}

	public T getKeyFrame(float stateTime, boolean looping) {
		return animation.getKeyFrame(stateTime, looping);
	}

	public Animation<T> getAnimation() {
		return animation;
	}

	@Override
	public void dispose() {
		texture.dispose();
	}

	public static class AnimationWrapperBuilder {

		private Texture texture;

		private AnimationWrapperBuilder(Texture texture) {
			this.texture = texture;
		}

		public AnimationWrapper<TextureRegion> build(int width, int height, float frameDuration,
				Function<TextureRegion[][], TextureRegion[]> frameMapper) {
			TextureRegion[][] frames = TextureRegion.split(texture, width, height);
			TextureRegion[] mappedFrames = frameMapper.apply(frames);
			Animation<TextureRegion> animation = new Animation<TextureRegion>(frameDuration, mappedFrames);
			return new AnimationWrapper<TextureRegion>(texture, animation);
		}
	}
}
