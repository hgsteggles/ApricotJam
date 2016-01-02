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
		for (TweenSpec tws : twc.tweenSpecs) {
			tws.time += deltaTime / tws.period;
			tws.tweenInterface.applyTween(entity, tws.interp.apply(tws.start, tws.end, tws.time));
			if (tws.time > tws.period) {
				switch (tws.cycle) {
					case ONCE:
						finished.add(tws);
						break;
					case LOOP:
						tws.time -= tws.period;
						break;
					case REVERSE:
						tws.time -= tws.period;
						float temp = tws.start;
						tws.start = tws.end;
						tws.end = temp;
						break;
				}
			}
		}
		twc.tweenSpecs.removeAll(finished, false);
	}
}
