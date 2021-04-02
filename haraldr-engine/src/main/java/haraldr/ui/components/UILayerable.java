package haraldr.ui.components;

import haraldr.event.Event;
import haraldr.graphics.Batch2D;
import haraldr.main.Window;

public interface UILayerable
{
    record UIEventResult(boolean requiresRedraw, boolean consumed) {}

    default UIEventResult onEvent(Event event, Window window)
    {
        return new UIEventResult(false, false);
    }

    default void draw(Batch2D batch) {}

    default void render() {}

    default boolean isEnabled()
    {
        return true;
    }
}