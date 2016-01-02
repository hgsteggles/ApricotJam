package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.art.ComputerArt;
import com.apricotjam.spacepanic.art.HelmetUI;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.MapPartComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.puzzle.MazeGenerator;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;

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
	private static final int PATCHES_X = 3;
	private static final int PATCHES_Y = 3;

	float width;
	float height;

	Entity screen;
	Entity mapCentre;

	MazeGenerator mazeGenerator;
	Patch[][] patches;

	public MapSystem(float width, float height) {
		this.width = width;
		this.height = height;
		screen = createScreen();
		mapCentre = createMapCentre();

		mazeGenerator = new MazeGenerator(PATCH_WIDTH, PATCH_HEIGHT);
		patches = new Patch[PATCHES_X][PATCHES_Y];

		for (int ipatch = 0; ipatch < PATCHES_X; ipatch++) {
			for (int jpatch = 0; jpatch < PATCHES_Y; jpatch++) {
				patches[ipatch][jpatch] = createPatch(ipatch - 1, jpatch - 1);
			}
		}
	}

	@Override
	public void addedToEngine(Engine engine) {
		engine.addEntity(screen);
		for (int ipatch = 0; ipatch < PATCHES_X; ipatch++) {
			for (int jpatch = 0; jpatch < PATCHES_Y; jpatch++) {
				for (Entity ast : patches[ipatch][jpatch].asteroids) {
					engine.addEntity(ast);
				}
			}
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
		tranc.parent = ComponentMappers.transform.get(mapCentre);
		asteroid.add(tranc);

		return asteroid;
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
