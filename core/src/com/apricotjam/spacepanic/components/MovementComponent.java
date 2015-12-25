package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

public class MovementComponent implements Component {
	public Vector3 linearVelocity = new Vector3();
	public float rotationalVelocity = 0.0f;
}
