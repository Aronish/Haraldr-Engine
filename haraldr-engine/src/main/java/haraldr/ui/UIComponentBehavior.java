package haraldr.ui;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;

public interface UIComponentBehavior
{
    default void setWidth(float width) {}
    default UIEventResult onEvent(Event event, Window window)
    {
        return new UIEventResult(false, false);
    }
    default void draw(Batch2D batch) {}
    default void onDispose() {} // Not really used
    default float getVerticalSize()
    {
        return 0f;
    }

    record UIEventResult(boolean requiresRedraw, boolean consumed)
    {
        public static final UIEventResult NONE = new UIEventResult(false, false); //Test
    }
}
