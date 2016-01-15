package com.apricotjam.spacepanic.misc;

import com.apricotjam.spacepanic.systems.pipes.PipeWorld;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class PipePuzzleDifficultyTest {
	static private PrintWriter writer;

	static public void run(float a, float b, float c) {
		try {
			writer = new PrintWriter("pipe-puzzle-data.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int idiff = 20; idiff < 30; ++idiff) {
			PuzzleGenerator gen = new PuzzleGenerator(5);
			Array<Float> turnOffFractions = new Array<Float>();
			turnOffFractions.add(a);
			turnOffFractions.add(b);
			turnOffFractions.add(c);
			gen.generatePuzzle(idiff % 10, 1 + (int) (idiff / 10f), turnOffFractions);
		}

		writer.close();
		writer = null;
	}

	static public void gridSizeTest() {
		try {
			writer = new PrintWriter("pipe-puzzle-data.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int igrid = 4; igrid < 7; ++igrid) {
			for (int idiff = 0; idiff < 10 * igrid; ++idiff) {
				PuzzleGenerator gen = new PuzzleGenerator(igrid);
				Array<Float> turnOffFractions = new Array<Float>();
				float total = 0f;
				int npipes = 1 + (int) (idiff / 10f);
				for (int i = 0; i < npipes - 1; ++i) {
					turnOffFractions.add(1.0f / npipes);
					total += (1.0f / npipes);
				}
				turnOffFractions.add(1.0f - total);
				gen.generatePuzzle(idiff % 10, npipes, turnOffFractions);
			}
		}


		writer.close();
		writer = null;
	}

	static public void run() {
		try {
			writer = new PrintWriter("puzzle-difficulty.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int idiff = 10; idiff < 20; ++idiff) {
			PuzzleGenerator gen = new PuzzleGenerator(5);
			for (int it = 0; it <= 10; ++it) {
				Array<Float> turnOffFractions = new Array<Float>();
				turnOffFractions.add(it / 10f);
				turnOffFractions.add((10f - it) / 10f);
				gen.generatePuzzle(idiff % 10, 1 + (int) (idiff / 10f), turnOffFractions);
			}
		}
		for (int idiff = 20; idiff < 30; ++idiff) {
			PuzzleGenerator gen = new PuzzleGenerator(5);
			for (int it = 0; it <= 10; ++it) {
				for (int jt = 0; jt <= 10 - it; ++jt) {
					Array<Float> turnOffFractions = new Array<Float>();
					turnOffFractions.add(it / 10f);
					turnOffFractions.add(jt / 10f);
					turnOffFractions.add((10f - it - jt) / 10f);
					gen.generatePuzzle(idiff % 10, 1 + (int) (idiff / 10f), turnOffFractions);
				}
			}
		}
		for (int idiff = 30; idiff < 40; ++idiff) {
			PuzzleGenerator gen = new PuzzleGenerator(5);
			for (int it = 0; it <= 10; ++it) {
				for (int jt = 0; jt <= 10 - it; ++jt) {
					for (int kt = 0; kt <= 10 - it - jt; ++kt) {
						Array<Float> turnOffFractions = new Array<Float>();
						turnOffFractions.add(it / 10f);
						turnOffFractions.add(jt / 10f);
						turnOffFractions.add(kt / 10f);
						turnOffFractions.add((10f - it - jt - kt) / 10f);
						gen.generatePuzzle(idiff % 10, 1 + (int) (idiff / 10f), turnOffFractions);
					}
				}
			}
		}

		writer.close();
		writer = null;
	}

	static private class PuzzleGenerator {
		private RandomXS128 rng = new RandomXS128(0);
		private byte[][] maskGrid;
		private byte[] randomMasks = createRandomMasks();

		private Array<GridPoint2> starts = new Array<GridPoint2>();
		private Array<GridPoint2> ends = new Array<GridPoint2>();
		private Array<Integer> turnOffCounters = new Array<Integer>();
		private int currPipe = 0;

		private int length = 2;

		private int ndifferent = 0;

		private int grid_length = 0;

		public PuzzleGenerator(int grid_length) {
			this.grid_length = grid_length;

			maskGrid = new byte[grid_length][grid_length];
		}

		public void generatePuzzle(int difficulty, int npipes, Array<Float> turnOffFractions) {
			// Difficulty from 1 to 10;
			// TODO: modify generation parameters to reflect difficulty.

			if (npipes == 1) {
				starts.add(new GridPoint2(-1, grid_length - 1));
				ends.add(new GridPoint2(grid_length, grid_length - 1));
			} else if (npipes == 2) {
				starts.add(new GridPoint2(-1, grid_length - 4));
				ends.add(new GridPoint2(grid_length, 0));
				starts.add(new GridPoint2(-1, grid_length - 2));
				ends.add(new GridPoint2(grid_length, grid_length - 1));
			} else if (npipes == 3) {
				starts.add(new GridPoint2(-1, grid_length - 4));
				ends.add(new GridPoint2(grid_length, 0));
				starts.add(new GridPoint2(-1, grid_length - 2));
				ends.add(new GridPoint2(grid_length, grid_length - 1));
				starts.add(new GridPoint2(-1, grid_length - 3));
				ends.add(new GridPoint2(grid_length, grid_length - 3));
			} else {
				starts.add(new GridPoint2(-1, grid_length - 4));
				ends.add(new GridPoint2(grid_length, 0));
				starts.add(new GridPoint2(-1, grid_length - 2));
				ends.add(new GridPoint2(grid_length, grid_length - 3));
				starts.add(new GridPoint2(-1, grid_length - 3));
				ends.add(new GridPoint2(grid_length, grid_length - 4));
				starts.add(new GridPoint2(-1, grid_length - 1));
				ends.add(new GridPoint2(grid_length, grid_length - 2));
			}

			int totalTurnOffCounter = Math.min(difficulty, 9);
			int currTurnOffTotal = 0;
			for (int i = 0; i < npipes - 1; ++i) {
				turnOffCounters.add((int) (totalTurnOffCounter * turnOffFractions.get(i)));
				currTurnOffTotal += turnOffCounters.get(i);
			}
			turnOffCounters.add(totalTurnOffCounter - currTurnOffTotal);

			resetMaskGrid();

			boolean done = updateMask(starts.get(0).x, starts.get(0).y, starts.get(0).x + 1, starts.get(0).y);
			writer.println(grid_length + "," + difficulty + "," + npipes + "," + ndifferent);

			// Fill out the rest.
			for (int i = 0; i < grid_length; ++i) {
				for (int j = 0; j < grid_length; ++j) {
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
			for (int i = 0; i < grid_length; ++i) {
				for (int j = 0; j < grid_length; ++j) {
					maskGrid[i][j] = 0;
				}
			}
		}

		private boolean withinBounds(int i, int j) {
			return !(i < 0 || i >= grid_length || j < 0 || j >= grid_length);
		}

		private boolean updateMask(int parent_i, int parent_j, int i, int j) {
			// Connect to parent after checking if possible.
			int parentDirection;

			if (parent_i < i) {
				parentDirection = 3;
			} else if (parent_i > i) {
				parentDirection = 1;
			} else if (parent_j < j) {
				parentDirection = 2;
			} else {
				parentDirection = 0;
			}

			if (i == ends.get(currPipe).x && j == ends.get(currPipe).y) {
				if (!PipeWorld.connectedAtIndex((byte) (8), parentDirection) || turnOffCounters.get(currPipe) != 0) {
					return false;
				} else {
					if (currPipe == starts.size - 1) {
						ndifferent += 1;
						return false;
					} else {
						currPipe += 1;
						if (!updateMask(starts.get(currPipe).x, starts.get(currPipe).y, starts.get(currPipe).x + 1, starts.get(currPipe).y)) {
							currPipe -= 1;
							return false;
						} else {
							currPipe -= 1;
							ndifferent += 1;
							return false;
						}
					}
				}
			}
			if (PipeWorld.connectedAtIndex(maskGrid[i][j], PipeWorld.oppositeDirectionIndex(parentDirection))) {
				return false;
			}
			maskGrid[i][j] = PipeWorld.connectAtIndex(maskGrid[i][j], parentDirection);

			// Possible routes.
			Array<Integer> routes = new Array<Integer>();

			for (int idir = 0; idir < 4; ++idir) {
				if (!PipeWorld.connectedAtIndex(maskGrid[i][j], idir)) { // If already connected, can't go down this route.
					int inew = i + PipeWorld.GridDeltas.get(idir).x;
					int jnew = j + PipeWorld.GridDeltas.get(idir).y;

					if (withinBounds(inew, jnew) || (inew == ends.get(currPipe).x && jnew == ends.get(currPipe).y)) {
						boolean isStart = (inew == starts.get(currPipe).x) && (jnew == starts.get(currPipe).y);
						if (!isStart) { // Pipe cannot connect to start tile.
							// Check if pipe can turn off from a direct route to exit.
							if ((inew - i > 0 && ends.get(currPipe).x - i <= 0) || (inew - i < 0 && ends.get(currPipe).x - i >= 0) ||
									(jnew - j > 0 && ends.get(currPipe).y - j <= 0) || (jnew - j < 0 && ends.get(currPipe).y - j >= 0)) {
								if (turnOffCounters.get(currPipe) > 0) {
									routes.add(idir);
								}
							} else {
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
					turnOffCounters.set(currPipe, turnOffCounters.get(currPipe) - 1);
				}

				maskGrid[i][j] = PipeWorld.connectAtIndex(maskGrid[i][j], route);

				length += 1;
				connected = updateMask(i, j, inew, jnew);

				if (connected) {
					break;
				} else {
					length -= 1;
					if ((inew - i > 0 && ends.get(currPipe).x - i <= 0) || (inew - i < 0 && ends.get(currPipe).x - i >= 0) ||
							(jnew - j > 0 && ends.get(currPipe).y - j <= 0) || (jnew - j < 0 && ends.get(currPipe).y - j >= 0)) {
						turnOffCounters.set(currPipe, turnOffCounters.get(currPipe) + 1);
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
}
