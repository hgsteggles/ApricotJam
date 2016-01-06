package com.apricotjam.spacepanic.testscreen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.apricotjam.spacepanic.systems.*;
import com.apricotjam.spacepanic.systems.map.MapSystem;

public class MapTestScreen extends BasicScreen {
	public MapTestScreen(SpacePanic spacePanic) {
		super(spacePanic);
		add(new RenderingSystem(spriteBatch, worldCamera));
		add(new ClickSystem());
		add(new TweenSystem());
		add(new MapSystem(8.25f, 4.75f));
	}

	@Override
	public void backPressed() {

	}
}
