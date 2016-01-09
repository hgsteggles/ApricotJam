package com.apricotjam.spacepanic.systems.helmet;

import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.helmet.LED_Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

public class HelmetSystem extends EntitySystem {
	private HelmetWorld world = new HelmetWorld();
	
	private Entity masterEntity;
	private ImmutableArray<Entity> leds;
	
	public HelmetSystem(Entity masterEntity) {
		this.masterEntity = masterEntity;
	}
	
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
