package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CameraShake {

	private static final float MIN_FREQ = 12f; // ~2Hz
	private static final float MAX_FREQ = 18f; // ~3Hz

	private OrthographicCamera camera;
	private Vector2 offset;
	private boolean ongoing = false;
	private float maxTime;
	private float currentTime;
	private float totalMovedX;

	public CameraShake(OrthographicCamera camera, float maxTime) {
		this.camera = camera;
		this.maxTime = maxTime;
		offset = new Vector2();
	}

	public void update() {
		if (!ongoing) {
			return;
		}

		if (currentTime > maxTime) {
			reset();
			return;
		}

		float frequency = MathUtils.random(MIN_FREQ, MAX_FREQ);
		offset.x = (float) Math.sin(currentTime * frequency);
		totalMovedX += offset.x;
		camera.translate(offset);
		currentTime += Gdx.graphics.getDeltaTime();
	}

	public void start() {
		ongoing = true;
		offset = new Vector2();
		currentTime = 0;
		totalMovedX = 0;
	}

	public void reset() {
		camera.translate(new Vector2(-totalMovedX, 0)); // move back to initial pos
		offset = new Vector2();
		ongoing = false;
	}
}
