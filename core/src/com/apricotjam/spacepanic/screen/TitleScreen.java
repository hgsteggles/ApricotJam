package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.Art;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.apricotjam.spacepanic.systems.RenderingSystem;
import com.apricotjam.spacepanic.systems.TweenSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;

public class TitleScreen extends BasicScreen {

	public TitleScreen(SpacePanic spacePanic) {
		super(spacePanic);

		add(new ClickSystem());
		add(new TweenSystem());

		add(createTitleEntity());
		add(createClickEntity());
		add(createBackground());
	}

	@Override
	public void backPressed() {
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

		TweenComponent tweenComponent = new TweenComponent();
		TweenSpec tweenSpec = new TweenSpec();
		tweenSpec.start = 1.0f;
		tweenSpec.end = 0.0f;
		tweenSpec.period = 0.8f;
		tweenSpec.interp = Interpolation.linear;
		tweenSpec.cycle = TweenSpec.Cycle.LOOP;
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
				spacePanic.setScreen(new MenuScreen(spacePanic));
			}
		};

		clickEntity.add(fontComp);
		clickEntity.add(transComp);
		clickEntity.add(clickComp);
		clickEntity.add(tweenComponent);

		return clickEntity;
	}

	private Entity createBackground() {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		texComp.region = Art.createTextureRegion(MiscArt.mainBackground);
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
