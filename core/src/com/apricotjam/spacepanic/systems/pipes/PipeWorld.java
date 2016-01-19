package com.apricotjam.spacepanic.systems.pipes;

import com.apricotjam.spacepanic.GameParameters;
import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.HelmetUI;
import com.apricotjam.spacepanic.art.PipeGameArt;
import com.apricotjam.spacepanic.art.PipeGameArt.RotatedAnimationData;
import com.apricotjam.spacepanic.art.Shaders;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.components.pipe.PipeComponent;
import com.apricotjam.spacepanic.components.pipe.PipeFluidComponent;
import com.apricotjam.spacepanic.components.pipe.PipeScreenComponent;
import com.apricotjam.spacepanic.components.pipe.PipeTileComponent;
import com.apricotjam.spacepanic.gameelements.Resource;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.interfaces.EventInterface;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.misc.Colors;
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
import com.badlogic.gdx.utils.Array;

public class PipeWorld {
	//static public final int gridLength = 5;
	static public final Array<GridPoint2> GridDeltas = createGridDeltas();

	private PipePuzzleGenerator generator;
	private RandomXS128 rng = new RandomXS128(0);

	private Entity masterEntity;
	private Entity abortButton;

	private Array<Entity> entryPipes = new Array<Entity>();
	private Array<Entity> exitPipes = new Array<Entity>();

	private int difficulty;
	private int gridLength;

	private Array<Entity> allEntities = new Array<Entity>();

	public PipeWorld(Entity masterEntity, int difficulty) {
		this.masterEntity = masterEntity;
		this.difficulty = difficulty;
		this.gridLength = PuzzleDifficulty.gridSize.get(difficulty);

		generator = new PipePuzzleGenerator(gridLength);
	}

	public void build(Engine engine) {
		// Create fluid lighting fbo.
		addToEngine(engine, createFluidFBO());

		// Generate puzzle.
		generator.generatePuzzle(PuzzleDifficulty.npipes.get(difficulty), PuzzleDifficulty.turnOffs.get(difficulty));
		//generator.generatePuzzle(difficulty%10, 1 + (int)(difficulty/10f));

		byte[][] maskGrid = generator.getMaskGrid();

		Entity[][] pipeEntities = new Entity[gridLength][gridLength];

		// Create puzzle pipes and fluid.
		for (int i = 0; i < gridLength; ++i) {
			for (int j = 0; j < gridLength; ++j) {
				Entity pipe = createPipe(maskGrid[i][j], i, j, true);

				if (rng.nextBoolean()) {
					rotateTile(pipe);
				} else {
					rotateTile(pipe);
					rotateTile(pipe);
					rotateTile(pipe);
				}

				addToEngine(engine, pipe);

				pipeEntities[i][j] = pipe;
			}
		}

		// Create circuit borders.
		for (int i = -1; i < gridLength + 1; ++i) {
			addToEngine(engine, createCircuitBorder(i, -1, false));
			addToEngine(engine, createCircuitBorder(i, gridLength, false));
		}
		for (int j = 0; j < gridLength; ++j) {
			addToEngine(engine, createCircuitBorder(-2, j, false));
			addToEngine(engine, createCircuitBorder(gridLength + 1, j, false));
		}
		addToEngine(engine, createCircuitBorder(-2, -1, true));
		addToEngine(engine, createCircuitBorder(-2, gridLength, true));
		addToEngine(engine, createCircuitBorder(gridLength + 1, -1, true));
		addToEngine(engine, createCircuitBorder(gridLength + 1, gridLength, true));

		// Create pipe background tiles.
		for (int i = 0; i < gridLength; ++i) {
			for (int j = 0; j < gridLength; ++j) {
				addToEngine(engine, createPipeBG(i, j));
			}
		}

		// Link up puzzle neighbours.
		for (int i = 0; i < gridLength; ++i) {
			for (int j = 0; j < gridLength; ++j) {
				for (int idir = 0; idir < 4; ++idir) {
					int i_child = i + GridDeltas.get(idir).x;
					int j_child = j + GridDeltas.get(idir).y;

					if (withinBounds(i_child, j_child)) {
						PipeTileComponent pipeTileComp = ComponentMappers.pipetile.get(pipeEntities[i][j]);
						pipeTileComp.neighbours[idir] = pipeEntities[i_child][j_child];
					}
				}
			}
		}

		buildTimerPipes(engine, pipeEntities);

		// Create exit pipe.
		Array<GridPoint2> exits = generator.getExitPoints();

		for (int iexit = 0; iexit < exits.size; ++iexit) {
			Entity exitPipe = createPipe((byte) (8), exits.get(iexit).x, exits.get(iexit).y, false);
			PipeTileComponent exitPipeTileComp = ComponentMappers.pipetile.get(pipeEntities[exits.get(iexit).x - 1][exits.get(iexit).y]);
			exitPipeTileComp.neighbours[1] = exitPipe;
			addToEngine(engine, exitPipe);
			exitPipes.add(exitPipe);
		}

		// Create back panel.
		addToEngine(engine, createBackPanel());

		// Create timer.
		//timer = createTimer(30);
		//addToEngine(engine, timer);

		// Create led display.
		//addToEngine(engine, createLED_Panel());

		// Create capsule parts.
		PipeScreenComponent pipeScreenComp = ComponentMappers.pipescreen.get(masterEntity);
		addToEngine(engine, createCapsulePart(true));
		addToEngine(engine, createCapsulePart(false));
		addToEngine(engine, createCapsuleLights(true, pipeScreenComp.resource));
		addToEngine(engine, createCapsuleLights(false, pipeScreenComp.resource));
		
		// Create abort button.
		abortButton = createAbortButton();
		addToEngine(engine, abortButton);
	}
	
