package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;

public class BufferScreen extends BasicScreen {
	//First frame on android was a few seconds of deltatime for some reason
	//This screen eats up the first frame so that SplashScreen works correctly
	public BufferScreen(SpacePanic spacePanic) {
		super(spacePanic);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		spacePanic.setScreen(new SplashScreen(spacePanic));
	}

	@Override
	public void backPressed() {

	}
}
