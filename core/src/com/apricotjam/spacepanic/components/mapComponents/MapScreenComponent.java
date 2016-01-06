package com.apricotjam.spacepanic.components.mapComponents;

import com.apricotjam.spacepanic.gameelements.Resource;
import com.badlogic.ashley.core.Component;

public class MapScreenComponent implements Component {
	public enum State {
		EXPLORING, ENCOUNTER
	}

	public State currentState = State.EXPLORING;
	public Resource encounterResource = Resource.NONE;
}
