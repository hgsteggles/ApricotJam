package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;

public class MiscArt {

	public static TextureRegion[] chars;
	public static TextureRegion[] pipes;
	public static TextureRegion title;

	public static ObjectMap<String, BitmapFont> fonts = new ObjectMap<String, BitmapFont>();
	public static IntMap<Integer> pipeIndexes = new IntMap<Integer>();

	public static void load() {
		title = Art.load("title.png");

		fonts.put("retro", new BitmapFont(Gdx.files.internal("fonts/retro3.fnt"),
										  Gdx.files.internal("fonts/retro3.png"), false));

		pipes = Art.split("pipespritesheetx640640.png", 128, 128);

		pipeIndexes.put(10, 1);
		pipeIndexes.put(5, 1);
		pipeIndexes.put(3, 10);
		pipeIndexes.put(6, 5);
		pipeIndexes.put(12, 14);
		pipeIndexes.put(9, 19);
	}
}
