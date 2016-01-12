package com.apricotjam.spacepanic.art;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

public class Audio {
	static public ObjectMap<String, Sound> sounds = new ObjectMap<String, Sound>();
	static public ObjectMap<String, Music> music = new ObjectMap<String, Music>();
	
	static public void load() {
		sounds.put("fluid-fill", Gdx.audio.newSound(Gdx.files.internal("sounds/fluid-fill.wav")));
		music.put("soundtrack", Gdx.audio.newMusic(Gdx.files.internal("sounds/soundtrack.wav")));
	}
	
	static public void dispose() {
		for (ObjectMap.Entry<String, Sound> entry : sounds.entries()) {
			entry.value.dispose();
		}
		for (ObjectMap.Entry<String, Music> entry : music.entries()) {
			entry.value.dispose();
		}
	}
}
