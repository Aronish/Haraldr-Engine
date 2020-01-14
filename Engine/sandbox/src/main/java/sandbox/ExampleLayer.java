package sandbox;

import engine.event.Event;
import engine.graphics.Line;
import engine.graphics.LineSegment;
import engine.graphics.Renderer2D;
import engine.graphics.Texture;
import engine.layer.Layer;
import engine.main.OrthographicCamera;
import engine.main.Window;
import engine.math.Vector3f;
import org.jetbrains.annotations.NotNull;

public class ExampleLayer extends Layer
{
    private OrthographicCamera orthographicCamera = new OrthographicCamera();
    private Texture texture = new Texture("textures/pixel_test.png");
    private Line line = new Line(new Vector3f(), 45f, 4f, 0.005f);
    private LineSegment lineSegment = new LineSegment(new Vector3f(5.0f, 0.0f), 5f, 0f, 0.5f);

    public ExampleLayer(String name)
    {
        super(name);
    }

    @Override
    public void onEvent(Window window, @NotNull Event event)
    {
    }

    @Override
    public void onUpdate(@NotNull Window window, float deltaTime)
    {
        EventHandler.processInput(orthographicCamera, window.getWindowHandle(), deltaTime);
        line.update(deltaTime);
        lineSegment.update(deltaTime);
    }

    @Override
    public void onRender()
    {
        Renderer2D.beginScene(orthographicCamera);
        line.draw();
        lineSegment.draw();
    }
}
