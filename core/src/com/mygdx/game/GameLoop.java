package com.mygdx.game;

import static com.mygdx.game.util.MusicUtil.fadeIn;

import java.util.stream.Stream;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
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
	private Sound deathSound;
	private Renderer renderer;
	private AiPlayer aiPlayer;
	private Goal goal;
	private Boulder boulder;
	private BitmapFont font;
	private SpriteBatch batch;

	private boolean over = false;

	@Override
	public void create() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Kenney Pixel.ttf"));
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		param.borderColor = new Color(61 / 256f, 43 / 256f, 31 / 256f, 1);
		param.color = new Color(227 / 256f, 218 / 256f, 201 / 256f, 1);
		param.borderWidth = 1f;
		param.size = 36;
		font = generator.generateFont(param);
		generator.dispose();

		music = Gdx.audio.newMusic(Gdx.files.internal("Atmospheric study combined.mp3"));
		music.setLooping(true);
		music.play();

		drumTap = Gdx.audio.newSound(Gdx.files.internal("99751__menegass__bongo1.wav"));
		drumTap2 = Gdx.audio.newSound(Gdx.files.internal("57297__satoration__bongo-dry-16bit-short.wav"));
		deathSound = Gdx.audio.newSound(Gdx.files.internal("watermelon.wav"));

		callbackHandler = new DelayedRunnableHandler();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		batch = new SpriteBatch();
		renderer = new Renderer(camera, batch);

		goal = createGoal();
		aiPlayer = createAiPlayer();
		boulder = createBoulder();
		TiledMap map = new TmxMapLoader().load("tiled/map1.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
	}

	private Goal createGoal() {
		Texture flagTexture = new Texture(Gdx.files.internal("schecky_flag_raise_strip15.png"));
		AnimationWrapper<TextureRegion> flagAnimation = AnimationWrapper.of(flagTexture).build(32, 45, 0.025f,
				x -> x[0]);
		return new Goal(new Rectangle(SCREEN_WIDTH - TILESIZE, 7 * TILESIZE, TILESIZE, 32), flagAnimation);
	}

	private AiPlayer createAiPlayer() {
		Texture warriorTexture = new Texture(Gdx.files.internal("warrior-spritesheet-larger.png"));
		AnimationWrapper<TextureRegion> warriorAnimation = AnimationWrapper.of(warriorTexture).build(64, 64, 0.1f,
				x -> x[0]);
		Texture deathTexture = new Texture(Gdx.files.internal("warrior-death.png"));
		AnimationWrapper<TextureRegion> deathAnimation = AnimationWrapper.of(deathTexture).build(64, 64, 0.05f,
				x -> x[0]);
		return new AiPlayer(new Rectangle(TILESIZE, 7 * TILESIZE, TILESIZE, TILESIZE * 2), warriorAnimation,
				deathAnimation);
	}

	private Boulder createBoulder() {
		return new Boulder(
				new Rectangle(SCREEN_WIDTH - TILESIZE * 4, SCREEN_HEIGHT - TILESIZE * 2, TILESIZE * 2, TILESIZE * 2),
				new Texture(Gdx.files.internal("block2.jpg")));
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
		Renderable restartText = over ? (renderer_) -> {
			GlyphLayout layout = new GlyphLayout(font, "Press R to restart");
			float x = (SCREEN_WIDTH - layout.width) / 2;
			float y = (SCREEN_HEIGHT - layout.height) / 2;
			font.draw(batch, layout, x, y);
		} : x -> {
		};
		renderer.render(Stream.of(goal, aiPlayer, boulder, restartText).map(x -> x)::iterator);
	}

	@Override
	public void dispose() {
		Stream.of(renderer, font, tiledMapRenderer, music, drumTap, drumTap2, deathSound, aiPlayer, goal, boulder)
				.forEach(Disposable::dispose);
	}

	public void handleInput() {
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
			Gdx.app.log("main", "Exiting app...");
		} else if (Gdx.input.isKeyJustPressed(Keys.F)) {
			toggleFullscreen();
		} else if (Gdx.input.isKeyJustPressed(Keys.D)) {
			boulder.drop();
		} else if (Gdx.input.isKeyJustPressed(Keys.R)) {
			restart();
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
		Stream.of(aiPlayer, goal, boulder).map(x -> (Actor) x).forEach(x -> x.update(this));
	}

	public void triggerPlayerWin() {
		if (over) {
			return;
		}

		aiPlayer.kill();
		deathSound.play();
		over = true;
		Gdx.app.log("main", "Player wins!");
	}

	public void restart() {
		over = false;
		time = 0;
		goal.dispose();
		goal = createGoal();

		aiPlayer.dispose();
		aiPlayer = createAiPlayer();

		boulder.dispose();
		boulder = createBoulder();
		callbackHandler = new DelayedRunnableHandler();

		// fade music back in
		if (!music.isPlaying()) {
			music.setVolume(0);
			music.play();
			fadeIn(music, 0.05f, callbackHandler);
		}
	}

	public void triggerAiWin() {
		if (over) {
			return;
		}

		goal.reach();
		music.stop();
		drumTap.play();
		callbackHandler.add(drumTap2::play, 1000);
		over = true;
		Gdx.app.log("main", "AI wins!");
	}

	public AiPlayer getAiPlayer() {
		return aiPlayer;
	}

	public Goal getGoal() {
		return goal;
	}

	public float getTime() {
		return time;
	}
}
