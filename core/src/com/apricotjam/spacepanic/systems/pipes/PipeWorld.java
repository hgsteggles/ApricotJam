package com.apricotjam.spacepanic.systems.pipes;

import com.apricotjam.spacepanic.art.HelmetUI;
import com.apricotjam.spacepanic.art.PipeGameArt;
import com.apricotjam.spacepanic.art.PipeGameArt.RotatedAnimationData;
import com.apricotjam.spacepanic.art.Shaders;
import com.apricotjam.spacepanic.components.AnimationComponent;
import com.apricotjam.spacepanic.components.BitmapFontComponent;
import com.apricotjam.spacepanic.components.ClickComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.FBO_Component;
import com.apricotjam.spacepanic.components.FBO_ItemComponent;
import com.apricotjam.spacepanic.components.HelmetPartComponent;
import com.apricotjam.spacepanic.components.PipeComponent;
import com.apricotjam.spacepanic.components.PipeFluidComponent;
import com.apricotjam.spacepanic.components.PipeTileComponent;
import com.apricotjam.spacepanic.components.ShaderComponent;
import com.apricotjam.spacepanic.components.ShaderLightingComponent;
import com.apricotjam.spacepanic.components.ShaderTimeComponent;
import com.apricotjam.spacepanic.components.StateComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TickerComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.components.TweenComponent;
import com.apricotjam.spacepanic.components.TweenSpec;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.interfaces.EventInterface;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;

public class PipeWorld {
	private PipePuzzleGenerator generator = new PipePuzzleGenerator();
	private RandomXS128 rng = new RandomXS128(0);
	
	private Entity entryPipe, exitPipe;
	private Entity connectionText;
	private Entity timer;
	
	public void build(Engine engine) {
		// Create fluid lighting fbo.
		engine.addEntity(createFluidLightFBO());
		
		generator.generatePuzzle(4);
		byte[][] maskGrid = generator.getMaskGrid();
		
		Entity[][] pipeEntities = new Entity[PipeSystem.GRID_LENGTH][PipeSystem.GRID_LENGTH];
		
		// Create puzzle pipes and fluid.
		for (int i = 0; i < PipeSystem.GRID_LENGTH; ++i) {
			for (int j = 0; j < PipeSystem.GRID_LENGTH; ++j) {
				GridPoint2 start = generator.getEntryPoint();
				GridPoint2 end = generator.getExitPoint();
				boolean isExitEntry = ((i == start.x && j == start.y) || (i == end.x && j == end.y));
				
				Entity pipe = createPipe(maskGrid[i][j], i, j, true);
				
				if (!isExitEntry) {
					if (rng.nextBoolean())
						PipeSystem.rotateTile(pipe);
					else {
						PipeSystem.rotateTile(pipe);
						PipeSystem.rotateTile(pipe);
						PipeSystem.rotateTile(pipe);
					}
				}
								
				engine.addEntity(pipe);
				
				// Create border.
				int type = 0;
				engine.addEntity(createPipeBorder(i, j, type));
				
				pipeEntities[i][j] = pipe;
			}
		}
		
		// Create borders.
		for (int i = 0; i < PipeSystem.GRID_LENGTH; ++i) {
			int type = 1;
			engine.addEntity(createPipeBorder(i, -1, type));
		}
		for (int j = 0; j < PipeSystem.GRID_LENGTH; ++j) {
			int type = 2;
			engine.addEntity(createPipeBorder(PipeSystem.GRID_LENGTH, j, type));
		}
		
		// Link up puzzle neighbours.
		for (int i = 0; i < PipeSystem.GRID_LENGTH; ++i) {
			for (int j = 0; j < PipeSystem.GRID_LENGTH; ++j) {
				for (int idir = 0; idir < 4; ++idir) {
					int i_child = i + PipeSystem.GridDeltas.get(idir).x;
					int j_child = j + PipeSystem.GridDeltas.get(idir).y;
					
					if (PipeSystem.withinBounds(i_child, j_child)) {
						PipeTileComponent pipeTileComp = ComponentMappers.pipetile.get(pipeEntities[i][j]);
						pipeTileComp.neighbours[idir] = pipeEntities[i_child][j_child];
					}
				}
			}
		}
		
		// Create timer pipes.
		Entity bottomLeftPipe = createPipe((byte)(1), -2, 0, false);
		Entity bottomLeftFluid = createFluid(bottomLeftPipe, 2);
		PipeTileComponent parentPipeTileComp = ComponentMappers.pipetile.get(bottomLeftPipe);
		entryPipe = bottomLeftPipe;
		
		engine.addEntity(bottomLeftPipe);
		engine.addEntity(bottomLeftFluid);
		
		for (int j = 1; j <= generator.getEntryPoint().y; ++j) {
			byte mask = (byte)((j == generator.getEntryPoint().y) ? 6 : 5);
			Entity timerPipe = createPipe(mask, -2, j, false);
			
			parentPipeTileComp.neighbours[0] = timerPipe;
			parentPipeTileComp = ComponentMappers.pipetile.get(timerPipe);
			
			engine.addEntity(timerPipe);
		}
		
		Entity connectingPipe = createPipe((byte)(10), -1, generator.getEntryPoint().y, false);
		
		parentPipeTileComp.neighbours[1] = connectingPipe;
		parentPipeTileComp = ComponentMappers.pipetile.get(connectingPipe);
		parentPipeTileComp.neighbours[1] = pipeEntities[0][generator.getEntryPoint().y];
		
		engine.addEntity(connectingPipe);
		
		// Create exit pipe.
		Entity finalPipe = createPipe((byte)(8), generator.getExitPoint().x, generator.getExitPoint().y, false);
		PipeTileComponent exitPipeTileComp = ComponentMappers.pipetile.get(pipeEntities[generator.getExitPoint().x - 1][generator.getExitPoint().y]);
		exitPipeTileComp.neighbours[1] = finalPipe;
		exitPipe = finalPipe;
		
		engine.addEntity(finalPipe);
		
		// Create pipe bgs.
		engine.addEntity(createFancyPipeBG());
		
		// Create timer.
		timer = createTimer(30);
		engine.addEntity(timer);
		
		// Create led display.
		//engine.addEntity(createLED_Panel());
	}
	
