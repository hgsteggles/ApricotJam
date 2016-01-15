package com.apricotjam.spacepanic.input;

import com.badlogic.gdx.InputProcessor;

import java.util.HashMap;

public class TestInput extends InputData implements InputProcessor {

	private HashMap<Integer, Boolean> pressedKeys = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> typedKeys = new HashMap<Integer, Boolean>();

	private void setPressed(int key, boolean state) {
		pressedKeys.put(key, state);
	}

	private void setTyped(int key, boolean state) {
		typedKeys.put(key, state);
	}

	public boolean isPressed(int key) {
		if (pressedKeys.containsKey(key)) {
			return pressedKeys.get(key);
		} else {
			return false;
		}
	}

	public boolean isTyped(int key) {
		if (typedKeys.containsKey(key)) {
			return typedKeys.get(key);
		} else {
			return false;
		}
	}

	public void resetTypedKeys() {
		for (int k : typedKeys.keySet()) {
			typedKeys.put(k, false);
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		setPressed(keycode, true);
		setTyped(keycode, true);
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		setPressed(keycode, false);
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public void reset() {
		super.reset();
		resetTypedKeys();
	}
}
