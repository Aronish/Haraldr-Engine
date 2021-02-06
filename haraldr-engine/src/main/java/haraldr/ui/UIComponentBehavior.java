package haraldr.ui;

import haraldr.graphics.Batch2D;

public interface UIComponentBehavior
{
    default void setWidth(float width) {}
    default void draw(Batch2D batch) {}
    default void onDispose() {} // Not really used
    default float getVerticalSize()
    {
        return 0f;
    }

    record UIEventResult(boolean requiresRedraw, boolean consumed) {}
}
