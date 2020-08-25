package editor;

import haraldr.graphics.Renderer;
import haraldr.main.GenericApplication;
import haraldr.main.ProgramArguments;
import haraldr.main.Window;

public class EditorApplication extends GenericApplication
{


    public EditorApplication()
    {
        super(new Window.WindowProperties(1280, 720, ProgramArguments.getIntOrDefault("MSAA", 0), false, false, false));
    }

    @Override
    protected void clientInit()
    {
        Renderer.setClearColor(0.8f, 0.2f, 0.3f, 1f);
    }

    @Override
    protected void clientUpdate(float deltaTime)
    {
    }

    @Override
    protected void clientRender()
    {
        Renderer.clear(Renderer.ClearMask.COLOR);
    }
}
