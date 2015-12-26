package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameCommon {

	public static Texture mainOverlay;
	public static Texture mainBackground;

	public static void load() {
		mainOverlay = Art.loadTexture("mainOverlay.png");
		mainBackground = Art.loadTexture("mainBackground.png");
	}
}
