package com.apricotjam.spacepanic;

import com.apricotjam.spacepanic.platform.PuzzleSelector;

public class AndroidPuzzleSelector implements PuzzleSelector {

	@Override
	public boolean valid(int gridSize, int turnOffs, int npipes, int nsols) {
		return nsols > 1 && gridSize < 6 && npipes < 4;
	}

}
