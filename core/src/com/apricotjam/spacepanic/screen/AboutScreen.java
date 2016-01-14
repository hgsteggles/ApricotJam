package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.badlogic.ashley.core.Entity;

public class AboutScreen extends BasicScreen {

	Entity title;
	Entity madeForText;
	Entity sourceCodeButton;

	public AboutScreen(SpacePanic spacePanic) {
		super(spacePanic);
	}

	@Override
	public void backPressed() {
		spacePanic.setScreen(new MenuScreen(spacePanic));
	}
}
