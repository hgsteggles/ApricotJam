package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

public class TweenComponent implements Component {
	public Array<TweenSpec> tweenSpecs = new Array<TweenSpec>();
}
