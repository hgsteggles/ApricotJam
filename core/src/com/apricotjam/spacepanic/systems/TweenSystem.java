package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.components.TweenComponent;
import com.apricotjam.spacepanic.components.TweenSpec;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class TweenSystem extends IteratingSystem {

	public TweenSystem() {
		super(Family.all(TweenComponent.class, TransformComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TweenComponent twc = ComponentMappers.tween.get(entity);
		for (TweenSpec tws: twc.tweenSpecs) {
			tws.x += deltaTime / tws.period;
			float newValue = tws.interp.apply(tws.start, tws.end, tws.x);
			TransformComponent trc = ComponentMappers.transform.get(entity);
			switch (tws.target) {
				case POSX:
					trc.position.x = newValue;
					break;
				case POSY:
					trc.position.y = newValue;
					break;
				case POSZ:
					trc.position.z = newValue;
					break;
				case SCALEX:
					trc.scale.x = newValue;
					break;
				case SCALEY:
					trc.scale.y = newValue;
					break;
				case ROTATION:
					trc.rotation = newValue;
					break;
			}
		}
	}
}
