package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class HelmetUI {

	public static Texture fullOverlay;
	public static Texture base;
	public static Texture pipesLeft;
	public static Texture pipesRight;

	public static Texture fluidBlue;
	public static Texture fluidRed;
	public static Texture fluidYellow;
	public static Texture fluidBlack;

	public static Texture valveBlue;
	public static Texture valveGreen;

	public static Texture screw;
	public static Texture speaker;

	public static void load() {
		fullOverlay = Art.loadTexture("mainOverlay.png");

		base = Art.loadTexture("helmetBase.png");
		pipesLeft = Art.loadTexture("helmetPipesLeft.png");
		pipesRight = Art.loadTexture("helmetPipesRight.png");

		fluidBlue = Art.loadTexture("fluidBlue.png");
		fluidRed = Art.loadTexture("fluidRed.png");
		fluidYellow = Art.loadTexture("fluidYellow.png");
		fluidBlack = Art.loadTexture("fluidBlack.png");

		valveBlue = Art.loadTexture("valveBlue.png");
		valveGreen = Art.loadTexture("valveGreen.png");

		screw = Art.loadTexture("screw.png");
		speaker = Art.loadTexture("comsSpeaker.png");
	}
}
