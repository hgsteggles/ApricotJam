package com.apricotjam.spacepanic.systems.helmet;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.HelmetUI;
import com.apricotjam.spacepanic.art.MapArt;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.art.PipeGameArt;
import com.apricotjam.spacepanic.art.PipeGameArt.RotatedRegionData;
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
	public static float HELMET_Z = 10f;
	public static float LEDBG_X = (71f/80f)*BasicScreen.WORLD_WIDTH;
	public static float LEDBG_Y = (6f/80f)*BasicScreen.WORLD_HEIGHT;
	public static float LEDBG_W = 3f;
	public static float LEDBG_H = 0.7f;
	
	private float PIPE_SIZE = 0.6f;
	
	private TransformComponent resourcePanelTransform;
	
	public void build(Engine engine) {
		// Create star background.
		engine.addEntity(createStarBackground());
		
		// Create helmet.
		engine.addEntity(createHelmet());
		
		// Create helmet features.
		//// Panels.
		engine.addEntity(createSidePanel(true));
		engine.addEntity(createSidePanel(false));
		Entity resourcePanel = createResourcePanel();
		resourcePanelTransform = ComponentMappers.transform.get(resourcePanel);
		engine.addEntity(resourcePanel);
		
		//// Screws.
		engine.addEntity(createScrew((46f/1280f)*BasicScreen.WORLD_WIDTH, (240f/720f)*BasicScreen.WORLD_HEIGHT));
		engine.addEntity(createScrew((48f/1280f)*BasicScreen.WORLD_WIDTH, (560f/720f)*BasicScreen.WORLD_HEIGHT));
		engine.addEntity(createScrew((439f/1280f)*BasicScreen.WORLD_WIDTH, (699f/720f)*BasicScreen.WORLD_HEIGHT));
		engine.addEntity(createScrew((1.0f - 46f/1280f)*BasicScreen.WORLD_WIDTH, (240f/720f)*BasicScreen.WORLD_HEIGHT));
		engine.addEntity(createScrew((1.0f - 48f/1280f)*BasicScreen.WORLD_WIDTH, (560f/720f)*BasicScreen.WORLD_HEIGHT));
		engine.addEntity(createScrew((1.0f - 439f/1280f)*BasicScreen.WORLD_WIDTH, (699f/720f)*BasicScreen.WORLD_HEIGHT));
		
		//// Speaker.
		engine.addEntity(createSpeaker());
		
		float bottomRightPipeX = (0.005f-0.045703125f)*BasicScreen.WORLD_WIDTH;
		float bottomRightPipeY = -0.069722221f*BasicScreen.WORLD_HEIGHT;
		float pipeHeight = 0.7f*PIPE_SIZE;
		int bottomPipeLength = 4;
		
		for (int i = 0; i < 4; ++i) {
			Entity[] pipes1 = createPipeLine(bottomPipeLength - i, bottomRightPipeX, bottomRightPipeY + i*pipeHeight);
			for (Entity pipe : pipes1)
				engine.addEntity(pipe);
			
			//// Pipe caps.
			engine.addEntity(createPipeCap(bottomRightPipeX + (bottomPipeLength - i)*PIPE_SIZE - (1f/16f)*PIPE_SIZE, bottomRightPipeY + i*pipeHeight, false));
			engine.addEntity(createPipeCap(bottomRightPipeX - PIPE_SIZE + (1f/16f)*PIPE_SIZE, bottomRightPipeY + i*pipeHeight, true));
			
			//// Resource icons.
			engine.addEntity(createResourceIcon(bottomRightPipeX - 1.5f*PIPE_SIZE, bottomRightPipeY + i*pipeHeight, i));
		}
		
		//// Fog.
		engine.addEntity(createFog());
		
		// Create black marquee.
		engine.addEntity(createLED_PanelShadow());
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
		transComp.position.z = -1000;

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
		transComp.position.z = HELMET_Z;
		e.add(transComp);
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("helmet-light");
		e.add(shaderComp);

		return e;
	}
	
	private Entity createSpeaker() {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		texComp.region = HelmetUI.speaker;
		texComp.size.x = 2f;
		texComp.size.y = 2f;
		e.add(texComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = 0.5f*BasicScreen.WORLD_WIDTH;
		transComp.position.y = -0.06f*BasicScreen.WORLD_HEIGHT;
		transComp.position.z = HELMET_Z + 1;
		e.add(transComp);
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("helmet-light");
		e.add(shaderComp);

		return e;
	}
	
	private Entity createScrew(float x, float y) {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		texComp.region = HelmetUI.screw;
		texComp.size.x = 0.4f;
		texComp.size.y = 0.4f;
		e.add(texComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = x;
		transComp.position.y = y;
		transComp.position.z = HELMET_Z + 1;
		e.add(transComp);
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("helmet-light");
		e.add(shaderComp);

		return e;
	}
	
	private Entity createSidePanel(boolean isLeft) {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		texComp.region = isLeft ? HelmetUI.sidepanelLeft : HelmetUI.sidepanelRight;
		texComp.size.x = texComp.region.getRegionWidth()*BasicScreen.WORLD_WIDTH/SpacePanic.WIDTH;
		texComp.size.y = texComp.region.getRegionHeight()*BasicScreen.WORLD_HEIGHT/SpacePanic.HEIGHT;
		texComp.color.set(0.75f, 0.75f, 0.75f, 1f);
		e.add(texComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = isLeft ? texComp.size.x/2f : BasicScreen.WORLD_WIDTH - texComp.size.x/2f;
		transComp.position.y = texComp.size.y/2f;
		transComp.position.z = HELMET_Z + 1;
		e.add(transComp);
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("helmet-light");
		e.add(shaderComp);

		return e;
	}
	
	private Entity createResourcePanel() {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		texComp.region = HelmetUI.resourcePanel;
		texComp.size.x = texComp.region.getRegionWidth()*BasicScreen.WORLD_WIDTH/SpacePanic.WIDTH;
		texComp.size.y = texComp.region.getRegionHeight()*BasicScreen.WORLD_HEIGHT/SpacePanic.HEIGHT;
		texComp.color.set(0.5f, 0.5f, 0.5f, 1f);
		e.add(texComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = texComp.size.x/2f + (16f/1280f)*BasicScreen.WORLD_WIDTH;
		transComp.position.y = texComp.size.y/2f + (16f/720f)*BasicScreen.WORLD_HEIGHT;
		transComp.position.z = HELMET_Z + 2;
		e.add(transComp);
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("helmet-light");
		e.add(shaderComp);

		return e;
	}
	
	private Entity[] createPipeLine(int size, float x, float y) {
		Entity[] pipes = new Entity[size];
		
		for (int i = 0; i < size; ++i)
			pipes[i] = createPipeSegment((byte)(10), x + i*PIPE_SIZE, y);
		
		return pipes;
	}
	
	private Entity createPipeSegment(byte mask, float x, float y) {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		RotatedRegionData rotRegionData = PipeGameArt.pipeRegions.get(mask);
		texComp.region = rotRegionData.region;
		texComp.size.x = PIPE_SIZE;
		texComp.size.y = PIPE_SIZE;
		e.add(texComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = x;
		transComp.position.y = y;
		transComp.position.z = HELMET_Z + 3;
		transComp.rotation = rotRegionData.rotation;
		transComp.parent = resourcePanelTransform;
		e.add(transComp);

		return e;
	}
	
	private Entity createPipeCap(float x, float y, boolean isLeft) {
		Entity e = new Entity();
		
		TextureComponent texComp = new TextureComponent();
		texComp.region = isLeft ? PipeGameArt.pipeCapLeft : PipeGameArt.pipeCapRight;
		texComp.size.x = PIPE_SIZE;
		texComp.size.y = PIPE_SIZE;
		e.add(texComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = x;
		transComp.position.y = y;
		transComp.position.z = HELMET_Z + 4;
		transComp.parent = resourcePanelTransform;
		e.add(transComp);
		
		return e;
	}
	
	private Entity createResourceIcon(float x, float y, int index) {
		Entity e = new Entity();
		
		TextureComponent texComp = new TextureComponent();
		texComp.region = MapArt.resourceIcons.get(index);
		texComp.size.x = 0.6f*PIPE_SIZE;
		texComp.size.y = 0.6f*PIPE_SIZE;
		e.add(texComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = x;
		transComp.position.y = y;
		transComp.position.z = HELMET_Z + 4;
		transComp.parent = resourcePanelTransform;
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
		transComp.position.z = HELMET_Z - 1;
		e.add(transComp);
		
		TweenComponent tweenComp = new TweenComponent();
		
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 0.0f;
		tweenSpec.end = 1.0f;
		tweenSpec.period = 1.2f;
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
	
	private Entity createLED_PanelShadow() {
		float offset = 0.003f*BasicScreen.WORLD_WIDTH;
		TextureComponent texComp = new TextureComponent();
		texComp.region = PipeGameArt.whitePixel;
		texComp.color.set(0.4f, 0.4f, 0.4f, 1.0f);
		texComp.size.x = LEDBG_W + offset;
		texComp.size.y = LEDBG_H + offset;
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.set(LEDBG_X - 0.5f*offset, LEDBG_Y - 0.5f*offset, HELMET_Z + 2);
		
		Entity entity = new Entity();
		entity.add(texComp).add(transComp);
		
		return entity;
	}
	
	private Entity createLED_Panel() {
		Entity entity = new Entity();
		
		TextureComponent texComp = new TextureComponent();
		texComp.region = PipeGameArt.ledBG;
		texComp.color = new Color(Color.BLACK);
		texComp.size.x = LEDBG_W;
		texComp.size.y = LEDBG_H;
		entity.add(texComp);
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.set(LEDBG_X, LEDBG_Y, HELMET_Z + 3);
		entity.add(transComp);
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("helmet-light");
		entity.add(shaderComp);
		
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
		transComp.position.set(LEDBG_X + (LEDBG_W + width)/2f, LEDBG_Y, HELMET_Z + 4);
		
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
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.set(LEDBG_X, LEDBG_Y, HELMET_Z + 4);
		
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
		transComp.position.set(LEDBG_X, LEDBG_Y, HELMET_Z + 4);
		
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
			}
		};
		tickComp.start();
		
		Entity entity = new Entity();
		entity.add(ledComp).add(fontComp).add(shaderComp).add(transComp).add(tweenComp).add(tickComp);
		
		return entity;
	}
}
