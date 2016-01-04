package com.apricotjam.spacepanic.desktop;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.puzzle.MazeGenerator;
import com.apricotjam.spacepanic.puzzle.Pathfinder;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import java.awt.*;
import java.util.ArrayList;

public class DesktopLauncher {
	public static void main (String[] arg) {
		//TexturePacker.process("../../images", "atlas", "art");
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = SpacePanic.WIDTH;
		config.height = SpacePanic.HEIGHT;
		new LwjglApplication(new SpacePanic(), config);
	}
}
