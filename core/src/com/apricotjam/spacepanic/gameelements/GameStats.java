package com.apricotjam.spacepanic.gameelements;

import com.apricotjam.spacepanic.systems.pipes.PuzzleDifficulty;
import com.badlogic.gdx.utils.ObjectMap;

public class GameStats {
	public float timeAlive = 0.0f;
	public ObjectMap<Resource, Integer> resourceIndex = new ObjectMap<Resource, Integer>();
	public int[] resourceCount = new int[Resource.values().length];
	public float difficulty = 0;

	public GameStats() {
		int i = 0;
		for (Resource r : Resource.values()) {
			resourceIndex.put(r, i);
			resourceCount[i] = 0;
			i++;
		}
	}

	public void addResource(Resource r) {
		resourceCount[resourceIndex.get(r)]++;
	}
}
