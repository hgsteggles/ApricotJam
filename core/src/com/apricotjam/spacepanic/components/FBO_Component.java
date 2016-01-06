package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FBO_Component implements Component {
	public String FBO_ID = "";
	public SpriteBatch batch = new SpriteBatch();
	public Camera camera = null;
}
