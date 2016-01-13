package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.Art;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.gameelements.GameSettings;
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

	private static final float BUTTONS_X = WORLD_WIDTH / 2.0f;
	private static final float BUTTONS_Y = WORLD_HEIGHT / 4.0f + 1.0f;
	private static final float BUTTONS_SPACING = 0.6f;

	public MenuScreen(SpacePanic spacePanic) {
		super(spacePanic);
		add(new ClickSystem());
		add(new TweenSystem());

		add(createTitleEntity());
		add(createBackground());

		add(createButton(BUTTONS_X, BUTTONS_Y, "START", new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				startGame();
			}
		}));

		String sound;
		if (GameSettings.isSoundOn()) {
			sound = "SOUND ON";
		} else {
			sound = "SOUND OFF";
		}
		add(createButton(BUTTONS_X, BUTTONS_Y - BUTTONS_SPACING, sound, new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				if (GameSettings.isSoundOn()) {
					GameSettings.setSoundOn(false);
					ComponentMappers.bitmapfont.get(entity).string = "SOUND OFF";
				} else {
					GameSettings.setSoundOn(true);
					ComponentMappers.bitmapfont.get(entity).string = "SOUND ON";
				}
			}
		}));
	}

	private void startGame() {
		System.out.println("Starting game!");
		spacePanic.setScreen(new GameScreen(spacePanic));
	}

	public Entity createTitleEntity() {
		Entity titleEntity = new Entity();

		TextureComponent textComp = new TextureComponent();
		textComp.region = MiscArt.title;
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
		tweenSpec.cycle = TweenSpec.Cycle.INFLOOP;
		tweenSpec.reverse = true;
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

	public Entity createButton(float x, float y, String text, ClickInterface clickInterface) {
		Entity button = new Entity();

		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "retro";
		fontComp.string = text;
		fontComp.color = Color.WHITE;
		fontComp.centering = true;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = x;
		transComp.position.y = y;

		ClickComponent clickComponent = new ClickComponent();
		clickComponent.clicker = clickInterface;
		clickComponent.active = true;
		clickComponent.shape = new Rectangle().setSize(2.0f, 0.5f).setCenter(0.0f, 0.0f);

		TextButtonComponent textButtonComponent = new TextButtonComponent();
		textButtonComponent.base = fontComp.color;
		textButtonComponent.pressed = Color.DARK_GRAY;

		button.add(fontComp);
		button.add(transComp);
		button.add(clickComponent);
		button.add(textButtonComponent);

		return button;
	}

	private Entity createBackground() {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		texComp.region = MiscArt.mainBackground;
		texComp.size.x = BasicScreen.WORLD_WIDTH;
		texComp.size.y = BasicScreen.WORLD_HEIGHT;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		transComp.position.z = -1.0f;

		e.add(texComp);
		e.add(transComp);

		return e;
	}

	@Override
	public void backPressed() {
		spacePanic.setScreen(new TitleScreen(spacePanic));
	}
}
