package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.gameelements.MenuButton;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.misc.EntityUtil;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.apricotjam.spacepanic.systems.TweenSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;

import java.util.ArrayList;

public class AboutScreen extends BasicScreen {

	public static class Credit {
		public String name = "";
		public String credit = "";
		public String linkText = "";
		public String linkUrl = "";

		public Credit(String name, String credit, String linkText, String linkUrl) {
			this.name = name;
			this.credit = credit;
			this.linkText = linkText;
			this.linkUrl = linkUrl;
		}
	}

	private static final float TITLE_Y = WORLD_HEIGHT * 5.0f / 6.0f;
	private static final float TITLE_TIME = 1.0f;

	private static final float CREDITS_X = WORLD_WIDTH / 2.0f + 1.0f;
	private static final float CREDITS_Y = WORLD_HEIGHT / 2.0f - 1.65f;
	private static final float CREDITS_SPACING = 0.6f;
	private static final float LINK_X = WORLD_WIDTH / 2.0f + 5.8f;
	private static final float BACK_X = 1.5f;
	private static final float BACK_Y = WORLD_HEIGHT - 0.5f;

	private static final String TEXTMADEFOR = "Made for the LibGDX gamejam 2015";
	private static final String TEXTTHEME = "Theme: \"Life in space\"";
	private static final String TEXTSRC = "Source code on ";
	private static final String TEXTGITHUB = "GITHUB";
	private static final String URLGITHUB = "https://github.com/ridoncules/ApricotJam";
	private static final String TEXTMADEBY = "Made by:";

	private static final ArrayList<Credit> CREDITS = new ArrayList<Credit>();

	static {
		CREDITS.add(new Credit("Russ MacCharles (PCGS)", "Art", "FACEBOOK", "https://facebook.com/pcgamestudio"));
		CREDITS.add(new Credit("Jacob Close", "Code", "GITHUB", "https://github.com/drumber-1"));
		CREDITS.add(new Credit("Harry Steggles", "Code", "GITHUB", "https://github.com/ridoncules"));
		CREDITS.add(new Credit("Rory Hebblethwaite", "Music", "SOUNDCLOUD", "https://soundcloud.com/rjhmusic-1"));
		CREDITS.add(new Credit("Jordan Swales", "Music", "", ""));
	}

	private Entity title;
	private Entity astronaut;
	private Entity background;

	public AboutScreen(SpacePanic spacePanic, Entity title, Entity astronaut, Entity background) {
		super(spacePanic);

		add(new ClickSystem());
		add(new TweenSystem());

		this.title = EntityUtil.clone(title);
		EntityUtil.addTitleTween(this.title, TITLE_Y, TITLE_TIME);
		add(this.title);

		this.astronaut = EntityUtil.clone(astronaut);
		add(this.astronaut);

		this.background = EntityUtil.clone(background);
		add(this.background);

		add(createText(WORLD_WIDTH / 2.0f, WORLD_HEIGHT * 2.0f / 3.0f, TEXTMADEFOR, 0));
		add(createText(WORLD_WIDTH / 2.0f, WORLD_HEIGHT * 2.0f / 3.0f - 0.5f, TEXTTHEME, 1));
		add(createText(WORLD_WIDTH / 2.0f - 1.5f, WORLD_HEIGHT * 2.0f / 3.0f - 1.5f, TEXTSRC, 2));

		MenuButton butSrc = new MenuButton(WORLD_WIDTH / 2.0f + 3.0f, WORLD_HEIGHT * 2.0f / 3.0f - 1.5f, TEXTGITHUB, new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				Gdx.net.openURI(URLGITHUB);
			}
		});
		butSrc.addToEngine(engine);

		add(createText(WORLD_WIDTH / 2.0f, WORLD_HEIGHT * 2.0f / 3.0f - 2.4f, TEXTMADEBY, 2));

		for (int i = 0; i < CREDITS.size(); i++) {
			addCredit(CREDITS.get(i), i);
		}

		MenuButton butBack = new MenuButton(BACK_X, BACK_Y, 2.0f, "BACK", new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				backPressed();
			}
		});
		butBack.addToEngine(engine);
	}

	@Override
	public void backPressed() {
		spacePanic.setScreen(new MenuScreen(spacePanic, title, astronaut, background));
	}

	public void addCredit(final Credit credit, int n) {
		String fullText = credit.name + "-" + credit.credit;
		float offset = (fullText.length() - (credit.name.length() * 2.0f)) / 5.0f;
		Entity text = createText(CREDITS_X + offset, CREDITS_Y - (n * CREDITS_SPACING), fullText);
		add(text);
		if (credit.linkUrl != "") {
			MenuButton link = new MenuButton(LINK_X, CREDITS_Y - (n * CREDITS_SPACING), credit.linkText, new ClickInterface() {
				@Override
				public void onClick(Entity entity) {
					Gdx.net.openURI(credit.linkUrl);
				}
			});
			link.addToEngine(engine);
		}
	}

	private Entity createText(float x, float y, String text, int n) {
		Entity entity = createText(x, y, text);

		return entity;
	}
}
