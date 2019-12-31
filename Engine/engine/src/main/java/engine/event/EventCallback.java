package engine.event;

import engine.main.Window;

@FunctionalInterface
public interface EventCallback
{
    void onEvent(Event event, Window window);
}
