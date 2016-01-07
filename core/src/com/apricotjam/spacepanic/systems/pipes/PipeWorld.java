package com.apricotjam.spacepanic.systems.pipes;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.PipeGameArt;
import com.apricotjam.spacepanic.art.PipeGameArt.RotatedAnimationData;
import com.apricotjam.spacepanic.art.Shaders;
import com.apricotjam.spacepanic.components.AnimationComponent;
import com.apricotjam.spacepanic.components.BitmapFontComponent;
import com.apricotjam.spacepanic.components.ClickComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.FBO_Component;
import com.apricotjam.spacepanic.components.FBO_ItemComponent;
import com.apricotjam.spacepanic.components.PipeComponent;
import com.apricotjam.spacepanic.components.PipeFluidComponent;
import com.apricotjam.spacepanic.components.PipeTileComponent;
import com.apricotjam.spacepanic.components.ShaderComponent;
import com.apricotjam.spacepanic.components.ShaderTimeComponent;
import com.apricotjam.spacepanic.components.StateComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TickerComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.input.InputManager;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.interfaces.EventInterface;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PipeWorld {
	static public float PIPE_Z = 0f;
	private PipePuzzleGenerator generator = new PipePuzzleGenerator();
	private RandomXS128 rng = new RandomXS128(0);
	
	private Entity entryPipe, exitPipe;
	private Entity timer;
	private TransformComponent screenTransComp;
	
	private enum TileType {CORNER, SIDE, CENTRE};
	
	public void build(Engine engine) {
		screenTransComp = new TransformComponent();
		// Create screen.
		Entity screen = createScreen();
		screenTransComp = ComponentMappers.transform.get(screen);
		engine.addEntity(screen);
		
		// Create fluid lighting fbo.
		engine.addEntity(createFluidFBO());
		
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
				
				// Create pipe bg.
				engine.addEntity(createPipeBG(i, j, TileType.CENTRE));
				
				pipeEntities[i][j] = pipe;
			}
		}
		
		// Create borders.
		for (int i = -1; i < PipeSystem.GRID_LENGTH + 1; ++i) {
			engine.addEntity(createPipeBG(i, -1, TileType.SIDE));
			engine.addEntity(createPipeBG(i, PipeSystem.GRID_LENGTH, TileType.SIDE));
		}
		for (int j = 0; j < PipeSystem.GRID_LENGTH; ++j) {
			engine.addEntity(createPipeBG(-2, j, TileType.SIDE));
			engine.addEntity(createPipeBG(PipeSystem.GRID_LENGTH + 1, j, TileType.SIDE));
		}
		
		// Create corners.
		engine.addEntity(createPipeBG(-2, -1, TileType.CORNER));
		engine.addEntity(createPipeBG(-2, PipeSystem.GRID_LENGTH, TileType.CORNER));
		engine.addEntity(createPipeBG(PipeSystem.GRID_LENGTH + 1, -1, TileType.CORNER));
		engine.addEntity(createPipeBG(PipeSystem.GRID_LENGTH + 1, PipeSystem.GRID_LENGTH, TileType.CORNER));
		
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
		
		int timer_i = -1;
		int timer_j = 0;
		
		// Create timer pipes.
		Entity bottomLeftPipe = createPipe((byte)(1), timer_i, timer_j, false);
		Entity bottomLeftFluid = createFluid(bottomLeftPipe, 2);
		PipeTileComponent parentPipeTileComp = ComponentMappers.pipetile.get(bottomLeftPipe);
		entryPipe = bottomLeftPipe;
		
		engine.addEntity(bottomLeftPipe);
		engine.addEntity(bottomLeftFluid);
		
		for (int j = timer_j + 1; j <= generator.getEntryPoint().y; ++j) {
			byte mask = (byte)((j == generator.getEntryPoint().y) ? 6 : 5);
			Entity timerPipe = createPipe(mask, timer_i, j, false);
			
			parentPipeTileComp.neighbours[0] = timerPipe;
			parentPipeTileComp = ComponentMappers.pipetile.get(timerPipe);
			
			engine.addEntity(timerPipe);
		}
		
		parentPipeTileComp.neighbours[1] = pipeEntities[0][generator.getEntryPoint().y];
		
		// Create exit pipe.
		Entity finalPipe = createPipe((byte)(8), generator.getExitPoint().x, generator.getExitPoint().y, false);
		PipeTileComponent exitPipeTileComp = ComponentMappers.pipetile.get(pipeEntities[generator.getExitPoint().x - 1][generator.getExitPoint().y]);
		exitPipeTileComp.neighbours[1] = finalPipe;
		exitPipe = finalPipe;
		
		engine.addEntity(finalPipe);
		
		// Create back panel.
		engine.addEntity(createBackPanel());
		
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
	
	public Entity getTimer() {
		return timer;
	}
	
	public Entity createScreen() {
		Entity entity = new Entity();
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.set(BasicScreen.WORLD_WIDTH/2f, BasicScreen.WORLD_HEIGHT/2f, PIPE_Z);
		transComp.parent = screenTransComp;
		entity.add(transComp);
		
		return entity;
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
		float gridOffsetX = -PipeSystem.GRID_LENGTH * pipeWidth / 2f;
		float gridOffsetY = -PipeSystem.GRID_LENGTH * pipeHeight / 2f;
		transComp.position.set(gridOffsetX + 0.5f * (2 * ipos + 1) * pipeWidth, gridOffsetY + 0.5f * (2 * jpos + 1) * pipeHeight, 0);
		
		transComp.rotation = PipeGameArt.pipeRegions.get(mask).rotation;
		transComp.parent = screenTransComp;
		
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
		Entity entity = new Entity();
		
		PipeTileComponent pipeTileComp = ComponentMappers.pipetile.get(pipe);
		entity.add(pipeTileComp);
		
		TransformComponent pipeTransComp = ComponentMappers.transform.get(pipe);
		entity.add(pipeTransComp);
		
		PipeFluidComponent pipeFluidComp = new PipeFluidComponent();
		pipeFluidComp.filling = true;
		pipeFluidComp.fillDuration = 4f;
		int exitDirection = PipeSystem.exitFromEntryDirection(pipeTileComp.mask, entryDirection);
		pipeFluidComp.exitMask = PipeSystem.maskFromDirection(exitDirection);
		pipeFluidComp.parentPipe = pipe;
		entity.add(pipeFluidComp);
		
		RotatedAnimationData animData = PipeGameArt.fluidRegions.get(pipeTileComp.mask).get(entryDirection);
		AnimationComponent animComp = new AnimationComponent();
		animComp.animations.put(PipeFluidComponent.STATE_FILLING, new Animation(pipeFluidComp.fillDuration/animData.regions.size, animData.regions));
		entity.add(animComp);
		
		FBO_ItemComponent fboItemComp = new FBO_ItemComponent();
		fboItemComp.fboBatch = Shaders.manager.getSpriteBatch("fluid-fb");
		entity.add(fboItemComp);
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("fluid");
		entity.add(shaderComp);
		
		ShaderTimeComponent shaderTimeComp = new ShaderTimeComponent();
		entity.add(shaderTimeComp);
		
		TextureComponent textureComp = new TextureComponent();
		textureComp.color.set(0.2f, 0.2f, 1.0f, 1f);
		entity.add(textureComp);
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.set(pipeTransComp.position.x, pipeTransComp.position.y, -1);
		transComp.rotation = animData.rotation;
		//transComp.parent = screenTransComp;
		entity.add(transComp);
		
		StateComponent stateComp = new StateComponent();
		stateComp.set(PipeFluidComponent.STATE_FILLING);
		entity.add(stateComp);
		
		return entity;
	}
	
	private Entity createPipeBG(int ipos, int jpos, TileType type) {
		TextureComponent textureComp = new TextureComponent();
		switch (type) {
			case CENTRE:
				textureComp.region = PipeGameArt.pipeBG_Centre;
				break;
			case SIDE:
				textureComp.region = PipeGameArt.pipeBG_Side[SpacePanic.rng.nextInt(PipeGameArt.pipeBG_Side.length)];
				break;
			case CORNER:
				textureComp.region = PipeGameArt.pipeBG_Corner;
				break;
		}
		
		textureComp.color.set(0.8f, 0.8f, 1.0f, type == TileType.CENTRE ? 0.6f : 1f);
		
		TransformComponent transComp = new TransformComponent();
		float gridOffsetX = - PipeSystem.GRID_LENGTH / 2f;
		float gridOffsetY = - PipeSystem.GRID_LENGTH / 2f;
		transComp.position.set(gridOffsetX + 0.5f * (2 * ipos + 1), gridOffsetY + 0.5f * (2 * jpos + 1), -2);
		transComp.parent = screenTransComp;
		
		Entity entity = new Entity();
		entity.add(textureComp).add(transComp);
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("light");
		entity.add(shaderComp);
		
		return entity;
	}
	
	private Entity createBackPanel() {
		Entity entity = new Entity();
		
		TextureComponent textureComp = new TextureComponent();
		textureComp.region = PipeGameArt.whitePixel;
		textureComp.size.set(PipeSystem.GRID_LENGTH + 2, PipeSystem.GRID_LENGTH + 2);
		textureComp.color.set(0.4f, 0.4f, 0.4f, 1f);
		entity.add(textureComp);
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.set(0, 0, -3);
		transComp.parent = screenTransComp;
		entity.add(transComp);
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("light");
		entity.add(shaderComp);
		
		return entity;
	}
	
	private Entity createTimer(int duration) {
		Entity entity = new Entity();
		
		PipeComponent pipeComp = new PipeComponent();
		entity.add(pipeComp);

		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "retro";
		fontComp.string = PipeSystem.createTimerString(duration);
		fontComp.color.set(Color.WHITE);
		fontComp.centering = true;
		entity.add(fontComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(0, PipeSystem.GRID_LENGTH / 2f + 0.5f, 0);
		transComp.parent = screenTransComp;
		entity.add(transComp);
		
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
		entity.add(tickComp);

		return entity;
	}
	
	private Entity createFluidFBO() {
		Entity entity = new Entity();
		
		TextureComponent texComp = new TextureComponent();
		texComp.region = new TextureRegion();
		texComp.size.set(BasicScreen.WORLD_WIDTH, BasicScreen.WORLD_HEIGHT);
		texComp.color.a = 0.8f;
		entity.add(texComp);
		
		FBO_Component fboComp = Shaders.generateFBOComponent("fluid-fb", texComp);
		entity.add(fboComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(0f, 0f, -1f);
		transComp.parent = screenTransComp;
		entity.add(transComp);
		
		return entity;
	}
}