	public Entity getAbortButton() {
		return abortButton;
	}

	public Array<Entity> getAllPipeEntities() {
		return allEntities;
	}

	private void addToEngine(Engine engine, Entity entity) {
		allEntities.add(entity);
		engine.addEntity(entity);
	}

	private void buildTimerPipes(Engine engine, Entity[][] pipeGrid) {
		Array<GridPoint2> starts = generator.getEntryPoints();

		int timer_i = -1;
		Array<Integer> timer_j = new Array<Integer>();
		timer_j.add(0);

		if (starts.size > 1) {
			if (starts.size < 4) {
				timer_j.add(gridLength - 1);
			} else {
				timer_j.add(gridLength - 2);
			}

			if (starts.size > 2) {
				timer_j.add(gridLength - 3);

				if (starts.size == 4) {
					timer_j.add(gridLength - 1);
				}
			}
		}

		for (int istart = 0; istart < starts.size; ++istart) {
			int verticalDirection = starts.get(istart).y - timer_j.get(istart);
			if (verticalDirection > 0) {
				Entity startPipe = createPipe((byte) (1), timer_i, timer_j.get(istart), false);
				entryPipes.add(startPipe);
				PipeTileComponent parentPipeTileComp = ComponentMappers.pipetile.get(startPipe);

				addToEngine(engine, startPipe);

				for (int j = timer_j.get(istart) + 1; j <= starts.get(istart).y; ++j) {
					byte mask = (byte) ((j == starts.get(istart).y) ? 6 : 5);
					Entity timerPipe = createPipe(mask, timer_i, j, false);

					parentPipeTileComp.neighbours[0] = timerPipe;
					parentPipeTileComp = ComponentMappers.pipetile.get(timerPipe);

					addToEngine(engine, timerPipe);
				}

				parentPipeTileComp.neighbours[1] = pipeGrid[0][starts.get(istart).y];
			} else if (verticalDirection < 0) {
				Entity startPipe = createPipe((byte) (4), timer_i, timer_j.get(istart), false);
				entryPipes.add(startPipe);
				PipeTileComponent parentPipeTileComp = ComponentMappers.pipetile.get(startPipe);

				addToEngine(engine, startPipe);

				for (int j = timer_j.get(istart) - 1; j >= starts.get(istart).y; --j) {
					byte mask = (byte) ((j == starts.get(istart).y) ? 3 : 5);
					Entity timerPipe = createPipe(mask, timer_i, j, false);

					parentPipeTileComp.neighbours[2] = timerPipe;
					parentPipeTileComp = ComponentMappers.pipetile.get(timerPipe);

					addToEngine(engine, timerPipe);
				}

				parentPipeTileComp.neighbours[1] = pipeGrid[0][starts.get(istart).y];
			} else {
				Entity startPipe = createPipe((byte) (2), timer_i, timer_j.get(istart), false);
				entryPipes.add(startPipe);
				PipeTileComponent parentPipeTileComp = ComponentMappers.pipetile.get(startPipe);

				addToEngine(engine, startPipe);

				parentPipeTileComp.neighbours[1] = pipeGrid[0][starts.get(istart).y];
			}
		}
	}

