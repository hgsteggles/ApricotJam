package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.gameelements.GameSettings;
import com.apricotjam.spacepanic.gameelements.MenuButton;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.misc.EntityUtil;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.apricotjam.spacepanic.systems.TweenSystem;
import com.badlogic.ashley.core.Entity;

public class MenuScreen extends BasicScreen {

	private static final float TITLE_Y = WORLD_HEIGHT * 3.0f / 4.0f;
	private static final float TITLE_TIME = 1.0f;

	private static final float BUTTONS_X = WORLD_WIDTH / 2.0f;
	private static final float BUTTONS_Y = WORLD_HEIGHT / 4.0f + 1.0f;
	private static final float BUTTONS_SPACING = 0.7f;

	private Entity title;
	private Entity astronaut;
	private Entity background;

	public MenuScreen(SpacePanic spacePanic, Entity background) {
		this(spacePanic, EntityUtil.createTitleEntity(TITLE_Y), EntityUtil.createAstronaut(), background);
		EntityUtil.addAstronautToTitle(astronaut, ComponentMappers.transform.get(title));
	}

	public MenuScreen(SpacePanic spacePanic, Entity title, Entity astronaut, Entity background) {
		super(spacePanic);
		add(new ClickSystem());
		add(new TweenSystem());

		this.title = EntityUtil.clone(title);
		add(this.title);
		EntityUtil.addTitleTween(this.title, TITLE_Y, TITLE_TIME);

		this.astronaut = EntityUtil.clone(astronaut);
		add(this.astronaut);

		this.background = EntityUtil.clone(background);
		add(this.background);

		addMenuItem(BUTTONS_X, BUTTONS_Y, "START", new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				startGame();
			}
		}, 0);

		String sound;
		if (GameSettings.isSoundOn()) {
			sound = "SOUND ON";
		} else {
			sound = "SOUND OFF";
		}
		addMenuItem(BUTTONS_X, BUTTONS_Y - BUTTONS_SPACING, sound, new ClickInterface() {
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
		}, 1);

		addMenuItem(BUTTONS_X, BUTTONS_Y - 2 * BUTTONS_SPACING, "ABOUT", new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				aboutScreen();
			}
		}, 2);
	}

	private void startGame() {
		System.out.println("Starting game!");
		spacePanic.setScreen(new IntroScreen(spacePanic, background));
	}

	private void aboutScreen() {
		spacePanic.setScreen(new AboutScreen(spacePanic, title, astronaut, background));
	}

	private void addMenuItem(float x, float y, String text, ClickInterface clickInterface, int n) {
		MenuButton menuButton = new MenuButton(x, y, 3.7f, text, clickInterface);
		menuButton.addToEngine(engine);
	}

	@Override
	public void backPressed() {
		spacePanic.setScreen(new TitleScreen(spacePanic, title, astronaut, background));
	}
}
