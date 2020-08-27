package sandbox;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.MouseMovedEvent;
import haraldr.event.MouseScrolledEvent;
import haraldr.graphics.Renderer3D;
import haraldr.input.Input;
import haraldr.input.Key;
import haraldr.main.EditorCameraController;
import haraldr.main.FPSCameraController;
import haraldr.main.GenericApplication;
import haraldr.main.PerspectiveCamera;
import haraldr.main.ProgramArguments;
import haraldr.main.Window;
import haraldr.scenegraph.Scene3D;

class ExampleApplication extends GenericApplication
{
    private Scene3D testScene;
    private boolean cursorVisible;

    private PerspectiveCamera camera;

    public ExampleApplication()
    {
        super(new Window.WindowProperties(1280, 720, ProgramArguments.getIntOrDefault("MSAA", 0), false, false, false));
    }

    @Override
    protected void clientInit(Window window)
    {
        testScene = new TestScene();
        testScene.onActivate();
        camera = new PerspectiveCamera(window.getWidth(), window.getHeight(), new EditorCameraController());
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {
        camera.onEvent(event, window);
        testScene.onEvent(event, window);
        if (Input.wasKeyPressed(event, Key.KEY_E))
        {
            cursorVisible = !cursorVisible;
            window.setCursorVisibility(cursorVisible);
        }
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