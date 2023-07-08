package com.mygdx.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

public final class MoveUtil {
	private MoveUtil() {
	}

	public static void moveNormalized(Rectangle pos, float speedX, float speedY) {
		float deltaTime = Gdx.graphics.getDeltaTime();
		pos.x += speedX * deltaTime;
		pos.y += speedY * deltaTime;
	}
}
