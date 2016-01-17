package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;

public class Art {

	public static TextureAtlas atlas;

	public static void load() {
		Art.atlas = new TextureAtlas(Gdx.files.internal("atlas/art.atlas"));

		HelmetUI.load(atlas);
		MapArt.load(atlas);
		MiscArt.load(atlas);
		PipeGameArt.load(atlas);
	}

	public static void dispose() {
		PipeGameArt.dipose();
		atlas.dispose();
	}

	public static Texture loadTexture(String name) {
		return new Texture(Gdx.files.internal(name));
	}

	public static TextureRegion createTextureRegion(Texture texture) {
		return new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
	}

	// Splits a given image into individual dx by dy textureregions
	public static TextureRegion[] split(Texture texture, int dx, int dy) {
		return split(texture, dx, dy, false, false);
	}

	// Splits a given image into individual dx by dy textureregions
	public static TextureRegion[] split(Texture texture, int dx, int dy, boolean flipX, boolean flipY) {
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
	public static TextureRegion[] splitRow(Texture texture, int dx, int dy, int row) {
		return splitRow(texture, dx, dy, row, false, false);
	}

	// Splits a single row given image into individual dx by dy textureregions
	public static TextureRegion[] splitRow(Texture texture, int dx, int dy, int row, boolean flipX, boolean flipY) {
		int xSlices = texture.getWidth() / dx;
		TextureRegion[] out = new TextureRegion[xSlices];
		for (int x = 0; x < xSlices; x++) {
			TextureRegion tr = new TextureRegion(texture, x * dx, row * dy, dx, dy);
			out[x] = tr;
			out[x].flip(flipX, flipY);
		}
		return out;
	}
}
