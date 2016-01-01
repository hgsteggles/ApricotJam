package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.systems.*;

public class MapTestScreen extends BasicScreen {
	public MapTestScreen(SpacePanic spacePanic) {
		super(spacePanic);
		add(new RenderingSystem(spriteBatch, worldCamera));
		add(new MovementSystem());
		add(new MapSystem(8.25f, 4.75f));
	}

	@Override
	public void backPressed() {

	}
}
