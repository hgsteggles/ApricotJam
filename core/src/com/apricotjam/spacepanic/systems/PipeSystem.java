package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.PipeTileComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.RandomXS128;
import com.sun.xml.internal.ws.api.pipe.Pipe;

public class PipeSystem extends EntitySystem {
	private ImmutableArray<Entity> pipeTiles;
	private RandomXS128 rng = new RandomXS128(0);
	
	@Override
	public void addedToEngine(Engine engine) {
		addTiles(engine);
		pipeTiles = engine.getEntitiesFor(Family.all(PipeTileComponent.class).get());
	}

	@Override
	public void update(float deltaTime) {
		
	}
	
	private void addTiles(Engine engine) {
		
	}
	
	private Entity createTile() {
		Entity tile = new Entity();
		PipeTileComponent pipeTileComp = new PipeTileComponent();
		pipeTileComp.fillDuration = 4f;
		pipeTileComp.currFill = 0;
		pipeTileComp.filling = false;
		pipeTileComp.connectedLeft = rng.nextBoolean();
		pipeTileComp.connectedRight = rng.nextBoolean();
		pipeTileComp.connectedUp = rng.nextBoolean();
		pipeTileComp.connectedDown = rng.nextBoolean();
		
		return tile;
	}
	
	private void rotateTile(Entity entity) {
		PipeTileComponent pipeTileComp = ComponentMappers.pipetile.get(entity);
		
		boolean tmp = pipeTileComp.connectedDown;
		pipeTileComp.connectedDown = pipeTileComp.connectedRight;
		pipeTileComp.connectedRight = pipeTileComp.connectedUp;
		pipeTileComp.connectedUp = pipeTileComp.connectedLeft;
		pipeTileComp.connectedLeft = tmp;
		
		// TODO: rotate textureregion.
	}
}
