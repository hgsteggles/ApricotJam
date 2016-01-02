package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.art.ComputerArt;
import com.apricotjam.spacepanic.art.HelmetUI;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.MapPartComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.input.InputManager;
import com.apricotjam.spacepanic.puzzle.MazeGenerator;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Input;

import java.util.ArrayList;

public class MapSystem extends EntitySystem {

	private class Patch {
		int width;
		int height;

		int[][] maze;
		ArrayList<Entity> asteroids;

		public Patch(int width, int height, int[][] maze) {
			this.width = width;
			this.height = height;
			this.maze = maze;
			asteroids = new ArrayList<Entity>();
		}

		public void dispose(Engine engine) {
			for (Entity ast : asteroids) {
				engine.removeEntity(ast);
			}
		}
	}

	private static final float ASTEROID_WIDTH = 0.1f;
	private static final float ASTEROID_HEIGHT = 0.1f;
	private static final int PATCH_WIDTH = 10;
	private static final int PATCH_HEIGHT = 10;
	private static final int PATCHES_X = 5;
	private static final int PATCHES_Y = 5;
	private static final float PATHINESS = 0.5f; //How likely each cell is to be a path on the patch boundaries

	float width;
	float height;

	Entity screen;
	Entity mapCentre;
	TransformComponent mapCentreTrans;
	Entity playerIcon;

	MazeGenerator mazeGenerator;
	Patch[][] patches;

	public MapSystem(float width, float height) {
		this.width = width;
		this.height = height;
		screen = createScreen();
		mapCentre = createMapCentre();
		mapCentreTrans = ComponentMappers.transform.get(mapCentre);
		playerIcon = createPlayerIcon();

		mazeGenerator = new MazeGenerator(PATCH_WIDTH, PATCH_HEIGHT, PATHINESS);
		patches = new Patch[PATCHES_X][PATCHES_Y];

		for (int ipatch = 0; ipatch < PATCHES_X; ipatch++) {
			for (int jpatch = 0; jpatch < PATCHES_Y; jpatch++) {
				int xOffset = PATCHES_X / 2;
				int yOffset = PATCHES_Y / 2;
				patches[ipatch][jpatch] = createPatch(ipatch - xOffset, jpatch - yOffset);
			}
		}
	}

	@Override
	public void addedToEngine(Engine engine) {
		engine.addEntity(screen);
		engine.addEntity(mapCentre);
		engine.addEntity(playerIcon);
		for (int ipatch = 0; ipatch < PATCHES_X; ipatch++) {
			for (int jpatch = 0; jpatch < PATCHES_Y; jpatch++) {
				for (Entity ast : patches[ipatch][jpatch].asteroids) {
					engine.addEntity(ast);
				}
			}
		}
	}

	@Override
	public void update (float deltaTime) {
		if (InputManager.testInput.isTyped(Input.Keys.LEFT)) {
			System.out.println("LEFT");
			mapCentreTrans.position.x += ASTEROID_WIDTH;
		}
		if (InputManager.testInput.isTyped(Input.Keys.RIGHT)) {
			System.out.println("RIGHT");
			mapCentreTrans.position.x -= ASTEROID_WIDTH;
		}
		if (InputManager.testInput.isTyped(Input.Keys.UP)) {
			System.out.println("UP");
			mapCentreTrans.position.y -= ASTEROID_HEIGHT;
		}
		if (InputManager.testInput.isTyped(Input.Keys.DOWN)) {
			System.out.println("DOWN");
			mapCentreTrans.position.y += ASTEROID_HEIGHT;
		}
	}

	private Entity createScreen() {
		Entity screen = new Entity();

		MapPartComponent mpc = new MapPartComponent();
		screen.add(mpc);

		//TextureComponent texc = new TextureComponent();
		//texc.region = ComputerArt.computer;
		//texc.size.x = width;
		//texc.size.y = height;
		//screen.add(texc);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		tranc.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		tranc.position.z = 0.0f;
		screen.add(tranc);

		return screen;
	}

	private Entity createMapCentre() {
		Entity mapCentre = new Entity();

		MapPartComponent mpc = new MapPartComponent();
		mapCentre.add(mpc);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = 0.0f;
		tranc.position.y = 0.0f;
		tranc.position.z = 0.0f;
		tranc.parent = ComponentMappers.transform.get(screen);
		mapCentre.add(tranc);

		return mapCentre;
	}

	private Entity createAsteroid(float x, float y) {
		Entity asteroid = new Entity();

		MapPartComponent mpc = new MapPartComponent();
		asteroid.add(mpc);

		TextureComponent texc = new TextureComponent();
		texc.region = HelmetUI.speaker;
		texc.size.x = ASTEROID_WIDTH;
		texc.size.y = ASTEROID_HEIGHT;
		asteroid.add(texc);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = x;
		tranc.position.y = y;
		tranc.position.z = 0.0f;
		tranc.parent = mapCentreTrans;
		asteroid.add(tranc);

		return asteroid;
	}

	private Entity createPlayerIcon() {
		Entity playerIcon = new Entity();

		MapPartComponent mpc = new MapPartComponent();
		playerIcon.add(mpc);

		TextureComponent texc = new TextureComponent();
		texc.region = HelmetUI.screw;
		texc.size.x = ASTEROID_WIDTH * 0.8f;
		texc.size.y = ASTEROID_HEIGHT * 0.8f;
		playerIcon.add(texc);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = 0.0f;
		tranc.position.y = 0.0f;
		tranc.position.z = 0.0f;
		tranc.parent = ComponentMappers.transform.get(screen);
		playerIcon.add(tranc);

		return playerIcon;
	}

	private Patch createPatch(int xpatch, int ypatch) {
		Patch patch = new Patch(PATCH_WIDTH, PATCH_HEIGHT, mazeGenerator.createPatch(xpatch, ypatch));
		for (int icell = 0; icell < PATCH_WIDTH; icell++) {
			for (int jcell = 0; jcell < PATCH_HEIGHT; jcell++) {
				if (patch.maze[icell][jcell] == MazeGenerator.WALL) {
					float x = (icell - (PATCH_WIDTH  / 2.0f) + (xpatch * PATCH_WIDTH * 1.0f)) * ASTEROID_WIDTH;
					float y = (jcell - (PATCH_HEIGHT / 2.0f) + (ypatch * PATCH_HEIGHT * 1.0f)) * ASTEROID_HEIGHT;
					patch.asteroids.add(createAsteroid(x, y));
				}
			}
		}
		return patch;
	}

}
