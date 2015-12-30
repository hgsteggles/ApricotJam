package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class MiscArt {

	public static AtlasRegion title;
	public static AtlasRegion mainBackground;
	public static Texture mainBackgroundScrollable;

	public static ObjectMap<String, BitmapFont> fonts = new ObjectMap<String, BitmapFont>();

	public static void load(TextureAtlas atlas) {
		title = atlas.findRegion("title");
		mainBackground = atlas.findRegion("mainBackground");
		mainBackgroundScrollable = Art.loadTexture("mainBackground.png");

		fonts.put("retro", new BitmapFont(Gdx.files.internal("fonts/retro3.fnt"),
										  Gdx.files.internal("fonts/retro3.png"), false));
	}
}
