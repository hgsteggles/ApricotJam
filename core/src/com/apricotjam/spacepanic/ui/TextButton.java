package com.apricotjam.spacepanic.ui;

import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.gdx.graphics.Color;

public abstract class TextButton extends Button {

    protected final String text;
    protected final float scale;

    public TextButton(float x, float y, String text, float scale, boolean centre) {
        super(x, y, text.length() * scale, scale, centre);
        this.text = text;
        this.scale = scale;
    }

    @Override
    public void render(BasicScreen screen) {
        if (pointerOver) {
            screen.drawString(text, centrePos.x, centrePos.y, scale*1.2f, true, Color.RED);
        } else {
            screen.drawString(text, pos.x, pos.y, scale, false, Color.WHITE);
        }
    }

}
