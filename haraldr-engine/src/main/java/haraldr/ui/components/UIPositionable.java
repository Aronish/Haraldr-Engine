package haraldr.ui.components;

import haraldr.math.Vector2f;

public interface UIPositionable
{
    default void setPosition(Vector2f position) {}
    default void setSize(Vector2f size) {}
    default Vector2f getPosition() { return new Vector2f(); }
    default Vector2f getSize() { return new Vector2f(); }
    default float getVerticalSize()
    {
        return 0f;
    }
}