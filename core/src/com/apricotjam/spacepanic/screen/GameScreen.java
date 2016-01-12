package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.GameParameters;
import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.components.helmet.HelmetScreenComponent;
import com.apricotjam.spacepanic.components.map.MapScreenComponent;
import com.apricotjam.spacepanic.components.pipe.PipeScreenComponent;
import com.apricotjam.spacepanic.gameelements.Resource;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.systems.*;
import com.apricotjam.spacepanic.systems.helmet.HelmetSystem;
import com.apricotjam.spacepanic.systems.helmet.HelmetSystem.LED_Message.Severity;
import com.apricotjam.spacepanic.systems.map.MapSystem;
import com.apricotjam.spacepanic.systems.pipes.PipeSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

public class GameScreen extends BasicScreen {

	public enum GameState {
		MAZING, PIPING, TRANSITIONING
	}

	public static float BACKGROUND_MOVEMENT_FACTOR = 0.1f;
	public static float MAP_X = BasicScreen.WORLD_WIDTH / 2.0f;
	public static float MAP_Y_ON = BasicScreen.WORLD_HEIGHT / 2.0f + 0.3f;
	public static float MAP_Y_OFF = -2.0f;
	public static float PIPE_X = BasicScreen.WORLD_WIDTH / 2.0f;
	public static float PIPE_Y = BasicScreen.WORLD_HEIGHT / 2.0f + 0.3f;
	public static float PIPE_SCALE_SMALL = 0.1f;
	public static float PIPE_SCALE_LARGE = 1.0f;

	private GameState currentState;

	private Entity mapSystemEntity;
	private Entity helmetSystemEntity;
	private Entity pipeSystemEntity;
	private TextureComponent backgroundTexComp;

	private PipeSystem pipeSystem = null;

	public GameScreen(SpacePanic spacePanic) {
		super(spacePanic);

		add(new RenderingSystem(spriteBatch, worldCamera));
		add(new ClickSystem());
		add(new TweenSystem());
		add(new MovementSystem());
		add(new ScrollSystem());
		add(new LineSystem());
		add(new TickerSystem());
		add(new AnimationSystem());
		add(new AnimatedShaderSystem());
		add(new ShaderLightingSystem());

		addHelmetSystem();
		addMapSystem();

		add(createBackground());

		currentState = GameState.MAZING;
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		switch (currentState) {
			case MAZING:
				updateMaze();
				break;
			case PIPING:
				updatePipe();
				break;
		}

		for (Resource r: Resource.values()) {
			alterResource(r, GameParameters.RESOURCE_DEPLETION.get(r) * delta);
		}
	}

	private void updateMaze() {
		MapScreenComponent msc = ComponentMappers.mapscreen.get(mapSystemEntity);
		if (msc.currentState == MapScreenComponent.State.ENCOUNTER) {
			resourceEncountered(msc.encounterResource);
		} else if (msc.currentState == MapScreenComponent.State.EXPLORING) {
			setBackgroundPosition();
		}
	}

	private void updatePipe() {
		PipeScreenComponent psc = ComponentMappers.pipescreen.get(pipeSystemEntity);
		if (psc.currentState == PipeScreenComponent.State.SUCCESS) {
			pipeGameComplete(psc.resource, true);
		} else if (psc.currentState == PipeScreenComponent.State.FAIL) {
			pipeGameComplete(psc.resource, false);
		}
	}

	private void resourceEncountered(Resource resource) {
		MapScreenComponent msc = ComponentMappers.mapscreen.get(mapSystemEntity);
		addMessage(resource + " located", Severity.HINT);
		msc.viewSize++;
		msc.currentState = MapScreenComponent.State.PAUSED;
		ComponentMappers.tween.get(mapSystemEntity).tweenSpecs.add(mapOutTween());
		currentState = GameState.TRANSITIONING;

		addPipeSystem(resource);
		ComponentMappers.tween.get(pipeSystemEntity).tweenSpecs.add(pipeInTween());
	}

