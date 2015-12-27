package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.Art;
import com.apricotjam.spacepanic.art.HelmetUI;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.systems.GameSystem;
import com.apricotjam.spacepanic.systems.MovementSystem;
import com.apricotjam.spacepanic.systems.RenderingSystem;
import com.apricotjam.spacepanic.systems.ScrollSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameScreen extends BasicScreen {

	public static float OVERLAYZ = 1000.0f;
	/*public static Array<Vector2> SCREWLOCATIONS = {
		new Vector2(1.5f, 6.4f),
		new Vector2(0.85f, 2.85f),
		new Vector2(4.3f, 0.85f)
	}*/

	public GameScreen(SpacePanic spacePanic) {
		super(spacePanic);

		add(new RenderingSystem(spriteBatch, worldCamera));
		add(new MovementSystem());
		add(new ScrollSystem());
		add(new GameSystem());

		add(createBackground());
		add(createOverlayBase());
		add(createPipes(true));
		add(createPipes(false));
	}

	private Entity createBackground() {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		Texture tex = MiscArt.mainBackground;
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

	private Entity createOverlayBase() {
		Entity e = new Entity();
		e.add(new HelmetPartComponent());

		TextureComponent texComp = new TextureComponent();
		texComp.region = Art.createTextureRegion(HelmetUI.base);
		texComp.size.x = BasicScreen.WORLD_WIDTH;
		texComp.size.y = BasicScreen.WORLD_HEIGHT;
		e.add(texComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		transComp.position.z = OVERLAYZ;
		e.add(transComp);

		return e;
	}

	private Entity createPipes(boolean left) {
		Entity e = new Entity();
		e.add(new HelmetPartComponent());

		TextureComponent texComp = new TextureComponent();
		if (left) {
			texComp.region = Art.createTextureRegion(HelmetUI.pipesLeft);
		} else {
			texComp.region = Art.createTextureRegion(HelmetUI.pipesRight);
		}
		texComp.size.x = BasicScreen.WORLD_WIDTH / 3.0f;
		texComp.size.y = BasicScreen.WORLD_HEIGHT / 5.0f;
		texComp.centre = false;
		e.add(texComp);

		TransformComponent transformComponent = new TransformComponent();
		if (left) {
			transformComponent.position.x = 0.0f;
		} else {
			transformComponent.position.x = BasicScreen.WORLD_WIDTH * 2.0f / 3.0f;
		}
		transformComponent.position.y = 0.0f;
		transformComponent.position.z = OVERLAYZ + 1;
		e.add(transformComponent);

		return e;
	}

	@Override
	public void backPressed() {
		spacePanic.setScreen(new MenuScreen(spacePanic));
	}
}
