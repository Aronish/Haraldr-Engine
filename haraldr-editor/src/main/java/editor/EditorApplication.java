package editor;

import haraldr.graphics.Renderer;
import haraldr.main.Application;
import haraldr.main.ProgramArguments;
import haraldr.main.Window;

public class EditorApplication extends Application
{
    @Override
    public void start()
    {
        int samples = ProgramArguments.getIntOrDefault("MSAA", 0);
        Window.WindowProperties windowProperties = new Window.WindowProperties(1280, 720, samples, false, false, false);
        init(windowProperties);
        loop();
    }

    @Override
    protected void init(Window.WindowProperties windowProperties)
    {
        super.init(windowProperties);
        Renderer.setClearColor(0.8f, 0.8f, 0.8f, 1f);
        setActiveOverlay(new EditorOverlay());
        setActiveScene(new EditorTestScene());
    }
}
