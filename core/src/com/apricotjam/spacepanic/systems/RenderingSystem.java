package com.apricotjam.spacepanic.systems;

import java.util.Comparator;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.AnimatedShaderComponent;
import com.apricotjam.spacepanic.components.BitmapFontComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.StateComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class RenderingSystem extends SortedIteratingSystem {
	public static final float PIXELS_TO_WORLD = BasicScreen.WORLD_WIDTH / SpacePanic.WIDTH;
	public static final float WORLD_TO_PIXELS = 1.0f / PIXELS_TO_WORLD;

	private Camera worldCamera, pixelcamera;
	private SpriteBatch batch;

	public RenderingSystem(SpriteBatch batch, Camera worldCamera) {
		super(Family.all(TransformComponent.class)
					.one(TextureComponent.class, BitmapFontComponent.class)
					.get(), new DepthComparator());

		this.batch = batch;

		this.worldCamera = worldCamera;

		this.pixelcamera = new OrthographicCamera(SpacePanic.WIDTH, SpacePanic.HEIGHT);
		this.pixelcamera.position.set(SpacePanic.WIDTH / 2.0f, SpacePanic.HEIGHT / 2.0f, 0);
	}

	@Override
	public void update(float deltaTime) {
		worldCamera.update();
		pixelcamera.update();
		batch.setProjectionMatrix(worldCamera.combined);

		batch.begin();
		super.update(deltaTime);
		batch.end();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if (ComponentMappers.texture.has(entity)) {
			TextureComponent tex = ComponentMappers.texture.get(entity);

			if (tex.region == null) {
				return;
			}

			TransformComponent t = ComponentMappers.transform.get(entity);
			TransformComponent totalTransform = t.getTotalTransform();

			float width = tex.size.x;
			float height = tex.size.y;
			float originX = width * 0.5f;
			float originY = height * 0.5f;

			if (ComponentMappers.animatedshader.has(entity)) {
				AnimatedShaderComponent animShaderComp = ComponentMappers.animatedshader.get(entity);
				batch.setShader(animShaderComp.shader);
				if (ComponentMappers.state.has(entity)) {
					StateComponent stateComp = ComponentMappers.state.get(entity);
					animShaderComp.shader.setUniformf("time", stateComp.time);
				}
			}
			
			batch.draw(tex.region,
					   totalTransform.position.x - originX, totalTransform.position.y - originY,
					   originX, originY,
					   width, height,
					   totalTransform.scale.x, totalTransform.scale.y,
					   totalTransform.rotation);
			
			batch.setShader(null);
		} else if (ComponentMappers.bitmapfont.has(entity)) {
			batch.setProjectionMatrix(pixelcamera.combined);

			BitmapFontComponent bitmap = ComponentMappers.bitmapfont.get(entity);
			TransformComponent t = ComponentMappers.transform.get(entity);
			TransformComponent totalTransform = t.getTotalTransform();
			BitmapFont font = MiscArt.fonts.get(bitmap.font);

			font.setColor(bitmap.color);
			Vector2 pospixel = new Vector2(totalTransform.position.x * WORLD_TO_PIXELS, totalTransform.position.y * WORLD_TO_PIXELS);
			GlyphLayout layout = new GlyphLayout(font, bitmap.string);
			if (!bitmap.centering) {
				font.draw(batch,
						  bitmap.string,
						  pospixel.x, pospixel.y + layout.height);
			} else {
				font.draw(batch,
						  bitmap.string,
						  pospixel.x - layout.width / 2.0f,
						  pospixel.y + layout.height / 2.0f);
			}

			batch.setProjectionMatrix(worldCamera.combined);
		}
	}

	private static class DepthComparator implements Comparator<Entity> {
		@Override
		public int compare(Entity e1, Entity e2) {
			return (int) Math.signum(ComponentMappers.transform.get(e1).getTotalZ() - ComponentMappers.transform.get(e2).getTotalZ());
		}
	}
}
