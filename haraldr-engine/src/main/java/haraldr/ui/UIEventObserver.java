package haraldr.ui;

import haraldr.event.Event;
import haraldr.main.Window;

public interface UIEventObserver
{
    default UIComponentBehavior.UIEventResult onEvent(Event event, Window window)
    {
        return new UIComponentBehavior.UIEventResult(false, false);
    }

    boolean isEnabled();
}