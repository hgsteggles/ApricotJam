package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class MiscArt {

    public static TextureRegion[] chars;
    public static TextureRegion testImage;
    public static TextureRegion title;
    
    public static ObjectMap<String, BitmapFont> fonts = new ObjectMap<String, BitmapFont>();

    public static void load() {
        title = Art.load("title.png");
        
        fonts.put("retro", new BitmapFont(Gdx.files.internal("fonts/retro3.fnt"),
                Gdx.files.internal("fonts/retro3.png"), false));
    }
}
