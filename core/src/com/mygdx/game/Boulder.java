package com.mygdx.game;

import java.util.Objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class Boulder extends RectEntity implements Actor, Renderable, Disposable {

	private Texture texture;
	private Sound stoneSound; // boulder cant dispose of the sound itself because it otherwise cant be reset!
	private Vector2 speed;
	private boolean dropping = false;

	protected Boulder(Rectangle rect, Texture texture, Sound stoneSound) {
		super(rect);
		this.texture = Objects.requireNonNull(texture);
		this.stoneSound = Objects.requireNonNull(stoneSound);
		speed = new Vector2(0, -800);
	}

	public void drop(SoundHandler soundHandler) {
		if (!dropping) {
			soundHandler.play(stoneSound);
		}
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
		texture.dispose();
	}
}
