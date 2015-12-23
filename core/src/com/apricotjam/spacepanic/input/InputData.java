package com.apricotjam.spacepanic.input;

import com.badlogic.gdx.math.Vector2;

public class InputData implements InputInterface {

	private Vector2 pointerLocation = new Vector2(); //Current pointer location
	private Vector2 pointerDownLocation = new Vector2(); //Pointer location when last touched down
	private Vector2 pointerDrag = new Vector2(); //Distance pointer has been dragged
	private boolean pointerDown = false; //True if pointer is down
	private boolean pointerDownLast = false; //True if point was moved down last frame
	private boolean pointerUpLast = false; //True if pointer was moved up last frame
	private boolean backPressedLast = false;

	public void backPressed() {
		backPressedLast = true;
	}

	public void pointerDown(Vector2 pos) {
		pointerDown(pos.x, pos.y);
	}

	public void pointerDown(float x, float y) {
		pointerLocation.set(x, y);
		pointerDownLocation.set(x, y);
		pointerDown = true;
		pointerDownLast = true;
	}

	public void pointerUp(Vector2 pos) {
		pointerUp(pos.x, pos.y);
	}

	public void pointerUp(float x, float y) {
		pointerLocation.set(x, y);
		pointerDrag.set(0, 0);
		pointerDown = false;
		pointerUpLast = true;
	}

	public void pointerMoved(Vector2 pos, boolean down) {
		pointerMoved(pos.x, pos.y, down);
	}

	public void pointerMoved(float x, float y, boolean down) {
		pointerLocation.set(x, y);
		if (down) {
			pointerDrag = pointerLocation.cpy().sub(pointerDownLocation);
		}
	}

	public void reset() {
		pointerDownLast = false;
		pointerUpLast = false;
		backPressedLast = false;
	}

	@Override
	public Vector2 getPointerLocation() {
		return pointerLocation;
	}

	@Override
	public Vector2 getPointerDownLocation() {
		return pointerDownLocation;
	}

	@Override
	public Vector2 getPointerDrag() {
		return pointerDrag;
	}

	@Override
	public boolean isPointerDown() {
		return pointerDown;
	}

	@Override
	public boolean isPointerDownLast() {
		return pointerDownLast;
	}

	@Override
	public boolean isPointerUpLast() {
		return pointerUpLast;
	}

	@Override
	public boolean isBackPressedLast() {
		return backPressedLast;
	}

}

