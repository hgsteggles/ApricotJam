package com.apricotjam.spacepanic.components;

import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.badlogic.gdx.math.Interpolation;

public class TweenSpec {

	public enum Cycle {
		ONCE, LOOP, REVERSE
	}
	public float start = 0.0f, end = 1.0f;
	public float period = 1.0f;
	public float time = 0;
	public Cycle cycle = Cycle.ONCE;
	public boolean reverse = false;
	public TweenInterface tweenInterface;
	public Interpolation interp = Interpolation.linear;
}
