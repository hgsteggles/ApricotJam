package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.TweenComponent;
import com.apricotjam.spacepanic.components.TweenSpec;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;

public class TweenSystem extends IteratingSystem {

	public TweenSystem() {
		super(Family.all(TweenComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TweenComponent twc = ComponentMappers.tween.get(entity);
		Array<TweenSpec> finished = new Array<TweenSpec>();
		for (TweenSpec tws: twc.tweenSpecs) {
			tws.time += deltaTime / tws.period;
			tws.tweenInterface.applyTween(entity, tws.interp.apply(tws.start, tws.end, tws.time));
			if (!tws.reverse && tws.time > tws.period || tws.reverse && tws.time < 0.0f) {
				switch (tws.cycle) {
					case ONCE:
						finished.add(tws);
						break;
					case LOOP:
						tws.time -= tws.period;
						break;
					case REVERSE:
						tws.period *= -1.0;
						tws.reverse = !tws.reverse;
						break;
				}
			}
		}
		twc.tweenSpecs.removeAll(finished, false);
		if (twc.tweenSpecs.size == 0) {
			entity.remove(TweenComponent.class);
		}

	}
}
