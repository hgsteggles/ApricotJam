package com.apricotjam.spacepanic.systems.maze;

import com.badlogic.gdx.math.RandomXS128;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MazeGenerator {

	public static final int PATH = 0;
	public static final int WALL = 1;
	public static final int UNEXPOSED = 2;
	private static final int UNDETERMINED = 3;

	private final long seed;
	private RandomXS128 rng = new RandomXS128(0);

	private int patchWidth;
	private int patchHeight;
	private float boundaryPathChance;

	public MazeGenerator(int patchWidth, int patchHeight, float boundaryPathChance) {
		this(System.nanoTime(), patchWidth, patchHeight, boundaryPathChance);
	}

	public MazeGenerator(long seed, int patchWidth, int patchHeight, float boundaryPathChance) {
		System.out.println("Seed: " + seed);
		this.seed = seed;
		this.patchWidth = patchWidth;
		this.patchHeight = patchHeight;
		this.boundaryPathChance = boundaryPathChance;
	}

	public int[][] createPatch(int x, int y) {
		if (x == 0 && y == 0) {
			return createHomePatch();
		}
		int[][] patch = new int[patchWidth + 1][patchHeight + 1];
		int[][] connectivity = new int[patchWidth + 1][patchHeight + 1];
		for (int i = 0; i < patchWidth + 1; i++) {
			for (int j = 0; j < patchHeight + 1; j++) {
				patch[i][j] = UNEXPOSED;
				connectivity[i][j] = 0;
			}
		}

		int[] thisBounds = createPatchBoundary(x, y);
		int[] topBounds = createPatchBoundary(x, y + 1);
		int[] topRightBounds = createPatchBoundary(x + 1, y + 1);
		int[] rightBounds = createPatchBoundary(x + 1, y);

		ArrayList<Point> exposed = new ArrayList<Point>();

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

		for (int i = 0; i < patchWidth + 1; i++) {
			for (int j = 0; j < patchHeight + 1; j++) {
				if (patch[i][j] == PATH) {
					int side = 0;
					if (i == 0) {
						side = 4;
					} else if (i == patchWidth) {
						side = 2;
					} if (j == 0) {
						side = 3;
					} else if (j == patchHeight) {
						side = 1;
					}
					connectivity[i][j] = side;
					createPath(i, j, patch, connectivity, exposed);
				}
			}
		}

		while (exposed.size() > 0) {
			int index = rng.nextInt(exposed.size());
			Point choice = exposed.get(index);
			if (validPath(choice.x, choice.y, patch)) {
				createPath(choice.x, choice.y, patch, connectivity, exposed);
			} else {
				patch[choice.x][choice.y] = WALL;
			}
			exposed.remove(choice);
		}

		//Clean up connectivity
		for (int i = 1; i < patchWidth; i++) {
			for (int j = 1; j < patchHeight; j++) {
				if (patch[i][j] != PATH) {
					connectivity[i][j] = 0;
				}
			}
		}

		//Find potential connection points
		HashMap<Integer, ArrayList<Point>> connections = new HashMap<Integer, ArrayList<Point>>();
		for (int i = 1; i < patchWidth; i++) {
			for (int j = 1; j < patchHeight; j++) {
				if (patch[i][j] != WALL) {
					continue;
				}
				boolean[] connected = {false, false, false, false, false};
				connected[connectivity[i - 1][j]] = true;
				connected[connectivity[i + 1][j]] = true;
				connected[connectivity[i][j - 1]] = true;
				connected[connectivity[i][j + 1]] = true;

				int sideMask = 0;
				for (int n = 1; n < connected.length; n++) {
					if (connected[n]) {
						sideMask += 1 << (n - 1);
					}
				}

				if (sideMask != 0 && sideMask != 1 && sideMask != 2 && sideMask != 4 && sideMask != 8) {
					if (!connections.containsKey(sideMask)) {
						connections.put(sideMask, new ArrayList<Point>());
					}
					connections.get(sideMask).add(new Point(i, j));
				}

			}
		}
		
		//Cut connections
		for (int i : connections.keySet()) {
			int index = rng.nextInt(connections.get(i).size());
			Point choice = connections.get(i).get(index);
			patch[choice.x][choice.y] = PATH;
		}

		//Remove extra boundaries
		int[][] trimmedPatch = new int[patchWidth][patchHeight];
		for (int i = 0; i < patchWidth; i++) {
			for (int j = 0; j < patchHeight; j++) {
				trimmedPatch[i][j] = patch[i][j];
			}
		}

		return trimmedPatch;
	}

	public int[][] createHomePatch() {
		int[][] patch = new int[patchWidth][patchHeight];
		for (int i = 0; i < patchWidth; i++) {
			for (int j = 0; j < patchHeight; j++) {
				patch[i][j] = PATH;
			}
		}
		int[] thisBounds = createPatchBoundary(0, 0);

		ArrayList<Point> exposed = new ArrayList<Point>();

		for (int i = 0; i < patchHeight; i++) {
			patch[0][i] = thisBounds[patchHeight - 1 - i];
		}
		for (int i = 0; i < patchWidth - 1; i++) {
			patch[i + 1][0] = thisBounds[patchHeight + i];
		}
		return patch;
	}

	private void createPath(int i, int j, int[][] patch, int[][] connectivity, ArrayList<Point> exposed) {
		patch[i][j] = PATH;
		int side = connectivity[i][j];

		if (getCell(i - 1, j, patch) == UNEXPOSED) {
			patch[i - 1][j] = UNDETERMINED;
			connectivity[i - 1][j] = side;
			exposed.add(new Point(i - 1, j));
		}

		if (getCell(i + 1, j, patch) == UNEXPOSED) {
			patch[i + 1][j] = UNDETERMINED;
			connectivity[i + 1][j] = side;
			exposed.add(new Point(i + 1, j));
		}

		if (getCell(i, j - 1, patch) == UNEXPOSED) {
			patch[i][j - 1] = UNDETERMINED;
			connectivity[i][j - 1] = side;
			exposed.add(new Point(i, j - 1));
		}
		if (getCell(i, j + 1, patch) == UNEXPOSED) {
			patch[i][j + 1] = UNDETERMINED;
			connectivity[i][j + 1] = side;
			exposed.add(new Point(i, j + 1));
		}
	}

	private boolean validPath(int i, int j, int[][] patch) {
		int edgeState = 0;

		if (getCell(i - 1, j, patch) == PATH) {
			edgeState += 1;
		}

		if (getCell(i + 1, j, patch) == PATH) {
			edgeState += 2;
		}

		if (getCell(i, j - 1, patch) == PATH) {
			edgeState += 4;
		}

		if (getCell(i, j + 1, patch) == PATH) {
			edgeState += 8;
		}

		if (edgeState == 1) {
			if (getCell(i + 1, j - 1, patch) == PATH)
				return false;
			if (getCell(i + 1, j + 1, patch) == PATH)
				return false;

			return true;
		} else if (edgeState == 2) {
			if (getCell(i - 1, j - 1, patch) == PATH)
				return false;
			if (getCell(i - 1, j + 1, patch) == PATH)
				return false;
			return true;
		} else if (edgeState == 4) {
			if (getCell(i - 1, j + 1, patch) == PATH)
				return false;
			if (getCell(i + 1, j + 1, patch) == PATH)
				return false;
			return true;
		} else if (edgeState == 8) {
			if (getCell(i - 1, j - 1, patch) == PATH)
				return false;
			if (getCell(i + 1, j - 1, patch) == PATH)
				return false;
			return true;
		}
		return false;
	}

	private int getCell(int i, int j, int[][] patch) {
		if (i < 0 || i > patchWidth || j < 0 || j > patchHeight) {
			return -1;
		} else {
			return patch[i][j];
		}
	}

	public int[] createPatchBoundary(int x, int y) {
		int nCells = patchHeight + patchWidth - 1;
		int[] bound = new int[nCells];
		setRandomState(x, y);
		for (int i = 0; i < nCells; ++i) {
			if (rng.nextFloat() <= boundaryPathChance) {
				bound[i] = PATH;
			} else {
				bound[i] = WALL;
			}
		}
		return bound;
	}

	public void printPatch(int[][] patch) {
		for (int j = patchHeight -1; j >= 0; j--) {
			for (int i = 0; i < patchWidth; i++) {
				if (patch[i][j] == PATH) {
					System.out.print(".");
				} else {
					System.out.print("X");
				}
			}
			System.out.print("\n");
		}
	}

	public void printPatch(int[][] patch, int[][] connectivity) {
		for (int j = patchHeight - 1; j >= 0; j--) {
			for (int i = 0; i < patchWidth; i++) {
				if (patch[i][j] == PATH) {
					System.out.print(connectivity[i][j]);
				} else {
					System.out.print(".");
				}
			}
			System.out.print("\n");
		}
	}

	private void setRandomState(long x, long y) {
		rng.setState(seed + x, seed + y);
	}

	public int getPatchWidth() {
		return patchWidth;
	}

	public int getPatchHeight() {
		return patchHeight;
	}

}
