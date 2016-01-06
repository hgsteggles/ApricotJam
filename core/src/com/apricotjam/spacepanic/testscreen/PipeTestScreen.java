package com.apricotjam.spacepanic.testscreen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.apricotjam.spacepanic.systems.AnimatedShaderSystem;
import com.apricotjam.spacepanic.systems.AnimationSystem;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.apricotjam.spacepanic.systems.HelmetSystem;
import com.apricotjam.spacepanic.systems.ShaderLightingSystem;
import com.apricotjam.spacepanic.systems.TickerSystem;
import com.apricotjam.spacepanic.systems.TweenSystem;
import com.apricotjam.spacepanic.systems.pipes.PipeSystem;

public class PipeTestScreen extends BasicScreen {

	public PipeTestScreen(SpacePanic spacePanic) {
		super(spacePanic);

		add(new HelmetSystem());
		add(new PipeSystem());
		add(new ClickSystem());
		add(new TweenSystem());
		add(new AnimationSystem());
		add(new AnimatedShaderSystem());
		add(new ShaderLightingSystem());
		add(new TickerSystem());
	}

	@Override
	public void backPressed() {
		// TODO Auto-generated method stub

	}
}
