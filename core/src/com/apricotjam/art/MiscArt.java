package com.apricotjam.art;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MiscArt {

    public static TextureRegion[] chars;
    public static TextureRegion testImage;

    public static void load() {
        chars = Art.split("chars.png", 8, 8);
        testImage = Art.load("badlogic.jpg");
    }
}
