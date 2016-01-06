package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import java.util.ArrayList;

public class MapArt {
	public static AtlasRegion computerBackground;
	public static AtlasRegion computerFrame;

	public static ArrayList<AtlasRegion> asteroids;

	public static AtlasRegion playerIcon;

	public static ArrayList<AtlasRegion> resourceIcons;

	public static void load(TextureAtlas atlas) {
		computerBackground = atlas.findRegion("computerBackground");
		computerFrame = atlas.findRegion("computerFrame");

		asteroids = new ArrayList<AtlasRegion>();
		asteroids.add(atlas.findRegion("asteroid"));
		asteroids.add(atlas.findRegion("asteroid2"));
		asteroids.add(atlas.findRegion("asteroid3"));

		playerIcon = atlas.findRegion("playerIcon");

		resourceIcons = new ArrayList<AtlasRegion>();
		resourceIcons.add(atlas.findRegion("resource0Icon"));
		resourceIcons.add(atlas.findRegion("resource1Icon"));
		resourceIcons.add(atlas.findRegion("resource2Icon"));
		resourceIcons.add(atlas.findRegion("resource3Icon"));
	}

}
