package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.input.InputData;
import com.apricotjam.spacepanic.input.ScreenInput;
import com.apricotjam.spacepanic.ui.ButtonManager;
import com.apricotjam.spacepanic.ui.TextButton;

public class MenuScreen extends BasicScreen {

    private float titlePosition = CAMERA_HEIGHT / 2.0f;
    private static final float titleSpeed = 3.5f;
    private static final float titleEndPosition = CAMERA_HEIGHT * 3.0f / 4.0f;

    private ButtonManager buttonManager;

    public MenuScreen(SpacePanic spacePanic, ScreenInput input) {
        super(spacePanic, input);
        buttonManager = new ButtonManager();
        buttonManager.add(new TextButton(CAMERA_WIDTH / 2.0f, CAMERA_HEIGHT / 4.0f, "START", 0.3f, true) {
            @Override
            public void onClick() {
                startGame();
            }
        });
    }

    @Override
    public void update(float delta, InputData inputData) {
        if (titlePosition < titleEndPosition) {
            titlePosition += delta * titleSpeed;
        }
        buttonManager.update(inputData);
    }

    @Override
    public void render() {
        spriteBatch.begin();
        draw(MiscArt.title, CAMERA_WIDTH / 2.0f, titlePosition, 6.5f, 1.5f, true);
        buttonManager.render(this);
        spriteBatch.end();
    }

    @Override
    public void backPressed() {
        spacePanic.setScreen(new TitleScreen(spacePanic, input));
    }


    private void startGame() {
        System.out.println("So the game would start now...");
    }
}
