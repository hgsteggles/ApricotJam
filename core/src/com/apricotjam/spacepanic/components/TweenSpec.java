package com.apricotjam.spacepanic.components;

import com.apricotjam.spacepanic.interfaces.TweenInterface;
import com.badlogic.gdx.math.Interpolation;

public class TweenSpec {
	public float start = 0.0f, end = 1.0f;
	public float period = 1.0f;
	public float x = 0;
	public TweenInterface tweenInterface;
	public Interpolation interp = Interpolation.linear;
}
