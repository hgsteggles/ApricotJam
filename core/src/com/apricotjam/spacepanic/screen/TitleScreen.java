package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.misc.EntityUtil;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.apricotjam.spacepanic.systems.TweenSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

public class TitleScreen extends BasicScreen {

	private static float TITLE_Y = BasicScreen.WORLD_HEIGHT / 2f;
	private static float TITLE_TIME = 2.0f;

	private Entity title;
	private Entity astronaut;
	private Entity background;

	public TitleScreen(SpacePanic spacePanic) {
		this(spacePanic, EntityUtil.createTitleEntity(TITLE_Y), EntityUtil.createAstronaut(), EntityUtil.createBackground());
		EntityUtil.addAstronautToTitle(astronaut, ComponentMappers.transform.get(title));
	}

	public TitleScreen(SpacePanic spacePanic, Entity title, Entity astronaut, Entity background) {
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

		add(createClickEntity());
	}

	@Override
	public void backPressed() {
		spacePanic.exit();
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

		TweenComponent tweenComponent = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 1.0f;
		tweenSpec.end = 0.0f;
		tweenSpec.period = 1.2f;
		tweenSpec.interp = Interpolation.linear;
		tweenSpec.cycle = TweenSpec.Cycle.INFLOOP;
		tweenSpec.tweenInterface = new TweenInterface() {
			@Override
			public void applyTween(Entity e, float a) {
				BitmapFontComponent bitmapFontComponent = ComponentMappers.bitmapfont.get(e);
				if (a > 0.5) {
					bitmapFontComponent.color.a = 1.0f;
				} else {
					bitmapFontComponent.color.a = 0.0f;
				}
			}
		};
		tweenComponent.tweenSpecs.add(tweenSpec);

		ClickComponent clickComp = new ClickComponent();
		clickComp.clicker = new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				spacePanic.setScreen(new MenuScreen(spacePanic, title, astronaut, background));
			}
		};

		clickEntity.add(fontComp);
		clickEntity.add(transComp);
		clickEntity.add(clickComp);
		clickEntity.add(tweenComponent);

		return clickEntity;
	}
}
