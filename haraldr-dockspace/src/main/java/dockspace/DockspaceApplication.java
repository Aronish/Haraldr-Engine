package dockspace;

import haraldr.event.Event;
import haraldr.graphics.Renderer;
import haraldr.graphics.Renderer2D;
import haraldr.main.Application;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.math.Vector4f;

public class DockspaceApplication extends Application
{
    private Dockspace dockspace;

    public DockspaceApplication()
    {
        super(new Window.WindowProperties(1280, 720, 0, false, false, false, true));
    }

    @Override
    protected void clientInit(Window window)
    {
        dockspace = new Dockspace(
                new Vector2f(),
                new Vector2f(window.getWidth(), window.getHeight())
        );

        dockspace.addPanel(new DockablePanel(new Vector2f(), new Vector2f(300f, 400f), new Vector4f(0.8f, 0.2f, 0.3f,0.5f)));
        //dockspace.addChild(new DockablePanel(new Vector2f(500f, 300f), new Vector2f(300f, 200f), new Vector4f(0.3f, 0.8f, 0.2f, 1f)));

        Renderer.disableDepthTest();
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {
        dockspace.onEvent(event, window);
    }

    @Override
    protected void clientUpdate(float deltaTime, Window window)
    {
    }

    @Override
    protected void clientRender(Window window)
    {
        Renderer.clear(Renderer.ClearMask.COLOR);
        Renderer2D.begin();
        dockspace.render();
        Renderer2D.end();
    }

    @Override
    public void clientDispose()
    {
    }
}
