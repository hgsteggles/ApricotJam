package com.apricotjam.spacepanic.systems.helmet;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.HelmetUI;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.art.PipeGameArt;
import com.apricotjam.spacepanic.art.Shaders;
import com.apricotjam.spacepanic.components.BitmapFontComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.HelmetPartComponent;
import com.apricotjam.spacepanic.components.LED_Component;
import com.apricotjam.spacepanic.components.MovementComponent;
import com.apricotjam.spacepanic.components.ScrollComponent;
import com.apricotjam.spacepanic.components.ShaderComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TickerComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.components.TweenComponent;
import com.apricotjam.spacepanic.components.TweenSpec;
import com.apricotjam.spacepanic.interfaces.EventInterface;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.apricotjam.spacepanic.systems.RenderingSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;

public class HelmetWorld {
	public static float LEDBG_X = BasicScreen.WORLD_WIDTH*(17f/20f);
	public static float LEDBG_Y = BasicScreen.WORLD_HEIGHT*(1f/12f);
	public static float LEDBG_W = 4f;
	public static float LEDBG_H = 0.8f;
	
	public void build(Engine engine) {
		// Create star background.
		engine.addEntity(createStarBackground());
		// Create helmet.
		engine.addEntity(createHelmet());
		// Create helmet features.
		// TODO
		engine.addEntity(createFog());
		
		// Create black marquee.
		engine.addEntity(createLED_Panel());
	}
	
	private Entity createStarBackground() {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		Texture tex = MiscArt.mainBackgroundScrollable;
		float texToCorner = (float)Math.sqrt((tex.getWidth() * tex.getWidth()) + (tex.getHeight() * tex.getHeight()));
		texComp.region = new TextureRegion(tex, 0, 0, (int)texToCorner, (int)texToCorner);
		tex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		texComp.size.x = texToCorner * RenderingSystem.PIXELS_TO_WORLD;
		texComp.size.y = texToCorner * RenderingSystem.PIXELS_TO_WORLD;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		transComp.position.z = -10.0f;

		MovementComponent movementComp = new MovementComponent();
		movementComp.rotationalVelocity = 1.0f;

		ScrollComponent scrollComp = new ScrollComponent();
		scrollComp.speed.x = 0.3f;

		e.add(texComp);
		e.add(transComp);
		e.add(movementComp);
		e.add(scrollComp);

		return e;
	}
	
	private Entity createHelmet() {
		Entity e = new Entity();
		e.add(new HelmetPartComponent());

		TextureComponent texComp = new TextureComponent();
		texComp.region = HelmetUI.base;
		texComp.size.x = BasicScreen.WORLD_WIDTH;
		texComp.size.y = BasicScreen.WORLD_HEIGHT;
		e.add(texComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		transComp.position.z = 10;
		e.add(transComp);

		return e;
	}
	
	private Entity createFog() {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		texComp.region = HelmetUI.fog;
		texComp.size.x = BasicScreen.WORLD_WIDTH;
		texComp.size.y = BasicScreen.WORLD_HEIGHT;
		e.add(texComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		transComp.position.z = 9;
		e.add(transComp);
		
		TweenComponent tweenComp = new TweenComponent();
		
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 0.0f;
		tweenSpec.end = 1.0f;
		tweenSpec.period = 2f;
		tweenSpec.cycle = TweenSpec.Cycle.REVERSE;
		tweenSpec.interp = Interpolation.fade;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				TextureComponent tc = ComponentMappers.texture.get(e);
				tc.color.a = a;
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);
		e.add(tweenComp);

		return e;
	}
	
	private Entity createLED_Panel() {
		TextureComponent texComp = new TextureComponent();
		texComp.region = PipeGameArt.ledBG;
		texComp.color = new Color(Color.BLACK);
		texComp.size.x = LEDBG_W;
		texComp.size.y = LEDBG_H;
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.set(LEDBG_X, LEDBG_Y, 11f);
		
		Entity entity = new Entity();
		entity.add(texComp).add(transComp);
		
		return entity;
	}
	
	static public Entity createMarqueeLED(String text) {
		LED_Component ledComp = new LED_Component();
		
		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "led";
		fontComp.centering = true;
		fontComp.string = text;
		fontComp.color.set(1f, 1f, 0f, 1f);
		GlyphLayout layout = new GlyphLayout(); //dont do this every frame! Store it as member
		layout.setText(MiscArt.fonts.get(fontComp.font), text);
		fontComp.layout = layout;
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("led");
		Shaders.manager.begin("led");
		float normX = (LEDBG_X - LEDBG_W/2f)/BasicScreen.WORLD_WIDTH;
		float normY = (LEDBG_Y - LEDBG_H/2f)/BasicScreen.WORLD_HEIGHT;
		Shaders.manager.setUniformf("maskRect", normX, normY, LEDBG_W/BasicScreen.WORLD_WIDTH, LEDBG_H/BasicScreen.WORLD_HEIGHT);
		Shaders.manager.end();
		
		float width = layout.width*BasicScreen.WORLD_WIDTH/SpacePanic.WIDTH;// contains the width of the current set text
		float height = layout.height*BasicScreen.WORLD_HEIGHT/SpacePanic.HEIGHT; // contains the height of the current set text
		TransformComponent transComp = new TransformComponent();
		transComp.position.set(LEDBG_X + (LEDBG_W + width)/2f, LEDBG_Y, 20f);
		
		TweenComponent tweenComp = new TweenComponent();
		
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 0.0f;
		tweenSpec.end = 1.0f;
		tweenSpec.period = 4f;
		tweenSpec.cycle = TweenSpec.Cycle.LOOP;
		tweenSpec.interp = Interpolation.linear;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				TransformComponent tc = ComponentMappers.transform.get(e);
				BitmapFontComponent bfc = ComponentMappers.bitmapfont.get(e); 
				float w = bfc.layout.width*BasicScreen.WORLD_WIDTH/SpacePanic.WIDTH;
				tc.position.x = LEDBG_X + (LEDBG_W + w)/2f - a*(LEDBG_W + w);
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);
		
		TickerComponent tickComp = new TickerComponent();
		tickComp.tickerActive = false;
		tickComp.finishActive = true;
		tickComp.duration = 1f*tweenSpec.period;
		tickComp.finish = new EventInterface() {
			@Override
			public void dispatchEvent(Entity entity) {
				entity.remove(TweenComponent.class);
			}
		};
		tickComp.start();
		
		Entity entity = new Entity();
		entity.add(ledComp).add(fontComp).add(shaderComp).add(transComp).add(tweenComp).add(tickComp);
		
		return entity;
	}
	
