package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;

public class MiscArt {

	public static Texture title;
	public static Texture mainBackground;

	public static Texture pipes;
	public static TextureRegion[] pipesRegion;
	public static IntMap<Integer> pipeIndexes = new IntMap<Integer>();

	public static ObjectMap<String, BitmapFont> fonts = new ObjectMap<String, BitmapFont>();

	public static void load() {
		title = Art.loadTexture("title.png");
		mainBackground = Art.loadTexture("mainBackground.png");

		fonts.put("retro", new BitmapFont(Gdx.files.internal("fonts/retro3.fnt"),
										  Gdx.files.internal("fonts/retro3.png"), false));


		pipes = Art.loadTexture("pipespritesheetx640640.png");
		pipesRegion = Art.split(pipes, 128, 128);

		pipeIndexes.put(1, 24);
		pipeIndexes.put(2, 21);
		pipeIndexes.put(4, 23);
		pipeIndexes.put(8, 22);
		pipeIndexes.put(0, 12);
		pipeIndexes.put(10, 1);
		pipeIndexes.put(5, 1);
		pipeIndexes.put(3, 10);
		pipeIndexes.put(6, 5);
		pipeIndexes.put(12, 14);
		pipeIndexes.put(9, 19);
		pipeIndexes.put(15, 2);
	}
}
