package com.apricotjam.spacepanic.desktop;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.platform.PlatformImplementations;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main(String[] arg) {
		//TexturePacker.process("../../images", "atlas", "art");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Untethered";
		config.width = SpacePanic.WIDTH;
		config.height = SpacePanic.HEIGHT;
		
		PlatformImplementations platformImps = new PlatformImplementations();
		platformImps.puzzleSelector = new DesktopPuzzleSelector();
		
		new LwjglApplication(new SpacePanic(platformImps), config);
	}
}
