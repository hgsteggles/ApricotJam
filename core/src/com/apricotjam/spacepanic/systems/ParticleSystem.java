package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.art.Particles;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.ParticleEffectComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class ParticleSystem extends IteratingSystem {

	public ParticleSystem() {
		super(Family.all(ParticleEffectComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ParticleEffectComponent particleComp = ComponentMappers.particle.get(entity);
		particleComp.effect.update(deltaTime);		
		
		if (ComponentMappers.transform.has(entity)) {
			TransformComponent totalTransform = ComponentMappers.transform.get(entity).getTotalTransform();
			particleComp.effect.setPosition(totalTransform.position.x, totalTransform.position.y);
			Particles.setRotation(particleComp.effect, totalTransform.rotation);
		}
		
		if (particleComp.effect.isComplete())
			getEngine().removeEntity(entity);
	}

}
