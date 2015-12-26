package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.graphics.Texture;

public class ComputerArt {
	public static Texture computer;
	public static Texture computerBackground;
	public static Texture computerFrameSmall;
	public static Texture computerFrameLarge;

	public static void load() {
		computer = Art.loadTexture("computer.png");
		computerBackground = Art.loadTexture("computerBackground.png");
		computerFrameSmall = Art.loadTexture("computerFrameSmall.png");
		computerFrameLarge = Art.loadTexture("computerFrameLarge.png");
	}
}
