package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.systems.PipeSystem;
import com.badlogic.ashley.core.Entity;

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
