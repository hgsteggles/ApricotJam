package com.apricotjam.spacepanic.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public class InputManager {

	public static ScreenInput screenInput;
	public static TestInput testInput;
	private static InputMultiplexer input;

	private InputManager() {
	}

	public static void create() {
		input = new InputMultiplexer();
		Gdx.input.setInputProcessor(input);

		screenInput = new ScreenInput();
		addInputProcessor(screenInput);

		testInput = new TestInput();
		addInputProcessor(testInput);
	}

	public static void reset() {
		screenInput.reset();
		testInput.reset();
	}

	public static void addInputProcessor(InputProcessor ip) {
		input.addProcessor(ip);
	}

	public static void removeInputProcessor(InputProcessor ip) {
		input.removeProcessor(ip);
	}
}
