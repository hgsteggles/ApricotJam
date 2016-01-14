package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;

public class AboutScreen extends BasicScreen {
	public AboutScreen(SpacePanic spacePanic) {
		super(spacePanic);
	}

	@Override
	public void backPressed() {
		spacePanic.setScreen(new MenuScreen(spacePanic));
	}
}
