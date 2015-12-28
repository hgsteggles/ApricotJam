package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ObjectMap;

public class MiscArt {

	public static Texture title;
	public static Texture mainBackground;

	public static ObjectMap<String, BitmapFont> fonts = new ObjectMap<String, BitmapFont>();

	public static void load() {
		title = Art.loadTexture("title.png");
		mainBackground = Art.loadTexture("mainBackground.png");

		fonts.put("retro", new BitmapFont(Gdx.files.internal("fonts/retro3.fnt"),
										  Gdx.files.internal("fonts/retro3.png"), false));
	}
}
