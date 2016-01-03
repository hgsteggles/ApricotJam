package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import java.util.ArrayList;

public class ComputerArt {
	public static AtlasRegion computer;
	public static AtlasRegion computerBackground;
	public static AtlasRegion computerFrameSmall;
	public static AtlasRegion computerFrameLarge;

	public static ArrayList<AtlasRegion> asteroids;

	public static void load(TextureAtlas atlas) {
		computer = atlas.findRegion("computer");
		computerBackground = atlas.findRegion("computerBackground");
		computerFrameSmall = atlas.findRegion("computerFrameSmall");
		computerFrameLarge = atlas.findRegion("computerFrameLarge");

		asteroids = new ArrayList<AtlasRegion>();
		asteroids.add(atlas.findRegion("asteroid"));
		asteroids.add(atlas.findRegion("asteroid2"));
		asteroids.add(atlas.findRegion("asteroid3"));
	}
}
