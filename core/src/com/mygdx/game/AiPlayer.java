package com.mygdx.game;

import java.util.Objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.util.Tuple;

public class AiPlayer extends RectEntity implements Renderable, Actor, Disposable {

	private Tuple speed;
	private AnimationWrapper<TextureRegion> walkAnimation;
	private TextureRegion currentFrame;

	public AiPlayer(Rectangle rect, AnimationWrapper<TextureRegion> walkAnimation) {
		super(rect);
		speed = new Tuple(200, 0);
		this.walkAnimation = Objects.requireNonNull(walkAnimation);
	}

	@Override
	public void update(GameLoop game) {
		move(speed);
		if (this.overlaps(game.getGoal())) {
			game.getGoal().reach();
		}
		currentFrame = walkAnimation.getKeyFrame(game.getTime(), true);
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
	}
}
