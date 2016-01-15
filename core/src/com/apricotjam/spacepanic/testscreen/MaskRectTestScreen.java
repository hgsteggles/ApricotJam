package com.apricotjam.spacepanic.testscreen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.art.Shaders;
import com.apricotjam.spacepanic.components.ShaderComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Entity;

public class MaskRectTestScreen extends BasicScreen {

	public MaskRectTestScreen(SpacePanic spacePanic) {
		super(spacePanic);

		add(createTexture());
	}

	@Override
	public void backPressed() {
		// TODO Auto-generated method stub

	}

	private Entity createTexture() {
		TextureComponent textComp = new TextureComponent();
		textComp.region = MiscArt.marioRegion;
		textComp.size.x = 16f;
		textComp.size.y = 16f;

		ShaderComponent shaderComp = new ShaderComponent();
		shaderComp.shader = Shaders.manager.get("maskrect");

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(BasicScreen.WORLD_WIDTH / 2f, BasicScreen.WORLD_HEIGHT / 2f, 0);

		Entity entity = new Entity();
		entity.add(textComp).add(shaderComp).add(transComp);

		return entity;
	}
}
