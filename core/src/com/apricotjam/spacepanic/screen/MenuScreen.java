package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.input.InputData;
import com.apricotjam.spacepanic.input.ScreenInput;

public class MenuScreen extends BasicScreen {

    private float titlePosition = CAMERA_HEIGHT / 2.0f;
    private static final float titleSpeed = 3.5f;
    private static final float titleEndPosition = CAMERA_HEIGHT * 3.0f / 4.0f;

    public MenuScreen(SpacePanic spacePanic, ScreenInput input) {
        super(spacePanic, input);
    }

    @Override
    public void update(float delta, InputData inputData) {
        if (titlePosition < titleEndPosition) {
            titlePosition += delta * titleSpeed;
        }
    }

    @Override
    public void render() {
        spriteBatch.begin();
        draw(MiscArt.title, CAMERA_WIDTH / 2.0f, titlePosition, 6.5f, 1.5f, true);
        spriteBatch.end();
    }

    @Override
    public void backPressed() {
        spacePanic.setScreen(new TitleScreen(spacePanic, input));
    }
}
