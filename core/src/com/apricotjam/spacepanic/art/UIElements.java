package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class UIElements {

    public static TextureRegion mainOverlay;

    public static void load() {
        mainOverlay = Art.load("ui/helmetOverlay.png");
    }
}
