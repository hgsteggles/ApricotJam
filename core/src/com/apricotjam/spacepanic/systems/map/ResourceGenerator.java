package com.apricotjam.spacepanic.systems.map;

import com.apricotjam.spacepanic.GameParameters;
import com.apricotjam.spacepanic.gameelements.Resource;
import com.badlogic.gdx.math.RandomXS128;

import java.awt.*;
import java.util.ArrayList;

public class ResourceGenerator {
	private static final float[] CUMULATIVE_RESOURCE_PROB = new float[4];

	public int[] nres = {0, 0, 0 ,0};

	static {
		CUMULATIVE_RESOURCE_PROB[0] = GameParameters.RESOURCE_FRACTIONS[0];
		for (int i = 1; i < GameParameters.RESOURCE_FRACTIONS.length; i++) {
			CUMULATIVE_RESOURCE_PROB[i] = CUMULATIVE_RESOURCE_PROB[i - 1] + GameParameters.RESOURCE_FRACTIONS[i];
		}
	}

	private RandomXS128 rng = new RandomXS128(0);
	private final long seed;

	public ResourceGenerator(long seed) {
		this.seed = seed;
	}

	public Resource[][] generateResources(int x, int y, int[][] maze) {
		Resource[][] resources = new Resource[Patch.PATCH_WIDTH][Patch.PATCH_HEIGHT];
		setRandomState(x, y);

		ArrayList<Point> potentialLocations = new ArrayList<Point>();
		for (int i = 0; i < Patch.PATCH_WIDTH; i++) {
			for (int j = 0; j < Patch.PATCH_HEIGHT; j++) {
				resources[i][j] = null;
				if (maze[i][j] == MazeGenerator.PATH) {
					potentialLocations.add(new Point(i, j));
				}
			}
		}

		int nResources = rng.nextInt(GameParameters.MAX_RESOURCES_PER_PATCH + 1);
		if (potentialLocations.size() < nResources) {
			for (Point p: potentialLocations) {
				resources[p.x][p.y] = rollResource();
			}
		} else {
			for (int i = 0; i < nResources; i++) {
				int choice = rng.nextInt(potentialLocations.size());
				Point p = potentialLocations.get(choice);
				resources[p.x][p.y] = rollResource();
				potentialLocations.remove(p);
			}
		}

		return resources;
	}

	private Resource rollResource() {
		float roll = rng.nextFloat();
		for (int i = 0; i < CUMULATIVE_RESOURCE_PROB.length; i++) {
			if (roll <= CUMULATIVE_RESOURCE_PROB[i]) {
				nres[i]++;
				return Resource.values()[i];
			}
		}
		return Resource.OXYGEN; // Shouldn't come to this
	}

	private void setRandomState(long x, long y) {
		rng.setSeed(seed + (x << 16)  + y);
	}
}
