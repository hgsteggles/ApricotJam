package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class ColorInterpolationComponent implements Component {
	public Color start = new Color();
	public Color finish = new Color();
}
