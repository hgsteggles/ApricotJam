package com.apricotjam.spacepanic;

import android.os.Bundle;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.platform.PlatformImplementations;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useImmersiveMode = true;
		
		PlatformImplementations platformImps = new PlatformImplementations();
		platformImps.puzzleSelector = new AndroidPuzzleSelector();
		
		initialize(new SpacePanic(platformImps), config);
	}
}
