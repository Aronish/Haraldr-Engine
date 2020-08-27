package editor;

import haraldr.event.Event;
import haraldr.event.EventType;
import haraldr.event.WindowResizedEvent;
import haraldr.graphics.Renderer;
import haraldr.graphics.Renderer2D;
import haraldr.graphics.Renderer3D;
import haraldr.graphics.ui.Pane;
import haraldr.main.GenericApplication;
import haraldr.main.ProgramArguments;
import haraldr.main.Window;
import haraldr.math.Vector2f;
import haraldr.scenegraph.Scene3D;

public class EditorApplication extends GenericApplication
{
    private Pane propertiesPane;
    private Scene3D scene;

    public EditorApplication()
    {
        super(new Window.WindowProperties(1280, 720, ProgramArguments.getIntOrDefault("MSAA", 0), false, false, false));
    }

    @Override
    protected void clientInit(Window window)
    {
        propertiesPane = new Pane(
                new Vector2f(),
                window.getWidth(), window.getHeight(),
                0.25f,
                0.3f,
                "Properties"
        );
        scene = new EditorTestScene();
        scene.onActivate();
    }

    @Override
    protected void clientEvent(Event event, Window window)
    {
        propertiesPane.onEvent(event, window);
        if (event.eventType == EventType.WINDOW_RESIZED)
        {
            var windowResizedEvent = (WindowResizedEvent) event;
            propertiesPane.onWindowResized(windowResizedEvent.width, windowResizedEvent.height);
        }
        scene.onEvent(event, window);
    }

    @Override
    protected void clientUpdate(float deltaTime, Window window)
    {
        propertiesPane.onUpdate(deltaTime);
        scene.onUpdate(window, deltaTime);
    }

    @Override
    protected void clientRender(Window window)
    {
        Renderer.enableDepthTest();
        Renderer3D.begin(window);
        scene.onRender();
        Renderer3D.end(window);

        Renderer.disableDepthTest();
        Renderer2D.begin();
        propertiesPane.render();
        Renderer2D.end();
        propertiesPane.renderText();
    }
}
