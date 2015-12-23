package com.apricotjam.spacepanic.components;

import com.badlogic.gdx.math.Interpolation;

public class TweenSpec {
	public enum TweenTarget {
		POSX, POSY, POSZ, SCALEX, SCALEY, ROTATION
	}
	public float start = 0.0f, end = 1.0f;
	public float period = 1.0f;
	public float x = 0;
	public TweenTarget target = TweenTarget.POSX;
	public Interpolation interp = Interpolation.linear;
}
