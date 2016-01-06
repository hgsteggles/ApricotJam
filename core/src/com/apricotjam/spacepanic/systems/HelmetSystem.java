package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.generators.HelmetWorld;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;

public class HelmetSystem extends EntitySystem {
	private HelmetWorld world = new HelmetWorld();
	
	@Override
	public void addedToEngine(Engine engine) {
		world.build(engine);
	}

	@Override
	public void update(float deltaTime) {
		
	}
	
	static public Entity addMarquee(String text) {
		return HelmetWorld.createMarqueeText(text);
	}
}
