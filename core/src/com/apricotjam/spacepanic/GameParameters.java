package com.apricotjam.spacepanic;

import com.apricotjam.spacepanic.gameelements.Resource;
import com.badlogic.gdx.utils.ObjectMap;

public class GameParameters {

	//Maximum resources, also starting resources
	//Note to harry: Might be interesting to start with partial resources, particularly PLUTONIUM
	//as it teaches player what is does the very first time it is picked up
	public static final ObjectMap<Resource, Float> RESOURCE_MAX = new ObjectMap<Resource, Float>();

	static {
		RESOURCE_MAX.put(Resource.OXYGEN, 20.0f);
		RESOURCE_MAX.put(Resource.DEMISTER, 20.0f);
		RESOURCE_MAX.put(Resource.PIPE_CLEANER, 20.0f);
		RESOURCE_MAX.put(Resource.PLUTONIUM, 10.0f);
	}

	//Resource depletion per second
	public static final ObjectMap<Resource, Float> RESOURCE_DEPLETION = new ObjectMap<Resource, Float>();

	static {
		RESOURCE_DEPLETION.put(Resource.OXYGEN, -0.1f);
		RESOURCE_DEPLETION.put(Resource.DEMISTER, -0.1f);
		RESOURCE_DEPLETION.put(Resource.PIPE_CLEANER, -0.1f);
		RESOURCE_DEPLETION.put(Resource.PLUTONIUM, -0.1f);
	}

	//Gain at any non-zero pipe cleaner amount
	public static final ObjectMap<Resource, Float> RESOURCE_GAIN = new ObjectMap<Resource, Float>();

	static {
		RESOURCE_GAIN.put(Resource.OXYGEN, 5.0f);
		RESOURCE_GAIN.put(Resource.DEMISTER, 5.0f);
		RESOURCE_GAIN.put(Resource.PIPE_CLEANER, 5.0f);
		RESOURCE_GAIN.put(Resource.PLUTONIUM, 5.0f);
	}

	//Gain at zero pipe cleaner
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
	// Speed you move though maze (duh)
	public static final float SPEED = 3.0f;
	// Maze viewsize (width) at full PLUTONIUM, in asteriods
	public static final float MAX_VIEWSIZE = 15.0f;
	// Maze viewsize (width) at no PLUTONIUM, in asteriods
	public static final float MIN_VIEWSIZE = 5.0f;

	//Probability that a generated resource will be OXYGEN, DEMISTER, PIPECLEANER or PLUTONIUM
	public static final float[] RESOURCE_FRACTIONS = {0.5f, 0.2f, 0.15f, 0.15f};
	//Each patch (10x10 asteroids) will have between 0 and this many resources
	public static int MAX_RESOURCES_PER_PATCH = 4;

	//Fog varies quadratically between these points because of how the fog shader works
	//Fog value at max DEMISTER
	public static float FOG_MAX = 15.0f;
	//Fog value at min DEMISTER
	public static float FOG_MIN = 0.1f;

	public static final float PUZZLE_DIFFICULTY_INC = 1f;

	public static final float[] TIMER_SLOWDOWN = {1f, 0.5f, 0.2f, 0.1f};

	public static final float FLUID_FILL_DURATION_BASE = 4f;
	public static final float FLUID_FILL_DURATION_TIMER = 4f;
	public static final float FLUID_FILL_DURATION_SOLVED = FLUID_FILL_DURATION_BASE / 32f;

	//Time till death after 02 runs out, countdown timer starts at 5 second
	//Initial message takes ~5 seconds to player, so this must 10 or more,
	//unless you want to change some code in GameScreen to make it work
	public static float DEATH_TIME = 11.0f;

	private GameParameters() {
	}
}
