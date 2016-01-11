package com.apricotjam.spacepanic.systems.pipes;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;

public class PipePuzzleGenerator {
	private RandomXS128 rng = new RandomXS128(0);
	private byte[][] maskGrid = new byte[PipeWorld.GRID_LENGTH][PipeWorld.GRID_LENGTH];
	private int turnOffCounter = 0;
	private byte[] randomMasks = createRandomMasks();
	
	private Array<GridPoint2> starts = new Array<GridPoint2>();
	private Array<GridPoint2> ends = new Array<GridPoint2>();
	private int currPipe = 0;
	
	private int length = 2;
	
	public void generatePuzzle(int difficulty, int npipes) {
		// Difficulty from 1 to 10;
		// TODO: modify generation parameters to reflect difficulty.
		
		if (npipes == 1) {
			starts.add(new GridPoint2(-1, PipeWorld.GRID_LENGTH - 1));
			ends.add(new GridPoint2(PipeWorld.GRID_LENGTH, PipeWorld.GRID_LENGTH - 1));
		}
		else if (npipes == 2) {
			starts.add(new GridPoint2(-1, PipeWorld.GRID_LENGTH - 4));
			ends.add(new GridPoint2(PipeWorld.GRID_LENGTH, 0));
			starts.add(new GridPoint2(-1, PipeWorld.GRID_LENGTH - 2));
			ends.add(new GridPoint2(PipeWorld.GRID_LENGTH, PipeWorld.GRID_LENGTH - 1));
		}
		else if (npipes == 3) {
			starts.add(new GridPoint2(-1, PipeWorld.GRID_LENGTH - 4));
			ends.add(new GridPoint2(PipeWorld.GRID_LENGTH, 0));
			starts.add(new GridPoint2(-1, PipeWorld.GRID_LENGTH - 2));
			ends.add(new GridPoint2(PipeWorld.GRID_LENGTH, PipeWorld.GRID_LENGTH - 1));
			starts.add(new GridPoint2(-1, PipeWorld.GRID_LENGTH - 3));
			ends.add(new GridPoint2(PipeWorld.GRID_LENGTH, PipeWorld.GRID_LENGTH - 3));
		}
		else {
			starts.add(new GridPoint2(-1, PipeWorld.GRID_LENGTH - 4));
			ends.add(new GridPoint2(PipeWorld.GRID_LENGTH, 0));
			starts.add(new GridPoint2(-1, PipeWorld.GRID_LENGTH - 2));
			ends.add(new GridPoint2(PipeWorld.GRID_LENGTH, PipeWorld.GRID_LENGTH - 3));
			starts.add(new GridPoint2(-1, PipeWorld.GRID_LENGTH - 3));
			ends.add(new GridPoint2(PipeWorld.GRID_LENGTH, PipeWorld.GRID_LENGTH - 4));
			starts.add(new GridPoint2(-1, PipeWorld.GRID_LENGTH - 1));
			ends.add(new GridPoint2(PipeWorld.GRID_LENGTH, PipeWorld.GRID_LENGTH - 2));
		}
		
		resetMaskGrid();
		turnOffCounter = Math.min(difficulty, 9);
		
		boolean done = updateMask(starts.get(0).x, starts.get(0).y, starts.get(0).x + 1, starts.get(0).y);
		
		if (!done) {
			System.out.println("PipePuzzleGenerator::generatePuzzle: unable to create puzzle.");
			System.exit(0);
		}
		
		// Fill out the rest.
		for (int i = 0; i < PipeWorld.GRID_LENGTH; ++i) {
			for (int j = 0; j < PipeWorld.GRID_LENGTH; ++j) {
				if (maskGrid[i][j] == 0) {
					maskGrid[i][j] = randomMasks[rng.nextInt(randomMasks.length)];
				}
			}	
		}
	}
	
	public byte[][] getMaskGrid() {
		return maskGrid;
	}
	
	public Array<GridPoint2> getEntryPoints() {
		return starts;
	}
	
	public Array<GridPoint2> getExitPoints() {
		return ends;
	}
	
	public int getSolutionLength() {
		return length;
	}
	
	private void resetMaskGrid() {
		currPipe = 0;
		length = 2;
		for (int i = 0; i < PipeWorld.GRID_LENGTH; ++i) {
			for (int j = 0; j < PipeWorld.GRID_LENGTH; ++j) {
				maskGrid[i][j] = 0;
			}
		}
	}
	
