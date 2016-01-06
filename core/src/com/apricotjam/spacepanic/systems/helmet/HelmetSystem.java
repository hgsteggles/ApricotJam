package com.apricotjam.spacepanic.systems.helmet;

import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.LED_Component;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

public class HelmetSystem extends EntitySystem {
	private HelmetWorld world = new HelmetWorld();
	
	private ImmutableArray<Entity> leds;
	
	@Override
	public void addedToEngine(Engine engine) {
		world.build(engine);
		leds = engine.getEntitiesFor(Family.all(LED_Component.class).get());
	}

	@Override
	public void update(float deltaTime) {
		for (Entity entity : leds) {
			if (!ComponentMappers.tween.has(entity))
				getEngine().removeEntity(entity);
		}
	}
}
