package com.apricotjam.spacepanic.desktop;

import com.apricotjam.spacepanic.SpacePanic;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Settings settings = new Settings();
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;
		TexturePacker.process(settings, "../images/pipetiles", "../core/assets/atlas", "pipetiles");
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = SpacePanic.WIDTH;
		config.height = SpacePanic.HEIGHT;
		new LwjglApplication(new SpacePanic(), config);
	}
}
