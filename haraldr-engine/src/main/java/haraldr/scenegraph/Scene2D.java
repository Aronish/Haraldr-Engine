package haraldr.scenegraph;

import haraldr.event.Event;
import haraldr.main.Window;

public abstract class Scene2D
{
    protected abstract void onClientActivate();
    protected abstract void onClientEvent(Window window, Event event);
    protected abstract void onClientUpdate(Window window, float deltaTime);
    protected abstract void onClientRender();
    protected abstract void onClientDispose();

    public final void onActivate()
    {
        onClientActivate();
    }

    public final void onUpdate(Window window, float deltaTime)
    {
        onClientUpdate(window, deltaTime);
    }

    public final void onEvent(Event event, Window window)
    {
        onClientEvent(window, event);
    }

    public final void onRender()
    {
        onClientRender();
    }

    public final void onDispose()
    {
        onClientDispose();
    }
}
