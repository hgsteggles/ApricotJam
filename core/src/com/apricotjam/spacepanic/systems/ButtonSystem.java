package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.components.ButtonComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.input.InputManager;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

public class ButtonSystem extends IteratingSystem {
    public ButtonSystem() {
        super(Family.all(ButtonComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ButtonComponent bc = ComponentMappers.button.get(entity);

        if (!bc.active) {
            return;
        }

        bc.pointerOver = false;
        bc.clickLast = false;

        Vector2 pos = new Vector2(InputManager.screenInput.getPointerLocation());
        boolean inside = false;
        if (ComponentMappers.transform.has(entity)) {
            TransformComponent tc = ComponentMappers.transform.get(entity);
            if (isInside(tc, pos.x, pos.y)) {
                inside = true;
            }
        } else {
            inside = true;
        }
        if (inside) {
            if (InputManager.screenInput.isPointerDown()) {
                bc.pointerOver = true;
            } else if (InputManager.screenInput.isPointerUpLast()) {
                bc.clickLast = true;
            }
        }
    }

    private boolean isInside(TransformComponent tc, float x, float y) {
        if (x > tc.position.x
                && x < tc.position.x + tc.scale.x
                && y > tc.position.y
                && y < tc.position.y + tc.scale.y) {
            return true;
        } else {
            return false;
        }
    }
}
