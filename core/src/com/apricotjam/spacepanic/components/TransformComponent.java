package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TransformComponent implements Component {
	public final Vector3 position = new Vector3();
	public final Vector2 scale = new Vector2(1.0f, 1.0f);
	public float rotation = 0.0f;
	public TransformComponent parent = null;

	public Vector3 getTotalPosition() {
		Vector3 totalPosition = new Vector3(position);
		TransformComponent ipar = parent;
		while (ipar != null) {
			totalPosition.add(ipar.position);
			ipar = ipar.parent;
		}
		return totalPosition;
	}

	public Vector2 getTotalScale() {
		Vector2 totalScale = new Vector2(scale);
		TransformComponent ipar = parent;
		while (ipar != null) {
			totalScale.x *= ipar.scale.x;
			totalScale.y *= ipar.scale.y;
			ipar = ipar.parent;
		}
		return totalScale;
	}

	public float getTotalRotation() {
		float totalRotation = rotation;
		TransformComponent ipar = parent;
		while (ipar != null) {
			totalRotation += ipar.rotation;
			ipar = ipar.parent;
		}
		return totalRotation;
	}
}

