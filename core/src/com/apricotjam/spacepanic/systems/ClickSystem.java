package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.components.*;
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
		boolean inside = false;
		if (ComponentMappers.transform.has(entity)) {
			TransformComponent tc = ComponentMappers.transform.get(entity);
			inside = isInside(bc, pos.x - tc.position.x, pos.y - tc.position.y);
		} else {
			inside = isInside(bc, pos.x, pos.y);
		}
		if (inside) {
			if (InputManager.screenInput.isPointerDown()) {
				bc.pointerOver = true;
			} else if (InputManager.screenInput.isPointerUpLast()) {
				bc.clickLast = true;
				bc.clicker.onClick();
			}
		}

		if (ComponentMappers.textbutton.has(entity)) {
			TextButtonComponent tc = ComponentMappers.textbutton.get(entity);
			BitmapFontComponent bmc = ComponentMappers.bitmapfont.get(entity);
			if (InputManager.screenInput.isPointerDown() && inside) {
				bmc.color = tc.pressed;
			} else {
				bmc.color = tc.base;
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
