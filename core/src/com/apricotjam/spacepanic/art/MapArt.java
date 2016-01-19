package com.apricotjam.spacepanic.art;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class MapArt {
	public static AtlasRegion computerFrame;

	public static ArrayList<AtlasRegion> asteroids;

	public static AtlasRegion playerIcon;
	public static AtlasRegion playerIconMove;

	public static ArrayList<AtlasRegion> resourceIcons;

	public static Texture mapLine;
	public static AtlasRegion crossGood;
	public static AtlasRegion crossBad;

	public static void create(AssetManager assetManager) {
		TextureAtlas atlas = assetManager.get("atlas/art.atlas", TextureAtlas.class);
		
		computerFrame = atlas.findRegion("computerFrame");

		asteroids = new ArrayList<AtlasRegion>();
		asteroids.add(atlas.findRegion("asteroid"));
		asteroids.add(atlas.findRegion("asteroid2"));
		asteroids.add(atlas.findRegion("asteroid3"));

		playerIcon = atlas.findRegion("playerIcon");
		playerIconMove = atlas.findRegion("playerIconMove");

		resourceIcons = new ArrayList<AtlasRegion>();
		resourceIcons.add(atlas.findRegion("resource0Icon"));
		resourceIcons.add(atlas.findRegion("resource1Icon"));
		resourceIcons.add(atlas.findRegion("resource2Icon"));
		resourceIcons.add(atlas.findRegion("resource3Icon"));

		mapLine = assetManager.get("mapline01.png", Texture.class);
		crossGood = atlas.findRegion("mapcross01");
		crossBad = atlas.findRegion("mapcross02");
	}

}
