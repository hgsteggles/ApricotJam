package com.apricotjam.spacepanic.systems;

import java.util.Comparator;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.art.Shaders;
import com.apricotjam.spacepanic.components.BitmapFontComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.FBO_Component;
import com.apricotjam.spacepanic.components.FBO_ItemComponent;
import com.apricotjam.spacepanic.components.ShaderComponent;
import com.apricotjam.spacepanic.components.ShaderLightingComponent;
import com.apricotjam.spacepanic.components.ShaderTimeComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class RenderingSystem extends EntitySystem {
	public static final float PIXELS_TO_WORLD = BasicScreen.WORLD_WIDTH / SpacePanic.WIDTH;
	public static final float WORLD_TO_PIXELS = 1.0f / PIXELS_TO_WORLD;

	private Camera worldCamera, pixelcamera;
	private SpriteBatch batch;
	
	private ImmutableArray<Entity> fboList;
	private SortedEntityList fboRenderQueue;
	private SortedEntityList screenRenderQueue;

	public RenderingSystem(SpriteBatch batch, Camera worldCamera) {
		super();
		
		fboRenderQueue = new SortedEntityList(Family.all(TransformComponent.class, FBO_ItemComponent.class)
				.one(TextureComponent.class, BitmapFontComponent.class)
				.get(), new DepthComparator());
		
		screenRenderQueue = new SortedEntityList(Family.all(TransformComponent.class)
				.one(TextureComponent.class, BitmapFontComponent.class)
				.exclude(FBO_ItemComponent.class)
				.get(), new DepthComparator());

		this.batch = batch;

		this.worldCamera = worldCamera;

		this.pixelcamera = new OrthographicCamera(SpacePanic.WIDTH, SpacePanic.HEIGHT);
		this.pixelcamera.position.set(SpacePanic.WIDTH / 2.0f, SpacePanic.HEIGHT / 2.0f, 0);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		fboList = engine.getEntitiesFor(Family.all(FBO_Component.class).get());
		
		fboRenderQueue.addedToEngine(engine);
		screenRenderQueue.addedToEngine(engine);
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
		
		fboRenderQueue.removedFromEngine(engine);
		screenRenderQueue.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) {
		// Update cameras.
		worldCamera.update();
		pixelcamera.update();
		
		// Render to frame buffers.
		for (Entity entity : fboList) {
			FBO_Component fboComp = ComponentMappers.fbo.get(entity);
			Shaders.manager.beginFB(fboComp.FBO_ID);
			fboComp.batch.setProjectionMatrix(worldCamera.combined);
			fboComp.batch.begin();
		}
		Array<Entity> fboItemEntities = fboRenderQueue.getSortedEntities();
		for (Entity entity : fboItemEntities) {
			FBO_ItemComponent fboItemComp = ComponentMappers.fboitem.get(entity);
			render(entity, fboItemComp.fboBatch);
		}
		for (Entity entity : fboList) {
			FBO_Component fboComp = ComponentMappers.fbo.get(entity);
			fboComp.batch.end();
			Shaders.manager.endFB();
			TextureComponent textComp = ComponentMappers.texture.get(entity);
			textComp.region = new TextureRegion(Shaders.manager.getFBTexture(fboComp.FBO_ID));
			textComp.region.flip(false, true);
		}
		
		// Render to screen.
		batch.setProjectionMatrix(worldCamera.combined);
		batch.begin();
		Array<Entity> screenEntities = screenRenderQueue.getSortedEntities();
		for (Entity entity : screenEntities) {
			render(entity, batch);
		}
		batch.end();
	}
	
	private void render(Entity entity, SpriteBatch spriteBatch) {
		ShaderComponent shaderComp = null;
		if (ComponentMappers.shader.has(entity)) {
			shaderComp = ComponentMappers.shader.get(entity);
			
			spriteBatch.setShader(shaderComp.shader);
			
			if (ComponentMappers.shadertime.has(entity)) {
				ShaderTimeComponent shaderTimeComp = ComponentMappers.shadertime.get(entity);
				shaderComp.shader.setUniformf("time", shaderTimeComp.time);
			}
			if (ComponentMappers.shaderlight.has(entity)) {
				ShaderLightingComponent shaderLightComp = ComponentMappers.shaderlight.get(entity);
				shaderComp.shader.setUniformf("LightPos", shaderLightComp.lightPosition);
			}
		}
		
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
			
			spriteBatch.setColor(tex.color);
			
			if (tex.normal != null) {
				tex.normal.getTexture().bind(1);
				tex.region.getTexture().bind(0);
				
				spriteBatch.draw(tex.region.getTexture(), 
						   totalTransform.position.x - originX, totalTransform.position.y - originY,
					       width, height);
			}
			else {
				spriteBatch.draw(tex.region,
						   totalTransform.position.x - originX, totalTransform.position.y - originY,
						   originX, originY,
						   width, height,
						   totalTransform.scale.x, totalTransform.scale.y,
						   totalTransform.rotation);
			}
			
			spriteBatch.setShader(null);
			spriteBatch.setColor(Color.WHITE);
			
		} else if (ComponentMappers.bitmapfont.has(entity)) {
			spriteBatch.setProjectionMatrix(pixelcamera.combined);

			BitmapFontComponent bitmap = ComponentMappers.bitmapfont.get(entity);
			TransformComponent t = ComponentMappers.transform.get(entity);
			TransformComponent totalTransform = t.getTotalTransform();
			BitmapFont font = MiscArt.fonts.get(bitmap.font);

			font.setColor(bitmap.color);
			Vector2 pospixel = new Vector2(totalTransform.position.x * WORLD_TO_PIXELS, totalTransform.position.y * WORLD_TO_PIXELS);
			GlyphLayout layout = new GlyphLayout(font, bitmap.string);
			if (!bitmap.centering) {
				font.draw(spriteBatch,
						  bitmap.string,
						  pospixel.x, pospixel.y + layout.height);
			} else {
				font.draw(spriteBatch,
						  bitmap.string,
						  pospixel.x - layout.width / 2.0f,
						  pospixel.y + layout.height / 2.0f);
			}

			spriteBatch.setProjectionMatrix(worldCamera.combined);
			
		}
	}

	private static class DepthComparator implements Comparator<Entity> {
		@Override
		public int compare(Entity e1, Entity e2) {
			return (int) Math.signum(ComponentMappers.transform.get(e1).getTotalZ() - ComponentMappers.transform.get(e2).getTotalZ());
		}
	}
}
