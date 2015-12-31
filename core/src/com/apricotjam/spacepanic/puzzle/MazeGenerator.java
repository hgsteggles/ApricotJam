package com.apricotjam.spacepanic.puzzle;

import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;

public class MazeGenerator {

	private final long seed;
	private RandomXS128 rng = new RandomXS128(0);

	private int patchWidth;
	private int patchHeight;

	private static final int UNDETERMINED = 0;
	public static final int PATH = 1;
	public static final int WALL = 2;

	public MazeGenerator(int patchWidth, int patchHeight) {
		this(System.nanoTime(), patchWidth, patchHeight);
	}

	public MazeGenerator(long seed, int patchWidth, int patchHeight) {
		this.seed = seed;
		this.patchWidth = patchWidth;
		this.patchHeight = patchHeight;
	}

	public int[][] createPatch(int x, int y) {
		int[][] patch = new int[patchWidth + 1][patchHeight + 1];
		for (int i = 0; i < patchWidth + 1; i++) {
			for (int j = 0; j < patchHeight + 1; j++) {
				patch[i][j] = PATH;
			}
		}

		int[] thisBounds = createPatchBoundary(x, y);
		int[] topBounds = createPatchBoundary(x, y + 1);
		int[] topRightBounds = createPatchBoundary(x + 1, y + 1);
		int[] rightBounds = createPatchBoundary(x + 1, y);

		for (int i = 0; i < patchHeight; i++) {
			patch[0][i] = thisBounds[patchHeight - 1 - i];
			patch[patchWidth][i] = rightBounds[patchHeight - 1 - i];
		}
		for (int i = 0; i < patchWidth - 1; i++) {
			patch[i + 1][0] = thisBounds[patchHeight + i];
			patch[i + 1][patchHeight] = topBounds[patchHeight + i];
		}
		patch[0][patchHeight] = topBounds[patchHeight - 1];
		patch[patchWidth][patchHeight] = topRightBounds[patchHeight - 1];

		printPatch(patch);
		return patch;
	}

	public int[] createPatchBoundary(int x, int y) {
		int nCells = patchHeight + patchWidth - 1;
		int[] bound = new int[nCells];
		setRandomStateBoundary(x, y);
		for (int i = 0; i < nCells; ++i) {
			if (rng.nextBoolean()) {
				bound[i] = PATH;
			} else {
				bound[i] = WALL;
			}
		}
		return bound;
	}

	public void printPatch(int[][] patch) {
		for (int j = patchHeight; j >= 0; j--) {
			for (int i = 0; i < patchWidth + 1; i++) {
				if(patch[i][j] == WALL) {
					System.out.print("X");
				} else {
					System.out.print(".");
				}
			}
			System.out.print("\n");
		}
	}

	// Coordinates get +0.5 to prevent non-random behaviour at zero
	private void setRandomStateBoundary(int x, int y) {
		rng.setState((int)((x + 0.5) * seed), (int)((y + 0.5) * seed));
	}


}
