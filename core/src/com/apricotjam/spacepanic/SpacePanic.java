package com.apricotjam.spacepanic;

import com.apricotjam.art.Art;
import com.apricotjam.input.ScreenInput;
import com.apricotjam.screen.BasicScreen;
import com.apricotjam.screen.TitleScreen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SpacePanic extends ApplicationAdapter {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;

	private BasicScreen screen;
	
	@Override
	public void create () {
		Art.load();
		setScreen(new TitleScreen(this, new ScreenInput()));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (screen != null) {
			if (screen.getInputData().isBackPressedLast()) {
				screen.backPressed();
			}
			screen.update();
			screen.render(Gdx.graphics.getDeltaTime());
			screen.getInputData().reset();
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
