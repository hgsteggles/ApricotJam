package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class MiscArt {

	public static AtlasRegion title;
	public static AtlasRegion astronautTitle;
	public static AtlasRegion mainBackground;
	public static Texture mainBackgroundScrollable;

	public static NinePatchDrawable buttonBorder;
	
	public static TextureRegion shipRegion;
	public static TextureRegion podScreenRegion;	
	public static TextureRegion playerRegion;
	public static TextureRegion playerMoveRegion;
	
	private static Skin skin = new Skin();

	public static void create(AssetManager assetManager) {
		TextureAtlas atlas = assetManager.get("atlas/art.atlas", TextureAtlas.class);
		
		title = atlas.findRegion("title");
		astronautTitle = atlas.findRegion("astronautTitle");
		mainBackground = atlas.findRegion("mainBackground");
		mainBackgroundScrollable = assetManager.get("mainBackground.png", Texture.class);
		
		shipRegion = atlas.findRegion("ship");
		podScreenRegion = atlas.findRegion("pod-screen");
		
		playerRegion = atlas.findRegion("player");
		playerRegion.flip(true, false);
		playerMoveRegion = atlas.findRegion("playerMove");
		playerMoveRegion.flip(true, false);
		
		skin.addRegions(atlas);
		buttonBorder = (NinePatchDrawable) skin.getDrawable("button-border");
	}
}
