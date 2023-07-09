package com.mygdx.game;

import java.util.Objects;
import java.util.stream.Stream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class Boulder extends RectEntity implements Actor, Renderable, Disposable {

	private Texture texture;
	private Sound stoneSound; // boulder cant dispose of the sound itself because it otherwise cant be reset!
	private Vector2 speed;
	private float dropTime = 0;
	private boolean dropping = false;

	protected Boulder(Rectangle rect, Texture texture, Sound stoneSound) {
		super(rect);
		this.texture = Objects.requireNonNull(texture);
		this.stoneSound = Objects.requireNonNull(stoneSound);
		speed = new Vector2(0, -800);
	}

	public void drop(SoundHandler soundHandler) {
		if (dropping) {
			return;
		}

		dropTime = 0;
		soundHandler.play(stoneSound);
		dropping = true;
	}

	@Override
	public void render(Renderer renderer) {
		renderer.draw(rect, texture);

		// draw dropping particle effect
		if (dropping) {
			float height = Math.min(100, 200 * dropTime);
			float width = 2;
			Color color = new Color(0.3f, 0.3f, 0.3f, 0.3f);
			float offset = 10;
			Stream.iterate(0, x -> x + 1).limit(6)
					.map(x -> new Rectangle(rect.x + x * offset + 7, rect.y + rect.height + 10, width, height))
					.forEach(x -> renderer.drawRectangle(x, color));
//			renderer.draw(, null);
		}
	}

	@Override
	public void update(GameLoop game) {
		if (dropping) {
			move(speed);
			dropTime += Gdx.graphics.getDeltaTime();
		}

		if (this.overlaps(game.getAiPlayer())) {
			game.triggerPlayerWin();
		}
	}

	@Override
	public void dispose() {
		texture.dispose();
	}
}
