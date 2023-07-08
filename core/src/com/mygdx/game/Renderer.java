package com.mygdx.game;

import java.util.Objects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public final class Renderer implements Disposable {

	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private Camera camera;

	public Renderer(Camera camera, SpriteBatch batch) {
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		this.batch = Objects.requireNonNull(batch);
		this.camera = Objects.requireNonNull(camera);
	}

	public void render(Iterable<Renderable> renderables) {
		startRender();
		renderables.forEach(x -> x.render(this));
		stopRender();
	}

	public void draw(Rectangle pos, Texture texture) {
		batch.draw(texture, pos.x, pos.y);
	}

	public void draw(Rectangle pos, TextureRegion textureRegion) {
//		draw(pos, textureRegion.getTexture());
		batch.draw(textureRegion, pos.x, pos.y);
	}

	public void drawRectangle(Rectangle rect, Color color) {
		drawRectangle(rect, color, true);
	}

	public void drawRectangle(Rectangle rect, Color color, boolean filled) {
		boolean batchDrawing = batch.isDrawing();
		if (batchDrawing) {
			batch.end();
		}

		shapeRenderer.begin();
		shapeRenderer.set(filled ? ShapeType.Filled : ShapeType.Line);
		shapeRenderer.setColor(color);
		shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
		shapeRenderer.end();

		if (batchDrawing) {
			batch.begin();
		}
	}

	public void drawCircle(Rectangle pos, float radius, Color color) {
		drawCircle(pos.x, pos.y, radius, color, true);
	}

	public void drawCircle(float x, float y, float radius, Color color) {
		drawCircle(x, y, radius, color, true);
	}

	public void drawCircle(float x, float y, float radius, Color color, boolean filled) {
//		shapeRenderer.set(filled ? ShapeType.Filled : ShapeType.Line);
//		shapeRenderer.setColor(color);
//		shapeRenderer.circle(x, y, radius);
	}

	private void startRender() {
		shapeRenderer.setProjectionMatrix(camera.combined);
		batch.setProjectionMatrix(camera.combined);
//		shapeRenderer.begin();
		batch.begin();
	}

	private void stopRender() {
//		shapeRenderer.end();
		batch.end();
	}

	@Override
	public void dispose() {
		shapeRenderer.dispose();
	}
}
