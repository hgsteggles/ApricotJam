package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.components.AnimatedShaderComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class AnimatedShaderSystem  extends IteratingSystem {
	public AnimatedShaderSystem() {
		super(Family.all(AnimatedShaderComponent.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		AnimatedShaderComponent animShaderComp = ComponentMappers.animatedshader.get(entity);
		
		animShaderComp.time += deltaTime;
	}
}