package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;

public class SoundComponent implements Component {
	public Sound sound = null;
	public float duration = 1.0f; // Negative numbers will give infinite duration
	public float volume = 1.0f;
	public float pitch = 1.0f;
	public float pan = 0.0f;

	public float time = 0.0f;
	public long soundID = -1;
}
