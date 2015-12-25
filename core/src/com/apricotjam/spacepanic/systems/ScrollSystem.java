package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.ScrollComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;

public class ScrollSystem extends IteratingSystem {
	public ScrollSystem() {
		super(Family.all(ScrollComponent.class, TextureComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ScrollComponent sc = ComponentMappers.scroll.get(entity);
		TextureComponent tc = ComponentMappers.texture.get(entity);

		float dx = deltaTime * sc.speed.x * RenderingSystem.WORLD_TO_PIXELS;
		float dy = deltaTime * sc.speed.y * RenderingSystem.WORLD_TO_PIXELS;

		if (ComponentMappers.transform.has(entity)) {
			float rotation = ComponentMappers.transform.get(entity).rotation;
			if (rotation != 0) {
				float dx_rot = dx * MathUtils.cosDeg(rotation) + dy * MathUtils.sinDeg(rotation);
				float dy_rot = dy * MathUtils.cosDeg(rotation) - dx * MathUtils.sinDeg(rotation);
				dx = dx_rot;
				dy = dy_rot;
			}
		}

		//x has to be negative to get scroll correct
		tc.region.scroll(-dx / tc.region.getRegionWidth(), dy / tc.region.getRegionHeight());
	}
}
