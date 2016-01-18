package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class SplashArt {
	public static AtlasRegion logo;
	public static AtlasRegion apricot;
	public static AtlasRegion jam;
	public static AtlasRegion toast;

	public static void load(TextureAtlas atlas) {
		logo = atlas.findRegion("fullLogo");
		apricot = atlas.findRegion("apricot");
		jam = atlas.findRegion("jam");
		toast = atlas.findRegion("toast");
	}
}
