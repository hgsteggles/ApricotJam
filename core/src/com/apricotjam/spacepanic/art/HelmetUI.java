package com.apricotjam.spacepanic.art;

import com.apricotjam.spacepanic.gameelements.Resource;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class HelmetUI {

	static public AtlasRegion fullOverlay;
	static public AtlasRegion base;
	static public AtlasRegion pipesLeft;
	static public AtlasRegion pipesRight;

	static public AtlasRegion fluidBlue;
	static public AtlasRegion fluidRed;
	static public AtlasRegion fluidYellow;
	static public AtlasRegion fluidBlack;

	static public AtlasRegion valveBlue;
	static public AtlasRegion valveGreen;

	static public AtlasRegion screw;
	static public AtlasRegion speaker;

	static public AtlasRegion sidepanelLeft;
	static public TextureRegion sidepanelRight;
	static public TextureRegion resourcePanel;
	static public TextureRegion ledFrame;
	static public AtlasRegion fog, fog2;

	static public ObjectMap<Resource, Color> resourceColors = new ObjectMap<Resource, Color>();

	static public void create(TextureAtlas atlas) {
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

		sidepanelLeft = atlas.findRegion("sidepanel");
		sidepanelRight = new TextureRegion(sidepanelLeft);
		sidepanelRight.flip(true, false);
		resourcePanel = atlas.findRegion("resource-panel");
		ledFrame = atlas.findRegion("ledFrame");
		fog = atlas.findRegion("fog");
		fog2 = atlas.findRegion("fog2");

		resourceColors.put(Resource.OXYGEN, new Color(0.00f, 0.60f, 0.60f, 1.00f));
		resourceColors.put(Resource.DEMISTER, new Color(0.85f, 0.85f, 0.00f, 1.00f));
		resourceColors.put(Resource.PIPE_CLEANER, new Color(0.85f, 0.00f, 0.85f, 1.00f));
		resourceColors.put(Resource.PLUTONIUM, new Color(0.00f, 0.85f, 0.00f, 1.00f));
	}
}
