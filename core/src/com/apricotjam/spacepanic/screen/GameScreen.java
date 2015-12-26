package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.Art;
import com.apricotjam.spacepanic.art.GameCommon;
import com.apricotjam.spacepanic.components.MovementComponent;
import com.apricotjam.spacepanic.components.ScrollComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.systems.MovementSystem;
import com.apricotjam.spacepanic.systems.RenderingSystem;
import com.apricotjam.spacepanic.systems.ScrollSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameScreen extends BasicScreen {
	public GameScreen(SpacePanic spacePanic) {
		super(spacePanic);

		add(new RenderingSystem(spriteBatch, worldCamera));
		add(new MovementSystem());
		add(new ScrollSystem());

		add(createOverlay());
		add(createBackground());
	}

	private Entity createOverlay() {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		texComp.region = Art.createTextureRegion(GameCommon.mainOverlay);
		texComp.size.x = BasicScreen.WORLD_WIDTH;
		texComp.size.y = BasicScreen.WORLD_HEIGHT;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		transComp.position.z = Float.MAX_VALUE;

		e.add(texComp);
		e.add(transComp);

		return e;
	}

	private Entity createBackground() {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		Texture tex = GameCommon.mainBackground;
		float texToCorner = (float)Math.sqrt((tex.getWidth() * tex.getWidth()) + (tex.getHeight() * tex.getHeight()));
		texComp.region = new TextureRegion(tex, 0, 0, (int)texToCorner, (int)texToCorner);
		tex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		texComp.size.x = texToCorner * RenderingSystem.PIXELS_TO_WORLD;
		texComp.size.y = texToCorner * RenderingSystem.PIXELS_TO_WORLD;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		transComp.position.z = Float.MIN_VALUE;

		MovementComponent movementComp = new MovementComponent();
		movementComp.rotationalVelocity = 10.0f;

		ScrollComponent scrollComp = new ScrollComponent();
		scrollComp.speed.x = 1.0f;

		e.add(texComp);
		e.add(transComp);
		e.add(movementComp);
		e.add(scrollComp);

		return e;
	}

	@Override
	public void backPressed() {
		spacePanic.setScreen(new MenuScreen(spacePanic));
	}
}
