package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class BitmapFontComponent implements Component {
	public String font = "";
	public String string = "";
	public Color color = new Color(Color.WHITE);
	public boolean centering = false;
	public GlyphLayout layout = null;
}
