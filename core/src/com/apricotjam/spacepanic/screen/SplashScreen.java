package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.PipeGameArt;
import com.apricotjam.spacepanic.art.SplashArt;
import com.apricotjam.spacepanic.components.ClickComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.components.TweenComponent;
import com.apricotjam.spacepanic.components.TweenSpec;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.apricotjam.spacepanic.systems.TweenSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;

public class SplashScreen extends BasicScreen {

	private static final float LOGO_SCALE = 0.015f;

	private static final float TOAST_X = BasicScreen.WORLD_WIDTH / 2.0f;
	private static final float TOAST_Y = BasicScreen.WORLD_HEIGHT / 2.0f;
	private static final float APRICOT_X = BasicScreen.WORLD_WIDTH / 2.0f - 2.8f;
	private static final float APRICOT_Y = BasicScreen.WORLD_HEIGHT / 2.0f;
	private static final float JAM_X = BasicScreen.WORLD_WIDTH / 2.0f + 1.9f;
	private static final float JAM_Y = BasicScreen.WORLD_HEIGHT / 2.0f;

	private static final float TIME = 1.5f;
	private static final float LOGO_TIME = 1.0f;

	public SplashScreen(SpacePanic spacePanic) {
		super(spacePanic);

		add(new ClickSystem());
		add(new TweenSystem());

		add(createClickEntity());

		add(createBG());
		add(createToast());
		add(createApricot());
		add(createJam());

		add(createTimerEntity());
	}
	
	private Entity createBG() {
		Entity entity = new Entity();
		
		TextureComponent texComp = new TextureComponent();
		texComp.region = PipeGameArt.whitePixel;
		texComp.size.x = 2f*BasicScreen.WORLD_WIDTH;
		texComp.size.y = 2f*BasicScreen.WORLD_HEIGHT;
		texComp.color.set(0f, 0f, 0f, 1f);
		entity.add(texComp);
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH/2f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT/2f;
		transComp.position.z = -1000;
		entity.add(transComp);
		
		return entity;
	}

	public Entity createToast() {
		Entity entity = new Entity();

		TextureComponent textComp = new TextureComponent();
		textComp.region = SplashArt.toast;
		textComp.size.x = textComp.region.getRegionWidth() * LOGO_SCALE;
		textComp.size.y = textComp.region.getRegionHeight() * LOGO_SCALE;
		entity.add(textComp);

		TransformComponent transformComponent = new TransformComponent();
		transformComponent.position.x = TOAST_X;
		transformComponent.position.y = -textComp.size.y;
		entity.add(transformComponent);

		TweenComponent tweenComponent = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = -textComp.size.y;
		tweenSpec.end = TOAST_Y;
		tweenSpec.period = LOGO_TIME;
		tweenSpec.interp = new Interpolation.SwingOut(1.0f);
		tweenSpec.cycle = TweenSpec.Cycle.ONCE;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				ComponentMappers.transform.get(e).position.y = a;
			}
		};
		tweenComponent.tweenSpecs.add(tweenSpec);
		entity.add(tweenComponent);

		return entity;
	}

	public Entity createApricot() {
		Entity entity = new Entity();

		TextureComponent textComp = new TextureComponent();
		textComp.region = SplashArt.apricot;
		textComp.size.x = textComp.region.getRegionWidth() * LOGO_SCALE;
		textComp.size.y = textComp.region.getRegionHeight() * LOGO_SCALE;
		entity.add(textComp);

		TransformComponent transformComponent = new TransformComponent();
		transformComponent.position.x = -textComp.size.x;
		transformComponent.position.y = APRICOT_Y;
		entity.add(transformComponent);

		TweenComponent tweenComponent = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = -textComp.size.x;
		tweenSpec.end = APRICOT_X;
		tweenSpec.period = LOGO_TIME;
		tweenSpec.interp = new Interpolation.SwingOut(1.0f);
		tweenSpec.cycle = TweenSpec.Cycle.ONCE;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				ComponentMappers.transform.get(e).position.x = a;
			}
		};
		tweenComponent.tweenSpecs.add(tweenSpec);
		entity.add(tweenComponent);

		return entity;
	}

	public Entity createJam() {
		Entity entity = new Entity();

		TextureComponent textComp = new TextureComponent();
		textComp.region = SplashArt.jam;
		textComp.size.x = textComp.region.getRegionWidth() * LOGO_SCALE;
		textComp.size.y = textComp.region.getRegionHeight() * LOGO_SCALE;
		entity.add(textComp);

		TransformComponent transformComponent = new TransformComponent();
		transformComponent.position.x = WORLD_WIDTH + textComp.size.x;
		transformComponent.position.y = JAM_Y;
		entity.add(transformComponent);

		TweenComponent tweenComponent = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = WORLD_WIDTH + textComp.size.x;
		tweenSpec.end = JAM_X;
		tweenSpec.period = LOGO_TIME;
		tweenSpec.interp = new Interpolation.SwingOut(1.0f);
		tweenSpec.cycle = TweenSpec.Cycle.ONCE;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				ComponentMappers.transform.get(e).position.x = a;
			}
		};
		tweenComponent.tweenSpecs.add(tweenSpec);
		entity.add(tweenComponent);

		return entity;
	}

	public Entity createClickEntity() {
		Entity clickEntity = new Entity();

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 4f;

		ClickComponent clickComp = new ClickComponent();
		clickComp.clicker = new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				nextScreen();
			}
		};

		clickEntity.add(transComp);
		clickEntity.add(clickComp);

		return clickEntity;
	}

	public Entity createTimerEntity() {
		Entity entity = new Entity();

		TweenComponent tweenComponent = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 0.0f;
		tweenSpec.end = 1.0f;
		tweenSpec.period = TIME;
		tweenSpec.interp = Interpolation.linear;
		tweenSpec.cycle = TweenSpec.Cycle.ONCE;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) { }

			@Override
			public void endTween(Entity e) {
				nextScreen();
			}
		};
		tweenComponent.tweenSpecs.add(tweenSpec);
		entity.add(tweenComponent);

		return entity;
	}

	public void nextScreen() {
		spacePanic.setScreen(new TitleScreen(spacePanic));
	}

	@Override
	public void backPressed() {
		nextScreen();
	}
}
