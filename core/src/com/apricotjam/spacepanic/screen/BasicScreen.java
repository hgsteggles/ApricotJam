package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.components.BitmapFontComponent;
import com.apricotjam.spacepanic.components.ClickComponent;
import com.apricotjam.spacepanic.components.TextButtonComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.input.InputManager;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.systems.RenderingSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class BasicScreen implements Screen {

	public static final float WORLD_WIDTH = 16.0f;
	public static final float WORLD_HEIGHT = 9.0f;
	protected final SpacePanic spacePanic;
	protected int width;
	protected int height;

	protected OrthographicCamera worldCamera;
	protected SpriteBatch spriteBatch;

	protected Engine engine;

	public BasicScreen(SpacePanic spacePanic) {
		this.spacePanic = spacePanic;
		worldCamera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
		worldCamera.position.set(WORLD_WIDTH / 2.0f, WORLD_HEIGHT / 2.0f, 0);
		worldCamera.update();
		InputManager.screenInput.setCamera(worldCamera);

		engine = new Engine();
		spriteBatch = new SpriteBatch();
		engine.addSystem(new RenderingSystem(spriteBatch, worldCamera));
	}

	public void add(Entity e) {
		engine.addEntity(e);
	}

	public void add(EntitySystem es) {
		engine.addSystem(es);
	}

	public void render(float delta) {
		engine.update(delta);
		
		//if (engine.getEntities().size() > 400) {
		//	System.out.println("(Basic Screen) Too many entities: " + engine.getEntities().size() + " > 400");
		//	System.exit(0);
		//}
	}

	@Override
	public void show() {
		worldCamera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
		worldCamera.position.set(WORLD_WIDTH / 2.0f, WORLD_HEIGHT / 2.0f, 0);
		worldCamera.update();

		spriteBatch.setProjectionMatrix(worldCamera.combined);

		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	public boolean isOverlay() {
		return false;
	}

	public abstract void backPressed();

	public Entity createTextButton(float x, float y, String text, ClickInterface clickInterface) {
		Entity button = createText(x, y, text);

		ClickComponent clickComponent = new ClickComponent();
		clickComponent.clicker = clickInterface;
		clickComponent.active = true;
		clickComponent.shape = new Rectangle().setSize(2.0f, 0.5f).setCenter(0.0f, 0.0f);

		TextButtonComponent textButtonComponent = new TextButtonComponent();
		textButtonComponent.base = Color.WHITE;
		textButtonComponent.pressed = Color.DARK_GRAY;

		button.add(clickComponent);
		button.add(textButtonComponent);

		return button;
	}

	public Entity createText(float x, float y, String text) {
		Entity entity = new Entity();

		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "retro";
		fontComp.string = text;
		fontComp.color = Color.WHITE;
		fontComp.centering = true;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = x;
		transComp.position.y = y;

		entity.add(fontComp);
		entity.add(transComp);

		return entity;
	}
}
