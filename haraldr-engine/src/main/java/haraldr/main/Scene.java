package haraldr.main;

import haraldr.event.Event;

public interface Scene
{
    void onActivate();
    void onEvent(Event event);
    void onUpdate(Window window, float deltaTime);
    void onRender();
    void onDispose();
}