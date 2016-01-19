package com.apricotjam.spacepanic.desktop;

import com.apricotjam.spacepanic.platform.PuzzleSelector;

public class DesktopPuzzleSelector implements PuzzleSelector {

	@Override
	public boolean valid(int gridSize, int turnOffs, int npipes, int nsols) {
		return nsols > 1;
	}
}
