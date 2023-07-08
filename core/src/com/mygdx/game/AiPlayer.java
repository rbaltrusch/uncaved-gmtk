package com.mygdx.game;

import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.util.Tuple;

public class AiPlayer extends RectEntity implements Renderable, Actor, Disposable {

	private Tuple speed;
	private AnimationWrapper<TextureRegion> walkAnimation;
	private AnimationWrapper<TextureRegion> deathAnimation;
	private TextureRegion currentFrame;
	private boolean alive = true;
	private float deathTime = 0;

	public AiPlayer(Rectangle rect, AnimationWrapper<TextureRegion> walkAnimation,
			AnimationWrapper<TextureRegion> deathAnimation) {
		super(rect);
		speed = new Tuple(200, 0);
		this.walkAnimation = Objects.requireNonNull(walkAnimation);
		this.deathAnimation = Objects.requireNonNull(deathAnimation);
	}

	public void kill() {
		alive = false;
		deathTime = 0;
	}

	@Override
	public void update(GameLoop game) {
		if (alive) {
			move(speed);
		}

		if (this.overlaps(game.getGoal())) {
			game.triggerAiWin();
		}

		currentFrame = alive ? walkAnimation.getKeyFrame(game.getTime(), true) : deathAnimation.getKeyFrame(deathTime);
		if (!alive) {
			deathTime += Gdx.graphics.getDeltaTime();
		}
	}

	@Override
	public void render(Renderer renderer) {
		if (currentFrame != null) {
			renderer.draw(rect, currentFrame);
		}
	}

	@Override
	public void dispose() {
		walkAnimation.dispose();
		deathAnimation.dispose();
	}
}
