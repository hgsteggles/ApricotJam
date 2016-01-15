package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class NinepatchComponent implements Component {
	public NinePatchDrawable patch = null;
	public final Vector2 size = new Vector2(1.0f, 1.0f);
	public boolean centre = true;
	public Color color = new Color(Color.WHITE);
}
