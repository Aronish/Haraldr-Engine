package editor;

import haraldr.event.Event;
import haraldr.graphics.Renderer;
import haraldr.main.GenericApplication;
import haraldr.main.ProgramArguments;
import haraldr.main.Window;
import haraldr.scenegraph.Scene2D;

public class EditorApplication extends GenericApplication
{
    private Scene2D editorOverlay;

    public EditorApplication()
    {
        super(new Window.WindowProperties(1280, 720, ProgramArguments.getIntOrDefault("MSAA", 0), false, false, false));
    }

    @Override
    protected void clientInit(Window window)
    {
        editorOverlay = new EditorOverlay();
        editorOverlay.onActivate(window);
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {
        editorOverlay.onEvent(event, window);
    }

    @Override
    protected void clientUpdate(float deltaTime, Window window)
    {
        editorOverlay.onUpdate(deltaTime, window);
    }

    @Override
    protected void clientRender()
    {
        Renderer.clear(Renderer.ClearMask.COLOR);
        Renderer.disableDepthTest();
        editorOverlay.onRender();
    }
}
