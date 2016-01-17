package com.apricotjam.spacepanic.components;

import com.apricotjam.spacepanic.components.helmet.HelmetPartComponent;
import com.apricotjam.spacepanic.components.helmet.HelmetScreenComponent;
import com.apricotjam.spacepanic.components.helmet.ResourceCountComponent;
import com.apricotjam.spacepanic.components.helmet.ResourcePipeComponent;
import com.apricotjam.spacepanic.components.map.MapScreenComponent;
import com.apricotjam.spacepanic.components.map.ResourceComponent;
import com.apricotjam.spacepanic.components.pipe.PipeFluidComponent;
import com.apricotjam.spacepanic.components.pipe.PipeScreenComponent;
import com.apricotjam.spacepanic.components.pipe.PipeTileComponent;
import com.badlogic.ashley.core.ComponentMapper;

public class ComponentMappers {
	public static ComponentMapper<TransformComponent> transform = ComponentMapper.getFor(TransformComponent.class);
	public static ComponentMapper<BitmapFontComponent> bitmapfont = ComponentMapper.getFor(BitmapFontComponent.class);
	public static ComponentMapper<TextureComponent> texture = ComponentMapper.getFor(TextureComponent.class);
	public static ComponentMapper<AnimationComponent> animation = ComponentMapper.getFor(AnimationComponent.class);
	public static ComponentMapper<StateComponent> state = ComponentMapper.getFor(StateComponent.class);
	public static ComponentMapper<ClickComponent> click = ComponentMapper.getFor(ClickComponent.class);
	public static ComponentMapper<TweenComponent> tween = ComponentMapper.getFor(TweenComponent.class);
	public static ComponentMapper<TextButtonComponent> textbutton = ComponentMapper.getFor(TextButtonComponent.class);
	public static ComponentMapper<PipeTileComponent> pipetile = ComponentMapper.getFor(PipeTileComponent.class);
	public static ComponentMapper<PipeFluidComponent> pipefluid = ComponentMapper.getFor(PipeFluidComponent.class);
	public static ComponentMapper<TickerComponent> ticker = ComponentMapper.getFor(TickerComponent.class);
	public static ComponentMapper<MovementComponent> movment = ComponentMapper.getFor(MovementComponent.class);
	public static ComponentMapper<ScrollComponent> scroll = ComponentMapper.getFor(ScrollComponent.class);
	public static ComponentMapper<HelmetPartComponent> helmetPart = ComponentMapper.getFor(HelmetPartComponent.class);
	public static ComponentMapper<ShaderComponent> shader = ComponentMapper.getFor(ShaderComponent.class);
	public static ComponentMapper<ShaderTimeComponent> shadertime = ComponentMapper.getFor(ShaderTimeComponent.class);
	public static ComponentMapper<ShaderLightingComponent> shaderlight = ComponentMapper.getFor(ShaderLightingComponent.class);
	public static ComponentMapper<ShaderSpreadComponent> shaderspread = ComponentMapper.getFor(ShaderSpreadComponent.class);
	public static ComponentMapper<ShaderDirectionComponent> shaderdirection = ComponentMapper.getFor(ShaderDirectionComponent.class);
	public static ComponentMapper<ShaderMaskComponent> shadermask = ComponentMapper.getFor(ShaderMaskComponent.class);
	public static ComponentMapper<FBO_Component> fbo = ComponentMapper.getFor(FBO_Component.class);
	public static ComponentMapper<FBO_ItemComponent> fboitem = ComponentMapper.getFor(FBO_ItemComponent.class);
	public static ComponentMapper<MapScreenComponent> mapscreen = ComponentMapper.getFor(MapScreenComponent.class);
	public static ComponentMapper<PipeScreenComponent> pipescreen = ComponentMapper.getFor(PipeScreenComponent.class);
	public static ComponentMapper<HelmetScreenComponent> helmetscreen = ComponentMapper.getFor(HelmetScreenComponent.class);
	public static ComponentMapper<ResourceComponent> resource = ComponentMapper.getFor(ResourceComponent.class);
	public static ComponentMapper<LineComponent> line = ComponentMapper.getFor(LineComponent.class);
	public static ComponentMapper<ResourceCountComponent> resourcecount = ComponentMapper.getFor(ResourceCountComponent.class);
	public static ComponentMapper<ResourcePipeComponent> resourcepipe = ComponentMapper.getFor(ResourcePipeComponent.class);
	public static ComponentMapper<ColorInterpolationComponent> colorinterps = ComponentMapper.getFor(ColorInterpolationComponent.class);
	public static ComponentMapper<SoundComponent> sound = ComponentMapper.getFor(SoundComponent.class);
	public static ComponentMapper<NinepatchComponent> ninepatch = ComponentMapper.getFor(NinepatchComponent.class);
	public static ComponentMapper<ParticleEffectComponent> particle = ComponentMapper.getFor(ParticleEffectComponent.class);
}
