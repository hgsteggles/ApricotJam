package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.TickerComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class TickerSystem extends IteratingSystem {
	public TickerSystem() {
		super(Family.all(TickerComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TickerComponent tc = ComponentMappers.ticker.get(entity);

		if (tc.tickerActive) {
			tc.tickTimeLeft -= deltaTime;

			while (tc.tickTimeLeft <= 0) {
				tc.tickTimeLeft += tc.interval;

				if (tc.ticker != null) {
					tc.ticker.dispatchEvent(entity);
				}
			}
		}

		if (tc.finishActive) {
			tc.totalTimeLeft -= deltaTime;

			if (tc.totalTimeLeft <= 0) {
				tc.totalTimeLeft = 0;
				tc.tickerActive = false;
				tc.finishActive = false;

				if (tc.finish != null) {
					tc.finish.dispatchEvent(entity);
				}
			}
		}
	}
}
