package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MapArt;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.components.map.ResourceComponent;
import com.apricotjam.spacepanic.gameelements.MenuButton;
import com.apricotjam.spacepanic.gameelements.Resource;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.misc.EntityUtil;
import com.apricotjam.spacepanic.systems.ClickSystem;
import com.apricotjam.spacepanic.systems.TweenSystem;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;

public class HelpScreen extends BasicScreen {

	private static class ResourceLine {
		public Resource resource;
		public String name = "";
		public String line1 = "";
		public String line2 = "";
		public String line3 = "";

		public ResourceLine(Resource resource, String name, String line1, String line2, String line3) {
			this.resource = resource;
			this.name = name;
			this.line1 = line1;
			this.line2 = line2;
			this.line3 = line3;
		}
	}

	private static final float TITLE_Y = WORLD_HEIGHT * 6.0f / 7.0f;
	private static final float TITLE_TIME = 1.0f;

	private static final float BACK_X = 1.5f;
	private static final float BACK_Y = WORLD_HEIGHT - 0.5f;

	private static String GENERAL_TEXT = "Gather these resources to survive!";
	private static final float GENERAL_X = WORLD_WIDTH / 2.0f;
	private static final float GENERAL_Y = WORLD_HEIGHT * 2.0f / 3.0f + 0.5f;

	private static final float RES_X = WORLD_WIDTH / 4.0f;
	private static final float RES_Y = WORLD_HEIGHT * 2.0f / 3.0f - 0.5f;
	private static final float RES_SPACING_X = WORLD_WIDTH / 2.0f;
	private static final float RES_SPACING_Y = 3.0f;

	private static final ArrayList<ResourceLine> RESOURCE_LINES = new ArrayList<ResourceLine>();
	static {
		RESOURCE_LINES.add(new ResourceLine(Resource.OXYGEN, "Oxygen", "You need to breathe!", "You will die without it", ""));
		RESOURCE_LINES.add(new ResourceLine(Resource.DEMISTER, "Demister", "Keep that visor clean!", "Your visor will fog up as this", "runs out"));
		RESOURCE_LINES.add(new ResourceLine(Resource.PIPE_CLEANER, "Pipe Cleaner", "A good astronaut has clean pipes!", "Resource gain is reduced when", "pipe cleaner is empty"));
		RESOURCE_LINES.add(new ResourceLine(Resource.PLUTONIUM, "Plutonium", "Powers your long range scanner!", "Will allow you to see further", " into the asteroid field"));
	}

	private Entity title;
	private Entity astronaut;
	private Entity background;

	public HelpScreen(SpacePanic spacePanic, Entity title, Entity astronaut, Entity background) {
		super(spacePanic);

		add(new ClickSystem());
		add(new TweenSystem());

		this.title = EntityUtil.clone(title);
		EntityUtil.addTitleTween(this.title, TITLE_Y, TITLE_TIME);
		add(this.title);

		this.astronaut = EntityUtil.clone(astronaut);
		add(this.astronaut);

		this.background = EntityUtil.clone(background);
		add(this.background);

		MenuButton butBack = new MenuButton(BACK_X, BACK_Y, 2.0f, "BACK", new ClickInterface() {
			@Override
			public void onClick(Entity entity) {
				backPressed();
			}
		});
		butBack.addToEngine(engine);

		add(createText(GENERAL_X, GENERAL_Y, GENERAL_TEXT));

		int n = 0;
		for (ResourceLine rl : RESOURCE_LINES) {
			addResourceLine(rl, RES_X + (n / 2) * RES_SPACING_X, RES_Y - (n % 2) * RES_SPACING_Y);
			n++;
		}
	}

	private void addResourceLine(ResourceLine resourceLine, float x, float y) {
		add(createResourceIcon(x - ((resourceLine.name.length() + 4) / 5.0f), y, resourceLine.resource));
		add(createText(x, y, resourceLine.name));
		add(createText(x, y - 0.75f, resourceLine.line1, true));
		add(createText(x, y - 1.25f, resourceLine.line2, true));
		add(createText(x, y - 1.75f, resourceLine.line3, true));
	}

	public Entity createResourceIcon(float x, float y, Resource resource) {
		Entity resourceIcon = new Entity();

		ResourceComponent resourceComponent = new ResourceComponent();
		resourceComponent.resource = resource;
		resourceIcon.add(resourceComponent);

		TextureComponent texc = new TextureComponent();
		switch (resource) {
			case OXYGEN:
				texc.region = MapArt.resourceIcons.get(0);
				break;
			case DEMISTER:
				texc.region = MapArt.resourceIcons.get(1);
				break;
			case PIPE_CLEANER:
				texc.region = MapArt.resourceIcons.get(2);
				break;
			case PLUTONIUM:
				texc.region = MapArt.resourceIcons.get(3);
				break;
		}
		texc.size.x = 1.0f;
		texc.size.y = 1.0f;
		resourceIcon.add(texc);

		TransformComponent tranc = new TransformComponent();
		tranc.position.x = x;
		tranc.position.y = y;
		tranc.position.z = 1.0f;
		resourceIcon.add(tranc);

		return resourceIcon;
	}

	@Override
	public void backPressed() {
		spacePanic.setScreen(new MenuScreen(spacePanic, title, astronaut, background));
	}
}
