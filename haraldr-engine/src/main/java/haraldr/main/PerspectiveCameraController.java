package haraldr.main;

import haraldr.event.Event;

public interface PerspectiveCameraController
{
    void onUpdate(float deltaTime, Window window);
    void onEvent(Event event, Window window);
    void setReference(PerspectiveCamera camera);
}