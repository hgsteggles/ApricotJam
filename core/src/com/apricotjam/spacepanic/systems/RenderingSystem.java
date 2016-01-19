package com.apricotjam.spacepanic.systems;


import java.util.Comparator;
import java.util.HashMap;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.Assets;
import com.apricotjam.spacepanic.art.Shaders;
import com.apricotjam.spacepanic.components.BitmapFontComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.FBO_Component;
import com.apricotjam.spacepanic.components.FBO_ItemComponent;
import com.apricotjam.spacepanic.components.NinepatchComponent;
import com.apricotjam.spacepanic.components.ParticleEffectComponent;
import com.apricotjam.spacepanic.components.ShaderComponent;
import com.apricotjam.spacepanic.components.ShaderDirectionComponent;
import com.apricotjam.spacepanic.components.ShaderLightingComponent;
import com.apricotjam.spacepanic.components.ShaderMaskComponent;
import com.apricotjam.spacepanic.components.ShaderSpreadComponent;
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
	private SortedEntityList fboRenderQueue, fbo2RenderQueue;
	private SortedEntityList screenRenderQueue;

	public RenderingSystem(SpriteBatch batch, Camera worldCamera) {
		super();
		
		fboRenderQueue = new SortedEntityList(Family.all(TransformComponent.class, FBO_ItemComponent.class)
				.exclude(FBO_Component.class)
				.one(TextureComponent.class, BitmapFontComponent.class, NinepatchComponent.class, ParticleEffectComponent.class)
				.get(), new DepthFBOComparator());
		
		fbo2RenderQueue = new SortedEntityList(Family.all(TransformComponent.class, FBO_ItemComponent.class, FBO_Component.class)
				.one(TextureComponent.class, BitmapFontComponent.class, NinepatchComponent.class, ParticleEffectComponent.class)
				.get(), new DepthFBOComparator());
		
		screenRenderQueue = new SortedEntityList(Family.all(TransformComponent.class)
				.one(TextureComponent.class, BitmapFontComponent.class, NinepatchComponent.class, ParticleEffectComponent.class)
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

		//Create FBO index
		HashMap<String, Entity> fboIndex = new HashMap<String, Entity>();

		for (Entity entity : fboList) {
			FBO_Component fboComp = ComponentMappers.fbo.get(entity);
			fboIndex.put(fboComp.FBO_ID, entity);
			Shaders.manager.beginFB(fboComp.FBO_ID);
			Shaders.manager.endFB();
		}

		//Render items to FBOs
		String currentFBO = "";
		Array<Entity> fboEntities = fboRenderQueue.getSortedEntities();
		for (Entity entity : fboEntities) {
			FBO_ItemComponent fboItemComp = ComponentMappers.fboitem.get(entity);
			if (!fboItemComp.fboID.equals(currentFBO)) {
				if (!currentFBO.equals("")) {
					endFBO(currentFBO, fboIndex);
				}
				currentFBO = fboItemComp.fboID;
				startFBO(currentFBO, fboIndex);
			}
			render(entity, fboItemComp.fboBatch);
		}

		//Render FBOs to FBOs.
		Array<Entity> fbo2Entities = fbo2RenderQueue.getSortedEntities();
		for (Entity entity : fbo2Entities) {
			FBO_ItemComponent fbo2ItemComp = ComponentMappers.fboitem.get(entity);
			if (!fbo2ItemComp.fboID.equals(currentFBO)) {
				if (!currentFBO.equals("")) {
					endFBO(currentFBO, fboIndex);
				}
				currentFBO = fbo2ItemComp.fboID;
				startFBO(currentFBO, fboIndex);
			}
			render(entity, fbo2ItemComp.fboBatch);
		}

		if (!currentFBO.equals("")) {
			endFBO(currentFBO, fboIndex);
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

	private void startFBO(String id, HashMap<String, Entity> fboIndex) {
		FBO_Component fboComp = ComponentMappers.fbo.get(fboIndex.get(id));
		Shaders.manager.beginFB(fboComp.FBO_ID, fboComp.clearColor);
		fboComp.batch.setProjectionMatrix(fboComp.camera.combined);
		fboComp.batch.begin();
	}

	private void endFBO(String id, HashMap<String, Entity> fboIndex) {
		Entity entity = fboIndex.get(id);
		FBO_Component fboComp = ComponentMappers.fbo.get(entity);
		fboComp.batch.end();
		Shaders.manager.endFB();
		TextureComponent textComp = ComponentMappers.texture.get(entity);
		textComp.region = new TextureRegion(Shaders.manager.getFBTexture(fboComp.FBO_ID));
		textComp.region.flip(false, true);
	}

	private void render(Entity entity, SpriteBatch spriteBatch) {
		if (ComponentMappers.shader.has(entity)) {
			ShaderComponent shaderComp = ComponentMappers.shader.get(entity);

			spriteBatch.setShader(shaderComp.shader);

			if (ComponentMappers.shadertime.has(entity)) {
				ShaderTimeComponent shaderTimeComp = ComponentMappers.shadertime.get(entity);
				shaderComp.shader.setUniformf("time", shaderTimeComp.time);
			}
			if (ComponentMappers.shaderlight.has(entity)) {
				ShaderLightingComponent shaderLightComp = ComponentMappers.shaderlight.get(entity);
				shaderComp.shader.setUniformf("LightPos", shaderLightComp.lightPosition);
			}
			if (ComponentMappers.shaderdirection.has(entity)) {
				ShaderDirectionComponent shaderDirComp = ComponentMappers.shaderdirection.get(entity);
				shaderComp.shader.setUniformf("direction", shaderDirComp.direction);
			}
			if (ComponentMappers.shaderspread.has(entity)) {
				ShaderSpreadComponent shaderSpreadComp = ComponentMappers.shaderspread.get(entity);
				shaderComp.shader.setUniformf("spread", shaderSpreadComp.spread);
			}
			if (ComponentMappers.shadermask.has(entity)) {
				ShaderMaskComponent shaderMaskComp = ComponentMappers.shadermask.get(entity).getTotalTransformedMask();
				float x = shaderMaskComp.position.x / BasicScreen.WORLD_WIDTH;
				float y = shaderMaskComp.position.y / BasicScreen.WORLD_HEIGHT;
				float w = shaderMaskComp.size.x / BasicScreen.WORLD_WIDTH;
				float h = shaderMaskComp.size.y / BasicScreen.WORLD_HEIGHT;

				shaderComp.shader.setUniformf("maskRect", x, y, w, h);
			}
		}

		if (ComponentMappers.texture.has(entity)) {
			TextureComponent tex = ComponentMappers.texture.get(entity);

			if (tex.region == null || tex.region.getTexture() == null) {
				return;
			}

			TransformComponent t = ComponentMappers.transform.get(entity);
			TransformComponent totalTransform = t.getTotalTransform();

			float width = tex.size.x;
			float height = tex.size.y;
			float originX = 0.0f;
			float originY = 0.0f;

			if (tex.centre) {
				originX = width * 0.5f;
				originY = height * 0.5f;
			}

			spriteBatch.setColor(tex.color);

			if (tex.normal != null) {
				tex.normal.getTexture().bind(1);
				tex.region.getTexture().bind(0);

				spriteBatch.draw(tex.region.getTexture(), totalTransform.position.x - originX, totalTransform.position.y - originY, width, height);
			} else {
				spriteBatch.draw(tex.region, totalTransform.position.x - originX, totalTransform.position.y - originY, originX, originY, width, height, totalTransform.scale.x, totalTransform.scale.y, totalTransform.rotation);
			}

			spriteBatch.setColor(Color.WHITE);

		} else if (ComponentMappers.bitmapfont.has(entity)) {
			spriteBatch.setProjectionMatrix(pixelcamera.combined);

			BitmapFontComponent bitmap = ComponentMappers.bitmapfont.get(entity);
			TransformComponent t = ComponentMappers.transform.get(entity);
			TransformComponent totalTransform = t.getTotalTransform();
			BitmapFont font = Assets.fonts.get(bitmap.font);

			font.setColor(bitmap.color);
			font.getData().setScale(bitmap.scale);
			
			Vector2 pospixel = new Vector2(totalTransform.position.x * WORLD_TO_PIXELS, totalTransform.position.y * WORLD_TO_PIXELS);
			GlyphLayout layout = new GlyphLayout(font, bitmap.string);
			if (!bitmap.centering) {
				font.draw(spriteBatch, bitmap.string, pospixel.x, pospixel.y + layout.height);
			} else {
				font.draw(spriteBatch, bitmap.string, pospixel.x - layout.width / 2.0f, pospixel.y + layout.height / 2.0f);
			}

			spriteBatch.setProjectionMatrix(worldCamera.combined);

		} else if (ComponentMappers.ninepatch.has(entity)) {
			spriteBatch.setProjectionMatrix(pixelcamera.combined);

			NinepatchComponent nine = ComponentMappers.ninepatch.get(entity);

			if (nine.patch == null) {
				return;
			}

			TransformComponent t = ComponentMappers.transform.get(entity);
			TransformComponent totalTransform = t.getTotalTransform();

			Vector2 pospixel = new Vector2(totalTransform.position.x * WORLD_TO_PIXELS, totalTransform.position.y * WORLD_TO_PIXELS);

			float width = nine.size.x * WORLD_TO_PIXELS;
			float height = nine.size.y * WORLD_TO_PIXELS;
			float originX = 0.0f;
			float originY = 0.0f;

			if (nine.centre) {
				originX = width * 0.5f;
				originY = height * 0.5f;
			}

			spriteBatch.setColor(nine.color);

			nine.patch.draw(spriteBatch, pospixel.x - originX, pospixel.y - originY, width, height);

			spriteBatch.setColor(Color.WHITE);
			spriteBatch.setProjectionMatrix(worldCamera.combined);
		} else if (ComponentMappers.particle.has(entity)) {
			ParticleEffectComponent particleComp = ComponentMappers.particle.get(entity);
			
			particleComp.effect.draw(spriteBatch);
		}

		if (ComponentMappers.shader.has(entity)) {
			spriteBatch.setShader(null);
		}
	}

	private static class DepthComparator implements Comparator<Entity> {
		@Override
		public int compare(Entity e1, Entity e2) {
			return (int) Math.signum(ComponentMappers.transform.get(e1).getTotalZ() - ComponentMappers.transform.get(e2).getTotalZ());
		}
	}

	private static class DepthFBOComparator implements Comparator<Entity> {
		@Override
		public int compare(Entity e1, Entity e2) {
			String id1 = ComponentMappers.fboitem.get(e1).fboID;
			String id2 = ComponentMappers.fboitem.get(e2).fboID;
			if (id1.equals(id2)) {
				return (int) Math.signum(ComponentMappers.transform.get(e1).getTotalZ() - ComponentMappers.transform.get(e2).getTotalZ());
			} else {
				return id1.compareTo(id2);
			}
		}
	}
}
