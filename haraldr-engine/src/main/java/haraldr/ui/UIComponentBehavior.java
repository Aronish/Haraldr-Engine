package haraldr.ui;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;

public interface UIComponentBehavior
{
    default void setWidth(float width) {}
    default boolean onEvent(Event event, Window window)
    {
        return false;
    }
    default void draw(Batch2D batch) {}
    default void drawOverlay(Batch2D overlayBatch) {}
    default void onDispose() {}
    default float getVerticalSize()
    {
        return 0f;
    }
}
