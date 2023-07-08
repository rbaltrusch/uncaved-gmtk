package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class Goal extends RectEntity implements Renderable, Disposable {

	private Color color;

	public Goal(Rectangle rect) {
		super(rect);
		color = new Color(1, 0, 1, 1);
	}

	@Override
	public void render(Renderer renderer) {
		renderer.drawRectangle(rect, color);
	}

	@Override
	public void dispose() {
	}
}
