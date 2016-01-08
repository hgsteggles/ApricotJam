package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.HelmetUI;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.components.mapComponents.MapScreenComponent;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.systems.*;
import com.apricotjam.spacepanic.systems.map.MapSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public class GameScreen extends BasicScreen {

	Entity mapSystemEntity;

	public static Vector2[] SCREWLOCATIONS = {
		new Vector2(1.5f, 6.45f),
		new Vector2(0.85f, 3.05f),
		new Vector2(4.3f, 1.05f)
	};

	public GameScreen(SpacePanic spacePanic) {
		super(spacePanic);

		add(new RenderingSystem(spriteBatch, worldCamera));
		add(new MovementSystem());
		add(new ScrollSystem());
		add(new ClickSystem());
		add(new AnimatedShaderSystem());
		add(new TweenSystem());

		addMapSystem();

		add(createBackground());
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		MapScreenComponent msc = ComponentMappers.mapscreen.get(mapSystemEntity);
		if (msc.currentState == MapScreenComponent.State.ENCOUNTER) {
			System.out.println("Look! A " + msc.encounterResource);
			msc.currentState = MapScreenComponent.State.PAUSED;
			TweenSpec ts = new TweenSpec();
			ts.start = 0.0f;
			ts.end = 360.0f;
			ts.cycle = TweenSpec.Cycle.ONCE;
			ts.interp = Interpolation.linear;
			ts.period = 2.0f;
			ts.tweenInterface = new TweenInterface() {
				@Override
				public void applyTween(Entity e, float a) {
					ComponentMappers.transform.get(e).rotation = a;
				}

				@Override
				public void endTween(Entity e) {
					ComponentMappers.mapscreen.get(e).currentState = MapScreenComponent.State.EXPLORING;
				}
			};
			ComponentMappers.tween.get(mapSystemEntity).tweenSpecs.add(ts);
		}
	}

	private void addMapSystem() {
		mapSystemEntity = new Entity();

		mapSystemEntity.add(new MapScreenComponent());

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		tranc.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		mapSystemEntity.add(tranc);

		mapSystemEntity.add(new TweenComponent());

		add(mapSystemEntity);
		add(new MapSystem(mapSystemEntity, 8.25f, 4.75f));
	}

	private Entity createBackground() {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		Texture tex = MiscArt.mainBackgroundScrollable;
		float texToCorner = (float)Math.sqrt((tex.getWidth() * tex.getWidth()) + (tex.getHeight() * tex.getHeight()));
		texComp.region = new TextureRegion(tex, 0, 0, (int)texToCorner, (int)texToCorner);

		texComp.size.x = texToCorner * RenderingSystem.PIXELS_TO_WORLD;
		texComp.size.y = texToCorner * RenderingSystem.PIXELS_TO_WORLD;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		transComp.position.z = -1.0f;

		MovementComponent movementComp = new MovementComponent();
		movementComp.rotationalVelocity = 5.0f;

		ScrollComponent scrollComp = new ScrollComponent();
		scrollComp.speed.x = 0.5f;

		e.add(texComp);
		e.add(transComp);
		e.add(movementComp);
		e.add(scrollComp);

		return e;
	}

	@Override
	public void backPressed() {
		spacePanic.setScreen(new MenuScreen(spacePanic));
	}
}
