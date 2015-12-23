package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.ComponentMapper;

public class ComponentMappers {
    public static ComponentMapper<TransformComponent> transform = ComponentMapper.getFor(TransformComponent.class);
    public static ComponentMapper<BitmapFontComponent> bitmapfont = ComponentMapper.getFor(BitmapFontComponent.class);
    public static ComponentMapper<TextureComponent> texture = ComponentMapper.getFor(TextureComponent.class);
    public static ComponentMapper<AlphaFlashComponent> flash = ComponentMapper.getFor(AlphaFlashComponent.class);
}
