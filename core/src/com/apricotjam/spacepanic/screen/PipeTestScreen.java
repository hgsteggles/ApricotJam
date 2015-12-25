package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.systems.PipeSystem;

public class PipeTestScreen extends BasicScreen {

	public PipeTestScreen(SpacePanic spacePanic) {
		super(spacePanic);

		add(new PipeSystem());
	}

	@Override
	public void backPressed() {
		// TODO Auto-generated method stub

	}
}
