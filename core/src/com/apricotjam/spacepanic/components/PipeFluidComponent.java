package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;

public class PipeFluidComponent implements Component {
	public static final int STATE_EMPTY = 0;
	public static final int STATE_FILLING = 1;
	
	public float fillDuration = 0f; // Time it takes to fill up pipe.
	public float currFill = 0f; // Tracks how full the pipe is 0 (empty) -> 1 (full).
	public boolean filling = true;
	public int iposExit = 0;
	public int jposExit = 0;
	public byte exitMask = 0;
	
}
