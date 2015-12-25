package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.ComponentMapper;

public class ComponentMappers {
	public static ComponentMapper<TransformComponent> transform = ComponentMapper.getFor(TransformComponent.class);
	public static ComponentMapper<BitmapFontComponent> bitmapfont = ComponentMapper.getFor(BitmapFontComponent.class);
	public static ComponentMapper<TextureComponent> texture = ComponentMapper.getFor(TextureComponent.class);
	public static ComponentMapper<ClickComponent> click = ComponentMapper.getFor(ClickComponent.class);
	public static ComponentMapper<TweenComponent> tween = ComponentMapper.getFor(TweenComponent.class);
	public static ComponentMapper<TextButtonComponent> textbutton = ComponentMapper.getFor(TextButtonComponent.class);
	public static ComponentMapper<PipeTileComponent> pipetile = ComponentMapper.getFor(PipeTileComponent.class);
	public static ComponentMapper<MovementComponent> movment = ComponentMapper.getFor(MovementComponent.class);
	public static ComponentMapper<ScrollComponent> scroll = ComponentMapper.getFor(ScrollComponent.class);
}
