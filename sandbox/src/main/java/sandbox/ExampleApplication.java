package sandbox;

import haraldr.event.Event;
import haraldr.graphics.Renderer3D;
import haraldr.input.Input;
import haraldr.input.Key;
import haraldr.scene.Camera;
import haraldr.main.Application;
import haraldr.main.ProgramArguments;
import haraldr.main.Window;
import haraldr.scene.OrbitalCamera;
import haraldr.scene.Scene3D;

class ExampleApplication extends Application
{
    private Scene3D testScene;
    private Camera camera;

    public ExampleApplication()
    {
        super(new Window.WindowProperties(1280, 720, ProgramArguments.getIntOrDefault("MSAA", 0), false, false, false));
    }

    @Override
    protected void clientInit(Window window)
    {
        testScene = new TestScene();
        testScene.onActivate();
        camera = new OrbitalCamera(window.getWidth(), window.getHeight());
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {
        camera.onEvent(event, window);
        testScene.onEvent(event, window);
        if (Input.wasKeyPressed(event, Key.KEY_E)) window.toggleCursor();
        if (Input.wasKeyPressed(event, Key.KEY_F)) window.toggleFullscreen();
        if (Input.wasKeyPressed(event, Key.KEY_ESCAPE)) stop();
    }

    @Override
    protected void clientUpdate(float deltaTime, Window window)
    {
        camera.onUpdate(deltaTime, window);
        testScene.onUpdate(deltaTime, window);
    }

    @Override
    protected void clientRender(Window window)
    {
        Renderer3D.begin(window, camera);
        testScene.onRender();
        Renderer3D.end(window);
    }
}