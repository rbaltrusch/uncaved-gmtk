package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.util.Tuple;

public class Boulder extends RectEntity implements Actor, Renderable, Disposable {

	private Texture texture;
	private Tuple speed;
	private boolean dropping = false;

	protected Boulder(Rectangle rect, Texture texture) {
		super(rect);
		this.texture = texture;
		speed = new Tuple(0, -800);
	}

	public void drop() {
		dropping = true;
	}

	@Override
	public void render(Renderer renderer) {
		renderer.draw(rect, texture);
	}

	@Override
	public void update(GameLoop game) {
		if (dropping) {
			move(speed);
		}
		if (this.overlaps(game.getAiPlayer())) {
			game.triggerPlayerWin();
		}
	}

	@Override
	public void dispose() {
	}
}
