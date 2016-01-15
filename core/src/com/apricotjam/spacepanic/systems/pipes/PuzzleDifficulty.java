package com.apricotjam.spacepanic.systems.pipes;

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

	static public void load() {
		gridSize = new Array<Integer>();
		turnOffs = new Array<Integer>();
		npipes = new Array<Integer>();

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

				if (sols > 1) {
					gridSize.add(gs);
					turnOffs.add(to);
					npipes.add(np);
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
