package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;

public class Particles {
	static public void setRotation(ParticleEffect effect, float rotation) {
		for (ParticleEmitter emitter : effect.getEmitters()) {                   
			ScaledNumericValue val = emitter.getAngle();
			float amplitude = (val.getHighMax() - val.getHighMin()) / 2f;
			float h1 = rotation + amplitude;                                            
			float h2 = rotation - amplitude;                                            
			val.setHigh(h1, h2);                                           
			val.setLow(rotation);
		}
	}
	
	static public void setContinuous(ParticleEffect effect, boolean continuous) {
		for (ParticleEmitter emitter : effect.getEmitters()) {
			emitter.setContinuous(continuous);
		}
	}
}
