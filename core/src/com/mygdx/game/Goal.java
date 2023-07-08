package com.mygdx.game;

import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class Goal extends RectEntity implements Renderable, Disposable, Actor {

	private AnimationWrapper<TextureRegion> flagAnimation;
	private TextureRegion currentFrame;

	private boolean reached = false;
	private float reachTime = 0;

	public Goal(Rectangle rect, AnimationWrapper<TextureRegion> flagAnimation) {
		super(rect);
		this.flagAnimation = Objects.requireNonNull(flagAnimation);
	}

	public void reach() {
		Gdx.app.log("goal", "Reached goal!");
		reached = true;
		reachTime = 0.05f;
	}

	@Override
	public void update(GameLoop game) {
		if (reached) {
			game.triggerAiWin();
			reachTime += Gdx.graphics.getDeltaTime();
		}

		float animationTime = reached ? reachTime : 0;
		currentFrame = flagAnimation.getKeyFrame(animationTime);
	}

	@Override
	public void render(Renderer renderer) {
		if (currentFrame != null) {
			renderer.draw(rect, currentFrame);
		}
	}

	@Override
	public void dispose() {
		flagAnimation.dispose();
	}
}
