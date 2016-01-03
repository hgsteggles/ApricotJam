package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class TextureComponent implements Component {
	public final Vector2 size = new Vector2(1.0f, 1.0f);
	public TextureRegion region = null;
	public TextureRegion normal = null;
	public boolean centre = true;
	public Color color = Color.WHITE;
}
