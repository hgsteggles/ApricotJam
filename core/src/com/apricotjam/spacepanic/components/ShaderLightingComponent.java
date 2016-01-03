package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

public class ShaderLightingComponent implements Component {
	public Vector3 lightPosition = new Vector3();
	public Vector3 lightColor = new Vector3();
	public Vector3 AmbientColor = new Vector3();
	public Vector3 fallOff = new Vector3();
	
	public float lightIntensity = 0;
	public float ambientIntensity = 0;
}
