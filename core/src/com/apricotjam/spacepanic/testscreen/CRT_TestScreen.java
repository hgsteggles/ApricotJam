package com.apricotjam.spacepanic.testscreen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.art.Shaders;
import com.apricotjam.spacepanic.components.ShaderComponent;
import com.apricotjam.spacepanic.components.ShaderTimeComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.apricotjam.spacepanic.systems.AnimatedShaderSystem;
import com.badlogic.ashley.core.Entity;

public class CRT_TestScreen extends BasicScreen {

	public CRT_TestScreen(SpacePanic spacePanic) {
		super(spacePanic);

		add(new AnimatedShaderSystem());

		add(createCRT());
	}

	@Override
	public void backPressed() {
		// TODO Auto-generated method stub

	}

	private Entity createCRT() {
		TextureComponent textComp = new TextureComponent();
		textComp.region = MiscArt.marioRegion;
		textComp.size.x = 4f;
		textComp.size.y = 4f;

		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("crt");

		ShaderTimeComponent shaderTimeComp = new ShaderTimeComponent();

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(BasicScreen.WORLD_WIDTH / 2f, BasicScreen.WORLD_HEIGHT / 2f, 0);

		Entity entity = new Entity();
		entity.add(textComp).add(shaderComp).add(shaderTimeComp).add(transComp);

		return entity;
	}
}
