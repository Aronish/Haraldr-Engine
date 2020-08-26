package haraldr.scenegraph;

import haraldr.event.Event;
import haraldr.main.Window;

public abstract class Scene2D
{
    protected abstract void onClientActivate(Window window);
    protected abstract void onClientEvent(Event event, Window window);
    protected abstract void onClientUpdate(float deltaTime, Window window);
    protected abstract void onClientRender();
    protected abstract void onClientDispose();

    public final void onActivate(Window window)
    {
        onClientActivate(window);
    }

    public final void onUpdate(float deltaTime, Window window)
    {
        onClientUpdate(deltaTime, window);
    }

    public final void onEvent(Event event, Window window)
    {
        onClientEvent(event, window);
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
