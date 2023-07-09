package com.mygdx.game;

import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class AiPlayer extends RectEntity implements Renderable, Actor, Disposable {

	private Vector2 speed;
	private AnimationWrapper<TextureRegion> walkAnimation;
	private AnimationWrapper<TextureRegion> deathAnimation;
	private TextureRegion currentFrame;
	private ParticleEffect walkEffect;
	private boolean alive = true;
	private boolean moving = false;
	private float deathTime = 0;

	public AiPlayer(Rectangle rect, AnimationWrapper<TextureRegion> walkAnimation,
			AnimationWrapper<TextureRegion> deathAnimation, ParticleEffect walkEffect) {
		super(rect);
		speed = new Vector2(200, 0);
		this.walkAnimation = Objects.requireNonNull(walkAnimation);
		this.deathAnimation = Objects.requireNonNull(deathAnimation);
		this.walkEffect = Objects.requireNonNull(walkEffect);
		currentFrame = walkAnimation.getKeyFrame(0);
	}

	public void kill() {
		alive = false;
		moving = false;
		deathTime = 0;
	}

	@Override
	public void update(GameLoop game) {
		if (alive) {
			move(speed);
			moving = true;
		}

		// pastGoal "hack" required for very large speeds to not glitch through goal
		boolean pastGoal = this.rect.x >= game.getGoal().getRectangle().x;
		if (this.overlaps(game.getGoal()) || pastGoal) {
			game.triggerAiWin();
		}

		currentFrame = alive ? walkAnimation.getKeyFrame(game.getTime(), true) : deathAnimation.getKeyFrame(deathTime);
		if (!alive) {
			deathTime += Gdx.graphics.getDeltaTime();
		}
	}

	@Override
	public void render(Renderer renderer) {
		if (walkEffect.isComplete() && alive) {
			walkEffect.reset();
		}
		walkEffect.update(Gdx.graphics.getDeltaTime());

		if (moving) {
			renderer.draw(rect, walkEffect);
		}

		if (currentFrame != null) {
			renderer.draw(rect, currentFrame);
		}
	}

	@Override
	public void dispose() {
		walkAnimation.dispose();
		deathAnimation.dispose();
		walkEffect.dispose();
	}

	public Vector2 getSpeed() {
		return speed;
	}
}
