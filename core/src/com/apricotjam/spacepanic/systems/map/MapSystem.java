package com.apricotjam.spacepanic.systems.map;

import com.apricotjam.spacepanic.art.MapArt;
import com.apricotjam.spacepanic.art.Shaders;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.components.mapComponents.MapScreenComponent;
import com.apricotjam.spacepanic.components.mapComponents.ResourceComponent;
import com.apricotjam.spacepanic.gameelements.Resource;
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

	Entity masterEntity;
	MapScreenComponent mapScreenComponent;

	Entity screen;
	TransformComponent screenTrans;
	Entity screenBackground;
	Entity screenFrame;
	Entity mapCentre;
	TransformComponent mapCentreTrans;
	Entity playerIcon;

	Vector2 playerPosition = new Vector2();
	ArrayList<Point> path = null;
	boolean moving = false;

	MazeGenerator mazeGenerator;
	ResourceGenerator resourceGenerator;
	Pathfinder pathfinder = new Pathfinder(Patch.PATCH_WIDTH * PatchConveyor.PATCHES_X, Patch.PATCH_HEIGHT * PatchConveyor.PATCHES_Y, MAXPATH);
	PatchConveyor patchConveyor;

	Random rng = new Random();

	public MapSystem(Entity masterEntity, float width, float height) {
		this(masterEntity, width, height, System.nanoTime());
	}

	public MapSystem(Entity masterEntity, float width, float height, long seed) {
		System.out.println("Seed: " + seed);

		this.masterEntity = masterEntity;
		this.width = width;
		this.height = height;

		screen = createScreen();
		screenTrans = ComponentMappers.transform.get(screen);
		mapScreenComponent = ComponentMappers.mapscreen.get(masterEntity);

		screenBackground = createScreenBackground();
		screenFrame = createScreenFrame();

		mapCentre = createMapCentre();
		mapCentreTrans = ComponentMappers.transform.get(mapCentre);
		playerIcon = createPlayerIcon();

		mazeGenerator = new MazeGenerator(seed, PATHINESS);
		resourceGenerator = new ResourceGenerator(seed);

		patchConveyor = new PatchConveyor(mazeGenerator, resourceGenerator, this);
	}

	@Override
	public void addedToEngine(Engine engine) {
		this.engine = engine;
		engine.addEntity(screen);
		engine.addEntity(screenBackground);
		engine.addEntity(screenFrame);
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
		checkForEncounter();
		patchConveyor.update(engine);
	}

	private void move(float dx, float dy) {
		mapCentreTrans.position.x -= dx * ASTEROID_WIDTH;
		mapCentreTrans.position.y -= dy * ASTEROID_HEIGHT;
		playerPosition.add(dx, dy);
		patchConveyor.move(dx, dy);
	}

	private void click(int x, int y) {
		if (mapScreenComponent.currentState == MapScreenComponent.State.EXPLORING) {
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
	}

	private void clickPlayer() {
		moving = false;
		for (int i = 0; i < resourceGenerator.nres.length; i++) {
			System.out.print(resourceGenerator.nres[i] + ", ");
		}
		System.out.print("\n");
	}

	private void checkForEncounter() {
		Point playerPoint = new Point((int)playerPosition.x, (int)playerPosition.y);
		Resource r = patchConveyor.popResourceAtLocation(playerPoint, engine);
		if (r != Resource.NONE) {
			mapScreenComponent.encounterResource = r;
			mapScreenComponent.currentState = MapScreenComponent.State.ENCOUNTER;
			moving = false;
		}
	}

	private ArrayList<Point> findPath(Point target) {
		pathfinder.setOffset(patchConveyor.getOffset());
		Point start = new Point((int)playerPosition.x, (int)playerPosition.y);
		return pathfinder.calculatePath(patchConveyor.getFullMaze(), start, target);
	}

	private Entity createScreen() {
		Entity screen = new Entity();

		MapScreenComponent mscomp = new MapScreenComponent();
		screen.add(mscomp);

		TextureComponent texc = new TextureComponent();
		texc.size.x = width;
		texc.size.y = height;
		screen.add(texc);

		screen.add(Shaders.generateFBOComponent("map-screen-fb", texc));

		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("crt");
		screen.add(shaderComp);

		ShaderTimeComponent shaderTimeComp = new ShaderTimeComponent();
		screen.add(shaderTimeComp);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = 0.0f;
		tranc.position.y = 0.0f;
		tranc.position.z = 0.0f;
		tranc.parent = ComponentMappers.transform.get(masterEntity);
		screen.add(tranc);

		ClickComponent cc = new ClickComponent();
		cc.shape = new Rectangle().setSize(width, height).setCenter(0.0f, 0.0f);
		cc.clicker = new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				Vector2 pos = new Vector2(InputManager.screenInput.getPointerDownLocation());
				TransformComponent tc = screenTrans.getTotalTransform();
				pos.sub(tc.position.x, tc.position.y);
				pos.sub(mapCentreTrans.position.x, mapCentreTrans.position.y);
				pos.x /= ASTEROID_WIDTH;
				pos.y /= ASTEROID_HEIGHT;
				click(Math.round(pos.x), Math.round(pos.y));
			}
		};
		screen.add(cc);

		return screen;
	}

	private Entity createScreenBackground() {
		Entity screenBackground = new Entity();

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = 0.0f;
		tranc.position.y = 0.0f;
		tranc.position.z = 0.0f;
		screenBackground.add(tranc);

		TextureComponent texc = new TextureComponent();
		texc.region = MapArt.computerBackground;
		texc.size.x = width;
		texc.size.y = height;
		screenBackground.add(texc);

		FBO_ItemComponent fboItemComp = new FBO_ItemComponent();
		fboItemComp.fboBatch = Shaders.manager.getSpriteBatch("map-screen-fb");
		screenBackground.add(fboItemComp);

		return screenBackground;
	}

	private Entity createScreenFrame() {
		Entity screenFrame = new Entity();

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = 0.0f;
		tranc.position.y = 0.0f;
		tranc.position.z = 0.0f;
		tranc.parent = screenTrans;
		screenFrame.add(tranc);

		TextureComponent texc = new TextureComponent();
		texc.region = MapArt.computerFrame;
		texc.size.x = width;
		texc.size.y = height;
		screenFrame.add(texc);

		return screenFrame;
	}

	private Entity createMapCentre() {
		Entity mapCentre = new Entity();

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = 0.0f;
		tranc.position.y = 0.0f;
		tranc.position.z = 0.0f;
		mapCentre.add(tranc);

		TweenComponent tc = new TweenComponent();
		mapCentre.add(tc);

		return mapCentre;
	}

	public Entity createAsteroid(float x, float y) {
		Entity asteroid = new Entity();

		TextureComponent texc = new TextureComponent();
		texc.region = MapArt.asteroids.get(rng.nextInt(MapArt.asteroids.size()));
		texc.size.x = ASTEROID_WIDTH;
		texc.size.y = ASTEROID_HEIGHT;
		asteroid.add(texc);

		FBO_ItemComponent fboItemComp = new FBO_ItemComponent();
		fboItemComp.fboBatch = Shaders.manager.getSpriteBatch("map-screen-fb");
		asteroid.add(fboItemComp);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = x;
		tranc.position.y = y;
		tranc.position.z = 1.0f;
		tranc.parent = mapCentreTrans;
		asteroid.add(tranc);

		return asteroid;
	}

	public Entity createResourceIcon(float x, float y, Resource resource) {
		Entity resourceIcon = new Entity();

		ResourceComponent resourceComponent = new ResourceComponent();
		resourceComponent.resource = resource;
		resourceIcon.add(resourceComponent);


		TextureComponent texc = new TextureComponent();
		texc.region = MapArt.asteroids.get(rng.nextInt(MapArt.asteroids.size()));
		switch (resource) {
			case OXYGEN:
				texc.region = MapArt.resourceIcons.get(0);
				break;
			case OIL:
				texc.region = MapArt.resourceIcons.get(1);
				break;
			case RESOURCE2:
				texc.region = MapArt.resourceIcons.get(2);
				break;
			case RESOURCE3:
				texc.region = MapArt.resourceIcons.get(3);
				break;
		}
		texc.size.x = ASTEROID_WIDTH;
		texc.size.y = ASTEROID_HEIGHT;
		resourceIcon.add(texc);

		FBO_ItemComponent fboItemComp = new FBO_ItemComponent();
		fboItemComp.fboBatch = Shaders.manager.getSpriteBatch("map-screen-fb");
		resourceIcon.add(fboItemComp);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = x;
		tranc.position.y = y;
		tranc.position.z = 1.0f;
		tranc.parent = mapCentreTrans;
		resourceIcon.add(tranc);

		return resourceIcon;
	}

	private Entity createPlayerIcon() {
		Entity playerIcon = new Entity();

		TextureComponent texc = new TextureComponent();
		texc.region = MapArt.playerIcon;
		texc.size.x = ASTEROID_WIDTH * 0.8f;
		texc.size.y = ASTEROID_HEIGHT * 0.8f;
		playerIcon.add(texc);

		FBO_ItemComponent fboItemComp = new FBO_ItemComponent();
		fboItemComp.fboBatch = Shaders.manager.getSpriteBatch("map-screen-fb");
		playerIcon.add(fboItemComp);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = 0.0f;
		tranc.position.y = 0.0f;
		tranc.position.z = 2.0f;
		playerIcon.add(tranc);

		return playerIcon;
	}

}
