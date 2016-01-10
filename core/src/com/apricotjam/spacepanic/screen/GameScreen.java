package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.GameParameters;
import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.components.helmet.HelmetScreenComponent;
import com.apricotjam.spacepanic.components.mapComponents.MapScreenComponent;
import com.apricotjam.spacepanic.gameelements.Resource;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.systems.*;
import com.apricotjam.spacepanic.systems.helmet.HelmetSystem;
import com.apricotjam.spacepanic.systems.helmet.HelmetWorld;
import com.apricotjam.spacepanic.systems.map.MapSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;

public class GameScreen extends BasicScreen {

	public static float BACKGROUND_MOVEMENT_FACTOR = 0.1f;

	Entity mapSystemEntity;
	Entity helmetSystemEntity;
	TextureComponent backgroundTexComp;

	public GameScreen(SpacePanic spacePanic) {
		super(spacePanic);

		add(new RenderingSystem(spriteBatch, worldCamera));
		add(new ClickSystem());
		add(new AnimatedShaderSystem());
		add(new TweenSystem());
		add(new LineSystem());
		add(new MovementSystem());
		add(new ScrollSystem());
		add(new AnimationSystem());;
		add(new ShaderLightingSystem());
		add(new TickerSystem());

		//add(new PipeSystem());
		addHelmetSystem();
		addMapSystem();

		add(createBackground());
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		MapScreenComponent msc = ComponentMappers.mapscreen.get(mapSystemEntity);
		if (msc.currentState == MapScreenComponent.State.ENCOUNTER) {
			System.out.println("Look! A " + msc.encounterResource);
			alterResource(msc.encounterResource, GameParameters.RESOURCE_GAIN.get(msc.encounterResource));
			msc.currentState = MapScreenComponent.State.EXPLORING;
			/*msc.currentState = MapScreenComponent.State.PAUSED;
			TweenSpec ts = new TweenSpec();
			ts.start = BasicScreen.WORLD_HEIGHT / 2.0f + 0.3f;
			ts.end =  -2.0f;
			ts.cycle = TweenSpec.Cycle.LOOP;
			ts.reverse = true;
			ts.loops = 2;
			ts.interp = Interpolation.linear;
			ts.period = 2.0f;
			ts.tweenInterface = new TweenInterface() {
				@Override
				public void applyTween(Entity e, float a) {
					ComponentMappers.transform.get(e).position.y = a;
				}

				@Override
				public void endTween(Entity e) {
					ComponentMappers.mapscreen.get(e).currentState = MapScreenComponent.State.EXPLORING;
				}
			};
			ComponentMappers.tween.get(mapSystemEntity).tweenSpecs.add(ts);*/
		} else if (msc.currentState == MapScreenComponent.State.EXPLORING) {
			setBackgroundPosition();
		}

		for (Resource r: Resource.values()) {
			alterResource(r, GameParameters.RESOURCE_DEPLETION.get(r) * delta);
		}

		HelmetScreenComponent hsc = ComponentMappers.helmetscreen.get(helmetSystemEntity);
		System.out.println(hsc.resourceCount.get(Resource.OXYGEN));
	}

	private void alterResource(Resource resource, float amount) {
		HelmetScreenComponent hsc = ComponentMappers.helmetscreen.get(helmetSystemEntity);
		float current = hsc.resourceCount.get(resource);
		float next = Math.max(current + amount, 0.0f);
		hsc.resourceCount.put(resource, next);
	}

	private void setBackgroundPosition() {
		MapScreenComponent msc = ComponentMappers.mapscreen.get(mapSystemEntity);
		float width = backgroundTexComp.region.getRegionWidth();
		float height = backgroundTexComp.region.getRegionHeight();
		float x = msc.playerPosition.x * BACKGROUND_MOVEMENT_FACTOR * width / backgroundTexComp.size.x;
		float y = msc.playerPosition.y * BACKGROUND_MOVEMENT_FACTOR * height / backgroundTexComp.size.y;
		backgroundTexComp.region.setRegionX((int)(x));
		backgroundTexComp.region.setRegionWidth((int)width);
		backgroundTexComp.region.setRegionY(-1 * (int)(y));
		backgroundTexComp.region.setRegionHeight((int)height);
	}

	private void addMapSystem() {
		mapSystemEntity = new Entity();

		mapSystemEntity.add(new MapScreenComponent());

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		tranc.position.y = BasicScreen.WORLD_HEIGHT / 2.0f + 0.3f;
		tranc.position.z = 5.0f;
		mapSystemEntity.add(tranc);

		mapSystemEntity.add(new TweenComponent());

		add(mapSystemEntity);
		add(new MapSystem(mapSystemEntity, 8.25f, 4.75f));
	}

	private void addHelmetSystem() {
		helmetSystemEntity = new Entity();

		HelmetScreenComponent helmetScreenComponent = new HelmetScreenComponent();
		for (Resource r: Resource.values()) {
			helmetScreenComponent.maxCount.put(r, GameParameters.RESOURCE_MAX.get(r));
			helmetScreenComponent.resourceCount.put(r, GameParameters.RESOURCE_MAX.get(r));
		}
		helmetSystemEntity.add(helmetScreenComponent);

		add(new HelmetSystem(helmetSystemEntity));
	}

	private Entity createBackground() {
		Entity e = new Entity();

		backgroundTexComp = new TextureComponent();
		Texture tex = MiscArt.mainBackgroundScrollable;
		float texToCorner = (float)Math.sqrt((tex.getWidth() * tex.getWidth()) + (tex.getHeight() * tex.getHeight()));
		backgroundTexComp.region = new TextureRegion(tex, 0, 0, (int)texToCorner, (int)texToCorner);

		backgroundTexComp.size.x = texToCorner * RenderingSystem.PIXELS_TO_WORLD;
		backgroundTexComp.size.y = texToCorner * RenderingSystem.PIXELS_TO_WORLD;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		transComp.position.z = -1000.0f;

		e.add(backgroundTexComp);
		e.add(transComp);

		return e;
	}

	@Override
	public void backPressed() {
		spacePanic.setScreen(new MenuScreen(spacePanic));
	}
}
