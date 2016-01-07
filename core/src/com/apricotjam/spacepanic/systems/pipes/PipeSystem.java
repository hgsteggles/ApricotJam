package com.apricotjam.spacepanic.systems.pipes;

import com.apricotjam.spacepanic.components.ClickComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.PipeFluidComponent;
import com.apricotjam.spacepanic.components.PipeTileComponent;
import com.apricotjam.spacepanic.components.ShaderTimeComponent;
import com.apricotjam.spacepanic.components.StateComponent;
import com.apricotjam.spacepanic.components.TickerComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.systems.helmet.HelmetSystem;
import com.apricotjam.spacepanic.systems.helmet.HelmetWorld;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

public class PipeSystem extends EntitySystem {
	public static final int GRID_LENGTH = 5;
	public static final Array<GridPoint2> GridDeltas = createGridDeltas();
	private ImmutableArray<Entity> pipeFluids, pipeTiles;
	private PipeWorld world = new PipeWorld();
	private boolean startedSolutionAnimation = false;
	private float solvedFluidSpeedup = 32f;

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
	
	static public boolean withinBounds(int i, int j) {
		return !(i < 0 || i >= GRID_LENGTH || j < 0 || j >= GRID_LENGTH);
	}
	
	static public int directionFromMask(byte mask) {
		for (int i = 0; i < 4; ++i) {
			if (connectedAtIndex(mask, i))
				return i;
		}
		return -1;
	}
	
	static public byte maskFromDirection(int dir) {
		return (byte)(1 << dir);
	}
	
	static public byte rotateMask(byte mask) {
		byte ND = (byte) (mask << 1);
		ND = (byte) (ND + ((mask >> 3) & 1));

		return (byte)(ND%16);
	}
	
	static public byte rotateMaskN(byte mask, int N) {
		byte result = mask;
		for (int i = 0; i < N; ++i)
			result = rotateMask(result);
		
		return result;
	}
	
	static public int exitFromEntryDirection(byte mask, int entryDir) {
		int oppositeDir = oppositeDirectionIndex(entryDir);
		if (connectedAtIndex(mask, oppositeDir))
			return oppositeDir;
		
		int dir = (entryDir+1)%4;
		if (connectedAtIndex(mask, dir))
			return dir;
		
		dir = (entryDir+3)%4;
		if (connectedAtIndex(mask, dir))
			return dir;
		
		return oppositeDir;
	}
	
	static public void rotateTile(Entity tile) {
		PipeTileComponent pipeTileComp = ComponentMappers.pipetile.get(tile);
		TransformComponent transComp = ComponentMappers.transform.get(tile);
		
		transComp.rotation -= 90f;
		if (transComp.rotation > 360f)
			transComp.rotation += 360f;
		pipeTileComp.mask = rotateMask(pipeTileComp.mask);
	}
	
	static public String createTimerString(int t) {
		int nminutes = (int)(t/60f);
		int nseconds = (int)(t - nminutes*60);
		
		String timestring = "";
		if (nminutes < 10)
			timestring += "0";
		timestring += Integer.toString(nminutes);
		
		timestring += ":";
		
		if (nseconds < 10)
			timestring += "0";
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
				int exitDirection = directionFromMask(pipeFluidComp.exitMask);
				PipeTileComponent currPipeTileComp = ComponentMappers.pipetile.get(pipeFluidComp.parentPipe);
				Entity nextPipe = currPipeTileComp.neighbours[exitDirection];
				if (nextPipe != null) {
					PipeTileComponent nextPipeTileComp = ComponentMappers.pipetile.get(nextPipe);
					
					int entryDirection = oppositeDirectionIndex(exitDirection);
					
					if (connectedAtIndex(nextPipeTileComp.mask, entryDirection)) {
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
				int currExitDirection = directionFromMask(currPipeTileComp.mask);
				
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
						int currEntryDirection = oppositeDirectionIndex(currExitDirection);
						
						if (!connectedAtIndex(currPipeTileComp.mask, currEntryDirection))
							break;
						else
							currExitDirection = exitFromEntryDirection(currPipeTileComp.mask, currEntryDirection);
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
