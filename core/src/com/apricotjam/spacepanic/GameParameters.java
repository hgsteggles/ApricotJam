package com.apricotjam.spacepanic;

import com.apricotjam.spacepanic.gameelements.Resource;
import com.badlogic.gdx.utils.ObjectMap;

public class GameParameters {

	public static final ObjectMap<Resource, Float> RESOURCE_MAX = new ObjectMap<Resource, Float>();
	static {
		RESOURCE_MAX.put(Resource.OXYGEN, 25.0f);
		RESOURCE_MAX.put(Resource.OIL, 20.0f);
		RESOURCE_MAX.put(Resource.RESOURCE2, 15.0f);
		RESOURCE_MAX.put(Resource.RESOURCE3, 10.0f);
	}

	public static final ObjectMap<Resource, Float> RESOURCE_DEPLETION = new ObjectMap<Resource, Float>();
	static {
		RESOURCE_DEPLETION.put(Resource.OXYGEN, -5.0f);
		RESOURCE_DEPLETION.put(Resource.OIL, -5.0f);
		RESOURCE_DEPLETION.put(Resource.RESOURCE2, -5.0f);
		RESOURCE_DEPLETION.put(Resource.RESOURCE3, -5.0f);
	}

	public static final ObjectMap<Resource, Float> RESOURCE_GAIN = new ObjectMap<Resource, Float>();
	static {
		RESOURCE_GAIN.put(Resource.OXYGEN, -5.0f);
		RESOURCE_GAIN.put(Resource.OIL, 5.0f);
		RESOURCE_GAIN.put(Resource.RESOURCE2, 5.0f);
		RESOURCE_GAIN.put(Resource.RESOURCE3, 5.0f);
	}

	// How likely each cell is to be a path on the patch boundaries, 0.5f is pretty good
	public static final float PATHINESS = 0.5f;
	// Amount of deadends, 0.0f - basically none, 1.0f quite a bit (but still playable)
	public static final float DEADENDNESS = 1.0f;
	public static final float SPEED = 3.0f;

	public static final float[] RESOURCE_FRACTIONS = { 0.6f, 0.2f, 0.15f, 0.05f };
	public static int MAX_RESOURCES_PER_PATCH = 4;

	private GameParameters() {}
}
