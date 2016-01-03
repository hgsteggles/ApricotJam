package com.apricotjam.spacepanic.systems;

import com.apricotjam.spacepanic.art.ComputerArt;
import com.apricotjam.spacepanic.art.HelmetUI;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.input.InputManager;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.puzzle.MazeGenerator;
import com.apricotjam.spacepanic.puzzle.Pathfinder;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class MapSystem extends EntitySystem {

	private class Patch {
		int x;
		int y;

		int[][] maze;
		ArrayList<Entity> asteroids;

		public Patch(int x, int y, int[][] maze) {
			this.x = x;
			this.y = y;
			this.maze = maze;
			asteroids = new ArrayList<Entity>();
		}

		public void addToEngine(Engine engine) {
			for (Entity ast : asteroids) {
				engine.addEntity(ast);
			}
		}

		public void removeFromEngine(Engine engine) {
			for (Entity ast : asteroids) {
				engine.removeEntity(ast);
			}
		}
	}

	private static final float PATHINESS = 0.5f; //How likely each cell is to be a path on the patch boundaries
	private static final float ASTEROID_WIDTH = 0.3f;
	private static final float ASTEROID_HEIGHT = 0.3f;
	private static final int PATCH_WIDTH = 10;
	private static final int PATCH_HEIGHT = 10;
	private static final int PATCHES_X = 5;
	private static final int PATCHES_Y = 5;

	//Limits before patches are rotated
	private static final float XLIMIT = ((PATCHES_X / 2.0f) - 1.0f) * PATCH_WIDTH * ASTEROID_WIDTH;
	private static final float YLIMIT = ((PATCHES_Y / 2.0f) - 1.0f) * PATCH_HEIGHT * ASTEROID_HEIGHT;

	private static final int MAXPATH = 30;
	private static final float SPEED = 5.0f;

	float width;
	float height;

	Engine engine = null;

	Entity screen;
	TransformComponent screenTrans;
	Entity mapCentre;
	TransformComponent mapCentreTrans;
	Entity playerIcon;

	Vector2 playerPosition = new Vector2();
	Vector2 offsetFromCentre = new Vector2(); //Distance player is from current centre patch, used to know when to rotate patches
	ArrayList<Point> path = null;
	boolean moving = false;

	MazeGenerator mazeGenerator;
	Pathfinder pathfinder;
	Patch[][] patches;

	Random rng;

	public MapSystem(float width, float height) {
		this.width = width;
		this.height = height;
		screen = createScreen();
		screenTrans = ComponentMappers.transform.get(screen);
		mapCentre = createMapCentre();
		mapCentreTrans = ComponentMappers.transform.get(mapCentre);
		playerIcon = createPlayerIcon();

		mazeGenerator = new MazeGenerator(PATCH_WIDTH, PATCH_HEIGHT, PATHINESS);
		patches = new Patch[PATCHES_X][PATCHES_Y];

		pathfinder = new Pathfinder(PATCH_WIDTH * PATCHES_X, PATCH_HEIGHT * PATCHES_Y, MAXPATH);

		rng = new Random();

		for (int ipatch = 0; ipatch < PATCHES_X; ipatch++) {
			for (int jpatch = 0; jpatch < PATCHES_Y; jpatch++) {
				int xOffset = PATCHES_X / 2;
				int yOffset = PATCHES_Y / 2;
				patches[ipatch][jpatch] = createPatch(ipatch - xOffset, jpatch - yOffset);
			}
		}
	}

	@Override
	public void addedToEngine(Engine engine) {
		this.engine = engine;
		engine.addEntity(screen);
		engine.addEntity(mapCentre);
		engine.addEntity(playerIcon);
		for (int ipatch = 0; ipatch < PATCHES_X; ipatch++) {
			for (int jpatch = 0; jpatch < PATCHES_Y; jpatch++) {
				patches[ipatch][jpatch].addToEngine(engine);
			}
		}
	}

	@Override
	public void update (float deltaTime) {
		if (moving) {
			Vector2 moveVector = new Vector2(path.get(0).x, path.get(0).y).sub(playerPosition);
			float dist = moveVector.len();
			Vector2 dir = moveVector.cpy().nor();
			if (dist > SPEED * deltaTime) {
				move(dir.x * SPEED * deltaTime, dir.y * SPEED * deltaTime);
			} else  {
				move(moveVector.x, moveVector.y);
				path.remove(0);
				if (path.size() == 0) {
					moving = false;
				}
			}
		}
		checkPatches();
	}

	private void checkPatches() {
		if (offsetFromCentre.y > YLIMIT) {
			movePatchesUp(engine);
			offsetFromCentre.y -= PATCH_HEIGHT * ASTEROID_HEIGHT;
		} else if (offsetFromCentre.y < -1 * YLIMIT) {
			movePatchesDown(engine);
			offsetFromCentre.y += PATCH_HEIGHT * ASTEROID_HEIGHT;
		}
		if (offsetFromCentre.x > XLIMIT) {
			movePatchesRight(engine);
			offsetFromCentre.x -= PATCH_WIDTH * ASTEROID_WIDTH;
		} else if (offsetFromCentre.x < -1 * XLIMIT) {
			movePatchesLeft(engine);
			offsetFromCentre.x += PATCH_WIDTH * ASTEROID_WIDTH;
		}
	}

	private void click(int x, int y) {
		path = findPath(new Point(x, y));
		if (path.size() > 0) {
			moving = true;
		}
	}

	private ArrayList<Point> findPath(Point target) {
		int xoff = patches[0][0].x * PATCH_WIDTH - (int)(PATCH_WIDTH / 2.0f);
		int yoff = patches[0][0].y * PATCH_HEIGHT - (int)(PATCH_HEIGHT / 2.0f);
		pathfinder.setOffset(xoff, yoff);

		int[][] fullMaze = new int[PATCH_WIDTH * PATCHES_X][PATCH_HEIGHT * PATCHES_Y];
		for (int ipatch = 0; ipatch < PATCHES_X; ipatch++) {
			for (int jpatch = 0; jpatch < PATCHES_Y; jpatch++) {
				for (int icell = 0; icell < PATCH_WIDTH; icell++) {
					for (int jcell = 0; jcell < PATCH_HEIGHT; jcell++) {
						fullMaze[ipatch * PATCH_WIDTH + icell][jpatch * PATCH_HEIGHT + jcell] = patches[ipatch][jpatch].maze[icell][jcell];
					}
				}
			}
		}

		Point start = new Point((int)playerPosition.x, (int)playerPosition.y);
		return pathfinder.calculatePath(fullMaze, start, target);
	}

	private void move(float dx, float dy) {
		mapCentreTrans.position.x -= dx * ASTEROID_WIDTH;
		mapCentreTrans.position.y -= dy * ASTEROID_HEIGHT;
		offsetFromCentre.x += dx * ASTEROID_WIDTH;
		offsetFromCentre.y += dy * ASTEROID_WIDTH;
		playerPosition.add(dx, dy);
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
			patches[i][PATCHES_Y - 1] = createPatch(patches[i][PATCHES_Y - 2].x, patches[i][PATCHES_Y - 2].y + 1);
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
			patches[i][0] = createPatch(patches[i][1].x, patches[i][1].y - 1);
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
			patches[0][j] = createPatch(patches[1][j].x - 1, patches[1][j].y);
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
			patches[PATCHES_X - 1][j] = createPatch(patches[PATCHES_X - 2][j].x + 1, patches[PATCHES_X - 2][j].y);
			patches[PATCHES_X - 1][j].addToEngine(engine);
		}
	}

	private Entity createScreen() {
		Entity screen = new Entity();

		MapPartComponent mpc = new MapPartComponent();
		screen.add(mpc);

		TextureComponent texc = new TextureComponent();
		texc.region = ComputerArt.computer;
		texc.size.x = width;
		texc.size.y = height;
		screen.add(texc);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		tranc.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		tranc.position.z = 0.0f;
		screen.add(tranc);

		ClickComponent cc = new ClickComponent();
		cc.shape = new Rectangle().setSize(width, height).setCenter(0.0f, 0.0f);
		cc.clicker = new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				Vector2 pos = new Vector2(InputManager.screenInput.getPointerDownLocation());
				pos.sub(screenTrans.position.x, screenTrans.position.y);
				pos.sub(mapCentreTrans.position.x, mapCentreTrans.position.y);
				pos.x /= ASTEROID_WIDTH;
				pos.y /= ASTEROID_HEIGHT;
				click(Math.round(pos.x), Math.round(pos.y));
			}
		};
		screen.add(cc);

		return screen;
	}

	private Entity createMapCentre() {
		Entity mapCentre = new Entity();

		MapPartComponent mpc = new MapPartComponent();
		mapCentre.add(mpc);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = 0.0f;
		tranc.position.y = 0.0f;
		tranc.position.z = 0.0f;
		tranc.parent = ComponentMappers.transform.get(screen);
		mapCentre.add(tranc);

		TweenComponent tc = new TweenComponent();
		mapCentre.add(tc);

		return mapCentre;
	}

	private Entity createAsteroid(float x, float y) {
		Entity asteroid = new Entity();

		MapPartComponent mpc = new MapPartComponent();
		asteroid.add(mpc);

		TextureComponent texc = new TextureComponent();
		texc.region = ComputerArt.asteroids.get(rng.nextInt(ComputerArt.asteroids.size()));
		texc.size.x = ASTEROID_WIDTH;
		texc.size.y = ASTEROID_HEIGHT;
		asteroid.add(texc);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = x;
		tranc.position.y = y;
		tranc.position.z = 0.0f;
		tranc.parent = mapCentreTrans;
		asteroid.add(tranc);

		return asteroid;
	}

	private Entity createPlayerIcon() {
		Entity playerIcon = new Entity();

		MapPartComponent mpc = new MapPartComponent();
		playerIcon.add(mpc);

		TextureComponent texc = new TextureComponent();
		texc.region = HelmetUI.screw;
		texc.size.x = ASTEROID_WIDTH * 0.8f;
		texc.size.y = ASTEROID_HEIGHT * 0.8f;
		playerIcon.add(texc);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = 0.0f;
		tranc.position.y = 0.0f;
		tranc.position.z = 0.0f;
		tranc.parent = ComponentMappers.transform.get(screen);
		playerIcon.add(tranc);

		return playerIcon;
	}

	private Patch createPatch(int xpatch, int ypatch) {
		Patch patch = new Patch(xpatch, ypatch, mazeGenerator.createPatch(xpatch, ypatch));
		for (int icell = 0; icell < PATCH_WIDTH; icell++) {
			for (int jcell = 0; jcell < PATCH_HEIGHT; jcell++) {
				if (patch.maze[icell][jcell] == MazeGenerator.WALL) {
					float x = (icell - (PATCH_WIDTH  / 2.0f) + (xpatch * PATCH_WIDTH * 1.0f)) * ASTEROID_WIDTH;
					float y = (jcell - (PATCH_HEIGHT / 2.0f) + (ypatch * PATCH_HEIGHT * 1.0f)) * ASTEROID_HEIGHT;
					patch.asteroids.add(createAsteroid(x, y));
				}
			}
		}
		return patch;
	}

}
