package engine.layer;

import engine.event.Event;
import engine.main.Window;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Layer
{
    protected boolean isActive = true;

    public abstract void onEvent(Window window, Event event);

    public abstract void onUpdate(Window window, float deltaTime);

    public abstract void onRender();

    public abstract void onDispose();

    public void setActive(boolean active)
    {
        isActive = active;
    }

    public boolean isActive()
    {
        return isActive;
    }
}