package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class Goal extends RectEntity implements Renderable, Disposable, Actor {

	private Color color;
	private Animation<TextureRegion> flagAnimation;
	private TextureRegion currentFrame;

	private boolean reached = false;
	private float reachTime = 0;

	public Goal(Rectangle rect, Animation<TextureRegion> flagAnimation) {
		super(rect);
		color = new Color(1, 0.5f, 1, 1);
		this.flagAnimation = flagAnimation;
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
		} else {
			renderer.drawRectangle(rect, color);
		}
	}

	@Override
	public void dispose() {
	}
}
