package com.apricotjam.spacepanic.misc;

import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.apricotjam.spacepanic.systems.RenderingSystem;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;

public class EntityUtil {
	private EntityUtil() {}

	public static Entity clone(Entity entity) {
		Entity clone = new Entity();
		for (Component c: entity.getComponents()) {
			clone.add(c);
		}
		return clone;
	}

	public static Entity createTitleEntity(float y) {
		Entity titleEntity = new Entity();

		TextureComponent textComp = new TextureComponent();
		textComp.region = MiscArt.title;
		textComp.size.x = 5.0f;
		textComp.size.y = textComp.size.x * textComp.region.getRegionHeight() / textComp.region.getRegionWidth();
		titleEntity.add(textComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2f;
		transComp.position.y = y;
		titleEntity.add(transComp);

		titleEntity.add(new TweenComponent());

		return titleEntity;
	}

	public static Entity createAstronaut() {
		Entity astronaut = new Entity();

		TextureComponent textComp = new TextureComponent();
		textComp.region = MiscArt.astronautTitle;
		textComp.size.x = 1.0f;
		textComp.size.y = textComp.size.x * textComp.region.getRegionHeight() / textComp.region.getRegionWidth();
		astronaut.add(textComp);

		TransformComponent offset = new TransformComponent();
		offset.position.x = -3.0f;
		offset.position.y = -1.0f;

		TransformComponent floating = new TransformComponent();
		floating.position.x = 0.0f;
		floating.position.y = 0.0f;
		floating.parent = offset;
		astronaut.add(floating);

		TweenComponent tweenComponent = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 0.0f;
		tweenSpec.end = 0.5f;
		tweenSpec.period = 2.0f;
		tweenSpec.interp = Interpolation.sine;
		tweenSpec.cycle = TweenSpec.Cycle.INFLOOP;
		tweenSpec.reverse = true;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				ComponentMappers.transform.get(e).position.y = a;
			}
		};
		tweenComponent.tweenSpecs.add(tweenSpec);
		astronaut.add(tweenComponent);

		return astronaut;
	}

	public static Entity createBackground() {
		Entity entity = new Entity();

		TextureComponent texComp = new TextureComponent();
		Texture tex = MiscArt.mainBackgroundScrollable;
		float texToCorner = (float)Math.sqrt((tex.getWidth() * tex.getWidth()) + (tex.getHeight() * tex.getHeight()));
		texComp.region = new TextureRegion(tex, 0, 0, (int)texToCorner, (int)texToCorner);
		tex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		texComp.size.x = texToCorner * RenderingSystem.PIXELS_TO_WORLD;
		texComp.size.y = texToCorner * RenderingSystem.PIXELS_TO_WORLD;
		entity.add(texComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		transComp.position.z = -100.0f;
		entity.add(transComp);

		entity.add(new MovementComponent());
		entity.add(new ScrollComponent());
		entity.add(new TweenComponent());

		return entity;
	}

	public static void addAstronautToTitle(Entity e, TransformComponent transform){
		ComponentMappers.transform.get(e).parent.parent = transform;
	}

	public static void addTitleTween(Entity entity, float target, float time) {
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = ComponentMappers.transform.get(entity).position.y;
		tweenSpec.end = target;
		tweenSpec.period = time;
		tweenSpec.cycle = TweenSpec.Cycle.ONCE;
		tweenSpec.interp = Interpolation.linear;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				TransformComponent tc = ComponentMappers.transform.get(e);
				tc.position.y = a;
			}
		};
		ComponentMappers.tween.get(entity).tweenSpecs.clear();
		ComponentMappers.tween.get(entity).tweenSpecs.add(tweenSpec);
	}
}
