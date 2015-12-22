package com.apricotjam.spacepanic.screen;

import com.apricotjam.spacepanic.art.MiscArt;
import com.apricotjam.spacepanic.input.InputData;
import com.apricotjam.spacepanic.input.ScreenInput;
import com.apricotjam.spacepanic.SpacePanic;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class BasicScreen implements Screen {

    protected final SpacePanic spacePanic;
    protected final ScreenInput input;

    protected static final float CAMERA_WIDTH = 16.0f;
    protected static final float CAMERA_HEIGHT = 9.0f;
    protected final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ      0123456789.,:;'\"!?$%()-=+/";

    protected int width;
    protected int height;

    protected OrthographicCamera cam;
    protected SpriteBatch spriteBatch;

    public BasicScreen(SpacePanic spacePanic, ScreenInput input) {
        this.spacePanic = spacePanic;
        this.input = input;
        cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        cam.position.set(CAMERA_WIDTH / 2.0f, CAMERA_HEIGHT / 2.0f, 0);
        cam.update();
        input.setCamera(cam);
        
        spriteBatch = new SpriteBatch();
    }

    public final void update(float delta) {
        update(delta, input.getInputData());
    }

    public abstract void update(float delta, InputData inputData);

    public final void render(float delta) {
        render();
    }

    public abstract void render();

    @Override
    public void show() {
        cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        cam.position.set(CAMERA_WIDTH / 2.0f, CAMERA_HEIGHT / 2.0f, 0);
        cam.update();

        Gdx.input.setInputProcessor(input);

        spriteBatch.setProjectionMatrix(cam.combined);

        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    public InputData getInputData() {
        return input.getInputData();
    }

    public boolean isInCharSet(char c) {
        return chars.contains(Character.toString(c).toUpperCase());
    }

    public void draw(TextureRegion region, float x, float y, float width, float height) {
        draw(region, x, y, width, height, 0.0f);
    }

    public void draw(TextureRegion region, float x, float y, float width, float height, boolean centre) {
        if(centre){
            draw(region, x - Math.abs(width/2.0f), y - Math.abs(height/2.0f), width, height, 0.0f);
        } else {
            draw(region, x, y, width, height, 0.0f);
        }
    }

    public void draw(TextureRegion region, float x, float y, float width, float height, float angle) {
        if (width < 0) {
            width = region.getRegionWidth() * (height / (float) region.getRegionHeight());
        } else if (height < 0) {
            height = region.getRegionHeight() * (width / (float) region.getRegionWidth());
        }
        spriteBatch.draw(region, x, y, width / 2.0f, height / 2.0f, width, height, 1.0f, 1.0f, angle); // Assume rotation about the centre
    }

    public void drawString(String string, float x, float y, float scale) {
        drawString(string, x, y, scale, false, Color.BLACK, false);
    }

    public void drawString(String string, float x, float y, float scale, boolean centre, Color color, int wrap) {
        String[] words = string.split(" ");
        int col = 0;
        int row = 0;
        for (String s : words) {
            if((col + s.length()) > wrap){
                row++;
                col = 0;
            }
            drawString(s, x + col*scale, y - row*scale, scale, centre, color, false);
            col += s.length() + 1;
        }
    }

    public void drawString(String string, float x, float y, float scale, boolean centre, Color color) {
        drawString(string, x, y, scale, centre, color, false);
    }

    public void drawString(String string, float x, float y, float scale, boolean centre, Color color, boolean invert) {
        string = string.toUpperCase();
        int len = string.length();
        for (int i = 0; i < string.length(); i++) {
            int charIndex;
            if(invert){
                charIndex = chars.indexOf(string.charAt(string.length() - i - 1));
            } else {
                charIndex = chars.indexOf(string.charAt(i));
            }
            if (charIndex >= 0) {
                Sprite s = new Sprite(MiscArt.chars[charIndex]);
                s.flip(invert, invert);
                s.setColor(color);
                s.setSize(scale, scale);
                if (!centre) {
                    s.setY(y);
                    s.setX(x + i * scale);
                } else {
                    s.setX(x + (i - len / 2.0f) * scale);
                    s.setY(y - scale/2.0f);
                }
                s.draw(spriteBatch);
            }
        }
    }

    public boolean isOverlay() {
        return false;
    }

    public abstract void backPressed();
}
