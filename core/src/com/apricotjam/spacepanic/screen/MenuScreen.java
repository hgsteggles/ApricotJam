package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.SpacePanic;
import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.components.*;
import com.apricotjam.spacepanic.systems.ButtonSystem;
import com.apricotjam.spacepanic.systems.RenderingSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class MenuScreen extends BasicScreen {

    private static final float TITLESPEED = 3.5f;
    private static final float TITLEENDPOSITION = WORLD_HEIGHT * 3.0f / 4.0f;

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
        transComp.position.x = BasicScreen.WORLD_WIDTH / 2f;
        transComp.position.y = BasicScreen.WORLD_HEIGHT / 2f;
        transComp.size.x = 5.0f;
        transComp.size.y = transComp.size.x * textComp.region.getRegionHeight() / textComp.region.getRegionWidth();

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
        transComp.position.x = BasicScreen.WORLD_WIDTH / 2f;
        transComp.position.y = BasicScreen.WORLD_HEIGHT / 4f;

        ButtonComponent buttonComponent = new ButtonComponent();
        buttonComponent.active = true;
        buttonComponent.shape = new Rectangle().setSize(2.0f, 0.5f)
                                               .setCenter(transComp.position.x, transComp.position.y);

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