	public Array<Entity> getEntryPipes() {
		return entryPipes;
	}

	public Array<Entity> getExitPipes() {
		return exitPipes;
	}

	public Array<GridPoint2> getEntryPoints() {
		return generator.getEntryPoints();
	}

	public Array<GridPoint2> getExitPoints() {
		return generator.getExitPoints();
	}

	private Entity createAbortButton() {
		Entity entity = new Entity();
		TextureComponent textureComp = new TextureComponent();
		textureComp.region = PipeGameArt.abortRegion;
		textureComp.size.set(1.5f, 0.75f);
		textureComp.color.set(1.0f, 0.3f, 0.3f, 1.0f);
		entity.add(textureComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(0, gridLength/2f + 0.5f, 0);
		transComp.parent = ComponentMappers.transform.get(masterEntity);
		entity.add(transComp);
		
		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("light");
		entity.add(shaderComp);
		
		ClickComponent clickComp = new ClickComponent();
		clickComp.shape = new Rectangle().setSize(textureComp.size.x, textureComp.size.y).setCenter(0, 0);
		entity.add(clickComp);
		
		return entity;
	}
	
	private Entity createCapsulePart(boolean isLeft) {
		Entity entity = new Entity();

		TextureComponent textureComp = new TextureComponent();
		textureComp.region = isLeft ? PipeGameArt.capsuleLeft : PipeGameArt.capsuleRight;
		textureComp.size.set((260f / 896f) * (gridLength + 2f), gridLength + 2f);
		entity.add(textureComp);

		TransformComponent transComp = new TransformComponent();
		float halfwidth = (gridLength + 4f + textureComp.size.x) / 2f;
		transComp.position.set(isLeft ? -halfwidth : halfwidth, 0, 0);
		transComp.parent = ComponentMappers.transform.get(masterEntity);
		entity.add(transComp);

		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("light");
		entity.add(shaderComp);

		return entity;
	}

	private Entity createCapsuleLights(boolean isLeft, Resource resource) {
		Entity entity = new Entity();

		TextureComponent textureComp = new TextureComponent();
		textureComp.region = isLeft ? PipeGameArt.capsuleMaskLeft : PipeGameArt.capsuleMaskRight;
		textureComp.size.set((260f / 896f) * (gridLength + 2f), gridLength + 2f);
		textureComp.color.set(0f, 0f, 0f, 1f);
		entity.add(textureComp);

		TransformComponent transComp = new TransformComponent();
		float halfwidth = (gridLength + 4f + textureComp.size.x) / 2f;
		transComp.position.set(isLeft ? -halfwidth : halfwidth, 0, 0);
		transComp.parent = ComponentMappers.transform.get(masterEntity);
		entity.add(transComp);

		ColorInterpolationComponent colorInterpComp = new ColorInterpolationComponent();
		colorInterpComp.start.set(0f, 0f, 0f, 1f);
		colorInterpComp.finish.set(HelmetUI.resourceColors.get(resource));
		entity.add(colorInterpComp);

		TweenComponent tweenComp = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 0.0f;
		tweenSpec.end = 1.0f;
		tweenSpec.period = 2f;
		tweenSpec.reverse = true;
		tweenSpec.cycle = TweenSpec.Cycle.INFLOOP;
		tweenSpec.interp = Interpolation.sine;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				TextureComponent tc = ComponentMappers.texture.get(e);
				ColorInterpolationComponent cic = ComponentMappers.colorinterps.get(e);
				Colors.lerp(tc.color, cic.start, cic.finish, a);
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);
		entity.add(tweenComp);

		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("light");
		entity.add(shaderComp);

		return entity;
	}

