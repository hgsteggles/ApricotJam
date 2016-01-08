package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class LineComponent implements Component {
	public Vector2 start = new Vector2();
	public Vector2 end = new Vector2();

	public Vector2 startCached = new Vector2();
	public Vector2 endCached = new Vector2();
}
