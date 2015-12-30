package com.apricotjam.spacepanic.testscreen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.PipeGameArt;
import com.apricotjam.spacepanic.art.PipeGameArt.RotatedAnimationData;
import com.apricotjam.spacepanic.components.AnimatedShaderComponent;
import com.apricotjam.spacepanic.components.AnimationComponent;
import com.apricotjam.spacepanic.components.PipeFluidComponent;
import com.apricotjam.spacepanic.components.StateComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.apricotjam.spacepanic.systems.AnimationSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;

public class PipeFillTestScreen extends BasicScreen {
	public PipeFillTestScreen(SpacePanic spacePanic) {
		super(spacePanic);
		
		add(new AnimationSystem());
		
		add(createFluid((byte)(6), 2));
	//	add(createFluid((byte)(10)));
		add(createPipe((byte)(6)));
	}

	@Override
	public void backPressed() {
		// TODO Auto-generated method stub

	}
	
	private Entity createPipe(byte mask) {
		Entity tile = new Entity();

		TextureComponent textureComp = new TextureComponent();
		textureComp.region = PipeGameArt.pipeRegions.get(mask).region;

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(BasicScreen.WORLD_WIDTH / 2f, BasicScreen.WORLD_HEIGHT / 2f, 1);
		transComp.rotation = PipeGameArt.pipeRegions.get(mask).rotation;
		
		tile.add(textureComp).add(transComp);
		
		return tile;
	}
	
	private Entity createFluid(byte mask, int entry) {
		Entity tile = new Entity();

		PipeFluidComponent pipeFluidComp = new PipeFluidComponent();
		pipeFluidComp.fillDuration = 4f;
		
		TextureComponent textureComp = new TextureComponent();

		RotatedAnimationData animData = PipeGameArt.fluidRegions.get(mask).get(entry);
		
		if (animData == null)
			System.out.println("animData is null");
		if (animData.regions.size == 0)
			System.out.println("animData is zero length");
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.set(BasicScreen.WORLD_WIDTH / 2f, BasicScreen.WORLD_HEIGHT / 2f, 0);
		transComp.rotation = animData.rotation;
		
		AnimationComponent animComp = new AnimationComponent();
		animComp.animations.put(PipeFluidComponent.STATE_FILLING, new Animation(pipeFluidComp.fillDuration/animData.regions.size, animData.regions));
		
		StateComponent stateComp = new StateComponent();
		stateComp.set(PipeFluidComponent.STATE_FILLING);
		
		AnimatedShaderComponent animShaderComp = new AnimatedShaderComponent();
		animShaderComp.shader = PipeGameArt.fluidShader;
		
		tile.add(pipeFluidComp).add(textureComp).add(transComp).add(animComp).add(stateComp).add(animShaderComp);

		return tile;
	}
}
