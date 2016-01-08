package com.apricotjam.spacepanic.components.mapComponents;

import com.apricotjam.spacepanic.gameelements.Resource;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class MapScreenComponent implements Component {
	public enum State {
		EXPLORING, ENCOUNTER, PAUSED
	}

	public State currentState = State.EXPLORING;
	public Resource encounterResource = Resource.NONE;

	public Vector2 playerPosition = new Vector2();
}
