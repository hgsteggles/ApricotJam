package com.apricotjam.spacepanic.input;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

public interface InputInterface {
    Vector2 getPointerLocation(); //Returns current pointer location
    Vector2 getPointerDownLocation(); //Returns pointer location when last touched down
    Vector2 getPointerDrag(); //Returns distance pointer has been dragged
    boolean isPointerDown(); //Returns true if pointer is down
    boolean isPointerDownLast(); //Returns true if point was moved down last frame
    boolean isPointerUpLast(); //Returns true if pointer was moved up last frame

    boolean isBackPressedLast(); //Returns true if back was pressed last frame

    void reset();
}
