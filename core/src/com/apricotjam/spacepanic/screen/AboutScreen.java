package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.gameelements.MenuButton;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
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

	private static final float TITLETIME = 1.0f;
	private static final float TITLEENDPOSITION = WORLD_HEIGHT * 5.0f / 6.0f;

	private static final float CREDITS_X = WORLD_WIDTH / 2.0f + 1.0f;
	private static final float CREDITS_Y = WORLD_HEIGHT / 2.0f - 1.75f;
	private static final float CREDITS_SPACING = 0.5f;
	private static final float LINK_X = WORLD_WIDTH / 2.0f + 6.0f;
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
		CREDITS.add(new Credit("Rory Hebblethwaite", "Music", "", ""));
		CREDITS.add(new Credit("Jordan Swales", "Music", "", ""));
	}

	Entity title;

	public AboutScreen(SpacePanic spacePanic, float titleHeight) {
		super(spacePanic);

		add(new ClickSystem());
		add(new TweenSystem());

		title = createTitleEntity(titleHeight);
		add(title);
		add(createBackground());

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

		add(createText(WORLD_WIDTH / 2.0f, WORLD_HEIGHT * 2.0f / 3.0f - 2.5f, TEXTMADEBY, 2));

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
		spacePanic.setScreen(new MenuScreen(spacePanic, ComponentMappers.transform.get(title).position.y));
	}

	public Entity createTitleEntity(float startPosition) {
		Entity titleEntity = new Entity();

		TextureComponent textComp = new TextureComponent();
		textComp.region = MiscArt.title;
		textComp.size.x = 5.0f;
		textComp.size.y = textComp.size.x * textComp.region.getRegionHeight() / textComp.region.getRegionWidth();

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2f;
		transComp.position.y = startPosition;

		TweenComponent tweenComp = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = transComp.position.y;
		tweenSpec.end = TITLEENDPOSITION;
		tweenSpec.period = TITLETIME;
		tweenSpec.cycle = TweenSpec.Cycle.ONCE;
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

	public void addCredit(final Credit credit, int n) {
		String fullText = credit.name + " - " + credit.credit;
		float offset = (fullText.length() - (credit.name.length() * 2.0f)) / 5.0f;
		Entity text = createText(CREDITS_X + offset, CREDITS_Y - (n * CREDITS_SPACING), fullText);
		add(text);
		MenuButton link = new MenuButton(LINK_X, CREDITS_Y - (n * CREDITS_SPACING), credit.linkText, new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				Gdx.net.openURI(credit.linkUrl);
			}
		});
		link.addToEngine(engine);
	}

	private Entity createText(float x, float y, String text, int n) {
		Entity entity = createText(x, y, text);

		return entity;
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
}
