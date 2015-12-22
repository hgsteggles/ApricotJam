package com.apricotjam.spacepanic.ui;

import com.apricotjam.spacepanic.input.InputData;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class ButtonManager {

    protected ArrayList<Button> buttons = new ArrayList<Button>();

    public ButtonManager() {}

    public void add(Button button) {
        buttons.add(button);
    }

    public void render(BasicScreen screen) {
        for (Button b : buttons) {
            if(b.isActive()) {
                b.render(screen);
            }
        }
    }

    public void update(InputData input) {
        this.reset();
        Vector2 pos = new Vector2(input.getPointerLocation());
        if (input.isPointerDown() || input.isPointerUpLast()) {
            for (Button b : buttons) {
                if (b.isActive() && b.isInside(pos.x, pos.y)) {
                    if (input.isPointerDown()) {
                        b.setPointerOver(true);
                    } else if (input.isPointerUpLast()) {
                        b.onClick();
                        b.setClickLast(true);
                    }
                    break; // Assume we can't click on two buttons at once
                }
            }
        }
    }

    private void reset() {
        for (Button b : buttons) {
            b.setPointerOver(false);
            b.setClickLast(false);
        }
    }

    public Button getButton(int n){
        return buttons.get(n);
    }

}