	private Entity createPipe(byte mask, int ipos, int jpos, boolean withinGrid) {
		Entity entity = new Entity();

		PipeTileComponent pipeTileComp = new PipeTileComponent();
		pipeTileComp.mask = mask;
		pipeTileComp.isTimer = !withinGrid;
		entity.add(pipeTileComp);

		TextureComponent textureComp = new TextureComponent();
		textureComp.region = PipeGameArt.pipeRegions.get(mask).region;
		entity.add(textureComp);

		TransformComponent transComp = new TransformComponent();
		float pipeWidth = textureComp.size.x;
		float pipeHeight = textureComp.size.y;
		float gridOffsetX = -gridLength * pipeWidth / 2f;
		float gridOffsetY = -gridLength * pipeHeight / 2f;
		transComp.position.set(gridOffsetX + 0.5f * (2 * ipos + 1) * pipeWidth, gridOffsetY + 0.5f * (2 * jpos + 1) * pipeHeight, 0);
		transComp.rotation = PipeGameArt.pipeRegions.get(mask).rotation;
		transComp.parent = ComponentMappers.transform.get(masterEntity);
		entity.add(transComp);

		if (withinGrid) {
			ClickComponent clickComp = new ClickComponent();
			clickComp.active = false;
			clickComp.clicker = new ClickInterface() {
				@Override
				public void onClick(Entity entity) {
					TransformComponent tc = ComponentMappers.transform.get(entity);
					tc.rotation -= 90f;
					if (tc.rotation > 360f) {
						tc.rotation += 360f;
					}
					PipeTileComponent ptc = ComponentMappers.pipetile.get(entity);
					ptc.mask = rotateMask(ptc.mask);
				}
			};
			clickComp.shape = new Rectangle().setSize(textureComp.size.x, textureComp.size.y).setCenter(0f, 0f);
			entity.add(clickComp);
		}

		return entity;
	}

