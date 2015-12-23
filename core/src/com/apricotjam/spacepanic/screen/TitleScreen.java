package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.input.InputManager;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;

public class TitleScreen extends BasicScreen {

	private static final float FLASHPERIOD = 0.8f;
	private float flashTimer = 0.0f;
	private Entity clickEntity;

	public TitleScreen(SpacePanic spacePanic) {
		super(spacePanic);
		add(new ClickSystem());
		add(createTitleEntity());
		add(createClickEntity());
	}

	@Override
	public void backPressed() {
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

	public Entity createClickEntity() {
		Entity clickEntity = new Entity();

		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "retro";
		fontComp.string = "Click to begin!";
		fontComp.color = new Color(Color.WHITE);
		fontComp.centering = true;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 4f;

		ClickComponent clickComp = new ClickComponent();
		clickComp.clicker = new ClickInterface() {
			@Override
			public void onClick() {
				spacePanic.setScreen(new MenuScreen(spacePanic));
			}
		};

		clickEntity.add(fontComp);
		clickEntity.add(transComp);
		clickEntity.add(clickComp);

		return clickEntity;
	}

}
