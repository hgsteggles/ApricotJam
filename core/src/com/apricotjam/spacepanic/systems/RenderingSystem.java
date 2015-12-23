package com.apricotjam.spacepanic.systems;

import java.util.Comparator;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.BitmapFontComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class RenderingSystem extends SortedIteratingSystem {
	public static final float WORLD_WIDTH = 16f;
	public static final float WORLD_HEIGHT = 9f;
	public static final float PIXELS_TO_WORLD = 1f/32f;
	
	private OrthographicCamera camera, pixelcamera;
	private SpriteBatch batch;

	public RenderingSystem(SpriteBatch batch) {
		super(Family.all(TransformComponent.class)
					.one(TextureComponent.class, BitmapFontComponent.class)
					.get(), new DepthComparator());
		
		this.batch = batch;
		
		camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
		camera.position.set(WORLD_WIDTH/2f, WORLD_HEIGHT/2f, 0);
		
		pixelcamera = new OrthographicCamera(SpacePanic.WIDTH, SpacePanic.HEIGHT);
		pixelcamera.position.set(SpacePanic.WIDTH/2f, SpacePanic.HEIGHT/2f, 0);
	}

	@Override
	public void update(float deltaTime) {
		camera.update();
		pixelcamera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		super.update(deltaTime);
		batch.end();
	}

	private static class DepthComparator implements Comparator<Entity> {
		@Override
		public int compare(Entity e1, Entity e2) {
			return (int)Math.signum(ComponentMappers.transform.get(e1).position.z - ComponentMappers.transform.get(e2).position.z);
		}
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if (ComponentMappers.texture.has(entity)) {
			TextureComponent tex = ComponentMappers.texture.get(entity);

			if (tex.region == null) {
				return;
			}

			TransformComponent t = ComponentMappers.transform.get(entity);

			float width = tex.region.getRegionWidth();
			float height = tex.region.getRegionHeight();
			float originX = width * 0.5f;
			float originY = height * 0.5f;

			batch.draw(tex.region,
					t.position.x - originX, t.position.y - originY,
					originX, originY,
					width, height,
					t.scale.x * PIXELS_TO_WORLD, t.scale.y * PIXELS_TO_WORLD,
					MathUtils.radiansToDegrees * t.rotation);
		} else if (ComponentMappers.bitmapfont.has(entity)) {
			batch.setProjectionMatrix(pixelcamera.combined);

			BitmapFontComponent bitmap = ComponentMappers.bitmapfont.get(entity);
			TransformComponent t = ComponentMappers.transform.get(entity);
			BitmapFont font = MiscArt.fonts.get(bitmap.font);

			font.setColor(bitmap.color);
			if (!bitmap.centering) {
				font.draw(batch, bitmap.string, t.position.x, t.position.y);
			} else {
				GlyphLayout layout = new GlyphLayout(font, bitmap.string);
				font.draw(batch, bitmap.string, t.position.x - layout.width / 2f, t.position.y - layout.height / 2f);
			}

			batch.setProjectionMatrix(camera.combined);
		}
	}
}
