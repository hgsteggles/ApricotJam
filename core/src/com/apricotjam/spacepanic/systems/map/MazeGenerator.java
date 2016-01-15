package com.apricotjam.spacepanic.systems.map;

import com.badlogic.gdx.math.RandomXS128;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MazeGenerator {

	public static final int PATH = 0;
	public static final int WALL = 1;
	private static final int UNEXPOSED = 2;
	private static final int UNDETERMINED = 3;

	private final long seed;
	private RandomXS128 rng = new RandomXS128(0);

	private float boundaryPathChance;
	private float blockChance;

	/**
	 * Creates a new maze generator
	 *
	 * @param boundaryPathChance Probability that a cell on the boundary will be a path, 0.5 seems good
	 * @param blockChance        Probability it will try and block connections to top and right patches
	 *                           Will always leave at least one, although this might not be connected on the other side
	 *                           1.0 is quite hard but still playable, 0.0 is very easy (In regards to pathing)
	 */
	public MazeGenerator(long seed, float boundaryPathChance, float blockChance) {
		this.seed = seed;
		this.boundaryPathChance = boundaryPathChance;
		this.blockChance = blockChance;
	}

	public int[][] createPatch(int x, int y) {
		if (x == 0 && y == 0) {
			return createHomePatch();
		}

		//Create edges
		int[] thisBounds = createPatchBoundary(x, y);
		int[] topBounds = createPatchBoundary(x, y + 1);
		int[] topRightBounds = createPatchBoundary(x + 1, y + 1);
		int[] rightBounds = createPatchBoundary(x + 1, y);

		//Create centre
		int[][] centrePatch = new int[Patch.PATCH_WIDTH - 1][Patch.PATCH_HEIGHT - 1];
		ArrayList<Point> exposed = new ArrayList<Point>();
		for (int i = 0; i < Patch.PATCH_WIDTH - 1; i++) {
			for (int j = 0; j < Patch.PATCH_HEIGHT - 1; j++) {
				centrePatch[i][j] = UNEXPOSED;
			}
		}
		int istart = rng.nextInt(Patch.PATCH_WIDTH - 1);
		int jstart = rng.nextInt(Patch.PATCH_HEIGHT - 1);
		createPath(istart, jstart, centrePatch, exposed, Patch.PATCH_WIDTH - 1, Patch.PATCH_HEIGHT - 1);
		while (exposed.size() > 0) {
			int index = rng.nextInt(exposed.size());
			Point choice = exposed.get(index);
			if (validPath(choice.x, choice.y, centrePatch, Patch.PATCH_WIDTH - 1, Patch.PATCH_HEIGHT - 1)) {
				createPath(choice.x, choice.y, centrePatch, exposed, Patch.PATCH_WIDTH - 1, Patch.PATCH_HEIGHT - 1);
			} else {
				centrePatch[choice.x][choice.y] = WALL;
			}
			exposed.remove(choice);
		}

		//Combine, noting down which part it is from in the connectivity array
		int[][] patch = new int[Patch.PATCH_WIDTH + 1][Patch.PATCH_HEIGHT + 1];
		int[][] connectivity = new int[Patch.PATCH_WIDTH + 1][Patch.PATCH_HEIGHT + 1];
		for (int i = 0; i < Patch.PATCH_WIDTH + 1; i++) {
			for (int j = 0; j < Patch.PATCH_HEIGHT + 1; j++) {
				connectivity[i][j] = 0;
				patch[i][j] = PATH;
				if (i == 0 && j < Patch.PATCH_HEIGHT) {
					patch[i][j] = thisBounds[Patch.PATCH_HEIGHT - 1 - j];
					if (patch[i][j] == PATH) {
						connectivity[i][j] = 1;
					}
				} else if (i == Patch.PATCH_WIDTH && j < Patch.PATCH_HEIGHT) {
					patch[i][j] = rightBounds[Patch.PATCH_HEIGHT - 1 - j];
					if (patch[i][j] == PATH) {
						connectivity[i][j] = 2;
					}
				} else if (j == 0 && i < Patch.PATCH_WIDTH) {
					patch[i][j] = thisBounds[Patch.PATCH_HEIGHT - 1 + i];
					if (patch[i][j] == PATH) {
						connectivity[i][j] = 3;
					}
				} else if (j == Patch.PATCH_HEIGHT && i < Patch.PATCH_WIDTH) {
					patch[i][j] = topBounds[Patch.PATCH_HEIGHT - 1 + i];
					if (patch[i][j] == PATH) {
						connectivity[i][j] = 4;
					}
				} else if (i == Patch.PATCH_WIDTH && j == Patch.PATCH_HEIGHT) {
					patch[i][j] = topRightBounds[Patch.PATCH_HEIGHT - 1];
					if (patch[i][j] == PATH) {
						connectivity[i][j] = 2;
					}
				} else {
					patch[i][j] = centrePatch[i - 1][j - 1];
					if (patch[i][j] == PATH) {
						connectivity[i][j] = 5;
					}
				}
			}
		}

		if (blockChance > 0.0f) {
			blockConnections(patch, connectivity);
		}

		//Remove extra boundaries
		int[][] trimmedPatch = new int[Patch.PATCH_WIDTH][Patch.PATCH_HEIGHT];
		for (int i = 0; i < Patch.PATCH_WIDTH; i++) {
			for (int j = 0; j < Patch.PATCH_HEIGHT; j++) {
				trimmedPatch[i][j] = patch[i][j];
			}
		}

		return trimmedPatch;
	}

	public int[][] createHomePatch() {
		int[][] patch = new int[Patch.PATCH_WIDTH][Patch.PATCH_HEIGHT];
		for (int i = 0; i < Patch.PATCH_WIDTH; i++) {
			for (int j = 0; j < Patch.PATCH_HEIGHT; j++) {
				patch[i][j] = PATH;
			}
		}
		int[] thisBounds = createPatchBoundary(0, 0);

		ArrayList<Point> exposed = new ArrayList<Point>();

		for (int i = 0; i < Patch.PATCH_HEIGHT; i++) {
			patch[0][i] = thisBounds[Patch.PATCH_HEIGHT - 1 - i];
		}
		for (int i = 0; i < Patch.PATCH_WIDTH - 1; i++) {
			patch[i + 1][0] = thisBounds[Patch.PATCH_HEIGHT + i];
		}
		return patch;
	}

	private void blockConnections(int[][] patch, int[][] connectivity) {
		//Find connection points
		HashMap<Integer, ArrayList<Point>> connections = new HashMap<Integer, ArrayList<Point>>();
		for (int i = 1; i < Patch.PATCH_WIDTH; i++) {
			for (int j = 1; j < Patch.PATCH_HEIGHT; j++) {
				if (connectivity[i][j] != 5) {
					continue;
				}
				boolean[] connected = {false, false, false, false, false, false};
				connected[connectivity[i - 1][j]] = true;
				connected[connectivity[i + 1][j]] = true;
				connected[connectivity[i][j - 1]] = true;
				connected[connectivity[i][j + 1]] = true;

				int sideMask = 0;
				for (int n = 1; n <= 4; n++) {
					if (connected[n]) {
						sideMask += 1 << n;
					}
				}

				if (sideMask == 4 || sideMask == 16) {
					if (!connections.containsKey(sideMask)) {
						connections.put(sideMask, new ArrayList<Point>());
					}
					connections.get(sideMask).add(new Point(i, j));
				}

			}
		}

		//Block connections
		for (int i : connections.keySet()) {
			ArrayList<Point> connectionList = connections.get(i);
			while (connectionList.size() > 1) {
				if (rng.nextFloat() <= blockChance) {
					int index = rng.nextInt(connectionList.size());
					Point choice = connectionList.get(index);
					patch[choice.x][choice.y] = WALL;
					connectivity[choice.x][choice.y] = 6;
					connectionList.remove(choice);
				} else {
					break;
				}
			}
		}
	}

	private void createPath(int i, int j, int[][] maze, ArrayList<Point> exposed, int width, int height) {
		maze[i][j] = PATH;

		if (getCell(i - 1, j, maze, width, height) == UNEXPOSED) {
			maze[i - 1][j] = UNDETERMINED;
			exposed.add(new Point(i - 1, j));
		}

		if (getCell(i + 1, j, maze, width, height) == UNEXPOSED) {
			maze[i + 1][j] = UNDETERMINED;
			exposed.add(new Point(i + 1, j));
		}

		if (getCell(i, j - 1, maze, width, height) == UNEXPOSED) {
			maze[i][j - 1] = UNDETERMINED;
			exposed.add(new Point(i, j - 1));
		}
		if (getCell(i, j + 1, maze, width, height) == UNEXPOSED) {
			maze[i][j + 1] = UNDETERMINED;
			exposed.add(new Point(i, j + 1));
		}
	}

	private boolean validPath(int i, int j, int[][] maze, int width, int height) {
		int edgeState = 0;

		if (getCell(i - 1, j, maze, width, height) == PATH) {
			edgeState += 1;
		}

		if (getCell(i + 1, j, maze, width, height) == PATH) {
			edgeState += 2;
		}

		if (getCell(i, j - 1, maze, width, height) == PATH) {
			edgeState += 4;
		}

		if (getCell(i, j + 1, maze, width, height) == PATH) {
			edgeState += 8;
		}

		if (edgeState == 1) {
			if (getCell(i + 1, j - 1, maze, width, height) == PATH) {
				return false;
			}
			if (getCell(i + 1, j + 1, maze, width, height) == PATH) {
				return false;
			}

			return true;
		} else if (edgeState == 2) {
			if (getCell(i - 1, j - 1, maze, width, height) == PATH) {
				return false;
			}
			if (getCell(i - 1, j + 1, maze, width, height) == PATH) {
				return false;
			}
			return true;
		} else if (edgeState == 4) {
			if (getCell(i - 1, j + 1, maze, width, height) == PATH) {
				return false;
			}
			if (getCell(i + 1, j + 1, maze, width, height) == PATH) {
				return false;
			}
			return true;
		} else if (edgeState == 8) {
			if (getCell(i - 1, j - 1, maze, width, height) == PATH) {
				return false;
			}
			if (getCell(i + 1, j - 1, maze, width, height) == PATH) {
				return false;
			}
			return true;
		}
		return false;
	}

	private int getCell(int i, int j, int[][] maze, int width, int height) {
		if (i < 0 || i >= width || j < 0 || j >= height) {
			return -1;
		} else {
			return maze[i][j];
		}
	}

	public int[] createPatchBoundary(int x, int y) {
		int nCells = Patch.PATCH_HEIGHT + Patch.PATCH_WIDTH - 1;
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
		for (int j = Patch.PATCH_HEIGHT; j >= 0; j--) {
			for (int i = 0; i < Patch.PATCH_WIDTH + 1; i++) {
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
		for (int j = Patch.PATCH_HEIGHT; j >= 0; j--) {
			for (int i = 0; i < Patch.PATCH_WIDTH + 1; i++) {
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
		rng.setSeed(seed + (x << 16) + y);
	}

}