	public Entity createFluid(Entity pipe, int entryDirection, Resource resource) {
		Entity entity = new Entity();

		PipeTileComponent pipeTileComp = ComponentMappers.pipetile.get(pipe);
		TransformComponent pipeTransComp = ComponentMappers.transform.get(pipe);

		PipeFluidComponent pipeFluidComp = new PipeFluidComponent();
		pipeFluidComp.filling = true;
		pipeFluidComp.fillDuration = GameParameters.FLUID_FILL_DURATION_BASE;
		int exitDirection = exitFromEntryDirection(pipeTileComp.mask, entryDirection);
		pipeFluidComp.exitMask = maskFromDirection(exitDirection);
		pipeFluidComp.parentPipe = pipe;
		entity.add(pipeFluidComp);

		RotatedAnimationData animData = PipeGameArt.fluidRegions.get(pipeTileComp.mask).get(entryDirection);
		AnimationComponent animComp = new AnimationComponent();
		animComp.animations.put(PipeFluidComponent.STATE_FILLING, new Animation(pipeFluidComp.fillDuration / animData.regions.size, animData.regions));
		entity.add(animComp);

		entity.add(Shaders.generateFBOItemComponent("fluid-fb"));

		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("fluid");
		entity.add(shaderComp);

		ShaderTimeComponent shaderTimeComp = new ShaderTimeComponent();
		entity.add(shaderTimeComp);

		TextureComponent textureComp = new TextureComponent();
		textureComp.color.set(HelmetUI.resourceColors.get(resource));
		entity.add(textureComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(pipeTransComp.position.x, pipeTransComp.position.y, -1);
		transComp.rotation = animData.rotation;
		entity.add(transComp);

		StateComponent stateComp = new StateComponent();
		stateComp.set(PipeFluidComponent.STATE_FILLING);
		if (pipeTileComp.isTimer) {
			stateComp.timescale = GameParameters.TIMER_SLOWDOWN[generator.getEntryPoints().size - 1] * GameParameters.FLUID_FILL_DURATION_BASE / GameParameters.FLUID_FILL_DURATION_TIMER;
		}
		entity.add(stateComp);

		return entity;
	}

	private Entity createCircuitBorder(int ipos, int jpos, boolean isCorner) {
		Entity entity = new Entity();

		TextureComponent textureComp = new TextureComponent();
		textureComp.region = isCorner ? PipeGameArt.circuitCorner : PipeGameArt.circuitSide[SpacePanic.rng.nextInt(PipeGameArt.circuitSide.length)];
		textureComp.color.set(0.8f, 0.8f, 1.0f, 1f);
		entity.add(textureComp);

		TransformComponent transComp = new TransformComponent();
		float gridOffsetX = -gridLength / 2f;
		float gridOffsetY = -gridLength / 2f;
		transComp.position.set(gridOffsetX + 0.5f * (2 * ipos + 1), gridOffsetY + 0.5f * (2 * jpos + 1), -2);
		transComp.parent = ComponentMappers.transform.get(masterEntity);
		entity.add(transComp);

		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("light");
		entity.add(shaderComp);

		return entity;
	}

	private Entity createPipeBG(int ipos, int jpos) {
		byte mask = 0;
		if (ipos > 0) {
			mask = connectAtIndex(mask, 3);
		}
		if (ipos < gridLength - 1) {
			mask = connectAtIndex(mask, 1);
		}
		if (jpos > 0) {
			mask = connectAtIndex(mask, 2);
		}
		if (jpos < gridLength - 1) {
			mask = connectAtIndex(mask, 0);
		}

		Entity entity = new Entity();

		TextureComponent textureComp = new TextureComponent();
		textureComp.region = PipeGameArt.pipeBGs.get(mask);
		textureComp.color.set(0.6f, 0.6f, 0.6f, 1f);
		entity.add(textureComp);

		TransformComponent transComp = new TransformComponent();
		float gridOffsetX = -gridLength / 2f;
		float gridOffsetY = -gridLength / 2f;
		transComp.position.set(gridOffsetX + 0.5f * (2 * ipos + 1), gridOffsetY + 0.5f * (2 * jpos + 1), -2);
		transComp.parent = ComponentMappers.transform.get(masterEntity);
		entity.add(transComp);

		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("light");
		entity.add(shaderComp);

		return entity;
	}

	private Entity createBackPanel() {
		Entity entity = new Entity();

		TextureComponent textureComp = new TextureComponent();
		textureComp.region = PipeGameArt.whitePixel;
		textureComp.size.set(gridLength + 2, gridLength + 2);
		textureComp.color.set(0.4f, 0.4f, 0.4f, 1f);
		entity.add(textureComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(0, 0, -3);
		transComp.parent = ComponentMappers.transform.get(masterEntity);
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
		fontComp.string = createTimerString(duration);
		fontComp.color.set(Color.WHITE);
		fontComp.centering = true;
		entity.add(fontComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(0, gridLength / 2f + 0.5f, 0);
		transComp.parent = ComponentMappers.transform.get(masterEntity);
		entity.add(transComp);

		TickerComponent tickComp = new TickerComponent();
		tickComp.duration = duration;
		tickComp.interval = 1;
		tickComp.ticker = new EventInterface() {
			@Override
			public void dispatchEvent(Entity entity) {
				BitmapFontComponent bfc = ComponentMappers.bitmapfont.get(entity);
				TickerComponent tc = ComponentMappers.ticker.get(entity);
				bfc.string = createTimerString((int) (tc.totalTimeLeft));
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
		transComp.parent = ComponentMappers.transform.get(masterEntity);
		entity.add(transComp);

		return entity;
	}

	static public int numberConnections(byte mask) {
		return ((mask) & 1) + ((mask >> 1) & 1) + ((mask >> 2) & 1) + ((mask >> 3) & 1);
	}

	static public boolean connectedLeft(byte mask) {
		return ((mask >> 3) & 1) == 1;
	}

	static public boolean connectedRight(byte mask) {
		return ((mask >> 1) & 1) == 1;
	}

	static public boolean connectedUp(byte mask) {
		return ((mask) & 1) == 1;
	}

	static public boolean connectedDown(byte mask) {
		return ((mask >> 2) & 1) == 1;
	}

	static public boolean connectedAtIndex(byte mask, int index) {
		return ((mask >> index) & 1) == 1;
	}

	static public byte connectAtIndex(byte mask, int index) {
		return (byte) (mask | (1 << index));
	}

	static public byte disconnectAtIndex(byte mask, int index) {
		return (byte) (mask & ~(1 << index));
	}

	static public int oppositeDirectionIndex(int index) {
		return (index + 2) % 4;
	}

	private boolean withinBounds(int i, int j) {
		return !(i < 0 || i >= gridLength || j < 0 || j >= gridLength);
	}

	static public int directionFromMask(byte mask) {
		for (int i = 0; i < 4; ++i) {
			if (connectedAtIndex(mask, i)) {
				return i;
			}
		}
		return -1;
	}

	static public byte maskFromDirection(int dir) {
		return (byte) (1 << dir);
	}

	static public byte rotateMask(byte mask) {
		byte ND = (byte) (mask << 1);
		ND = (byte) (ND + ((mask >> 3) & 1));

		return (byte) (ND % 16);
	}

	static public byte rotateMaskN(byte mask, int N) {
		byte result = mask;
		for (int i = 0; i < N; ++i) {
			result = rotateMask(result);
		}

		return result;
	}

	static public int exitFromEntryDirection(byte mask, int entryDir) {
		int oppositeDir = oppositeDirectionIndex(entryDir);
		if (connectedAtIndex(mask, oppositeDir)) {
			return oppositeDir;
		}

		int dir = (entryDir + 1) % 4;
		if (connectedAtIndex(mask, dir)) {
			return dir;
		}

		dir = (entryDir + 3) % 4;
		if (connectedAtIndex(mask, dir)) {
			return dir;
		}

		return oppositeDir;
	}

	static public void rotateTile(Entity tile) {
		PipeTileComponent pipeTileComp = ComponentMappers.pipetile.get(tile);
		TransformComponent transComp = ComponentMappers.transform.get(tile);

		transComp.rotation -= 90f;
		if (transComp.rotation > 360f) {
			transComp.rotation += 360f;
		}
		pipeTileComp.mask = rotateMask(pipeTileComp.mask);
	}

	static public String createTimerString(int t) {
		int nminutes = (int) (t / 60f);
		int nseconds = (int) (t - nminutes * 60);

		String timestring = "";
		if (nminutes < 10) {
			timestring += "0";
		}
		timestring += Integer.toString(nminutes);

		timestring += ":";

		if (nseconds < 10) {
			timestring += "0";
		}
		timestring += Integer.toString(nseconds);

		return timestring;
	}

	static private Array<GridPoint2> createGridDeltas() {
		Array<GridPoint2> deltas = new Array<GridPoint2>();
		deltas.add(new GridPoint2(0, 1));
		deltas.add(new GridPoint2(1, 0));
		deltas.add(new GridPoint2(0, -1));
		deltas.add(new GridPoint2(-1, 0));

		return deltas;
	}
}
