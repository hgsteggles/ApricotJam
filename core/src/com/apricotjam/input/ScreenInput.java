package com.apricotjam.input;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input;

public class ScreenInput implements InputProcessor {
    protected InputData inputData = new InputData();

    public InputData getInputData() {
        return inputData;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE){
            inputData.backPressed();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        inputData.pointerDown(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        inputData.pointerUp(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        inputData.pointerMoved(screenX, screenY, true);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        inputData.pointerMoved(screenX, screenY, false);
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
