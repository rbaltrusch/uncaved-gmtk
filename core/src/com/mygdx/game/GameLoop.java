package com.mygdx.game;

import java.util.stream.Stream;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.util.DelayedRunnableHandler;

public final class GameLoop extends ApplicationAdapter {

	private static final int TILESIZE = 32;
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 640;

	private DelayedRunnableHandler callbackHandler;
	private float time = 0;

	private OrthogonalTiledMapRenderer tiledMapRenderer;
	private OrthographicCamera camera;

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
		callbackHandler = new DelayedRunnableHandler();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 640);
		renderer = new Renderer(camera);

		Texture flagTexture = new Texture(Gdx.files.internal("schecky_flag_raise_strip15.png"));
		AnimationWrapper<TextureRegion> flagAnimation = AnimationWrapper.of(flagTexture).build(32, 45, 0.025f,
				x -> x[0]);
		goal = new Goal(new Rectangle(Gdx.graphics.getWidth() - TILESIZE, 7 * TILESIZE, TILESIZE, 32), flagAnimation);

		Texture warriorTexture = new Texture(Gdx.files.internal("warrior-spritesheet-larger.png"));
		AnimationWrapper<TextureRegion> warriorAnimation = AnimationWrapper.of(warriorTexture).build(64, 64, 0.1f,
				x -> x[0]);
		aiPlayer = new AiPlayer(new Rectangle(0, 7 * TILESIZE, TILESIZE * 2, TILESIZE * 2), warriorAnimation);

		music = Gdx.audio.newMusic(Gdx.files.internal("Atmospheric study combined.mp3"));
		music.setLooping(true);
		music.play();

		drumTap = Gdx.audio.newSound(Gdx.files.internal("99751__menegass__bongo1.wav"));
		drumTap2 = Gdx.audio.newSound(Gdx.files.internal("57297__satoration__bongo-dry-16bit-short.wav"));
		TiledMap map = new TmxMapLoader().load("tiled/map1.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
	}

	@Override
	public void render() {
		time += Gdx.graphics.getDeltaTime();
		handleInput();
		updateActors();
		callbackHandler.update();

		camera.update();
		ScreenUtils.clear(159f / 256, 129f / 256, 112f / 256, 1);
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		renderer.render(Stream.of(goal, aiPlayer).map(x -> (Renderable) x)::iterator);
	}

	@Override
	public void dispose() {
		Stream.of(renderer, tiledMapRenderer, music, drumTap, drumTap2, aiPlayer, goal).forEach(Disposable::dispose);
	}

	public void handleInput() {
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
			Gdx.app.log("main", "Exiting app...");
		} else if (Gdx.input.isKeyJustPressed(Keys.F)) {
			toggleFullscreen();
		}
	}

	private void toggleFullscreen() {
		boolean fullscreen = Gdx.graphics.isFullscreen();
		boolean success = false;
		if (fullscreen) {
			success = Gdx.graphics.setWindowedMode(SCREEN_WIDTH, SCREEN_HEIGHT);
		} else {
			DisplayMode mode = Gdx.graphics.getDisplayMode();
			success = Gdx.graphics.setFullscreenMode(mode);
		}
		Gdx.app.log("main", String.format("Setting fullscreen to %s, success: %s", !fullscreen, success));
	}

	public void updateActors() {
		aiPlayer.update(this);
		goal.update(this);
	}

	public void triggerAiWin() {
		if (over) {
			Gdx.app.log("main", "Cannot trigger ai win. Game is already over.");
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

	public float getTime() {
		return time;
	}
}
