package com.mygdx.game;

import java.util.stream.Stream;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.util.DelayedRunnableHandler;

public final class GameLoop extends ApplicationAdapter {

	private Logger log;
	private DelayedRunnableHandler callbackHandler;

	// disposable objects (need to be cleaned up at end)
	private Music music;
	private Sound drumTap;
	private Sound drumTap2;
	private Renderer renderer;
	private AiPlayer aiPlayer;
	private Goal goal;

	private boolean over = false;

	@Override
	public void create() {
		log = new Logger("main");
		callbackHandler = new DelayedRunnableHandler();

		OrthographicCamera camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		renderer = new Renderer(camera);

		aiPlayer = new AiPlayer(new Rectangle(0, 100, 30, 50));
		goal = new Goal(new Rectangle(700, 100, 30, 50));

		music = Gdx.audio.newMusic(Gdx.files.internal("Atmospheric study combined.mp3"));
		music.setLooping(true);
		music.play();

		drumTap = Gdx.audio.newSound(Gdx.files.internal("99751__menegass__bongo1.wav"));
		drumTap2 = Gdx.audio.newSound(Gdx.files.internal("57297__satoration__bongo-dry-16bit.wav"));
	}

	@Override
	public void render() {
		handleInput();
		updateActors();
		callbackHandler.update();
		ScreenUtils.clear(0, 0, 0, 1);
		renderer.render(Stream.of(aiPlayer, goal).map(x -> (Renderable) x)::iterator);
	}

	@Override
	public void dispose() {
		Stream.of(renderer, music, drumTap, drumTap2, aiPlayer, goal).forEach(Disposable::dispose);
	}

	public void handleInput() {
		if (Gdx.input.isButtonJustPressed(Keys.LEFT)) {
			log.info("left pressed");
		}
	}

	public void updateActors() {
		aiPlayer.update(this);
	}

	public void triggerAiWin() {
		if (over) {
			log.info("Cannot trigger ai win. Game is already over.");
			return;
		}

		music.stop();
		drumTap.play();
		callbackHandler.add(drumTap2::play, 1000);
		over = true;
	}

	public Goal getGoal() {
		return goal;
	}
}