	public Entity getEntryPipe() {
		return entryPipe;
	}
	
	public Entity getExitPipe() {
		return exitPipe;
	}
	
	public Entity getConnectionText() {
		return connectionText;
	}
	
	public Entity getTimer() {
		return timer;
	}
	
	private Entity createPipe(byte mask, int ipos, int jpos, boolean withinGrid) {
		Entity pipe = new Entity();

		PipeTileComponent pipeTileComp = new PipeTileComponent();
		pipeTileComp.mask = mask;

		TextureComponent textureComp = new TextureComponent();
		textureComp.region = PipeGameArt.pipeRegions.get(mask).region;

		TransformComponent transComp = new TransformComponent();
		float pipeWidth = textureComp.size.x;
		float pipeHeight = textureComp.size.y;
		float gridOffsetX = BasicScreen.WORLD_WIDTH / 2f - PipeSystem.GRID_LENGTH * pipeWidth / 2f;
		float gridOffsetY = BasicScreen.WORLD_HEIGHT / 2f - PipeSystem.GRID_LENGTH * pipeHeight / 2f;
		transComp.position.set(gridOffsetX + 0.5f * (2 * ipos + 1) * pipeWidth, gridOffsetY + 0.5f * (2 * jpos + 1) * pipeHeight, 0);
		transComp.rotation = PipeGameArt.pipeRegions.get(mask).rotation;
		
		ClickComponent clickComp = new ClickComponent();
		clickComp.active = withinGrid;
		clickComp.clicker = new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				TransformComponent tc = ComponentMappers.transform.get(entity);
				tc.rotation -= 90f;
				if (tc.rotation > 360f)
					tc.rotation += 360f;
				PipeTileComponent ptc = ComponentMappers.pipetile.get(entity);
				ptc.mask = PipeSystem.rotateMask(ptc.mask);
			}
		};
		clickComp.shape = new Rectangle().setSize(textureComp.size.x, textureComp.size.y).setCenter(0f, 0f);
		
		pipe.add(pipeTileComp).add(textureComp).add(transComp).add(clickComp);

		return pipe;
	}
	
	public Entity createFluid(Entity pipe, int entryDirection) {
		PipeTileComponent pipeTileComp = ComponentMappers.pipetile.get(pipe);
		TransformComponent pipeTransComp = ComponentMappers.transform.get(pipe);
		
		PipeFluidComponent pipeFluidComp = new PipeFluidComponent();
		pipeFluidComp.filling = true;
		pipeFluidComp.fillDuration = 4f;
		int exitDirection = PipeSystem.exitFromEntryDirection(pipeTileComp.mask, entryDirection);
		pipeFluidComp.exitMask = PipeSystem.maskFromDirection(exitDirection);
		pipeFluidComp.parentPipe = pipe;
		
		RotatedAnimationData animData = PipeGameArt.fluidRegions.get(pipeTileComp.mask).get(entryDirection);
		AnimationComponent animComp = new AnimationComponent();
		animComp.animations.put(PipeFluidComponent.STATE_FILLING, new Animation(pipeFluidComp.fillDuration/animData.regions.size, animData.regions));
		
		FBO_ItemComponent fboItemComp = new FBO_ItemComponent();
		fboItemComp.fboBatch = Shaders.manager.getSpriteBatch("fluid-light-fb");
		
		TextureComponent textureComp = new TextureComponent();
		textureComp.color.set(0.2f, 0.2f, 1.0f, 1f);

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(pipeTransComp.position.x, pipeTransComp.position.y, -1);
		transComp.rotation = animData.rotation;
		
		StateComponent stateComp = new StateComponent();
		stateComp.set(PipeFluidComponent.STATE_FILLING);
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("fluid");
		
		ShaderTimeComponent shaderTimeComp = new ShaderTimeComponent();
		
		Entity entity = new Entity();
		
		entity.add(pipeFluidComp).add(textureComp).add(transComp).add(animComp).add(stateComp).add(shaderComp)
			.add(shaderTimeComp);

		return entity;
	}
	
	private Entity createPipeBorder(int ipos, int jpos, int type) {
		TextureComponent textureComp = new TextureComponent();
		if (type == 0)
			textureComp.region = PipeGameArt.pipeBorder;
		else
			textureComp.region = PipeGameArt.pipeBorderTop;
		textureComp.color.set(0f, 0f, 0f, 1f);
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("light");
		
		ShaderLightingComponent shaderLightingComponent = new ShaderLightingComponent();
		
		TransformComponent transComp = new TransformComponent();
		float pipeWidth = textureComp.size.x;
		float pipeHeight = textureComp.size.y;
		float gridOffsetX = BasicScreen.WORLD_WIDTH / 2f - PipeSystem.GRID_LENGTH * pipeWidth / 2f;
		float gridOffsetY = BasicScreen.WORLD_HEIGHT / 2f - PipeSystem.GRID_LENGTH * pipeHeight / 2f;
		transComp.position.set(gridOffsetX + 0.5f * (2 * ipos + 1) * pipeWidth, gridOffsetY + 0.5f * (2 * jpos + 1) * pipeHeight, 1);
		if (type == 2)
			transComp.rotation = 90f;
		
		Entity entity = new Entity();
		entity.add(textureComp).add(shaderComp).add(shaderLightingComponent).add(transComp);
		
		return entity;
	}
	
	private Entity createFancyPipeBG() {
		TextureComponent textureComp = new TextureComponent();
		textureComp.region = PipeGameArt.pipeBG;
		textureComp.size.set(PipeSystem.GRID_LENGTH + 3, PipeSystem.GRID_LENGTH);
		//textureComp.normal = MiscArt.rockNormalRegion;
		textureComp.color.set(0.8f, 0.8f, 0.8f, 1.0f);
		//textureComp.color.a = 0.5f;
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("light");
		
		ShaderLightingComponent shaderLightingComponent = new ShaderLightingComponent();
		
		TransformComponent transComp = new TransformComponent();
		float posX = (BasicScreen.WORLD_WIDTH / 2f) - (PipeSystem.GRID_LENGTH/2f + 2 - (3 + PipeSystem.GRID_LENGTH)/2f);
		float posY = (BasicScreen.WORLD_HEIGHT / 2f);
		transComp.position.set(posX, posY, -2);
		
		Entity entity = new Entity();
		entity.add(textureComp).add(shaderComp).add(shaderLightingComponent).add(transComp);
		
		return entity;
	}
	
	private Entity createTimer(int duration) {
		Entity entity = new Entity();
		
		PipeComponent pipeComp = new PipeComponent();

		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "retro";
		fontComp.string = PipeSystem.createTimerString(duration);
		fontComp.color.set(Color.WHITE);
		fontComp.centering = true;

		TransformComponent transComp = new TransformComponent();
		float tileWidth = 1;
		float tileHeight = 1;
		float gridOffsetX = BasicScreen.WORLD_WIDTH / 2f - PipeSystem.GRID_LENGTH * tileWidth / 2f;
		float gridOffsetY = BasicScreen.WORLD_HEIGHT / 2f - PipeSystem.GRID_LENGTH * tileHeight / 2f;
		float ipos = PipeSystem.GRID_LENGTH/2f - 0.5f;
		int jpos = PipeSystem.GRID_LENGTH;
		transComp.position.set(gridOffsetX + 0.5f * (2 * ipos + 1) * tileWidth, gridOffsetY + 0.5f * (2 * jpos + 1) * tileHeight, 0);
		
		TickerComponent tickComp = new TickerComponent();
		tickComp.duration = duration;
		tickComp.interval = 1;
		tickComp.ticker = new EventInterface() {
			@Override
			public void dispatchEvent(Entity entity) {
				BitmapFontComponent bfc = ComponentMappers.bitmapfont.get(entity);
				TickerComponent tc = ComponentMappers.ticker.get(entity);
				bfc.string = PipeSystem.createTimerString((int)(tc.totalTimeLeft));
			}
		};
		tickComp.start();

		entity.add(pipeComp).add(fontComp).add(transComp).add(tickComp);

		return entity;
	}
	
	private Entity createFluidLightFBO() {
		Entity e = new Entity();
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("light");
		
		TextureComponent texComp = new TextureComponent();
		texComp.region = new TextureRegion();
		texComp.size.set(BasicScreen.WORLD_WIDTH, BasicScreen.WORLD_HEIGHT);
		texComp.color.a = 0.8f;
		e.add(texComp);
		
		FBO_Component fboComp = Shaders.generateFBOComponent("fluid-light-fb", texComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = 0;
		transComp.position.y = 0;
		transComp.position.z = -1;
		
		Entity entity = new Entity();
		entity.add(fboComp).add(shaderComp).add(texComp).add(transComp);
		
		return entity;
	}
}
