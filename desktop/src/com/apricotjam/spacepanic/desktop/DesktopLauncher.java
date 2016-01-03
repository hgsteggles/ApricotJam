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

		/*MazeGenerator mg = new MazeGenerator(1234, 10, 10, 0.5f);
		int[][] patch = mg.createPatch(3, 3);
		mg.printPatch(patch);

		Pathfinder pf = new Pathfinder(10, 10, 30);
		ArrayList<Point> path = pf.calculatePath(patch, new Point(1, 0), new Point(1, 6));
		System.out.println(path);*/
	}
}
