package com.apricotjam.screen;

import com.apricotjam.art.MiscArt;
import com.apricotjam.input.InputData;
import com.apricotjam.input.ScreenInput;
import com.apricotjam.spacepanic.SpacePanic;
import com.badlogic.gdx.math.Vector2;

public class TitleScreen extends BasicScreen {

    float xImage;
    float yImage;

    public TitleScreen(SpacePanic spacePanic, ScreenInput input) {
        super(spacePanic, null, input);
        xImage = CAMERA_WIDTH / 2.0f;
        yImage = CAMERA_HEIGHT / 2.0f;
    }

    @Override
    public void update(InputData inputData) {
        if (inputData.isPointerDownLast()) {
            System.out.println("Raw location:" + inputData.getPointerLocation().toString());
            Vector2 worldPos =  unprojectPointer(inputData.getPointerLocation());
            System.out.println("World location:" + worldPos.toString());
            xImage = worldPos.x;
            yImage = worldPos.y;
        }
    }

    @Override
    public void backPressed() {
        System.out.println("I pressed back!");
    }

    @Override
    public void render(float delta) {
        spriteBatch.begin();
        draw(MiscArt.testImage, xImage, yImage, 1.0f, 1.0f, true);
        spriteBatch.end();
    }
}
