package com.apricotjam.spacepanic.systems.maze;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;

public class Patch {
	public static final int PATCH_WIDTH = 10;
	public static final int PATCH_HEIGHT = 10;

	public final int x;
	public final int y;

	public final int width;
	public final int height;

	public int[][] maze;
	public ArrayList<Entity> asteroids;

	public Patch(int x, int y, MazeGenerator mazeGenerator, MapSystem mapSystem) {
		this.x = x;
		this.y = y;

		this.width = mazeGenerator.getPatchWidth();
		this.height = mazeGenerator.getPatchHeight();

		maze = mazeGenerator.createPatch(x, y);

		asteroids = new ArrayList<Entity>();
		for (int icell = 0; icell < PATCH_WIDTH; icell++) {
			for (int jcell = 0; jcell < PATCH_HEIGHT; jcell++) {
				if (maze[icell][jcell] == MazeGenerator.WALL) {
					float xast = (icell - (PATCH_WIDTH  / 2.0f) + (x * PATCH_WIDTH * 1.0f)) * MapSystem.ASTEROID_WIDTH;
					float yast = (jcell - (PATCH_HEIGHT / 2.0f) + (y * PATCH_HEIGHT * 1.0f)) * MapSystem.ASTEROID_HEIGHT;
					asteroids.add(mapSystem.createAsteroid(xast, yast));
				}
			}
		}
	}

	public void addToEngine(Engine engine) {
		for (Entity ast : asteroids) {
			engine.addEntity(ast);
		}
	}

	public void removeFromEngine(Engine engine) {
		for (Entity ast : asteroids) {
			engine.removeEntity(ast);
		}
	}
}
