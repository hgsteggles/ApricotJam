package com.apricotjam.spacepanic.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ScreenInput implements InputProcessor {
    protected InputData inputData = new InputData();
    protected Camera camera;

    public InputData getInputData() {
        return inputData;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
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
        inputData.pointerDown(unprojectPointer(screenX, screenY));
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        inputData.pointerUp(unprojectPointer(screenX, screenY));
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        inputData.pointerMoved(unprojectPointer(screenX, screenY), true);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        inputData.pointerMoved(unprojectPointer(screenX, screenY), false);
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    //Converts pixel coordinates to world coordinates
    protected Vector2 unprojectPointer(Vector2 screen) {
        Vector3 pointer = new Vector3(screen.x, screen.y, 0);
        camera.unproject(pointer);
        return new Vector2(pointer.x, pointer.y);
    }

    //Converts pixel coordinates to world coordinates
    private Vector2 unprojectPointer(int screenX, int screenY) {
        Vector3 pointer = new Vector3(screenX, screenY, 0);
        camera.unproject(pointer);
        return new Vector2(pointer.x, pointer.y);
    }
}
