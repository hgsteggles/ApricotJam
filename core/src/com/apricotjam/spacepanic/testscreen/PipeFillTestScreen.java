package com.apricotjam.spacepanic.testscreen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.PipeGameArt;
import com.apricotjam.spacepanic.components.AnimatedShaderComponent;
import com.apricotjam.spacepanic.components.AnimationComponent;
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
		
		add(createFluid());
		add(createPipe());
	}

	@Override
	public void backPressed() {
		// TODO Auto-generated method stub

	}
	
	private Entity createPipe() {
		Entity tile = new Entity();

		TextureComponent textureComp = new TextureComponent();
		textureComp.region = PipeGameArt.pipesRegion[0];

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(BasicScreen.WORLD_WIDTH / 2f, BasicScreen.WORLD_HEIGHT / 2f, 1);
		
		tile.add(textureComp).add(transComp);
		
		return tile;
	}
	
	private Entity createFluid() {
		Entity tile = new Entity();

		TextureComponent textureComp = new TextureComponent();
		textureComp.region = PipeGameArt.pipeFluidTestMaskRegion[0];

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(BasicScreen.WORLD_WIDTH / 2f, BasicScreen.WORLD_HEIGHT / 2f, 0);
		
		AnimationComponent animComp = new AnimationComponent();
		animComp.animations.put(0, new Animation(0.1f, PipeGameArt.pipeFluidTestMaskRegion));
		
		StateComponent stateComp = new StateComponent();
		stateComp.set(0);
		
		AnimatedShaderComponent animShaderComp = new AnimatedShaderComponent();
		animShaderComp.shader = PipeGameArt.fluidShader;
		
		tile.add(textureComp).add(transComp).add(animComp).add(stateComp).add(animShaderComp);

		return tile;
	}
}
