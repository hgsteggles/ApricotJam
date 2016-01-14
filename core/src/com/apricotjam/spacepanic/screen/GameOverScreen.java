package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.BitmapFontComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.MovementComponent;
import com.apricotjam.spacepanic.components.NinepatchComponent;
import com.apricotjam.spacepanic.components.ScrollComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.components.TweenComponent;
import com.apricotjam.spacepanic.components.TweenSpec;
import com.apricotjam.spacepanic.components.TweenSpec.Cycle;
import com.apricotjam.spacepanic.gameelements.MenuButton;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.apricotjam.spacepanic.systems.MovementSystem;
import com.apricotjam.spacepanic.systems.RenderingSystem;
import com.apricotjam.spacepanic.systems.ScrollSystem;
import com.apricotjam.spacepanic.systems.TweenSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;

public class GameOverScreen extends BasicScreen {

	public GameOverScreen(SpacePanic spacePanic, Entity backgroundEntity) {
		super(spacePanic);

		add(new ClickSystem());
		add(new TweenSystem());
		add(new MovementSystem());
		add(new ScrollSystem());
		
		addMovementScroll(backgroundEntity);
		add(backgroundEntity);
		add(createGameOver());
		
		MenuButton button = new MenuButton(BasicScreen.WORLD_WIDTH/2f, BasicScreen.WORLD_HEIGHT/4f, 0, 4f, 1f, "Main Menu", new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				startMenu();
			}
		});
		button.getBorderEntity().add(createBorderTween());
		button.getTextEntity().add(createMainMenuTween());
		
		ComponentMappers.ninepatch.get(button.getBorderEntity()).color.a = 0f;
		ComponentMappers.bitmapfont.get(button.getTextEntity()).color.a = 0f;
		
		add(button.getBorderEntity());
		add(button.getTextEntity());
	}
	
	private TweenComponent createMainMenuTween() {
		TweenComponent tweenComp = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = -1.0f;
		tweenSpec.end = 1.0f;
		tweenSpec.period = 4f;
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
	
	private void startMenu() {
		spacePanic.setScreen(new MenuScreen(spacePanic));
	}
	
	private Entity createGameOver() {
		Entity entity = new Entity();
		
		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "retro";
		fontComp.string = "GAME OVER";
		fontComp.color.set(1f, 1f, 1f, 0f);
		fontComp.centering = true;
		entity.add(fontComp);
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2f;
		transComp.position.z = 1f;
		entity.add(transComp);
		
		TweenComponent tweenComponent = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 0f;
		tweenSpec.end = 1.0f;
		tweenSpec.period = 2f;
		tweenSpec.interp = Interpolation.linear;
		tweenSpec.cycle = TweenSpec.Cycle.ONCE;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				BitmapFontComponent bitmapFontComponent = ComponentMappers.bitmapfont.get(e);
				bitmapFontComponent.color.a = Math.max(a, 0f);
			}
		};
		tweenComponent.tweenSpecs.add(tweenSpec);
		entity.add(tweenComponent);
		
		return entity;
	}
	
	private TweenComponent createBorderTween() {
		TweenComponent tweenComp = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = -1.0f;
		tweenSpec.end = 1.0f;
		tweenSpec.period = 4f;
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
	
	private void addMovementScroll(Entity entity) {
		MovementComponent movementComp = new MovementComponent();
		movementComp.rotationalVelocity = 5.0f;
		entity.add(movementComp);
		
		ScrollComponent scrollComp = new ScrollComponent();
		scrollComp.speed.x = 0.5f;
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
				ComponentMappers.movment.get(e).rotationalVelocity = 5.0f*a;
				ComponentMappers.scroll.get(e).speed.x = 0.5f*a;
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);
		entity.add(tweenComp);
	}

	@Override
	public void backPressed() {
	}

}
