package com.apricotjam.spacepanic.systems.map;

import com.apricotjam.spacepanic.GameParameters;
import com.apricotjam.spacepanic.art.Art;
import com.apricotjam.spacepanic.art.MapArt;
import com.apricotjam.spacepanic.art.Shaders;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.components.mapComponents.MapScreenComponent;
import com.apricotjam.spacepanic.components.mapComponents.ResourceComponent;
import com.apricotjam.spacepanic.gameelements.Resource;
import com.apricotjam.spacepanic.input.InputManager;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;

public class MapSystem extends EntitySystem {

	public static final float ASTEROID_SIZE = 0.5f;

	private float width;
	private float height;

	private Engine engine = null;

	private Entity masterEntity;
	private MapScreenComponent mapScreenComponent;

	private Entity screen;
	private TransformComponent screenTrans;
	private Entity screenBackground;
	private Entity screenFrame;
	private Entity mapCentre;
	private TransformComponent mapCentreTrans;
	private Entity playerIcon;

	private boolean moving = false;

	private MazeGenerator mazeGenerator;
	private ResourceGenerator resourceGenerator;
	private PatchConveyor patchConveyor;
	private Path path;

	private HashSet<Point> usedResources = new HashSet<Point>();

	private Random rng = new Random();

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

		mazeGenerator = new MazeGenerator(seed, GameParameters.PATHINESS, GameParameters.DEADENDNESS);
		resourceGenerator = new ResourceGenerator(seed);
		patchConveyor = new PatchConveyor(mazeGenerator, resourceGenerator, this);
		path = new Path(patchConveyor, this);
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
		path.addToEngine(engine);
	}

	@Override
	public void update (float deltaTime) {
		if (moving) {
			Vector2 moveVector = path.getNext().cpy().sub(mapScreenComponent.playerPosition);
			float dist = moveVector.len();
			Vector2 dir = moveVector.cpy().nor();
			if (dist > GameParameters.SPEED * deltaTime) {
				move(dir.x * GameParameters.SPEED * deltaTime, dir.y * GameParameters.SPEED * deltaTime);
			} else  {
				move(moveVector.x, moveVector.y);
				path.legComplete(engine);
				if (path.size() == 0) {
					hardStop();
				}
			}
			path.update(engine);
		}
		checkForEncounter();
		patchConveyor.update(engine);
	}

	private void move(float dx, float dy) {
		mapCentreTrans.position.x -= dx * ASTEROID_SIZE;
		mapCentreTrans.position.y -= dy * ASTEROID_SIZE;
		mapScreenComponent.playerPosition.add(dx, dy);
		patchConveyor.move(dx, dy);
	}

	private void softStop() {
		path.softStop(engine);
	}

	private void hardStop() {
		path.hardStop(engine);
		moving = false;
	}

	private Point getPlayerPoint(Vector2 pos) {
		return new Point(Math.round(pos.x), Math.round(pos.y));
	}

	private void click(int x, int y) {
		if (mapScreenComponent.currentState == MapScreenComponent.State.EXPLORING) {
			Point playerPoint = getPlayerPoint(mapScreenComponent.playerPosition);
			if (playerPoint.x == x && playerPoint.y == y) {
				clickPlayer();
				return;
			}

			boolean success = path.calculateNew(engine, getPlayerPoint(mapScreenComponent.playerPosition), new Point(x, y));
			if (success) {
				moving = true;
			} else {
				engine.addEntity(createCrossBad(x, y));
			}
		}
	}

	private void clickPlayer() {
		hardStop();
		for (int i = 0; i < resourceGenerator.nres.length; i++) {
			System.out.print(resourceGenerator.nres[i] + ", ");
		}
		System.out.print("\n");
	}

	private void checkForEncounter() {
		Point playerPoint = getPlayerPoint(mapScreenComponent.playerPosition);
		Resource r = patchConveyor.popResourceAtLocation(playerPoint, engine);
		if (r != null) {
			usedResources.add(new Point(playerPoint));
			mapScreenComponent.encounterResource = r;
			mapScreenComponent.currentState = MapScreenComponent.State.ENCOUNTER;
			softStop();
		}
	}

	public boolean isResourceUsed(Point pos) {
		return usedResources.contains(pos);
	}

	public MapScreenComponent getMapScreenComponent() {
		return mapScreenComponent;
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
				pos.x /= ASTEROID_SIZE;
				pos.y /= ASTEROID_SIZE;
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

		screenBackground.add(Shaders.generateFBOItemComponent("map-screen-fb"));

		return screenBackground;
	}

	private Entity createScreenFrame() {
		Entity screenFrame = new Entity();

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = 0.0f;
		tranc.position.y = 0.0f;
		tranc.position.z = 0.1f;
		tranc.parent = screenTrans;
		screenFrame.add(tranc);

		TextureComponent texc = new TextureComponent();
		texc.region = MapArt.computerFrame;
		texc.size.y = height * 1.35f;
		texc.size.x = texc.size.y * texc.region.getRegionWidth() / texc.region.getRegionHeight();
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
		texc.size.x = ASTEROID_SIZE;
		texc.size.y = ASTEROID_SIZE;
		asteroid.add(texc);

		asteroid.add(Shaders.generateFBOItemComponent("map-screen-fb"));

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
		texc.size.x = ASTEROID_SIZE;
		texc.size.y = ASTEROID_SIZE;
		resourceIcon.add(texc);

		resourceIcon.add(Shaders.generateFBOItemComponent("map-screen-fb"));

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
		texc.size.x = ASTEROID_SIZE * 0.8f;
		texc.size.y = ASTEROID_SIZE * 0.8f;
		playerIcon.add(texc);

		playerIcon.add(Shaders.generateFBOItemComponent("map-screen-fb"));

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = 0.0f;
		tranc.position.y = 0.0f;
		tranc.position.z = 2.5f;
		playerIcon.add(tranc);

		return playerIcon;
	}

	public Entity createLine(Vector2 start, Vector2 end, float lineWidth, Texture tex) {
		Vector2 startWorld = start.cpy().scl(ASTEROID_SIZE);
		Vector2 endWorld = end.cpy().scl(ASTEROID_SIZE);
		lineWidth *= ASTEROID_SIZE;

		Entity line = new Entity();

		LineComponent linec = new LineComponent();
		linec.start = startWorld;
		linec.end = endWorld;
		line.add(linec);

		TransformComponent tranc = new TransformComponent();
		tranc.position.z = 2.0f;
		tranc.parent = mapCentreTrans;
		line.add(tranc);

		line.add(Shaders.generateFBOItemComponent("map-screen-fb"));

		TextureComponent texc = new TextureComponent();
		texc.size.y = lineWidth;
		texc.region = Art.createTextureRegion(tex);
		line.add(texc);

		return line;
	}

	public Entity createCrossGood(float x, float y) {
		Entity cross = new Entity();

		TextureComponent texc = new TextureComponent();
		texc.region = MapArt.crossGood;
		texc.size.x = ASTEROID_SIZE * 0.8f;
		texc.size.y = ASTEROID_SIZE * 0.8f;
		cross.add(texc);

		cross.add(Shaders.generateFBOItemComponent("map-screen-fb"));

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = x * ASTEROID_SIZE;
		tranc.position.y = y * ASTEROID_SIZE;
		tranc.position.z = 3.0f;
		tranc.parent = mapCentreTrans;
		cross.add(tranc);

		TweenComponent tweenc = new TweenComponent();
		TweenSpec ts = new TweenSpec();
		ts.start = 0.1f;
		ts.end = 1.0f;
		ts.interp = Interpolation.bounceOut;
		ts.period = 0.5f;
		ts.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				TransformComponent tc = ComponentMappers.transform.get(e);
				tc.scale.x = a;
				tc.scale.y = a;
			}
		};
		tweenc.tweenSpecs.add(ts);
		cross.add(tweenc);

		return cross;
	}

	public Entity createCrossBad(float x, float y) {
		Entity cross = new Entity();

		TextureComponent texc = new TextureComponent();
		texc.region = MapArt.crossBad;
		texc.size.x = ASTEROID_SIZE * 0.8f;
		texc.size.y = ASTEROID_SIZE * 0.8f;
		cross.add(texc);

		cross.add(Shaders.generateFBOItemComponent("map-screen-fb"));

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = x * ASTEROID_SIZE;
		tranc.position.y = y * ASTEROID_SIZE;
		tranc.position.z = 3.0f;
		tranc.parent = mapCentreTrans;
		cross.add(tranc);

		TweenComponent tweenc = new TweenComponent();
		TweenSpec ts = new TweenSpec();
		ts.start = 0.1f;
		ts.end = 1.0f;
		ts.interp = Interpolation.bounceOut;
		ts.period = 0.5f;
		ts.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				TransformComponent tc = ComponentMappers.transform.get(e);
				tc.scale.x = a;
				tc.scale.y = a;
			}

			@Override
			public void endTween(Entity e) {
				engine.removeEntity(e);
			}
		};
		tweenc.tweenSpecs.add(ts);
		cross.add(tweenc);

		return cross;
	}

}
