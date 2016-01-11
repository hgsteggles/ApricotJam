package com.apricotjam.spacepanic.misc;

import com.badlogic.gdx.graphics.Color;

public class Colors {
	static public void lerp(Color target, Color start, Color finish, float t) {
		target.r = start.r + t * (finish.r - start.r);
		target.g = start.g + t * (finish.g - start.g);
		target.b = start.b + t * (finish.b - start.b);
		target.a = start.a + t * (finish.a - start.a);
	}
}
