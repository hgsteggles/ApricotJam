package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;

public class SoundComponent implements Component {
	public Sound sound = null;
	public long soundID = -1;
}
