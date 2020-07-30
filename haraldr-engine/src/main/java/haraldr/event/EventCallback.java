package haraldr.event;

import haraldr.main.Window;

@FunctionalInterface
public interface EventCallback
{
    void onEvent(Event event, Window window);
}
