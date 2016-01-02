package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.components.AnimationComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.StateComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;

public class AnimationSystem extends IteratingSystem {
	public AnimationSystem() {
		super(Family.all(TextureComponent.class, AnimationComponent.class, StateComponent.class).get());
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TextureComponent tex = ComponentMappers.texture.get(entity);
		AnimationComponent anim = ComponentMappers.animation.get(entity);
		StateComponent state = ComponentMappers.state.get(entity);
		
		Animation animation = anim.animations.get(state.get());
		
		if (animation != null) {
			tex.region = animation.getKeyFrame(state.time); 
		}
		
		state.time += state.timescale*deltaTime;
	}
}
