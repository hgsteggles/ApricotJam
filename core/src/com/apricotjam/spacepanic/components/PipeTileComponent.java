package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;

public class PipeTileComponent implements Component {
	public float fillDuration = 0f; // Time it takes to fill up pipe.
	public float currFill = 0f; // Tracks how full the pipe is 0 (empty) -> 1 (full).
	public boolean filling = false;
	public boolean connectedLeft = false;
	public boolean connectedRight = false;
	public boolean connectedUp = false;
	public boolean connectedDown = false;
}
