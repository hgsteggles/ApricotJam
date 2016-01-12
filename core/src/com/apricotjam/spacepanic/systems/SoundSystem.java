package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.art.Audio;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.SoundComponent;
import com.apricotjam.spacepanic.components.TweenComponent;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class SoundSystem extends IteratingSystem implements EntityListener {
	
	public SoundSystem() {
		super(Family.all(SoundComponent.class, TweenComponent.class).get());
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(Family.all(SoundComponent.class).get(), this);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TweenComponent tweenComp = ComponentMappers.tween.get(entity);
		if (tweenComp.tweenSpecs.size == 0)
			getEngine().removeEntity(entity);
	}

	@Override
	public void entityAdded(Entity entity) {
	}

	@Override
	public void entityRemoved(Entity entity) {
		SoundComponent soundComp = ComponentMappers.sound.get(entity);
		soundComp.sound.stop(soundComp.soundID);
	}
	
	
}