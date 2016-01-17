package com.apricotjam.spacepanic.gameelements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;


public class GameSettings {

	private static Preferences options = Gdx.app.getPreferences("game.options");

	private GameSettings() {
	}

	public static boolean isSoundOn() {
		return options.getBoolean("sound", true);
	}

	public static void setSoundOn(boolean value) {
		options.putBoolean("sound", value);
		options.flush();
	}
	
	public static float getHighScore() {
		return options.getFloat("highscore", 0);
	}
	
	public static void setHighScore(float score) {
		options.putFloat("highscore", score);
		options.flush();
	}
	
	public static String generateScoreString(float timeAlive) {
		String score;
		if (timeAlive < 60) {
			score = Math.round(timeAlive) + " seconds";
		} else {
			int mins = (int)Math.floor(timeAlive / 60.0f);
			int seconds = Math.round(timeAlive % 60);
			score = mins + " minutes " + seconds + " seconds";
		}
		return score;
	}
	
	public static boolean getIntroSkippable() {
		return options.getBoolean("introskip", false);
	}
	
	public static void setIntroSkippable(boolean skippable) {
		options.putBoolean("introskip", skippable);
		options.flush();
	}
}
