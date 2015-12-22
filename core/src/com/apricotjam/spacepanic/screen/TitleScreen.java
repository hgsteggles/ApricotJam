package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.input.InputData;
import com.apricotjam.spacepanic.input.ScreenInput;
import com.apricotjam.spacepanic.SpacePanic;
import com.badlogic.gdx.graphics.Color;

public class TitleScreen extends BasicScreen {

    private float flashTimer = 0.0f;
    private static final float FLASHPERIOD = 0.8f;


    public TitleScreen(SpacePanic spacePanic, ScreenInput input) {
        super(spacePanic, input);
    }

    @Override
    public void update(float delta, InputData inputData) {
        flashTimer += delta;
        if (flashTimer > FLASHPERIOD) {
            flashTimer = 0.0f;
        }
        if (inputData.isPointerDownLast()) {
            spacePanic.setScreen(new MenuScreen(spacePanic, input));
        }
    }

    @Override
    public void backPressed() {
    }

    @Override
    public void render() {
        spriteBatch.begin();
        draw(MiscArt.title, CAMERA_WIDTH / 2.0f, CAMERA_HEIGHT / 2.0f, 6.5f, 1.5f, true);
        if (flashTimer > FLASHPERIOD / 2.0f) {
            drawString("Click to begin!", CAMERA_WIDTH / 2.0f, CAMERA_HEIGHT / 4.0f, 0.3f, true, Color.WHITE);
        }
        spriteBatch.end();
    }

}
