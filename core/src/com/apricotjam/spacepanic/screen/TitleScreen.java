package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.BitmapFontComponent;
import com.apricotjam.spacepanic.components.ComponentMappers;
import com.apricotjam.spacepanic.components.TextureComponent;
import com.apricotjam.spacepanic.components.TransformComponent;
import com.apricotjam.spacepanic.input.InputManager;
import com.apricotjam.spacepanic.systems.RenderingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;

public class TitleScreen extends BasicScreen {

    private float flashTimer = 0.0f;
    private static final float FLASHPERIOD = 0.8f;
    private Entity clickEntity;

    public TitleScreen(SpacePanic spacePanic) {
        super(spacePanic);
        add(createTitleEntity());
        clickEntity = createClickEntity();
        add(clickEntity);
    }

    @Override
    public void render(float delta) {
    	super.render(delta);
        flashTimer += delta;
        if (flashTimer > FLASHPERIOD) {
            flashTimer = 0.0f;
        }
        if (InputManager.screenInput.isPointerDownLast()) {
            spacePanic.setScreen(new MenuScreen(spacePanic));
        }
        
        if (flashTimer > FLASHPERIOD / 2.0f) {
            ComponentMappers.bitmapfont.get(clickEntity).color.a = 1.0f;
        } else {
            ComponentMappers.bitmapfont.get(clickEntity).color.a = 0.0f;
        }
    }

    @Override
    public void backPressed() {
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
        
        TransformComponent transComp = new TransformComponent();
        transComp.position.x = SpacePanic.WIDTH / 2f;
        transComp.position.y = SpacePanic.HEIGHT / 4f;
        
        clickEntity.add(fontComp);
        clickEntity.add(transComp);
        
        return clickEntity;
    }

}
