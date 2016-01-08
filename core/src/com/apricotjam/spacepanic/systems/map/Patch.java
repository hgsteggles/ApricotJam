package com.apricotjam.spacepanic.systems.map;

import com.apricotjam.spacepanic.gameelements.Resource;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Patch {
	public static final int PATCH_WIDTH = 10;
	public static final int PATCH_HEIGHT = 10;

	public final int x;
	public final int y;

	public int[][] maze;
	public ArrayList<Entity> asteroids;
	public HashMap<Point, Entity> resources;

	public Patch(int x, int y, MazeGenerator mazeGenerator, ResourceGenerator resourceGenerator, MapSystem mapSystem) {
		this.x = x;
		this.y = y;

		maze = mazeGenerator.createPatch(x, y);
		Resource[][] resourceArray = resourceGenerator.generateResources(x, y, maze);

		asteroids = new ArrayList<Entity>();
		resources = new HashMap<Point, Entity>();
		for (int icell = 0; icell < PATCH_WIDTH; icell++) {
			for (int jcell = 0; jcell < PATCH_HEIGHT; jcell++) {
				if (maze[icell][jcell] == MazeGenerator.WALL) {
					float xast = (icell - (PATCH_WIDTH  / 2.0f) + (x * PATCH_WIDTH)) * MapSystem.ASTEROID_SIZE;
					float yast = (jcell - (PATCH_HEIGHT / 2.0f) + (y * PATCH_HEIGHT)) * MapSystem.ASTEROID_SIZE;
					asteroids.add(mapSystem.createAsteroid(xast, yast));
				}

				if (resourceArray[icell][jcell] != Resource.NONE) {
					Point pos = new Point(x * PATCH_WIDTH + icell - (PATCH_WIDTH / 2), y * PATCH_HEIGHT - (PATCH_HEIGHT / 2) + jcell);
					if (!mapSystem.isResourceUsed(pos)) {
						float xicon = pos.x * MapSystem.ASTEROID_SIZE;
						float yicon = pos.y * MapSystem.ASTEROID_SIZE;
						resources.put(new Point(icell, jcell), mapSystem.createResourceIcon(xicon, yicon, resourceArray[icell][jcell]));
					}
				}
			}
		}
	}

	public void addToEngine(Engine engine) {
		for (Entity ast : asteroids) {
			engine.addEntity(ast);
		}
		for (Entity resource : resources.values()) {
			engine.addEntity(resource);
		}
	}

	public void removeFromEngine(Engine engine) {
		for (Entity ast : asteroids) {
			engine.removeEntity(ast);
		}
		for (Entity resource : resources.values()) {
			engine.removeEntity(resource);
		}
	}
}
