package com.apricotjam.spacepanic.gameelements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;


public class GameSettings {

	private static Preferences options = Gdx.app.getPreferences("game.options");

	private GameSettings() {}

	public static boolean isSoundOn() {
		return options.getBoolean("sound", true);
	}

	public static void setSoundOn(boolean value) {
		options.putBoolean("sound", value);
		options.flush();
	}
}
