package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.components.*;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

public class LineSystem extends IteratingSystem {
	public LineSystem() {
		super(Family.all(LineComponent.class).one(TextureComponent.class, TransformComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		updateLine(entity);
	}

	public static void updateLine(Entity line) {
		LineComponent lineComponent = ComponentMappers.line.get(line);
		if (upToDate(lineComponent)) {
			return;
		}
		Vector2 lineVec = lineComponent.end.cpy().sub(lineComponent.start);
		if (ComponentMappers.transform.has(line)) {
			TransformComponent transc = ComponentMappers.transform.get(line);
			Vector2 centre = lineComponent.end.cpy().lerp(lineComponent.start, 0.5f);
			transc.position.x = centre.x;
			transc.position.y = centre.y;
			transc.rotation = lineVec.angle();
		}
		if (ComponentMappers.texture.has(line)) {
			TextureComponent texc = ComponentMappers.texture.get(line);
			float lineLength = lineVec.len();
			texc.size.x = lineLength;
			float regionWidth = lineLength * texc.region.getTexture().getHeight() / (texc.size.y * texc.region.getTexture().getWidth());
			texc.region.setU2(regionWidth);
		}
		lineComponent.startCached.x = lineComponent.start.x;
		lineComponent.startCached.y = lineComponent.start.y;
		lineComponent.endCached.x = lineComponent.end.x;
		lineComponent.endCached.y = lineComponent.end.y;
	}

	protected static boolean upToDate(LineComponent lineComponent) {
		return (lineComponent.start.x == lineComponent.startCached.x &&
				lineComponent.start.y == lineComponent.startCached.y &&
				lineComponent.end.x == lineComponent.endCached.x &&
				lineComponent.end.y == lineComponent.endCached.y);
	}
}
