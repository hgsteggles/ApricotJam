package com.apricotjam.spacepanic.components;

import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.badlogic.gdx.math.Interpolation;

public class TweenSpec {

	public float start = 0.0f, end = 1.0f;
	public float period = 1.0f;
	public float time = 0;
	public int loops = 1;
	public boolean reverse = false;
	public boolean finished = false;
	public Cycle cycle = Cycle.ONCE;
	public TweenInterface tweenInterface;
	public Interpolation interp = Interpolation.linear;

	public enum Cycle {
		ONCE, INFLOOP, LOOP
	}
}
