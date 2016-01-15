package com.apricotjam.spacepanic.testscreen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.components.helmet.HelmetScreenComponent;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.apricotjam.spacepanic.systems.*;
import com.apricotjam.spacepanic.systems.helmet.HelmetSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;

public class GameOverTestScreen extends BasicScreen {
	private Entity helmetSystemEntity;
	private Entity pipeSystemEntity;

	public GameOverTestScreen(SpacePanic spacePanic) {
		super(spacePanic);

		helmetSystemEntity = createHelmetMasterEntity();

		add(createBackground());

		add(new HelmetSystem(helmetSystemEntity));
		add(new MovementSystem());
		add(new ScrollSystem());
		add(new ClickSystem());
		add(new TweenSystem());
		add(new AnimationSystem());
		add(new AnimatedShaderSystem());
		add(new ShaderLightingSystem());
		add(new TickerSystem());
		add(new SoundSystem());

		add(helmetSystemEntity);
		add(createGameOver());
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
		entity.add(transComp);

		TweenComponent tweenComponent = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = -2f;
		tweenSpec.end = 1.0f;
		tweenSpec.period = 6f;
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

	private Entity createHelmetMasterEntity() {
		Entity entity = new Entity();
		entity.add(new HelmetScreenComponent());

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2f;
		transComp.position.z = 20.0f;
		transComp.scale.x = 1f;
		transComp.scale.y = 1f;
		entity.add(transComp);

		TweenComponent tweenComp = new TweenComponent();
		TweenSpec ts = new TweenSpec();
		ts.start = 0f;
		ts.end = 4f;
		ts.cycle = TweenSpec.Cycle.ONCE;
		ts.interp = Interpolation.linear;
		ts.period = 8.0f;
		ts.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				TransformComponent tc = ComponentMappers.transform.get(e);
				tc.scale.x = Math.max(a, 1f);
				tc.scale.y = Math.max(a, 1f);
			}
		};
		tweenComp.tweenSpecs.add(ts);
		entity.add(tweenComp);

		return entity;
	}

	private Entity createBackground() {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		Texture tex = MiscArt.mainBackgroundScrollable;
		float texToCorner = (float) Math.sqrt((tex.getWidth() * tex.getWidth()) + (tex.getHeight() * tex.getHeight()));
		texComp.region = new TextureRegion(tex, 0, 0, (int) texToCorner, (int) texToCorner);
		tex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		texComp.size.x = texToCorner * RenderingSystem.PIXELS_TO_WORLD;
		texComp.size.y = texToCorner * RenderingSystem.PIXELS_TO_WORLD;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		transComp.position.z = -100.0f;

		MovementComponent movementComp = new MovementComponent();
		movementComp.rotationalVelocity = 5.0f;

		ScrollComponent scrollComp = new ScrollComponent();
		scrollComp.speed.x = 0.5f;

		e.add(texComp);
		e.add(transComp);
		e.add(movementComp);
		e.add(scrollComp);

		return e;
	}

	@Override
	public void backPressed() {
		// TODO Auto-generated method stub

	}
}
