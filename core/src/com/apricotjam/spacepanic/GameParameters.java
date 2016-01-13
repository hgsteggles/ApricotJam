package com.apricotjam.spacepanic;

import com.apricotjam.spacepanic.gameelements.Resource;
import com.badlogic.gdx.utils.ObjectMap;

public class GameParameters {

	public static final ObjectMap<Resource, Float> RESOURCE_MAX = new ObjectMap<Resource, Float>();
	static {
		RESOURCE_MAX.put(Resource.OXYGEN, 20.0f);
		RESOURCE_MAX.put(Resource.DEMISTER, 20.0f);
		RESOURCE_MAX.put(Resource.PIPE_CLEANER, 20.0f);
		RESOURCE_MAX.put(Resource.PLUTONIUM, 10.0f);
	}

	public static final ObjectMap<Resource, Float> RESOURCE_DEPLETION = new ObjectMap<Resource, Float>();
	static {
		RESOURCE_DEPLETION.put(Resource.OXYGEN, -0.1f);
		RESOURCE_DEPLETION.put(Resource.DEMISTER, -0.1f);
		RESOURCE_DEPLETION.put(Resource.PIPE_CLEANER, -0.1f);
		RESOURCE_DEPLETION.put(Resource.PLUTONIUM, -0.1f);
	}

	public static final ObjectMap<Resource, Float> RESOURCE_GAIN = new ObjectMap<Resource, Float>();
	static {
		RESOURCE_GAIN.put(Resource.OXYGEN, 5.0f);
		RESOURCE_GAIN.put(Resource.DEMISTER, 5.0f);
		RESOURCE_GAIN.put(Resource.PIPE_CLEANER, 5.0f);
		RESOURCE_GAIN.put(Resource.PLUTONIUM, 5.0f);
	}

	public static final ObjectMap<Resource, Float> RESOURCE_GAIN_ALT = new ObjectMap<Resource, Float>();
	static {
		RESOURCE_GAIN_ALT.put(Resource.OXYGEN, 2.5f);
		RESOURCE_GAIN_ALT.put(Resource.DEMISTER, 2.5f);
		RESOURCE_GAIN_ALT.put(Resource.PIPE_CLEANER, 2.5f);
		RESOURCE_GAIN_ALT.put(Resource.PLUTONIUM, 2.5f);
	}

	// How likely each cell is to be a path on the patch boundaries, 0.5f is pretty good
	public static final float PATHINESS = 0.5f;
	// Amount of deadends, 0.0f - basically none, 1.0f quite a bit (but still playable)
	public static final float DEADENDNESS = 1.0f;
	public static final float SPEED = 3.0f;
	public static final float MAX_VIEWSIZE = 15.0f;
	public static final float MIN_VIEWSIZE = 5.0f;

	public static final float[] RESOURCE_FRACTIONS = { 0.5f, 0.2f, 0.15f, 0.15f };
	public static int MAX_RESOURCES_PER_PATCH = 4;

	public static float FOG_MAX = 15.0f;
	public static float FOG_MIN = 0.1f;

	public static final ObjectMap<Resource, Integer> RESOURCE_MIN_PIPE_DIFFICULTY = new ObjectMap<Resource, Integer>();
	static {
		RESOURCE_MIN_PIPE_DIFFICULTY.put(Resource.OXYGEN, 1);
		RESOURCE_MIN_PIPE_DIFFICULTY.put(Resource.DEMISTER, 5);
		RESOURCE_MIN_PIPE_DIFFICULTY.put(Resource.PIPE_CLEANER, 10);
		RESOURCE_MIN_PIPE_DIFFICULTY.put(Resource.PLUTONIUM, 15);
	}

	public static final ObjectMap<Resource, Integer> RESOURCE_SPREAD_PIPE_DIFFICULTY = new ObjectMap<Resource, Integer>();
	static {
		RESOURCE_SPREAD_PIPE_DIFFICULTY.put(Resource.OXYGEN, 8);
		RESOURCE_SPREAD_PIPE_DIFFICULTY.put(Resource.DEMISTER, 10);
		RESOURCE_SPREAD_PIPE_DIFFICULTY.put(Resource.PIPE_CLEANER, 12);
		RESOURCE_SPREAD_PIPE_DIFFICULTY.put(Resource.PLUTONIUM, 14);
	}

	public static float DEATH_TIME = 11.0f;

	private GameParameters() {}
}
