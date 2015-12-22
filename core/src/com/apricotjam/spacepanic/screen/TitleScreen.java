package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.BitmapFontComponent;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.input.InputData;
import com.apricotjam.spacepanic.input.ScreenInput;
import com.apricotjam.spacepanic.systems.RenderingSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;

public class TitleScreen extends BasicScreen {

    private float flashTimer = 0.0f;
    private static final float FLASHPERIOD = 0.8f;
    private BitmapFontComponent bmfontcomp; 
    
    Engine engine;

    public TitleScreen(SpacePanic spacePanic, ScreenInput input) {
        super(spacePanic, input);
        
        engine = new Engine();
        
        engine.addSystem(new RenderingSystem(spriteBatch));
        
        engine.addEntity(createTitleEntity());
        engine.addEntity(createClickEntity());
        
    }

    @Override
    public void update(float delta, InputData inputData) {
    	engine.update(delta);
    	
        flashTimer += delta;
        if (flashTimer > FLASHPERIOD) {
            flashTimer = 0.0f;
        }
        if (inputData.isPointerDownLast()) {
            spacePanic.setScreen(new MenuScreen(spacePanic, input));
        }
        
        if (flashTimer > FLASHPERIOD / 2.0f)
        	bmfontcomp.color.a = 1f;
        else
        	bmfontcomp.color.a = 0f;
    }

    @Override
    public void backPressed() {
    }

    @Override
    public void render() {
        //spriteBatch.begin();
        //draw(MiscArt.title, CAMERA_WIDTH / 2.0f, CAMERA_HEIGHT / 2.0f, 6.5f, 1.5f, true);
        //if (flashTimer > FLASHPERIOD / 2.0f) {
           //drawString("Click to begin!", CAMERA_WIDTH / 2.0f, CAMERA_HEIGHT / 4.0f, 0.3f, true, Color.WHITE);
        //}
       // spriteBatch.end();
    }
    
    public Entity createTitleEntity() {
        Entity titleEntity = new Entity();
        
        TextureComponent textComp = new TextureComponent();
        textComp.region = MiscArt.title;
        
        TransformComponent transComp = new TransformComponent();
        transComp.position.x = RenderingSystem.WORLD_WIDTH / 2f;
        transComp.position.y = RenderingSystem.WORLD_HEIGHT / 2f;
        transComp.scale.x = 6.5f/(MiscArt.title.getRegionWidth()*RenderingSystem.PIXELS_TO_WORLD);
        transComp.scale.y = 1.5f/(MiscArt.title.getRegionHeight()*RenderingSystem.PIXELS_TO_WORLD);
        
        titleEntity.add(textComp);
        titleEntity.add(transComp);
        
        return titleEntity;
    }
    
    public Entity createClickEntity() {
        Entity clickEntity = new Entity();
        
        BitmapFontComponent fontComp = new BitmapFontComponent();
        fontComp.font = "retro";
        fontComp.string = "Click to begin!";
        fontComp.color = new Color(Color.WHITE);
        fontComp.centering = true;
        bmfontcomp = fontComp;
        
        TransformComponent transComp = new TransformComponent();
        transComp.position.x = SpacePanic.WIDTH / 2f;
        transComp.position.y = SpacePanic.HEIGHT / 4f;
        
        clickEntity.add(fontComp);
        clickEntity.add(transComp);
        
        return clickEntity;
    }
}
