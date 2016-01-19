package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ObjectMap;

public class Assets {
	public static ObjectMap<String, BitmapFont> fonts = new ObjectMap<String, BitmapFont>();
	public static ObjectMap<String, Sound> sounds = new ObjectMap<String, Sound>();
	public static ObjectMap<String, Music> music = new ObjectMap<String, Music>();
	public static ObjectMap<String, ParticleEffect> particles = new ObjectMap<String, ParticleEffect>();
	
	public static void load(AssetManager manager) {
		manager.load("atlas/art.atlas", TextureAtlas.class);
		manager.load("sounds/fluid-fill.wav", Sound.class);
		manager.load("sounds/breathing.wav", Sound.class);
		manager.load("sounds/alarm.ogg", Sound.class);
		manager.load("sounds/soundtrack.ogg", Music.class);
		manager.load("fonts/retro3.fnt", BitmapFont.class);
		manager.load("fonts/led1.fnt", BitmapFont.class);
		
		ParticleEffectParameter particleParam = new ParticleEffectParameter();
		particleParam.atlasFile = "atlas/art.atlas";
		manager.load("particles/explosion", ParticleEffect.class, particleParam);
		manager.load("particles/burner", ParticleEffect.class, particleParam);
		
		TextureParameter texParam = new TextureParameter();
		texParam.wrapU = Texture.TextureWrap.Repeat;
		texParam.wrapV = Texture.TextureWrap.Repeat;
		manager.load("mainBackground.png", Texture.class, texParam);
		manager.load("mapline01.png", Texture.class, texParam);
	}

	public static void done(AssetManager manager) {
		TextureAtlas atlas = manager.get("atlas/art.atlas", TextureAtlas.class);
		HelmetUI.create(atlas);
		PipeGameArt.create(atlas);
		SplashArt.create(atlas);
		MapArt.create(manager);
		MiscArt.create(manager);
		
		fonts.put("retro", manager.get("fonts/retro3.fnt", BitmapFont.class));
		fonts.put("led", manager.get("fonts/led1.fnt", BitmapFont.class));
		
		sounds.put("alarm", manager.get("sounds/alarm.ogg", Sound.class));
		sounds.put("breathing", manager.get("sounds/breathing.wav", Sound.class));
		sounds.put("fluid-fill", manager.get("sounds/fluid-fill.wav", Sound.class));
		
		music.put("soundtrack", manager.get("sounds/soundtrack.ogg", Music.class));
		
		particles.put("burner", manager.get("particles/burner", ParticleEffect.class));
		particles.put("explosion", manager.get("particles/explosion", ParticleEffect.class));
	}
}
