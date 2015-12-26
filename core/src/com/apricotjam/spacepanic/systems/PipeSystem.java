package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.ClickComponent;
import com.apricotjam.spacepanic.components.PipeTileComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.puzzle.PipePuzzleGenerator;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.apricotjam.spacepanic.screen.MenuScreen;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class PipeSystem extends EntitySystem {
	public static final int GRID_LENGTH = 5;
	public static final Array<GridPoint2> GridDeltas = createGridDeltas();
	private ImmutableArray<Entity> pipeTiles;
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
	
	static public int opppositeDirectionIndex(int index) {
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
		pipeTiles = engine.getEntitiesFor(Family.all(PipeTileComponent.class).get());
	}

	@Override
	public void update(float deltaTime) {

	}

	private void addTiles(Engine engine) {
		/*
		Array<Integer> randKeys = new Array<Integer>();
		randKeys.add(10);
		randKeys.add(5);
		randKeys.add(3);
		randKeys.add(6);
		randKeys.add(12);
		randKeys.add(9);
		*/
		
		byte[][] maskGrid = generator.generatePuzzle(0);
		
		for (int i = 0; i < GRID_LENGTH; ++i) {
			for (int j = 0; j < GRID_LENGTH; ++j) {
				//engine.addEntity(createTile((byte) ((int) randKeys.get(rng.nextInt(randKeys.size))), i, j));
				engine.addEntity(createTile(maskGrid[i][j], i, j));
			}
			
		}
	}

	public Entity createTile(byte mask, int ipos, int jpos) {
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
			transComp.rotation = -90f;
		}
		
		ClickComponent clickComp = new ClickComponent();
		clickComp.active = true;
		clickComp.clicker = new ClickInterface() {
			@Override
			public void onClick() {
				transComp.rotation -= 90f;
				if (transComp.rotation > 360f)
					transComp.rotation += 360f;
				pipeTileComp.mask = rotateMask(pipeTileComp.mask);
			}
		};
		clickComp.shape = new Rectangle().setSize(textureComp.size.x, textureComp.size.y).setCenter(0f, 0f);

		tile.add(pipeTileComp).add(textureComp).add(transComp).add(clickComp);

		return tile;
	}

	private byte rotateMask(byte mask) {
		byte ND = (byte) (mask << 1);
		ND = (byte) (ND + ((mask >> 3) & 1));

		return (byte)(ND%16);
	}
}
