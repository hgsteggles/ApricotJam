package com.apricotjam.spacepanic.systems.maze;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector2;

import java.awt.Point;

public class PatchConveyor {

	public static final int PATCHES_X = 5;
	public static final int PATCHES_Y = 5;

	//Limits before patches are rotated
	private static final float XLIMIT = ((PATCHES_X / 2.0f) - 1.0f) * Patch.PATCH_WIDTH * MapSystem.ASTEROID_WIDTH;
	private static final float YLIMIT = ((PATCHES_Y / 2.0f) - 1.0f) * Patch.PATCH_HEIGHT * MapSystem.ASTEROID_HEIGHT;

	Patch[][] patches;
	MazeGenerator mazeGenerator;
	MapSystem mapSystem;

	Vector2 offsetFromCentre = new Vector2(); //Distance player is from current centre patch, used to know when to rotate patches

	public PatchConveyor(MazeGenerator mazeGenerator, MapSystem mapSystem) {
		this.patches = new Patch[PATCHES_X][PATCHES_Y];
		this.mazeGenerator = mazeGenerator;
		this.mapSystem = mapSystem;

		for (int ipatch = 0; ipatch < PATCHES_X; ipatch++) {
			for (int jpatch = 0; jpatch < PATCHES_Y; jpatch++) {
				int xOffset = PATCHES_X / 2;
				int yOffset = PATCHES_Y / 2;
				patches[ipatch][jpatch] = new Patch(ipatch - xOffset, jpatch - yOffset, mazeGenerator, mapSystem);
			}
		}
	}

	public void addToEngine(Engine engine) {
		for (int ipatch = 0; ipatch < PATCHES_X; ipatch++) {
			for (int jpatch = 0; jpatch < PATCHES_Y; jpatch++) {
				patches[ipatch][jpatch].addToEngine(engine);
			}
		}
	}

	public void update(Engine engine) {
		if (offsetFromCentre.y > YLIMIT) {
			movePatchesUp(engine);
			offsetFromCentre.y -= Patch.PATCH_HEIGHT;
		} else if (offsetFromCentre.y < -1 * YLIMIT) {
			movePatchesDown(engine);
			offsetFromCentre.y += Patch.PATCH_HEIGHT;
		}
		if (offsetFromCentre.x > XLIMIT) {
			movePatchesRight(engine);
			offsetFromCentre.x -= Patch.PATCH_WIDTH;
		} else if (offsetFromCentre.x < -1 * XLIMIT) {
			movePatchesLeft(engine);
			offsetFromCentre.x += Patch.PATCH_WIDTH;
		}
	}

	public void move(float dx, float dy) {
		offsetFromCentre.x += dx;
		offsetFromCentre.y += dy;
	}

	public Point getOffset() {
		int xoff = patches[0][0].x * Patch.PATCH_WIDTH - (int)(Patch.PATCH_WIDTH / 2.0f);
		int yoff = patches[0][0].y * Patch.PATCH_HEIGHT - (int)(Patch.PATCH_HEIGHT / 2.0f);
		return new Point(xoff, yoff);
	}

	public int[][] getFullMaze() {
		int[][] fullMaze = new int[Patch.PATCH_WIDTH * PATCHES_X][Patch.PATCH_HEIGHT * PATCHES_Y];
		for (int ipatch = 0; ipatch < PATCHES_X; ipatch++) {
			for (int jpatch = 0; jpatch < PATCHES_Y; jpatch++) {
				for (int icell = 0; icell < Patch.PATCH_WIDTH; icell++) {
					for (int jcell = 0; jcell < Patch.PATCH_HEIGHT; jcell++) {
						fullMaze[ipatch * Patch.PATCH_WIDTH + icell][jpatch * Patch.PATCH_HEIGHT + jcell] = patches[ipatch][jpatch].maze[icell][jcell];
					}
				}
			}
		}
		return fullMaze;
	}

	private void movePatchesUp(Engine engine) {
		for (int i = 0; i < PATCHES_X; i++) {
			patches[i][0].removeFromEngine(engine);
		}
		for (int j = 0; j < PATCHES_Y - 1; j++) {
			for (int i = 0; i < PATCHES_X; i++) {
				patches[i][j] = patches[i][j + 1];
			}
		}
		for (int i = 0; i < PATCHES_X; i++) {
			patches[i][PATCHES_Y - 1] = new Patch(patches[i][PATCHES_Y - 2].x, patches[i][PATCHES_Y - 2].y + 1, mazeGenerator, mapSystem);
			patches[i][PATCHES_Y - 1].addToEngine(engine);
		}
	}

	private void movePatchesDown(Engine engine) {
		for (int i = 0; i < PATCHES_X; i++) {
			patches[i][PATCHES_Y - 1].removeFromEngine(engine);
		}
		for (int j = PATCHES_Y - 1; j > 0; j--) {
			for (int i = 0; i < PATCHES_X; i++) {
				patches[i][j] = patches[i][j - 1];
			}
		}
		for (int i = 0; i < PATCHES_X; i++) {
			patches[i][0] = new Patch(patches[i][1].x, patches[i][1].y - 1, mazeGenerator, mapSystem);
			patches[i][0].addToEngine(engine);
		}
	}

	private void movePatchesLeft(Engine engine) {
		for (int j = 0; j < PATCHES_Y; j++) {
			patches[PATCHES_X - 1][j].removeFromEngine(engine);
		}
		for (int i = PATCHES_X - 1; i > 0; i--) {
			for (int j = 0; j < PATCHES_Y; j++) {
				patches[i][j] = patches[i - 1][j];
			}
		}
		for (int j = 0; j < PATCHES_Y; j++) {
			patches[0][j] = new Patch(patches[1][j].x - 1, patches[1][j].y, mazeGenerator, mapSystem);
			patches[0][j].addToEngine(engine);
		}
	}

	private void movePatchesRight(Engine engine) {
		for (int j = 0; j < PATCHES_Y; j++) {
			patches[0][j].removeFromEngine(engine);
		}
		for (int i = 0; i < PATCHES_X - 1; i++) {
			for (int j = 0; j < PATCHES_Y; j++) {
				patches[i][j] = patches[i + 1][j];
			}
		}
		for (int j = 0; j < PATCHES_Y; j++) {
			patches[PATCHES_X - 1][j] = new Patch(patches[PATCHES_X - 2][j].x + 1, patches[PATCHES_X - 2][j].y, mazeGenerator, mapSystem);
			patches[PATCHES_X - 1][j].addToEngine(engine);
		}
	}
}