	private boolean updateMask(int parent_i, int parent_j, int i, int j) {
		// Connect to parent after checking if possible.
		int parentDirection;
		
		if (parent_i < i)
			parentDirection = 3;
		else if (parent_i > i)
			parentDirection = 1;
		else if (parent_j < j)
			parentDirection = 2;
		else
			parentDirection = 0;
		
		if (i == ends.get(currPipe).x && j == ends.get(currPipe).y) {
			if (!PipeWorld.connectedAtIndex((byte)(8), parentDirection) || turnOffCounter != 0)
				return false;
			else {
				if (currPipe == starts.size - 1)
					return true;
				else {
					currPipe += 1;
					if (!updateMask(starts.get(currPipe).x, starts.get(currPipe).y, starts.get(currPipe).x + 1, starts.get(currPipe).y)) {
						currPipe -= 1;
						return false;
					}
					else
						return true;
				}
			}
		}
		if (PipeWorld.connectedAtIndex(maskGrid[i][j], PipeWorld.oppositeDirectionIndex(parentDirection)))
			return false;
		maskGrid[i][j] = PipeWorld.connectAtIndex(maskGrid[i][j], parentDirection);
		
		// Possible routes.
		Array<Integer> routes = new Array<Integer>();
		
		for (int idir = 0; idir < 4; ++idir) {
			if (!PipeWorld.connectedAtIndex(maskGrid[i][j], idir)) { // If already connected, can't go down this route.
				int inew = i + PipeWorld.GridDeltas.get(idir).x;
				int jnew = j + PipeWorld.GridDeltas.get(idir).y;
				
				if (PipeWorld.withinBounds(inew, jnew) || (inew == ends.get(currPipe).x && jnew == ends.get(currPipe).y)) {
					boolean isStart = (inew == starts.get(currPipe).x) && (jnew == starts.get(currPipe).y);
					if (!isStart) { // Pipe cannot connect to start tile.
						// Check if pipe can turn off from a direct route to exit.
						if ((inew - i > 0 && ends.get(currPipe).x - i <= 0) || (inew - i < 0 && ends.get(currPipe).x - i >= 0) ||
							(jnew - j > 0 && ends.get(currPipe).y - j <= 0) || (jnew - j < 0 && ends.get(currPipe).y - j >= 0)) {
							if (turnOffCounter > 0)
								routes.add(idir);
						}
						else {
							routes.add(idir);
						}
					}
				}
			}
		}
		routes.shuffle();
		
		boolean connected = false;
		for (Integer route : routes) {
			int inew = i + PipeWorld.GridDeltas.get(route).x;
			int jnew = j + PipeWorld.GridDeltas.get(route).y;
			
			if ((inew - i > 0 && ends.get(currPipe).x - i <= 0) || (inew - i < 0 && ends.get(currPipe).x - i >= 0) ||
					(jnew - j > 0 && ends.get(currPipe).y - j <= 0) || (jnew - j < 0 && ends.get(currPipe).y - j >= 0)) {
				turnOffCounter -= 1;
			}
			
			maskGrid[i][j] = PipeWorld.connectAtIndex(maskGrid[i][j], route);
			
			length += 1;
			connected = updateMask(i, j, inew, jnew);
			
			if (connected)
				break;
			else {
				length -= 1;
				if ((inew - i > 0 && ends.get(currPipe).x - i <= 0) || (inew - i < 0 && ends.get(currPipe).x - i >= 0) ||
						(jnew - j > 0 && ends.get(currPipe).y - j <= 0) || (jnew - j < 0 && ends.get(currPipe).y - j >= 0)) {
					turnOffCounter += 1;
				}
				maskGrid[i][j] = PipeWorld.disconnectAtIndex(maskGrid[i][j], route);
			}
		}
		
		if (!connected) {
			maskGrid[i][j] = PipeWorld.disconnectAtIndex(maskGrid[i][j], parentDirection);
		}
		
		return connected;
	}
	
	static private byte[] createRandomMasks() {
		byte[] masks = new byte[7];
		masks[0] = 3;
		masks[1] = 6;
		masks[2] = 12;
		masks[3] = 9;
		masks[4] = 5;
		masks[5] = 10;
		masks[6] = 15;
		
		return masks;
	}
}
