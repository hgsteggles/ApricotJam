package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.components.ClickComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.input.InputManager;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

public class ClickSystem extends IteratingSystem {
	public ClickSystem() {
		super(Family.all(ClickComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ClickComponent bc = ComponentMappers.click.get(entity);

		if (!bc.active) {
			return;
		}

		bc.pointerOver = false;
		bc.clickLast = false;

		Vector2 pos = new Vector2(InputManager.screenInput.getPointerLocation());
		if (isInside(bc, pos.x, pos.y)) {
			if (InputManager.screenInput.isPointerDown()) {
				bc.pointerOver = true;
			} else if (InputManager.screenInput.isPointerUpLast()) {
				bc.clickLast = true;
			}
		}
	}

	private boolean isInside(ClickComponent bc, float x, float y) {
		if (bc.shape == null) {
			return true;
		} else {
			return bc.shape.contains(x, y);
		}
	}
}
