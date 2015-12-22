package com.apricotjam.spacepanic.ui;

import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.gdx.math.Vector2;

public abstract class Button {

    protected Vector2 pos;
    protected Vector2 centrePos;
    protected float width;
    protected float height;

    private boolean active = true;

    protected boolean pointerOver = false; // True when pointer is held down over the button
    protected boolean clickLast = false; // True if button has been clicked in the last frame

    public abstract void onClick();

    public abstract void render(BasicScreen screen);

    public Button(float x, float y, float width, float height, boolean centre) {
        this.width = width;
        this.height = height;
        if (centre) {
            centrePos = new Vector2(x, y);
            pos = new Vector2(x - this.width / 2.0f, y - this.height / 2.0f);
        } else {
            centrePos = new Vector2(x + this.width / 2.0f, y + this.height / 2.0f);
            pos = new Vector2(x, y);
        }
    }

    public boolean isInside(float x, float y) {
        if (x > pos.x && x < pos.x + width && y > pos.y && y < pos.y + height) {
            return true;
        } else {
            return false;
        }
    }

    public void setPointerOver(boolean pointerOver) {
        this.pointerOver = pointerOver;
    }

    public void setClickLast(boolean clickLast) {
        this.clickLast = clickLast;
    }

    public Vector2 getPosition() {
        return pos;
    }

    public void setPosition(float x, float y) {
        pos.set(x, y);
    }

    public void setPosition(Vector2 newPos) {
        pos.set(newPos);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
