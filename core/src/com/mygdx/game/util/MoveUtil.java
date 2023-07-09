package com.mygdx.game.util;

import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public final class MoveUtil {
	private MoveUtil() {
	}

	public static Vector2 normalizeVector(Vector2 vector) {
		Objects.requireNonNull(vector);
		float deltaTime = Gdx.graphics.getDeltaTime();
		return new Vector2(vector.x * deltaTime, vector.y * deltaTime);
	}

	public static void moveNormalized(Rectangle pos, float speedX, float speedY) {
		float deltaTime = Gdx.graphics.getDeltaTime();
		pos.x += speedX * deltaTime;
		pos.y += speedY * deltaTime;
	}
}
