package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.MovementComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class MovementSystem extends IteratingSystem {
	public MovementSystem() {
		super(Family.all(MovementComponent.class, TransformComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TransformComponent tc = ComponentMappers.transform.get(entity);
		MovementComponent mc = ComponentMappers.movment.get(entity);

		tc.position.x += mc.linearVelocity.x * deltaTime;
		tc.position.y += mc.linearVelocity.y * deltaTime;
		tc.position.z += mc.linearVelocity.z * deltaTime;

		tc.rotation += mc.rotationalVelocity * deltaTime;
	}
}
