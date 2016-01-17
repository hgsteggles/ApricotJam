package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Particles {
	public static ObjectMap<String, ParticleEffect> effects = new ObjectMap<String, ParticleEffect>();

	static public void load(TextureAtlas atlas) {
		ParticleEffect effect;

		effect = new ParticleEffect();
		effect.load(Gdx.files.internal("particles/explosion"), atlas);
		effects.put("explosion", effect);

		effect = new ParticleEffect();
		effect.load(Gdx.files.internal("particles/burner"), atlas);
		effect.scaleEffect(0.01f);
		effects.put("burner", effect);

	}

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
