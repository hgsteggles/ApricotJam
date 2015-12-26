package com.apricotjam.spacepanic.puzzle;

import com.apricotjam.spacepanic.systems.PipeSystem;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;

public class PipePuzzleGenerator {
	private RandomXS128 rng = new RandomXS128(0);
	private byte[][] maskGrid = new byte[PipeSystem.GRID_LENGTH][PipeSystem.GRID_LENGTH];
	private int turnOffCounter = 0;
	
	private GridPoint2 start = new GridPoint2(0, 0);
	private GridPoint2 end = new GridPoint2(PipeSystem.GRID_LENGTH - 1, PipeSystem.GRID_LENGTH - 1);
	
	public byte[][] generatePuzzle(int difficulty) {
		// TODO: modify generation parameters to reflect difficulty.
		
		resetMaskGrid();
		turnOffCounter = 3;
		
		maskGrid[start.x][start.y] = PipeSystem.connectAtIndex(maskGrid[start.x][start.y], 1);
		maskGrid[end.x][end.y] = PipeSystem.connectAtIndex(maskGrid[end.x][end.y], 3);
		
		updateMask(start.x, start.y, start.x+1, start.y);
		
		return maskGrid;
	}
	
	private void resetMaskGrid() {
		for (int i = 0; i < PipeSystem.GRID_LENGTH; ++i) {
			for (int j = 0; j < PipeSystem.GRID_LENGTH; ++j) {
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
		
		if (i == end.x && j == end.y) {
			if (!PipeSystem.connectedAtIndex(maskGrid[i][j], parentDirection) || turnOffCounter != 0)
				return false;
			else
				return true;
		}
		if (PipeSystem.connectedAtIndex(maskGrid[i][j], PipeSystem.opppositeDirectionIndex(parentDirection)))
			return false;
		maskGrid[i][j] = PipeSystem.connectAtIndex(maskGrid[i][j], parentDirection);
		
		// Possible routes.
		Array<Integer> routes = new Array<Integer>();
		
		for (int idir = 0; idir < 4; ++idir) {
			if (!PipeSystem.connectedAtIndex(maskGrid[i][j], idir)) { // If already connected, can't go down this route.
				int inew = i + PipeSystem.GridDeltas.get(idir).x;
				int jnew = j + PipeSystem.GridDeltas.get(idir).y;
				
				if (inew >= 0 && inew < PipeSystem.GRID_LENGTH && jnew >= 0 && jnew < PipeSystem.GRID_LENGTH) {
					boolean isStart = (inew == start.x) && (jnew == start.y);
					if (!isStart) { // Pipe cannot connect to start tile.
						// Check if pipe can turn off from a direct route to exit.
						if ((inew - i > 0 && end.x - i <= 0) || (inew - i < 0 && end.x - i >= 0) ||
							(jnew - j > 0 && end.y - j <= 0) || (jnew - j < 0 && end.y - j >= 0)) {
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
			int inew = i + PipeSystem.GridDeltas.get(route).x;
			int jnew = j + PipeSystem.GridDeltas.get(route).y;
			
			if ((inew - i > 0 && end.x - i <= 0) || (inew - i < 0 && end.x - i >= 0) ||
					(jnew - j > 0 && end.y - j <= 0) || (jnew - j < 0 && end.y - j >= 0)) {
				turnOffCounter -= 1;
			}
			
			maskGrid[i][j] = PipeSystem.connectAtIndex(maskGrid[i][j], route);
			connected = updateMask(i, j, inew, jnew);
			
			if (connected)
				break;
			else {
				if ((inew - i > 0 && end.x - i <= 0) || (inew - i < 0 && end.x - i >= 0) ||
						(jnew - j > 0 && end.y - j <= 0) || (jnew - j < 0 && end.y - j >= 0)) {
					turnOffCounter += 1;
				}
				maskGrid[i][j] = PipeSystem.disconnectAtIndex(maskGrid[i][j], route);
			}
		}
		
		if (!connected)
			maskGrid[i][j] = PipeSystem.disconnectAtIndex(maskGrid[i][j], parentDirection);
		
		return connected;
	}
}
