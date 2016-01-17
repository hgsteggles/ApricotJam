package com.apricotjam.spacepanic.misc;

import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;

public class EntityUtil {
	private EntityUtil() {}

	public static void addParent(Entity e, TransformComponent transform){
		ComponentMappers.transform.get(e).parent = transform;
	}

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
		astronaut.add(offset);

		return astronaut;
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
