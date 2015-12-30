package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.art.ComputerArt;
import com.apricotjam.spacepanic.components.MapPartComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;

public class MapSystem extends EntitySystem {

	Entity screen;
	float width;
	float height;

	public MapSystem(float width, float height) {
		this.width = width;
		this.height = height;
		screen = createScreen();
	}

	@Override
	public void addedToEngine(Engine engine) {
		engine.addEntity(screen);
	}

	private Entity createScreen() {
		Entity screen = new Entity();

		MapPartComponent mpc = new MapPartComponent();
		screen.add(mpc);

		TextureComponent texc = new TextureComponent();
		texc.region = ComputerArt.computer;
		texc.size.x = width;
		texc.size.y = height;
		screen.add(texc);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		tranc.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		tranc.position.z = 0.0f;
		screen.add(tranc);

		return screen;
	}

}
