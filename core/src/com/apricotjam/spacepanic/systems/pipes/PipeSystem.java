package com.apricotjam.spacepanic.systems.pipes;

import com.apricotjam.spacepanic.components.ClickComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.StateComponent;
import com.apricotjam.spacepanic.components.TickerComponent;
import com.apricotjam.spacepanic.components.pipe.PipeFluidComponent;
import com.apricotjam.spacepanic.components.pipe.PipeTileComponent;
import com.apricotjam.spacepanic.systems.helmet.HelmetWorld;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

public class PipeSystem extends EntitySystem {
	private ImmutableArray<Entity> pipeFluids, pipeTiles;
	private PipeWorld world;
	private boolean startedSolutionAnimation = false;
	private float solvedFluidSpeedup = 32f;
	
	private Entity masterEntity;
	
	public PipeSystem(Entity masterEntity) {
		this.masterEntity = masterEntity;
		world = new PipeWorld(masterEntity);
	}

	@Override
	public void addedToEngine(Engine engine) {
		world.build(engine);
		pipeTiles = engine.getEntitiesFor(Family.all(PipeTileComponent.class, ClickComponent.class).get());
		pipeFluids = engine.getEntitiesFor(Family.all(PipeFluidComponent.class).get());
	}

	@Override
	public void update(float deltaTime) {
		//GridPoint2 start = generator.getEntryPoint();
		//GridPoint2 end = generator.getExitPoint();
		
		for (Entity pipeFluid : pipeFluids) {
			PipeFluidComponent pipeFluidComp = ComponentMappers.pipefluid.get(pipeFluid);
			StateComponent stateComp = ComponentMappers.state.get(pipeFluid);
			pipeFluidComp.currFill += stateComp.timescale*deltaTime;
			
			if (pipeFluidComp.filling && pipeFluidComp.currFill >= pipeFluidComp.fillDuration) { // Pipe is full.
				pipeFluidComp.filling = false;
				
				// Find next pipe. If not connected, player fails; else start next pipe filling and prevent user from rotating it.
				int exitDirection = PipeWorld.directionFromMask(pipeFluidComp.exitMask);
				PipeTileComponent currPipeTileComp = ComponentMappers.pipetile.get(pipeFluidComp.parentPipe);
				Entity nextPipe = currPipeTileComp.neighbours[exitDirection];
				if (nextPipe != null) {
					PipeTileComponent nextPipeTileComp = ComponentMappers.pipetile.get(nextPipe);
					
					int entryDirection = PipeWorld.oppositeDirectionIndex(exitDirection);
					
					if (PipeWorld.connectedAtIndex(nextPipeTileComp.mask, entryDirection)) {
						// Next pipe is connected, start filling.
						Entity nextFluid = world.createFluid(nextPipe, entryDirection);
						ComponentMappers.shadertime.get(nextFluid).time = ComponentMappers.shadertime.get(pipeFluid).time;
						if (startedSolutionAnimation) {
							StateComponent nextStateComp = ComponentMappers.state.get(nextFluid);
							nextStateComp.timescale = solvedFluidSpeedup;
						}
						
						getEngine().addEntity(nextFluid);
						
						// Stop player rotating the filling pipe.
						ClickComponent clickComp = ComponentMappers.click.get(nextPipe);
						clickComp.active = false;
					}
					else {
						if (!startedSolutionAnimation)
							failed();
					}
				}
				else {
					if (!startedSolutionAnimation)
						failed();
				}
			}
		}
		
		if (!startedSolutionAnimation) {
			boolean isSolved = true;
			
			for (int ipipe = 0; ipipe < world.getEntryPoints().size; ++ipipe) {
				Entity currPipe = world.getEntryPipes().get(ipipe);
				PipeTileComponent currPipeTileComp = ComponentMappers.pipetile.get(currPipe);
				int currExitDirection = PipeWorld.directionFromMask(currPipeTileComp.mask);
				
				boolean isCurrPipeSolved = false;
				
				while (!isCurrPipeSolved) {
					currPipe = currPipeTileComp.neighbours[currExitDirection];
					if (currPipe == null)
						break;
					else if (currPipe == world.getExitPipes().get(ipipe)) {
						isCurrPipeSolved = true;
						break;
					}
					else {
						currPipeTileComp = ComponentMappers.pipetile.get(currPipe);
						int currEntryDirection = PipeWorld.oppositeDirectionIndex(currExitDirection);
						
						if (!PipeWorld.connectedAtIndex(currPipeTileComp.mask, currEntryDirection))
							break;
						else
							currExitDirection = PipeWorld.exitFromEntryDirection(currPipeTileComp.mask, currEntryDirection);
					}
				}
				
				if (!isCurrPipeSolved) {
					isSolved = false;
					break;
				}	
			}
			
			if (isSolved) {
				startedSolutionAnimation = true;
				solved();
			}
		}
	}
	
	public void solved() {
		// Prevent user from rotating tiles.
		for (Entity pipeTile : pipeTiles) {
			ClickComponent clickComp = ComponentMappers.click.get(pipeTile);
			clickComp.active = false;
		}
		// Speed up the fluid filling.
		for (Entity pipeFluid : pipeFluids) {
			StateComponent stateComp = ComponentMappers.state.get(pipeFluid);
			stateComp.timescale = solvedFluidSpeedup;
		}
		// Show connection LED text.
		getEngine().addEntity(HelmetWorld.createAppearLED("SUCCESS"));
		// Stop timer;
		TickerComponent timerTickerComp = ComponentMappers.ticker.get(world.getTimer());
		timerTickerComp.tickerActive = false;
		timerTickerComp.finishActive = false;
	}
	
	public void failed() {
		// Prevent user from rotating tiles.
		for (Entity pipeTile : pipeTiles) {
			ClickComponent clickComp = ComponentMappers.click.get(pipeTile);
			clickComp.active = false;
		}
		// Show connection LED text.
		getEngine().addEntity(HelmetWorld.createFlashLED("ERROR"));
		// Stop timer;
		TickerComponent timerTickerComp = ComponentMappers.ticker.get(world.getTimer());
		timerTickerComp.tickerActive = false;
		timerTickerComp.finishActive = false;
	}
}
