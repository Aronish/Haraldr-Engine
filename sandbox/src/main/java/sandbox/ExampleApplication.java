package sandbox;

import haraldr.graphics.Renderer;
import haraldr.main.Application;
import haraldr.main.ProgramArguments;
import haraldr.main.Scene;
import haraldr.main.Window;

class ExampleApplication extends Application
{
    private Scene mainScene, debugOverlay;

    @Override
    public void start()
    {
        int samples = ProgramArguments.getIntOrDefault("MSAA", 0);
        Window.WindowProperties windowProperties = new Window.WindowProperties(1280, 720, samples, true, false, false);
        init(windowProperties);
        loop();
    }

    @Override
    protected void init(Window.WindowProperties windowProperties)
    {
        super.init(windowProperties);
        Renderer.setClearColor(0.1f, 0.1f, 0.1f, 1f);
        mainScene = new TestScene();
        debugOverlay = new DebugOverlay();
        setActiveScene(mainScene);
        setActiveOverlay(debugOverlay);
    }
}