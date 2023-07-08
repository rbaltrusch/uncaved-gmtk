package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.util.Tuple;

public class AiPlayer extends RectEntity implements Renderable, Actor, Disposable {

	private Color color;
	private Tuple speed;

	public AiPlayer(Rectangle rect) {
		super(rect);
		color = new Color(0, 0, 1, 1);
		speed = new Tuple(200, 0);
	}

	@Override
	public void update(GameLoop game) {
		move(speed);
		if (this.overlaps(game.getGoal())) {
			game.triggerAiWin();
		}
	}

	@Override
	public void render(Renderer renderer) {
		renderer.drawRectangle(rect, color);
	}

	@Override
	public void dispose() {
	}
}
