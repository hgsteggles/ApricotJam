package com.apricotjam.spacepanic.systems.helmet;

import com.apricotjam.spacepanic.components.AnimationComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.helmet.HelmetScreenComponent;
import com.apricotjam.spacepanic.components.helmet.LED_Component;
import com.apricotjam.spacepanic.components.helmet.ResourcePipeComponent;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Animation;

public class HelmetSystem extends EntitySystem {
	static private float RESOURCE_FILL_SPEED = 5.0f;
	private HelmetWorld world = new HelmetWorld();
	
	private Entity masterEntity;
	private ImmutableArray<Entity> leds;
	private ImmutableArray<Entity> resourcePipes;
	
	public HelmetSystem(Entity masterEntity) {
		this.masterEntity = masterEntity;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		world.build(engine);
		leds = engine.getEntitiesFor(Family.all(LED_Component.class).get());
		resourcePipes = engine.getEntitiesFor(Family.all(ResourcePipeComponent.class).get());
	}

	@Override
	public void update(float deltaTime) {
		for (Entity entity : leds) {
			if (!ComponentMappers.tween.has(entity))
				getEngine().removeEntity(entity);
		}
		
		HelmetScreenComponent helmetScreenComp = ComponentMappers.helmetscreen.get(masterEntity);
		
		for (Entity entity : resourcePipes) {
			TextureComponent texComp = ComponentMappers.texture.get(entity);
			AnimationComponent animComp = ComponentMappers.animation.get(entity);
			ResourcePipeComponent resourcePipeComp = ComponentMappers.resourcepipe.get(entity);
			
			Animation animation = animComp.animations.get(0);
			
			if (animation != null) {
				float targetCount = helmetScreenComp.resourceCount.get(resourcePipeComp.resource);
				if (resourcePipeComp.currCount > targetCount) {
					resourcePipeComp.currCount =  Math.max(resourcePipeComp.currCount - RESOURCE_FILL_SPEED*deltaTime, world.resourceTotal.get(resourcePipeComp.resource));
				}
				else if (resourcePipeComp.currCount < targetCount) {
					resourcePipeComp.currCount =  Math.min(resourcePipeComp.currCount + RESOURCE_FILL_SPEED*deltaTime, world.resourceTotal.get(resourcePipeComp.resource));
				}
				
				texComp.region = animation.getKeyFrame(Math.max(resourcePipeComp.currCount - resourcePipeComp.minCount, 0));
			}
		}
	}
}
