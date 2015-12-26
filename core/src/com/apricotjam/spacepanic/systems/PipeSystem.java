package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.PipeTileComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;

public class PipeSystem extends EntitySystem {
	public static final int GRID_LENGTH = 5;
	private ImmutableArray<Entity> pipeTiles;
	private RandomXS128 rng = new RandomXS128(0);

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

	@Override
	public void addedToEngine(Engine engine) {
		addTiles(engine);
		pipeTiles = engine.getEntitiesFor(Family.all(PipeTileComponent.class).get());
	}

	@Override
	public void update(float deltaTime) {

	}

	private void addTiles(Engine engine) {
		Array<Integer> randKeys = new Array<Integer>();
		randKeys.add(10);
		randKeys.add(5);
		randKeys.add(3);
		randKeys.add(6);
		randKeys.add(12);
		randKeys.add(9);

		for (int i = 0; i < GRID_LENGTH; ++i) {
			for (int j = 0; j < GRID_LENGTH; ++j) {
				engine.addEntity(createTile((byte) ((int) randKeys.get(rng.nextInt(randKeys.size))), i, j));
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
		float tileWidth = 1f;
		float tileHeight = 1f;
		float gridOffsetX = BasicScreen.WORLD_WIDTH / 2f - GRID_LENGTH * tileWidth / 2f;
		float gridOffsetY = BasicScreen.WORLD_HEIGHT / 2f - GRID_LENGTH * tileHeight / 2f;
		transComp.position.set(gridOffsetX + 0.5f * (2 * ipos + 1) * tileWidth, gridOffsetY + 0.5f * (2 * jpos + 1) * tileHeight, 0);

		if (mask == (byte) (10)) {
			transComp.rotation = 90f;
		}

		tile.add(pipeTileComp).add(textureComp).add(transComp);

		return tile;
	}

	private byte rotateMask(byte mask) {
		byte ND = (byte) (mask << 1);
		ND = (byte) (ND + ((mask >> 3) & 1));

		return mask;
	}
}
