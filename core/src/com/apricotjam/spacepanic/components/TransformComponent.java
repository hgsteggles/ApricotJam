package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TransformComponent implements Component {
	public final Vector3 position = new Vector3();
	public final Vector2 scale = new Vector2(1.0f, 1.0f);
	public float rotation = 0.0f;
	public TransformComponent parent = null;

	public TransformComponent getTotalTransform() {
		TransformComponent total = new TransformComponent();
		total.position.add(position);
		total.scale.x *= scale.x;
		total.scale.y *= scale.y;
		total.rotation += rotation;
		TransformComponent ipar = parent;
		while (ipar != null) {
			total.position.add(ipar.position);
			total.scale.x *= ipar.scale.x;
			total.scale.y *= ipar.scale.y;
			total.rotation += ipar.rotation;
			ipar = ipar.parent;
		}
		return total;
	}

	public float getTotalZ() {
		float totalZ = position.z;
		TransformComponent ipar = parent;
		while (ipar != null) {
			totalZ += ipar.position.z;
			ipar = ipar.parent;
		}
		return totalZ;
	}
}

