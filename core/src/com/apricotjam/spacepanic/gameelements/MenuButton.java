package com.apricotjam.spacepanic.gameelements;

import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.BitmapFontComponent;
import com.apricotjam.spacepanic.components.ClickComponent;
import com.apricotjam.spacepanic.components.NinepatchComponent;
import com.apricotjam.spacepanic.components.TextButtonComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.interfaces.ClickInterface;
import com.apricotjam.spacepanic.screen.BasicScreen;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class MenuButton {
	private Entity ninepatch, bitmapfont;

	public MenuButton(float x, float y, String text, ClickInterface clickInterface) {
		//this(x, y, 1.0f, 4.0f, 0.6f, text, clickInterface);
		this(x, y, 1.0f, text.length() / 2.3f, 0.6f, text, clickInterface); //2.5 gives exactly text width, 2.3 allows for a little padding
	}

	public MenuButton(float x, float y, float width, String text, ClickInterface clickInterface) {
		this(x, y, 1.0f, width, 0.6f, text, clickInterface);
	}
	
	public MenuButton(float x, float y, float z, float w, float h, String text, ClickInterface clickInterface) {
		ninepatch = createButton(x, y, z, w, h);
		bitmapfont = createText(x, y, z, w, h, text, clickInterface);
	}
	
	public Entity getBorderEntity() {
		return ninepatch;
	}
	
	public Entity getTextEntity() {
		return bitmapfont;
	}
	
	private Entity createText(float x, float y, float z, float w, float h, String text, ClickInterface clickInterface) {
		Entity entity = new Entity();

		BitmapFontComponent fontComp = new BitmapFontComponent();
		fontComp.font = "retro";
		fontComp.string = text;
		fontComp.color = Color.WHITE;
		fontComp.centering = true;
		entity.add(fontComp);

		TransformComponent transComp = new TransformComponent();
		transComp.position.set(x, y, z + 1);
		entity.add(transComp);
		
		ClickComponent clickComponent = new ClickComponent();
		clickComponent.clicker = clickInterface;
		clickComponent.active = true;
		clickComponent.shape = new Rectangle().setSize(w, h).setCenter(0.0f, 0.0f);
		entity.add(clickComponent);

		TextButtonComponent textButtonComponent = new TextButtonComponent();
		textButtonComponent.base = fontComp.color;
		textButtonComponent.pressed = Color.DARK_GRAY;
		entity.add(textButtonComponent);

		return entity;
	}
	
	static public Entity createButton(float x, float y, float z, float w, float h) {
		Entity entity = new Entity();
		
		NinepatchComponent nineComp = new NinepatchComponent();
		nineComp.patch = MiscArt.buttonBorder;
		nineComp.size.set(w, h);
		entity.add(nineComp);
		
		TransformComponent transComp = new TransformComponent();
		transComp.position.set(x, y, z);
		entity.add(transComp);

		return entity;
	}

	public void addToEngine(Engine engine) {
		engine.addEntity(ninepatch);
		engine.addEntity(bitmapfont);
	}

	public void removeFromEngine(Engine engine) {
		engine.removeEntity(ninepatch);
		engine.removeEntity(bitmapfont);
	}
}
