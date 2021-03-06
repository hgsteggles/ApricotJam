package com.apricotjam.spacepanic.components.map;

import com.apricotjam.spacepanic.gameelements.Resource;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class MapScreenComponent implements Component {
	public enum State {
		EXPLORING, ENCOUNTER, PAUSED
	}

	public State currentState = State.EXPLORING;
	public Resource encounterResource = null;

	public Vector2 playerPosition = new Vector2();
	public float viewSize = 1.0f; // Number of asteroids viewable in the horizontal plane
}
