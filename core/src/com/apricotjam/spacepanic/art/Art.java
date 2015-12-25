package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Art {

	public static void load() {
		MiscArt.load();
		GameCommon.load();
	}

	// Splits a given image into individual dx by dy textureregions
	public static TextureRegion[] split(String name, int dx, int dy) {
		return split(name, dx, dy, false, false);
	}

	// Splits a given image into individual dx by dy textureregions
	public static TextureRegion[] split(String name, int dx, int dy, boolean flipX, boolean flipY) {
		Texture texture = new Texture(Gdx.files.internal(name));
		int xSlices = texture.getWidth() / dx;
		int ySlices = texture.getHeight() / dy;
		TextureRegion[] out = new TextureRegion[xSlices * ySlices];
		for (int x = 0; x < xSlices; x++) {
			for (int y = 0; y < ySlices; y++) {
				out[x + y * xSlices] = new TextureRegion(texture, x * dx, y * dy, dx, dy);
				out[x + y * xSlices].flip(flipX, flipY);
			}
		}
		return out;
	}

	// Splits a single row given image into individual dx by dy textureregions
	public static TextureRegion[] splitRow(String name, int dx, int dy, int row) {
		return splitRow(name, dx, dy, row, false, false);
	}

	// Splits a single row given image into individual dx by dy textureregions
	public static TextureRegion[] splitRow(String name, int dx, int dy, int row, boolean flipX, boolean flipY) {
		Texture texture = new Texture(Gdx.files.internal(name));
		int xSlices = texture.getWidth() / dx;
		TextureRegion[] out = new TextureRegion[xSlices];
		for (int x = 0; x < xSlices; x++) {
			TextureRegion tr = new TextureRegion(texture, x * dx, row * dy, dx, dy);
			out[x] = tr;
			out[x].flip(flipX, flipY);
		}
		return out;
	}

	public static TextureRegion load(String name, int width, int height) {
		Texture texture = new Texture(Gdx.files.internal(name));
		return new TextureRegion(texture, 0, 0, width, height);
	}

	public static TextureRegion load(String name) {
		Texture texture = new Texture(Gdx.files.internal(name));
		return new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
	}
}
