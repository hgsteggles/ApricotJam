package com.apricotjam.spacepanic;

import com.apricotjam.spacepanic.art.Art;
import com.apricotjam.spacepanic.input.InputManager;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.apricotjam.spacepanic.screen.TitleScreen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class SpacePanic extends ApplicationAdapter {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;

	private BasicScreen screen;

	@Override
	public void create() {
		Art.load();
		InputManager.create();
		setScreen(new GameScreen(this));
		//setScreen(new TitleScreen(this));
		//setScreen(new PipeTestScreen(this));
	}

	@Override
	public void render() {
		Gdx.graphics.getGL20().glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		if (screen != null) {
			if (InputManager.screenInput.isBackPressedLast()) {
				screen.backPressed();
			}
			screen.render(Gdx.graphics.getDeltaTime());
			InputManager.reset();
		}
	}

	@Override
	public void dispose() {
		if (screen != null) {
			screen.dispose();
		}
	}

	@Override
	public void pause() {
		if (screen != null) {
			screen.pause();
		}
	}

	@Override
	public void resume() {
		if (screen != null) {
			screen.resume();
		}
	}

	@Override
	public void resize(int width, int height) {
		if (screen != null) {
			screen.resize(width, height);
		}
	}

	public void setScreen(BasicScreen newScreen) {
		if (newScreen != null) {
			if (!(newScreen.isOverlay()) && this.screen != null) { // New screen will replace current
				this.screen.dispose();
			}
			this.screen = newScreen;
			this.screen.show();
			this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}

	public void exit() {
		Gdx.app.exit();
	}
}
