package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class ComputerArt {
	public static AtlasRegion computer;
	public static AtlasRegion computerBackground;
	public static AtlasRegion computerFrameSmall;
	public static AtlasRegion computerFrameLarge;

	public static void load(TextureAtlas atlas) {
		computer = atlas.findRegion("computer");
		computerBackground = atlas.findRegion("computerBackground");
		computerFrameSmall = atlas.findRegion("computerFrameSmall");
		computerFrameLarge = atlas.findRegion("computerFrameLarge");
	}
}
