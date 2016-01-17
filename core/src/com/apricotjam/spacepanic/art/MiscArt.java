package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.ObjectMap;

public class MiscArt {

	public static AtlasRegion title;
	public static AtlasRegion mainBackground;
	public static Texture mainBackgroundScrollable;

	public static ObjectMap<String, BitmapFont> fonts = new ObjectMap<String, BitmapFont>();
	
	public static TextureRegion marioRegion;
	
	public static NinePatchDrawable buttonBorder;
	
	public static TextureRegion shipRegion;
	public static TextureRegion podScreenRegion;	
	public static TextureRegion playerRegion;
	public static TextureRegion playerMoveRegion;
	
	private static Skin skin = new Skin();;

	public static void load(TextureAtlas atlas) {
		title = atlas.findRegion("title");
		mainBackground = atlas.findRegion("mainBackground");
		mainBackgroundScrollable = Art.loadTexture("mainBackground.png");
		mainBackgroundScrollable.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

		fonts.put("retro", new BitmapFont(Gdx.files.internal("fonts/retro3.fnt"),
										  Gdx.files.internal("fonts/retro3.png"), false));
		fonts.put("led", new BitmapFont(Gdx.files.internal("fonts/led1.fnt"),
				  Gdx.files.internal("fonts/led1.png"), false));
		
		marioRegion = atlas.findRegion("mario");
		
		shipRegion = atlas.findRegion("ship");
		podScreenRegion = atlas.findRegion("pod-screen");
		
		playerRegion = atlas.findRegion("player");
		playerRegion.flip(true, false);
		playerMoveRegion = atlas.findRegion("playerMove");
		playerMoveRegion.flip(true, false);
		
		skin.addRegions(atlas);
		buttonBorder = (NinePatchDrawable)skin.getDrawable("button-border");
	}
}