	private void pipeGameComplete(Resource resource, boolean success) {
		ComponentMappers.tween.get(pipeSystemEntity).tweenSpecs.add(pipeOutTween());
		PipeScreenComponent psc = ComponentMappers.pipescreen.get(pipeSystemEntity);
		psc.currentState = PipeScreenComponent.State.PAUSED;
		currentState = GameState.TRANSITIONING;

		ComponentMappers.tween.get(mapSystemEntity).tweenSpecs.add(mapInTween());

		if (success) {
			addMessage(resource + " acquired", Severity.SUCCESS);
			alterResource(resource, GameParameters.RESOURCE_GAIN.get(resource));
		} else {
			addMessage(resource + " lost", Severity.FAIL);
		}
	}

	private void alterResource(Resource resource, float amount) {
		HelmetScreenComponent hsc = ComponentMappers.helmetscreen.get(helmetSystemEntity);
		float old = hsc.resourceCount.get(resource);
		float max = GameParameters.RESOURCE_MAX.get(resource);
		float next = MathUtils.clamp(old + amount, 0.0f, max);
		hsc.resourceCount.put(resource, next);

		switch (resource) {
			case OXYGEN:
				break;
			case DEMISTER:
				break;
			case PIPE_CLEANER:
				break;
			case PLUTONIUM:
				updateViewSize(next / max);
				break;
		}
	}

	private void addMessage(String text, Severity severity) {
		HelmetScreenComponent hsc = ComponentMappers.helmetscreen.get(helmetSystemEntity);
		hsc.messages.addLast(new HelmetSystem.LED_Message(text, severity));
	}

	private void updateViewSize(float fraction) {
		MapScreenComponent msc = ComponentMappers.mapscreen.get(mapSystemEntity);
		msc.viewSize = GameParameters.MIN_VIEWSIZE + fraction * (GameParameters.MAX_VIEWSIZE - GameParameters.MIN_VIEWSIZE);
	}

