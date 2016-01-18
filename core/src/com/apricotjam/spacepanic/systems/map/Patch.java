package com.apricotjam.spacepanic.systems.map;

import java.util.ArrayList;
import java.util.HashMap;

import com.apricotjam.spacepanic.gameelements.Resource;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.GridPoint2;

public class Patch {
	public static final int PATCH_WIDTH = 10;
	public static final int PATCH_HEIGHT = 10;

	public final int x;
	public final int y;

	public int[][] maze;
	public ArrayList<Entity> asteroids;
	public HashMap<GridPoint2, Entity> resources;

	public Patch(int x, int y, MazeGenerator mazeGenerator, ResourceGenerator resourceGenerator, MapSystem mapSystem) {
		this.x = x;
		this.y = y;

		maze = mazeGenerator.createPatch(x, y);
		Resource[][] resourceArray = resourceGenerator.generateResources(x, y, maze);

		asteroids = new ArrayList<Entity>();
		resources = new HashMap<GridPoint2, Entity>();
		for (int icell = 0; icell < PATCH_WIDTH; icell++) {
			for (int jcell = 0; jcell < PATCH_HEIGHT; jcell++) {
				if (maze[icell][jcell] == MazeGenerator.WALL) {
					float xast = (icell - (PATCH_WIDTH / 2.0f) + (x * PATCH_WIDTH));
					float yast = (jcell - (PATCH_HEIGHT / 2.0f) + (y * PATCH_HEIGHT));
					asteroids.add(mapSystem.createAsteroid(xast, yast));
				}

				if (resourceArray[icell][jcell] != null) {
					GridPoint2 pos = new GridPoint2(x * PATCH_WIDTH + icell - (PATCH_WIDTH / 2), y * PATCH_HEIGHT - (PATCH_HEIGHT / 2) + jcell);
					if (!mapSystem.isResourceUsed(pos)) {
						float xicon = pos.x;
						float yicon = pos.y;
						resources.put(new GridPoint2(icell, jcell), mapSystem.createResourceIcon(xicon, yicon, resourceArray[icell][jcell]));
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
