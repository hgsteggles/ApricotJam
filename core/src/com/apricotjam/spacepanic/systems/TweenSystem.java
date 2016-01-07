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
			if (tws.time > 1) {
				tws.tweenInterface.endTween(entity);
				switch (tws.cycle) {
					case ONCE:
						tws.time = Math.min(tws.time, 1);
						finished.add(tws);
						break;
					case LOOP:
						tws.time -= 1;
						break;
					case REVERSE:
						tws.time -= 1;
						float temp = tws.start;
						tws.start = tws.end;
						tws.end = temp;
						break;
				}
			}
			tws.tweenInterface.applyTween(entity, tws.interp.apply(tws.start, tws.end, tws.time));
		}
		twc.tweenSpecs.removeAll(finished, false);
	}
}
