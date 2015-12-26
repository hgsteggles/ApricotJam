package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.apricotjam.spacepanic.systems.PipeSystem;
import com.apricotjam.spacepanic.systems.TweenSystem;

public class PipeTestScreen extends BasicScreen {

	public PipeTestScreen(SpacePanic spacePanic) {
		super(spacePanic);

		add(new PipeSystem());
		add(new ClickSystem());
		add(new TweenSystem());
	}

	@Override
	public void backPressed() {
		// TODO Auto-generated method stub

	}
}
