package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class MenuScreen extends BasicScreen {

	private static final float TITLESPEED = 3.5f;
	private static final float TITLEENDPOSITION = WORLD_HEIGHT * 3.0f / 4.0f;

	private Entity startButton;
	private Entity title;

	public MenuScreen(SpacePanic spacePanic) {
		super(spacePanic);
		add(new ClickSystem());

		title = createTitleEntity();
		add(title);
		startButton = createStartButton();
		add(startButton);

	}

	private void startGame() {
		System.out.println("So the game would start now...");
	}

	public Entity createTitleEntity() {
		Entity titleEntity = new Entity();

		TextureComponent textComp = new TextureComponent();
		textComp.region = MiscArt.title;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2f;
		transComp.size.x = 5.0f;
		transComp.size.y = transComp.size.x * textComp.region.getRegionHeight() / textComp.region.getRegionWidth();

		titleEntity.add(textComp);
		titleEntity.add(transComp);

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
	public void render(float delta) {
		super.render(delta);

		TransformComponent titleTransform = ComponentMappers.transform.get(title);
		if (titleTransform.position.y < TITLEENDPOSITION) {
			titleTransform.position.y += delta * TITLESPEED;
		}
	}

	@Override
	public void backPressed() {
		spacePanic.setScreen(new TitleScreen(spacePanic));
	}
}
