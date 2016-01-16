package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.components.TweenSpec.Cycle;
import com.apricotjam.spacepanic.gameelements.GameStats;
import com.apricotjam.spacepanic.gameelements.MenuButton;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.apricotjam.spacepanic.systems.MovementSystem;
import com.apricotjam.spacepanic.systems.ScrollSystem;
import com.apricotjam.spacepanic.systems.TweenSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;

public class GameOverScreen extends BasicScreen {

	private GameStats gameStats;

	public GameOverScreen(SpacePanic spacePanic, Entity backgroundEntity, GameStats gameStats) {
		super(spacePanic);

		this.gameStats = gameStats;

		add(new ClickSystem());
		add(new TweenSystem());
		add(new MovementSystem());
		add(new ScrollSystem());

		addMovementScroll(backgroundEntity);
		add(backgroundEntity);
		add(createGameOver(2.0f, 0.0f));
		add(createScoreMessage(2.0f, 0.5f));

		MenuButton button2 = new MenuButton(BasicScreen.WORLD_WIDTH / 2f, BasicScreen.WORLD_HEIGHT / 4f, "NEW GAME", new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				newGame();
			}
		});
		button2.getTextEntity().add(createTextFadeTween(2.0f, 1.5f));
		button2.getBorderEntity().add(createBorderFadeTween(2.0f, 1.5f));
		ComponentMappers.bitmapfont.get(button2.getTextEntity()).color.a = 0f;
		ComponentMappers.ninepatch.get(button2.getBorderEntity()).color.a = 0f;
		button2.addToEngine(engine);

		MenuButton button = new MenuButton(BasicScreen.WORLD_WIDTH / 2f, BasicScreen.WORLD_HEIGHT / 4f - 1.0f, "MAIN MENU", new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				startMenu();
			}
		});
		button.getTextEntity().add(createTextFadeTween(2.0f, 2.5f));
		button.getBorderEntity().add(createBorderFadeTween(2.0f, 2.5f));
		ComponentMappers.bitmapfont.get(button.getTextEntity()).color.a = 0f;
		ComponentMappers.ninepatch.get(button.getBorderEntity()).color.a = 0f;
		button.addToEngine(engine);
	}

	private TweenComponent createTextFadeTween(float duration, float delay) {
		TweenComponent tweenComp = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.end = 1.0f;
		tweenSpec.start = -1 * delay * tweenSpec.end / duration;
		tweenSpec.period = duration + delay;
		tweenSpec.interp = Interpolation.linear;
		tweenSpec.cycle = TweenSpec.Cycle.ONCE;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				BitmapFontComponent bitmapFontComponent = ComponentMappers.bitmapfont.get(e);
				bitmapFontComponent.color.a = Math.max(a, 0f);
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);

		return tweenComp;
	}

	private TweenComponent createBorderFadeTween(float duration, float delay) {
		TweenComponent tweenComp = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.end = 1.0f;
		tweenSpec.start = -1 * delay * tweenSpec.end / duration;
		tweenSpec.period = duration + delay;
		tweenSpec.interp = Interpolation.linear;
		tweenSpec.cycle = TweenSpec.Cycle.ONCE;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				NinepatchComponent nc = ComponentMappers.ninepatch.get(e);
				nc.color.a = Math.max(a, 0f);
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);

		return tweenComp;
	}

	private void startMenu() {
		spacePanic.setScreen(new MenuScreen(spacePanic));
	}

	private void newGame() {
		spacePanic.setScreen(new GameScreen(spacePanic));
	}

	private Entity createGameOver(float duration, float delay) {
		Entity entity = new Entity();

		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "retro";
		fontComp.string = "GAME OVER";
		fontComp.color.set(1f, 1f, 1f, 0f);
		fontComp.centering = true;
		entity.add(fontComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2f + 1.0f;
		transComp.position.z = 1f;
		entity.add(transComp);

		entity.add(createTextFadeTween(duration, delay));

		return entity;
	}

	private Entity createScoreMessage(float duration, float delay) {
		Entity entity = new Entity();

		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "retro";
		fontComp.string = "You survived for " + Math.round(gameStats.timeAlive) + " seconds";
		fontComp.color.set(1f, 1f, 1f, 0f);
		fontComp.centering = true;
		entity.add(fontComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2f + 0.25f;
		transComp.position.z = 1f;
		entity.add(transComp);

		entity.add(createTextFadeTween(duration, delay));

		return entity;
	}

	private void addMovementScroll(Entity entity) {
		MovementComponent movementComp = new MovementComponent();
		movementComp.rotationalVelocity = 0.0f;
		entity.add(movementComp);

		ScrollComponent scrollComp = new ScrollComponent();
		scrollComp.speed.x = 0.0f;
		entity.add(scrollComp);

		TweenComponent tweenComp = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 0f;
		tweenSpec.end = 1f;
		tweenSpec.period = 2f;
		tweenSpec.cycle = Cycle.ONCE;
		tweenSpec.interp = Interpolation.linear;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				ComponentMappers.movment.get(e).rotationalVelocity = 3.0f * a;
				ComponentMappers.scroll.get(e).speed.x = 0.5f * a;
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);
		entity.add(tweenComp);
	}

	@Override
	public void backPressed() {
	}

}
