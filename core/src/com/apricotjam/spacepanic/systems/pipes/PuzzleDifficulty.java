package com.apricotjam.spacepanic.systems.pipes;

import com.apricotjam.spacepanic.platform.PuzzleSelector;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PuzzleDifficulty {
	static public Array<Integer> gridSize;
	static public Array<Integer> turnOffs;
	static public Array<Integer> npipes;
	static public int ndifficulties;
	
	static public int maxGridSize;

	static public void create(PuzzleSelector selector) {
		gridSize = new Array<Integer>();
		turnOffs = new Array<Integer>();
		npipes = new Array<Integer>();
		ndifficulties = 0;
		maxGridSize = 0;
		
		FileHandle file = Gdx.files.internal("config/puzzle-difficulty.txt");

		BufferedReader br = new BufferedReader(new InputStreamReader(file.read()));
		String line = null;

		try {
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				int gs = Integer.parseInt(values[0]);
				int to = Integer.parseInt(values[1]);
				int np = Integer.parseInt(values[2]);
				int sols = Integer.parseInt(values[3]);

				if (selector.valid(gs, to, np, sols)) {
					gridSize.add(gs);
					turnOffs.add(to);
					npipes.add(np);
					ndifficulties += 1;
					maxGridSize = Math.max(maxGridSize, gs);
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
