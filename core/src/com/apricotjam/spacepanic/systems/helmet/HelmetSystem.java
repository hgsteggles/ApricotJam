package com.apricotjam.spacepanic.systems.helmet;

import com.apricotjam.spacepanic.art.Audio;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.components.helmet.HelmetScreenComponent;
import com.apricotjam.spacepanic.components.helmet.LED_Component;
import com.apricotjam.spacepanic.components.helmet.ResourcePipeComponent;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

public class HelmetSystem extends EntitySystem {
	static private float RESOURCE_FILL_SPEED = 5.0f;
	private HelmetWorld world;
	
	private Entity masterEntity;
	private Entity breathingSound;
	private ImmutableArray<Entity> leds;
	private ImmutableArray<Entity> resourcePipes;
	
	public HelmetSystem(Entity masterEntity) {
		this.masterEntity = masterEntity;
		world = new HelmetWorld(masterEntity);
		breathingSound = createBreathingSound();
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		world.build(engine);
		engine.addEntity(breathingSound);
		leds = engine.getEntitiesFor(Family.all(LED_Component.class).get());
		resourcePipes = engine.getEntitiesFor(Family.all(ResourcePipeComponent.class).get());

		for (Entity e : resourcePipes) {
			HelmetScreenComponent helmetScreenComp = ComponentMappers.helmetscreen.get(masterEntity);
			ResourcePipeComponent resourcePipeComp = ComponentMappers.resourcepipe.get(e);
			resourcePipeComp.currCount = helmetScreenComp.resourceCount.get(resourcePipeComp.resource);
			updateResourcePipe(e);
		}
	}

	@Override
	public void update(float deltaTime) {
		HelmetScreenComponent helmetScreenComp = ComponentMappers.helmetscreen.get(masterEntity);
		
		if (helmetScreenComp.messages.size != 0) {
			if (leds.size() == 0) {
				LED_Message message = helmetScreenComp.messages.removeFirst();
				getEngine().addEntity(world.createLED(message.text, message.color, message.time, message.scroll, message.flash));
			}
		}
		
		for (Entity entity : leds) {
			if (ComponentMappers.tween.get(entity).tweenSpecs.size == 0) {
				getEngine().removeEntity(entity);
			}
		}
		
		for (Entity entity : resourcePipes) {
			ResourcePipeComponent resourcePipeComp = ComponentMappers.resourcepipe.get(entity);
			float targetCount = helmetScreenComp.resourceCount.get(resourcePipeComp.resource);
			if (resourcePipeComp.currCount > targetCount) {
				resourcePipeComp.currCount =  Math.max(resourcePipeComp.currCount - RESOURCE_FILL_SPEED*deltaTime, targetCount);
			} else if (resourcePipeComp.currCount < targetCount) {
				resourcePipeComp.currCount =  Math.min(resourcePipeComp.currCount + RESOURCE_FILL_SPEED*deltaTime, targetCount);
			}
			resourcePipeComp.currCount = targetCount;
			updateResourcePipe(entity);
		}

		
		ShaderSpreadComponent shaderSpreadComp = ComponentMappers.shaderspread.get(world.getDemisterFog());
		shaderSpreadComp.spread = helmetScreenComp.demisterSpread;
	}

	private void updateResourcePipe(Entity e) {
		ResourcePipeComponent resourcePipeComp = ComponentMappers.resourcepipe.get(e);
		HelmetScreenComponent helmetScreenComp = ComponentMappers.helmetscreen.get(masterEntity);
		float frac = resourcePipeComp.currCount / helmetScreenComp.maxCount.get(resourcePipeComp.resource);
		frac = MathUtils.clamp(frac, 0.0f, 1.0f);
		TextureComponent texComp = ComponentMappers.texture.get(e);
		texComp.size.x = frac * resourcePipeComp.maxSize;
	}

	public Entity createBreathingSound() {
		Entity entity = new Entity();

		SoundComponent soundComp = new SoundComponent();
		soundComp.sound = Audio.sounds.get("breathing");
		soundComp.volume = 0.1f;
		soundComp.pan = -0.3f;
		soundComp.duration = -1.0f;
		soundComp.loop = true;
		entity.add(soundComp);

		return entity;
	}
	
	public static class LED_Message {
		public String text;
		public Color color;
		public float time = 2.0f;
		public boolean flash = false;
		public boolean scroll = false;

		public LED_Message(String text, Color color, float time, boolean scroll, boolean flash) {
			this.text = text;
			this.color = color;
			this.time = time;
			this.flash = flash;
			this.scroll = scroll;
		}
	}

	public void killBreathing() {
		ComponentMappers.sound.get(breathingSound).sound.stop(ComponentMappers.sound.get(breathingSound).soundID);
	}
}
