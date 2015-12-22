package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MiscArt {

    public static TextureRegion[] chars;
    public static TextureRegion testImage;
    public static TextureRegion title;

    public static void load() {
        chars = Art.split("chars.png", 8, 8);
        testImage = Art.load("badlogic.jpg");
        title = Art.load("title.png");
    }
}
