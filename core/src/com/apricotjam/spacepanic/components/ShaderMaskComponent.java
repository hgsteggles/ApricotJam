package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ShaderMaskComponent implements Component {
	public final Vector3 position = new Vector3();
	public final Vector2 size = new Vector2();
	public TransformComponent parent;

	public ShaderMaskComponent getTotalTransformedMask() {
		ShaderMaskComponent total = new ShaderMaskComponent();
		total.position.add(position);
		total.size.x = size.x;
		total.size.y = size.y;
		TransformComponent ipar = parent;
		while (ipar != null) {
			total.position.x *= ipar.scale.x;
			total.position.y *= ipar.scale.y;
			total.position.add(ipar.position);
			total.size.x *= ipar.scale.x;
			total.size.y *= ipar.scale.y;
			ipar = ipar.parent;
		}
		return total;
	}
}
