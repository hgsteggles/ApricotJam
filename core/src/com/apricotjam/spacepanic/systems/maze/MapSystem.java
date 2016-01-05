package com.apricotjam.spacepanic.systems.maze;

import com.apricotjam.spacepanic.art.ComputerArt;
import com.apricotjam.spacepanic.art.HelmetUI;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.input.InputManager;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class MapSystem extends EntitySystem {

	private static final float PATHINESS = 0.5f; //How likely each cell is to be a path on the patch boundaries
	public static final float ASTEROID_WIDTH = 0.5f;
	public static final float ASTEROID_HEIGHT = 0.5f;

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
	ArrayList<Point> path = null;
	boolean moving = false;

	MazeGenerator mazeGenerator = new MazeGenerator(Patch.PATCH_WIDTH, Patch.PATCH_HEIGHT, PATHINESS);
	Pathfinder pathfinder = new Pathfinder(Patch.PATCH_WIDTH * PatchConveyor.PATCHES_X, Patch.PATCH_HEIGHT * PatchConveyor.PATCHES_Y, MAXPATH);
	PatchConveyor patchConveyor;

	Random rng = new Random();

	public MapSystem(float width, float height) {
		this.width = width;
		this.height = height;
		screen = createScreen();
		screenTrans = ComponentMappers.transform.get(screen);
		mapCentre = createMapCentre();
		mapCentreTrans = ComponentMappers.transform.get(mapCentre);
		playerIcon = createPlayerIcon();

		patchConveyor = new PatchConveyor(mazeGenerator, this);
	}

	@Override
	public void addedToEngine(Engine engine) {
		this.engine = engine;
		engine.addEntity(screen);
		engine.addEntity(mapCentre);
		engine.addEntity(playerIcon);
		patchConveyor.addToEngine(engine);
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
		patchConveyor.update(engine);
	}

	private void move(float dx, float dy) {
		mapCentreTrans.position.x -= dx * ASTEROID_WIDTH;
		mapCentreTrans.position.y -= dy * ASTEROID_HEIGHT;
		playerPosition.add(dx, dy);
	}

	private void click(int x, int y) {
		Point playerPoint = new Point((int)playerPosition.x, (int)playerPosition.y);
		if (playerPoint.x == x && playerPoint.y == y) {
			clickPlayer();
			return;
		}

		ArrayList<Point> newPath = findPath(new Point(x, y));
		if (newPath.size() > 0) {
			path = newPath;
			moving = true;
		}
	}

	private void clickPlayer() {
		moving = false;
	}

	private ArrayList<Point> findPath(Point target) {
		pathfinder.setOffset(patchConveyor.getOffset());
		Point start = new Point((int)playerPosition.x, (int)playerPosition.y);
		return pathfinder.calculatePath(patchConveyor.getFullMaze(), start, target);
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

	public Entity createAsteroid(float x, float y) {
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

}
