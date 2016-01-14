package com.apricotjam.spacepanic;

import com.apricotjam.spacepanic.art.Art;
import com.apricotjam.spacepanic.art.Audio;
import com.apricotjam.spacepanic.art.Shaders;
import com.apricotjam.spacepanic.gameelements.GameSettings;
import com.apricotjam.spacepanic.input.InputManager;
import com.apricotjam.spacepanic.misc.ScreenshotFactory;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.apricotjam.spacepanic.screen.TitleScreen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.RandomXS128;

public class SpacePanic extends ApplicationAdapter {
	static public final int WIDTH = 1280;
	static public final int HEIGHT = 720;
	static public RandomXS128 rng = new RandomXS128(0);
	
	private boolean video = false;
	private float accum = 0;
	private float frame_time = 1.0f/30f;
	private int nprints = (int)(1f/frame_time);

	private BasicScreen screen;

	private Music soundtrack;

	@Override
	public void create() {
		Art.load();
		Audio.load();
		Shaders.load();
		InputManager.create();
		setScreen(new TitleScreen(this));

		soundtrack = Audio.music.get("soundtrack");
		soundtrack.setLooping(true);
		soundtrack.play();
		if (!GameSettings.isSoundOn()) {
			soundtrack.setVolume(0.0f);
		}
	}

	@Override
	public void render() {
		Gdx.graphics.getGL20().glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		if (screen != null) {
			if (InputManager.screenInput.isBackPressedLast()) {
				screen.backPressed();
			}
			float deltatime = Gdx.graphics.getDeltaTime();
			screen.render(deltatime);
			InputManager.reset();

			if (!GameSettings.isSoundOn()) {
				soundtrack.setVolume(0.0f);
			} else {
				soundtrack.setVolume(1.0f);
			}
			
			if (video && nprints > 0) {
				accum += deltatime;
				if (accum > frame_time) {
					accum -= frame_time;
					ScreenshotFactory.saveScreenshot();
					nprints -= 1;
				}
			}
		}
	}

	@Override
	public void dispose() {
		if (screen != null) {
			screen.dispose();
		}
		Art.dispose();
		Audio.dispose();
		Shaders.dispose();
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
		Shaders.manager.resize(width, height);
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
