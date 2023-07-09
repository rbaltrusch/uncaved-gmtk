package com.mygdx.game;

import static com.mygdx.game.util.MoveUtil.moveNormalized;

import java.util.Objects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class RectEntity {

	protected Rectangle rect;

	protected RectEntity(Rectangle rect) {
		this.rect = Objects.requireNonNull(rect);
	}

	public void move(Vector2 speed) {
		move(speed.x, speed.y);
	}

	public void move(float speedX, float speedY) {
		moveNormalized(rect, speedX, speedY);
	}

	public boolean overlaps(RectEntity entity) {
		return this.rect.overlaps(entity.rect);
	}

	public boolean overlaps(Rectangle rect) {
		return this.rect.overlaps(rect);
	}
}
