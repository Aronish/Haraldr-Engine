package sandbox;

import engine.event.Event;
import engine.event.EventType;
import engine.event.MouseMovedEvent;
import engine.graphics.Line;
import engine.graphics.Renderer2D;
import engine.graphics.Texture;
import engine.layer.Layer;
import engine.main.OrthographicCamera;
import engine.main.Window;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

import static engine.main.Application.MAIN_LOGGER;

public class ExampleLayer extends Layer
{
    private OrthographicCamera orthographicCamera = new OrthographicCamera();
    private Texture texture = new Texture("textures/pixel_test.png");
    private Line line = new Line(new Vector3f(), 45f, 4f, 0.05f);

    public ExampleLayer(String name)
    {
        super(name);
    }

    @Override
    public void onEvent(Window window, @NotNull Event event)
    {
        if (event.eventType == EventType.MOUSE_MOVED)
        {
            MouseMovedEvent mouseMovedEvent = (MouseMovedEvent) event;

            MAIN_LOGGER.info(event.toString());
        }
    }

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        EventHandler.processInput(orthographicCamera, window.getWindowHandle(), deltaTime);
        line.update(deltaTime);
    }

    @Override
    public void onRender()
    {
        Renderer2D.beginScene(orthographicCamera);
        line.draw();
    }
}
