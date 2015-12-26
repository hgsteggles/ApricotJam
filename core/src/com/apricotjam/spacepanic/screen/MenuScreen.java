package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.Art;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.apricotjam.spacepanic.systems.TweenSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;

public class MenuScreen extends BasicScreen {

	private static final float TITLETIME = 1.0f;
	private static final float TITLEENDPOSITION = WORLD_HEIGHT * 3.0f / 4.0f;

	private Entity startButton;
	private Entity title;

	public MenuScreen(SpacePanic spacePanic) {
		super(spacePanic);
		add(new ClickSystem());
		add(new TweenSystem());

		add(createTitleEntity());
		add(createStartButton());

	}

	private void startGame() {
		System.out.println("Starting game!");
		spacePanic.setScreen(new GameScreen(spacePanic));
	}

	public Entity createTitleEntity() {
		Entity titleEntity = new Entity();

		TextureComponent textComp = new TextureComponent();
		textComp.region = Art.createTextureRegion(MiscArt.title);
		textComp.size.x = 5.0f;
		textComp.size.y = textComp.size.x * textComp.region.getRegionHeight() / textComp.region.getRegionWidth();

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2f;

		TweenComponent tweenComp = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = transComp.position.y;
		tweenSpec.end = TITLEENDPOSITION;
		tweenSpec.period = TITLETIME;
		tweenSpec.cycle = TweenSpec.Cycle.REVERSE;
		tweenSpec.interp = Interpolation.linear;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				TransformComponent tc = ComponentMappers.transform.get(e);
				tc.position.y = a;
			}
		};
		tweenComp.tweenSpecs.add(tweenSpec);

		titleEntity.add(textComp);
		titleEntity.add(transComp);
		titleEntity.add(tweenComp);

		return titleEntity;
	}

	public Entity createStartButton() {
		Entity clickEntity = new Entity();

		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "retro";
		fontComp.string = "START";
		fontComp.color = Color.WHITE;
		fontComp.centering = true;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 4f;

		ClickComponent clickComponent = new ClickComponent();
		clickComponent.clicker = new ClickInterface() {
			@Override
			public void onClick() {
				startGame();
			}
		};
		clickComponent.active = true;
		clickComponent.shape = new Rectangle().setSize(2.0f, 0.5f).setCenter(0.0f, 0.0f);

		TextButtonComponent textButtonComponent = new TextButtonComponent();
		textButtonComponent.base = fontComp.color;
		textButtonComponent.pressed = Color.RED;

		clickEntity.add(fontComp);
		clickEntity.add(transComp);
		clickEntity.add(clickComponent);
		clickEntity.add(textButtonComponent);

		return clickEntity;
	}

	@Override
	public void backPressed() {
		spacePanic.setScreen(new TitleScreen(spacePanic));
	}
}
