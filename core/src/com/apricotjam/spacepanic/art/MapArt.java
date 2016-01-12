package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

public class MapArt {
	public static AtlasRegion computerFrame;

	public static ArrayList<AtlasRegion> asteroids;

	public static AtlasRegion playerIcon;

	public static ArrayList<AtlasRegion> resourceIcons;

	public static Texture mapLine;
	public static AtlasRegion crossGood;
	public static AtlasRegion crossBad;

	public static void load(TextureAtlas atlas) {
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

		mapLine = Art.loadTexture("mapline01.png");
		mapLine.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		crossGood = atlas.findRegion("mapcross01");
		crossBad = atlas.findRegion("mapcross02");
	}

}
