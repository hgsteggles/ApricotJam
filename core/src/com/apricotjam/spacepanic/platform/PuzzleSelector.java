package com.apricotjam.spacepanic.platform;

public interface PuzzleSelector {
	public boolean valid(int gridSize, int turnOffs, int npipes, int nsols);
}
