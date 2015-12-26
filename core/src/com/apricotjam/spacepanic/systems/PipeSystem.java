package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.BitmapFontComponent;
import com.apricotjam.spacepanic.components.ClickComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.PipeTileComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.components.TweenComponent;
import com.apricotjam.spacepanic.components.TweenSpec;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.puzzle.PipePuzzleGenerator;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class PipeSystem extends EntitySystem {
	public static final int GRID_LENGTH = 6;
	public static final Array<GridPoint2> GridDeltas = createGridDeltas();
	private Entity[][] pipeTiles = new Entity[GRID_LENGTH][GRID_LENGTH];
	private RandomXS128 rng = new RandomXS128(0);
	private PipePuzzleGenerator generator = new PipePuzzleGenerator();

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
		return (byte)(mask | (1 << index));
	}

	static public byte disconnectAtIndex(byte mask, int index) {
		return (byte)(mask & ~(1 << index));
	}
	
	static public int oppositeDirectionIndex(int index) {
		return (index + 2)%4;
	}
	
	static private Array<GridPoint2> createGridDeltas() {
		Array<GridPoint2> deltas = new Array<GridPoint2>();
		deltas.add(new GridPoint2(0, 1));
		deltas.add(new GridPoint2(1, 0));
		deltas.add(new GridPoint2(0, -1));
		deltas.add(new GridPoint2(-1, 0));
		
		return deltas;
	}

	@Override
	public void addedToEngine(Engine engine) {
		addTiles(engine);
		//pipeTiles = engine.getEntitiesFor(Family.all(PipeTileComponent.class).get());
	}

	@Override
	public void update(float deltaTime) {
		// Check if the puzzle is solved.
		GridPoint2 start = generator.getEntryPoint();
		GridPoint2 end = generator.getExitPoint();
		
		int curr_i = start.x;
		int curr_j = start.y;
		
		Entity tile = pipeTiles[start.x][start.y];
		PipeTileComponent tileComp = ComponentMappers.pipetile.get(tile);
		
		// Find start pipe exit.
		int lastPipeExitDir = 0;
		for (int idir = 0; idir < 4; ++idir) {
			if (connectedAtIndex(tileComp.mask, idir)) {
				lastPipeExitDir = idir;
				break;
			}
		}
	
		boolean solved = false;
		
		while (!solved) {
			curr_i = curr_i + GridDeltas.get(lastPipeExitDir).x;
			curr_j = curr_j + GridDeltas.get(lastPipeExitDir).y;
			if (curr_i < 0 || curr_i >= GRID_LENGTH || curr_j < 0 || curr_j >= GRID_LENGTH) {
				break;
			}
			
			tile = pipeTiles[curr_i][curr_j];
			tileComp = ComponentMappers.pipetile.get(tile);
			
			// Check if connected to previous pipe segment.
			if (connectedAtIndex(tileComp.mask, oppositeDirectionIndex(lastPipeExitDir))) {
				// If this is the exit tile then it is solved.
				// If cross pipe, then the exit is opposite to entry. Else the exit is at 90 degrees.
				if (curr_i == end.x && curr_j == end.y) {
					solved = true;
				}
				else if (connectedAtIndex(tileComp.mask, lastPipeExitDir)) {
					continue;
				}
				else if (connectedAtIndex(tileComp.mask, (lastPipeExitDir+1)%4)) {
					lastPipeExitDir = (lastPipeExitDir+1)%4;
					continue;
				}
				else if (connectedAtIndex(tileComp.mask, (lastPipeExitDir+3)%4)) {
					lastPipeExitDir = (lastPipeExitDir+3)%4;
					continue;
				}
			}
			else {
				break;
			}
		}
		
		if (solved) {
			getEngine().addEntity(createSolvedText());
			resetTiles();
		}
	}

	private void addTiles(Engine engine) {
		generator.generatePuzzle(4);
		byte[][] maskGrid = generator.getMaskGrid();
		
		for (int i = 0; i < GRID_LENGTH; ++i) {
			for (int j = 0; j < GRID_LENGTH; ++j) {
				GridPoint2 start = generator.getEntryPoint();
				GridPoint2 end = generator.getExitPoint();
				boolean isExitEntry = ((i == start.x && j == start.y) || (i == end.x && j == end.y));
				
				Entity tile = createTile(maskGrid[i][j], i, j, isExitEntry);
				
				if (!isExitEntry) {
					if (rng.nextBoolean())
						rotateTile(tile);
					else {
						rotateTile(tile);
						rotateTile(tile);
						rotateTile(tile);
					}
				}
				
				engine.addEntity(tile);
				
				pipeTiles[i][j] = tile;
			}
		}
	}
	
	private void resetTiles() {
		generator.generatePuzzle(4);
		byte[][] maskGrid = generator.getMaskGrid();
		
		for (int i = 0; i < GRID_LENGTH; ++i) {
			for (int j = 0; j < GRID_LENGTH; ++j) {
				GridPoint2 start = generator.getEntryPoint();
				GridPoint2 end = generator.getExitPoint();
				boolean isExitEntry = ((i == start.x && j == start.y) || (i == end.x && j == end.y));
				
				resetTile(maskGrid[i][j], i, j, isExitEntry);
				
				if (!isExitEntry) {
					if (rng.nextBoolean())
						rotateTile(pipeTiles[i][j]);
					else {
						rotateTile(pipeTiles[i][j]);
						rotateTile(pipeTiles[i][j]);
						rotateTile(pipeTiles[i][j]);
					}
				}
			}
		}
	}

	private Entity createTile(byte mask, int ipos, int jpos, boolean isExitEntry) {
		Entity tile = new Entity();

		PipeTileComponent pipeTileComp = new PipeTileComponent();
		pipeTileComp.fillDuration = 4f;
		pipeTileComp.currFill = 0;
		pipeTileComp.filling = false;
		pipeTileComp.mask = mask;

		TextureComponent textureComp = new TextureComponent();
		textureComp.region = MiscArt.pipesRegion[MiscArt.pipeIndexes.get(mask)];

		TransformComponent transComp = new TransformComponent();
		float tileWidth = textureComp.size.x;
		float tileHeight = textureComp.size.y;
		float gridOffsetX = BasicScreen.WORLD_WIDTH / 2f - GRID_LENGTH * tileWidth / 2f;
		float gridOffsetY = BasicScreen.WORLD_HEIGHT / 2f - GRID_LENGTH * tileHeight / 2f;
		transComp.position.set(gridOffsetX + 0.5f * (2 * ipos + 1) * tileWidth, gridOffsetY + 0.5f * (2 * jpos + 1) * tileHeight, 0);

		if (mask == (byte) (5)) {
			transComp.rotation = 270f;
		}
		
		ClickComponent clickComp = new ClickComponent();
		clickComp.active = !isExitEntry;
		clickComp.clicker = new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				TransformComponent tc = ComponentMappers.transform.get(entity);
				tc.rotation -= 90f;
				if (tc.rotation > 360f)
					tc.rotation += 360f;
				PipeTileComponent ptc = ComponentMappers.pipetile.get(entity);
				ptc.mask = rotateMask(pipeTileComp.mask);
			}
		};
		clickComp.shape = new Rectangle().setSize(textureComp.size.x, textureComp.size.y).setCenter(0f, 0f);

		tile.add(pipeTileComp).add(textureComp).add(transComp).add(clickComp);

		return tile;
	}
	
	private void resetTile(byte mask, int ipos, int jpos, boolean isExitEntry) {
		PipeTileComponent pipeTileComp = ComponentMappers.pipetile.get(pipeTiles[ipos][jpos]);
		pipeTileComp.mask = mask;
		
		ClickComponent clickComp = ComponentMappers.click.get(pipeTiles[ipos][jpos]);
		clickComp.active = !isExitEntry;
		
		if (pipeTileComp.mask == (byte)(5)) {
			TransformComponent transComp = new TransformComponent();
			transComp.rotation = 270f;
		}
	}
	
	public void rotateTile(Entity tile) {
		PipeTileComponent pipeTileComp = ComponentMappers.pipetile.get(tile);
		TransformComponent transComp = ComponentMappers.transform.get(tile);
		
		transComp.rotation -= 90f;
		if (transComp.rotation > 360f)
			transComp.rotation += 360f;
		pipeTileComp.mask = rotateMask(pipeTileComp.mask);
	}
	
	private Entity createSolvedText() {
		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "retro";
		fontComp.string = "Solved!";
		fontComp.color = new Color(Color.WHITE);
		fontComp.centering = true;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT * 9f / 10f;

		TweenComponent tweenComponent = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 1.0f;
		tweenSpec.end = 0.0f;
		tweenSpec.period = 1f;
		tweenSpec.interp = Interpolation.linear;
		tweenSpec.cycle = TweenSpec.Cycle.ONCE;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				BitmapFontComponent bitmapFontComponent = ComponentMappers.bitmapfont.get(e);
				bitmapFontComponent.color.a = Math.max(Math.min(a, 1f), 0f);
			}
		};
		tweenComponent.tweenSpecs.add(tweenSpec);

		Entity solvedText = new Entity();
		solvedText.add(fontComp).add(transComp).add(tweenComponent);
		
		return solvedText;
	}

	private byte rotateMask(byte mask) {
		byte ND = (byte) (mask << 1);
		ND = (byte) (ND + ((mask >> 3) & 1));

		return (byte)(ND%16);
	}
}
