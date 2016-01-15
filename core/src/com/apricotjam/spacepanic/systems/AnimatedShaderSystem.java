package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.ShaderTimeComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class AnimatedShaderSystem extends IteratingSystem {
	public AnimatedShaderSystem() {
		super(Family.all(ShaderTimeComponent.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		ShaderTimeComponent animShaderComp = ComponentMappers.shadertime.get(entity);

		animShaderComp.time += deltaTime;
	}
}