package com.apricotjam.spacepanic.testscreen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.MovementComponent;
import com.apricotjam.spacepanic.components.ScrollComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.components.TweenComponent;
import com.apricotjam.spacepanic.components.helmet.HelmetScreenComponent;
import com.apricotjam.spacepanic.components.pipe.PipeScreenComponent;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.apricotjam.spacepanic.systems.AnimatedShaderSystem;
import com.apricotjam.spacepanic.systems.AnimationSystem;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.apricotjam.spacepanic.systems.MovementSystem;
import com.apricotjam.spacepanic.systems.RenderingSystem;
import com.apricotjam.spacepanic.systems.ScrollSystem;
import com.apricotjam.spacepanic.systems.ShaderLightingSystem;
import com.apricotjam.spacepanic.systems.TickerSystem;
import com.apricotjam.spacepanic.systems.TweenSystem;
import com.apricotjam.spacepanic.systems.helmet.HelmetSystem;
import com.apricotjam.spacepanic.systems.helmet.HelmetSystem.LED_Message;
import com.apricotjam.spacepanic.systems.helmet.HelmetSystem.LED_Message.Severity;
import com.apricotjam.spacepanic.systems.pipes.PipeSystem;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PipeTestScreen extends BasicScreen {
	private Entity helmetSystemEntity;
	private Entity pipeSystemEntity;

	public PipeTestScreen(SpacePanic spacePanic) {
		super(spacePanic);
		
		helmetSystemEntity = createHelmetMasterEntity();
		pipeSystemEntity = createPipeMasterEntity();
		
		add(createBackground());

		add(new HelmetSystem(helmetSystemEntity));
		PipeSystem pipeSystem = new PipeSystem(pipeSystemEntity);
		add(pipeSystem);
		add(new MovementSystem());
		add(new ScrollSystem());
		add(new ClickSystem());
		add(new TweenSystem());
		add(new AnimationSystem());
		add(new AnimatedShaderSystem());
		add(new ShaderLightingSystem());
		add(new TickerSystem());
		
		pipeSystem.start();
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		PipeScreenComponent pipeScreenComp = ComponentMappers.pipescreen.get(pipeSystemEntity);
		if (pipeScreenComp.currentState == PipeScreenComponent.State.SUCCESS) {
			System.out.println("Solved the pipe puzzle!");
			pipeScreenComp.currentState = PipeScreenComponent.State.PAUSED;
			
			HelmetScreenComponent helmetScreenComp = ComponentMappers.helmetscreen.get(helmetSystemEntity);
			helmetScreenComp.resourceCount.put(pipeScreenComp.resource, helmetScreenComp.resourceCount.get(pipeScreenComp.resource) + 10);
			
			helmetScreenComp.messages.addLast(new LED_Message("SUCCESS", Severity.SUCCESS));
			helmetScreenComp.messages.addLast(new LED_Message("RESOURCE COLLECTED", Severity.HINT));
		}
		else if (pipeScreenComp.currentState == PipeScreenComponent.State.FAIL) {
			System.out.println("Failed the pipe puzzle :(");
			
			pipeScreenComp.currentState = PipeScreenComponent.State.PAUSED;
		}
	}
	
	private Entity createHelmetMasterEntity() {
		Entity entity = new Entity();
		entity.add(new HelmetScreenComponent());
		
		return entity;
	}
	
	private Entity createPipeMasterEntity() {
		Entity entity = new Entity();

		PipeScreenComponent pipeScreenComp = new PipeScreenComponent();
		pipeScreenComp.currentState = PipeScreenComponent.State.PAUSED;
		entity.add(pipeScreenComp);

		TransformComponent tranc = new TransformComponent();
		tranc.position.set(BasicScreen.WORLD_WIDTH / 2.0f, BasicScreen.WORLD_HEIGHT / 2.0f, -10);
		entity.add(tranc);

		entity.add(new TweenComponent());
		
		return entity;
	}

	private Entity createBackground() {
		Entity e = new Entity();

		TextureComponent texComp = new TextureComponent();
		Texture tex = MiscArt.mainBackgroundScrollable;
		float texToCorner = (float)Math.sqrt((tex.getWidth() * tex.getWidth()) + (tex.getHeight() * tex.getHeight()));
		texComp.region = new TextureRegion(tex, 0, 0, (int)texToCorner, (int)texToCorner);
		tex.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		texComp.size.x = texToCorner * RenderingSystem.PIXELS_TO_WORLD;
		texComp.size.y = texToCorner * RenderingSystem.PIXELS_TO_WORLD;

		TransformComponent transComp = new TransformComponent();
		transComp.position.x = BasicScreen.WORLD_WIDTH / 2.0f;
		transComp.position.y = BasicScreen.WORLD_HEIGHT / 2.0f;
		transComp.position.z = -100.0f;

		MovementComponent movementComp = new MovementComponent();
		movementComp.rotationalVelocity = 5.0f;

		ScrollComponent scrollComp = new ScrollComponent();
		scrollComp.speed.x = 0.5f;

		e.add(texComp);
		e.add(transComp);
		e.add(movementComp);
		e.add(scrollComp);

		return e;
	}

	@Override
	public void backPressed() {
		// TODO Auto-generated method stub

	}
}
