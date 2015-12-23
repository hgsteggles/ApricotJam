package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.systems.ButtonSystem;
import com.apricotjam.spacepanic.systems.RenderingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;

public class MenuScreen extends BasicScreen {

    private static final float TITLESPEED = 3.5f;
    private static final float TITLEENDPOSITION = CAMERA_HEIGHT * 3.0f / 4.0f;

    private Entity startButton;
    private Entity title;

    public MenuScreen(SpacePanic spacePanic) {
        super(spacePanic);
        add(new ButtonSystem());

        title = createTitleEntity();
        add(title);
        startButton = createClickEntity();
        add(startButton);

    }

    private void startGame() {
        System.out.println("So the game would start now...");
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
        fontComp.string = "START";
        fontComp.color = new Color(Color.WHITE);
        fontComp.centering = true;

        TransformComponent transComp = new TransformComponent();
        transComp.position.x = SpacePanic.WIDTH / 2f;
        transComp.position.y = SpacePanic.HEIGHT / 4f;
        transComp.scale.x = 1.0f;
        transComp.scale.y = 1.0f;

        ButtonComponent buttonComponent = new ButtonComponent();
        buttonComponent.active = true;

        clickEntity.add(fontComp);
        clickEntity.add(transComp);
        clickEntity.add(buttonComponent);

        return clickEntity;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        TransformComponent titleTransform = ComponentMappers.transform.get(title);
        if (titleTransform.position.y < TITLEENDPOSITION) {
            titleTransform.position.y += delta * TITLESPEED;
        }

        ButtonComponent button = ComponentMappers.button.get(startButton);
        if (button.clickLast) {
            startGame();
        }
        if (button.pointerOver) {
            TransformComponent buttonTransform = ComponentMappers.transform.get(startButton);
            BitmapFontComponent buttonFont = ComponentMappers.bitmapfont.get(startButton);
            buttonFont.color = Color.RED;
        } else {
            TransformComponent buttonTransform = ComponentMappers.transform.get(startButton);
            BitmapFontComponent buttonFont = ComponentMappers.bitmapfont.get(startButton);
            buttonFont.color = Color.WHITE;
        }
    }

    @Override
    public void backPressed() {
        spacePanic.setScreen(new TitleScreen(spacePanic));
    }
}