	private TweenSpec mapOutTween() {
		TweenSpec ts = new TweenSpec();
		ts.start = MAP_Y_ON;
		ts.end =  MAP_Y_OFF;
		ts.cycle = TweenSpec.Cycle.ONCE;
		ts.interp = Interpolation.linear;
		ts.period = 2.0f;
		ts.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				ComponentMappers.transform.get(e).position.y = a;
			}
		};
		return ts;
	}

	private TweenSpec mapInTween() {
		TweenSpec ts = new TweenSpec();
		ts.start = MAP_Y_OFF;
		ts.end =  MAP_Y_ON;
		ts.cycle = TweenSpec.Cycle.ONCE;
		ts.interp = Interpolation.linear;
		ts.period = 2.0f;
		ts.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				ComponentMappers.transform.get(e).position.y = a;
			}

			@Override
			public void endTween(Entity e) {
				currentState = GameState.MAZING;
				ComponentMappers.mapscreen.get(e).currentState = MapScreenComponent.State.EXPLORING;
			}
		};
		return ts;
	}

	private TweenSpec pipeOutTween() {
		TweenSpec ts = new TweenSpec();
		ts.start = PIPE_X;
		ts.end = PIPE_X + BasicScreen.WORLD_WIDTH;
		ts.cycle = TweenSpec.Cycle.ONCE;
		ts.interp = Interpolation.linear;
		ts.period = 2.0f;
		ts.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				ComponentMappers.transform.get(e).position.x = a;
			}

			@Override
			public void endTween(Entity e) {
				engine.removeSystem(pipeSystem);
				engine.removeEntity(pipeSystemEntity);
			}
		};
		return ts;
	}

	private TweenSpec pipeInTween() {
		TweenSpec ts = new TweenSpec();
		ts.start = PIPE_SCALE_SMALL;
		ts.end =  PIPE_SCALE_LARGE;
		ts.cycle = TweenSpec.Cycle.ONCE;
		ts.interp = Interpolation.linear;
		ts.period = 2.0f;
		ts.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				ComponentMappers.transform.get(e).scale.x = a;
				ComponentMappers.transform.get(e).scale.y = a;
			}

			@Override
			public void endTween(Entity e) {
				currentState = GameState.PIPING;
				pipeSystem.start();
			}
		};
		return ts;
	}

	private void setBackgroundPosition() {
		MapScreenComponent msc = ComponentMappers.mapscreen.get(mapSystemEntity);
		float width = backgroundTexComp.region.getRegionWidth();
		float height = backgroundTexComp.region.getRegionHeight();
		float x = msc.playerPosition.x * BACKGROUND_MOVEMENT_FACTOR * width / backgroundTexComp.size.x;
		float y = msc.playerPosition.y * BACKGROUND_MOVEMENT_FACTOR * height / backgroundTexComp.size.y;
		backgroundTexComp.region.setRegionX((int)(x));
		backgroundTexComp.region.setRegionWidth((int)width);
		backgroundTexComp.region.setRegionY(-1 * (int)(y));
		backgroundTexComp.region.setRegionHeight((int)height);
	}

	private void addMapSystem() {
		mapSystemEntity = new Entity();

		MapScreenComponent msc = new MapScreenComponent();
		msc.viewSize = GameParameters.BASE_VIEWSIZE;
		mapSystemEntity.add(msc);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = MAP_X;
		tranc.position.y = BasicScreen.WORLD_HEIGHT / 2.0f + 0.3f;
		tranc.position.z = 10.0f;
		mapSystemEntity.add(tranc);

		mapSystemEntity.add(new TweenComponent());

		add(mapSystemEntity);
		add(new MapSystem(mapSystemEntity, 8.25f, 4.75f));
	}

	private void addHelmetSystem() {
		helmetSystemEntity = new Entity();

		HelmetScreenComponent helmetScreenComponent = new HelmetScreenComponent();
		for (Resource r: Resource.values()) {
			helmetScreenComponent.maxCount.put(r, GameParameters.RESOURCE_MAX.get(r));
			helmetScreenComponent.resourceCount.put(r, GameParameters.RESOURCE_MAX.get(r));
		}
		helmetSystemEntity.add(helmetScreenComponent);

		add(helmetSystemEntity);
		add(new HelmetSystem(helmetSystemEntity));
	}

	private void addPipeSystem(Resource resource) {
		pipeSystemEntity = new Entity();

		PipeScreenComponent pipeScreenComp = new PipeScreenComponent();
		pipeScreenComp.currentState = PipeScreenComponent.State.PAUSED;
		pipeScreenComp.resource = resource;
		pipeSystemEntity.add(pipeScreenComp);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = PIPE_X;
		tranc.position.y = PIPE_Y;
		tranc.position.z = 5.0f;
		tranc.scale.x = PIPE_SCALE_SMALL;
		tranc.scale.y = PIPE_SCALE_SMALL;
		pipeSystemEntity.add(tranc);

		TweenComponent tweenc = new TweenComponent();
		pipeSystemEntity.add(tweenc);

		pipeSystem = new PipeSystem(pipeSystemEntity, 0);
		add(pipeSystemEntity);
		add(pipeSystem);
	}

	private Entity createBackground() {
		Entity e = new Entity();

		backgroundTexComp = new TextureComponent();
		Texture tex = MiscArt.mainBackgroundScrollable;
		float texToCorner = (float)Math.sqrt((tex.getWidth() * tex.getWidth()) + (tex.getHeight() * tex.getHeight()));
		backgroundTexComp.region = new TextureRegion(tex, 0, 0, (int)texToCorner, (int)texToCorner);

		backgroundTexComp.size.x = texToCorner * RenderingSystem.PIXELS_TO_WORLD;
		backgroundTexComp.size.y = texToCorner * RenderingSystem.PIXELS_TO_WORLD;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		transComp.position.z = -1000.0f;

		e.add(backgroundTexComp);
		e.add(transComp);

		return e;
	}

	@Override
	public void backPressed() {
		spacePanic.setScreen(new MenuScreen(spacePanic));
	}
}
