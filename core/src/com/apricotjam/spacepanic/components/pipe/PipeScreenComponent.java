package com.apricotjam.spacepanic.components.pipe;

import com.apricotjam.spacepanic.gameelements.Resource;
import com.badlogic.ashley.core.Component;

public class PipeScreenComponent implements Component {
	public enum State { PLAYING, PAUSED };

	public State currentState = State.PAUSED;
	public Resource resource = Resource.OXYGEN;
}
