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
	static public AtlasRegion fog;
	
	static public ObjectMap<Resource, Color> resourceColors = new ObjectMap<Resource, Color>(); 

	static public void load(TextureAtlas atlas) {
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
		
		resourceColors.put(Resource.OXYGEN, new Color(0f, 0f, 1f, 1f));
		resourceColors.put(Resource.OIL, new Color(0f, 0f, 0f, 1f));
		resourceColors.put(Resource.RESOURCE2, new Color(0f, 1f, 0f, 1f));
		resourceColors.put(Resource.RESOURCE3, new Color(1f, 0f, 1f, 1f));
	}
}
