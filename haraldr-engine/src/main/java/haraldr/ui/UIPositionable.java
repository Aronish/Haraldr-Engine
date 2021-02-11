package haraldr.ui;

import haraldr.math.Vector2f;

public interface UIPositionable
{
    default void setPosition(Vector2f position) {}
    default void addPosition(Vector2f position) {}
    default void setWidth(float width) {}
    default float getVerticalSize()
    {
        return 0f;
    }
}