package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class HelmetUI {

	public static AtlasRegion fullOverlay;
	public static AtlasRegion base;
	public static AtlasRegion pipesLeft;
	public static AtlasRegion pipesRight;

	public static AtlasRegion fluidBlue;
	public static AtlasRegion fluidRed;
	public static AtlasRegion fluidYellow;
	public static AtlasRegion fluidBlack;

	public static AtlasRegion valveBlue;
	public static AtlasRegion valveGreen;

	public static AtlasRegion screw;
	public static AtlasRegion speaker;
	
	public static AtlasRegion fog;

	public static void load(TextureAtlas atlas) {
		fullOverlay = atlas.findRegion("mainOverlay");

		base = atlas.findRegion("helmetBase");
		pipesLeft = atlas.findRegion("helmetPipesLeft");
		pipesRight = atlas.findRegion("helmetPipesRight");

		fluidBlue = atlas.findRegion("fluidBlue");
		fluidRed = atlas.findRegion("fluidRed");
		fluidYellow = atlas.findRegion("fluidYellow");
		fluidBlack = atlas.findRegion("fluidBlack");

		valveBlue = atlas.findRegion("valveBlue");
		valveGreen = atlas.findRegion("valveGreen");

		screw = atlas.findRegion("screw");
		speaker = atlas.findRegion("comsSpeaker");
		
		fog = atlas.findRegion("fog");
	}
}
