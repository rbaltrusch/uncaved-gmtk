package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.util.Tuple;

public class AiPlayer extends RectEntity implements Renderable, Actor, Disposable {

	private Color color;
	private Tuple speed;
	private Animation<TextureRegion> walkAnimation;
	private TextureRegion currentFrame;

	public AiPlayer(Rectangle rect, Animation<TextureRegion> walkAnimation) {
		super(rect);
		color = new Color(1, 0.5f, 0.5f, 1);
		speed = new Tuple(200, 0);
		this.walkAnimation = walkAnimation;
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
		} else {
			renderer.drawRectangle(rect, color);
		}
	}

	@Override
	public void dispose() {
	}
}
