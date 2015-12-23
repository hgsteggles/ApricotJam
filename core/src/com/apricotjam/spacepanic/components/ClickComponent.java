package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Shape2D;

public class ClickComponent implements Component {
	public boolean active = true;
	public Shape2D shape;

	public boolean pointerOver = false; // True when pointer is held down over the click
	public boolean clickLast = false; // True if click has been clicked in the last frame
}
