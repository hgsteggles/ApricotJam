package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.art.Audio;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.SoundComponent;
import com.apricotjam.spacepanic.components.TweenComponent;
import com.apricotjam.spacepanic.gameelements.GameSettings;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class SoundSystem extends IteratingSystem implements EntityListener {

	private boolean soundOn;

	public SoundSystem() {
		super(Family.all(SoundComponent.class).get());
		soundOn = GameSettings.isSoundOn();
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(Family.all(SoundComponent.class).get(), this);
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		soundOn = GameSettings.isSoundOn();
	}


	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SoundComponent sc = ComponentMappers.sound.get(entity);
		if (soundOn != GameSettings.isSoundOn()) {
			if (GameSettings.isSoundOn()) {
				sc.sound.setVolume(sc.soundID, sc.volume);
			} else {
				sc.sound.setVolume(sc.soundID, 0.0f);
			}
		}

		if (sc.duration >= 0) {
			sc.time += deltaTime;
			if (sc.time > sc.duration) {
				getEngine().removeEntity(entity);
			}
		}
	}

	@Override
	public void entityAdded(Entity entity) {
		SoundComponent sc = ComponentMappers.sound.get(entity);
		float volume = 0.0f;
		if (GameSettings.isSoundOn()) {
			volume = sc.volume;
		}
		sc.soundID = sc.sound.play(volume, sc.pitch, sc.pan);
	}

	@Override
	public void entityRemoved(Entity entity) {
		SoundComponent soundComp = ComponentMappers.sound.get(entity);
		soundComp.sound.stop(soundComp.soundID);
	}
	
	
}