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
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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
	private static final int WARRIOR_SPEED_INCREASE_PER_WIN = 50;

	private DelayedRunnableHandler callbackHandler;
	private SoundHandler soundHandler;

	private OrthogonalTiledMapRenderer tiledMapRenderer;
	private OrthographicCamera camera;
	private CameraShake cameraShake;

	// disposable objects (need to be cleaned up at end)
	private Music music;
	private Sound drumTap;
	private Sound drumTap2;
	private Sound deathSound;
	private Sound stoneSound;
	private SoundCollection damageSound;
	private Renderer renderer;
	private AiPlayer aiPlayer;
	private Goal goal;
	private Boulder boulder;
	private BitmapFont font;
	private BitmapFont titleFont;
	private SpriteBatch batch;

	private boolean over = false;
	private boolean lost = false;
	private boolean inTitleScreen = true;
	private float time = 0;
	private int winCount = 0;
	private int highScore = 0;

	@Override
	public void create() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Kenney Pixel.ttf"));
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		param.borderColor = new Color(61 / 256f, 43 / 256f, 31 / 256f, 1);
		param.color = new Color(227 / 256f, 218 / 256f, 201 / 256f, 1);
		param.borderWidth = 1f;
		param.size = 36;
		font = generator.generateFont(param);

		param.borderWidth = 5f;
		param.size = 72;
		titleFont = generator.generateFont(param);
		generator.dispose();

		soundHandler = new SoundHandler();
		music = Gdx.audio.newMusic(Gdx.files.internal("Atmospheric study combined.mp3"));
		music.setLooping(true);
		soundHandler.play(music);

		drumTap = Gdx.audio.newSound(Gdx.files.internal("99751__menegass__bongo1.wav"));
		drumTap2 = Gdx.audio.newSound(Gdx.files.internal("57297__satoration__bongo-dry-16bit-short.wav"));
		deathSound = Gdx.audio.newSound(Gdx.files.internal("watermelon.wav"));
		stoneSound = Gdx.audio.newSound(Gdx.files.internal("stone2.wav"));

		// @formatter:off
		damageSound = new SoundCollection(
			Stream.iterate(1, x -> x + 1)
				.limit(6)
				.map(x -> String.format("damage%s.wav", x))
				.map(Gdx.files::internal)
				.map(Gdx.audio::newSound)
				.toList()
		);
		// @formatter:on

		callbackHandler = new DelayedRunnableHandler();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		cameraShake = new CameraShake(camera, 0.5f);
		batch = new SpriteBatch();
		renderer = new Renderer(camera, batch);

		goal = createGoal();
		aiPlayer = createAiPlayer();
		boulder = createBoulder();
		TiledMap map = new TmxMapLoader().load("tiled/map2.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
	}

	@Override
	public void render() {
		time += Gdx.graphics.getDeltaTime();
		handleInput();
		updateActors();
		callbackHandler.update();
		camera.update();
		cameraShake.update();

		ScreenUtils.clear(34f / 256, 32f / 256, 54f / 256, 1);
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();

		renderer.render(Stream.of(goal, aiPlayer, boulder).map(x -> (Renderable) x)::iterator);
		renderer.render(constructTextRenderables()::iterator);
	}

	@Override
	public void dispose() {
		Stream.of(renderer, batch, font, titleFont, tiledMapRenderer, music, drumTap, drumTap2, deathSound, aiPlayer,
				goal, boulder).forEach(Disposable::dispose);
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

		ParticleEffect walkEffect = new ParticleEffect();
		walkEffect.load(Gdx.files.internal("walk6.particleeffect"), Gdx.files.internal(""));
		walkEffect.start();

		return new AiPlayer(new Rectangle(TILESIZE, 7 * TILESIZE, TILESIZE, TILESIZE * 2), warriorAnimation,
				deathAnimation, walkEffect);
	}

	private Boulder createBoulder() {
		return new Boulder(new Rectangle(SCREEN_WIDTH - TILESIZE * 3.5f, SCREEN_HEIGHT - TILESIZE * 3 + 10,
				TILESIZE * 2, TILESIZE * 2), new Texture(Gdx.files.internal("block2.jpg")), stoneSound);
	}

	public Stream<Renderable> constructTextRenderables() {
		Renderable emptyRenderable = x -> {
		};
		Renderable titleText = inTitleScreen ? (renderer_) -> {
			// title
			GlyphLayout layout = new GlyphLayout(titleFont, "UNCAVED");
			float x = (SCREEN_WIDTH - layout.width) / 2;
			float y = (SCREEN_HEIGHT - layout.height) / 2 + 60;
			titleFont.draw(batch, layout, x, y);

			// controls
			layout = new GlyphLayout(font, "Press Enter to start");
			x = (SCREEN_WIDTH - layout.width) / 2;
			y = (SCREEN_HEIGHT - layout.height) / 2;
			font.draw(batch, layout, x, y);

			layout = new GlyphLayout(font, "Drop with D");
			Rectangle rect = boulder.getRectangle();
			x = rect.x + (rect.width - layout.width) / 2;
			y = rect.y - layout.height;
			font.draw(batch, layout, x, y);
		} : emptyRenderable;

		Renderable pointsText = inTitleScreen ? emptyRenderable : (renderer_) -> {
			// points
			GlyphLayout layout = new GlyphLayout(font, String.format("Points: %s", winCount));
			float x = TILESIZE;
			float y = SCREEN_HEIGHT - TILESIZE - 5;
			font.draw(batch, layout, x, y);

			// warrior speed
			layout = new GlyphLayout(font, String.format("Warrior speed: %s", (int) aiPlayer.getSpeed().x));
			y -= layout.height + 5;
			font.draw(batch, layout, x, y);
		};

		Renderable restartText = over ? (renderer_) -> {
			GlyphLayout layout = new GlyphLayout(font, "Press R to restart");
			float x = (SCREEN_WIDTH - layout.width) / 2;
			float y = (SCREEN_HEIGHT - layout.height) / 2;
			font.draw(batch, layout, x, y);
		} : emptyRenderable;

		Renderable highScoreText = lost ? (renderer_) -> {
			GlyphLayout layout = new GlyphLayout(font, String.format("Highscore: %s", highScore));
			float x = (SCREEN_WIDTH - layout.width) / 2;
			float y = (SCREEN_HEIGHT - layout.height) / 2 - layout.height - 5;
			font.draw(batch, layout, x, y);
		} : emptyRenderable;
		return Stream.of(restartText, titleText, pointsText, highScoreText);
	}

	public void handleInput() {
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
			Gdx.app.log("main", "Exiting app...");
		} else if (Gdx.input.isKeyJustPressed(Keys.F)) {
			toggleFullscreen();
		} else if (Gdx.input.isKeyJustPressed(Keys.M)) {
			soundHandler.toggleMute();
		} else if (!inTitleScreen && Gdx.input.isKeyJustPressed(Keys.D)) {
			boulder.drop(soundHandler);
		} else if (!inTitleScreen && Gdx.input.isKeyJustPressed(Keys.R)) {
			restart();
		} else if (inTitleScreen && (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.R))) {
			inTitleScreen = false;
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
		if (inTitleScreen) { // HACK
			return;
		}
		Stream.of(aiPlayer, goal, boulder).map(x -> (Actor) x).forEach(x -> x.update(this));
	}

	public void restart() {
		over = false;
		lost = false;
		time = 0;
		goal.dispose();
		goal = createGoal();

		aiPlayer.dispose();
		aiPlayer = createAiPlayer();
		aiPlayer.getSpeed().x += winCount * WARRIOR_SPEED_INCREASE_PER_WIN;

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

	public void triggerPlayerWin() {
		if (over) {
			return;
		}

		aiPlayer.kill();
		cameraShake.start();
		soundHandler.play(deathSound);
		soundHandler.play(damageSound);
		over = true;
		winCount++;
		aiPlayer.getSpeed().x += winCount * WARRIOR_SPEED_INCREASE_PER_WIN;
		highScore = Math.max(highScore, winCount);
		Gdx.app.log("main", "Player wins!");
	}

	public void triggerAiWin() {
		if (over) {
			return;
		}

		goal.reach();
		music.stop();
		soundHandler.play(drumTap);
		callbackHandler.add(() -> soundHandler.play(drumTap2), 1000);
		over = true;
		lost = true;
		winCount = 0;
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
