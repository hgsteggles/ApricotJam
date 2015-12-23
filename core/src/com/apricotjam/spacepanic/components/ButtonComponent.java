package com.apricotjam.spacepanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Shape2D;

public class ButtonComponent implements Component {
    public boolean active = true;
    public Shape2D shape;

    public boolean pointerOver = false; // True when pointer is held down over the button
    public boolean clickLast = false; // True if button has been clicked in the last frame
}
