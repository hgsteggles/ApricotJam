package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameCommon {

    public static TextureRegion mainOverlay;
    public static TextureRegion mainBackground;

    public static void load() {
        mainOverlay = Art.load("mainOverlay.png");
		mainBackground = Art.load("mainBackground.png");
    }
}