	static public Entity createFlashLED(String text) {
		LED_Component ledComp = new LED_Component();
		
		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "led";
		fontComp.centering = true;
		fontComp.string = text;
		fontComp.color.set(1f, 0f, 0f, 1f);
		GlyphLayout layout = new GlyphLayout(); //dont do this every frame! Store it as member
		layout.setText(MiscArt.fonts.get(fontComp.font), text);
		fontComp.layout = layout;
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("led");
		Shaders.manager.begin("led");
		float normX = (LEDBG_X - LEDBG_W/2f)/BasicScreen.WORLD_WIDTH;
		float normY = (LEDBG_Y - LEDBG_H/2f)/BasicScreen.WORLD_HEIGHT;
		Shaders.manager.setUniformf("maskRect", normX, normY, LEDBG_W/BasicScreen.WORLD_WIDTH, LEDBG_H/BasicScreen.WORLD_HEIGHT);
		Shaders.manager.end();
		
		float width = layout.width*BasicScreen.WORLD_WIDTH/SpacePanic.WIDTH;// contains the width of the current set text
		float height = layout.height*BasicScreen.WORLD_HEIGHT/SpacePanic.HEIGHT; // contains the height of the current set text
		TransformComponent transComp = new TransformComponent();
		transComp.position.set(LEDBG_X, LEDBG_Y, 20f);
		
		TweenComponent tweenComp = new TweenComponent();
		
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 0.0f;
		tweenSpec.end = 1.0f;
		tweenSpec.period = 0.8f;
		tweenSpec.cycle = TweenSpec.Cycle.REVERSE;
		tweenSpec.interp = Interpolation.linear;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				BitmapFontComponent bfc = ComponentMappers.bitmapfont.get(e);
				bfc.color.a = a;
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);
		
		TickerComponent tickComp = new TickerComponent();
		tickComp.tickerActive = false;
		tickComp.finishActive = true;
		tickComp.duration = 8f*tweenSpec.period;
		tickComp.finish = new EventInterface() {
			@Override
			public void dispatchEvent(Entity entity) {
				entity.remove(TweenComponent.class);
			}
		};
		tickComp.start();
		
		Entity entity = new Entity();
		entity.add(ledComp).add(fontComp).add(shaderComp).add(transComp).add(tweenComp).add(tickComp);
		
		return entity;
	}
	
	static public Entity createAppearLED(String text) {
		LED_Component ledComp = new LED_Component();
		
		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "led";
		fontComp.centering = true;
		fontComp.string = text;
		fontComp.color.set(0f, 1f, 0f, 1f);
		GlyphLayout layout = new GlyphLayout(); //dont do this every frame! Store it as member
		layout.setText(MiscArt.fonts.get(fontComp.font), text);
		fontComp.layout = layout;
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("led");
		Shaders.manager.begin("led");
		float normX = (LEDBG_X - LEDBG_W/2f)/BasicScreen.WORLD_WIDTH;
		float normY = (LEDBG_Y - LEDBG_H/2f)/BasicScreen.WORLD_HEIGHT;
		Shaders.manager.setUniformf("maskRect", normX, normY, LEDBG_W/BasicScreen.WORLD_WIDTH, LEDBG_H/BasicScreen.WORLD_HEIGHT);
		Shaders.manager.end();
		
		float width = layout.width*BasicScreen.WORLD_WIDTH/SpacePanic.WIDTH;// contains the width of the current set text
		float height = layout.height*BasicScreen.WORLD_HEIGHT/SpacePanic.HEIGHT; // contains the height of the current set text
		TransformComponent transComp = new TransformComponent();
		transComp.position.set(LEDBG_X, LEDBG_Y, 20f);
		
		TweenComponent tweenComp = new TweenComponent();
		
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 0.0f;
		tweenSpec.end = 2.0f;
		tweenSpec.period = 4f;
		tweenSpec.cycle = TweenSpec.Cycle.ONCE;
		tweenSpec.interp = Interpolation.linear;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				BitmapFontComponent bfc = ComponentMappers.bitmapfont.get(e);
				bfc.color.a = Math.min(a, 1);
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);
		
		TickerComponent tickComp = new TickerComponent();
		tickComp.tickerActive = false;
		tickComp.finishActive = true;
		tickComp.duration = 1f*tweenSpec.period;
		tickComp.finish = new EventInterface() {
			@Override
			public void dispatchEvent(Entity entity) {
				entity.remove(TweenComponent.class);
				System.out.println("HERE");
			}
		};
		tickComp.start();
		
		Entity entity = new Entity();
		entity.add(ledComp).add(fontComp).add(shaderComp).add(transComp).add(tweenComp).add(tickComp);
		
		return entity;
	}
}
