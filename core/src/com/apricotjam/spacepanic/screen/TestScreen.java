package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.input.InputData;
import com.apricotjam.spacepanic.input.ScreenInput;
import com.apricotjam.spacepanic.SpacePanic;
import com.badlogic.gdx.math.Vector2;

public class TestScreen extends BasicScreen {

    float xImage;
    float yImage;

    public TestScreen(SpacePanic spacePanic, ScreenInput input) {
        super(spacePanic, input);
        xImage = CAMERA_WIDTH / 2.0f;
        yImage = CAMERA_HEIGHT / 2.0f;
    }

    @Override
    public void update(float delta, InputData inputData) {
        if (inputData.isPointerDownLast()) {
            System.out.println("location:" + inputData.getPointerLocation().toString());
            xImage = inputData.getPointerLocation().x;
            yImage = inputData.getPointerLocation().y;
        }
    }

    @Override
    public void backPressed() {
        System.out.println("I pressed back!");
    }

    @Override
    public void render() {
        spriteBatch.begin();
        draw(MiscArt.testImage, xImage, yImage, 1.0f, 1.0f, true);
        spriteBatch.end();
    }
}
