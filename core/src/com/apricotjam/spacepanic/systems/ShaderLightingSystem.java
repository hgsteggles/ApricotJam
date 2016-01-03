package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.ShaderLightingComponent;
import com.apricotjam.spacepanic.input.InputManager;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

public class ShaderLightingSystem extends IteratingSystem {
	public ShaderLightingSystem() {
		super(Family.all(ShaderLightingComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ShaderLightingComponent slc = ComponentMappers.shaderlight.get(entity);
		
		Vector2 pos = new Vector2(InputManager.screenInput.getPointerLocation());
		
		slc.lightPosition.x = (pos.x)/BasicScreen.WORLD_WIDTH;
		slc.lightPosition.y = (pos.y)/BasicScreen.WORLD_HEIGHT;
	}
}
